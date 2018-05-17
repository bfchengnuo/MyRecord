> 引用自： https://github.com/paddingme/Learning-HTML-CSS/issues/17

Emmet 是高效、快速编写 HTML 和 CSS 代码的一种插件，如果还不了解，请戳[Emmet — the essential toolkit for web-developers](http://docs.emmet.io/),再根据你使用的编辑器（sublime 或 vim 等）下载对应的 Emmet 插件，让你的代码快步如飞吧。下面我记录下常用的 Emmet 语法和快捷键。代码区里的均指在编辑器里输入的字符，然后按 “Tab” 键便会生成代码。
例如 输入`!` 然后按 “tab” 会得到如下所示的HTML文档初始机构，再也不用一个字母一个字母敲了，是不是很简单呢？

[![initializers](https://cloud.githubusercontent.com/assets/5771087/5154271/3a62e56a-728d-11e4-8259-1c09201c5b7f.gif)](https://cloud.githubusercontent.com/assets/5771087/5154271/3a62e56a-728d-11e4-8259-1c09201c5b7f.gif)

## HTML 编写 

1. 生成HTML文档初始机构 2. `html:5` 或者 `!` 生成 HTML5 结构

  ```html
  <!DOCTYPE html>
  <html lang="zh-CN">
    <head>
      <meta charset="UTF-8">
      <title>Document</title>
    </head>
    <body>
  
    </body>
  </html>
  ```

2. `html:xt` 生成 HTML4 过渡型

   ```html
   <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
   <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="zh-cmn-Hans">
     <head>
       <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
       <title>Document</title>
     </head>
     <body>
   
     </body>
   </html>
   ```

3. `html:4s` 生成 HTML4 严格型

   ```html
   <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
   <html lang="zh-cmn-Hans">
     <head>
       <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
       <title>Document</title>
     </head>
     <body>
   
     </body>
   </html>
   ```

4. 任意一个 html 标签输入都会生成完整的闭合标签。

   例如输入 `p` 按 `tab` 则 生成：`<p></p>`

5. 生成带有 id 、class 的 HTML 标签: `#`为 id，`.` 为 class。

   例如输入 `div#header.section` 则生成 `<div id="header" class="section"></div>`

6. 生成后代：`>`

   例如输入`p>span` 则生成 `<p><span></span></p>`

7. 生成兄弟标签：`+`

   例如输入`p+div` 则生成 `<p></p><div></div>`

8. 生成上级标签： `^`

   例如输入`ul>li>a^div` 则生成

   ```html
   <ul>
     <li><a href=""></a></li>
     <div></div>
   </ul>
   ```

   也可以使用多个 `^`,
   例如输入`ul>li>a^^div` 则生成

   ```html
   <ul>
       <li><a href=""></a></li>
   </ul>
   <div></div>
   ```

9. 重复生成多个标签 `*`
   例如输入`ul>li*5` 则生成

   ```html
   <ul>
     <li></li>
     <li></li>
     <li></li>
     <li></li>
     <li></li>
   </ul>
   ```

10. 生成分组的标签: `()`
   例如输入`ul>(li>a)*5` 则生成

   ```html
   <ul>
     <li><a href=""></a></li>
     <li><a href=""></a></li>
     <li><a href=""></a></li>
     <li><a href=""></a></li>
     <li><a href=""></a></li>
   </ul>
   ```

   注意和`ul>li>a*5` 生成是不一样的

   ```html
   <ul>
     <li>
       <a href=""></a>
       <a href=""></a>
       <a href=""></a>
       <a href=""></a>
       <a href=""></a>
     </li>
   </ul>
   ```

11. 生成自定义属性：`[]`
    例如输入`img[https://octodex.github.com/images/codercat.jpg][alt=octcat]` 则生成

    ```html
    <img src="https://octodex.github.com/images/codercat.jpg" alt="octcat" />
    ```

12. 生成递增的属性标签等: `$`
    例如输入`ul>li.item$*5` 则生成

    ```html
    <ul>
      <li class="item1"></li>
      <li class="item2"></li>
      <li class="item3"></li>
      <li class="item4"></li>
      <li class="item5"></li>
    </ul>
    ```

13. 生成多位递增的呢：`$$$`
    例如输入`ul>li.item$$$*5` 则生成

    ```html
    <ul>
      <li class="item001"></li>
      <li class="item002"></li>
      <li class="item003"></li>
      <li class="item004"></li>
      <li class="item005"></li>
    </ul>
    ```

    想生成几位输入几个`$`

14. 要生成递减的呢：`@-`
    例如输入`ul>li.item$@-*5` 则生成

    ```html
    <ul>
      <li class="item5"></li>
      <li class="item4"></li>
      <li class="item3"></li>
      <li class="item2"></li>
      <li class="item1"></li>
    </ul>
    ```

15. 想要从某个特定的顺序开始呢：`@N`
    例如输入`ul>li.item$@10*5` 则生成

    ```html
    <ul>
      <li class="item10"></li>
      <li class="item11"></li>
      <li class="item12"></li>
      <li class="item13"></li>
      <li class="item14"></li>
    </ul>
    ```

16. 逆序生成到某个数：`@-`
    例如输入`ul>li.item$@-10*5` 则生成

    ```html
    <ul>
      <li class="item14"></li>
      <li class="item13"></li>
      <li class="item12"></li>
      <li class="item11"></li>
      <li class="item10"></li>
    </ul>
    ```

17. 生成文本内容：`{}`
    例如输入`p{我是paddingme}` 则生成

    ```html
    <p>我是paddingme</p>
    ```

18. 缺省元素:

    - 声明一个带class的div 可以不用输入div；
      `.header+.footer` 则生成:

    ```html
    <div class="header"></div>
    <div class="footer"></div>
    ```

    - Emmet 还会根据父标签进行判定
      例如输入`ul>.item*3` 则生成：

    ```html
    <ul>
      <li class="item"></li>
      <li class="item"></li>
      <li class="item"></li>
    </ul>
    ```

    下面是所有的隐式标签名称：

    - li：用于 ul 和 ol 中
    - tr：用于 table、tbody、thead 和 tfoot 中
    - td：用于 tr 中
    - option：用于 select 和 optgroup 中

## CSS 编写

首先，Sublime Text3 已经提供了比较强大的 CSS 样式所写方法来提高 CSS 编写效率。例如编写 `position: absolute`; 这一个属性，我们只需要输入 posa 这四个字母即可。可以在平时书写过程中，留意一下 ST3 提供了哪些属性的缩写方法，这样就可以提高一定的效率了。但是 Emmet 提供了更多的功能，请往下看。

1. 简写属性和属性值
   比如要定义元素的宽度，只需输入w100，即可生成：

   ```css
   width: 100px;
   ```

   Emmet 的默认设置 w 是 width 的缩写，后面紧跟的数字就是属性值。默认的属性值单位是 px ，你可以在值的后面紧跟字符生成单位，可以是任意字符。例如，`w100foo` 会生成：

   ```css
   width:100foo;
   ```

   同样也可以简写属性单位，如果你紧跟属性值后面的字符是`p`，那么将会生成：

   ```css
   width:100%;
   ```

   下面是单位别名列表：

   - p 表示%
   - e 表示 em
   - x 表示 ex

   像 margin 这样的属性，可能并不是一个属性值，生成多个属性值需要用横杠（-）连接两个属性值，因为 Emmet 的指令中是不允许空格的。例如使用 `m10-20` 可以生成：

   ```css
   margin: 10px 20px;
   ```

   如果你想生成负值，多加一条横杠即可。例如：`m10--20` 可以生成：

   ```css
   margin: 10px -20px;
   ```

   需要注意的是，如果你对每个属性都指定了单位，那么不需要使用横杠分割。例如使用 m10e20e 可以生成：

   ```css
   margin: 10em 20em;
   ```

   如果使用了横杠分割，那么属性值就变成负值了。例如使用 `m10e-20e` 就生成：

   ```css
   margin: 10em -20em;
   ```

   如果你想一次生成多条语句，可以使用 “+” 连接两条语句，例如使用 h10p+m5e 可以生成：

   ```css
   height: 10%;
   margin: 5em;
   ```

   颜色值也是可以快速生成的，例如 `c#3` 生成`color: #333`;，更复杂一点的，使用 `bd5#0s` 可以生成 `border: 5px #000 solid`;。
   下面是颜色值生成规则：

   - ‘# 1’ → #111111
   - ‘#e0’ → #e0e0e0
   - ‘#fc0’ → #ffcc00
     生成 `!important` 这条语句也当然很简单，只需要一个 “!” 就可以了。

2. 附加属性
   使用 `@f` 即可生成 CSS3 中的 font-face 的代码结构：

   ```css
   @font-face {
      font-family:;
      src:url();
   }
   ```

   但是这个结构太简单，不包含一些其他的 font-face 的属性，诸如 background-image、border-radius、font、[@font-face](https://github.com/font-face)、 text-outline、 text-shadow 等属性，我们可以在生成的时候输入 “+” 生成增强的结构，例如我们可以输入 `@f+` 命令，即可输出选项增强版的 font-face 结构：

   ```css
   @font-face {
     font-family: 'FontName';
     src: url('FileName.eot');
     src: url('FileName.eot?#iefix') format('embedded-opentype'),
       url('FileName.woff') format('woff'),
       url('FileName.ttf') format('truetype'),
       url('FileName.svg#FontName') format('svg');
     font-style: normal;
     font-weight: normal;
   }
   ```

3. 模糊匹配
   如果有些缩写你拿不准，Emmet 会根据你的输入内容匹配最接近的语法，比如输入 `ov:h`、`ov-h`、`ovh` 和 `oh`，生成的代码是相同的：

   ```css
   overflow: hidden;
   ```

4. 供应商前缀
   CSS3 等现在还没有出正式的 W3C 规范，但是很多浏览器已经实现了对应的功能，仅作为测试只用，所以在属性前面加上自己独特的供应商前缀，不同的浏览器只会识别带有自己规定前缀的样式。然而为了实现兼容性，我们不得不编写大量的冗余代码，而且要加上对应的前缀。使用 Emmet 可以快速生成带有前缀的 CSS3 属性。
   在任意字符前面加上一条横杠（-），即可生成该属性的带前缀代码，例如输入 `-foo-`css，会生成：

   ```css
   -webkit-foo-css: ;
   -moz-foo-css: ;
   -ms-foo-css: ;
   -o-foo-css: ;
   foo-css: ;
   ```

   虽然 foo-css 并不是什么属性，但是照样可以生成。此外，你还可以详细的控制具体生成哪几个浏览器前缀或者先后顺序，使用 `-wm-trf` 即可生成：

   ```css
   -webkit-transform: ;
   -moz-transform: ;
   transform: ;
   ```

   可想而知，w 就是 webkit 前缀的缩写，m 是 moz 前缀缩写，s 是 ms 前缀缩写，o 就是 opera 浏览器前缀的缩写。如果使用 `-osmw-abc` 即可生成：

   ```css
   -o-abc: ;
   -ms-abc: ;
   -moz-abc: ;
   -webkit-abc: ;
   abc: ;
   ```

5. 渐变背景
   CSS3 中新增加了一条属性 `linear-gradient` 使用这个属性可以直接制作出渐变的效果。但是这个属性的参数比较复杂，而且需要添加实验性前缀，无疑需要生成大量代码。而 在 Emmet 中使用 `lg()`指令即可快速生成，例如：使用 `lg(left,#fff,50%,#000)`可以直接生成：

   ```css
   background-image: -webkit-gradient(linear, 0 0, 100% 0, color-stop(0.5, #fff),  to(#000));
   background-image: -webkit-linear-gradient(left, #fff 50%, #000);
   background-image: -moz-linear-gradient(left, #fff 50%, #000);
   background-image: -o-linear-gradient(left, #fff 50%, #000);
   background-image: linear-gradient(left, #fff 50%, #000);
   ```

6. 附加功能
   生成Lorem ipsum文本
   Lorem ipsum 指一篇常用于排版设计领域的拉丁文文章，主要目的是测试文章或文字在不同字型、版型下看起来的效果。通过 Emmet，你只需输入 lorem 或 lipsum 即可生成这些文字。还可以指定文字的个数，比如 `lorem10`，将生成：

   ```css
   Lorem ipsum dolor sit amet, consectetur adipisicing elit. Explicabo, esse,  provident, nihil laudantium vitae quam natus a earum assumenda ex vel  consectetur fugiat eveniet minima veritatis blanditiis molestiae harum est!
   ```

## 定制

你还可以定制Emmet插件：

- 添加新缩写或更新现有缩写，可修改 snippets.json 文件
- 更改Emmet过滤器和操作的行为，可修改 preferences.json 文件
- 定义如何生成HTML或XML代码，可修改 syntaxProfiles.json 文件
  如何自定义 Emmet 语法可参考：<http://qianduanblog.com/post/sublime-text-3-custom-emmet-output-bootstrap-widget.html;github> 上 一丝的 [Emment-plus](https://github.com/yisibl/emmet-plus) 也推荐

## 快捷键

- Ctrl+,: 展开缩写
- Ctrl+M: 匹配对
- Ctrl+H: 使用缩写包括
- Shift+Ctrl+M: 合并行
- Ctrl+Shift+?: 上一个编辑点
- Ctrl+Shift+?: 下一个编辑点
- Ctrl+Shift+?: 定位匹配对

Emmet 的官方 API 列表在[这里](http://docs.emmet.io/cheat-sheet/)，有一个图片版本可以[点此下载](http://bubkoo.qiniudn.com/emmet-api.jpg)