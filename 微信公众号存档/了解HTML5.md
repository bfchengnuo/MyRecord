# 了解HTML5

## 什么是HTML5

引用wiki上的一段话：

> **HTML5**是[HTML](https://zh.wikipedia.org/wiki/HTML)最新的修订版本，2014年10月由[万维网联盟](https://zh.wikipedia.org/wiki/%E4%B8%87%E7%BB%B4%E7%BD%91%E8%81%94%E7%9B%9F)（W3C）完成标准制定。目标是替换1999年所制定的[HTML](https://zh.wikipedia.org/wiki/HTML) 4.01和[XHTML](https://zh.wikipedia.org/wiki/XHTML) 1.0标准，以期能在互联网应用迅速发展的时候，使网络标准达到匹配当代的网络需求。**广义论及HTML5时，实际指的是包括HTML、[CSS](https://zh.wikipedia.org/wiki/CSS)和[JavaScript](https://zh.wikipedia.org/wiki/JavaScript)在内的一套技术组合。**它希望能够减少[网页浏览器](https://zh.wikipedia.org/wiki/%E7%B6%B2%E9%A0%81%E7%80%8F%E8%A6%BD%E5%99%A8)对于需要[插件](https://zh.wikipedia.org/wiki/%E6%8F%92%E4%BB%B6)的[丰富性网络应用服务](https://zh.wikipedia.org/wiki/%E8%B1%90%E5%AF%8C%E6%80%A7%E7%B6%B2%E8%B7%AF%E6%87%89%E7%94%A8%E6%9C%8D%E5%8B%99)（Plug-in-Based Rich Internet Application，[RIA](https://zh.wikipedia.org/wiki/RIA)），
> 例如：[Adobe](https://zh.wikipedia.org/wiki/Adobe)[Flash](https://zh.wikipedia.org/wiki/Flash)、[Microsoft](https://zh.wikipedia.org/wiki/Microsoft) [Silverlight](https://zh.wikipedia.org/wiki/Silverlight)与[Oracle](https://zh.wikipedia.org/wiki/Oracle) [JavaFX](https://zh.wikipedia.org/wiki/JavaFX)的需求，并且提供更多能有效加强网络应用的标准集。

简单可以理解为：HTML 5 ≈ HTML4.0 + CSS3 + JS + API
HTML 的上一个版本诞生于 1999 年。自从那以后，Web 世界已经经历了巨变。HTML是时候更新下，适应时代了！

> HTML5 本身并不是技术，而是一个标准为 HTML5 建立的一些规则：
> - 新特性应该基于 HTML、CSS、DOM 以及 JavaScript。
> - 减少对外部插件的需求（比如 Flash）
> - 更优秀的错误处理
> - 更多取代脚本的标记
> - HTML5 应该独立于设备
> - 开发进程应对公众透明
>
> HTML5 中的一些有趣的新特性：
>
> - 用于绘画的 canvas 元素
> - 用于媒介回放的 video 和 audio 元素
> - 对本地离线存储的更好的支持
> - 新的特殊内容元素，比如 article、footer、header、nav、section
> - 新的表单控件，比如 calendar、date、time、email、url、search

然而很多把HTML5说成H5，这是一种很不专业的说法，毕竟html里还有个H5标签呢(￣^￣)
它只是最新的一个标准，不过在国内营销成分太多了...

## 与HTML(4)的区别

### 文档类型声明上

html:

```html
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "
http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
```

HTML5:

```html
<!DOCTYPE html>
```

很显然，做了极大的精简。那如果你把 html4 解析的页面定义成了 html5 个规范,会发生什么情况呢?
答案是 什么情况都没有
W3C标准已经向地球所有人类承诺 DOCTYPE 参数的定义兼容所有 html 版本(因为前两次的失败教训)
其实包括meta标记的定义已经引用都做了精简：

```html
html4及以下
<meta http-equiv="content-type" conent="text/html" charset="utf-8" />
<link style="text/css" rel="stylesheet" href="/css/main.css" />
<script type="text/javascript" src="/javascript/main.js" ></script>
html5
<meta charset="utf-8" />
<link rel="stylesheet" href="/css/main.css" />
<script src="/javascript/main.js" ></script>
```

### 标签的增减

**废弃的标签：**

下面这些标签并不被HTML5 支持： 

> <acronym>、<applet>、<basefont>、<big>、<center>、<dir>、<font>、<frame>、<frameset>、<noframes>、<s>、<strike>、<tt>、<u> 和<xmp>。

因为这些完全可以用CSS实现，并且功能更丰富

**更加语义化的新增标签：**

HTML5 新增的一些新标签除了不仅仅是更具语义的`<div> `标签的替代品，**并不提供额外的功能**。这些都是新增的标签：

> <article>、<section>、<aside>、<hgroup>、 <header>,<footer>、<nav>、<time>、<mark>、<figure> 和<figcaption>。
>
> 例：
> <header>：代表HTML的头部数据
> <footer>：页面的脚部区域
> <nav>：页面导航元素
> <article>：自包含的内容
> <section>：使用内部article去定义区域或者把分组内容放到区域里
> <aside>：代表页面的侧边栏内容

另：在HTML5 中，空标签（如：br、img 和input ）并不需要闭合标签。

### 表单增强

> 新的INPUT类型：color, email, date, month, week, time, datetime, datetime-local, number,range,search, tel, 和url
> 新属性：required, autofocus, pattern, list, autocomplete 和placeholder
> 新元素：<keygen>, <datalist>, <output>, <meter> 和<progress>

### 绘图和本地存储

这里算HTML5的重要内容，所以一点半点也说不清楚，说下关键字
绘图：Canvas
客户端存储数据的新方法：

- localStorage - 没有时间限制的数据存储
- sessionStorage - 针对一个 session 的数据存储

之前，这些都是由 cookie 完成的。但是 cookie 不适合大量数据的存储，因为它们由每个对服务器的请求来传递，这使得 cookie 速度很慢而且效率也不高。

在 HTML5 中，数据不是由每个服务器请求传递的，而是只有在请求时使用数据。它使在不影响网站性能的情况下存储大量数据成为可能。

对于不同的网站，数据存储于不同的区域，并且一个网站只能访问其自身的数据。

HTML5 使用 JavaScript 来存储和访问数据。

### 其他

上面所说只是一些比较受关注的特性，其他的还有不少，比如HTML5中的自定义属性，更多的我就没去探索，毕竟只是了解

## 参考

W3CSchool教程系列:http://www.w3school.com.cn/html5/index.asp
http://www.techug.com/40-important-html-5-interview-questions-with-answers
http://www.daqianduan.com/2857.html
http://www.daqianduan.com/2857.html

去探寻标签的使用方法