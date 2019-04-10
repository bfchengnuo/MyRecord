# J2EE存在如下13种技术规范

曾经是挺辉煌的，但是现在.......

> 除了struts 是基于 servlet 标准和 filter 标准之外，其他和 javaee 屁关系没有。
>
> 随着架构的演进，大家肯定会青睐更轻量级的方案，javaee 会渐渐退出历史舞台。
>
> 总结，javaee 至少是没有那么重要了，碰到标准没有的情况（虽然现代架构很容易就能碰到），比如集群消息组件（kafka），集群缓存组件（redis），集群并发计算组件（spark），别犹豫，不要为了 javaee 而 javaee。

- JDBC 为什么不是？

  因为你只需要安装 javase 的包就可以引入；忘记从那个版本开始 JDBC 已经属于 SE 的范围了。

- Servlet 也已经很多公司不再使用了

  比如鹅厂的广告部用play（隔壁 Typesafe 家 Play Framework）代替 javase 系列的 server 改进并发性能

- Java家族一向是民间普及率高于官方....

老版的解释请继续往下看

## JDBC（java Database Connectivity）

JDBC API 为访问不同的数据库提供了一种统一的途径，就像 ODBC 一样，JDBC 对开发者屏蔽了一些细节问题，同时，JDBC 对数据库的访问也具有平台无关性。

## JNDI(Java Name and Directory Interface)

JNDI API 被用于执行名字和目录服务。它提供了一致的模型用来存取和操作企业级的资源如DNS和LDAP，本地文件系统，或应用服务器中的对象。

## EJB（Enterprise JavaBean）

J2ee技术之所以赢得全体广泛重视的原因之一就是 EJB，他们提供了一个框架开发和实施分布式商务逻辑，由此很显著简化了具有可伸缩性和高度复杂的企业级应用开发。

EJB规范定义了EJB组件何时如何与他们的容器继续拧交互作用。容器负责提供公用的服务，例如目录服务、事务管理、安全性、资源缓冲池以及容错性。

但是注意的是，EJB并不是J2EE的唯一途径。正是由于EJB的开放性，使得有的厂商能够以一种和EJB平行的方式来达到同样的目的。

## RMI（RemoteMethod Invoke）

remote（遥远的） invoke（调用）

正如其名字所表示的那样，RMI协议调用远程对象上方法。它使用了序列化方式在客户端和服务器端传递数据。

RMI是一种被EJB使用的更底层的协议。

## Java IDL(接口定义语言)/CORBA

公共对象请求代理结构（Common Object Request Breaker Architecture）

在java IDL的支持下，开发人员可以将Java和CORBA集成在一起。

他们可以创建Java对象并使之可以在CORBA ORB中展开，或者他们还可以创建Java类并做为和其他ORB一起展开的CORBA对象客户。

后一种方法提供了另外一种途径，通过它可以被用于你的新的应用和旧系统相集成。

## JSP(Java Server Pages)

Jsp页面由html代码和嵌入其中的Java新代码所组成。

服务器在页面被客户端所请求以后对这些java代码进行处理，然后将生成的html页面返回给客户端的浏览器。

## Java Servlet

servlet是一种小型的java程序，它扩展了web服务器的功能。作为一种服务器端的应用，当被请求时开始执行，这和CGI Perl脚本很相似。

Servlet提供的功能大多和jsp类似，不过实现方式不同。

JSP通过大多数的html代码中嵌入少量的java代码，而servlet全部由java写成并生成相应的html。

## XML（Extensible Markup Language）

XML是一种可以用来定义其他标记语言的语言。它被用来在不同的商务过程中共享数据。

XML的发展和Java是互相独立的，但是，它和java具有相同目标正是平台独立。

通过java和xml的组合，我们可以得到一个完美的具有平台独立性的解决方案。

## JMS（Java Message Service）

Ms是用于和面向消息的中间件相互通信的应用程序接口（API）。

它既支持点对点的域，有支持发布/订阅类型的域，并且提供对下列类型的支持：经认可的消息传递，事务性消息传递，一致性消息和具有持久性的订阅者的支持。

JMS还提供了另一种方式对您的应用与旧的后台系统相集成。

## JTA（Java Transaction Architecture）

JTA定义了一种标准API，应用系统由此可以访问各种事务监控。

## JTS（Java Transaction Service）

JTS 是 CORBA OTS 事务监控的基本实现。JTS 规定了事务管理器的实现方式。

该事务管理器是在高层支持 Java Transaction API（JTA） 规范，并且在较底层实现 OMG OTS specification 的 java 映像。

JTS 事务管理器为应用服务器、资源管理器、独立的应用以及通信资源管理器提供了事务服务。

## JavaMail

JavaMail是用于存取邮件服务的API，它提供了一套邮件服务器的抽象类。不仅支持SMTP服务器，也支持IMAP服务器。

## JAF（JavaBeans Activation Framework）

JavaMail利用JAF来处理MIME编码的邮件附件。MIME的字节流可以被转换成java对象，或者转换自Java对象。大多数应用都可以不需要直接使用JAF
