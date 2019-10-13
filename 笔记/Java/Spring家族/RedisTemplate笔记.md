# RedisTemplate笔记

Redis 目前常用的存储结构有五种，String 字符串，List 列表，Set 集合，Hash 散列，ZSet 有序集合；

不过大部分主要集中在 String 这个数据结构的读写操作之上，这一类使用的最为频繁，介绍 Redis 的命令与使用在博客之前的文章就说过了。

## 依赖

环境肯定是以 SB 为主，既然是用 RedisTemplate 那么官方肯定有相应的 starter：

``` xml
<dependencies>
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
  </dependency>
</dependencies>
```

如果你的 Redis 配置没动的话，例如端口 6379，无密码，那么就可以直接用了。

## get/set

直接使用默认的 RedisTemplate 进行 redis 的读写操作，如果没有指定序列化方式，就不能使用更简单的 opsForValue 进行操作，当然在 SB 的 starter 下可以愉快的使用 opsForXXX。

``` java
/**
 * 非序列化下使用 execute
 */
@Component
public class KVBean {
  private final StringRedisTemplate redisTemplate;

  public KVBean(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  /**
   * 设置并获取结果，要求 key，value 都不能为空；如果之前没有值，返回 null
   */
  public byte[] setAndGetOldValue(String key, String value) {
    return redisTemplate.execute((RedisCallback<byte[]>) con -> con.getSet(key.getBytes(), value.getBytes()));
  }

  public Boolean setValue(String key, String value) {
    return redisTemplate
      .execute((RedisCallback<Boolean>) connection -> connection.set(key.getBytes(), value.getBytes()));
  }

  public byte[] getValue(String key) {
    return redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.get(key.getBytes()));
  }

  public Boolean mSetValue(Map<String, String> values) {
    Map<byte[], byte[]> map = new HashMap<>(values.size());
    for (Map.Entry<String, String> entry : values.entrySet()) {
      map.put(entry.getKey().getBytes(), entry.getValue().getBytes());
    }

    return redisTemplate.execute((RedisCallback<Boolean>) con -> con.mSet(map));
  }

  public List<byte[]> mGetValue(List<String> keys) {
    return redisTemplate.execute((RedisCallback<List<byte[]>>) con -> {
      byte[][] bkeys = new byte[keys.size()][];
      for (int i = 0; i < keys.size(); i++) {
        bkeys[i] = keys.get(i).getBytes();
      }
      return con.mGet(bkeys);
    });
  }
}
```

> opsForXXX 的底层，就是通过调用 execute 方式来做的，其主要就是封装了一些使用姿势，定义了序列化，使用起来更加的简单和便捷；

## 简单使用

RedisTemplate 中定义了对 5 种数据结构操作

```
redisTemplate.opsForValue();//操作字符串
redisTemplate.opsForHash();//操作hash
redisTemplate.opsForList();//操作list
redisTemplate.opsForSet();//操作set
redisTemplate.opsForZSet();//操作有序set
```

当然，字符串的话，能用 StringRedisTemplate 就直接用 StringRedisTemplate 吧。

```java
redisTemplate.opsForValue().set("stringValue","bbb");
redisTemplate.opsForValue().set("aaa", resultList, 30L, TimeUnit.MINUTES);

redisTemplate.opsForList().leftPush("list","a");  
String listValue = redisTemplate.opsForList().index("list",1) + "";  
List<Object> list =  redisTemplate.opsForList().range("list",0,-1); 
// 把最后一个参数值放到指定集合的第一个出现中间参数的前面
redisTemplate.opsForList().leftPush("list","a","n"); 
redisTemplate.opsForList().leftPushAll("list","w","x","y"); 
```

就先这些吧

## 关于序列化

RedisTemplate 默认使用 JDK 的序列化，但是非常不推荐，因为它序列化后在 Redis 服务器中是乱码不能解析，可读比较差。

在 RedisTemplate 的源码中，一上来就给出了这么几个定义：

``` java
// 配置默认序列化器
private boolean enableDefaultSerializer = true;
private RedisSerializer<?> defaultSerializer;
private ClassLoader classLoader;

private RedisSerializer keySerializer = null;
private RedisSerializer valueSerializer = null;
private RedisSerializer hashKeySerializer = null;
private RedisSerializer hashValueSerializer = null;
private RedisSerializer<String> stringSerializer = new StringRedisSerializer();
```

看到了我们关心的 `keySerializer` 和 `valueSerializer`，在 `RedisTemplate.afterPropertiesSet()` 方法中，可以看到，默认的序列化方案：

```java
public void afterPropertiesSet() {
  super.afterPropertiesSet();
  boolean defaultUsed = false;
  if (defaultSerializer == null) {
    defaultSerializer = new JdkSerializationRedisSerializer(
      classLoader != null ? classLoader : this.getClass().getClassLoader());
  }
  if (enableDefaultSerializer) {
    if (keySerializer == null) {
      keySerializer = defaultSerializer;
      defaultUsed = true;
    }
    if (valueSerializer == null) {
      valueSerializer = defaultSerializer;
      defaultUsed = true;
    }
    if (hashKeySerializer == null) {
      hashKeySerializer = defaultSerializer;
      defaultUsed = true;
    }
    if (hashValueSerializer == null) {
      hashValueSerializer = defaultSerializer;
      defaultUsed = true;
    }
  }
  ...
    initialized = true;
}
```

默认的方案是使用了 `JdkSerializationRedisSerializer`，所以导致了前面的阅读性差结果，注意：字符串和使用 jdk 序列化之后的字符串是两个概念。

例如，看一下 set 的源码：

```java
public void set(K key, V value) {
  final byte[] rawValue = rawValue(value);
  execute(new ValueDeserializingRedisCallback(key) {

    protected byte[] inRedis(byte[] rawKey, RedisConnection connection) {
      connection.set(rawKey, rawValue);
      return null;
    }
  }, true);
}
```

最终与 Redis 交互使用的是原生的 connection，键值则全部是字节数组，意味着所有的序列化都依赖于应用层完成，**Redis 只认字节**！

### StringRedisSerializer

上面说过的，其实大多数操作是字符串操作，键值均为字符串，`String.getBytes()` 即可满足需求。spring-data-redis 也考虑到了这一点，其一，提供了 **StringRedisSerializer** 的实现，其二，提供了 **StringRedisTemplate**，继承自 RedisTemplate。

```java
public class StringRedisTemplate extends RedisTemplate<String, String>{
  public StringRedisTemplate() {
    RedisSerializer<String> stringSerializer = new StringRedisSerializer();
    setKeySerializer(stringSerializer);
    setValueSerializer(stringSerializer);
    setHashKeySerializer(stringSerializer);
    setHashValueSerializer(stringSerializer);
  }
  // ...
}

// 测试
@Autowired
StringRedisTemplate stringRedisTemplate;
stringRedisTemplate.opsForValue().set("student:2", "SkYe");
```

PS：即使使用这种方式，你也不能直接扔业务对象，它不会自动调用 toString 方法，还好给你抛个异常。

### 自定义序列化

其实，字符串已经是万能格式了，你可以使用 Jackson 之类的将对象转成 json 然后存到 Redis，使用字符串的方式，如果你嫌麻烦，想直接扔对象，让它内部自动给你做转换也是可以的，这其实是一个等价操作。

但有两点得时刻记住两点:

1. Redis 只认字节。
2. 使用什么样的序列化器序列化，就必须使用同样的序列化器反序列化。

> 曾经在 review 代码时发现，项目组的两位同事操作 redis，一个使用了 RedisTemplate，一个使用了 StringRedisTemplate，当他们操作同一个键时，key 虽然相同，但由于序列化器不同，导致无法获取成功。差异虽小，但影响是非常可怕的。

所以，最优的方案自然是在项目初期就统一好序列化方案，所有模块引用同一份依赖，避免不必要的麻烦（或者干脆全部使用默认配置）。

---

无论是 RedisTemplate 中默认使用的 `JdkSerializationRedisSerializer`，还是 StringRedisTemplate 中使用的 `StringRedisSerializer` 都是实现自统一的接口 `RedisSerializer`：

``` java
public interface RedisSerializer<T> {
   byte[] serialize(T t) throws SerializationException;
   T deserialize(byte[] bytes) throws SerializationException;
}
```

在 spring-data-redis 中提供了其他的默认实现，用于替换默认的序列化方案。

- GenericToStringSerializer 依赖于内部的 ConversionService，将所有的类型转存为字符串
- GenericJackson2JsonRedisSerializer 和 Jackson2JsonRedisSerializer 以 JSON 的形式序列化对象
- OxmSerializer 以 XML 的形式序列化对象

那么接下来就是替换了：

替换的方式有很多，例如可以将全局的 RedisTemplate 覆盖，也可以在使用时在局部实例化一个 RedisTemplate 替换（不依赖于 IOC 容器）需要根据实际的情况选择替换的方式，以 Jackson2JsonRedisSerializer 为例介绍全局替换的方式：

```java
@Bean
public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
  RedisTemplate redisTemplate = new RedisTemplate();
  redisTemplate.setConnectionFactory(redisConnectionFactory);
  Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

  // 修改 Jackson 序列化时的默认行为
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
  objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

  jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

  // 手动指定 RedisTemplate 的 Key 和 Value 的序列化器
  redisTemplate.setKeySerializer(new StringRedisSerializer());
  redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);

  redisTemplate.afterPropertiesSet();
  return redisTemplate;
}
```

### 可能存在的坑

我想，如果你要自定义的话肯定会选 GenericJackson2JsonRedisSerializer 和 Jackson2JsonRedisSerializer 其中一个，但是他们俩有啥区别呢？

- Jackson2JsonRedisSerializer
  从名字可以看出来，这是把一个对象以 Json 的形式存储，效率高且对调用者友好，**优点是速度快，序列化后的字符串短小精悍**，不需要实现 Serializable 接口。
  但缺点也非常致命：那就是此类的构造函数中有一个类型参数，**必须提供要序列化对象的类型信息(.class对象)**，它序列化后没有 @class 信息，是非常干净的。
  类型信息在反序列化中是非常重要的。
- GenericJackson2JsonRedisSerializer
  基本和上面的功能差不多，使用方式也差不多，但是不需要指定类型（因为存储了对象的 class 信息的），所以更推荐。

然后说说他们的坑，对于 Jackson2JsonRedisSerializer，网上很多说如果是带泛型的集合会抛异常，不过在高版本 2.x 之后应该遇不到了。

至于 GenericJackson2JsonRedisSerializer 的坑，为了集合泛型的通用，它会统一当作 Object 处理，这样在处理某些对象时，例如 Long 的 1，它会默认当作 integer 处理，你需要再手动 object 接一下再转。

或者，考虑下自定义序列化器的方案（默认实现的泛型限制了只接受 String 类型。重写后，@Cacheable 等注解的 key 支持不仅仅是 String 类型了）：

``` java
/**
 * 必须重写序列化器，否则 @Cacheable 注解的 key 会报类型转换错误
 */
public class StringRedisSerializer implements RedisSerializer<Object> {

  private final Charset charset;
  private final String target = "\"";
  private final String replacement = "";

  public StringRedisSerializer() {
    this(Charset.forName("UTF8"));
  }

  public StringRedisSerializer(Charset charset) {
    Assert.notNull(charset, "Charset must not be null!");
    this.charset = charset;
  }

  @Override
  public String deserialize(byte[] bytes) {
    return (bytes == null ? null : new String(bytes, charset));
  }

  @Override
  public byte[] serialize(Object object) {
    //底层还是调用的 fastjson 的工具来操作的
    String string = JSON.toJSONString(object);
    if (string == null) {
      return null;
    }
    string = string.replace(target, replacement);
    return string.getBytes(charset);
  }
}
```

当然你可以使用其他的第三方序列化工具 FastJsonRedisSerializer、KryoRedisSerializer 等。

## 参考

https://www.cnkirito.moe/spring-data-redis-2/

https://blog.csdn.net/f641385712/article/details/84679456