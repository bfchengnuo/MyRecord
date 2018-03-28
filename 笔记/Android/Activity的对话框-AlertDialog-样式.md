---
title: Activity的对话框(AlertDialog)样式
date: 2016-06-21 18:56:33
tags: [Android,Activity]
categories: Android
---
## 前言
有些时候需要把一个activity当成一个dialog来使用，或者说需要一个dialog里面放一些布局或者其它控件，这时候就需要一个Dialog样式的Activity了。
<!-- more -->
## Java代码
这一部分和一般的activity没有什么差别，基本是保持一致的，但是我们会遇到几个问题：
1. dialog的位置要修改到底部；
2. dialog有默认的padding，要去这些padding，让dialog于屏幕同宽；（这个无法通过设置Gravity.width办到）
3. dialog的滑入和滑出动画；

对于前两个问题我们可以这样写：
``` java
Window window = this.getWindow();

//去掉dialog的title，要在setContentView（）前
window.requestFeature(Window.FEATURE_NO_TITLE);

setContentView(R.layout.layout_logout_confirm);

//去掉dialog默认的padding
window.getDecorView().setPadding(0, 0, 0, 0);

WindowManager.LayoutParams lp = window.getAttributes();
lp.width = WindowManager.LayoutParams.MATCH_PARENT;
lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

//设置dialog的位置在底部
lp.gravity = Gravity.BOTTOM;

window.setAttributes(lp);
```
## 资源文件编写
首先我们因为要用对话框类型的样式 所以我们自定义风格，让它继承style文件中添加：
``` html
<!--自定义的弹窗式activity-->
<style name="MyDialogStyle" parent="android:Theme.Dialog">
    <item name="android:windowAnimationStyle">@style/AnimBottom</item>
    <!-- 边框 -->
    <item name="android:windowFrame">@null</item>
    <!-- 是否浮现在activity之上 -->
    <item name="android:windowIsFloating">true</item>
    <!-- 半透明 -->
    <item name="android:windowIsTranslucent">true</item>
    <!-- 无标题 -->
    <item name="android:windowNoTitle">true</item>
    <!-- 背景透明 -->
    <item name="android:windowBackground">@android:color/transparent</item>
    <!-- 模糊 -->
    <item name="android:backgroundDimEnabled">true</item>
</style>
<!--引用动画效果-->
<style name="AnimBottom" parent="@android:style/Animation">
    <item name="android:windowEnterAnimation">@anim/push_bottom_in</item>
    <item name="android:windowExitAnimation">@anim/push_bottom_out</item>
</style>
```
关于动画的实现
``` html
<!-- 上下滑入式 -->
<set xmlns:android="http://schemas.android.com/apk/res/android" >

    <translate
        android:duration="200"
        android:fromYDelta="100%p"
        android:toYDelta="0"        
     />      
</set>

<!-- 上下滑出式 -->
<set xmlns:android="http://schemas.android.com/apk/res/android" >

    
    <translate
        android:duration="200"
        android:fromYDelta="0"
        android:toYDelta="50%p" />
</set>
```
## AndroidManifest文件配置
最后我们在AndroidManifest加入：
``` html
<activity
        android:name=".ui.activity.GoodKeypadActivity"
        android:theme="@style/MyDialogStyle"/>
```
## 参考
[自定义底部滑出的dialog](http://www.jianshu.com/p/15cdc5e162b6)