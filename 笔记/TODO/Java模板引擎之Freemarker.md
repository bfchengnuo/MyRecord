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
2. 注释，即 `<#--...-->` 格式不会输出
3. 插值（Interpolation）：即 `${..}` 或者 `#{..}` 格式的部分，将使用数据模型中的部分替代输出
4. FTL 指令：FreeMarker 指令，和 HTML 标记类似，名字前加 `#` 予以区分，不会输出。

## 常用语法

### 空判断

对于 null 不存在的对象的取值，可以使用 `${var!'default'}` 这样就不会抛异常（就是在后面加一个叹号），如果是 `name.val!` 默认就是会对最近的 val 进行 null 判断，想要对 name 也进行判断可以加个括号，这样就会对整体进行判断，也就是里面的每个变量进行判断.

---

判断是否存在（两个问号）：
```html
<#if var??></#if>
```
### 格式化

对于日期对象：

`${date?String('yyyy-MM-dd')}`

`？`其实是调用函数，调用的是 Freemarker 内部定义好的函数，或者 JDK 自带的。

配置日期的**自动转义**，需要是 java.sql 包下的 date 对象，它是用于 ORM 的，会自动格式化（根据配置的格式）。

转义 HTML 内容：`${var?html}`

---

freemarker 只能调用 Java 的 Public static 方法及属性 。

调用方法时，通常使用 `?` 可以调用对象的方法，当内置方法名是由两个单词组成的时候采用的是下划线分割，比如字符串的  `upper_case` 、`lower_case`、`index_of()`、`last_index_of()`.

当方法没有传入参数时，可以直接当属性来用，有参数时就需要写括号、传入参数了。

``` xml
<#assign name=user.getName("Nwen")>
```

### 集合遍历

对于 List：

``` html
<#list myList?sort?reverse as item>
  // 获取下标 0 开始
  ${item_index}
</#list>
```

对于 Map 的遍历：

``` html
<#list myMap?keys as key>
  ${key}:${myMap[key]}
</#list>
```

### 逻辑表达式

if 、switch 表达式：

``` xml
<!-- 定义一个变量 var -->
<#assign var=99 />

<#if var == 99>
  // something
  <#elseif var &gt; 99>
  // something
  <#else>
  // something
</#if>
    
<!-- 判断集合是否为空 -->
<#if list??></#if>
<#if list?exists></#if>




<#switch var>
  <#case 10>
  // something
  <#break>
  
  <#case 20>
  // something
  <#default>
  // other
</#switch>
```

> 在 HTML 中的 if 标签可以直接使用 && 来进行逻辑运算，不需要转义

待填坑

## 自定义函数

定义的自定义函数可以在 html 模板中直接调用，但是需要先通过 mode 传过去，key 就是自定义函数名，也就是前台调用时写的名字；至于 value 是一个实现了 **TemplateMethodModelEx**  的类，并且重写其 exec 方法。

需要注意的是，exec 方法接受的参数肯定是 Freemarker 自己的数据类型，不能直接强转成 Java 的类型。

``` java
/**
 * 实现排序 freemarker要实现TemplateMethodModelEx接口
 */
public class SortMethod implements TemplateMethodModelEx{
  @Override
  public Object exec(List arguments) throws TemplateModelException {
    //获取第一个参数
    SimpleSequence arg0 = (SimpleSequence)arguments.get(0);
    List<BigDecimal> list = arg0.toList();

    //Comparator接口
    Collections.sort(list,new Comparator<BigDecimal>(){
      @Override
      public int compare(BigDecimal o1, BigDecimal o2) {
        return o1.intValue() - o2.intValue();  //升序
      }	
    });

    return list;
  }
}
```

## 自定义指令

自定义指令的使用为了区别于 Freemarker 语法，使用的是 `<@>` 的形式，就是把 `#` 改为 `@` 了。

例如：`<@role user="入参1" role="入参2";result1，result2> `

然后需要在 spring_freemarker 的配置文件中进行定义这个自定义的指令：

``` xml
<property name="freemarkerVariables">  
  <map>  
    <entry key="role" value-ref="roleDirectiveModel" />  
  </map>  
</property>
```

然后配置文件中引用的 bean 需要实现 TemplateDirectiveModel  接口，并且重写 exec 方法。

``` java
public class RoleDirectiveModel implements TemplateDirectiveModel {
  /**
	 * env:环境变量
	 * params:指令参数（储存你所需要的值，随便是什么Key-Value你懂的）
	 * loopVars:循环变量
	 * body:指令内容
	 * 除了params外，其他的都能使null。
	 */
  @Override
  public void execute(Environment env, Map params, TemplateModel[] loopVars,
                      TemplateDirectiveBody body) throws TemplateException, IOException {

    TemplateScalarModel user = (TemplateScalarModel)params.get("user");
    TemplateScalarModel role = (TemplateScalarModel)params.get("role");

    if("123456".equals(user.getAsString()) 
       && "admin".equals(role.getAsString())){//用户id
      loopVars[0] = TemplateBooleanModel.TRUE;
    }

    List<String> otherRights = new ArrayList<String>();
    otherRights.add("add");
    otherRights.add("delete");
    otherRights.add("update");
    loopVars[1] = new SimpleSequence(otherRights);

    body.render(env.getOut());
  }
}
```

这里也是，返回值的填充使用 Freemarker 内置的数据类型，不能使用 Java 中的，比如 boolean。

## 其他

常用内建函数：

String：date、datetime、time、?string("0.##") ......

List：first  、last、  seq_contains 、trunk（几个一组）、seq_index_of、sort_by  ........

is函数：is_string 、  is_number  、 is_method 、hs_content （是否有内容）.......

eval 求值

// TODO.....

macro、function 指令 .....

## 参考

http://www.51gjie.com/javaweb/880.html

https://segmentfault.com/a/1190000011768799