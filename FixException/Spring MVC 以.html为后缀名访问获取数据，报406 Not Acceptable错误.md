> 原文：http://www.cnblogs.com/yuanfy008/p/7221304.html

如题，最近以 spring mvc 作为后台框架，前端异步获取数据时（.html 为后缀名的访问方式），报 406 Not Acceptable 错误。

当初都不知道啥原因，前后台都没报错就是返回不了数据，于是查了下 http 406 响应码：

**406 (SC_NOT_ACCEPTABLE)表示请求资源的MIME类型与客户端中Accept头信息中指定的类型不一致。**

下面请看出错的操作流程及代码：

1、先配置spring mvc 核心servlet （DispatcherServlet） 至web.xml中，其中配置可以以.html和.do为后缀名的请求。

```xml
<servlet>
  <servlet-name>DispatcherServlet</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:dispatcher-servlet.xml</param-value>
  </init-param>
</servlet>
<servlet-mapping>
  <servlet-name>DispatcherServlet</servlet-name>
  <url-pattern>*.html</url-pattern>
</servlet-mapping>
<servlet-mapping>
  <servlet-name>DispatcherServlet</servlet-name>
  <url-pattern>*.do</url-pattern>
</servlet-mapping>
```

2、controller部分代码如下：

```java
@RequestMapping(value="chat/startClient")
@ResponseBody
public AjaxResult startClient(UserEntity user,HttpServletRequest request) {
  AjaxResult result = new AjaxResult(1);
  if (user.getUserId() == null){
    user.setUserId(System.currentTimeMillis());
    SessionUtil.setAttr(request, SessionUtil.SESSION_USER, user);
  } else {
    UserEntity sessionUser = SessionUtil.getUser(request);
    if (sessionUser.getUserId().equals(user.getUserId())) {
      user = sessionUser; 
    }
  }
  if (Client.startClient(user)) {
    result.setData(user);
  }
  return result;
}
```

针对上面情况在网上折腾了一会，终于找到了破解之法，该问题的主要原因：

Spring MVC有点不一样，如果你没有配置什么样的请求方式对应什么样的响应方式的话，它会根据 url 的后缀名对应不同响应头的格式。

在 协商资源表述 的方式下，默认优先根据后缀决定，如果决定不了再参考 Accept 头信息。

---

解决方法：所以我们要针对此情况进行配置，更改 html 对应返回的类型。（如果想要使用 @ResponseBody 返回json格式，就需要相关 jar 在类路径下） 一般我们是配置在 mvc 配置文件中需要配置 `<mvc:annotation-driven />`, 所以我们只要修改下这里就行，修改配置代码如下：

```xml
<mvc:annotation-driven content-negotiation-manager="contentNegotiationManager" />
<!-- 以.html为后缀名访问，默认返回数据类型是 text/html， 所以要修改返回的数据类型 -->
<bean id="contentNegotiationManager" class="org.springframework.web.accept.ContentNegotiationManagerFactoryBean"> 
  <property name="mediaTypes">  
    <map>  
      <entry key="html" value="application/json;charset=UTF-8"/> 
    </map>  
  </property> 
</bean>
```

ContentNegotiationManagerFactoryBean 是内容协商管理工厂bean对象，主要用来配置多视图请求格式。 