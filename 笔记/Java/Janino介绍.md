# Janino介绍

Janino 是一个超级小但又超级快的 **Java™ 编译器**. 它不仅能像 javac 工具那样将一组源文件编译成字节码文件，还可以对一些 Java 表达式，代码块，类中的文本(class body)或者内存中源文件进行编译，并把编译后的字节码直接加载到同一个 JVM 中运行.

Janino 不是一个开发工具,  **而是作为运行时的嵌入式编译器**，比如作为表达式求值的翻译器或类似于 JSP 的服务端页面引擎；

Janino 还被整合到 Apache Commons JCL 项目和 JBoss Rules/Drools 项目中；

Janino 可以被用于静态代码分析或者对代码进行修改

## 产生原因

对使用过 JSP 的 Java 开发人员来说，Java 源代码（内嵌于JSP 中的）动态编译成类文件是很熟悉的技术。但却未必知晓此技术的复杂性。

而造成它比较笨拙的原因在于需要安装 JDK（非JRE）。由于 JDK 并非免费（被 Oracle 收购后尤其突出），于是又出现了 license 的问题。此外，对于不同的平台，需进行不同的平台配置并部署相应的类文件。对简化版的程序也同样需要复杂的操作。

尽管存在以上的问题，一些开源内库如 Jasper 仍基于 JDK （即使有完全遵守 GPL 的 OpenJDK），且能提供很好的程序执行功能。

基于 Java 的构建工具 Ant 同样面临在编译 Java 代码时需克服过于复杂的问题。基于以上所述，**动态编译 Java 代码通常被认为是没有办法的办法，且在实际应用中表现欠佳。**

## 运行方式

Janino 采用如下的方式来解决这些问题：

1.    Janino 如同普通的应用程序一样，运行在 JVM 上，而不是将编译的工作间接转交给 javac（或是等同的 Java 编译器）来完成。同时也不需要额外的配置或安装 JDK。

2.    由于 Janino 直接从JVM获得 classes 类文件，而不是通过应用程序获取 class 文件和 `.jar` 文件。这意味着无需考虑权限问题或是构建路径的配置。

3.    Janino 提供一种简单易用的方式来编译表达、脚本和 classes 类文件。开发人员无需关心加载动态生成代码的技术细节，因为 Janino 自动实现了它。

      在最简单的层面，传递一个包含 Java 代码的字符串即可返回一个对象。

Janino 被更广范的应用于那些比较复杂、杂乱且与平台相关的 Java 应用程序的源码编译。Janino 通过动态编译代码，从而提高了程序的性能。

## 结论

在平时的开发工作中，有很多情况可以用到动态编译的技术。如下则列出了一些基本的参考意见：

1. 使用动态编译类来取代 Java 代理类，可避免所有的反射，因此可显著的提高性能。

2. 对于用户自定义功能等请求，可以像 Java 代码一样进行编译与评价，而之前只能采用域定义语言(domain-specific language)来解析与评价。

3. 如果数据记录的字段固定，但不知道编译所需时间，此时是 HashMaps 应用的典型场景。

   利用 Janino，已知的字段可用于构建私有字段。再加上一些额外的工作，可通过 Map 接口将字段暴露出来。如果字段类型相对记录数量来说很少时，内存的保存就显得很重要了。

在程序的性能优化方面 Janino 还有更多的应用场合，对于想更好的提高 Java 代码性能的开发人员，可以更加深入的研究Janino。

## 其他用途

最常见的应该是在日志框架里，当引用 slf4j + log4j/logback 的时候，常常会顺带引用 Janino 来提高日志输出的性能：

``` xml
<dependencies>
  <!-- 日志文件管理包 -->
  <!--  logback+slf4j -->
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>${slf4j.version}</version>
  </dependency>
  <dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-core</artifactId>
    <version>${logback.version}</version>
  </dependency>
  <dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-access</artifactId>
    <version>${logback.version}</version>
  </dependency>
  <dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>${logback.version}</version>
  </dependency>
  <!-- 日志文件管理包-提高性能log输出性能 -->
  <!--  janino -->
  <dependency>
    <groupId>org.codehaus.janino</groupId>
    <artifactId>commons-compiler</artifactId>
    <version>${janino.version}</version>
  </dependency>
  <dependency>
    <groupId>org.codehaus.janino</groupId>
    <artifactId>janino</artifactId>
    <version>${janino.version}</version>
  </dependency>
</dependencies>
```

## 附：提高性能的传统方法

Java 程序的慢速构建已经影响了系统可见的性能。它会影响程序开发者的效率、开发成本及程序的可扩展性。那么，对于采用 Java 作为开发语言的公司，又将如何提到程序的性能呢？下面是几种常用的性能优化方法： 

1. **优化算法**

   在本文的实例中，将采用最安全且原始的方法来提到程序性能。仅通过改变本地代码而实现提高性能的目标，并且能简易地测试程序的可扩展性。 

2. **优化硬件**

   此种方法可以比较原始的提高程序的性能。相比开发者花费数小时去优化软件而言，通过改变单个服务器的机器性能更合算些。但是这种优化硬件的方法并没有考虑到如下一些较复杂的因素：开发人员的需要、应用程序被多次部署的可能性以及硬件能能被更新的空间。 

3. **优化软件**

   通常而言，Java代码（Java库、应用服务器、数据库驱动程序等）的运行效率较其它语言会更高些。尽量保证程序代码的高效性，是提高应用程序性能的不二选择。但遗憾的是，这往往很难预先决定，因为类库的性能特性取决于操作环境，而操作环境不到开发的最后阶段是不确定的。

4. **优化程序架构**

   由于对人员素质要求较高，优化程序架构是最后的方法。通常而言，对程序的每一次架构上的变动，代码都需部分的重写并重新的评估。对于特大型的项目，局部的架构变动是很有可能的，而这种情况下优化程序架构的风险就相当的高。      

如果为了提高程序的性能而穷尽以上的方法，那将是一件令人十分痛苦的事情，特别对一些复杂且难以理顺的大项目更是如此。

## 附2：JDK和JRE

Java Development Kit（JDK）是 Sun 针对 Java 开发人员发布的**免费软件开发工具包（SDK，Software development kit）**。自从 Java 推出以来，**JDK 已经成为使用最广泛的 Java SDK**。

由于 JDK 的一部分特性采用商业许可证，而非开源。因此，2006 年 Sun 宣布将发布基于 GPL 的开源 JDK，使 JDK 成为自由软件。在去掉了少量闭源特性之后，昇阳电脑最终促成了 GPL 的 OpenJDK 的发布。

作为Java语言的SDK，普通用户并不需要安装JDK来运行Java程序，而只需要安装JRE（Java Runtime Environment）。而程序开发者必须安装JDK来编译、调试程序。

JDK中还包括完整的JRE（Java Runtime Environment），Java运行环境，也被称为private runtime。包括了用于产品环境的各种库类，如基础类库rt.jar，以及给开发人员使用的补充库，如国际化与本地化的类库、IDL库等等。

https://zh.wikipedia.org/zh-hans/JDK

---

Java运行环境（Java Runtime Environment，简称JRE）是一个软件，由太阳微系统所研发，JRE 可以让电脑系统运行 Java应用程序（Java Application）。

JRE的内部有一个Java虚拟机（Java Virtual Machine，JVM）以及一些标准的类别函数库（Class Library）。

许可协议大部分是基于 GPL 的

---

Oracle 提供的 Java SE 在“通用计算”使用范围内仍然是完全免费的，**Oracle提供的Java SE Advanced系列的产品是收费的** ；

但其实很简单的判断方式就是：使用了 ``-XX:+UnlockCommercialVMOptions`` 的功能都是收费的。

> 一台台式机，装着普通的 Windows 或者 Linux，在上面跑 Java SE 是属于通用计算的范围内。
>
> 但如果这样一台台式机被包装到一个像 ATM 那样的柜子中，平时只运行某些特定的 Java 程序给客户提供服务的话，那就有可能要被归类到“嵌入式领域”。

Google 好像是在 Android 6.0 的某个版本使用了 OpenJDK，来彻底解决 JDK 的版权问题

~~毒瘤 Oracle，坑完 MySQL 坑 openOffice，坑完了 Solaris 坑 JAVA，据说跟甲骨文有关的新闻就疯狂的喷甲骨文就对了~~

## 参考

http://tech.it168.com/oldarticle/2007-06-08/200706080812093_all.shtml

https://www.zhihu.com/question/53791269