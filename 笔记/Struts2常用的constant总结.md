## 常用Constants

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts PUBLIC
    "-//Apache Software Foundation//DTD Struts Configuration 2.3//EN"
    "http://struts.apache.org/dtds/struts-2.3.dtd">
<struts>

    <!-- 把它设置为开发模式，发布时要设置为false -->
    <constant name="struts.devMode" value="true" />
    <!-- 设置在class被修改时是否热加载，发布时要设置为false -->
    <constant name="struts.convention.classes.reload" value="true"/>
    <!-- 自动动态方法的调用，使用这个设置后可以这样调用：action!method -->
    <constant name="struts.enable.DynamicMethodInvocation" value="true" />
    <!-- 指定jsp文件所在的目录地址 -->
    <constant name="struts.convention.result.path" value="/WEB-INF/content/" />
    <!-- 使用struts-default默认的转换器，如果是rest的使用：rest-default，rest需要rest的jar插件 -->
    <constant name="struts.convention.default.parent.package" value="struts-default"/>
    <!-- 用于配置包名后缀。默认为action、actions、struts-->
    <constant name="struts.convention.package.locators" value="actions" />
    <!-- 用于配置类名后缀，默认为Action，设置后，Struts2只会去找这种后缀名的类做映射 -->
    <constant name="struts.convention.action.suffix" value="Action"/>
    <!-- 设置即使没有@Action注释，依然创建Action映射。默认值是false。因为Convention-Plugin是约定优于配置的风格，
        可以不通过注解根据预先的定义就能访问相应Action中的方法 -->
    <constant name="struts.convention.action.mapAllMatches" value="true"/>
    <!-- 自定义jsp文件命名的分隔符 -->
    <constant name="struts.convention.action.name.separator" value="-" />
    <!-- 国际化资源文件名称 -->
    <constant name="struts.custom.i18n.resources" value="i18n" />
    <!-- 是否自动加载国际化资源文件  -->
    <constant name="struts.i18n.reload" value="true" />
    <!-- 浏览器是否缓存静态内容 -->
    <constant name="struts.serve.static.browserCache" value="false" />
     <!-- 上传文件大小限制设置 -->
    <constant name="struts.multipart.maxSize" value="-1" />
    <!-- 主题，将值设置为simple，即不使用UI模板。这将不会生成额外的html标签 -->
    <constant name="struts.ui.theme" value="simple" />
    <!-- 编码格式 -->
    <constant name="struts.i18n.encoding" value="UTF-8" />

</struts>
```

## 基础Constants

-   **struts.devMode**
    可选值 true,false （默认false），在开发模式下，struts2 的动态重新加载配置和资源文件的功能会默认生效。同时开发模式下也会提供更完善的日志支持。
-   **struts.i18n.reload**
    可选值 true,false（默认值依赖于struts.devMode），是否自动重新加载本地的资源文件。
-   **struts.i18n.encoding** 
    主要用于设置请求编码（默认值（UTF-8）） ，Head 和 Include 标签的解析编码。资源和配置文件的解析编码。
-   **struts.configuration.xml.reload**
    可选值 true,false（默认值依赖于struts.devMode）是否自动重新加载XML配置文件
-   **struts.action.extension**
    设置 struts 的 Action 请求的后缀，支持多个时以逗号隔开。
-   **struts.action.excludePattern**
    设置 struts 所排除的 url（通过正则表达式匹配）（支持多个，以逗号隔开）
-   **struts.tag.altSyntax**
    可选值 true，false（默认true） 是否支持 ognl 表达式
-   **struts.url.http.port**
    设置生成 URL 所对应的 http 端口
-   **struts.url.https.port**
    设置生成 URL 所对应的 https 端口
-   **struts.url.includeParams**
    可选值 none, get, all (默认get)，设置 URL 是否包含参数，以及是否只包含 GET 方式的参数。
-   **struts.locale**
    设置 struts2 默认的 locale，决定使用哪个资源文件。
-   **struts.ui.templateDir**
    该属性指定视图主题所需要模板文件的位置，该属性的默认值是 template，即默认加载 template 路径下的模板文件
-   **struts.ui.theme**
    该属性指定视图标签默认的视图主题，该属性的默认值是 xhtml。
-   **struts.ui.templateSuffix**
    该属性指定模板文件的后缀，该属性的默认属性值是 ftl。该属性还允许使用 ftl、vm 或 jsp，分别对应 FreeMarker、Velocity 和 JSP 模板
-   **struts.multipart.saveDir**
    设置上传临时文件的默认目录
-   **struts.multipart.maxSize**
    设置上传的临时文件的最大限制
-   **struts.objectFactory.spring.autoWire**
    可选值（name, type, auto, constructor,name）（默认name），设置 spring 的自动装配方式，只有引入spring 插件后才有效。
-   **struts.objectFactory.spring.autoWire.alwaysRespect** 
    （默认false）设置是否总是以自动装配策略创建对象。
-   **struts.objectFactory.spring.useClassCache** 
    （默认false）对象工厂是否使用类缓存，开发模式无效。
-   **struts.xslt.nocache** 
    （默认为false）设置XsltResult是否不是用缓存。
-   **struts.custom.properties** 
    设置用户的自定义属性文件名列表（用，隔开）
-   **struts.custom.i18n.resources** 
    设置用户自定义的资源文件路径列表（用，隔开）
-   **struts.serve.static** 
    （默认false） 设置是否支持静态资源请求（要求url在struts或static下）
-   **struts.serve.static.browserCache** 
    （默认false） 是否在静态资源响应中设置缓存。只有在支持静态资源时有效。
-   **struts.el.throwExceptionOnFailure** 
    （默认false）是否在解析el表达式或无法找到属性时抛出RuntimeException
-   **struts.ognl.logMissingProperties** 
    （默认false）是否日志无发找到的属性
-   **struts.ognl.enableExpressionCache** 
    是否缓存ognl解析的表达式。
-   **struts.enable.DynamicMethodInvocation** 
    （默认false）是否支持动态的方法调用,在URL上通过!method指定方法。
-   **struts.enable.SlashesInActionNames** 
    在URL中的Action段中是否支持斜线
-   **struts.mapper.alwaysSelectFullNamespace** 
    （默认false） 是否总是用最后一个斜线前的URL段作为namespace

## 核心对象Constants

-   **struts.actionProxyFactory** 
    设置 ActionProxy 的实体工厂，该工厂同时也生成默认的 ActionInvoctation
-   **struts.xworkConverter** 
    设置 XWorkConverter 对象，该对象用于获取各种类型的转换器。
-   **struts.unknownHandlerManager** 
    设置 UnknownHandlerManager 的实现类，用于处理无法找到方法等异常。
-   **struts.multipart.handler** 
    设置 mutipartRequest 的 handler （默认是 jakarta）对应类：
    `org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest`
-   **struts.mapper.class** 
    可选值（struts，composite，restful，restful2）设置 URL 解析且映射到 ACTION 的实现，（默认struts）
-   **struts.mapper.prefixMapping** 
    通过 URL 前缀映射到对应的 Mapper，格式为 `urlPrefix1:mapperName2, urlPrefix2:mapperName2`。
    必须添加 mapperClass 为 `org.apache.struts2.dispatcher.mapper.PrefixBasedActionMapper`，并指定 `struts.mapper.class` 为该 mapper。
-   **struts.mapper.composite** 
    设置是否支持复合（多个）actionMapper，mapperName 用逗号隔开。必须配置 `struts.mapper.class` 为 composite 才会生效
-   **struts.mapper.idParameterName** 
    用于 Restful2ActionMapper 作为 URL 中 id 所对应的 parameterName
-   **struts.ognl.allowStaticMethodAccess** 
    （默认false）设置 ognl 表达式是否支持静态方法。
-   **struts.configuration** 
    设置 struts2 的 Settings 类。（2.1.2后不再使用）
-   **struts.urlRenderer** 
    设置 struts2 的 URL render（用于生成的URL），（默认struts），类名
    `org.apache.struts2.components.ServletUrlRenderer`
-   **struts.objectFactory** 
    设置 struts2 的对象工厂，默认（struts），类名 `org.apache.struts2.impl.StrutsObjectFactory`，当引入 struts2-spring 插件之后，则被修改为 `org.apache.struts2.spring.StrutsSpringObjectFactory`
-   **struts.xworkTextProvider** 
    设置 struts2 的资源文件内容提供类的实现。默认为 `com.opensymphony.xwork2.TextProviderSupport`
-   **struts.actionValidatorManager** 
    设置 ActionValidatorManager  的实现类。
-   **struts.valueStackFactory** 
    设置 struts2 的 ValueStack 工厂的实现。
-   **struts.reflectionProvider** 
    设置 ReflectionProvider 的实现类
-   **struts.reflectionContextFactory** 
    设置 ReflectionContextFactory 的实现类
-   **struts.patternMatcher** 
    设置 PatternMatcher 的实现类
-   **struts.staticContentLoader** 
    设置 StaticContentLoader 的实现类

## 参考

http://www.cnblogs.com/HD/p/3653930.html