## 组合式异步编程

近年来的两种趋势：

第一种：随着多核处理器的出现，提升应用程序处理速度最有效的方式是编写能充分发挥多核能力的软件。你已经看到通过切分大型的任务，让每个子任务**并行运行**，这一目标是能够实现的；你也已经了解相对直接使用线程的方式，使用分支/合并框架（在Java 7中引入）和并行流（在Java 8中新引入）能以更简单、更有效的方式实现这一目标。

第二种趋势反映在公共 API 日益增长的互联网服务应用。著名的互联网大鳄们纷纷提供了自己的公共 API 服务；现在，很少有网站或者网络应用会以完全隔离的方式工作。更多的时候，我们看到的下一代网络应用都采用“混聚”（mash-up）的方式：它会使用来自多个来源的内容，将这些内容聚合在一起，方便用户的生活。
要实现类似的服务，你需要与互联网上的多个 Web 服务通信。可是，你并不希望因为等待某些服务的响应，阻塞应用程序的运行，浪费数十亿宝贵的 CPU 时钟周期。
这些场景体现了多任务程序设计的另一面。

如果你的意图是实现并发，而非并行，或者你的主要目标是在同一个 CPU 上执行几个松耦合的任务，**充分利用 CPU 的核**，让其足够忙碌，从而**最大化程序的吞吐量**，那么你其实真正想做的是避免因为等待远程服务的返回，或者对数据库的查询，而阻塞线程的执行，浪费宝贵的计算资源，因为这种等待的时间很可能相当长。

### Future 接口

Future 接口在 Java 5 中被引入，设计初衷是对将来某个时刻会发生的结果进行建模。**它建模了一种异步计算**，返回一个执行运算结果的引用，**当运算结束后，这个引用被返回给调用方**。

在 Future 中触发那些潜在耗时的操作把调用线程解放出来，让它能继续执行其他有价值的工作，不再需要呆呆等待耗时的操作完成。

 Future 的另一个优点是它比更底层的 Thread 更易用。要使用 Future ，通常你只需要将耗时的操作封装在一个 Callable 对象中，再将它提交给 ExecutorService ，就万事大吉了。

``` java
ExecutorService executor = Executors.newCachedThreadPool();
// 提交异步任务
Future<Double> future = executor.submit(new Callable<Double>() {
  public Double call() {
    return doSomeLongComputation();
  }});

// 可以做点其他的事情
doSomethingElse();

try {
  // 获取异步操作的结果，如果被阻塞，就等待一秒钟后退出
  // 提供 isDone 方法来判断是否结束
  Double result = future.get(1, TimeUnit.SECONDS);
} catch (ExecutionException ee) {
  // 计算抛出一个异常
} catch (InterruptedException ie) {
  // 当前线程在等待过程中被中断
} catch (TimeoutException te) {
  // 在Future对象完成之前超过已过期
}
```

这种编程方式让你的线程可以在 ExecutorService 以并发方式调用另一个线程执行耗时操作的同时，去执行一些其他的任务。接着，如果你运行到必须需要异步任务的返回值时，可以调用它的 get 方法去获取操作的结果。
如果操作已经完成，该方法会立刻返回操作的结果，否则它会阻塞你的线程，直到操作完成，返回相应的结果。

如果该长时间运行的操作永远不返回了会怎样？为了处理这种可能性，虽然 Future 提供了一个无需任何参数的 get 方法，我们还是推荐大家使用重载版本的 get 方法，它接受一个超时的参数，通过它，你可以定义你的线程等待 Future 结果的最长时间。

当然，它是有局限性的，要实现下面的需求就比较困难了：

- 将两个异步计算合并为一个；这两个异步计算之间相互独立，同时第二个又依赖于第一个的结果。
- 等待 Future 集合中的所有任务都完成。
- 仅等待 Future 集合中最快结束的任务完成，并返回它的结果。
- 通过编程方式完成一个 Future 任务的执行。
- 应对 Future 的完成事件。

### CompletableFuture 类

假设一种商店提供价格获取的接口，异步第一版：

``` java
public Future<Double> getPriceAsync(String product) {
  // 用于包含计算的结果
  CompletableFuture<Double> futurePrice = new CompletableFuture<>();
  // 在另一个线程中以异步的方式计算
  new Thread( () -> {
    // calculatePrice 方法是耗时的，使用 sleep 模拟
    double price = calculatePrice(product);
    futurePrice.complete(price);
  }).start();
  // 无需等待，直接返回
  return futurePrice;
}
```

 Future 是一个暂时还不可知值的处理器，这个值在计算完成后，可以通过调用它的 get 方法取得。因为这样的设计， getPriceAsync 方法才能立刻返回，给调用线程一个机会，能在同一时间去执行其他有价值的计算任务。

#### 异常处理

如果价格计算过程中产生了错误会怎样呢？非常不幸，这种情况下你会得到一个相当糟糕的结果：用于提示错误的异常会被限制在试图计算商品价格的当前线程的范围内，最终会杀死该线程，而这会导致等待 get 方法返回结果的**客户端永久地被阻塞。**

使用重载的 get 方法设置超时时间是个不错的选择，但是这样你不会有机会发现其线程内到底发生了什么问题才引发了这样的异常。为了让客户端能了解原因，你需要使用 CompletableFuture 的 completeExceptionally 方法将导致 CompletableFuture 内发生问题的异常抛出。

第二版：

``` java
public Future<Double> getPriceAsync(String product) {
  CompletableFuture<Double> futurePrice = new CompletableFuture<>();
  new Thread( () -> {
    try {
      double price = calculatePrice(product);
      futurePrice.complete(price);
    } catch (Exception ex) {
      // 发生异常则抛出
      futurePrice.completeExceptionally(ex);
    }
  }).start();
  return futurePrice;
}
```

此外， CompletableFuture 类自身提供了大量精巧的工厂方法能够大大方便我们的异步程序编写，第三版：

``` java
public Future<Double> getPriceAsync(String product) {
  return CompletableFuture.supplyAsync(() -> calculatePrice(product));
}
```

supplyAsync 方法接受一个生产者（ Supplier ）作为参数，**返回一个 CompletableFuture 对象**，该对象完成异步执行后会读取调用生产者方法的返回值。
生产者方法会交由 ForkJoinPool 池中的某个执行线程（ Executor ）运行，但是你也可以使用 supplyAsync 方法的重载版本，传递第二个参数指定不同的执行线程执行生产者方法。
一般而言，向 CompletableFuture 的工厂方法传递可选参数，指定生产者方法的执行线程是可行的，这个稍后会说到。
同时，它提供了跟第二版同样的错误管理机制，基本是等价的，只不过这样一行代码更加简洁了。

### 免受阻塞

假设规定的方法接口返回值不是 Future，那就只能是同步的了，例如一个从商家列表查询指定商品价格用来比较的方法，第一版：

``` java
// 假设此方法签名强制规定
public List<String> findPrices(String product) {
  return shops.stream()
    .map(shop -> String.format("%s price is %.2f",
                               shop.getName(), 
                               shop.getPrice(product)))
    .collect(toList());
}
```

这样其实就算是顺序执行，一个查询操作会阻塞另一个，最简单的优化就是使用新提供的并行，第二版：

``` java
public List<String> findPrices(String product) {
  return shops.parallelStream()
    .map(shop -> String.format("%s price is %.2f",
                               shop.getName(), 
                               shop.getPrice(product)))
    .collect(toList());
}
```

虽然只是将 stream 换成了 parallelStream，但是性能可是极大的提高了不少，下面就来试试 CompletableFuture 的异步请求会怎么样，第三版：

``` java
List<CompletableFuture<String>> priceFutures =
  shops.stream()
  .map(shop -> CompletableFuture.supplyAsync(
    () -> String.format("%s price is %.2f",
                        shop.getName(), 
                        shop.getPrice(product))))
  .collect(toList());
```

使用这种方式，你会得到一个 `List<CompletableFuture<String>>`，但是，由于你用 CompletableFutures 实现的 findPrices 方法要求返回一个 `List<String>` ，你需要等待所有的 future 执行完毕，将其包含的值抽取出来，填充到列表中才能返回。

为了实现这个效果，你可以向最初的 `List<CompletableFuture<String>>` 施加第二个 map 操作，对 List 中的所有 future 对象执行 join 操作，一个接一个地等待它们运行结束。

> CompletableFuture 类中的 join 方法和 Future 接口中的 get 有相同的含义，并且也声明在 Future 接口中，它们唯一的不同是 **join 不会抛出任何检测到的异常**。

``` java
// 符合原方法签名的
public List<String> findPrices(String product) {
  List<CompletableFuture<String>> priceFutures =
    shops.stream()
    .map(shop -> CompletableFuture.supplyAsync(
      () -> shop.getName() + " price is " +
      shop.getPrice(product)))
    .collect(Collectors.toList());
  
  return priceFutures.stream()
    .map(CompletableFuture::join)
    .collect(toList());
}
```

这里使用了两个不同的 Stream 流水线，而不是在同一个处理流的流水线上一个接一个地放置两个 map 操作，考虑流操作之间的延迟特性，如果你在单一流水线中处理流，发向不同商家的请求只能以同步、顺序执行的方式才会成功，这样就和使用同步没啥区别了。

然后你会发现，使用异步和并行其实差距并不是很大，因为它们**内部采用的是同样的通用线程池**，默认都使用固定数目的线程，具体线程数取决于 `Runtime.getRuntime().availableProcessors()` 的返回值。
然而， CompletableFuture 具有一定的优势，因为它允许你对执行器（ Executor ）进行配置，尤其是线程池的大小，让它以更适合应用需求的方式进行配置，满足程序的要求，而这是并行流 API 无法提供的.

> 线程池的大小:
>
> 《Java并发编程实战》一书中，Brian Goetz 和合著者们为线程池大小的优化提供了不少中肯的建议。这非常重要，如果线程池中线程的数量过多，最终它们会竞争稀缺的处理器和内存资源，浪费大量的时间在上下文切换上。反之，如果线程的数目过少，正如你的应用所面临的情况，处理器的一些核可能就无法充分利用。
>
> 推荐的数量计算公式：
> N (threads) = N (CPU) * U (CPU) * (1 + W/C)
>
> - N (CPU) 是处理器的核的数目，可以通过 `Runtime.getRuntime().availableProce-ssors()` 得到
>
> - U (CPU) 是期望的 CPU 利用率（该值应该介于 0 和 1 之间）
>
> - W/C 是等待时间与计算时间的比率
>
> 如果你的应用 99% 的时间都在等待商店的响应，所以估算出的 W/C 比率为 100。这意味着如果你期望的 CPU 利用率是 100%，你需要创建一个拥有 400 个线程的线程池。

相关代码（自定义线程池）：

``` java
// 创建线程池
private final Executor executor = Executors.newFixedThreadPool(
  // 最小值 100
  Math.min(shops.size(), 100),
  new ThreadFactory() {
    public Thread newThread(Runnable r) {
      Thread t = new Thread(r);
      // 设置为守护线程，不会阻止程序的关停
      t.setDaemon(true);
      return t;
    }
  });

// 使用
CompletableFuture.supplyAsync(
  () -> shop.getName() + " price is " + shop.getPrice(product),
  executor);
```

Java 程序无法终止或者退出一个正在运行中的线程，所以最后剩下的那个线程会由于一直等待无法发生的事件而引发问题。与此相反，如果将线程标记为守护进程，意味着程序退出时它也会被回收。
这二者之间没有性能上的差异，所以这里使用守护线程是比较合适的。

这样，在数量较多的调用情况下，效率会好很多。

---

那么，既然使用 Stream 并发与 CompletableFuture 这样看上去都差不多，那么应该如何选择呢？

- 如果你进行的是**计算密集型**的操作，并且**没有 I/O**，那么推荐使用 Stream 接口，因为实现简单，同时效率也可能是最高的（如果所有的线程都是计算密集型的，那就没有必要创建比处理器核数更多的线程）
- 如果你并行的工作单元还涉及等待 I/O 的操作（包括网络连接等待），那么使用 CompletableFuture 灵活性更好，依据等待/计算，或者 W/C 的比率设定需要使用的线程数。
  这种情况不使用并行流的另一个原因是，处理流的流水线中如果发生 I/O 等待，流的延迟特性会让我们很难判断到底什么时候触发了等待。

### 对多个异步任务进行流水线操作

还是以上面查询价格的例子来说，这次加入了折扣码的逻辑，需要动态根据这个码查询折扣算出最终价格，至于处理折扣的相关代码就不贴了，核心方法第一版：

``` java
public List<String> findPrices(String product) {
  return shops.stream()
    // 将每个 shop 对象转换成了一个字符串，该字符串包含了该 shop 中指定商品的价格和折扣代码。
    .map(shop -> shop.getPrice(product))
    // 对这些字符串进行了解析，在 Quote 对象中对它们进行转换
    .map(Quote::parse)
    // 模拟调用远程的 Discount 服务，计算出最终的折扣价格，并返回该价格及提供该价格商品的 shop
    .map(Discount::applyDiscount)
    .collect(toList());
}
```

用它做基准测试还是不错的，然后我们也知道把流转换为并行流的方式，非常容易提升该程序的性能，但是在数量增加后扩展性不好，因为 Stream 底层依赖的是线程数量固定的通用线程池。

所以，我们使用异步的方式来看看，第二版：

``` java
public List<String> findPrices(String product) {
  List<CompletableFuture<String>> priceFutures =
    shops.stream()
    // 跟之前一样，以异步的方式取得原始价格
    .map(shop -> CompletableFuture.supplyAsync(  // one
      () -> shop.getPrice(product),
      executor))
    // Quote 存在时，进行转换
    .map(future -> future.thenApply(Quote::parse))
    // 使用另一个异步任务构造申请折扣的 Future
    .map(future -> future.thenCompose(quote ->
                                      CompletableFuture.supplyAsync(  // two
                                        () -> Discount.applyDiscount(quote),
                                        executor)))
    .collect(toList());
  
  // 等待全部完毕，提取值
  return priceFutures.stream()
    .map(CompletableFuture::join)
    .collect(toList());
}
```

第一个转换的结果是一个 `Stream<CompletableFuture<String>>` ，一旦运行结束每个 CompletableFuture 对象中都会包含对应 shop 返回的字符串，当然还使用了之前的自定义线程池。

由于一般情况下解析操作不涉及任何远程服务，也不会进行任何 I/O 操作，它几乎可以在第一时间进行，所以能够采用同步操作，不会带来太多的延迟。所以对第一步中生成的 CompletableFuture 对象调用它的 thenApply ，将一个由字符串转换 Quote 的方法作为参数传递给它。

> 直到你调用的 CompletableFuture 执行结束，使用的 thenApply 方法都不会阻塞你代码的执行。这意味着 CompletableFuture 最终结束运行时，你希望传递 Lambda 表达式给 thenApply 方法，将 Stream 中的每个 `CompletableFuture<String>` 对象转换为对应的 `CompletableFuture<Quote>` 对象。

第三个转换因为也涉及远程调用，所以我们也希望使用异步，可以像上面一样将这一操作以 Lambda 表达式的方式传递给了 supplyAsync 工厂方法，该方法最终会返回另一个 CompletableFuture 对象；
然后如何将这两个异步串联起来工作能？

> Java 8 的 CompletableFuture API 提供了名为 **thenCompose** 的方法，它就是专门为这一目的而设计的， thenCompose 方法允许你对两个异步操作进行流水线，第一个操作完成时，将其结果作为参数传递给第二个操作。
>
> 换句话说，你可以创建两个 CompletableFutures 对象，对第一个 CompletableFuture 对象调用 thenCompose ，并向其传递一个函数。
> 当第一个CompletableFuture 执行完毕后，它的结果将作为该函数的参数，这个函数的返回值是以第一个 CompletableFuture 的返回做输入计算出的第二个 CompletableFuture 对象。
>
> 同时它也提供了一个以 **Async** 后缀结尾的版本 thenComposeAsync 。通常而言，名称中不带 Async 的方法和它的前一个任务一样，在同一个线程中运行；而名称以 Async 结尾的方法会将后续的任务提交到一个线程池，所以每个任务是由不同的线程处理的。

就这个例子而言，第二个 CompletableFuture 对象的结果取决于第一个 CompletableFuture ，所以无论你使用哪个版本的方法来处理 CompletableFuture 对象，对于最终的结果，或者大致的时间而言都没有多少差别。我们选择 thenCompose 方法的原因是因为它更高效一些，因为少了很多线程切换的开销。

---

那么，如何将两个 CompletableFuture 对象整合起来，无论它们是否存在依赖，因为另一种比较常见的情况是，你需要将两个完全不相干的 CompletableFuture 对象的结果整合起来，而且你也不希望等到第一个任务完全结束才开始第二项任务。

这种情况，你应该使用 **thenCombine** 方法，它接收名为 BiFunction 的第二参数，这个参数定义了当两个 CompletableFuture 对象完成计算后，结果如何合并。同 thenCompose 方法一样，thenCombine 方法也提供有一个 Async 的版本。

``` java
Future<Double> futurePriceInUSD = 
  // 第一个任务：查询商品价格
  CompletableFuture.supplyAsync(() -> shop.getPrice(product))
    .thenCombine(
    // 第二个任务：查询汇率
    CompletableFuture.supplyAsync(() -> exchangeService.getRate(Money.EUR, Money.USD)),
    // 结果合并
    (price, rate) -> price * rate
);
```

然后，可以稍微了解一下没有 8 的时候使用 Java7 是怎么实现的：

``` java
ExecutorService executor = Executors.newCachedThreadPool();
final Future<Double> futureRate = executor.submit(new Callable<Double>() {
  public Double call() {
    return exchangeService.getRate(Money.EUR, Money.USD);
  }});
Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
  public Double call() {
    double priceInEUR = shop.getPrice(product);
    return priceInEUR * futureRate.get();
  }});
```

相比之下，下面的代码采用第三个 Future 单独进行商品价格和汇率的乘法运算，效果是几乎相同的。这两种实现看起来没太大区别，原因是你只对两个 Future 进行了合并。

### 响应completion 事件

大多数情况下，他们远程的服务大多所消耗的时间是不同的，你希望在某些服务查询速度要比另一些更快，具体到上面的例子，也就是只要有商店返回商品价格就在第一时间显示返回值，而不是等到全面都完成后返回。

所以，应该直接处理 CompletableFuture 流，这样每个 CompletableFuture 都在为某个商店执行必要的操作。

``` java
public Stream<CompletableFuture<String>> findPricesStream(String product) {
  return shops.stream()
    .map(shop -> CompletableFuture.supplyAsync(
      () -> shop.getPrice(product), 
      executor))
    .map(future -> future.thenApply(Quote::parse))
    .map(future -> future.thenCompose(quote ->
                                      CompletableFuture.supplyAsync(
                                        () -> Discount.applyDiscount(quote), 
                                        executor)));
}

// 使用
findPricesStream("myPhone").map(f -> f.thenAccept(System.out::println));
```

相比之下只是最后在每个 CompletableFuture 上注册一个操作，该操作会在 CompletableFuture 完成执行后使用它的返回值。Java 8 的 CompletableFuture 通过 **thenAccept** 方法提供了这一功能，它接收 CompletableFuture 执行完毕后的返回值做参数。

和之前看到的 thenCompose 和 thenCombine 方法一样， thenAccept 方法也提供了一个异步版本，名为 thenAcceptAsync 。异步版本的方法会对处理结果的消费者进行调度，从线程池中选择一个新的线程继续执行，不再由同一个线程完成 CompletableFuture 的所有任务。
因为想要避免**不必要的(在这里)**上下文切换，更重要的是希望避免在等待线程上浪费时间，尽快响应 CompletableFuture 的 completion 事件，所以这里没有采用异步版本。

由于 thenAccept 方法已经定义了如何处理 CompletableFuture 返回的结果，一旦 CompletableFuture 计算得到结果，它就返回一个 `CompletableFuture<Void>` 。
所以， map 操作返回的是一个 `Stream<CompletableFuture<Void>>` 。对这个 `CompletableFuture<Void>` 对象，你能做的事非常有限，只能等待其运行结束，不过这也是你所期望的。
你还希望能给最慢的商店一些机会，让它有机会打印输出返回的价格。为了实现这一目的，你可以把构成 Stream 的所有 `CompletableFuture<Void>` 对象放到一个数组中，等待所有的任务执行完成：

``` java
CompletableFuture[] futures = findPricesStream("myPhone")
  .map(f -> f.thenAccept(System.out::println))
  .toArray(size -> new CompletableFuture[size]);

CompletableFuture.allOf(futures).join();
```

allOf 工厂方法接收一个由 CompletableFuture 构成的数组，数组中的所有 CompletableFuture 对象执行完成之后，它返回一个 `CompletableFuture<Void>` 对象。

然而在另一些场景中，你可能希望只要 CompletableFuture 对象数组中有任何一个执行完毕就不再等待，比如，你正在查询两个汇率服务器，任何一个返回了结果都能满足你的需求。
在这种情况下，你可以使用一个类似的工厂方法 **anyOf** 。该方法接收一个 CompletableFuture 对象构成的数组，返回由第一个执行完毕的 CompletableFuture 对象的返回值构成的 `CompletableFuture<Object>`。