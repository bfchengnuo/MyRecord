---
title: weight属性解析
date: 2016-07-13 15:28:33
tags: [Android,UI布局]
categories: Android
---
LinearLayout 是我们使用很频繁的一个布局，在控制控件位置的时候我们也经常使用它的weight属性，我只是无脑的设置其值为1，具体代表什么意思以及实现原理都不造，看书看到了这，顺便记录下。
<!-- more -->
搜索资料来看，weight属性只是支持LinearLayout的。

## weightSum属性和layout_weight 属性
不同的Android设备的尺寸往往是不尽相同，作为开发者我们应该要创建适配不同屏幕尺寸的XML文件，当然硬编码是不可取的。
我们利用weightSum属性和layout_weight 属性来进行控制，首先我们来看看android:weightSum是什么鬼：
>   定义weight总和的最大值，如果未指定，所有子视图的layout_weight的累加值作为总和的最大值。
>   一个典型的案例是：通过设置子视图的layout_weight为0.5，并设置LinearLayout的weightSum属性值为1.0，实现子视图占据可用宽度的50%。

我们可以想象出一个盒子，盒子的可用空间比例就是weightSum，盒子中每个物体可用的空间比例就是layout_weight。

下面是个按钮居中，宽度占据50%的例子:
``` html
	<?xml version="1.0" encoding="utf-8"?>
	<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
	    android:layout_width="match_parent"
	    android:layout_height="match_parent"
	    android:orientation="horizontal"
		android:gravity="center"
		android:weightSun="1">
		
	    <Button
	        android:layout_width="0dp"
	        android:layout_height="wrap_content"
	        android:text="Click me"
	        android:layout_weight="0.5"/>
	</LinearLayout>
```
## 实现-计算公式
至于为什么我们要把`android:layout_width"`设置为0dp呢，不只是官方推荐这样用，因为下面的一个计算公式。

下面我们以宽为200dp weightSum为1的LinearLayout为例分析下：
如上面，计算button宽度的公式为：
> btn's width + btn's weight * 200 /sum(weight)

我们指定button的宽度为0dp weight为0.5 sum为1，所以：
0 + 0.5*200/1 =100

**总结下就是：首先按照分配的长度来给予长度，剩余的长度再按权重的比例来分配。**