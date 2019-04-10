---
title: Android控件布局属性整理
date: 2016-08-09 20:59:17
tags: [Android,UI布局]
categories: Android
---

布局的时候辣么多的属性老是记不住，这里做个笔记，方便以后查阅，打算长期更新，先总结下用到的最多的线性布局和相对布局。逐步更新控件的属性设置。
<!-- more -->
## RelativeLayout 相对布局的一些属性

- 第一类:属性值为true或false **(仅RelativeLayout中有效)**

| 属性名                                     | 作用                      |
| :-------------------------------------- | :---------------------- |
| android:layout_centerHrizontal          | 水平居中                    |
| android:layout_centerVertical           | 垂直居中                    |
| android:layout_centerInparent           | 相对于父元素完全居中              |
| android:layout_alignParentBottom        | 贴紧父元素的下边缘               |
| android:layout_alignParentLeft          | 贴紧父元素的左边缘               |
| android:layout_alignParentRight         | 贴紧父元素的右边缘               |
| android:layout_alignParentTop           | 贴紧父元素的上边缘               |
| android:layout_alignWithParentIfMissing | 如果对应的兄弟元素找不到的话就以父元素做参照物 |

- 第二类：属性值必须为id的引用名“@id/id-name” **(仅RelativeLayout中有效)**

| 属性名                             | 作用                              |
| ------------------------------- | ------------------------------- |
| android:layout_below            | 在某元素的下方                         |
| android:layout_above            | 在某元素的的上方                        |
| android:layout_toLeftOf         | 在某元素的左边                         |
| android:layout_toRightOf        | 在某元素的右边                         |
| ---------------分割线------------- | ---------------分割线------------- |
| android:layout_alignTop         | 本元素的上边缘和某元素的的上边缘对齐              |
| android:layout_alignLeft        | 本元素的左边缘和某元素的的左边缘对齐              |
| android:layout_alignBottom      | 本元素的下边缘和某元素的的下边缘对齐              |
| android:layout_alignRight       | 本元素的右边缘和某元素的的右边缘对齐              |

- 第三类：属性值为具体的像素值，如30dip，40px **(任何布局有效)**

| 属性名                         | 作用         |
| --------------------------- | ---------- |
| android:layout_marginBottom | 离某元素底边缘的距离 |
| android:layout_marginLeft   | 离某元素左边缘的距离 |
| android:layout_marginRight  | 离某元素右边缘的距离 |
| android:layout_marginTop    | 离某元素上边缘的距离 |

- 其他 

    android:layout_alignBaseline  //与某元素的基准线对齐
    android:layout_gravity  //相对与父元素的位置

  Android4.2+ 新增属性：

  > android:layout_alignStart 　　        		两个控件开始对齐
  > android:layout_alignEnd                 		两个控件结束对齐
  > android:layout_alignParentStart    		  	子控件和父控件开始对齐
  > android:layout_alignParentEnd  　		子控件和父控件结束对齐

## LinearLayout 线性布局的一些属性

android:orientation="vertical"		//垂直排列
android:orientation="horizontal"	//水平排列
android:baselineAligned="false"	//设置基准线,一条线上(只对有文字内容，如TextView,EditText,Button等才有效果)
[weight(权重)](http://bfchengnuo.info/2016/07/13/weight%E5%B1%9E%E6%80%A7%E8%A7%A3%E6%9E%90/)

android:divider="@drawable/drawable"		//分割线的drwable，不能直接给color（无效）
android:dividerPadding="0.5dp"			//分割线高度或者宽度
分割线的Shape.xml:

``` html
<shape xmlns:android="http://schemas.android.com/apk/res/android">
<solid android:color="@color/colorAccent"/>
<size android:height="1px"/>   (size必须设置)
</shape>
```

选择的样式（可以多选）：

LinearLayout.SHOW_DIVIDER_BEGINNING;		//开始的分割线
LinearLayout.SHOW_DIVIDER_MIDDLE;			//中间的分割线
LinearLayout.SHOW_DIVIDER_END;				//结束的分割线
LinearLayout.SHOW_DIVIDER_NONE;			//没有分割线

分割线实际开发下面的这种形式用的比较多：

```html
<View android:layout_width="match_parent"
    android:layout_height="0.5dp"
    android:background="@color/colorPrimary"/>
```

## 百分比布局

这里再说一个比较新的，百分比布局，从名字就可以看出，设置高度和宽度支持百分比参数，我们知道线性布局是支持按比例进行分割的，这个布局就是为了弥补 **相对布局 和 帧布局** 不能按比例分割而产生的

使用它要导入 support 库，这也是为了保证其兼容性，所以必须要加入依赖：
`compile 'com.android.support:percent:22.2.0'`
还要记得加入 app 命名空间哦
这两个布局为PercentRelativeLayout、PercentFrameLayout，从名字可以看出确实的拓展，肯定是继承关系

这个使用起来其实是很简单的，说白了就是多加了几个属性的支持，一个例子就能看明白了

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.percent.PercentFrameLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_gravity="left|top"
        android:background="#44ff0000"
        android:text="width:30%,height:20%"
        app:layout_heightPercent="20%"
        android:gravity="center"
        app:layout_widthPercent="30%"/>

    <TextView
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_gravity="right|top"
        android:gravity="center"
        android:background="#4400ff00"
        android:text="width:70%,height:20%"
        app:layout_heightPercent="20%"
        app:layout_widthPercent="70%"/>
</android.support.percent.PercentFrameLayout>
```

想要深入了解的，移步鸿洋博客：http://blog.csdn.net/lmj623565791/article/details/46695347

## 关于控件的属性

不同控件又很多属性都是通用的，下面的并不是独有。~~整理太麻烦啦~~
部分属性是支持多个值的，用 `|` 隔开

### TextView

| 属性名                           | 作用                                       |
| ----------------------------- | ---------------------------------------- |
| android:textColorHighlight    | 被选中文字的底色，默认为蓝色                           |
| android:textColorHighlight    | 被选中文字的底色，默认为蓝色                           |
| android:textScaleX            | 设置文字之间间隔，默认为1.0f                         |
| android:textColorHint         | 设置提示信息文字的颜色，默认为灰色。与hint一起使用              |
| android:inputType             | 设置文本的类型，用于帮助输入法显示合适的键盘类型 通常用于EditView    |
| android:maxLength             | 限制显示的文本长度，超出部分不显示                        |
| android:lines                 | 设置文本的行数，设置两行就显示两行，即使第二行没有数据。             |
| android:maxLines              | 设置文本的最大显示行数，与width或者layout_width结合使用，超出部分自动换行，超出行数将不显示。 |
| android:minLines              | 设置文本的最小行数，与lines类似。                      |
| android:lineSpacingExtra      | 设置行间距。                                   |
| android:lineSpacingMultiplier | 设置行间距的倍数。如”1.2″                          |
| android:ellipsize             | 设置文字太长省略效果/位置 (ET不支持)                    |
| android:focusableInTouchMode  | 设置触摸焦点，配合上面的跑马灯模式                        |
| android:focusable             | 设置键盘焦点，连同上面两个配合，使用跑马灯效果                  |
| android:marqueeRepeatLimit    | 设置跑马灯效果的重复次数，配合上                         |

### EditText

| 属性名                        | 作用                                       |
| -------------------------- | ---------------------------------------- |
| android:numeric=”integer”  | 设置只能输入整数，如果是小数则是：decimal，带符号 numberSigned |
| android:singleLine=”true”  | 设置单行输入，一旦设置为true，则文字不会自动换行。              |
| android:password=”true”    | 设置只能输入密码，也可以使用 inputType设置               |
| android:background=”@null” | 空间背景，这里没有，指透明                            |
| android:editable=”false”   | 设置EditText不可编辑                           |
| android:singleLine=”true”  | 强制输入的内容在单行                               |
| android:ellipsize=”end”    | 自动隐藏尾部溢出数据，一般用于文字内容过长一行无法全部显示时           |
| android:maxLength="10"     | 限制最大的输入字符数为10                            |
| android:digits=“abc123”    | 限制输入，只能输入后面设置的字符                         |

### Button

| 属性名         | 作用                      |
| ----------- | ----------------------- |
| textAllCaps | 默认的英文字母是大写的，设为False即可关闭 |

### ListView

| 属性名                   | 作用          |
| --------------------- | ----------- |
| android:divider       | 设置分割线的颜色/图片 |
| android:dividerHeight | 分割线的高度      |

## 补充

设置 android:onClick="show" 属性，点击事件就会执行show()方法。

像这样layout_xxx 带有layout的说明是相对于父布局来说的，没有则相对于控件自身来说。

还有很多~~奇怪的用法~~，用到了补充 还有[这个网站](http://www.igooda.cn/jzjl/20141117670.html)挺全的

## 需要注意的问题

Drawable文件夹里面的图片命名是不能大写的。

android:layout_gravity 是相对与它的父元素，说明元素显示在父元素的什么位置。但是如果该子元素本身高度如果为wrap_content，则会导致android:layout_gravity失效。记得这样设`android:layout_height="match_parent"`

weight只有线性布局拥有，首先是按照分配的长度来给予长度，剩余的长度再按权重的比例来分配。