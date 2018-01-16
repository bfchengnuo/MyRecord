# Java模板引擎之Freemarker

FreeMarker 是一个**基于 Java 的模板引擎**，最初专注于使用 MVC 软件架构生成动态网页。但是，它是一个通用的模板引擎，不依赖于 servlets 或 HTTP 或 HTML，因此它通常用于生成源代码，配置文件或电子邮件。FreeMarker 是自由软件。

FreeMarker 不属于 Web 层框架，最多只算是 Web 层中的 View 层的组件，用于解析服务端的数据展示再视图层。

来简单说下它的原理：

数据模型 + 模板 = 输出（HTML）

也就是说 FreeMarker 会将 Java Object（数据）填充到事先准备好的 TemplateFile 中，然后最终渲染出展示用的 HTML；在 TemplateFile 中通常会定义一些标志（比如 `${name}` 之类的）用于把 Java 对象中的数据填充到模板中。

最后，Spring 对 FreeMarker 的支持也很 nice，必须要掌握。

它的性能貌似没有 Velocity 高，学习起来没有 Velocity 简单，但它内置许多功能强大的标记、以及大量常用的函数，带有宏定义（macro）功能，类似于JSP自定义标签，但是更加简单方便，并且支持 JSP 标签

## 入门

导包就不用多说了，然后下一步就是编写模板文件了，FreeMarker 的模板文件的后缀名是 ftl。通常放在 WebRoot 下的 Template 文件夹下。

1. 文本，直接输出的部分
2. 注释，即<#--...-->格式不会输出
3. 插值（Interpolation）：即${..}或者#{..}格式的部分,将使用数据模型中的部分替代输出
4. FTL指令：FreeMarker指令，和HTML标记类似，名字前加#予以区分，不会输出。

## 其他

对于 null 不存在的对象的取值，可以使用 `${var!'default'}` 这样就不会抛异常.

判断是否存在：
```html
<#if var??></#if>
```
对于日期对象：

`${date?String('yyyy-MM-dd')}`

`？`其实是调用函数，调用的是 Freemarker 内部定义好的函数，或者 JDK 自带的

配置日期的自动转义，需要是 java.sql 包下的 date 对象，它是用于 ORM 的

转义HTML内容：`${var?html}`


调用方法时：

通常使用 ? 可以调用对象的方法，当方法名是由两个单词组成的时候采用的是下划线分割

当方法没有传入参数时，可以直接当属性来用，有参数时就需要写括号、传入参数了

``` html
<#list myList?sort?reverse as item>
  
</#list>
```

待填坑

## 参考

http://www.51gjie.com/javaweb/880.html