终于还是开始使用 Vue 了，~~作为一个有志向的青年，当然是前后端通吃~~

这里记录一下我在使用 Vue 时遇到的一些样式问题。

PS： CSS 样式什么的最讨厌啦，让我写动画岂不是噩梦？相比之下还是更喜欢 JS 来

## 样式穿透

因为 scoped 的关系，本组件的样式是无法影响子组件的，但是很多时候我们就想重新定义子组件的样式。

之前学习的时候，css 使用的是 stylus，它的样式穿透写法是：

> 父样式名 >>> 子样式名

这个之前说过，但是这一次遇到的是 less 语法（sass 和 less 是一样的），上面的写法不支持，查了一下，是这样写：

> <strike>父样式名 /deep/ 子样式名 </strike>

see http://sfau.lt/b5be0Vr

#### 注意：
> sass 的 /deep/ 写法已经被废弃，高版本不再支持之前 sass 的那种 /deep/ 写法，需要统一改为 `::v-deep` 的写法，别担心，它是兼容 /deep/ 的。

## calc问题

在 CSS3 中提供了一个 calc 函数，关于它是干嘛的可以去 wiki 一下。

这里要说的是当我使用了 calc 函数后，结果死活不对，最后确定是因为语言设置成 less，它把表达式当作 less 语法给解析了。。。

``` css
.mkt-hover{
  width: calc(~"100% - 16px");
}
```

解决方案就是在表达式前面加一个 `~` 后面用字符串包裹，这样就不会给你解析了。
