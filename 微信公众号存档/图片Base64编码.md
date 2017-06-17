# 图片Base64编码

>   图片的 base64 编码就是可以将一副图片数据编码成一串字符串，使用该字符串代替图像地址。

有什么意义呢？

传统上使用 img 标签都是引用个地址，在浏览器解析的时候对单独发送一个请求来获取这个图片，这个过程需要时间，~~更重要的是需要服务器处理，这回造成一些服务器方面的压力~~

如果一些简单的图片可以直接随着 HTML 加载，那么既省了时间又省了一个请求；图片 Base64 编码就是来处理这个的

PS:关于什么是 Base64 编码前面有篇已经写的很清楚了

## 长什么样？

事件的起因是我在 澄空学园 看到了一副不错的图，于是想要扒下来，结果 F12 后是这么个样子：

![](../img/base_img.jpeg)

当时的我是一脸懵逼，这是什么玩意？虽然能看出是 Base64 编码，但是试着解析了下.....完全是乱码

后来知道了有图片 Base64 编码这个东西；它有两种形式，分别对应在 CSS 中、在 HTML 中

```css
body {
  background: url(data:image/gif;base64,R0lGODlhHAAmAKIHAKqqqsvLy0hISObm5vf394uLiwAAAP///yH5B…EoqQqJKAIBaQOVKHAXr3t7txgBjboSvB8EpLoFZywOAo3LFE5lYs/QW9LT1TRk1V7S2xYJADs=) no-repeat center;
}
```

HTML 中：

```html
<img src="data:image/gif;base64,R0lGODlhHAAmAKIHAKqqqsvLy0hISObm5vf394uLiwAAAP///yH5B…EoqQqJKAIBa...."/>
```

## 什么时候用？

猜也能猜到了，当然是图片**足够小！** ；貌似最好不用高于 4 KB ，要不然效果反而不好了....

 测试 3 K 左右的转换完后都有 4K 多个字符....这么多放进 CSS 文件，让那些写 CSS 的怎么维护，233333

所以说使用还是非常有限的，即使它能被 gzip 压缩

**总之就是：如果图片足够小且因为用处的特殊性无法被制作成雪碧图（CssSprites），在整个网站的复用性很高且基本不会被更新**。

---

不到 4 KB 的图片有什么卵用？额.....我不知道，虽然省了一次请求但是会拖渲染的速度....

## 如何转换？

最简单的方案就是把图片拖到 Chrome 里，然后 F12；切换到 Source ，就能看到 Base64 编码后的样子了

或者 Google 下，网上有一大堆在线转换的；不过 Base64 转图片的倒是不好找，我找到一个还不错的：

http://www.vgot.net/test/image2base64.php



最后，虽然这个使用非常有限，但是起码我知道了原来图片还可以这样玩！