> https://blog.csdn.net/qq_32588349/article/details/52097943

一般我们在 Spring 的配置文件 `applicationContext.xml` 中对 Service 层代码配置事务管理，可以对 Service 的方法进行 AOP 增强或事务处理如事务回滚；

但是遇到一个问题，在 Controller 类中调用 Service 层方法，配置的事务管理会失效，查询相关资料发现原因。

其实 Spring 和 SpringMVC 两个容器为父子关系，Spring 为父容器，而 SpringMVC 为子容器。

也就是说 `applicationContext.xml` 中应该负责扫描除 @Controller 的注解如 @Service，**而 SpringMVC 的配置文件应该只负责扫描 @Controller**，否则会产生重复扫描导致 Spring 容器中配置的事务失效。 

当然如果使用的是 XML 配置 AOP 的方式来管理事务，那么应该是没有影响的。

## 解决方案 1

按照上面的规则，其实问题主要在于 SpringMVC 的配置文件扫包范围，Spring 的配置文件就算也扫了 @Controller  注解，事务管理的 Service 只要没被（SpringMVC）重新扫描就不会出现事务失效问题。 

也就是说只要保证在 SpringMVC 的配置文件中只扫 controller 的包就可以了，如果分布在多个包结构下，可以使用逗号来配置多个。

## 解决方案 2

定义 SpringMVC 只扫描 @Controller 注解：

``` xml
<context:component-scan base-package="com.nn.web.controller"  
                        use-default-filters="false">  
  <context:include-filter type="annotation"  
                          expression="org.springframework.stereotype.Controller" />  
</context:component-scan>  
```

这样其他注解就不会扫描了。

## 补充

在 `context:component-scan` 可以添加 **use-default-filters**，spring 配置中的 use-default-filters 用来指示**是否自动扫描**带有 @Component、@Repository、@Service 和 @Controller 的类。默认为 true，即默认扫描。 

如果想要过滤其中这四个注解中的一个（不扫描），比如 @Repository，可以添加 `<context:exclude-filter />` 子标签：

``` xml
<context:component-scan base-package="com.loli" scoped-proxy="targetClass" use-default-filters="true">  
  <context:exclude-filter type="annotation" expression="org.springframework.stereotype.Repository"/>  
</context:component-scan> 
```

 上面的 `<context:include-filter/>` 子标签是用来添加扫描注解的。

但是如果添加和排除的是相同，**则必须 include-filter 在前，exclude-filter 在后** 

> 引用：https://blog.csdn.net/u014520797/article/details/50667720

---

scoped-proxy 属性是指定代理，接受三个值：no（默认值），interfaces（接口代理），targetClass（类代理） 

那什么时候需要用到 scope 代理呢，举个例子：

我们知道 Bean 的作用域 scope 有：singleton，prototype，request，session；

那有这么一种情况，当你把一个 session 或者 request 的 Bean 注入到 singleton 的 Bean 中时，因为 singleton 的 Bean 在容器启动时就会创建（假设为 A），而 session 的 Bean 在用户访问时才会创建（假设为 B），那么当 A 中要注入 B 时，有可能 B 还未创建，这个时候就会出问题；

那么用代理的时候就来了，B 如果是个接口，就用 interfaces 代理，是个类则用 targetClass 代理（这就是默认值 no 的情况下） 