# JavaEE复习计划

对于这一块，也只是用到了 Web 开发的相关技术，并且很多技术已经过时或者被民间更好的取代，所以侧重于最基础的 Servlet API、会话机制。

## Servlet API

顶级接口：Servlet、ServletConfig、ServletContext

最常用的类：HttpServlet

### Servlet 接口

广义上说，一个类实现了 Servlet 接口那么就称这个类是 Servlet，也就是说 **该接口中定义了所有 servlet 都必须实现的方法** 。

方法一览：

- init(ServletConfig)
- service(ServletRequest, ServletResponse)
- destroy()
- getServletConfig()
- getServletInfo()

根据官方 API 的解释，简单翻译过来就是这么个意思：

Servlet 是一个运行在服务器端的小的 JAVA 程序，Servlet 接受和响应来自客户端的请求（通常是 HTTP协议）；

为了实现该接口，可以继承 `javax.servlet.GenericServlet` 也可以继承 `javax.servlet.http.HttpServlet`.

该接口定义了初始化、处理请求、删除 Servlet 的方法，这些方法被称作生命周期方法，以如下的顺序调用：

- Servlet 被构造，调用 `init()` 方法初始化
- 所有请求交给 `service` 方法处理
- 处理完毕之后使用 `destroy` 方法销毁资源，等待 GC 回收

除了生命周期方法之外该接口还提供了一个 getServletConfig 方法，Servlet 获得一些启动信息。

### ServletConfig接口

ServletConfig 的对象是由容器构造的。

初始化 Servlet 的时候，该对象传递一些启动的参数给 Servlet （以 init() 方法参数的形式）

可以简单理解：ServletConfig 中封装了 web.xml 中给 Servlet 配置的参数 （ `<init-param>` ）

方法一览：

- getInitParameter(String name)
- getInitParameterNames()
- getServletContext()
- getServletName()

**Servlet 和 ServletConfig 是一一对应的，一个 Servlet 都跟着一个 config 对象**

### ServletContext接口

Servlet 上下文封装了与 Servlet 所在容器通信的一些方法。

对此其实应该很熟悉，是我们常用的四大存储域之一，是范围最大的一块。

ServletContext 与**容器**对应的；一个 ServletContext 可以对应多个 Servlet.

其中定义了很多常用的方法，比如获取绝对路径的 getRealpath，存储数据的 get/set Attrbiute.

获得 ServletContext 的集中方式：

1. ServletConfig.getServletContext();
2. HttpSession.getServletContext();
3. FilterConfig.getServletContext();
4. ServletContextEvent.getServletContext();

PS：既然 ServletConfig 可以拿到，那么它的实现类也都是可以的~

### GenerciServlet抽象类

它实现了之前说的 Servlet 和 ServletConfig 接口，但是毕竟是个抽象类，只有一个方法未实现，所以上面有八个方法都可以使用，此外还有他自己的一些方法。

下面说说重要的几个：

- init(ServletConfig)

  默认的实现可以总结为两句：`{this.config = config;init();}` ，可以看出此类肯定有个 ServletConfig 类型的属性，在 init 中进行保存 config 的引用方便后续方法的使用。

  然后直接又调用了一个空参的 init 方法，这个方法就是 GenerciServlet 自己定义的，默认空实现，如果有自己的初始化逻辑，重写这个空参方法即可，并且不需要再调用 `super.init(config)` 方法。

- abstract service(ServletRequest, ServletResponse)

  是的，唯一的一个抽象方法，唯一的一个未实现的。

  服务器收到请求会调用 service 进行处理，怎么处理也应该由开发者去实现，注意参数为 ServletRequest，一般我们需要强转成 HttpServletRequest ，一般也就是处理 Http 请求吧。

然后还有两个打印日志的方法，这就是全部的方法了。

### HttpServlet类

它继承自 GenericServlet ，并实现了它未实现的 service 方法，从名字可以看出是专门处理 Http 请求的，开发者写的 Servlet 一般也是继承自这个类。

然后它实现的方法做了两件事：

1. 将 ServletRequest/ServletResponse 强转为 HttpServletRequest/HttpServletResponse
2. 调用自己定义的 service(HttpServletRequest, HttpServletResponse) 方法（此方法是 protected 的）

那么，它自己定义的这个 service 方法到底干了什么呢？

简单说就是通过 `request.getMethod()` 获取到请求方式，然后根据这个请求方式来分别调用 doXXX 方法，所以开发者一般都会重写其 doGet 和 doPost。

PS：protected 修饰的方法可以覆盖，但是权限不能变小。

## API 总结

从实现者、调用者、构造者的角度来看就是：

| API名字           | 实现者     | 调用者     | 构造者(new) |
| ----------------- | ---------- | ---------- | ----------- |
| Servlet           | **开发者** | 容器       | 容器        |
| ServletConfig     | 容器       | **开发者** | 容器        |
| ServletContext    | 容器       | **开发者** | 容器        |
| Request           | 容器       | **开发者** | 容器        |
| Response          | 容器       | **开发者** | 容器        |
| RequestDispatcher | 容器       | **开发者** | 容器        |
| Cookie            | 容器       | **开发者** | **开发者**  |
| HttpSession       | 容器       | **开发者** | 容器        |
| Filter            | **开发者** | 容器       | 容器        |
| Listener          | **开发者** | 容器       | 容器        |

## 一个请求的流程

服务器以 Tomcat 为例，那么一个请求的过程大概可以分为下面几步：

1. 服务器启动阶段

   Tomcat 启动时检测自身配置文件 conf 目录，如果配置有问题，则无法启动。

   Tomcat 检查 webapps 下工程的所有的 web.xml 文件，如果该文件有问题，则 tomcat 报错，启动正常（报错的工程无法访问，其他工程可以正常使用）。

2. 浏览器地址栏输入地址，回车

3. 浏览器将地址信息打包成标准的 HTTP 请求字符串

4. 如果输入的是域名，那么进行 DNS 解析，然后通过网络发送到服务器地址

5. 服务器接受到请求并解析，将请求信息打包到 HttpServletRequest/response 对象中

6. 到 `web.xml` 中寻找指定的 `url-pattern` ，根据 servlet-name 找到 Servlet-class

7. 容器检测内存中是否有该 Servlet 的对象，如果有则使用原来的对象，如果没有则使用反射构造 Servlet 对象

8. 【没有找到对象】容器调用 init() --> 方法初始化

9. 容器调用 service(ServletRequest) --> doXx

10. 我们重写 DOXxx 方法 完成业务逻辑

11. 容器将 response 对象转换成标准的 HTTP 响应字符串，再通过网络回送给浏览器

12. 浏览器接受标准的响应信息，解析，显示

当然上面的每一步其实还可以细分，但是这里就先不分的过于细了。