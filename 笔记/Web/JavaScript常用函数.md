## 字符串

> 当 String() 和运算符 new 一起作为构造函数使用时，它返回一个新创建的 String 对象，存放的是字符串 s 或 s 的字符串表示。
> 
> 当不用 new 运算符调用 String() 时，它只把 s 转换成原始的字符串，并返回转换后的值。
> 
> **字符串是 JavaScript 的一种基本的数据类型。**
>
> String 对象的 length 属性声明了该字符串中的字符数。
>
> String 类定义了大量操作字符串的方法，例如从字符串中提取字符或子串，或者检索字符或子串。
>
> 需要注意的是，**JavaScript 的字符串是不可变的（immutable），String 类定义的方法都不能改变字符串的内容**。像 `String.toUpperCase()` 这样的方法，返回的是全新的字符串，而不是修改原始字符串。

## 字符串对象属性

| 属性        | 描述                       |
| ----------- | -------------------------- |
| constructor | 对创建该对象的函数的引用   |
| length      | 字符串的长度               |
| prototype   | 允许您向对象添加属性和方法 |

## 对象方法

| 方法                                                         | 描述                                                 |
| ------------------------------------------------------------ | ---------------------------------------------------- |
| [anchor()](http://www.w3school.com.cn/jsref/jsref_anchor.asp) | 创建 HTML 锚。                                       |
| [big()](http://www.w3school.com.cn/jsref/jsref_big.asp)      | 用大号字体显示字符串。                               |
| [blink()](http://www.w3school.com.cn/jsref/jsref_blink.asp)  | 显示闪动字符串。                                     |
| [bold()](http://www.w3school.com.cn/jsref/jsref_bold.asp)    | 使用粗体显示字符串。                                 |
| [charAt()](http://www.w3school.com.cn/jsref/jsref_charAt.asp) | 返回在指定位置的字符。                               |
| [charCodeAt()](http://www.w3school.com.cn/jsref/jsref_charCodeAt.asp) | 返回在指定的位置的字符的 Unicode 编码。              |
| [concat()](http://www.w3school.com.cn/jsref/jsref_concat_string.asp) | 连接字符串。                                         |
| [fixed()](http://www.w3school.com.cn/jsref/jsref_fixed.asp)  | 以打字机文本显示字符串。                             |
| [fontcolor()](http://www.w3school.com.cn/jsref/jsref_fontcolor.asp) | 使用指定的颜色来显示字符串。                         |
| [fontsize()](http://www.w3school.com.cn/jsref/jsref_fontsize.asp) | 使用指定的尺寸来显示字符串。                         |
| [fromCharCode()](http://www.w3school.com.cn/jsref/jsref_fromCharCode.asp) | 从字符编码创建一个字符串。                           |
| [indexOf()](http://www.w3school.com.cn/jsref/jsref_indexOf.asp) | 检索字符串。                                         |
| [italics()](http://www.w3school.com.cn/jsref/jsref_italics.asp) | 使用斜体显示字符串。                                 |
| [lastIndexOf()](http://www.w3school.com.cn/jsref/jsref_lastIndexOf.asp) | 从后向前搜索字符串。                                 |
| [link()](http://www.w3school.com.cn/jsref/jsref_link.asp)    | 将字符串显示为链接。                                 |
| [localeCompare()](http://www.w3school.com.cn/jsref/jsref_localeCompare.asp) | 用本地特定的顺序来比较两个字符串。                   |
| [match()](http://www.w3school.com.cn/jsref/jsref_match.asp)  | 找到一个或多个正则表达式的匹配。                     |
| [replace()](http://www.w3school.com.cn/jsref/jsref_replace.asp) | 替换与正则表达式匹配的子串。                         |
| [search()](http://www.w3school.com.cn/jsref/jsref_search.asp) | 检索与正则表达式相匹配的值。                         |
| [slice()](http://www.w3school.com.cn/jsref/jsref_slice_string.asp) | 提取字符串的片断，并在新的字符串中返回被提取的部分。 |
| [small()](http://www.w3school.com.cn/jsref/jsref_small.asp)  | 使用小字号来显示字符串。                             |
| [split()](http://www.w3school.com.cn/jsref/jsref_split.asp)  | 把字符串分割为字符串数组。                           |
| [strike()](http://www.w3school.com.cn/jsref/jsref_strike.asp) | 使用删除线来显示字符串。                             |
| [sub()](http://www.w3school.com.cn/jsref/jsref_sub.asp)      | 把字符串显示为下标。                                 |
| [substr()](http://www.w3school.com.cn/jsref/jsref_substr.asp) | 从起始索引号提取字符串中指定数目的字符。             |
| [substring()](http://www.w3school.com.cn/jsref/jsref_substring.asp) | 提取字符串中两个指定的索引号之间的字符。             |
| [sup()](http://www.w3school.com.cn/jsref/jsref_sup.asp)      | 把字符串显示为上标。                                 |
| [toLocaleLowerCase()](http://www.w3school.com.cn/jsref/jsref_toLocaleLowerCase.asp) | 把字符串转换为小写。                                 |
| [toLocaleUpperCase()](http://www.w3school.com.cn/jsref/jsref_toLocaleUpperCase.asp) | 把字符串转换为大写。                                 |
| [toLowerCase()](http://www.w3school.com.cn/jsref/jsref_toLowerCase.asp) | 把字符串转换为小写。                                 |
| [toUpperCase()](http://www.w3school.com.cn/jsref/jsref_toUpperCase.asp) | 把字符串转换为大写。                                 |
| toSource()                                                   | 代表对象的源代码。                                   |
| [toString()](http://www.w3school.com.cn/jsref/jsref_toString_string.asp) | 返回字符串。                                         |
| [valueOf()](http://www.w3school.com.cn/jsref/jsref_valueOf_string.asp) | 返回某个字符串对象的原始值。                         |
