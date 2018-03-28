---
title: Android样式开发汇总(一)
date: 2016-08-03 13:03:18
tags: [Android,UI布局]
categories: Android
---

转载自 Keegan小钢 (有改动)
原文地址:http://keeganlee.me/post/android/20150830
强烈建议阅读原文，写的非常棒，不知什么原因作者博客地址有时候会崩，所以也就转过来了，以后查看也比较方便。

本篇是对原作者的内容进行整合，所以内容非常多，很长，只是为了日后找资料方便，还是建议去原作者学习。

<!-- more -->

> 一个应用，应该保持一套统一的样式，包括Button、EditText、ProgressBar、Toast、Checkbox等各种控件的样式，还包括控件间隔、文字大小和颜色、阴影等等。web的样式用css来定义，而android的样式主要则是通过shape、selector、layer-list、level-list、style、theme等组合实现。

## Shape

很显然这是个定义形状的，也是最基础的，就先从它开始吧。
一般用shape定义的xml文件存放在drawable目录下，使用shape可以自定义形状，可以定义下面四种类型的形状，通过android:shape属性指定：

- rectangle: 矩形，默认的形状，可以画出直角矩形、圆角矩形、弧形等
- oval: 椭圆形，用得比较多的是画正圆
- line: 线形，可以画实线和虚线
- ring: 环形，可以画环形进度条

### rectangle-矩形

rectangle是默认的形状，也是用得最多的形状，一些文字背景、按钮背景、控件或布局背景等，以下是一些简单的例子：

![img](http://obb857prj.bkt.clouddn.com/rectangle.jpg)

下面是属性解释(都是以标签的形式)：

- **solid**: 设置形状填充的颜色，只有android:color一个属性
  - *android:color* 填充的颜色
- **padding**: 设置内容与形状边界的内间距，可分别设置左右上下的距离
  - *android:left* 左内间距
  - *android:right* 右内间距
  - *android:top* 上内间距
  - *android:bottom* 下内间距
- **gradient**: 设置形状的渐变颜色，可以是线性渐变、辐射渐变、扫描性渐变
  - *android:type* 渐变的类型*linear* 线性渐变，默认的渐变类型*radial* 放射渐变，设置该项时，android:gradientRadius也必须设置*sweep* 扫描性渐变
  - *android:startColor* 渐变开始的颜色
  - *android:endColor* 渐变结束的颜色
  - *android:centerColor* 渐变中间的颜色
  - *android:angle* 渐变的角度，线性渐变时才有效，必须是45的倍数，0表示从左到右，90表示从下到上
  - *android:centerX* 渐变中心的相对X坐标，放射渐变时才有效，在0.0到1.0之间，默认为0.5，表示在正中间
  - *android:centerY* 渐变中心的相对X坐标，放射渐变时才有效，在0.0到1.0之间，默认为0.5，表示在正中间
  - *android:gradientRadius* 渐变的半径，只有渐变类型为radial时才使用
  - *android:useLevel* 如果为true，则可在LevelListDrawable中使用
- **corners**: 设置圆角，只适用于rectangle类型，可分别设置四个角不同半径的圆角，当设置的圆角半径很大时，比如200dp，就可变成弧形边了
  - *android:radius* 圆角半径，会被下面每个特定的圆角属性重写
  - *android:topLeftRadius* 左上角的半径
  - *android:topRightRadius* 右上角的半径
  - *android:bottomLeftRadius* 左下角的半径
  - *android:bottomRightRadius* 右下角的半径
- **stroke**: 设置描边，可描成实线或虚线。
  - *android:color* 描边的颜色
  - *android:width* 描边的宽度
  - *android:dashWidth* 设置虚线时的横线长度
  - *android:dashGap* 设置虚线时的横线之间的距离

下面看下具体是怎么在XML文件中使用的：

```html
<?xml version="1.0" encoding="utf-8"?>
<!-- android:shape指定形状类型，默认为rectangle -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <!-- solid指定形状的填充色，只有android:color一个属性 -->
    <solid android:color="#2F90BD" />
    <!-- padding设置内容区域离边界的间距 -->
    <padding
        android:bottom="12dp"
        android:left="12dp"
        android:right="12dp"
        android:top="12dp" />
    <!-- corners设置圆角，只适用于rectangle -->
    <corners android:radius="200dp" />
    <!-- stroke设置描边 -->
    <stroke
        android:width="2dp"
        android:color="@android:color/darker_gray"
        android:dashGap="4dp"
        android:dashWidth="4dp" />
</shape>
```

然后就是view控件具体怎么引用呢，这里以textview为例：

```html
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    android:text="加了虚线描边的矩形"
    android:textSize="16sp"
    android:textColor="@android:color/white"
    android:background="@drawable/定义的shape文件名" /> 
```

### Oval-椭圆

oval用来画椭圆，而在实际应用中，更多是画正圆，比如消息提示，圆形按钮等，下图是一些例子的预览：

![](http://obb857prj.bkt.clouddn.com/oval.jpeg)

上面的效果图应用了solid、padding、stroke、gradient、size几个特性。上面已经说过了，size是用来设置形状大小的，如下：

- **size**: 设置形状默认的大小，可设置宽度和高度*android:width* 宽度*android:height* 高度

数字0是默认的椭圆，只加了solid填充颜色，数字1则加了上下左右4dp的padding，后面的数字都是正圆，是通过设置size的同样大小的宽高实现的，也可以通过设置控件的宽高一致大小来实现。数字3加了描边，数字4是镂空描边，数字5是虚线描边，数字6用了radial渐变。
注意，使用radial渐变时，必须指定渐变的半径，即android:gradientRadius属性。
下面是关于渐变的一个实例：

```html
<?xml version="1.0" encoding="utf-8"?>
<!-- android:shape指定形状类型，默认为rectangle -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval">
    <!-- padding设置内间距 -->
    <padding
        android:bottom="4dp"
        android:left="4dp"
        android:right="4dp"
        android:top="4dp" />
    <!-- size设置形状的大小 -->
    <size
        android:width="40dp"
        android:height="40dp" />
    <!-- gradient设置渐变 -->
    <gradient
        android:endColor="#000000"
        android:gradientRadius="40dp"
        android:startColor="#FFFFFF"
        android:type="radial" />
</shape>
```

然后是引用代码，和上面是一样的，也是作为 android:background 的值来引用。

### line-线形

照例先上一个预览图:

![](http://obb857prj.bkt.clouddn.com/Line.jpeg)

line主要用于画分割线，是通过stroke和size特性组合来实现的，先看虚线的代码：

```html
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="line">
    <!-- 实际显示的线 -->
    <stroke
        android:width="1dp"
        android:color="#2F90BD"
        android:dashGap="2dp"
        android:dashWidth="4dp" />
    <!-- 形状的高度 -->
    <size android:height="4dp" />
</shape>
```

画线时，有几点特性必须要知道的：

1. 只能画水平线，画不了竖线；
2. 线的高度是通过stroke的android:width属性设置的；
3. size的android:height属性定义的是整个形状区域的高度；
4. size的height必须大于stroke的width，否则，线无法显示；
5. 线在整个形状区域中是居中显示的；
6. 线左右两边会留有空白间距，线越粗，空白越大；
7. 引用虚线的view需要添加属性android:layerType，值设为"software"，否则显示不了虚线。

### ring-环形

首先，shape根元素有些属性只适用于ring类型，先过目下这些属性吧：

- *android:innerRadius* 内环的半径
- *android:innerRadiusRatio* 浮点型，以环的宽度比率来表示内环的半径，默认为3，表示内环半径为环的宽度除以3，该值会被android:innerRadius覆盖
- *android:thickness* 环的厚度
- *android:thicknessRatio* 浮点型，以环的宽度比率来表示环的厚度，默认为9，表示环的厚度为环的宽度除以9，该值会被android:thickness覆盖
- *android:useLevel* 一般为false，否则可能环形无法显示，只有作为LevelListDrawable使用时才设为true


![](http://obb857prj.bkt.clouddn.com/ring.jpeg)

第一个图只添加了solid；第二个图只添加了gradient，类型为sweep；第三个图只添加了stroke；第四个图添加了gradient和stroke两项特性。
以下为第四个图的代码：

```html
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:innerRadiusRatio="3"
    android:shape="ring"
    android:thicknessRatio="9"
    android:useLevel="false">
    <gradient
        android:endColor="#2F90BD"
        android:startColor="#FFFFFF"
        android:type="sweep" />
    <stroke
        android:width="1dp"
        android:color="@android:color/black" />
</shape>
```

如果想让这个环形旋转起来，变成可用的进度条，则只要在shape外层包多一个rotate元素就可以了。

```html
<?xml version="1.0" encoding="utf-8"?>
<rotate xmlns:android="http://schemas.android.com/apk/res/android"
    android:fromDegrees="0"
    android:pivotX="50%"
    android:pivotY="50%"
    android:toDegrees="1080.0">
    <shape
        android:innerRadiusRatio="3"
        android:shape="ring"
        android:thicknessRatio="8"
        android:useLevel="false">
        <gradient
            android:endColor="#2F90BD"
            android:startColor="#FFFFFF"
            android:type="sweep" />
    </shape>
</rotate>
```

关于什么是rotate标签，在View Animation(动画)会讲解到。
最后附上演示源码地址:https://github.com/keeganlee/kstyle.git

## Selector

shape只能定义单一的形状，而实际应用中，很多地方比如按钮、Tab、ListItem等都是不同状态有不同的展示形状。举个例子，一个按钮的背景，默认时是一个形状，按下时是一个形状，不可操作时又是另一个形状。有时候，不同状态下改变的不只是背景、图片等，文字颜色也会相应改变。而要处理这些不同状态下展示什么的问题，就要用selector来实现了。

selector标签，可以添加一个或多个item子标签，而相应的状态是在item标签中定义的。定义的xml文件可以作为两种资源使用：drawable和color。作为drawable资源使用时，一般和shape一样放于**drawable**目录下，item必须指定**android:drawable**属性；作为color资源使用时，则放于**color**目录下，item必须指定**android:color**属性。

那么，看看都有哪些**状态**可以设置呢：

- **android:state_enabled**: 设置触摸或点击事件是否可用状态，一般只在false时设置该属性，表示不可用状态
- **android:state_pressed**: 设置是否按压状态，一般在true时设置该属性，表示已按压状态，默认为false
- **android:state_selected**: 设置是否选中状态，true表示已选中，false表示未选中
- **android:state_checked**: 设置是否勾选状态，主要用于CheckBox和RadioButton，true表示已被勾选，false表示未被勾选
- **android:state_checkable**: 设置勾选是否可用状态，类似state_enabled，只是state_enabled会影响触摸或点击事件，而state_checkable影响勾选事件
- **android:state_focused**: 设置是否获得焦点状态，true表示获得焦点，默认为false，表示未获得焦点
- **android:state_window_focused**: 设置当前窗口是否获得焦点状态，true表示获得焦点，false表示未获得焦点，例如拉下通知栏或弹出对话框时，当前界面就会失去焦点；另外，ListView的ListItem获得焦点时也会触发true状态，可以理解为当前窗口就是ListItem本身
- **android:state_activated**: 设置是否被激活状态，true表示被激活，false表示未激活，API Level 11及以上才支持，可通过代码调用控件的setActivated(boolean)方法设置是否激活该控件
- **android:state_hovered**: 设置是否鼠标在上面滑动的状态，true表示鼠标在上面滑动，默认为false，API Level 14及以上才支持

接下来，看看示例代码，以下是bg_btn_selector.xml的代码，用于按钮的背景：

```html
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 当前窗口失去焦点时 -->
    <item android:drawable="@drawable/bg_btn_lost_window_focused" android:state_window_focused="false" />
    <!-- 不可用时 -->
    <item android:drawable="@drawable/bg_btn_disable" android:state_enabled="false" />
    <!-- 按压时 -->
    <item android:drawable="@drawable/bg_btn_pressed" android:state_pressed="true" />
    <!-- 被选中时 -->
    <item android:drawable="@drawable/bg_btn_selected" android:state_selected="true" />
    <!-- 被激活时 -->
    <item android:drawable="@drawable/bg_btn_activated" android:state_activated="true" />
    <!-- 默认时 -->
    <item android:drawable="@drawable/bg_btn_normal" />
</selector>
```

而下面则是text_btn_selector.xml的代码，用于按钮的文本颜色：

```html
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 当前窗口失去焦点时 -->
    <item android:color="@android:color/black" android:state_window_focused="false" />
    <!-- 不可用时 -->
    <item android:color="@android:color/background_light" android:state_enabled="false" />
    <!-- 按压时 -->
    <item android:color="@android:color/holo_blue_light" android:state_pressed="true" />
    <!-- 被选中时 -->
    <item android:color="@android:color/holo_green_dark" android:state_selected="true" />
    <!-- 被激活时 -->
    <item android:color="@android:color/holo_green_light" android:state_activated="true" />
    <!-- 默认时 -->
    <item android:color="@android:color/white" />
</selector>
```

最后，则是在控件中的引用：

```html
<Button 
    android:id="@+id/btn_default"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp"
    android:background="@drawable/bg_btn_selector"
    android:text="默认按钮"
    android:textColor="@color/text_btn_selector" />
```

那么，在使用过程中，有几点还是需要注意和了解的：

1. selector作为drawable资源时，item指定**android:drawable**属性，并放于**drawable**目录下；
2. selector作为color资源时，item指定**android:color**属性，并放于**color**目录下；
3. color资源也可以放于drawable目录，引用时则用**@drawable**来引用，但不推荐这么做，drawable资源和color资源最好还是分开；
4. **android:drawable**属性除了引用**@drawable**资源，也可以引用**@color**颜色值；但**android:color**只能引用**@color**；
5. **item是从上往下匹配的**，如果匹配到一个item那它就将采用这个item，而不是采用最佳匹配的规则；所以**设置默认的状态，一定要写在最后**，如果写在前面，则后面所有的item都不会起作用了。

另外，selector标签下有两个比较有用的属性要说一下，添加了下面两个属性之后，则会在状态改变时出现淡入淡出效果，但必须在API Level 11及以上才支持：

- **android:enterFadeDuration** 状态改变时，新状态展示时的淡入时间，以毫秒为单位
- **android:exitFadeDuration** 状态改变时，旧状态消失时的淡出时间，以毫秒为单位

### 特殊-ListView的ListItem样式

最后，关于ListView的ListItem样式，有两种设置方式，一种是在ListView标签里设置**android:listSelector**属性，另一种是在ListItem的布局layout里设置**android:background**。但是，这两种设置的结果却有着不同。同时，使用ListView时也有些其他需要注意的地方，总结如下：

1. **android:listSelector**设置的ListItem默认背景是透明的，不管你在selector里怎么设置都无法改变它的背景。所以，如果想改ListItem的默认背景，只能通过第二种方式，在ListItem的布局layout里设置**android:background**。

2. 当触摸点击ListItem时，第一种设置方式下，**state_pressed**、**state_focused**和**state_window_focused**设为true时都会触发，而第二种设置方式下，只有**state_pressed**会触发。

3. 当ListItem里有Button或CheckBox之类的控件时，会抢占ListItem本身的焦点，导致ListItem本身的触摸点击事件会无效。那么，要解决此问题，有三种解决方案：

   - 将Button或CheckBox换成TextView或ImageView之类的控件
   - 设置Button或CheckBox之类的控件设置**focusable**属性为false
   - 设置ListItem的根布局属性**android:descendantFocusability="blocksDescendants"**

   第三种是最方便，也是推荐的方式，它会将ListItem根布局下的所有子控件都设置为不能获取焦点。**android:descendantFocusability**属性的值有三种，其中，ViewGroup是指设置该属性的View，本例中就是ListItem的根布局：

   - **beforeDescendants**：ViewGroup会优先其子类控件而获取到焦点
   - **afterDescendants**：ViewGroup只有当其子类控件不需要获取焦点时才获取焦点
   - **blocksDescendants**：ViewGroup会覆盖子类控件而直接获得焦点

## layer-list

![](http://obb857prj.bkt.clouddn.com/layer-list.jpeg)

上图Tab的背景效果，和带阴影的圆角矩形，是怎么实现的呢？大部分的人会让美工切图，用点九图做背景。但是，如果只提供一张图，会怎么样呢？比如，中间的Tab背景红色底线的像素高度为4px，那么，在mdpi设备上显示会符合预期，在hdpi设备上显示时会细了一点点，在xhdpi设备上显示时会再细一点，在xxhdpi上显示时又细了，在xxxhdpi上显示时则更细了。因为在xxxhdpi上，1dp=4px，所以，4px的图，在xxxhdpi设备上显示时，就只剩下1dp了。所以，为了适配好各种分辨率，必须提供相应的多套图片。如果去查看android的res源码资源，也会发现，像这种Tab的背景点九图，也根据不同分辨率尺寸提供了不同尺寸的点九图片。

但是，在这个demo里，都没有用到任何实际的图片资源，都是用shape、selector，以及本篇要讲解的layer-list完成的。

**使用layer-list可以将多个drawable按照顺序层叠在一起显示**，像上图中的Tab，是由一个红色的层加一个白色的层叠在一起显示的结果，阴影的圆角矩形则是由一个灰色的圆角矩形叠加上一个白色的圆角矩形。先看下代码吧，以下是Tab背景的代码：

```html
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 第一种加载方式 -->
    <!--<item android:drawable="@drawable/bg_tab_selected" android:state_checked="true" />-->
    <!-- 第二种加载方式 -->
    <item android:state_checked="true">
        <layer-list>
            <!-- 红色背景 -->
            <item>
                <color android:color="#E4007F" />
            </item>
            <!-- 白色背景 -->
            <item android:bottom="4dp" android:drawable="@android:color/white" />
        </layer-list>
    </item>
    <!-- 默认的样式 -->
    <item>
        <layer-list>
            <!-- 红色背景 -->
            <item>
                <color android:color="#E4007F" />
            </item>
            <!-- 白色背景 -->
            <item android:bottom="1dp" android:drawable="@android:color/white" />
        </layer-list>
    </item>
</selector>
```

以下是带阴影的圆角矩形：

```html
<?xml version="1.0" encoding="utf-8"?>
<layer-list xmlns:android="http://schemas.android.com/apk/res/android">
    <!-- 灰色阴影 -->
    <item
        android:left="2dp"
        android:top="4dp">
        <shape>
            <solid android:color="@android:color/darker_gray" />
            <corners android:radius="10dp" />
        </shape>
    </item>
    <!-- 白色前景 -->
    <item
        android:bottom="4dp"
        android:right="2dp">
        <shape>
            <solid android:color="#FFFFFF" />
            <corners android:radius="10dp" />
        </shape>
    </item>
</layer-list>
```

从上面的示例代码可以看到，layer-list可以作为根节点，也可以作为selector中item的子节点。layer-list可以添加多个item子节点，每个item子节点对应一个drawable资源，**按照item从上到下的顺序叠加在一起**，再通过设置每个item的偏移量就可以看到阴影等效果了。layer-list的item可以通过下面四个属性设置偏移量：

- android:top 顶部的偏移量
- android:bottom 底部的偏移量
- android:left 左边的偏移量
- android:right 右边的偏移量

这四个偏移量和控件的margin设置差不多，都是外间距的效果。如何不设置偏移量，前面的图层就完全挡住了后面的图层，从而也看不到后面的图层效果了。比如上面的例子，Tab背景中的白色背景设置了android:bottom之后才能看到一点红色背景。那么如果偏移量设为负值会怎么样呢？经过验证，偏移超出的部分会被截掉而看不到，不信可以自己试一下。有时候这很有用，比如当我想显示一个半圆的时候。

另外，关于item的用法，也做下总结：

1. 根节点不同时，可设置的属性是会不同的，比如selector下，可以设置一些状态属性，而在layer-list下，可以设置偏移量；
2. 就算父节点同样是selector，放在drawable目录和放在color目录下可用的属性也会不同，比如drawable目录下可用的属性为android:drawable，在color目录下可用的属性为android:color；
3. item的子节点可以为任何类型的drawable类标签，除了上面例子中的shape、color、layer-list，也可以是selector，还有其他没讲过的bitmap、clip、scale、inset、transition、rotate、animated-rotate、lever-list等等。

## drawable汇总

### 普通图片

图片是最常用的drawable资源，格式包括：png(推荐)、jpg(可接受)、gif(不建议)。用图片资源需要根据不同屏幕密度提供多张不同尺寸的图片，它们的关系如下表：

| 密度分类    | 密度值范围      | 代表分辨率       | 图标尺寸      | 图片比例 |
| ------- | ---------- | ----------- | --------- | ---- |
| mdpi    | 120~160dpi | 320x480px   | 48x48px   | 1    |
| hdpi    | 160~240dpi | 480x800px   | 72x72px   | 1.5  |
| xhdpi   | 240~320dpi | 720x1280px  | 96x96px   | 2    |
| xxhdpi  | 320~480dpi | 1080x1920px | 144x144px | 3    |
| xxxhdpi | 480~640dpi | 1440x2560px | 192x192px | 4    |

本来还有一个ldpi的，但现在这种小屏幕的设备基本灭绝了，所以不需要再考虑适配。如上表所示，一套图片一般需要提供5张不同比例的图片。还好有切图工具，可以让切图变得简单，这里推荐两款：Cutterman和Cut&Slice me，都是Photoshop下的插件，输出支持android、ios和web三种平台。
使用切图工具虽然方便了，但还是无法避免一套图片需要提供多张不同尺寸的图片，这会加大安装包的大小。另外，需要对图片做改动时，比如换个颜色，必须更换所有尺寸图片。所以，建议尽量减少引入图片，而通过使用shape、layer-list等自己画，易于修改和维护，也减少了安装包大小，适配性也更好。

### bitmap标签

可以通过bitmap标签对图片做一些设置，如平铺、拉伸或保持图片原始大小，也可以指定对齐方式。看看bitmap标签的一些属性吧：

- **android:src** 必填项，指定图片资源，只能是图片，不能是xml定义的drawable资源
- **android:gravity** 设置图片的对齐方式，比如在layer-list中，默认会尽量填满整个视图，导致图片可能会被拉伸，为了避免被拉伸，就可以设置对齐方式，可取值为下面的值，多个取值可以用 | 分隔：
  - *top* 图片放于容器顶部，不改变图片大小
  - *bottom* 图片放于容器底部，不改变图片大小
  - *left* 图片放于容器左边，不改变图片大小
  - *right* 图片放于容器右边，不改变图片大小
  - *center* 图片放于容器中心位置，包括水平和垂直方向，不改变图片大小
  - *fill* 拉伸整张图片以填满容器的整个高度和宽度，默认值
  - *center_vertical* 图片放于容器垂直方向的中心位置，不改变图片大小
  - *center_horizontal* 图片放于容器水平方向的中心位置，不改变图片大小
  - *fill_vertical* 在垂直方向上拉伸图片以填满容器的整个高度
  - *fill_horizontal* 在水平方向上拉伸图片以填满容器的整个宽度
  - *clip_vertical* 附加选项，裁剪基于垂直方向的gravity设置，设置top时会裁剪底部，设置bottom时会裁剪顶部，其他情况会同时裁剪顶部和底部
  - *clip_horizontal* 附加选项，裁剪基于水平方向的gravity设置，设置left时会裁剪右侧，设置right时会裁剪左侧，其他情况会同时裁剪左右两侧
- **android:antialias** 设置是否开启抗锯齿
- **android:dither** 设置是否抖动，图片与屏幕的像素配置不同时会用到，比如图片是ARGB 8888的，而屏幕是RGB565
- **android:filter** 设置是否允许对图片进行滤波，对图片进行收缩或者延展使用滤波可以获得平滑的外观效果
- **android:tint** 给图片着色，比如图片本来是黑色的，着色后可以变成白色
- **android:tileMode** 设置图片平铺的方式，取值为下面四种之一：
  - *disable* 不做任何平铺，默认设置
  - *repeat* 图片重复铺满
  - *mirror* 使用交替镜像的方式重复图片的绘制
  - *clamp* 复制图片边缘的颜色来填充容器剩下的空白部分，比如引入的图片如果是白色的边缘，那么图片所在的容器里除了图片，剩下的空间都会被填充成白色
- **android:alpha** 设置图片的透明度，取值范围为0.0~1.0之间，0.0为全透明，1.0为全不透明，API Level最低要求是11，即Android 3.0
- **android:mipMap** 设置是否可以使用mipmap，但API Level最低要求是17，即Android 4.2
- **android:autoMirrored** 设置图片是否需要镜像反转，当布局方向是RTL，即从右到左布局时才有用，API Level 19(Android 4.4)才添加的属性
- **android:tileModeX** 和tileMode一样设置图片的平铺方式，只是这个属性只设置水平方向的平铺方式，这是API Level 21(Android 5.0)才添加的属性
- **android:tileModeY** 和tileMode一样设置图片的平铺方式，只是这个属性只设置垂直方向的平铺方式，这是API Level 21(Android 5.0)才添加的属性
- **android:tintMode** 着色模式，也是API Level 21(Android 5.0)才添加的属性

### 点九图片

点九图片文件扩展名为：.9.png，通过点九图片可以做局部拉伸，比如，一张圆角矩形图片，我们不想让它的四个边角都被拉伸从而导致模糊失真，使用点九图就可以控制拉伸区域，让四个边角保持完美显示。效果如下图：
![img](http://keeganlee.me/android/_image/20150916/%E7%82%B9%E4%B9%9D%E6%95%88%E6%9E%9C%E5%9B%BE.jpg)

画点九图一般用Android SDK工具集里的draw9patch工具，只需要在四条边画黑线就可以了，如下图所示：
![img](http://keeganlee.me/android/_image/20150916/%E7%82%B9%E4%B9%9D%E5%9B%BE%E6%8B%89%E4%BC%B8%E5%8C%BA%E5%9F%9F%E5%9B%BE.jpg)
拉伸区域就是图片会被拉伸的部分，可以为1个点，也可以为一条线，甚至也可以为断开的几个点或几条线，总之，有黑点的地方就会被拉伸，没有黑点的地方就不会被拉伸。而显示内容区域其实就等于默认给使用的控件设置了padding，控件的内容只能显示在内容区域内。

### nine-patch标签

使用nine-patch标签可以对点九图片做一些设置处理，不过可设置的属性并不多：

- **android:src** 必填项，必须指定点九类型的图片
- **android:dither** 设置是否抖动，图片与屏幕的像素配置不同时会用到，比如图片是ARGB 8888的，而屏幕是RGB565
- **android:tint** 给图片着色，比如图片本来是黑色的，着色后可以变成白色
- **android:tintMode** 着色模式，API Level 21(Android 5.0)才添加的属性
- **android:alpha** 设置图片的透明度，取值范围为0.0~1.0之间，0.0为全透明，1.0为全不透明，API Level最低要求是11
- **android:autoMirrored** 设置图片是否需要镜像反转，当布局方向是RTL，即从右到左布局时才有用，API Level 19(Android 4.4)才添加的属性

### color标签

color标签是drawable里最简单的标签了，只有一个属性：**android:color**，指定颜色值。这个标签一般很少用，因为基本都可以通过其他更方便的方式定义颜色。另外，颜色值一般都在colors.xml文件中定义，其根节点为resources。看看两者的不同：

```html
<!-- 文件：res/drawable/white.xml -->
<color xmlns:android="http://schemas.android.com/apk/res/android"
    android:color="#FFFFFF" />
```

```html
<!-- 文件：res/values/colors.xml -->
<resources>
    <color name="white">#FFFFFF</color>
</resources>
```

引用的时候，前一种通过**@drawable/white**引用，后一种通过**@color/white**引用。

### inset标签

使用inset标签可以对drawable设置边距，其用法和View的padding类似，只不过padding是设置内容与边界的距离，而inset则可以设置背景drawable与View边界的距离。inset标签的可设置属性如下：

- **android:drawable** 指定drawable资源，如果不设置该属性，也可以定义drawable类型的子标签
- **android:visible** 设置初始的可见性状态，默认为false
- **android:insetLeft** 左边距
- **android:insetRight** 右边距
- **android:insetTop** 顶部边距
- **android:insetBottom** 底部边距
- **android:inset** 设置统一边距，会覆盖上面四个属性，但API Level要求为21，即Android 5.0

### clip标签

使用clip标签可以对drawable进行裁剪，在做进度条时很有用。通过设置level值控制裁剪多少，level取值范围为0~10000，默认为0，表示完全裁剪，图片将不可见；10000则完全不裁剪，可见完整图片。看看clip标签可以设置的属性：

- **android:drawable** 指定drawable资源，如果不设置该属性，也可以定义drawable类型的子标签
- **android:clipOrientation** 设置裁剪的方向，取值为以下两个值之一：
  - *horizontal* 在水平方向上进行裁剪，条状的进度条就是水平方向的裁剪
  - *vertical* 在垂直方向上进行裁剪
- **android:gravity** 设置裁剪的位置，可取值如下，多个取值用 | 分隔：
  - *top* 图片放于容器顶部，不改变图片大小。当裁剪方向为vertical时，会裁掉图片底部
  - *bottom* 图片放于容器底部，不改变图片大小。当裁剪方向为vertical时，会裁掉图片顶部
  - *left* 图片放于容器左边，不改变图片大小，默认值。当裁剪方向为horizontal，会裁掉图片右边部分
  - *right* 图片放于容器右边，不改变图片大小。当裁剪方向为horizontal，会裁掉图片左边部分
  - *center* 图片放于容器中心位置，包括水平和垂直方向，不改变图片大小。当裁剪方向为horizontal时，会裁掉图片左右部分；当裁剪方向为vertical时，会裁掉图片上下部分
  - *fill* 拉伸整张图片以填满容器的整个高度和宽度。这时候图片不会被裁剪，除非level设为了0，此时图片不可见
  - *center_vertical* 图片放于容器垂直方向的中心位置，不改变图片大小。裁剪和center时一样
  - *center_horizontal* 图片放于容器水平方向的中心位置，不改变图片大小。裁剪和center时一样
  - *fill_vertical* 在垂直方向上拉伸图片以填满容器的整个高度。当裁剪方向为vertical时，图片不会被裁剪，除非level设为了0，此时图片不可见
  - *fill_horizontal* 在水平方向上拉伸图片以填满容器的整个宽度。当裁剪方向为horizontal时，图片不会被裁剪，除非level设为了0，此时图片不可见
  - *clip_vertical* 附加选项，裁剪基于垂直方向的gravity设置，设置top时会裁剪底部，设置bottom时会裁剪顶部，其他情况会同时裁剪顶部和底部
  - *clip_horizontal* 附加选项，裁剪基于水平方向的gravity设置，设置left时会裁剪右侧，设置right时会裁剪左侧，其他情况会同时裁剪左右两侧

那怎么设置level呢？android没有提供直接在xml里设置level的属性，这需要通过代码去设置。举例用法如下：

1. 定义clip.xml：

   ```html
   <?xml version="1.0" encoding="utf-8"?>
   <clip xmlns:android="http://schemas.android.com/apk/res/android"
       android:clipOrientation="horizontal"
       android:drawable="@drawable/img4clip"
       android:gravity="left" />
   ```

2. 在ImageView中引用：

   ```html
   <ImageView
       android:id="@+id/img"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:background="@drawable/bg_img"
       android:src="@drawable/clip" />
   ```

3. 在代码中设置level：

   ```java
   ImageView img =  (ImageView) findViewById(R.id.img);
   img.getDrawable().setLevel(5000); //level范围值0~10000 
   ```

### scale标签

使用scale标签可以对drawable进行缩放操作，和clip一样是通过设置level来控制缩放的比例。scale标签可以设置的属性如下：

- **android:drawable** 指定drawable资源，如果不设置该属性，也可以定义drawable类型的子标签
- **android:scaleHeight** 设置可缩放的高度，用百分比表示，格式为XX%，0%表示不做任何缩放，50%表示只能缩放一半
- **android:scaleWidth** 设置可缩放的宽度，用百分比表示，格式为XX%，0%表示不做任何缩放，50%表示只能缩放一半
- **android:scaleGravity** 设置drawable缩放后的位置，取值和bitmap标签的一样，就不一一列举说明了，不过默认值是left
- **android:useIntrinsicSizeAsMinimum** 设置drawable原有尺寸作为最小尺寸，设为true时，缩放基本无效，API Level最低要求为11

使用的时候，和clip一样，用法如下：

1. 定义scale.xml：

   ```html
   <?xml version="1.0" encoding="utf-8"?>
   <scale xmlns:android="http://schemas.android.com/apk/res/android"
       android:drawable="@drawable/img4scale"
       android:scaleGravity="left"
       android:scaleHeight="50%"
       android:scaleWidth="50%"
       android:useIntrinsicSizeAsMinimum="false" />
   ```

2. 在ImageView中引用：

   ```html
   <ImageView
       android:id="@+id/img"
       android:layout_width="match_parent"
       android:layout_height="wrap_content"
       android:background="@drawable/bg_img"
       android:src="@drawable/scale" />
   ```

3. 在代码中设置level：

   ```java
   ImageView img =  (ImageView) findViewById(R.id.img);
   img.getDrawable().setLevel(5000); //level范围值0~10000 
   ```

### level-list标签

当需要在一个View中显示不同图片的时候，比如手机剩余电量不同时显示的图片不同，level-list就可以派上用场了。level-list可以管理一组drawable，每个drawable设置一组level范围，最终会根据level值选取对应的drawable绘制出来。level-list通过添加item子标签来添加相应的drawable，其下的item只有三个属性：

- **android:drawable** 指定drawable资源，如果不设置该属性，也可以定义drawable类型的子标签
- **android:minLevel** 该item的最小level值
- **android:maxLevel** 该item的最大level值

以下是示例代码：

```html
<?xml version="1.0" encoding="utf-8"?>
<level-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:drawable="@drawable/battery_low"
        android:maxLevel="10"
        android:minLevel="0" />
    <item
        android:drawable="@drawable/battery_below_half"
        android:maxLevel="50"
        android:minLevel="10" />
    <item
        android:drawable="@drawable/battery_over_half"
        android:maxLevel="99"
        android:minLevel="50" />
    <item
        android:drawable="@drawable/battery_full"
        android:maxLevel="100"
        android:minLevel="100" />
</level-list>
```

那么，当电量剩下10%时则可以设置level值为10，将会匹配第一张图片：

```java
img.getDrawable().setLevel(10);
```

item的匹配规则是从上到下的，当设置的level值与前面的item的level范围匹配，则采用。一般item的添加按maxLevel从小到大排序下来，此时minLevel可以不用指定也能匹配到。如上面代码就可以简化如下：

```html
<?xml version="1.0" encoding="utf-8"?>
<level-list xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:drawable="@drawable/battery_low"
        android:maxLevel="10" />
    <item
        android:drawable="@drawable/battery_below_half"
        android:maxLevel="50" />
    <item
        android:drawable="@drawable/battery_over_half"
        android:maxLevel="99" />
    <item
        android:drawable="@drawable/battery_full"
        android:maxLevel="100" />
</level-list>
```

但不能反过来将**android:maxLevel="100"**的item放在最前面，那样所有电量都只匹配第一条了。

### transition标签

transition其实是继承自layer-list的，只是，transition只能管理两层drawable，另外提供了两层drawable之间切换的方法，切换时还会有淡入淡出的动画效果。示例代码如下：

```html
<?xml version="1.0" encoding="utf-8"?>
<transition xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:drawable="@drawable/on" />
    <item android:drawable="@drawable/off" />
</transition>
```

transition标签生成的Drawable对应的类为TransitionDrawable，要切换时，需要主动调用TransitionDrawable的startTransition()方法，参数为动画的毫秒数，也可以调用reverseTransition()方法逆向切换。

```java
((TransitionDrawable)drawable).startTransition(500); //正向切换，即从第一个drawable切换到第二个
((TransitionDrawable)drawable).reverseTransition(500); //逆向切换，即从第二个drawable切换回第一个
```

### rotate标签

使用rotate标签可以对一个drawable进行旋转操作，在[shape篇](http://keeganlee.me/post/android/20150830)讲环形时最后举了个进度条时就用到了rotate标签。另外，比如你有一张箭头向上的图片，但你还需要一个箭头向下的图片，这时就可以使用rotate将向上的箭头旋转变成一张箭头向下的drawable。
先看看rotate标签的一些属性吧：

- **android:drawable** 指定drawable资源，如果不设置该属性，也可以定义drawable类型的子标签
- **android:fromDegrees** 起始的角度度数
- **android:toDegrees** 结束的角度度数，正数表示顺时针，负数表示逆时针
- **android:pivotX** 旋转中心的X坐标，浮点数或是百分比。浮点数表示相对于drawable的左边缘距离单位为px，如5; 百分比表示相对于drawable的左边缘距离按百分比计算，如5%; 另一种百分比表示相对于父容器的左边缘，如5%p; 一般设置为50%表示在drawable中心
- **android:pivotY** 旋转中心的Y坐标
- **android:visible** 设置初始的可见性状态，默认为false

示例代码如下，目标是将一张箭头向上的图片转180度，转成一张箭头向下的图片：

```html
<?xml version="1.0" encoding="utf-8"?>
<rotate xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/ic_arrow"
    android:fromDegrees="0"
    android:pivotX="50%"
    android:pivotY="50%"
    android:toDegrees="180" />
```

将它引用到ImageView里，发现图片根本没有转变。其实，要让它可以旋转，还需要设置level值。level取值范围为0~10000，应用到rotate，则与fromDegrees~toDegrees相对应，如上面例子的角度范围为0~180，那么，level取值0时，则旋转为0度；level为10000时，则旋转180度；level为5000时，则旋转90度。因为level默认值为0，所以图片没有转变。那么，我们想转180度，其实可以将fromDegrees设为180，而不设置toDegrees，这样，不用再在代码里设置level图片就可以旋转180了。

### animation-list标签

通过animation-list可以将一系列drawable构建成帧动画，就是将一个个drawable，一帧一帧的播放。通过添加item子标签设置每一帧使用的drawable资源，以及每一帧持续的时间。示例代码如下：

```html
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="false">
    <item
        android:drawable="@drawable/anim1"
        android:duration="1000" />
    <item
        android:drawable="@mipmap/anim2"
        android:duration="1000" />
    <item
        android:drawable="@mipmap/anim3"
        android:duration="1000" />
</animation-list>
```

**android:oneshot**属性设置是否循环播放，设为true时，只播放一轮就结束，设为false时，则会轮询播放。
**android:duration**属性设置该帧持续的时间，以毫秒数为单位。
animation-list对应的Drawable类为AnimationDrawable，要让动画运行起来，需要主动调用AnimationDrawable的start()方法。另外，如果在Activity的onCreate()方法里直接调用start()方法会没有效果，因为view还没有初始化完成是播放不了动画的。

### animated-rotate

rotate标签只是将原有的drawable转个角度变成另一个drawable，它是静态的。而animated-rotate则会让drawable不停地做旋转动画。
animated-rotate可设置的属性只有四个：

- **android:drawable** 指定drawable资源，如果不设置该属性，也可以定义drawable类型的子标签
- **android:pivotX** 旋转中心的X坐标
- **android:pivotY** 旋转中心的Y坐标
- **android:visible** 设置初始的可见性状态，默认为false

示例代码：

```html
<?xml version="1.0" encoding="utf-8"?>
<animated-rotate xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@drawable/img_daisy"
    android:pivotX="50%"
    android:pivotY="50%"
    android:visible="false" />
```

------

### 写在最后

至此，drawable资源基本都讲完了，但还不是全部，Android 5.0新增的几个标签：animated-selector、vector、animated-vector、ripple，因为还没弄清楚具体的用法，而且也涉及到Material Design，所以不在本篇讲解，后续做Material Design专题分享的时候会再详细讲解用法。
PS：selector标签下的item其实还可以添加set标签，这是添加动画集的标签，下一篇就将分享下一些常用动画的制作。

## 未完待续

本来是想写在一篇的，可惜看了看实在是太长了，还是分开吧，下面的就是动画和主题了，正好我也还没看，就先到这里吧。~~有时间再去学习~~