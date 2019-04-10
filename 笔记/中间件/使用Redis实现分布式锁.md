# 使用Redis实现分布式锁

使用 Redis 实现分布式锁首先要先知道几个 Redis 的命令，分布式锁就是通过这几个命令来实现的

-  setnx
  只有不存在的时候，setnx 才会设置值成功；
  可以理解为是否存在和设置值这两条命令的集合版，不过是原子性的。
- getset
  先 get 再 set，也是两条命令的整合，具有原子性。
- expire
  设置有效期
- del
  删除

## 简单流程

首先使用 setnx 存入一个值，key 为锁名，val 为当前的时间戳加一个超时时间，这是为了防止死锁。

![image](https://user-images.githubusercontent.com/16206117/45962253-02682b80-c053-11e8-8873-06ebe570140f.png)

仔细看这个架构好像有点问题，因为我们设置的 val 根本没用，也没有任何的防死锁措施，只是实现比较简单而已，更完善的第二版在这：

![image](https://user-images.githubusercontent.com/16206117/45962340-39d6d800-c053-11e8-8bf1-f073a31debc8.png)
这样基本就不会出现死锁的情况了，下面来看看具体的代码。

## 第一版

这就是上面第一张图的简单实现：

``` java
public void closeOrderTaskV1(){
  log.info("关闭订单定时任务启动");
  long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));

  Long setnxResult = RedisShardedPoolUtil
    .setnx(
    Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
    String.valueOf(System.currentTimeMillis()+lockTimeout)
  );
  if(setnxResult != null && setnxResult.intValue() == 1){
    // 如果返回值是 1，代表设置成功，获取锁
    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
  }else{
    log.info("没有获得分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
  }
  log.info("关闭订单定时任务结束");
}

private void closeOrder(String lockName){
  // 有效期5秒，防止死锁
  RedisShardedPoolUtil.expire(lockName,5);
  log.info("获取{},ThreadName:{}",
           Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
           Thread.currentThread().getName());
  int hour = Integer.parseInt(PropertiesUtil
                              .getProperty("close.order.task.time.hour","2"));
  orderService.closeOrder(hour);
  // 删除 key，释放锁
  RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
  log.info("释放{},ThreadName:{}",
           Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
           Thread.currentThread().getName());
}
```

很显然，这个防不了死锁，我们设置的超时时间也没用到，当执行到 closeOrder 方法之前宕掉的话，那么因为这个 key 没有设置有效期，就会到期其他模块一直进不去。

closeOrder 中的设置有效期和执行后的删除键（释放锁）也是双重防死锁，这个有效期需要根据线上运行的实际情况来得出一个合理的时间。

## 第二版

既然第一版有问题，那么来解决下好了：

``` java
@Scheduled(cron="0 */1 * * * ?")
public void closeOrderTaskV2(){
  log.info("关闭订单定时任务启动");
  long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));
  Long setnxResult = RedisShardedPoolUtil.setnx(
    Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
    String.valueOf(System.currentTimeMillis()+lockTimeout));
  
  if(setnxResult != null && setnxResult.intValue() == 1){
    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
  }else{
    //未获取到锁，继续判断，判断时间戳，看是否可以重置并获取到锁
    String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    if(lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)){
      String getSetResult = RedisShardedPoolUtil.getSet(
        Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
        String.valueOf(System.currentTimeMillis()+lockTimeout));

      // 根据返回的旧值，判断是否可以获取锁
      if(getSetResult == null
         || (getSetResult != null 
             && StringUtils.equals(lockValueStr,getSetResult))){
        //已真正获取到锁
        closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
      }else{
        log.info("没有获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
      }
    }else{
      log.info("锁未失效，没有获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }
  }
  log.info("关闭订单定时任务结束");
}
```

这样看上去基本就是万无一失了，前半段并不需要修改，我们在 else 后做了一个超时判断，来觉得是否可以重置锁，这个判断可是不简单呢。

首先通过 get 方法来获取 val，用这个 val 和当前时间的时间戳来判断是否超时，然后我们使用 getset 方法重新获取老值，并且重新设置超时时间（原子操作）；

根据返回的旧值，判断是否可以获取锁，这里会有三种情况：

- 当 key 没有旧值时，即 key 不存在时，返回 nil 对应 Java 中的 Null
  这说明其他分布式程序已经执行完使用 del 删除了键（释放了锁）或者过了 Redis 的生存时间；
  这时可以安全获取锁。
- 当 key 有旧值，并且旧值和之前获取的一致的情况下
  这说明这段时间没有程序操作这把锁，并且因为 getset 之后重新设置了有效期，可以保证现在也是安全的，可以获取锁。
- 当 key 有旧值，并且旧值和之前获取的不一致的情况下
  这说明在程序执行期间有其他的分布式模块也操作了这把锁，并且对方比较快，先执行了 getset 这就导致两个旧值对不起来，这种情况下只能放弃，等待下次获取。

## 使用Redisson

先来看看基本的介绍：

> Redisson 是架设在 Redis 基础上的一个 Java 驻内存数据网格（In-Memory Data Grid）。
>
> 充分的利用了 Redis 键值数据库提供的一系列优势，基于 Java 实用工具包中常用接口，为使用者提供了一系列具有分布式特性的常用工具类。使得原本作为协调单机多线程并发程序的工具包获得了协调分布式多机多线程并发系统的能力，大大降低了设计和研发大规模分布式系统的难度。同时结合各富特色的分布式服务，**更进一步简化了分布式环境中程序相互之间的协作。**
>
> Redisson 采用了基于 NIO 的 Netty 框架，不仅能作为 Redis 底层驱动客户端，具备提供对 Redis 各种组态形式的连接功能，对 Redis 命令能以同步发送、异步形式发送、异步流形式发送或管道形式发送的功能，LUA 脚本执行处理，以及处理返回结果的功能，还在此基础上融入了更高级的应用方案。
>
> Redisson 生而具有的高性能，分布式特性和丰富的结构等特点恰巧与 Tomcat 这类服务程序对会话管理器（Session Manager）的要求相吻合。利用这样的特点，Redisson 专门为 Tomcat 提供了会话管理器（Tomcat Session Manager）。
>
> 在此不难看出，Redisson 同其他 Redis Java 客户端有着很大的区别，**相比之下其他客户端提供的功能还仅仅停留在作为数据库驱动层面上**，比如仅针对 Redis 提供连接方式，发送命令和处理返回结果等。像上面这些高层次的应用则只能依靠使用者自行实现。

可以看出 Redisson 对分布式一些工具做了很好的封装，如今分布式盛行的年代下，越来越多的项目使用 Redisson  作为 Redis 的客户端，使用它可以更方便的使用 Redis 分布式锁，来看第三版：

``` java
public void closeOrderTaskV3(){
  RLock lock = redissonManager
    .getRedisson()
    .getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
  boolean getLock = false;
  try {
    if(getLock = lock.tryLock(0,50, TimeUnit.SECONDS)){
      log.info("Redisson获取到分布式锁:{},ThreadName:{}",
               Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
               Thread.currentThread().getName());
      int hour = Integer
        .parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
      orderService.closeOrder(hour);
    }else{
      log.info("Redisson没有获取到分布式锁:{},ThreadName:{}",
               Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
               Thread.currentThread().getName());
    }
  } catch (InterruptedException e) {
    log.error("Redisson分布式锁获取异常",e);
  } finally {
    if(!getLock){
      return;
    }
    lock.unlock();
    log.info("Redisson分布式锁释放锁");
  }
}
```

这段代码中使用了 Redisson 提供的 RLock 对象来获取、释放锁，这其实是一种可重入锁，Redisson 还提供了其他的多种锁，就不多说了；用这个来实现分布式锁原理其实是一样的，只不过被 Redisson 封装后更加的简单了。

使用 RLock 的 tryLock 方法来尝试获取锁，可以使用三个参数的构造，第一个是最多等待时间（超时就直接过了），第二个是自动解锁时间，第三个是时间单位。

这里的等待时间如果预估不准可以写 0，否则就会出现同时获得锁的情况，也就是程序执行的太快，还没超过等待时间所以又被第二个拿到了。

## 其他

另外，关掉 Tomcat 的时候如果你直接 kill 掉，而是温柔的杀死他，使用 *shutdown*，那么可以使用这个注解来保证在它死之前执行 del 删除锁来避免死锁，虽然这很不现实，如果方法执行时间过长很多人也不能忍受。

``` java
@PreDestroy
public void delLock(){
  RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
}
```

还有类似的好用注解，例如 @PostConstruct 标注 init 方法，会在构造完成后执行这个初始化。