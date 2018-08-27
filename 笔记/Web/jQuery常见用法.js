// jQuery常见


// 查看版本号
$()['jquery']


// 遍历选择的元素，i-index=下标；e-element=当前遍历的元素
$('e').each(function(i,e){})
	
	
// 解绑、绑定事件，这种写法能保证只有一个，避免重复加载绑定多个事件
e.unbind('click').click(function(){});
	

// 创建一个 div 标签，在里面又写入了一个 p 标签
$('<div>').css({padding:'5px'}).html('<p>');


// 从某一个元素（e）中查找
$('ul',e)


// 执行函数，第一个参数一般是 this，从第二个开始就是函数所需要的参数
$('e').fun.call(this);


// 查找某个元素，可以使用一个表达式，也可以传入一个元素
$('e').find('[name=cid]').val('zzz')
	
	
// 向上查找最近的一个 form 元素，但是不包括它本身
$(this).parentsUntil("form")
$(this).parentsUntil("form").parent("form")


// 查找同级元素(div 中有个 class 为 pics)
e.siblings("div.pics")


	
// 技巧：
eval("1311")  可以从字符串转换为数字  // 本质是可计算某个字符串，并执行其中的的 JavaScript 代码。




// 这个很好用
$(this)