# 拥抱Reactor&反应式编程

## 认识Reactor

Reactor 是一个基础库，可用它构建时效性**流式数据**应用，或者有**低延迟**和**容错性**要求的微/纳/皮级服务。

简单说，Reactor 是一个轻量级 **JVM 基础库**，帮助你的服务或应用高效，异步地传递消息。

Reactor 仅仅致力于解决异步和函数调用问题。和 Spring 天然无缝整合（毕竟 Reactor 框架是 Pivotal 公司（开发 Spring 等技术的公司）开发的，实现了 Reactive Programming 思想）。

> "高效"是指什么?
>
> - 消息从 A 传递到 B 时，产生很少的**内存**垃圾，甚至不产生。
> - 解决消费者处理消息的效率低于生产者时带来的**溢出**问题。
> - 尽可能提供非阻塞**异步流**。
>
> PS：Spring 5 其最大的意义就是能将反应式编程技术（它就是常见的观察者模式的一种延伸）的普及向前推进一大步。而作为在背后支持 Spring 5 反应式编程的框架 Reactor，也相应的发布了 3.1.0 版本。

从经验可知(主要是 rage 和 drunk 的推特)，异步编程很难，而像 JVM 这类提供众多可选参数的平台则尤其困难。 

Reactor 旨在帮助大多数用例**真正非阻塞地运行**。我们提供的 API 比 JDK 的 java.util.concurrent 库低级原语更高效。Reactor 提供了下列功能的替代函数 (并**建议不使用 JDK 原生语句**)：

- 阻塞等待：
  如 `Future.get()`
- 不安全的数据访问：
  如 `ReentrantLock.lock()`
- 异常冒泡：
  如 `try…catch…finally`
- 同步阻塞：
  如 `synchronized{ }`
- Wrapper 分配(GC 压力)：
  如 `new Wrapper(event)`

当消息传递效率成为系统性能瓶颈的时候(10k msg/s，100k msg/s，1M...)，非阻塞机制就显得尤为重要。 

例如看下面的一段代码：

``` java
private ExecutorService  threadPool = Executors.newFixedThreadPool(8);
final List<T> batches = new ArrayList<T>();

Callable<T> t = new Callable<T>() { // *1
  public T run() {
    synchronized(batches) { // *2
      T result = callDatabase(msg); // *3
      batches.add(result);
      return result;
    }
  }
};

Future<T> f = threadPool.submit(t); // *4
T result = f.get() // *5
```

注释中标注的几点：

1. Callable 分配 -- 可能导致 GC 压力。
2. 同步过程强制每个线程执行停 - 检查操作。
3. 消息的消费可能比生产慢。
4. 使用线程池(ThreadPool)将任务传递给目标线程 -- 通过 FutureTask 方式肯定会产生 GC 压力。
5. 阻塞直至 `callDatabase()` 回调。

---

在这个简单的例子中，很容易指出为什么扩容是很有限的：

- 分配对象可能产生 GC 压力，特别是当任务运行时间过长。
  每次 GC 暂停都会影响全局性能。
- 默认，队列是无界的，任务会因为数据库调用而堆积。
  积压虽然不会直接导致内存泄漏，但会带来严重副作用：GC 暂停时要扫描更多的对象；有丢失重要数据位的风险；等等 …
  典型链式队列节点分配时会产生大量内存压力。
- 阻塞回调容易产生恶性循环。
  阻塞回调会降低消息生产者的效率。在实践中，任务提交后需要等待结果返回，此时流式过程几乎演变为同步的了。
  会话过程抛出的任何带数据存储的异常都会以不受控的方式被传递给生产者，否定了任何通常在线程边界附近可用的容错性。

要实现完全非阻塞是很难办到的，尤其是在有着类似**微服务架构**这样时髦绰号的分布式系统的世界里。因此 Reactor 做了部分妥协，尝试利用最优的可用模式，使开发者觉得他们是在写异步纳米服务，而不是什么数学论文。

到了某个阶段，延迟是每一个系统到都要面对的实实在在的问题。为此：

> Reactor 提供的框架可以帮助减轻应用中由延迟产生的副作用，只需要增加一点点开销：
>
> - 使用了一些聪明的结构，通过启动预分配策略解决运行时**分配问题**；
> - 通过确定信息传递主结构的**边界**，避免任务的无限堆叠；
> - 采用主流的**响应与事件驱动构架**模式，提供包含反馈在内的**非阻塞端对端流**；
> - 引入新的 [Reactive Streams](http://projectreactor.io/docs/reference/#reactivestreams) 标准,拒绝超过当前容量请求，从而保证限制结构的有效性；
> - 在 IPC 上也使用了类似理念，提供对流控制友好的**非阻塞 IO 驱动**；
> - 开放了帮助开发者们以**零副作用**方式组织他们代码的函数接口，借助这些函数来处理容错性和线程安全。

为实现异步目标，响应式技术和 Reactor 模块该如何搭配：

- Spring XD + Reactor-Net (Core/Stream)： 
  使用 Reactor 作为 Sink/Source IO 驱动。
- Grails | Spring + Reactor-Stream (Core)： 
  用 Stream 和 Promise 做后台处理。
- Spring Data + Reactor-Bus (Core)： 
  发射数据库事件 (保存/删除/…)。
- Spring Integration Java DSL + Reactor Stream (Core)：
   Spring 集成的微批量信息通道。
- RxJavaReactiveStreams + RxJava + Reactor-Core：
   融合富结构与高效异步 IO 处理
- RxJavaReactiveStreams + RxJava + Reactor-Net (Core/Stream)： 
  用 RxJava 做数据输入，异步 IO 驱动做传输。

---

**响应式数据流**作为一种新的数据流规范应用于 Java 9 及其后续版本，并被多个供应商和技术企业采纳，包括包括 Netflix，Oracle，Pivotal 或 Typesafe。

这一规范的定位非常清晰，旨在提供同/异步数据序列流式控制机制，并在 JVM 上首先推广。该规范由 4 个 Java 接口，1 个 TCK 和一些样例组成。

---

**响应式扩展**，就是通常所说的 Rx，是一组定义良好的函数式 API，大规模扩展了**观察者模式**。

Rx 模式支持响应式数据序列处理，主要的设计要点有：

- 使用回调链分离时间/延迟：仅当数据可用时才会回调
- 分离线程模型：用 Observable / Stream 来处理同步或异步
- 控制错误链/终止：数据载荷信号以及错误与完成信号都传递给回调链
- 解决各种预定义 API 中多重分散-聚合和构造问题

JVM 中响应式扩展的标准实现是 RxJava。它提供了强大的函数式 API，并将原始微软库中几乎全部的概念移植了过来。

> 响应式数据流和响应式扩展算是最近比较新的技术了，因为牵扯到异步非阻塞技术比较难理解，但是从 Spring5 的方向来看，这是未来，至于如何学习，我还在摸索那条路比较好。

---

### 核心

**Reactor 核心**含有如下特性：

> - **通用 IO & 函数式类型**，一些 Java 8 接口的反向移植•函数，提供者，消费者，谓词，双向消费者，双向函数
> - 元组
> - 资源池、暂停器、定时器
> - 缓冲器，编解码和少量预定义的编解码器
> - **环境**上下文
> - **调度者**约定和几个预定义调度者
> - 预定义**响应式数据流处理者**

Reactor-核心自身可替代其它消息传递机制，完成时序任务调度，或者帮你将代码组织为函数块，实现 Java 8 的反向移植接口。这种拆分便于同其他的响应式库配合使用，而没耐心的开发者也不用再去费劲弄懂环形缓冲区了。

## 反应式编程介绍

反应式编程（Reactive Programming）这种新的编程范式越来越受到开发人员的欢迎。在 Java 社区中比较流行的是 RxJava 和 RxJava 2。Spring5 中使用的是另外一个新的反应式编程库 Reactor。

**反应式编程来源于数据流和变化的传播**，举个例子：比如求值一个简单的表达式 c=a+b，当 a 或者 b 的值发生变化时，传统的编程范式需要对 a+b 进行重新计算来得到 c 的值。如果使用反应式编程，当 a 或者 b 的值发生变化时，c 的值会自动更新。

反应式编程最早由 .NET 平台上的 Reactive Extensions (Rx) 库来实现。后来迁移到 Java 平台之后就产生了著名的 RxJava 库，并产生了很多其他编程语言上的对应实现。在这些实现的基础上产生了后来的反应式流（Reactive Streams）规范。该规范定义了反应式流的相关接口，并将集成到 Java 9 中。

---

在传统的编程范式中，我们一般通过迭代器（Iterator）模式来遍历一个序列。这种遍历方式是由调用者来控制节奏的，**采用的是拉的方式**：每次由调用者通过 next()方法来获取序列中的下一个值。

使用反应式流时采用的则是**推的方式**，即常见的发布者-订阅者模式：当发布者有新的数据产生时，这些数据会被推送到订阅者来进行处理。

在反应式流上可以添加各种不同的操作来对数据进行处理，形成数据处理链。这个以声明式的方式添加的处理链只在订阅者进行订阅操作时才会真正执行。

---

反应式流中第一个重要概念是**负压（backpressure）**。在基本的消息推送模式中，当消息发布者产生数据的速度过快时，会使得消息订阅者的处理速度无法跟上产生的速度，从而给订阅者造成很大的压力。当压力过大时，有可能造成订阅者本身的奔溃，所产生的级联效应甚至可能造成整个系统的瘫痪。

负压的作用在于提供一种从订阅者到生产者的反馈渠道。**订阅者**可以通过 `request()` 方法来声明其一次所能处理的消息数量，而生产者就只会产生相应数量的消息，直到下一次 `request()` 方法调用。这实际上变成了推拉结合的模式。

## Flux 和 Mono

Flux 和 Mono 是 Reactor 中的两个基本概念。Flux 表示的是包含 **0 到 N 个元素的异步序列**。在该序列中可以包含三种不同类型的消息通知：

- 正常的包含元素的消息
- 序列结束的消息
- 序列出错的消息

当消息通知产生时，订阅者中对应的方法 `onNext()`, `onComplete()` 和  `onError()` 会被调用。

Mono 表示的是包含 **0 或者 1 个元素的异步序列**。该序列中同样可以包含与 Flux 相同的三种类型的消息通知。

Flux 和 Mono 之间可以进行转换。对一个 Flux 序列进行计数操作，得到的结果是一个 `Mono<Long>` 对象。把两个 Mono 序列合并在一起，得到的是一个 Flux 对象。

## 使用Reactor

创建 Flux，Reactor 提供了一系列的静态方法来创建 Flux

``` java
// 可以指定序列中包含的全部元素。创建出来的 Flux 序列在发布这些元素之后会自动结束。
Flux.just("Hello", "World").subscribe(System.out::println);

// （还有 fromIterable 和 fromStream）可以从一个数组、Iterable 对象或 Stream 对象中创建 Flux 对象。
Flux.fromArray(new Integer[] {1, 2, 3}).subscribe(System.out::println);

// 创建一个不包含任何元素，只发布结束消息的序列。
// 此外还有 error 和 never
Flux.empty().subscribe(System.out::println);

// 创建包含从 start 起始的 count 个数量的 Integer 对象的序列。
Flux.range(1, 10).subscribe(System.out::println);

// 创建一个包含了从 0 开始递增的 Long 对象的序列。
// 其中包含的元素按照指定的间隔来发布。
// 除了间隔时间之外，还可以指定起始元素发布之前的延迟时间。
Flux.interval(Duration.of(10, ChronoUnit.SECONDS)).subscribe(System.out::println);

// 与 interval()方法的作用相同，只不过该方法通过毫秒数来指定时间间隔和延迟时间。
Flux.intervalMillis(1000).subscribe(System.out::println);
```

上面的这些静态方法适合于简单的序列生成，当序列的生成需要复杂的逻辑时，则应该使用 `generate()` 或 `create()` 方法。

``` java
// generate 方式
Flux.generate(sink -> {
    sink.next("Hello");
    sink.complete();
}).subscribe(System.out::println);

final Random random = new Random();
Flux.generate(ArrayList::new, (list, sink) -> {
    int value = random.nextInt(100);
    list.add(value);
    sink.next(value);
    if (list.size() == 10) {
        sink.complete();
    }
    return list;
}).subscribe(System.out::println);

// create 方式
Flux.create(sink -> {
    for (int i = 0; i < 10; i++) {
        sink.next(i);
    }
    sink.complete();
}).subscribe(System.out::println);
```

Mono 的创建方式与之前介绍的 Flux 比较相似。Mono 类中也包含了一些与 Flux 类中相同的静态方法。这些方法包括 just()，empty()，error()和 never()等。除了这些方法之外，Mono 还有一些独有的静态方法。

``` java
Mono.fromSupplier(() -> "Hello").subscribe(System.out::println);
// 从一个 Optional 对象或可能为 null 的对象中创建 Mono。
// 只有 Optional 对象中包含值或对象不为 null 时，Mono 序列才产生对应的元素。
Mono.justOrEmpty(Optional.of("Hello")).subscribe(System.out::println);
Mono.create(sink -> sink.success("Hello")).subscribe(System.out::println);
```

---

和 RxJava 一样，Reactor 的强大之处在于可以在反应式流上通过声明式的方式添加多种不同的操作符。

例如 buffer 和 bufferTimeout 这两个操作符的作用是把当前流中的元素收集到集合中，并把集合对象作为流中的新元素。

还有 filter 、take、reduce 和 reduceWith、merge 和 mergeSequential、flatMap 和 flatMapSequential、消息处理、调度器相关的方法，这方面其实有很多内容，但是没细看，估计短时间内接触不到，有个印象等用的时候知道有这么个东西然后再查 API 好了。

## 参考

http://wiki.jikexueyuan.com/project/reactor-2.0/06.html

https://www.ibm.com/developerworks/cn/java/j-cn-with-reactor-response-encode/index.html