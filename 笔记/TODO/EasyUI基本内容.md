## EasyUI基本内容

在使用之前肯定要导入相应的库，官方现在有两个版本，一个是基于 jQuery 的一个是基于 Angular 的，我就是用传统的 jQuery 的了。

必要的导入：

``` html
<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">

<script type="text/javascript" src="easyui/jquery.min.js"></script>
<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
```

css 和 js 的整体导入顺序无所谓，但是要保证先导入 jQuery 再导入 EasyUI。

使用的时候也就是为 div 等标签加 class 样式，EasyUI 官方给的样式一般都是以 `easyui-` 开头，比如：`class="easyui-panel"` ，这是一个普通的面板视图。

>   其实 class 并不是非要 `easyui-` 开头，在 style 文件里并没有这样定义，就入上面的可以直接写：`class="panel"`
>
>   我像是 js 进行了一定的处理，这些的写法在 ide 中是不会提示的.....

## 基本组件使用

演示常用的几个“组件”的使用，EasyUI 常用于后台 UI 的开发，虽然不是很漂亮，但是实用性还是可以的。
从 EasyUI 1.3 版开始可以用 HTML5 标准的 `'data-options'` 属性来定义 EasyUI 的自定义属性（推荐）

### panel（面板）

详细的使用方法可以在 API 文档的 Layout 中查看，栗子：

### DataGrid（数据表格）

只说动态创建的方式，首先要有一个位置：`<table id="dg"></table>  `

然后通过 js 来请求服务器动态的添加：

``` javascript
$('#dg').datagrid({    
  url:'datagrid_data.json',    
  columns:[[    
    {field:'code',title:'代码',width:100},    
    {field:'name',title:'名称',width:100,formatter:formatDate},    
    {field:'price',title:'价格',width:100,align:'right'}    
  ]]    
});

// 格式化的函数
function formatDate(val,row){
  var now = new Date(val);
  return now.format("yyyy-MM-dd hh:mm:ss");
}

//扩展Date的format方法   
Date.prototype.format = function (format) {  
  var o = {  
    "M+": this.getMonth() + 1,  
    "d+": this.getDate(),  
    "h+": this.getHours(),  
    "m+": this.getMinutes(),  
    "s+": this.getSeconds(),  
    "q+": Math.floor((this.getMonth() + 3) / 3),  
    "S": this.getMilliseconds()  
  }  
  if (/(y+)/.test(format)) {  
    format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));  
  }  
  for (var k in o) {  
    if (new RegExp("(" + k + ")").test(format)) {  
      format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));  
    }  
  }  
  return format;  
} 
```

如果有时间，很可能是时间戳的形式，只能自己用 **formatter** 属性来指定一个格式化函数自己处理。

再来展示下 JSON 的格式：

``` json
{"total":28,"rows":[
	{"productid":"FI-SW-01","productname":"Koi","unitcost":10.00,"status":"P","listprice":36.50,"attr1":"Large","itemid":"EST-1"},
	{"productid":"K9-DL-01","productname":"Dalmation","unitcost":12.00,"status":"P","listprice":18.50,"attr1":"Spotted Adult Female","itemid":"EST-10"}
]}
```

total 来记录总条数，rows 放的是一个个的“实体类”；如果使用了分页，往服务器传的当前页的 key 默认是 page