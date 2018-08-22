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