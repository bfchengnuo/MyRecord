# Struts2中jar包的作用

可以分为核心包和辅助包，一般的小项目用核心包就够了，虽然全加上并不会有问题，但是为了精简还是按需加入比较好

## 核心包

这些包是必须要加入的，注意有些包是有依赖性的

-   struts2-core-2.1.8.1

    struts2 的核心 jar 包，不可缺少的

-   xwork-core-2.1.6

    xwork 的核心包，由于 Struts2 是由 xwork 的延伸 有些类依然关联着 xwork 的类

-   aopalliance-1.0

    这个包为 AOP 提供了最普通和通用的接口

-   commons-fileupload-1.2.1.jar  ,  commons-io.jar

    struts 的上传下载 两者缺一不可

-   commons-lang-3-3.1.jar

    为 java.lang 包提供扩展

-   commons-logging-1.0.4

    Jakarta 的通用日志记录包

-   freemarker-2.3.15

    FreeMarker是一个模板引擎，一个基于模板生成文本输出的通用工具

-   ognl-2.7.3

    是支持 ognl 表达式

## 拓展包

选择性加入，不要太在意版本

-   struts2-spring-plugin

    struts2 和 spring 整合需要的包


-   antlr-2.7.2

    一种工具，它可以接受词文法语言描述，并能产生识别这些语言的语句的程序

-   asm-3.3

    操作 java 字节码的类库

-   asm-commons-3.3

    提供了基于事件的表现形式

-   asm-tree-3.3

    提供了基于对象的表现形式

-   classworlds-1.1

    基于 java 操作类装载的开发框架。java 的 classloader 的机制和本地类可以引起头痛，多为某些类型的应用程序开发的混乱

-   commons-beanutils-1.8.0

    jakarta commons 项目中的一个子项目。这个项目开发的目的是帮助开发者动态的获取/设值 JavaBean 的属性，同时解决每次都要写 getXXX 和 setXXX 的麻烦

-   commons-chain-1.2

    Apache  的 Commons-Chain 项目已将命令模式 (Command) 和责任链 (Chain of Responsebility) 模式两者完美的结合

-   commons-collections-3.1

    包含了一些 Apache 开发的集合类，扩展了标准的 Java Collection 框架，提供了额外的 Map、List 和 Set 实现以及多个有用的工具类库。功能比 java.util.* 强大。

-   commons-digester-2.0

    Jakarta Struts 中的一个工具，用于处理 struts-config.xml 配置文件

-   commons-logging-api-1.1

    Apache Commons 包中的一个，包含了一些数据类型工具类，是 java.lang.* 的扩展。

-   commons-validator-1.3.1

    校验方法)和校验规则。支持校验规则的和错误消息的国际化。 struts 使用它对表单进行验证

-   dwr-1.1.1

    Direct Web Remoting 是一个 WEB 远程调用框架. Java 开发利用这个框架可以让 AJAX 开发变得很简单.

-   ezmorph-1.0.6

    EZMorph 是一个简单的 java 类库用于将一种对象转换成另外一种对象。
    EZMorph 原先是 Json-lib 项目中的转换器。EZMorph 支持原始数据类型（Primitive），对象（Object），多维护数组转换与 DynaBeans 的转换。struts2中，json 的处理便使用了 EZMorph 库

-   google-collections-1.0

    对现有 Java 集合类的一个扩展。

-   jackson-core-asl-1.9.2

    一个高性能的解析器的核心库

-   json-lib-2.3-jdk15

    提供了强大的 JSON 支持，利用 Ajax 提交上来的 JSON 字符串进行解析，可以转化为 POJO 对象，可以从 POJO 转化为 js 可以识别的 JSON 对象

-   juli-6.0.18

    用于 tomcat 错误日志查看

-   oro-2.0.8

    RO 一套文本处理工具，能提供 perl5.0 兼容的正则表达式， AWK-like 正则表达式， glob 表达式。还提供替换,分割,文件名过虑等功能

-   oval-1.31

    OVal 是一个提供事务和对象的可扩展验证框架的任何类型的 Java 对象。

-   plexus-container-default-1.0-alpha-10

    Plexus 项目提供完整的软件栈，用于创建和执行软件项目。根据丛容器，应用程序可以利用面向组件编程构建模块化，它可以轻易地组装和重用可重用组件。
    根据 Plexus容器，应用程序可以利用面向组件编程构建模块化，它可以轻易地组装和重用可重用组件。

-   plexus-utils-1.2

    Plexus 项目提供完整的软件栈，用于创建和执行软件项目。根据丛容器，应用程序可以利用面向组件编程构建模块化，它可以轻易地组装和重用可重用组件。

-   sitemesh-2.4.2

    SiteMesh 是一个用来在 JSP 中实现页面布局和装饰（layout and decoration）的框架组件，能够帮助网站开发人员较容易实现页面中动态内容和静态装饰外观的分离。

-   struts2-codebehind-plugin-2.3.4

    通常 JSP 页面来自于文件系统。利用这个插件，你可以将jsp页面部署到jar包中

-   struts2-config-browser-plugin-2.3.4

    struts 配置浏览器所需要的插件

-   struts2-convention-plugin-2.3.4

    在默认情况下该公约插件查找操作类在以下软件包支柱,struts2的行为或行动,任何包相匹配这些名称将被考虑作为根包为常规插件。

-   struts2-dojo-plugin-2.3.4

    为 struts 所提供的一些控件例如：日历

-   struts2-dwr-plugin-2.3.4.

    用于整合DWR

-   struts2-embeddedjsp-plugin-2.3.4

    用于将 jsp 页面放在 jar 包中

-   struts2-jasperreports-plugin-2.3.4

    用于整合 JasperReports

-   struts2-javatemplates-plugin-2.3.4

    Apache 提供的‘javatemplates‘用于代替默认的Freemarker渲染器

-   struts2-jfreechart-plugin-2.3.4

    struts2 使用 jfreechart 的插件包

-   struts2-jsf-plugin-2.3.4

    sturts 整合 jsf 的插件包

-   struts2-json-plugin-2.3.4

    struts2 所用到的json插件包

-   struts2-junit-plugin-2.3.4

    struts 所提供的junit调试

-   struts2-osgi-plugin-2.3.4

    这个插件提供了支持启动一个实例的Apache Felix在一个web应用程序,和扫描安装的bundle的Struts配置。还提供了一个管理包

-   struts2-oval-plugin-2.3.4

    插件定义了拦截器”ovalValidation”和拦截器堆栈”ovalValidationStack”在“oval-default”包。使用这个拦截器,扩大“oval-default””包

-   struts2-plexus-plugin-2.3.4

    使用该插件,当配置Struts动作,拦截器,在Struts或结果。xml,设置class属性包含丛对象id,而不是实际的Java类。这将允许丛来创建对象和注入任何依赖关系也由管理丛。

-   struts2-portlet-plugin-2.3.4

    Portlet 的插件,用于发展中 JSR286 Portlet 使用 Struts

-   struts2-rest-plugin-2.3.4

    rest 插件用于自动处理序列化,并反序列化每种格式。

-   struts2-sitegraph-plugin-2.3.4

    生成一个web应用程序的图形视图

-   struts2-struts1-plugin-2.3.4

    这个jar包是用于将 strusts 和 spring 进行整合的一个插件，在处理**数据库**的事物时，通过这个插件将数据源配置到底层的sessionFactory中，然后再将sessionFactory注入到相应Dao层或者service层，在配置请求页面的处理结果页面配置struts.xml文件由spring进行管理的
    出过大漏洞...（貌似是用在 Struts2 整合 Struts 1 中的）

-   struts2-testng-plugin-2.3.4

    这个插件是用来在单元测试,而不是在运行时。因此,它包含在您的构建的类路径中,但不要将它部署 WEB-INF/lib 在 Struts2 的应用程序

-   struts2-tiles-plugin-2.3.4

    这个插件可以安装插件 jar 复制到应用程序的 WEB-INF/lib 目录中

-   testng-5.1-jdk15

    TestNG 是一个测试框架从 JUnit 和 NUnit 启发,但该框架引入了一些新功能,使它更强大,也更容易使用。而该jar包就是用于整合使用该框架。

-   tiles-api-2.0.6

    提供对tiles的支持：类和标记库在一个JSP环境中使用tiles。

-   tiles-core-2.0.6

    tiles核心库,包括基本的实现的api。

-   tiles-jsp-2.0.6

    提供对tilesJSP的支持:类和标记库在一个JSP环境使用tiles。

-   velocity-1.6.3

    Java模板技术-velocity

-   xmlpull-1.1.3.1

    支持可扩展的XML

-   xpp3_min-1.1.4c

    Java对象和XML之间相互转换所需JAR包

-   xstream-1.4.2

    xstream 提供对象和xml之间的转换