---
title: 让radiobutton的样式随是否选中而变化
date: 2016-06-14 17:10:50
tags: Android
categories: Android
---
最近在团队合作做一个项目，无意中发现了别的哥们写的这种用法，真是涨知识！
只要通过资源文件原来就能做到这样的效果，必须get！
<!-- more -->
关于这方面的知识已补充，详见[Android样式开发汇总](http://bfchengnuo.info/2016/08/03/Android%E6%A0%B7%E5%BC%8F%E5%BC%80%E5%8F%91%E6%B1%87%E6%80%BB-%E4%B8%80/)

## 布局文件配置
我们想要实现的是当点击不同的选项相应的图片已经字体的颜色就会变化，类似微信这样的~
![](http://o6lgtfj7v.bkt.clouddn.com/wechat.jpg)
于是我们在控件的drawableTop属性和textColor属性引用一个资源文件。
``` html
	……………………
<RadioButton
            android:id="@+id/rb_help"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:drawablePadding="3dp"
            android:drawableTop="@drawable/tab_help_ic_selector"
            android:gravity="center_horizontal"
            android:text="测试1"
            android:textColor="@drawable/tab_text_color_selector" />

        <RadioButton
            android:id="@+id/rb_good"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:drawablePadding="3dp"
            android:drawableTop="@drawable/tab_goods_ic_selector"
            android:gravity="center_horizontal"
            android:text="测试2"
            android:textColor="@drawable/tab_text_color_selector" />
```
## 资源文件
**tab_XXX_ic_selector图片之类的**
``` html
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:drawable="@drawable/tab_good_ic_click" android:state_checked="true" />
    <item android:drawable="@drawable/tab_good_ic" />

</selector>
```
第一个就是点击状态(获得焦点？)加载的，第二个就是未点击状态加载的

**tab_text_color_selector字体颜色**
``` html
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:state_checked="true" android:color="@color/main_hue"/>
    <item android:color="@color/black"/>

</selector>
```
大体和上面差不多，需要注意的是这里drawable变成了color。