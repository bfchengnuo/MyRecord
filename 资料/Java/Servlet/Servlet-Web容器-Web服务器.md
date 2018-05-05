## Servlet

是一种独立于平台和协议的服务器端的Java应用程序，可以生成动态的Web页面。 

它担当Web浏览器或其他HTTP客户程序发出请求，与HTTP服务器上的数据库或应用程序之间的中间层。

Servlet是位于Web 服务器内部的服务器端的Java应用程序，与传统的从命令行启动的Java应用程序不同，Servlet由Web服务器进行加载，该Web服务器必须包含支持Servlet的Java虚拟机。

### servlet由来

servlet是在服务器上运行的小程序。这个词是在Java applet的环境中创造的，Java applet 是一种当作单独文件跟网页一起发送的小程序，它通常用于在客户端运行，结果得到为用户进行运算或者根据用户互作用定位图形等服务。

服务器上需要一些程序，常常是根据用户输入访问数据库的程序。这些通常是使用公共网关接口（CGI）应用程序完成的。然而，在服务器上运行Java，这种程序可使用Java编程语言实现。在通信量大的服务器上，Java servlet的优点在于它们的执行速度更快于CGI程序。各个用户请求被激活成单个程序中的一个线程，而创建单独的程序，这意味着各个请求的系统开销比较小。


## WEB服务器

也称为WWW(WORLD WIDE WEB)服务器，主要功能是提供网上信息浏览服务。

1. 应用层使用HTTP协议。
2. HTML文档格式。
3. 浏览器统一资源定位器(URL)。

使用较多的 web server 服务器软件有：微软的信息服务器（IIS）、Apache、Nginx。

通俗的讲，Web服务器传送(serves)页面使浏览器可以浏览，然而应用程序服务器提供的是客户端应用程序可以调用(call)的方法(methods)。

确切一点，你可以说：Web服务器专门处理HTTP请求(request)，但是应用程序服务器是通过很多协议来为应用程序提供(serves)商业逻辑(business logic)。

Web 服务器可以解析(handles)HTTP协议。当Web服务器接收到一个HTTP请求(request)，会返回一个HTTP响应(response)，例如送回一个HTML页面。为了处理一个请求(request)，Web服务器可以响应(response)一个静态页面或图片，进行页面跳转(redirect)，或者把动态响应(dynamic response)的产生委托(delegate)给一些其它的程序例如CGI脚本，JSP(JavaServer Pages)脚本，servlets，ASP(Active Server Pages)脚本，服务器端(server-side)JavaScript，或者一些其它的服务器端(server-side)技术。无论它们(译者注：脚本)的目的如何，这些服务器端(server-side)的程序通常产生一个HTML的响应(response)来让浏览器可以浏览。

要知道，Web 服务器的代理模型(delegation model)非常简单。当一个请求(request)被送到Web服务器里来时，它只单纯的把请求(request)传递给可以很好的处理请求(request)的程序(译者注：服务器端脚本)。Web服务器仅仅提供一个可以执行服务器端(server-side)程序和返回(程序所产生的)响应(response)的环境，而不会超出职能范围。服务器端(server-side)程序通常具有事务处理(transaction processing)，数据库连接(database connectivity)和消息(messaging)等功能。

虽然Web服务器不支持事务处理或数据库连接池，但它可以配置(employ)各种策略(strategies)来实现容错性(fault tolerance)和可扩展性(scalability)，例如负载平衡(load balancing)，缓冲(caching)。集群特征(clustering―features)经常被误认为仅仅是应用程序服务器专有的特征。


## web容器

~~或者又叫 Web应用服务器？~~

容器：充当中间件的角色(所谓容器就是指符合一定的规范能提供一系列服务的管理器，方便别人使用它来完成一系列的功能)

WEB容器：给处于其中的应用程序组件（JSP，SERVLET）提供一个环境，使JSP、SERVLET直接更容器中的环境变量接口交互，不必关注其它系统问题。主要有WEB服务器来实现。例如：TOMCAT,WEBLOGIC,WEBSPHERE等。

该容器提供的接口严格遵守J2EE规范中的WEB APPLICATION 标准。我们把遵守以上标准的WEB服务器就叫做J2EE中的WEB容器。

例如tomcat，使用tomcat可以为我们提供servlet、jsp等服务，我们俗称叫servlet服务器，在服务器中会有相关的容器，servlet容器可以调用servlet和jsp动态的为我们生成html

对于刚刚接触的人来说，可以把服务器就理解成一个容器也可以，不过两者的确不是一回事，是服务器为我们提供一个容器使我们的程序能够在容器里运行，使用服务器提供的一系列功能