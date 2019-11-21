# 布局首选方案之Flex布局

某天在网上看到了一篇帖子有感而发，作为后端还是挺喜欢 JS 的，毕竟动态语言确实爽，不过 CSS 我就....心累，真的玩不起，我们称之为玄学。

当我还以为盒子模型是主流时，评论区大呼 Flex 布局真香，而我就一脸懵逼了，CSS3 一直不怨触碰，但如果主流布局方式变了，那肯定是要学习一下的（当然还有一种是  grid  这个看着比较复杂，之后再说）。

## Flex布局简介

布局的传统解决方案，基于盒状模型，依赖 display 属性 + position 属性 + float 属性。它对于那些特殊布局非常不方便，比如，垂直居中就不容易实现。

2009 年，W3C 提出了一种新的方案---- Flex 布局，可以简便、完整、响应式地实现各种页面布局。目前，它已经得到了所有浏览器的支持，这意味着，现在就能很安全地使用这项功能。 

 Flex 是 Flexible Box 的缩写，意为"弹性布局"，用来为盒状模型提供最大的灵活性。 

``` css
/* 任何一个容器都可以指定为 Flex 布局。 */
.box{
  display: flex;
}

/* 行内元素也可以使用 Flex 布局。*/
.box{
  display: inline-flex;
}

/* Webkit 内核的浏览器，必须加上 -webkit 前缀。 */
.box{
  display: -webkit-flex; /* Safari */
  display: flex;
}
```

**注意，设为 Flex 布局以后，子元素的 float、clear 和 vertical-align 属性将失效。**

## 基本概念

采用 Flex 布局的元素，称为 Flex 容器（flex container），简称"容器"。它的所有子元素自动成为容器成员，称为 Flex 项目（flex item），简称"项目"。 

 ![img](http://www.ruanyifeng.com/blogimg/asset/2015/bg2015071004.png) 

容器默认存在两根轴：水平的主轴（main axis）和垂直的交叉轴（cross axis）。主轴的开始位置（与边框的交叉点）叫做 `main start`，结束位置叫做 `main end`；交叉轴的开始位置叫做 `cross start`，结束位置叫做 `cross end`。

项目默认沿主轴排列。单个项目占据的主轴空间叫做 `main size`，占据的交叉轴空间叫做 `cross size`。

## 容器的属性

以下6个属性设置在容器上。

- flex-direction
- flex-wrap
- flex-flow
- justify-content
- align-items
- align-content

第一次看到这些还是很陌生的，毕竟之前没接触过 Flex 布局，虽然它早就已经有了，下面就详细解释下这些属性。

### flex-direction(主轴方向)

`flex-direction` 属性决定主轴的方向（即项目的排列方向），它可能有 4 个值：

- `row`（默认值）：主轴为水平方向，起点在左端。
- `row-reverse`：主轴为水平方向，起点在右端。
- `column`：主轴为垂直方向，起点在上沿。
- `column-reverse`：主轴为垂直方向，起点在下沿。

无论是横向还是纵向，默认都是紧凑着来（顶格），也就是如果想要它居中排列，还需要另外的设置。

###  flex-wrap(如何换行)

默认情况下，项目都排在一条线（又称"轴线"）上。`flex-wrap` 属性定义，如果一条轴线排不下，如何换行， 它可能取三个值 ：

-  `nowrap`（默认）：不换行。 
-  `wrap`：换行，第一行在上方。 
-  `wrap-reverse`：换行，第一行在下方。 

简单说就是定义是否换行，和换行的方向，第一行在下面还是上面的问题。

### flex-flow(聚合属性)

`flex-flow` 属性是 `flex-direction` 属性和 `flex-wrap` 属性的简写形式，默认值为 `row nowrap`。 

就是把前面两个属性值写在一起了，CSS 中很常见，下一个！

### justify-content(主轴对齐方式)

`justify-content`  属性定义了项目在主轴上的对齐方式。  它可能取 5 个值，**具体对齐方式与轴的方向有关**。下面假设主轴为从左到右。 

- `flex-start`（默认值）：左对齐
- `flex-end`：右对齐
- `center`： 居中
- `space-between`：两端对齐，项目之间的间隔都相等。
- `space-around`：每个项目两侧的间隔相等。所以，项目之间的间隔比项目与边框的间隔大一倍。

如果主轴是从上到下，那么就不是左右对齐的问题了，就是上下了，属性也没用使用 left 和 right 这类词，而是 start 和 end，配合之前的 Flex 基本模型，还是非常好理解的；同时，也是垂直居中的一种方案。

### align-items(交叉轴对齐方式)

`align-items` 属性定义项目在交叉轴（默认主轴左右，交叉轴上下）上如何对齐。  它可能取 5 个值。具体的对齐方式**与交叉轴的方向有关**，下面假设交叉轴从上到下。 

- `flex-start`：交叉轴的起点对齐，即所有项目靠上。
- `flex-end`：交叉轴的终点对齐，即所有项目靠下。
- `center`：交叉轴的中点对齐，即所有项目排列在中间，垂直居中。
- `baseline`: 项目的第一行文字的基线对齐。
- `stretch`（默认值）：如果项目未设置高度或设为 auto，将占满整个容器的高度。

这个属性也是垂直居中的一种方案，它决定了项目在容器中垂直方向上的位置（默认情况下）

### align-content(多根轴线对齐方式)

`align-content` 属性定义了**多根轴线**的对齐方式。如果项目只有一根轴线，该属性不起作用，该属性可能取 6 个值：

- `flex-start`：与交叉轴的起点对齐。
- `flex-end`：与交叉轴的终点对齐。
- `center`：与交叉轴的中点对齐。
- `space-between`：与交叉轴两端对齐，轴线之间的间隔平均分布。
- `space-around`：每根轴线两侧的间隔都相等。所以，轴线之间的间隔比轴线与边框的间隔大一倍。
- `stretch`（默认值）：轴线占满整个交叉轴。

 这个属性看似跟 align-items 没啥区别，区别就仅仅是 align-content 当项目只有一根轴线，该属性不起作用；

也就是说，align-items 它是用来让每一个**单行的**容器居中而不是让整个容器居中；align-content 属性只适用于多行的 flex 容器，并且会把多行作为一个整体，它们之间没有间隙。

简单讲，关键还是看是不是多行。

## 项目的属性

以上的六个属性全部是用在容器上的，相应的项目也有六个可配属性：

- order
- flex-grow
- flex-shrink
- flex-basis
- flex
- align-self

同样下面详细解释一下这几个属性

### order(项目排序)

`order` 属性定义项目的排列顺序。**数值越小，排列越靠前**，默认为 0。 

### flex-grow(放大比例)

`flex-grow` 属性定义项目的放大比例，默认为 `0`，即如果存在剩余空间，也不放大。

如果所有项目的 `flex-grow` 属性都为 1，则它们将等分剩余空间（如果有的话）。如果一个项目的 `flex-grow` 属性为 2，其他项目都为 1，则前者占据的剩余空间将比其他项多一倍。

### flex-shrink(缩放比例)

`flex-shrink` 属性定义了项目的缩小比例，默认为 1，即如果空间不足，该项目将缩小。 

如果所有项目的 `flex-shrink` 属性都为 1，当空间不足时，都将等比例缩小。如果一个项目的 `flex-shrink` 属性为 0，其他项目都为 1，则空间不足时，**前者不缩小**。负值对该属性无效。

### flex-basis(占位大小)

`flex-basis` 属性定义了在分配多余空间之前，项目占据的主轴空间（main size）。**浏览器根据这个属性，计算主轴是否有多余空间**。它的默认值为 `auto`，即项目的本来大小。

 它可以设为跟 `width` 或 `height` 属性一样的值（比如 350px），则项目将占据固定空间。

### flex(聚合属性)

`flex` 属性是 `flex-grow`, `flex-shrink` 和 `flex-basis` 的简写，默认值为 `0 1 auto`。后两个属性可选。 

该属性有两个快捷值：auto (`1 1 auto`) 和 none (`0 0 auto`)。

建议优先使用这个属性，而不是单独写三个分离的属性，因为浏览器会推算相关值。

### align-self(独立对齐方式)

`align-self` 属性允许单个项目有与其他项目不一样的对齐方式，可覆盖 `align-items` 属性。默认值为 `auto`，表示继承父元素的 `align-items` 属性，如果没有父元素，则等同于 `stretch`。 

 该属性可能取 6 个值，除了 auto，其他都与 align-items 属性完全一致。 

## 参考

https://www.ruanyifeng.com/blog/2015/07/flex-grammar.html

示例：http://www.ruanyifeng.com/blog/2015/07/flex-examples.html