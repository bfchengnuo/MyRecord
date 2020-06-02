# Java11 Features

就在不久，Java 14 出来了，现在 Java8 已经是非常普遍了，也有很多的人已经用上 Java11 了，意识到再不学就晚了，赶紧看看 Java 9 - 11 的特性，我的认知还停留在 Java9 的模块化 2333；

虽然 Java11 没有 Java8 那么大的『进化』，但是技术必然要向前，并且要快步向前，要不然被其他语言吃掉也有可能，隔壁 JS 生态就非常恐怖，后来的 Go 也非常出彩，学吧，反正核心都差不多。

至于为啥选 Java11，肯定是因为它是 TLS，并且已经是 18 年的事了（感叹一下 Go 的更新速度），下一个 TLS 是 Java17，会在 2021 年 9 月发布。

> 其中最令人兴奋的 JVM 的提升，例如 GC，没有进行详细的说明，有时间需要单独开一篇来单独讲

## 局部变量类型推断

Java 10 引入了一个新的语言关键字 `var`，这个关键字肯定不陌生，它可以在声明**局部变量**时替换类型信息，当编译器不能正确识别出变量的数值类型时，`var` 将不被允许使用；

var 最大的价值就是定义复杂类型的时候，例如 List 泛型的深层次嵌套；从 Java 11 开始，lambda 表达式的参数也允许使用 var 关键字，这样使得你可以为这些参数添加注解标识；

## 集合与字符串

集合框架新增了一系列的 of 方法来产生不可变对象：

``` java
public class CollectionApi {
  public static void main(String[] args) {
    // 创建的是不可变集合
    var list = List.of("A", "B", "C");
    // copy 的结果也是不可变
    var copy = List.copyOf(list);
    // true， 如果 copy 的不是不可变对象，会得到一个新实例
    System.out.println(list == copy);

    var map = Map.of("A", 1, "B", 2);
    System.out.println(map);
  }
}
```

字符串新增了 isBlank、lines、strip 等方法；strip 与 trim 的区别是它能去除 Unicode 空白符，例如中文全角的空格。

## HTTP Client

Java 9 引入了一个新的孵化 `HttpClient`  API 来处理 HTTP 请求。从 Java 11 开始，这个 API 已经可以在标准库 `java.net` 中使用了；这个新的 HttpClient 既可以被同步使用，也可以被异步使用。同步请求将会阻塞当前的线程，直到返回响应消息。

这样就不需要再引入 Apache 的库了，简单的使用直接用它就好了。

并且支持 WebSocket 和 HTTP2 流以及服务器推送特性。

## Epsilon

A NoOp Garbage Collectors

启用方法：`-XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC`

一款新的实验性无操作垃圾收集器。Epsilon GC 只负责内存分配，不实现任何内存回收机制。这对于性能测试非常有用，可用于与其他 GC 对比成本和收益。

一旦可用堆内存用完，JVM 就会退出，如果有 `System.gc()` 调用，实际上什么也不会发生（这种场景下和 `-XX:+DisableExplicitGC` 效果一样），因为没有内存回收，这个实现可能会警告用户尝试强制 GC 是徒劳。

主要用途如下:

- 性能测试（它可以帮助过滤掉 GC 引起的性能假象）
- 内存压力测试（例如，知道测试用例应该分配不超过 1GB 的内存，我们可以使用 `-Xmx1g -XX:+UseEpsilonGC` 如果程序有问题，则程序会崩溃

- 非常短的 JOB 任务（对象这种任务，接受 GC 清理堆那都是浪费空间）
- VM 接口测试
- Last-drop 延迟 & 吞吐改进

## ZGC

新的垃圾收集器 ZGC。一种可伸缩的低延迟垃圾收集器（实验性）。

启用方法：`-XX:+UnlockExperimentalVMOptions -XX:+UseZGC`

这应该是 java11 最为瞩目的特性，没有之一，但是它也是实验性质，生产环境不建议使用；

ZGC, A Scalable Low-Latency Garbage collector( Experimental) ZGC 一个可伸缩的低延时的垃圾回收器；

GC 暂停时间不会超过 10ms，既能处理几百兆的小堆，也能处理 T 的大堆（OMG）和 G1 相比，应用吞吐能力不会下降超过 15% 为未来的 GC 功能和利用 colord 指针以及 Load barriers 优化奠定基础。初始只支持 64 位系统；

ZGC 的设计目标是：支持 TB 级内存容量，暂停时间低 （<10ms），对整个程序吞吐量的影响小于 15%。将来还可以扩 展实现机制，以支持不少令人兴奋的功能，例如多层堆（即热对象置于 DRAM 和冷对象置于 NVMe 闪存），或压缩堆；

GC 是 java 主要优势之一，然而，当 GC 停顿太长，就会开始影响应用的响应时间，消除或者减少 GC 停顿时长，java 将对更广泛的应用场景是一个更有吸引力的平台，此外，现代系统中可用内存不断增长，用户和程序员希望 JVM 能够以高效的方式充分利用这些内存，并且无需长时间的 GC 暂停时间；

ZGC 是一个并发，基于 region，压缩型的垃圾收集器，只有 root 扫描阶段会 STW，因此 GC 停顿时间不会随着堆的增长和存活对象的增长而变长；

| 垃圾回收机 | 等待平均值(ms) | 等待最大值(ms) |
| ---------- | -------------- | -------------- |
| ZGC        | 1.091          | 1.681          |
| G1         | 156.806        | 543.846        |

cardtable 用于记录那些分代收集中从更老的对象引用了年轻对象的 card

---

其他 GC 相关的信息：

从增强了Garbage-First（G1）并用它替代 Parallel GC 成为默认的垃圾收集器。

删除了JDK 8 中弃用的 GC 组合（DefNew + CMS，ParNew + SerialOld，Incremental CMS）。

Java 12 中引入一个新的低停顿垃圾收集器：Shenandoah。其工作原理是通过与 Java 应用程序中的执行线程同时运行，用以执行其垃圾收集、内存回收任务，通过这种运行方式，给虚拟机带来短暂的停顿时间。

JDK8 默认的 GC：Parallel Scavenge（新生代）和 Parallel Old（老年代）

对于 G1 GC，相比于 JDK8，升级到 JDK 11 即可免费享受到：并行的 Full GC，快速的 CardTable 扫描，自适应的堆占用比例调整(IHOP)，在并发标记阶段的类型卸载等等。这些都是针对 G1 的不断增强，其中串行 FullGC 等甚至是曾经被广泛诟病的短板，你会发现 GC 配置和调优在 JDK11 中越来越方便

## JFR

Flight Recorder 源自飞机的黑盒子。 Flight Recorder 以前是商业版的特性，在 java11 当中开源出来，它可以导出事件到文件中，之后可以用 Java Mission Control 来分析。

两种启动方式：

1. 可以在应用启动时配置 `java -XX:StartFlightRecording`

2. 应用启动之后，使用 jcmd 来录制，如下代码：

   ```sh
    jcmd <pid> JFR.start  # 启动记录仪
    jcmd <pid> JFR.dump.filename=recording.jfr  # 将记录内容保存到文件里
    jcmd <pid> JFR.stop  # 停止记录仪
   ```

是 Oracle 刚刚开源的强大特性。我们知道在生产系统进行不同角度的 Profiling，有各种工具、框架，但是能力范围、可靠性、开销等，大都差强人意，要么能力不全面，要么开销太大，甚至不可靠可能导致 Java 应用进程宕机。

而 JFR 是一套集成进入 JDK、JVM 内部的事件机制框架，通过良好架构和设计的框架，硬件层面的极致优化，生产环境的广泛验证，它可以做到极致的可靠和低开销。

在 SPECjbb2015 等基准测试中，JFR 的性能开销最大不超过 1%，所以，工程师可以基本没有心理负担地在大规模分布式的生产系统使用，这意味着我们既可以随时主动开启 JFR 进行特定诊断，也可以让系统长期运行 JFR，用以在复杂环境中进行 “After-the-fact” 分析。还需要苦恼重现随机问题吗? JFR 让问题简化了很多；

在保证低开销的基础上，JFR 提供的能力也令人眼前一亮，例如：我们无需 BCI 就可以进行 Object Allocation Profiling， 终于不用担心 BTrace 之类把进程搞挂了。

对锁竞争、阻塞、延迟，JVM GC、SafePoint 等领域，进行非常细粒度分析。甚至深入 JIT Compiler 内部，全面把握热点方法、内联、逆优化等等。

JFR 提供了标准的 Java，C++ 等扩展 API，可以与各种层面的应用进行定制、集成，为复杂的企业应用栈或者复杂的分布式应用，提供 All-in-One 解决方案。而这一切都是内建在 JDK 和 JVM 内部的，并不需要额外的依赖，开箱即用。

## jshell

jshell 在 java9 里被提出来的，就是可以直接在终端里写 java 程序了，命令行输入 jshell 回车就可以执行，不用先创建 java 文件，然后编译成 class 文件，最后再执行了，它把这些步骤都省了，类似于脚本语言的执行方式，并且它自动导入了一些核心包，本质是相当于内部类来执行，不过感觉用处不大，简单玩玩还行。

``` java
import java.lang.*;
import java.io.*;
import java.math.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.prefs.*;
import java.util.regex.*;
import java.util.stream.*;
```

以上自动导入的包。

## inputStream与Stream

``` java
var classLoader = ClassLoader.getSystemClassLoader();
var inputStream = classLoader.getResourceAsStream("myFile.txt");
var tempFile = File.createTempFile("myFileCopy", "txt");
try (var outputStream = new FileOutputStream(tempFile)) {
  // 新增 API
    inputStream.transferTo(outputStream);
}



// 新增 API
Stream.of(1, 2, 3, 2, 1)
    .dropWhile(n -> n < 3)
    .collect(Collectors.toList());  // [3, 2, 1]

Stream.of(1, 2, 3, 2, 1)
    .takeWhile(n -> n < 3)
    .collect(Collectors.toList());  // [1, 2]
```

## 其他

像 Java9 的模块化相关内容就不详细跟进了，感觉用的基本没有，虽然看似很好，毕竟 OSGi 我都没用过。

模块化的好处就是瘦身，之前随便跑一个 Hello World 程序都需要上百兆的环境， JRE 中有一个超级大的 rt.jar 60 多 M，tools.jar 也有几十兆；

如果使用模块化，达到了瘦身目的，但是开发成本会增加，这个还是要取舍。

---

在 Java11 里可以直接使用 java 命令来执行单个的 java 文件，执行命令 `java Hello.java` 执行过程中不会生成 class 文件（当前目录下不会生成）

---

Java11 进行了很多瘦身工作，移除了很多较少使用的模块，不过因为这些模块平时用的不多，就不过多介绍了。

---

在 Docker 容器中运行 Java 应用程序一直存在一个问题，那就是在容器中运行 JVM 程序在设置内存大小和 CPU 使用率后，会导致应用程序的性能下降。

这是因为 Java 应用程序没有意识到它正在容器中运行。随着 Java 10 的发布，这个问题总算得以解决，JVM 现在可以识别由容器控制组（cgroups）设置的约束。可以在容器中使用内存和 CPU 约束来直接管理 Java 应用程序，其中包括：

- 遵守容器中设置的内存限制
- 在容器中设置可用的 CPU
- 在容器中设置 CPU 约束

参考：[Docker面对Java将不再尴尬](https://www.techug.com/post/java-on-docker-will-no-longer-suck-improvements-coming-in-java-10.html)

---

废弃 JS 引擎 Nashorn。可以考虑 GraalVM。

---

统一了 JVM 日志，为所有组件引入了同一个日志系统。

---

**响应式流（Reactive Streams) API**: Java 9 中引入了新的响应式流 API 来支持 Java 9 中的响应式编程，或者叫做 Flow API，基于发布订阅模式。

区别是新增背压的概念，就是我能处理多少就跟你要多少，你不要多给我，区别于传统的消息订阅模型。

## 需要注意

从 Java9 开始，OpenJDK 官方已经移除了 JDK 的 tools.jar、rt.jar、dt.jar，用到其中的相关类就没法编译了，使用模块化实现。

过去它们分别的作用是：编译相关的基础库（bin 下的可执行文件实际调用的程序本体，包括服务器编译 JSP）、Java 标准库的 class、swing 相关。

总之，可以理解为 9 之后，默认情况下，大多数 JDK 的内部 API 都不可访问。

## 参考

https://zhuanlan.zhihu.com/p/82717035

https://tomoya92.github.io/2019/02/21/java11/