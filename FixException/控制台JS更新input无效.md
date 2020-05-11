# 控制台JS更新input无效

使用控制台来更新页面的 Input 的值，UI 正确更新，但是效果依然是旧值。

猜测与流行的 MVVM 框架双向绑定有关，很多页面想要实现自动赋值不再向以前那样对输入框简单的赋值即可，还需要对应的触发相关的事件让对应框架去取值才行。

---

大多数框架使用的是 input 事件监听，相比 change 的输入完成触发，input 是实时的；不过也不排除使用了其他事件。

网上查到的手动调用方案有：

``` js
var t = document.querySelectorAll('.bui-input-input')[2]

var evt = document.createEvent('HTMLEvents');
evt.initEvent('input', true, true);
t.value='setValue';
t.dispatchEvent(evt)


window.inputValue = function (dom, st) {
  var evt = new InputEvent('input', {
    inputType: 'insertText',
    data: st,
    dataTransfer: null,
    isComposing: false
  });
  dom.value = st;
  dom.dispatchEvent(evt);
}
```

我使用了一种简单粗暴的方式：

`t.dispatchEvent(new CustomEvent('input', {}))`

如果无效，尝试 change、blur 等其他事件。

---

另外，有强制开启 Vue DevTools 的方法。