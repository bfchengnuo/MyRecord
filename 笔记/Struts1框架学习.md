# Struts1框架学习

Struts 是 Apache 软件基金会（ASF）赞助的一个开源项目。它最初是 jakarta 项目中的一个子项目，并在2004年3月成为 ASF 的顶级项目。它通过采用 JavaServlet/JSP 技术，实现了基于 JavaEEWeb 应用的 MVC 设计模式的应用框架，是 MVC 经典设计模式中的一个经典产品。
~~ASF 不是一个挂卡工具么...~~

Struts 的讲解网上有很多文章，我学习也是靠这些，这里仅仅记录作为初学者学到的皮毛吧，以便日后翻阅

> 作为控制器的 Servlet。
> 提供大量的标签库。
> 对表单的处理
> 提供了用于国际化的框架，利用不同的配置文件，可以帮助用户选择合适自己的语言。
> 提供了 JDBC 的实现，来定义数据源和数据库连接池。
> XML语法分析工具。
> 文件处理机制。

总之，它是基于 MVC 开发的，工作在 Web 层的框架，也就是为了简化 Web 层的开发；MVC 是一种设计模式，它可以使用在三层架构中的 web 层

## 工作模式

简单可以理解为，当浏览器请求服务器时，请求交给 Struts 的 ActionServlet 处理，可能会对请求的一些对象进行增强，然后根据事先设置的配置文件交给指定的 Action 处理，处理完后返回给 ActionServlet ，然后可能就是调用相应的 JSP 页面进行显示了，如下图所示：
![](https://github.com/bfchengnuo/MyRecord/blob/master/img%2Fstruts1.jpg)

## 前

现在来说基本都已经过渡到 Struts2 了，但是特别特别老的项目还是一代，反正我看的教程是提到了一代，老方讲的辣么好，多听听也没坏处
所以这个就不发博客了，留在这存着吧....
完整的示例代码见：[Github](https://github.com/bfchengnuo/java_learn/tree/master/XC/Struts1%E7%A4%BA%E4%BE%8B%E4%BB%A3%E7%A0%81)

## 基本使用

按照上面所说，期望把请求交给 ActionServlet 进行处理，从它的名字也可以看出，它本质就是个 Servlet ，要想处理请求就必须在 web.xml 中进行相应的配置，这个映射就比较熟了

```xml
<servlet>
  <servlet-name>ActionServlet</servlet-name>
  <servlet-class>org.apache.struts.action.ActionServlet</servlet-class>
  
   <init-param>
    <param-name>config</param-name>
    <param-value>/WEB-INF/struts-config.xml</param-value>
  </init-param>
  <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
  <servlet-name>ActionServlet</servlet-name>
  <url-pattern>*.do</url-pattern>
</servlet-mapping>
```

pattern 一般有个不成文的规定都是 `*.do` 也就是处理所有 do 结尾的请求，比如 form 提交表单的地址写 `xxxx.do` 这样就会自动识别

struts 的正常工作依赖于它的配置文件，所以在配置 servlet 的时候记得加个配置文件，并且因为它处理很多请求，所以一般设置个 load-on-startup 让他随服务器的启动加载

### struts-config的配置

根据上面所说，所有的请求最终其实是由 Action 来进行处理的，那么那个请求让那个 Action 进行处理总要指明吧，这个就配置在 struts-config 文件中，这个文件在 WEB-INF 目录下：

```xml
<struts-config>  
    <form-beans>  
       <form-bean name="loginForm" type="com.bjpowernode.struts.LoginActionForm"/>  
    </form-beans>  
     
    <action-mappings>  
       <action path="/login"  
              type="com.bjpowernode.struts.LoginAction"  
              name="loginForm"  
              scope="request"  
              >  
              <forward name="success" path="/login_success.jsp"/>  
              <forward name="error" path="/login_error.jsp"/>  
       </action>  
    </action-mappings>    
</struts-config> 
```

按照上面的慢慢来说，首先 action 标签是核心，它对应一个 Action，再说它的属性

-   path：
    用来指定处理那个请求，上面的栗子就是处理 login.do 这个请求啦，注意：**不需要 .do 结尾**
-   type：
    和上面的属性是一对，用来指定具体的 java 文件路径，**不过这个类一定要继承自 Action ！！！并且复写 execute 方法** ；因为 ActionServlet 其实就是调用的这个方法
-   name：
    指定了请求携带数据所对应的 javabean ，主要用于表单的提交；和上面的 form-beans 标签对应起来，也是要指定具体的 bean 的路径，和 action 类似，**一定要继承 ActionForm ！！！**
-   scope：
    指定 name 中的 formbean 存放在那个域，一般有两个可选域，request 和 session ，默认是 session
-   input：
    这个属性也是很常用的，通常 formbean 是需要进行校验的，还有一个属性叫 **validate** 来控制是否进行校验，默认是 true
    而 input 这个属性就是当校验失败时，返回那个页面，也可以说是 formbean 的数据是由那个页面提供的
-   forward
    这个属性一旦配置了 type 就没效果了，因为直接转发了嘛

其他的就不说了，毕竟已经用的很少了

### Action

下面再来说说 Action ，这个也很重要啊，按照上面所说，需要继承 Action ，然后在 execute 里进行逻辑处理

```java
public class LoginAction extends Action {
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// form 就是配置的 name 中的相应的 javabean
		LoginActionForm laf = (LoginActionForm)form;
		String username = laf.getUsername();
		String password = laf.getPassword();

        // mapping 封装了所有的在 action 标签中配置的信息
		return mapping.findForward("error");
	}
}
```

### ActionForm

是的，这个就是个 javabean ，只是需要继承 ActionForm，并且一般是需要复写 **validate** 方法的，这个方法是用来校验数据的，当然可以使用 **struts-Validate** 框架去校验，简便开发

最后返回的是一个错误类，ActionServlet 会进行检测，如果是空就就代表通过，如果有信息就代表校验失败，就会跳到 input 设置的界面上，相关的错误信息会存在于 request 域，表单提交的数据也会回传回去便于数据的回显，key 就是 action 中配置的 name 的值，当然完全可以使用 param 取；在 JSP 中可以通过相应的标签进行获取显示

关于文件上传，在 bean 里直接定义为 FormFile 就行了，通过这个对象可以直接获取文件名和流，只要别忘记关流就行！！！

注意：错误信息默认都是根据设置的 key 从资源文件中进行获取的，也就是那个国际化资源文件；并且在资源文件中是可以使用占位符的，这样就可以很好的进行复用了

---

再说下，为了一个简单的表单去创建一个 bean 感觉不太值啊，所以就有了动态 formbean 这一说，使用的是 DynaActionForm，使用它不需要写 java 文件，在 xml 里配配 bean 就出来了

```xml
<form-bean name="register2Form" type="org.apache.struts.validator.DynaValidatorForm">
	<form-property name="username" type="java.lang.String"></form-property>
	<form-property name="password" type="java.lang.String"></form-property>
	<form-property name="email" type="java.lang.String"></form-property>
</form-bean>
```

获取值的时候也非常简单，就是根据 key 获取 val 嘛

```java
@Override
public ActionForward execute(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {
	DynaActionForm dynaform = (DynaActionForm) form;
	System.out.println(dynaform.getString("username"));
	System.out.println(dynaform.getString("password"));
	System.out.println(dynaform.getString("email"));
	return null;
}
```

### DispatchAction

这个和 Action 有啥关系呢，当然是父子关系，23333 ，这个是为了多个请求对应一个 action 的，一种请求对应一个方法，这样就不是只用 execute 了

方法名和参数是一致的，并且这些方法和 action 的签名是一致的，**也就是说除了名字不一样其他的都是一样的**，包括参数的顺序
最后就是要在 action 中配置个parameter ,这个属性就是你 url 参数的 key，val 就对应方法名了，除了这样配置还有一种方式，那就是使用 **MappingDispatchAction** 它是 DispatchAction 的儿子，这样可以直接根据请求的 url 指定相应的方法

```xml
<action path="/bookAction" type="cn.itcast.web.action.BookAction" parameter="method"/>
<!-- 第二种方式 -->
<action path="/addbook" type="cn.itcast.web.action.BookAction2" parameter="add"/>
<action path="/updatebook" type="cn.itcast.web.action.BookAction2" parameter="update"/>
<action path="/findbook" type="cn.itcast.web.action.BookAction2" parameter="find"/>
<action path="/deletebook" type="cn.itcast.web.action.BookAction2" parameter="delete"/>
```

他们对应的 Action 没啥好说的，就是这几个方法一样的，只不过是 `BookAction extends DispatchAction` ；而 `BookAction2 extends MappingDispatchAction`

## 相关标签库

标签库的目的当然是为了简化我们的开发，比如在表单的校验方面就大大的简化了；用之前记得导入相应的标签库：`<%@taglib prefix="html" uri="http://struts.apache.org/tags-html" %>`
常用的一些标签有：

-   link
    这个最爽的是不用再写获取当前应用名的那一坨了，也不需要 .do 了，直接写就行；不过这个标签也很反人类，id 其实是所谓的 name 值，paramName 确实根据其值作为 key 去四个域中查找相应的值

    ```jsp
    <%
    	request.setAttribute("data","aaa");
    %>
    <html:link action="/registerUI" paramId="name" paramName="data">注册</html:link>
    ```

-   errors
    主要是用于处理表单校验失败后回显的(其实就是从 request 域取出数据) :
    `<html:password property="password"/> <html:errors property="password"/>`

-   form
    这个是重头戏，大大方便了表单的书写，为了避免表单的重复提交，可以在处理表单时使用 **saveToken** 方法生成一个令牌，然后再进行转发到 JSP，会存在 session 里，可以前很类似，使用 form 时会自动添加相应的隐藏域
    处理表单的时候可以调用 **isTokenValid** 来检测令牌是否有效；最后还不要忘了使用 **resetToken** 释放令牌（删除）

    ```jsp
    <html:form action="/register" enctype="multipart/form-data">
    	用户名：<html:text property="username"/><html:errors property="username"/><br/>
    	密码：<html:password property="password"/><html:errors property="password"/><br/>
    	电子邮箱：<html:text property="email"/><br/>
    	大头照：<input type="file" name="file"><br/>
    	<input type="submit" value="注册"><br/>
    </html:form>
    ```

## struts-Validate

这个框架可以解释为是 struts 中的一个框架，但是默认并没有启用，需要手动再进行配置一下，这个框架的作用就是用来校验表单的，避免再写那烦人的校验代码

```xml
<plug-in className="org.apache.struts.validator.ValidatorPlugIn">
  <set-property property="pathnames" 
                value="/org/apache/struts/validator/validator-rules.xml,
                       /WEB-INF/validation.xml"/>
</plug-in>
```

它依赖于两个配置文件：**validator_rule.xml** 和 **validation.xml** ；表示用那个校验器和如何进行校验
validator_rule.xml 自带的 jar 中已经配置好了，一般无需配置，要做的就是配 struts 的配置文件，把它开启，相关的配置可以去 struts 的相关 jar 包去找找看，validation.xml 当然也是放在 WEB-INF 目录下

```xml
<form-validation>
	<formset>
		<form name="register2Form">
            <!-- 这些校验器可以从 jar 包里面的配置文件找到详细说明 -->
			<field property="username" depends="required,minlength,maxlength">
                <!-- 替换国际化资源文件里相应的占位符，false 指的是用当前key替换，不去查资源文件 -->
				<arg key="用户名" resource="false"/>
				<arg name="minlength" position="1" key="${var:minlength}" resource="false"/>
				<arg name="maxlength" position="1" key="${var:maxlength}" resource="false"/>
				
				<var>
					<var-name>minlength</var-name>
					<var-value>3</var-value>
				</var>
				<var>
					<var-name>maxlength</var-name>
					<var-value>6</var-value>
				</var>
			</field>
			
			<field property="password" depends="mask">
				<arg key="密码" resource="false"/>
				<var>
					<var-name>mask</var-name>
					<var-value>^\d{3,6}$</var-value>
				</var>
			</field>
			
			<field property="email" depends="email">
				<arg key="邮箱" resource="false"/>
			</field>
		</form>
	</formset>
</form-validation>
```

它的使用也需要资源文件，错误信息同样会放在国际化资源文件中，所以实现在资源文件中加好，可以从 jar 包里面抄

## 其他

关于资源文件，就是那个国际化的，需要在 struts 的配置文件里进行指定：`<message-resources parameter="MessageResource"></message-resources>` 这个就是放在根目录下的

关于中文乱码，struts 并没解决，因为它是基于 servlet 的所以....还是自己写个过滤器进行处理乱码问题吧