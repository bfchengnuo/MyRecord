# Thymeleaf语法

官方文档（PDF）里面有大量的例子，还不错，基本是够用了，抛开性能问题，能被 SpringBoot 采用的，肯定也有其优势。

由一个简单的例子来入门吧：

``` html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <h1>成功！</h1>
    <!--th:text 将div里面的文本内容设置为 -->
    <div th:text="${hello}">这是显示欢迎信息</div>
</body>
</html>
```

就是一个 HTML 文件，并且是可以打开的，给它加了个命名空间是为了 IDE 有提示，然后就都是用类似 `th:xx` 的属性来写了，当使用模板引擎解析时，就会进行相应的填充，例如 `th:text` 就会替换标签里的内容。

可以使用 `th:任意html属性` 来替换原生属性的值。

## 表达式

可以使用 `${...}` 获取变量值，使用的是 OGNL，所以支持下面的几种特性

- 获取对象的属性、调用方法
- 使用内置的基本对象
  例如：ctx、vars、request、response、session 等
- 内置的一些工具对象
  例如：arrays、lists、sets、maps、ids、bools 等

---

另一种表达式形式是 `*{...}` 它和 `${}` 在功能上是一样，但是它还可以配合 `th:object` 来使用。

举个栗子：

``` html
<div th:object="${session.user}">
  <p>Name: <span th:text="*{firstName}">Sebastian</span>.</p>
  <p>Surname: <span th:text="*{lastName}">Pepper</span>.</p>
  <p>Nationality: <span th:text="*{nationality}">Saturn</span>.</p>
</div>
```

应该可以看懂吧。。。

---

其他的几个表达式也提一下：

`#{...}`：获取国际化内容

`@{...}`：定义 URL，举例：`@{/order/process(execId=${execId},execType='FAST')}`

`~{...}`：片段引用表达式，举例：`<div th:insert="~{commons :: main}">...</div>`

---

与其他表达式类似，支持字面量、文本操作、数学运算、布尔运算、比较运算、条件运算

> and、or、!、not、gt、lt、eq、ne 等
>
> If-then: `(if) ? (then)`
> If-then-else: `(if) ? (then) : (else)`
> Default: `(value) ?: (defaultvalue)`

---

TODO

## 页面抽取

抽取公共片段使用 `th:fragment` 属性，然后给定一个名字即可。

``` html
<div th:fragment="copy">
&copy; 2011 The Good Thymes Virtual Grocery
</div>
```

然后可以在需要的地方进行导入，导入方式有多种，每一种也可能有多种写法，举个栗子：

``` html
<!-- 示例 -->
<div th:insert="~{footer :: copy}"></div>

<!-- 
	第一种：模板名::选择器，这种不需要定义 fragment 了，
	模板名就是 HTML 文件名了，不在根路径下需要指明地址
	~{commons/bar::#idName}
-->
~{templatename::selector}
<!-- 第二种：模板名::片段名 -->
~{templatename::fragmentname}
```

- **th:insert**：
  将公共片段整个**插入到**声明引入的元素中
- **th:replace**：
  将声明引入的元素**替换为**公共片段
- **th:include**：
  将被引入的片段的内容**包含进**这个标签中（模板最外层的标签会自动去掉，然后再插入进去）

不同的方式效果也有所不同，例如 insert 方式就直接把内容塞到了当前标签里。

> 如果使用 `th:insert` 等属性进行引入，可以不用写 `~{}`
>
> 行内写法的话，可以加上，例如：`[[~{}]]` （转义） `[(~{})]` （不转义）

另外，可以使用变量（参数化），来达到灵活控制的目的，例如：

``` html
<!-- 定义变量，这种形式其实可以忽略 -->
<div th:fragment="frag (onevar,twovar)">
  <!-- 使用变量 -->
	<p th:text="${onevar} + ' - ' + ${twovar}">...</p>
</div>

<!-- 使用参数 -->
<div id="sidebar">
  <a th:class="${activeUri=='main.html'?'nav-link active':'nav-link'}"
   href="#" th:href="@{/main.html}"/>
</div>
<!-- 传入参数 -->
<div th:replace="commons/bar::#sidebar(activeUri='emps')"></div>
```

当然，写法有多种，在官方手册的 <kbd>8.2</kbd> 节有详细说明。

## 常用

常用的属性组合

``` html
<tr th:each="user : ${users}">
  <!-- 避免NPE，${user != null}?${user.name} -->
  <td class="username" th:text="${user.name}">Jeremy Grapefruit</td>
  <td class="usertype" th:text="#{|user.type.${user.type}|}">Normal User</td>
  <!-- 日期格式化：${#dates.listFormat(datesList, 'yyyy-MM-dd HH:mm:ss')} -->
</tr>

<!-- if 成立才渲染 -->
<input th:if="${user != null}"/>
```

