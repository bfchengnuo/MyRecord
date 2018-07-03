Java启动参数共分为三类：

1. 标准参数（-），所有的 JVM 实现都必须实现这些参数的功能，而且向后兼容；
2. 非标准参数（-X），默认 jvm 实现这些参数的功能，但是并不保证所有 jvm 实现都满足，且不保证向后兼容；
3. 非 Stable 参数（-XX），此类参数各个 jvm 实现会有所不同，将来可能会随时取消，需要慎重使用；

以下展示部分参数，较全的参考：http://www.tianshouzhi.com/api/tutorials/jvm/99

## 常用配置

参考：

https://blog.csdn.net/csujiangyu/article/details/52071473

https://blog.csdn.net/java2000_wl/article/details/8030172

### 整体考虑堆大小

`-Xms3550m`， 初始化堆大小。通常情况和 `-Xmx` 大小设置一样，避免虚拟机频繁自动计算后调整堆大小。 

`-Xmx3550m`，最大堆大小。

### 考虑分代设置堆大小

首先通过 jstat 等工具查看应用程序正常情况下需要堆大小，再根据实际情况设置。

#### 新生代

`-xmn2g`，新生代大小。Sun 官方推荐配置为整个堆的 `3/8`。 

`-XX:SurvivorRatio=8`，Eden 和 Survivor 的比值。

#### 老年代

老年代 = 整个堆大小 - 新生代 - 永久代

#### 永久代

`-XX:Permsize=512m` ，设置永久代初始值。 

`-XX:MaxPermsize=512m`，设置永久代的最大值。 

注：Java8 没有永久代说法，它们被称为元空间，`-XX:MetaspaceSize=N`

### 考虑本机直接内存

`-XX:MaxDirectMemorySize=100M`。默认与 Java 堆大最大值 (-Xmx)

### 考虑虚拟机栈

每个线程池的堆栈大小。在 jdk5 以上的版本，每个线程堆栈大小为 1m，jdk5 以前的版本是每个线程池大小为 256k 。一般设置 256k。 

`-Xss256K.`

### 考虑选择垃圾收集器

配置方式：

表一：Sun/oracle JDK GC组合方式

| 参数                                     | 新生代GC方式                  | 老年代和持久**代**GC方式                                     |
| ---------------------------------------- | ----------------------------- | ------------------------------------------------------------ |
| -XX:+UseSerialGC                         | Serial 串行GC                 | Serial Old 串行GC                                            |
| -XX:+UseParallelGC                       | Parallel Scavenge  并行回收GC | Serial Old  并行GC                                           |
| -XX:+UseConcMarkSweepGC                  | ParNew 并行GC                 | CMS 并发GC  当出现“Concurrent Mode Failure”时 采用Serial Old 串行GC |
| -XX:+UseParNewGC                         | ParNew 并行GC                 | Serial Old 串行GC                                            |
| -XX:+UseParallelOldGC                    | Parallel Scavenge  并行回收GC | Parallel Old 并行GC                                          |
| -XX:+UseConcMarkSweepGC -XX:+UseParNewGC | Serial 串行GC                 | CMS 并发GC  当出现“Concurrent Mode Failure”时 采用Serial Old 串行GC |

---

| 参数                    | 描述                                                         | 新生代GC方式                  | 老年代和持久代GC方式                                         |
| ----------------------- | ------------------------------------------------------------ | ----------------------------- | ------------------------------------------------------------ |
| -XX:+UseSerialGC        | Jvm运行在Client模式下的默认值，打开此开关后，使用Serial + Serial Old的收集器组合进行内存回收 | Serial 串行GC                 | Serial Old 串行GC                                            |
| -XX:+UseParNewGC        | 打开此开关后，使用ParNew + Serial Old的收集器进行垃圾回收    | ParNew 并行GC                 | Serial Old 串行GC                                            |
| -XX:+UseConcMarkSweepGC | 使用ParNew + CMS +  Serial Old的收集器组合进行内存回收，Serial Old作为CMS出现“Concurrent Mode Failure”失败后的后备收集器使用。 | ParNew 并行GC                 | CMS 并发GC 当出现“Concurrent Mode Failure”时采用Serial Old 串行GC |
| -XX:+UseParallelGC      | Jvm运行在Server模式下的默认值，打开此开关后，使用Parallel Scavenge +  Serial Old的收集器组合进行回收 | Parallel Scavenge  并行回收GC | Serial Old 串行GC                                            |
| -XX:+UseParallelOldGC   | 使用Parallel Scavenge +  Parallel Old的收集器组合进行回收    | Parallel Scavenge  并行回收GC | Parallel Old 并行GC                                          |

---

表二：关于GC的一些常用参数：

| 参数                               | 描述                                                         |
| ---------------------------------- | ------------------------------------------------------------ |
| -XX:+UseSerialGC                   | Jvm运行在Client模式下的默认值，打开此开关后，使用Serial + Serial Old的收集器组合进行内存回收 |
| -XX:+UseParNewGC                   | 打开此开关后，使用ParNew + Serial Old的收集器进行垃圾回收    |
| -XX:+UseConcMarkSweepGC            | 使用ParNew + CMS +  Serial Old的收集器组合进行内存回收，Serial Old作为CMS出现“Concurrent Mode Failure”失败后的后备收集器使用。 |
| -XX:+UseParallelGC                 | Jvm运行在Server模式下的默认值，打开此开关后，使用Parallel Scavenge +  Serial Old的收集器组合进行回收 |
| -XX:+UseParallelOldGC              | 使用Parallel Scavenge +  Parallel Old的收集器组合进行回收    |
| -XX:SurvivorRatio                  | 新生代中Eden区域与Survivor区域的容量比值，默认为8，代表Eden:Subrvivor = 8:1 |
| -XX:PretenureSizeThreshold         | 直接晋升到老年代对象的大小，设置这个参数后，大于这个参数的对象将直接在老年代分配 |
| -XX:MaxTenuringThreshold           | 晋升到老年代的对象年龄，每次Minor GC之后，年龄就加1，当超过这个参数的值时进入老年代 |
| -XX:UseAdaptiveSizePolicy          | 动态调整java堆中各个区域的大小以及进入老年代的年龄           |
| -XX:+HandlePromotionFailure        | 是否允许新生代收集担保，进行一次minor gc后, 另一块Survivor空间不足时，将直接会在老年代中保留 |
| -XX:ParallelGCThreads              | 设置并行GC进行内存回收的线程数                               |
| -XX:GCTimeRatio                    | GC时间占总时间的比列，默认值为99，即允许1%的GC时间，仅在使用Parallel Scavenge 收集器时有效 |
| -XX:MaxGCPauseMillis               | 设置GC的最大停顿时间，在Parallel Scavenge 收集器下有效       |
| -XX:CMSInitiatingOccupancyFraction | 设置CMS收集器在老年代空间被使用多少后出发垃圾收集，默认值为68%，仅在CMS收集器时有效，-XX:CMSInitiatingOccupancyFraction=70 |
| -XX:+UseCMSCompactAtFullCollection | 由于CMS收集器会产生碎片，此参数设置在垃圾收集器后是否需要一次内存碎片整理过程，仅在CMS收集器时有效 |
| -XX:+CMSFullGCBeforeCompaction     | 设置CMS收集器在进行若干次垃圾收集后再进行一次内存碎片整理过程，通常与UseCMSCompactAtFullCollection参数一起使用 |
| -XX:+UseFastAccessorMethods        | 原始类型优化                                                 |
| -XX:+DisableExplicitGC             | 是否关闭手动System.gc                                        |
| -XX:+CMSParallelRemarkEnabled      | 降低标记停顿                                                 |
| -XX:LargePageSizeInBytes           | 内存页的大小不可设置过大，会影响Perm的大小，-XX:LargePageSizeInBytes=128m |

#### Serial收集器(串行收集器)

历史最悠久的串行收集器。参数 `-XX:UseSerialGC`。不太常用。

#### Parallel Scavenge(吞吐量优先垃圾收集器)

**并行**收集器，不同于多线程收集器 ParNew，关注吞吐量的收集器。

`-XX:MaxGCPauseMillis=10`，设置垃圾收集停顿的最大毫秒数。 

`-XX:GCTimeRatio=49`，垃圾收集器占比，默认是 99。

`-XX:+UseAdaptiveSeizPolicy`，GC 自适应调节策略。

`-XX:+UseParallelGC` ，虚拟机 Server 模式默认值，使用 `Parallel Scavenge + Serial Old` 进行内存回收。

`-XX:+UseParallelOldGC`, 使用 `Parallel Scavenge + Parallel Old` 进行内存回收。

#### CMS

CMS 作为老年代收集器，不能与 Parallel Scavenge 并存。可能会有内存碎片问题。 

`-XX:+UserConcMarkSweepGC`，新生代默认用 ParNew 收集。也可以用 `-XX:+UserParNewGC` 强制指定新生代用 ParNew 收集. 

`-XX:ParallelGCThreads=4`，设置垃圾收集线程数。默认是`(CPU数量+3)/4`。垃圾收集线程数不少于 25%，当 CPU 数量小于 4 的时候影响大。

`-XX:CMSInitiatingOccupancyFraction=80`，老年代垃圾占比达到这个阈值开始 CMS 收集，1.6 版本默认是 92。设置过高容易导致并发收集失败，会出现 SerialOld 收集的情况。 

`-XX:+UseCMSCompactAtFullCollection`，在 FULL GC 的时候， 对年老代的压缩增加这个参数是个好习惯。可能会影响性能，但是可以消除碎片。 

`-XX:CMSFullGCsBeforeCompaction=1`，多少次后进行内存压缩。 

`-XX:+CMSParallelRemarkEnabled`, 为了减少第二次暂停的时间，开启并行 remark ，降低标记停顿

#### G1(Garbage First)

`-XX:+UseG1GC`，谨慎使用，需要经过线上测试，还没有被设置为默认垃圾收集器。

之前的垃圾收集器收集的范围是新生代或者老年代，而 G1 垃圾收集器收集的范围包括新生代和老年代整个堆。

G1 将 Java 堆划为多个大小相同的独立区域 (Region)，垃圾收集单位是 Region。G1 垃圾收集适合至少大于 4G 内存得系统。并且不会产生内存空间碎片。

堆内存中一个Region的大小可以通过`-XX:G1HeapRegionSize`参数指定，大小区间只能是1M、2M、4M、8M、16M和32M，总之是2的幂次方，如果 G1HeapRegionSize 为默认值，则在堆初始化时计算Region的实践大小 

| 参数                    | 含义                                |
| ----------------------- | ----------------------------------- |
| -XX:MaxGCPauseMillis    | 设置G1收集过程目标时间，默认值200ms |
| -XX:G1NewSizePercent    | 新生代最小值，默认值5%              |
| -XX:G1MaxNewSizePercent | 新生代最大值，默认值60%             |

### 其他参数

`-XX:MaxTenuringThreshold=30`，晋升老年代的年龄。 
`-XX:PretenureSizeThreshold=?`，晋升老年代的对象大小。

### 考虑日志打印

`-verbose:gc`，打印 GC 日志 
`-XX:+PrintGC`，打印 GC 基本日志 
`-XX:+PrintGCDetails`，打印 GC 详细日志 
`-XX:+PrintGCTimeStamps`，打印相对时间戳 
`-XX:+PrintGCApplicationStoppedTime`，打印垃圾回收期间程序暂停的时间 
`-XX:+PrintGCApplicationConcurrentTime`，打印每次垃圾回收前,程序未中断的执行时间 
`-XX:+PrintTenuringDistribution`：查看每次 minor GC 后新的存活周期的阈值 
`-XX:+PrintTLAB`，查看 TLAB 空间的使用情况 
`-Xloggc:filename`，把相关日志信息记录到文件以便分析

### 考虑OOM(堆溢出)时保留现场日志

#### 当抛出OOM时进行heapdump

`-XX:+HeapDumpOnOutOfMemoryError`，JVM 异常自动生成堆转储 
`-XX:HeapDumpPath=`，堆转储文件名

## 标准参数（-）

JVM 的标准参数都是以 "-" 开头，通过输入"`java -help`"或者"`java -?`"，可以查看 JVM 标准参数列表

- `-client`
  设置 jvm 使用 client 模式，特点是启动速度比较快，但运行时性能和内存管理效率不高，通常用于客户端应用程序或者 PC 应用开发和调试。

- `-server`
  设置 jvm 使 server 模式，特点是启动速度比较慢，但运行时性能和内存管理效率很高，适用于生产环境。
  在具有 64 位能力的 jdk 环境下将默认启用该模式，而忽略 -client 参数。

- `-classpath classpath（-cp classpath）`
  告知 jvm 搜索目录名、jar 文档名、zip 文档名，之间用分号 `;` 分隔；
  使用 `-classpath` 后 jvm 将不再使用 CLASSPATH 中的类搜索路径，如果 `-classpath` 和 CLASSPATH 都没有设置，则 jvm 使用当前路径 (`.` ) 作为类搜索路径。
  jvm 搜索类的方式和顺序为：Bootstrap，Extension，User。

  Bootstrap ：中的路径是 jvm 自带的 jar 或 zip 文件，jvm 首先搜索这些包文件，用`System.getProperty("sun.boot.class.path")` 可得到搜索路径。
  Extension ：是位于 `JRE_HOME/lib/ext` 目录下的 jar 文件，jvm 在搜索完 Bootstrap 后就搜索该目录下的 jar 文件，用 `System.getProperty("java.ext.dirs")` 可得到搜索路径。
  User ：搜索顺序为当前路径 `.`、CLASSPATH、`-classpath`，jvm 最后搜索这些目录，用`System.getProperty("java.class.path")` 可得到搜索路径。

- `-Dproperty=value`
  设置系统属性名/值对，运行在此 jvm 之上的应用程序可用 `System.getProperty("property")` 得到 value 的值。
  如果 value 中有空格，则需要用双引号将该值括起来，如 `-Dname="space string"` 。
  该参数通常用于设置系统级全局变量值，如配置文件路径，以便该属性在程序中任何地方都可访问。

- `-jar`
  指定以 jar 包的形式执行一个应用程序。
  要这样执行一个应用程序，必须让 jar 包的 manifest 文件中声明初始加载的 Main-class，当然那 Main-class 必须有 `public static void main(String[] args)` 方法。

- `-verbose(-verbose:class)`
  输出 jvm 载入类的相关信息，当 jvm 报告说找不到类或者类冲突时可此进行诊断。

- `-verbose:gc `
  输出每次 GC 的相关情况。

- `-verbose:jni`
   输出 native 方法调用的相关情况，一般用于诊断 jni 调用错误信息。

- `-version`
   输出 java 的版本信息，比如 jdk 版本、vendor、model。

## 非标准参数（-x）

通过"`java -X`"可以输出非标准参数列表 

- `-Xint`
  设置 jvm 以解释模式运行，所有的字节码将被直接执行，而不会编译成本地码。
- `-Xbatch`
  关闭后台代码编译，强制在前台编译，编译完成之后才能进行代码执行；
  默认情况下，jvm 在后台进行编译，若没有编译完成，则前台运行代码时以解释模式运行。
- `-Xbootclasspath/a:path`
  将指定路径的所有文件追加到默认bootstrap路径中；
- `-Xbootclasspath/p:path`
  让 jvm 优先于 bootstrap 默认路径加载指定路径的所有文件；
- `-Xnoclassgc`
  关闭针对 class 的 gc 功能；因为其阻止内存回收，所以可能会导致 OutOfMemoryError 错误，慎用；
- `-Xloggc:file`
  与 `-verbose:gc` 功能类似，只是将每次 GC 事件的相关情况记录到一个文件中，文件的位置最好在本地，以避免网络的潜在问题。
  若与 verbose 命令同时出现在命令行中，则以 `-Xloggc` 为准。
- `-Xms<size>`
  指定 jvm 堆的初始大小，默认为物理内存的 1/64，最小为 1M；可以指定单位，比如 k、m，若不指定，则默认为字节。
- `-Xmx<size>`
  指定 jvm 堆的最大值，默认为物理内存的 1/4 或者 1G，最小为 2M；单位与 `-Xms` 一致。
- `-Xss<size>`
  设置单个线程栈的大小，一般默认为 512k。
- `-Xprof`
  输出 cpu 配置文件数据

> -Xms、-Xmx……都是我们性能优化中很重要的参数；
>
> -Xprof、-Xloggc:file 等都是在没有专业跟踪工具情况下排错的好手；

## JVM非Stable参数（-XX）

Java 6（update 21oder 21之后）版本， HotSpot JVM 提供给了两个新的参数，在JVM启动后，在命令行中可以输出所有XX参数和值：`-XX:+PrintFlagsFinal and -XX:+PrintFlagsInitial`

可以使用以下语句输出所有的参数和默认值 ：`java -XX:+PrintFlagsInitial  -XX:+PrintFlagsInitial>>1.txt`

这些参数可以被松散的聚合成三类：

- 行为参数（Behavioral Options）：用于改变 jvm 的一些基础行为；
- 性能调优（Performance Tuning）：用于 jvm 的性能调优；
- 调试参数（Debugging Options）：一般用于打开跟踪、打印、输出等jvm参数，用于显示 jvm 更加详细的信息；

---

### 行为参数(功能开关)

- `-XX:-DisableExplicitGC`
  禁止调用 `System.gc();`但 jvm 的 gc 仍然有效
- `-XX:+MaxFDLimit`
  最大化文件描述符的数量限制
- `-XX:+ScavengeBeforeFullGC`
  新生代 GC 优先于 Full GC 执行
- `-XX:+UseGCOverheadLimit`
  在抛出 OOM 之前限制 jvm 耗费在 GC 上的时间比例
- `-XX:-UseConcMarkSweepGC`
  对老生代采用并发标记交换算法进行 GC
- `-XX:-UseParallelGC`
  启用并行 GC
- `-XX:-UseParallelOldGC`
  对 Full GC 启用并行，当 `-XX:-UseParallelGC` 启用时该项自动启用
- `-XX:-UseSerialGC`
  启用串行 GC
- `-XX:+UseThreadPriorities`
  启用本地线程优先级

### 性能调优

- `-XX:LargePageSizeInBytes=4m`
  设置用于 Java 堆的大页面尺寸
- `-XX:MaxHeapFreeRatio=70`
  GC 后 java 堆中空闲量占的最大比例
- `-XX:MaxNewSize=size`
  新生成对象能占用内存的最大值
- `-XX:MaxPermSize=64m`
  老生代对象能占用内存的最大值
- `-XX:MinHeapFreeRatio=40`
  GC 后 java 堆中空闲量占的最小比例
- `-XX:NewRatio=2`
  新生代内存容量与老生代内存容量的比例
- `-XX:NewSize=2.125m`
  新生代对象生成时占用内存的默认值
- `-XX:ReservedCodeCacheSize=32m`
  保留代码占用的内存容量
- `-XX:ThreadStackSize=512`
  设置线程栈大小，若为 0 则使用系统默认值
- `-XX:+UseLargePages`
  使用大页面内存

### 调试参数

- `-XX:-CITime`
  打印消耗在JIT编译的时间
- `-XX:ErrorFile=./hs_err_pid<pid>.log`
  保存错误日志或者数据到文件中
- `-XX:-ExtendedDTraceProbes`
  开启 solaris 特有的 dtrace 探针
- `-XX:HeapDumpPath=./java_pid<pid>.hprof`
  指定导出堆信息时的路径或文件名
- `-XX:-HeapDumpOnOutOfMemoryError`
  当首次遭遇 OOM 时导出此时堆中相关信息
- `-XX:OnError="<cmd args>;<cmd args>"`
  出现致命 ERROR 之后运行自定义命令
- `-XX:OnOutOfMemoryError="<cmd args>;<cmd args>"`
  当首次遭遇 OOM 时执行自定义命令
- `-XX:-PrintClassHistogram` 
  遇到 Ctrl-Break 后打印类实例的柱状信息，与 jmap -histo 功能相同
- `-XX:-PrintConcurrentLocks`
  遇到 Ctrl-Break 后打印并发锁的相关信息，与 `jstack -l` 功能相同
- `-XX:-PrintCommandLineFlags`
  打印在命令行中出现过的标记
- `-XX:-PrintCompilation`
  当一个方法被编译时打印相关信息
- `-XX:-PrintGC`
  每次GC时打印相关信息

- `-XX:-PrintGC Details`
  每次GC时打印详细信息

- `-XX:-PrintGCTimeStamps`
  打印每次GC的时间戳

- `-XX:-TraceClassLoading`
  跟踪类的加载信息

- `-XX:-TraceClassLoadingPreorder`
  跟踪被引用到的所有类的加载信息

- `-XX:-TraceClassResolution`
  跟踪常量池

- `-XX:-TraceClassUnloading`
  跟踪类的卸载信息

- `-XX:-TraceLoaderConstraints`
  跟踪类加载器约束的相关信息