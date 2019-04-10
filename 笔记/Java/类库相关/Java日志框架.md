# Log4j配置

默认会读取资源目录下的 `log4j.properties` 文件，当然也可以自定义配置文件的位置：
` PropertyConfigurator.configure("D:/Code/conf/log4j.properties");`

配置文件的基本格式：

``` properties
# 配置根 Logger
log4j.rootLogger = [level], appenderName1, appenderName2,  …

# 配置日志信息输出目的地 Appender
log4j.appender.appenderName = fully.qualified.name.of.appender.class
  log4j.appender.appenderName.option1 = value1
  …
  log4j.appender.appenderName.optionN = valueN 

# 配置日志信息的格式（布局）
log4j.appender.appenderName.layout = fully.qualified.name.of.layout.class 
  log4j.appender.appenderName.layout.option1 = value1 
  … 
  log4j.appender.appenderName.layout.optionN = valueN
```

## 输出级别

关于 **level** 日志输出级别，共有五级：

| 标识    | ID   | 描述            |
| ----- | ---- | ------------- |
| FATAL | 0    | 适用于严重错误事件     |
| ERROR | 3    | 适用于代码存在错误事件   |
| WARN  | 4    | 适用于代码会有潜在错误事件 |
| INFO  | 6    | 适用于代码运行期间     |
| DEBUG | 7    | 适用于代码调试期间     |

除此之外还有两种状态就是 ALL： 打开所有日志；OFF：关闭所有日志；

## 输出目的地

Appender 为日志输出目的地，Log4j提供的appender有以下几种：

``` properties
org.apache.log4j.ConsoleAppender（控制台），
org.apache.log4j.FileAppender（文件），
org.apache.log4j.DailyRollingFileAppender（每天产生一个日志文件），
org.apache.log4j.RollingFileAppender（文件大小到达指定尺寸的时候产生一个新的文件），
org.apache.log4j.WriterAppender（将日志信息以流格式发送到任意指定的地方）
```

## 日志输出格式

Layout：日志输出格式，Log4j提供的layout有以下几种：

``` properties
org.apache.log4j.HTMLLayout（以HTML表格形式布局），
org.apache.log4j.PatternLayout（可以灵活地指定布局模式），
org.apache.log4j.SimpleLayout（包含日志信息的级别和信息字符串），
org.apache.log4j.TTCCLayout（包含日志产生的时间、线程、类别等等信息）
```

打印参数  Log4J 采用类似 C 语言中的 printf 函数的打印格式格式化日志信息，如下:

```
%m  输出代码中指定的消息
%p  输出优先级，即 DEBUG，INFO，WARN，ERROR，FATAL 
%r  输出自应用启动到输出该log信息耗费的毫秒数 
%c  输出所属的类目，通常就是所在类的全名 
%t  输出产生该日志事件的线程名 
%n  输出一个回车换行符，Windows 平台为 "\r\n"，Unix 平台为 "\n" 
%d  输出日志时间点的日期或时间，默认格式为ISO8601，也可以在其后指定格式，
	比如：%d{yyyy-MM-dd HH:mm:ss,SSS}，输出类似：2017-12-21 13:37:05 512
%l  输出日志事件的发生位置，包括类目名、发生的线程，以及在代码中的行数。
	举例：Testlog4.main(TestLog4.java: 10 )
[%10p]	右对齐，最小宽度10
[%-10p]	左对齐，最小宽度10
```

SSS 其实是毫秒的意思

## 历史

那就看下日志家族们：common-logging、log4j、slf4j、logback，裂墙推荐刘哥的文章：

> 在 log4j 被 Apache Foundation 收入门下之后，由于理念不合，log4j 的作者 Ceki 离开并开发了 slf4j 和 logback。

### common-logging

common-logging 是 Apache 提供的一个通用的日志接口

在 common-logging 中，有一个 Simple logger 的简单实现，但是它功能很弱，所以使用 common-logging ，通常都是配合着 log4j 来使用；

**common-logging 会通过动态查找的机制，在程序运行时自动找出真正使用的日志库，并且尽可能找到一个”最合适”的日志实现类，如果判断有 Log4j 包,则使用 log4j，最悲观的情况下也总能保证提供一个日志实现 (SimpleLog)**

### log4j&log4j2

Apache 的一个开放源代码项目，实现了输出到控制台、文件、 回滚文件、发送日志邮件、输出到数据库日志表、自定义标签等全套功能,且配置比较简单;

> **Apache Logging** 一直在关门憋大招，log4j2 在 beta 版鼓捣了几年，终于在 2014 年发布了 GA 版，不仅吸收了 logback 的先进功能，更通过优秀的锁机制、LMAX Disruptor、"无垃圾"机制等先进特性，在性能上全面超越了 log4j 和 logback。
>
> log4j2 弃用了 properties 方式配置，采用的是 .xml，.json或者.jsn这种方式来做

使用log4j2 需要两个依赖：

``` xml
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-core</artifactId>
  <version>2.5</version>
</dependency>
<dependency>
  <groupId>org.apache.logging.log4j</groupId>
  <artifactId>log4j-api</artifactId>
  <version>2.5</version>
</dependency>
```

配置文件示例，就是格式变成了 XML ，和上面其实也差不多：

``` xml
<?xml version="1.0" encoding="UTF-8"?>    
<configuration status="error">  
  <!-- 先定义所有的appender -->  
  <appenders>  
    <!-- 这个输出控制台的配置 -->  
    <Console name="Console" target="SYSTEM_OUT">  
      <!-- 控制台只输出level及以上级别的信息（onMatch），其他的直接拒绝（onMismatch）-->  
      <ThresholdFilter level="trace" onMatch="ACCEPT" onMismatch="DENY"/>
      <!-- 这个都知道是输出日志的格式 -->  
      <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>  
    </Console>  

    <!-- append 为TRUE表示消息增加到指定文件中，false 表示消息覆盖指定的文件内容，默认值是true -->
    <!-- 打印出所有的信息 -->
    <File name="log" fileName="log/test.log" append="false">  
      <PatternLayout pattern="%d{HH:mm:ss.SSS} %-5level %class{36} %L %M - %msg%xEx%n"/>  
    </File>  

    <!-- 添加过滤器ThresholdFilter,可以有选择的输出某个级别以上的类别 onMatch="ACCEPT" onMismatch="DENY"意思是匹配就接受,否则直接拒绝  -->  
    <File name="ERROR" fileName="logs/error.log">  
      <ThresholdFilter level="error" onMatch="ACCEPT" onMismatch="DENY"/>  
      <PatternLayout pattern="%d{yyyy.MM.dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n"/>  
    </File>  

    <!-- 这个会打印出所有的信息，每次大小超过size，则这size大小的日志会自动存入按年份-月份建立的文件夹下面并进行压缩，作为存档 -->  
    <RollingFile name="RollingFile" fileName="logs/web.log"  
                 filePattern="logs/$${date:yyyy-MM}/web-%d{MM-dd-yyyy}-%i.log.gz">  
      <PatternLayout pattern="%d{yyyy-MM-dd 'at' HH:mm:ss z} %-5level %class{36} %L %M - %msg%xEx%n"/>  
      <SizeBasedTriggeringPolicy size="2MB"/>  
    </RollingFile>  
  </appenders>  

  <!-- 然后定义logger，只有定义了logger并引入的appender，appender才会生效 -->  
  <loggers>  
    <!--  建立一个默认的root的logger -->  
    <root level="trace">  
      <appender-ref ref="RollingFile"/>  
      <appender-ref ref="Console"/>  
      <appender-ref ref="ERROR" />  
      <appender-ref ref="log"/>  
    </root>  
  </loggers>  
</configuration>  
```

配置其实都差不多，应该还是蛮好看懂的。

### slf4j

slf4J，即简单日志门面（Simple Logging Facade for Java），**不是具体的日志解决方案，它只服务于各种各样的日志系统**。按照官方的说法，SLF4J 是一个用于日志系统的简单 Facade，允许最终用户在部署其应用时使用其所希望的日志系统。

可以这么说，slf4j 等于 commons-logging，是各种日志实现的通用入口，会根据 classpath 中存在下面哪一个 Jar 来决定具体的日志实现库;

SLF4J 不同于其他日志类库，与其它有很大的不同。SLF4J (Simple logging Facade for Java) 不是一个真正的日志实现，而是一个抽象层（ abstraction layer），它允许你在后台使用任意一个日志类库。如果是在编写供内外部都可以使用的API或者通用类库，那么你真不会希望使用你类库的客户端必须使用你选择的日志类库

> 如果一个项目已经使用了log4j，而你加载了一个类库，比方说 Apache Active MQ——它依赖于于另外一个日志类库 logback，**那么你就需要把它也加载进去**。
>
> 但如果 Apache Active MQ 使用了 SLF4J，你可以继续使用你的日志类库 (当前是 log4j) 而无需忍受加载和维护一个新的日志框架的痛苦

slf4j 为各类日志输出服务提供了适配库，如 slf4j-log4j12，slf4j-simple，slf4j-jdk14 等。一个 Java 工程下只能引入一个 slf4j 适配库，slf4j 会加载 `org.slf4j.impl.StaticLoggerBinder` 作为输出日志的实现类。这个类在每个适配库中都存在，当需要更换日志输出服务时（比如从logback切换回log4j），只需要替换掉适配库即可。

slf4j还推出了jcl-over-slf4j 桥接库，能够把使用JCL的API输出的日志桥接到slf4j上，方便那些想要使用slf4j作为日志门面但同时又要使用Spring等需要依赖JCL的类库的系统

### logback

logback 是由 log4j 创始人设计的又一个开源日志组件。

logback 当前分成三个模块：logback-core、logback- classic 和 logback-access。

logback-core 是其它两个模块的基础模块。logback-classic 是 log4j 的一个 改良版本。此外 logback-classic 完整实现 SLF4J API 使你可以很方便地更换成其它日志系统如 log4j。

logback 天然与 slf4j 适配，不需要额外引入适配库（毕竟是一个作者写的）

想在 Java 程序中使用 Logback，需要依赖三个 jar 包，分别是 slf4j-api，logback-core，logback-classic。其中 slf4j-api 并不是 Logback 的一部分，是另外一个项目，但是强烈建议将 slf4j 与 Logback 结合使用。

> 而 logback-access 主要作为一个与 Servlet 容器交互的模块，比如说 tomcat 或者 jetty，提供一些与 HTTP 访问相关的功能。

### 其他问题

在 Java 领域日志工具中，最早得到广泛使用的是 log4j。那么为啥有 common-logging 的出现？上面已经介绍了common-logging 只提供 log 的接口，其中具体的实现时动态绑定的，所以 common-logging 与 log4j 的结合比较多！

但是随之也产生了一些问题，那就是 common-logging 的动态绑定有时候也会失败，在这样的背景下 slf4j 应运而生，slf4j 与 common-logging 一样提供 log 接口，但是 slf4j 是通过静态绑定实现。

> slf4j唯独没有提供log4j2的适配库和桥接库，log4j-slf4j-impl和log4j-to-slf4j都是Apache Logging自己开发的，看样子Ceki和Apache Logging的梁子真的很深啊……倒是Apache没有端架子，可能也是因为slf4j太火了吧

log4j2 和 logback 各有长处，总体来说，如果对性能要求比较高的话，log4j2 相对还是较优的选择

## log4j常用配置

网上搜集的一些配置：

``` properties
### set log levels ###  
log4j.rootLogger = debug, stdout, D, E  
  
### 输出到控制台 ###  
log4j.appender.stdout = org.apache.log4j.ConsoleAppender  
log4j.appender.stdout.Target = System.out  
log4j.appender.stdout.layout = org.apache.log4j.PatternLayout  
log4j.appender.stdout.layout.ConversionPattern =  %d{ABSOLUTE} %5p %c{ 1 }:%L - %m%n  
  
### 输出到日志文件 ###  
log4j.appender.D = org.apache.log4j.DailyRollingFileAppender
log4j.appender.D.File = logs/log.log
log4j.appender.D.Append = true
## 输出DEBUG级别以上的日志
log4j.appender.D.Threshold = DEBUG
log4j.appender.D.layout = org.apache.log4j.PatternLayout
log4j.appender.D.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %t:%r ] - [ %p ]  %m%n  

### 保存异常信息到单独文件 ###  
log4j.appender.E = org.apache.log4j.DailyRollingFileAppender
log4j.appender.E.File = logs/error.log
log4j.appender.E.Append = true
## 只输出ERROR级别以上的日志!!!
log4j.appender.E.Threshold = ERROR
log4j.appender.E.layout = org.apache.log4j.PatternLayout
log4j.appender.E.layout.ConversionPattern = %-d{yyyy-MM-dd HH:mm:ss}  [ %l:%c:%t:%r ] - [ %p ]  %m%
```

## 常见的用法

使用的是 slf4j 的 API 哦：

``` java
if (logger.isDebugEnabled()) {
  logger.debug("Processing trade with id: " + id + " symbol: " + symbol);
}

logger.debug("Processing trade with id: {} and symbol : {} ", id, symbol);
```

## 参考

http://www.jianshu.com/p/85d141365d39
https://segmentfault.com/a/1190000008315137