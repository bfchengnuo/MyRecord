---
title: Web课上总结
date: 2017-01-05 10:34:05
tags: JavaScript
categories: Web
---

这本来是为了~~应付~~考试而整理的，只是上传到了 Github，后来都忘记这档子事了，偶然间，现在忽然发现也很有用的呢，所以就从 Github 上搬过来了！以前曾认为很简单的代码现在都快看不懂了，药丸..<!-- more -->

## frameset框架

用来组织多个窗口，frameset 元素仅仅会规定**在框架集中存在多少列或多少行**。您必须使用 cols (列)或 rows (行)属性。

```html
<frameset rows="71,*" border="0">
	<frame scrolling="no" src="top.html">
	<frameset cols="222,*">
		<frame scrolling="no" noresize="noresize" src="left.html">
		<frame scrolling="no" noresize="noresize" src="right.html" name="right">
	</frameset>
</frameset>
```

上面的例子首先把页面分成上下部分，上面 71px 高度，引入`top.html`文件，不允许有滚动条
下部分又套了一个框架，分成左右部分，左边 222px 宽度，不允许有滚动条，不允许改变宽度
关于设置点击左边框架右边框架做相应改变，在右边我们定义了个 name，只要在引用页中的a标签设置`target="right"`就可以了

## 布局相关

li 标签去除默认样式：`list-style-type: none;`
a 标签去除默认样式：`text-decoration: none;`

### 伪类选择器

`:hover` 选择器用于选择鼠标指针浮动在上面的元素。
注意：`:hover` **选择器可用于所有元素**，不只是链接。
`:link` 选择器设置指向未被访问页面的链接的样式
`:visited` 选择器用于设置指向已被访问的页面的链接
`:active` 选择器用于活动链接。

>   在 CSS 定义中，:hover 必须位于 :link 和 :visited 之后（如果存在的话），这样样式才能生效。

## 表单元素

使用表单元素首先要用form标签括起来的啊`<form id="form1" action="/home/index" method="post">`
关于校验相关，点击按钮执行一个校验函数，成功返回 true 然后提交到服务器，错误返回 false 不提交，只需要在 form 标签中增加一个属性：`onsubmit="return check(this)"`
form 对象中的元素如果设置了 name 属性可以直接用`.`调用

一、文本域

```html
<input type="text" name="text" value="" />
```

二、密码域

密码跟文本框类似，但是在里面输入的内容显示为圆点。

```html
<input type="password" name="text" value="" />
```

三、单选按钮

```html
男人:<input type="radio" name="sex" value="male" /> Male
<br />
女人：<input type="radio" name="sex" value="female" /> Female
```

四、复选框

```html
<input type="checkbox" name="check1" value="" />
```

五、按钮

```html
<input type="button" value="确认" />
```

六、重置按钮

当点击重置按钮时，重置按钮所在的表单将全部清空，而其他表单不受影响。

```html
<input type="reset" value="重置" />
```

七、提交按钮

当点击提交按钮时，浏览器将自动提交表单。

```html
<input type="submit" value="提交" />
```

八、隐藏域

隐藏域在浏览器中并不显示，仅仅为保存一些不太重要的资料而存在。

```html
<input type="hidden" value="我是一个隐藏域" />
```

九、上传域

```html
<input type="file" value="" />
```

十、图片按钮

```html
<input type="image" src="123.gif" />
```

十一、下拉列表

```html
<select>
   <option value="0">0</option>
   <option value="1">1</option>
   <option value="2">2</option>
</select>     
```

| 属性       | 可选值      | 说明              |
| -------- | -------- | --------------- |
| disabled | disabled | 规定禁用该下拉列表。      |
| multiple | multiple | 规定可选择多个选项。      |
| name     | name     | 规定下拉列表的名称。      |
| size     | number   | 规定下拉列表中可见选项的数目。 |

十二、label

 label 元素不会向用户呈现任何特殊效果。不过，它为鼠标用户改进了可用性。如果您在 label 元素内点击文本，就会触发此控件。就是说，当用户选择该标签时，浏览器就会自动将焦点转到和标签相关的表单控件上。例如，当将单选按钮放在label 内。则点击 label 内的文字也会触发单选按钮，而不必只是点中小圆点。

```html
<p><label><input type="radio" name="male" />男人</label></p>
<p><label><input type="radio" name="male" />女人</label></p>
<!-- 或者这种形式 -->
<form>
  <label for="male">Male</label>
  <input type="radio" name="sex" id="male" />
  <br />
  <label for="female">Female</label>
  <input type="radio" name="sex" id="female" />
</form>
```

**说说下拉列表的联动问题**
采用比较传统的方法，首先设置 select 标签的`onchange="change(this)"`属性

```javascript
function change(obj) {
  var now = obj.selectedIndex;
  var selNew = document.getElementById('sel2');
  switch (now) {
  case 1:
    selNew.options.length = 0;
    selNew.add(new Option("1-zi1","1-zi1"),null); //text , value
    selNew.add(new Option("1-zi2","1-zi2"),null);
    selNew.add(new Option("1-zi3","1-zi3"),null);
    selNew.add(new Option("1-zi4","1-zi4"),null);
    break;
  case 2:
    selNew.options.length = 0;
    selNew.add(new Option("2-zi1","2-zi1"),null); //text , value
    selNew.add(new Option("2-zi2","2-zi2"),null);
    selNew.add(new Option("2-zi3","2-zi3"),null);
    selNew.add(new Option("2-zi4","2-zi4"),null);
    break;
  default:
    selNew.options.length = 0;
    selNew.add(new Option("-请选择-"),null);
    break;
  }
}

//添加到指定位置
function add() {
  var sel = document.getElementById('sel');
  var options = sel.options;
  sel.add(new Option("测试3","test3"),options[1]);
}
```

## 表格

跨行合并：rowspan (用于td)
跨列合并：colspan (用于td)

### 表格的属性(已废弃请用CSS)

align   水平对齐（属性有：left 左；center 中；right 右）
cellspacing   单元格间距
cellpadding   单元格边距
bgcolor   背景颜色
background   背景图像
border   边框
bordercolor   边框颜色
Valign   行内容垂直对齐(middle)

做一个细线表格只需要：
`<table width="300px" align="center" border="0" cellspacing="1" bgcolor="#fcb4b5" height="300px">`

## JS-表格操作

普通的操作中 name 属性显得很重要

```javascript
var flag = true;
//插入一行
function insert() {
  var tb = document.getElementById('tb');
  var inputs = document.getElementsByTagName('input');
  var newRow = tb.insertRow(Number(inputs[4].value)); //插入到那一行
  //创建赋值列
  newRow.insertCell(0).innerHTML = inputs[0].value;
  newRow.insertCell(1).innerHTML = inputs[1].value;
  newRow.insertCell(2).innerHTML = inputs[2].value;
  newRow.insertCell(3).innerHTML = inputs[3].value;
}
//删除某一行
function del() {
  var tb = document.getElementById('tb');
  var num = Number(document.getElementsByTagName('input')[6].value);
  tb.deleteRow(num);
}
//删除某一行的某一列，就是清空内容
function delL() {
  var tb = document.getElementById('tb');
  var inputs = document.getElementsByTagName('input');
  var cells = tb.rows[inputs[8].value].cells;
  cells[inputs[9].value - 1].innerHTML = "";
}
//切换表头的CSS样式
function revCss() {
  var tb = document.getElementById('tb');
  var title = tb.rows[0];
  if (flag) {
    title.className = 'titleRev';
    flag = false;
  }else {
    title.className = 'title';
    flag = true;
  }
}
```

**JS动态生成表格的几种方式**

```javascript
function createTable() {
  var t = document.createElement('table');
  t.setAttribute('border', '');
  t.setAttribute('cellspacing', '0');
  t.setAttribute('cellpadding', '3');
  for (var i = 0; i < 5; i++) {
  	var r = t.insertRow();
    for (var j = 0; j < 5; j++) {
      var c = r.insertCell();
      c.innerHTML = i + ',' + j;
  	}
  }
  document.body.appendChild(t);
}
//第二种方式
function createTable2() {
  var t = document.createElement('table');
  var b = document.createElement('tbody');
  for (var i = 0; i < 2000; i++) {
    var r = document.createElement('tr');
    for (var j = 0; j < 5; j++) {
      var c = document.createElement('td');
      var m = document.createTextNode(i + ',' + j);
      c.appendChild(m);
      r.appendChild(c);
  	}
  	b.appendChild(r);
  }

  t.appendChild(b);
  document.body.appendChild(t);
  t.setAttribute('border', '1');
}
//第三种
function createTable3() {
  var data = '';
  data += '<table border=1><tbody>';
  for (var i = 0; i < 2000; i++) {
    data += '<tr>';
    for (var j = 0; j < 5; j++) {
      data += '<td>' + i + ',' + j + '</td>';
  	}
  	data += '</tr>';
  }
  data += '</tbody><table>';
  document.getElementById('table1').innerHTML = data;
}
//第四种
function createTable4() {
  var data = new Array();
  data.push('<table border=1><tbody>');
  for (var i = 0; i < 2000; i++) {
  	data.push('<tr>');
    for (var j = 0; j < 5; j++) {
      data.push('<td>' + i + ',' + j + '</td>');
    }
 	data.push('</tr>');
  }
  data.push('</tbody><table>');
  document.getElementById('table1').innerHTML = data.join('');
}

window.onload = createTable;
```

**表格排序功能的关键代码**

```javascript
window.onload = function() {
    var oTable = document.getElementById('tableTest');
    var oTbody = oTable.tBodies[0];
    var oBtn = document.getElementById('sort');
    var arr = []; //用来存放每一个tr
    var isAsc = true; //用来判断升序，还是降序

    oBtn.onclick = function() {
        for (var i = 0; i < oTbody.rows.length; i++) {
          //这里是把每一个tr存放到一个数组,存的应该是引用  
          arr[i] = oTbody.rows[i];
        }

        //数组根据cells[0].innerHTML来排序
        arr.sort(function(td1, td2) {
            if (isAsc) {
                return parseInt(td1.cells[0].innerHTML) - parseInt(td2.cells[0].innerHTML);
            } else {
                return parseInt(td2.cells[0].innerHTML) - parseInt(td1.cells[0].innerHTML);
            }

        });
        //把排序后的tr 重新插入tbody
        for (var j = 0; j < arr.length; j++) {
            oTbody.appendChild(arr[j]);
        }
        //判断升序，降序
        isAsc = !isAsc;
    }
}
```

## JS-DOM操作

一切尽在不言中

```javascript
// 获取元素
var el = document.getElementById('xxx');
var els = document.getElementsByClassName('highlight');  //IE8 9 10 均不支持，DOM3加入
var els = document.getElementsByTagName('td');

// 获取父元素、父节点
var parent = ele.parentElement;
var parent = ele.parentNode;

// 获取子节点，子节点可以是任何一种节点，可以通过nodeType来判断
//标准下、非标准下都只含元素类型，但对待非法嵌套的子节点，处理方式与childNodes一致。   
var nodes = ele.children;
//非标准下：只包含元素类型，不会包含非法嵌套的子节点。
//标准下：包含元素和文本类型，会包含非法嵌套的子节点。 
var nodes = ele.childNodes;

// 当前元素的第一个/最后一个子元素节点
var el = ele.firstChild;
var el = ele.lastChild;

// 下一个/上一个兄弟元素节点
var el = ele.nextSibling;
var el = ele.previousSibling;
var el = ele.nextElementSibling;
var el = ele.previousElementSibling;

//创建新标签
var imgNew = document.createElement("img");

// 添加、删除子元素
ele.appendChild(el);
ele.removeChild(el);

// 替换子元素
ele.replaceChild(el1, el2);

// 插入子元素
parentElement.insertBefore(newElement, referenceElement);

//克隆元素
ele.cloneNode(true) //该参数指示被复制的节点是否包括原节点的所有属性和子节点

// 获取、设置属性
var c = el.getAttribute('class');
el.setAttribute('class', 'highlight');
```

## JS-字符串操作

### 判断某个字符出现的次数

分割大法好

```html
<script language=javascript>
var s='jhdoiweesdds';
var n=(s.split('d')).length-1;
document.write(n);
</script>
```

### 替换全部的某个字符

正则大法好

```javascript
var str = 'abcadeacf';
var str1 = str.replace('a', 'o');
alert(str1);  
// 打印结果： obcadeacf
var str2 = str.replace(/a/g, 'o');
alert(str2);  
//打印结果： obcodeocf,
```

注意： 此处replace的第一个参数为正则表达式，/g是全文匹配标识。
删除某一段字符串也可以使用替换的方式
顺便补充点正则相关的东西

```javascript
var pattTemp = /^a/;
if(pattTemp.test(obj.value)){
  alert("a开头的字符");
}

var str = "this is JavaScript";
var t1 = str.indexOf("a");
var t2 = str.indexOf("a",str.indexOf("a") + 1);
```

## JS-杂项

**实现打字机效果：**

```javascript
var i = 0;
var timeID;
var showString= "这是一个基于JavaScript的打字机特效，这是一段测试文字哦，也包括了英文测试。";

window.onload = function ()
{
	marquee();
}

function marquee() {
	timeID= setTimeout("marquee()",100);
	var div = document.getElementById('show');
	div.innerHTML = div.innerHTML + showString.charAt(i);
	i++;

	if (i >= showString.length){
		clearTimeout(timeID);
	}
}
```

或者可以使用setInterval函数：

```javascript
var i = 0;
var timeID;
var showString= "这是一个基于JavaScript的打字机特效，这是一段测试文字哦，也包括了英文测试。";

window.onload = function ()
{
	timeID= setInterval(marquee,100);
}

function marquee() {
	var div = document.getElementById('show');
	div.innerHTML = div.innerHTML + showString.charAt(i);
	i++;

	if (i >= showString.length){
		clearInterval(timeID);
	}
}
```

**获取当前日期：**

```javascript
var myDate = new Date();
myDate.getYear();        //获取当前年份(2位)
myDate.getFullYear();    //获取完整的年份(4位,1970-????)
myDate.getMonth();       //获取当前月份(0-11,0代表1月)
myDate.getDate();        //获取当前日(1-31)
myDate.getDay();         //获取当前星期X(0-6,0代表星期天)
myDate.getTime();        //获取当前时间(从1970.1.1开始的毫秒数)
myDate.getHours();       //获取当前小时数(0-23)
myDate.getMinutes();     //获取当前分钟数(0-59)
myDate.getSeconds();     //获取当前秒数(0-59)
myDate.getMilliseconds();    //获取当前毫秒数(0-999)
myDate.toLocaleDateString();     //获取当前日期

var mytime=myDate.toLocaleTimeString();     //获取当前时间
myDate.toLocaleString( );        //获取日期与时间
```

**JS写的倒计时**
其实和打字机没差，关键代码

```javascript
var i = 0;
function show() {
  document.write(i);
  i++;
  var tPor = setTimeout("show()",1000);
  if (i > 10) {
  	clearTimeout(tPor);
  }
}
show();
```


