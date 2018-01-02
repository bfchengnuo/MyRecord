# Java使用Jetty实现嵌入式Web服务器及Servlet容器

首先来介绍下 Jetty，根据 wiki 的介绍：

Jetty 是一个纯粹的基于 Java 的网页服务器和 Java Servlet 容器。尽管网页服务器通常用来为人们呈现文档，**但是 Jetty 通常在较大的软件框架中用于计算机与计算机之间的通信。**

**Jetty 作为 Eclipse 基金会的一部分，是一个自由和开源项目**。该网页服务器被用在 Apache ActiveMQ、Alfresco、Apache Geronimo、Apache Maven、Google App Engine、Eclipse、FUSE 等产品上。

Jetty 也是 Lift、Eucalyptus、Red5、Hadoop、I2P 等开源项目的服务器。Jetty 支持最新的 Java Servlet API（带 JSP 的支持），支持 SPDY 和 WebSocket 协议。

2016年，Jetty 的代码主仓库已经迁移到了 [Github](https://github.com/eclipse/jetty.project) ，但是其仍然处于 Eclipse IP Process 政策下开发。

**Jetty 在嵌入式的 Java 应用程序中提供 Web 服务**，其已经是 Eclipse IDE 中的一个组成部分。它支持 AJP、JASPI、JMX、JNDI、OSGi、WebSocket 和其他的 Java 技术。

Apache Hadoop 是 Jetty 应用在框架中的典型范例。 Hadoop 在几个模块中使用Jetty作为 Web 服务器

总结一下：

> Jetty 是一个 Java 实现的开源的 servlet 容器，它既可以像 Tomcat 一样作为一个**完整的** Web 服务器和 Servlet 容器，同时也可以嵌入在 Java 应用程序中，在 Java 程序中调用 Jetty
>
> 因为它的“轻量级”，在不是很复杂的小项目中是个不错的选择，启动（加载）也非常的快速

下面主要看下 Jetty 在嵌入式的 Java 应用程序中的应用

## 加载静态页面

导入依赖就不说了，Jetty  本身就是通过 jar 包的方式分发，或者可以使用 Maven 来构建：

``` xml
<dependency>
  <groupId>org.eclipse.jetty</groupId>
  <artifactId>jetty-servlet</artifactId>
  <version>9.2.1.v20140609</version>
</dependency>
```

当然 Servlet 相关的那些依赖不要忘了加入，然后是 Java 代码入口：

``` java
public static void main(String[] args) throws Exception {
  Server server = new Server(8080);

  ResourceHandler resourceHandler = new ResourceHandler();
  resourceHandler.setResourceBase("D:/test");
  // 可显示目录结构，类似 FTP
  resourceHandler.setDirectoriesListed(true); 

  server.setHandler(resourceHandler);
  server.start();
} 
```

运行 Java 程序，Jetty 服务器就会启动了，在浏览器中就可以访问了，但是这种方式只能访问静态页面，不支持 Servlet/JSP

## 实现Servlet容器(外部)

Java 代码主入口：

``` java
public static void main(String[] args) throws Exception {  
  Server server = new Server(8080);
  
  WebAppContext webapp = new WebAppContext();
  webapp.setResourceBase("E:/apache-tomcat-7.0.47/webapps/test");
  // 也可以通过设置 war 包的方式
  // webapp.setWar("C:/TVPlay.war");
  
  server.setHandler(webapp);
  server.start();
}
```

就是设置一个 Java Web 应用程序的目录就可以了，这种是使用外部文件（地址）的方式

## 运行内部编写的Servlet

很多时候是我们需要写几个 Servlet，犯不着建个 web 工程，这时候用 Jetty 来嵌入一个服务器最合适不过了，主入口：

``` java
public static void main(String[] args) throws Exception {
  Server server = new Server(8080);

  ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
  // Or ServletContextHandler.NO_SESSIONS
  context.setContextPath("/");
  server.setHandler(context);

  // http://localhost:8080/hello
  context.addServlet(new ServletHolder(new HelloServlet()), "/hello");
  // http://localhost:8080/hello/Kerronex
  context.addServlet(new ServletHolder(new HelloServlet("Hello Kerronex!")), "/hello/Kerronex");

  server.start();
  server.join();
}
```

具体对应的 Servlet 我就不贴了，很简单的 doGet 测试下就可以了~~

打包后直接用命令 `java -jar xxx.jar` 允许就可以啦

### 关于join

如果 server 没有起来，这里面 join() 函数起到的作用就是**使线程阻塞**， 这里 join() 函数实质上调用的 jetty 的线程池( 这里和 Thread 中的 join 函数相似 )

如果没有 join 函数，jetty 服务器也能正常启动或运行正常，**是因为 jetty 比较小，启动速度非常快**

然而如果你的 application 比较重的话， 调用 join 函数，能够保证你的 server 真正的起来（也就是说在 jetty start 之前 join 方法都是阻塞状态，避免 JVM 退出）

## 其他

TODO：使用 Jetty 构建 web 项目

需要使用插件及相关依赖：

``` xml
<dependency>
  <groupId>org.eclipse.jetty</groupId>
  <artifactId>jetty-websocket</artifactId>
  <version>8.1.11.v20130520</version>
</dependency>
<dependency>
  <groupId>org.eclipse.jetty</groupId>
  <artifactId>jetty-webapp</artifactId>
  <version>8.1.11.v20130520</version>
</dependency>
<!-- jetty -->
<dependency>
  <groupId>org.eclipse.jetty</groupId>
  <artifactId>jetty-server</artifactId>
  <version>8.1.11.v20130520</version>
</dependency>


<build>
  <plugins>
    <plugin>
      <groupId>org.eclipse.jetty</groupId>
      <artifactId>jetty-maven-plugin</artifactId>
      <version>9.3.7.v20160115</version>
      <configuration>
        <webApp>
          <contextPath>/</contextPath>
        </webApp>
        <scanIntervalSeconds>3</scanIntervalSeconds>
        <scanTargetPatterns>
          <scanTargetPattern>
            <directory>src/main/webapp</directory> 
            <includes>
              <include>**/*.xml</include>
              <include>**/*.properties</include>
            </includes>
          </scanTargetPattern>
        </scanTargetPatterns>
        <webAppConfig>
          <defaultsDescriptor>src/main/resource/webdefault222.xml</defaultsDescriptor>
        </webAppConfig>
        <connectors>
          <connector implementation="org.eclipse.jetty.server.nio.SelectChannelConnector">
            <port>8010</port>
            <maxIdleTime>400000</maxIdleTime>
          </connector>
        </connectors>
      </configuration>
    </plugin>
    </plugins>
</build>
```

相关命令：

`mvn jetty:run`

`mvn -Djetty.http.port=9999 jetty:run`

参见：

https://www.zhihu.com/question/52433013

http://www.blogjava.net/fancydeepin/archive/2015/06/23/maven-jetty-plugin.html

http://blog.csdn.net/tomato__/article/details/37927813

---

单独下载的 Jetty 的 jar 包就可以单独运行，也是使用 Java -jar 命令