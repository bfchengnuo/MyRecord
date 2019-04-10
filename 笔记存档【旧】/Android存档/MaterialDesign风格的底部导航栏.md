---
title: MaterialDesign风格的底部导航栏
date: 2017-04-06 18:57:39
tags: [Android,MaterialDesign]
categories: Android
---

Android **底部导航栏**的实现方式特别多，例如 TabHost ，TabLayout，或者 TextView 等，都可以实现底部导航栏的效果，但是却没有 Google 官方统一的导航栏样式，而 BottomNavigationBar 就是 Google 添加到 Material Design 中的底部导航栏，也可以说是现今 Android 底部导航栏的一个标准与统一吧<!-- more -->

## 基本使用

虽然 Google 加入到了 MD 中，但它不属于 Design Support 库，使用之前现添加依赖，代码托管在 [Github](https://github.com/Ashok-Varma/BottomNavigation) 上：
`compile 'com.ashokvarma.android:bottom-navigation-bar:1.4.1'`

官方的建议是小于3个推荐使用 tab ，大于三个推荐使用 navigation drawer

使用起来也非常简单，直接就当一个控件用就好了，说个比较简单的例子，布局文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!--内容区域-->
    <TextView
        android:id="@+id/bottom_nav_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <!--BottomNavigationBar-->
    <com.ashokvarma.bottomnavigation.BottomNavigationBar
        android:id="@+id/bottom_navigation_bar_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_alignParentBottom="true"/>

</RelativeLayout>
```

内容比较简单，只为测试，实际上内容去应该写一个 ViewGroup 用来装 fragment 比较好，这里就直接用个 TV 显示了，再来 Activity 方面的代码：

```java
public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    private TextView mBottomNavContent;
    private BottomNavigationBar mBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavContent = (TextView) findViewById(R.id.bottom_nav_content);
        mBottom = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar_container);

        mBottom.setMode(BottomNavigationBar.MODE_DEFAULT);
        mBottom
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "位置").setActiveColor(android.R.color.holo_orange_light))
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "发现").setActiveColor(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "爱好").setActiveColor(android.R.color.holo_red_dark))
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "图书").setActiveColor(R.color.colorPrimaryDark))
                .setFirstSelectedPosition(0)  // 设置默认选择
                .initialise();

        // 设置监听事件
        mBottom.setTabSelectedListener(this);
    }

    @Override
    public void onTabSelected(int position) {
        switch (position) {
            case 0:
                mBottomNavContent.setText("First");
                break;
            case 1:
                mBottomNavContent.setText("Second");
                break;
            case 2:
                mBottomNavContent.setText("Third");
                break;
            case 3:
                mBottomNavContent.setText("Fourth");
                break;
        }
    }

    @Override
    public void onTabUnselected(int position) {
    }
  
    @Override
    public void onTabReselected(int position) {
    }
}
```

上面的代码比较基本，阅读起来应该没压力，就是设置了条目、加了个监听事件，下面再来说说一下其他设置

## 设置Mode

模式有三种，XML 使用 bnbMode 设置，代码中使用 **setMode()** 方法设置，分别是：

-   MODE_DEFAULT
    如果 Item 的个数 <=3 就会使用 **MODE_FIXED** 模式，否则使用 **MODE_SHIFTING** 模式
-   MODE_FIXED
    填充模式，未选中的 Item 会显示文字，没有换挡动画。
-   MODE_SHIFTING
    换挡模式，未选中的 Item 不会显示文字，选中的会显示文字。在切换的时候会有一个像换挡的动画

## 设置BackgroundStyle

xml:  bnbBackgroundStyle
方法：setBackgroundStyles()
包含3种Style:

-   BACKGROUND_STYLE_DEFAULT
    如果设置的 Mode 为**MODE_FIXED**，将使用 BACKGROUND_STYLE_STATIC 。
    如果 Mode 为 **MODE_SHIFTING** 将使用 BACKGROUND_STYLE_RIPPLE。
-   BACKGROUND_STYLE_STATIC
    点击的时候没有水波纹效果
-   BACKGROUND_STYLE_RIPPLE
    点击的时候有水波纹效果

## 设置默认颜色

xml：bnbActiveColor,  bnbInactiveColor,  bnbBackgroundColor
方法：setActiveColor,  setInActiveColor,  setBarBackgroundColor
例如：

```java
bottomNavigationBar
	.setActiveColor(R.color.primary)
	.setInActiveColor("#FFFFFF")
	.setBarBackgroundColor("#ECECEC")
```

-   **InActiveColor**
    表示未选中Item中的图标和文本颜色。默认为 Color.LTGRAY
-   **ActiveColor :**
    在 **BACKGROUND_STYLE_STATIC** (忘记了的看上面一条) 下，表示选中 Item 的图标和文本颜色。
    而在 **BACKGROUND_STYLE_RIPPLE** 下，表示整个容器的背景色。默认Theme's Primary Color
-   **backgroundColor :**
    在 **BACKGROUND_STYLE_STATIC** 下，表示整个容器的背景色。
    而在 **BACKGROUND_STYLE_RIPPLE** 下，表示选中 Item 的图标和文本颜色。默认 Color.WHITE

## 定制Item的颜色

我们可以为每个 Item 设置选中未选中的颜色，如果没有设置，将继承 BottomNavigationBar 设置的选中未选中颜色。
方法：
`BottomNavigationItem.setInActiveColor()` ： 设置 Item 未选中颜色方法
`BottomNavigationItem.setActiveColor()` ： 设置 Item 选中颜色方法

## 定制icon

如果使用颜色的变化不足以展示一个选项 Item 的选中与非选中状态，可以使用 **BottomNavigationItem.setInActiveIcon()** 方法来设置。
例如：

```java
new BottomNavigationItem(R.mipmap.ic_directions_bus_white_24dp, "公交")//这里表示选中的图片
  .setInactiveIcon(ContextCompat.getDrawable(this,R.mipmap.ic_launcher)))//非选中的图片
```

## 设置隐藏与显示

**如果 BottomNavigationBar 是在 CoordinatorLayout 布局里**，默认设置当向下滑动时会自动隐藏它，当向上滑动时会自动显示它。
我们可以通过 `setAutoHideEnabled(boolean)` 设置是否允许这种行为
当然也可以手动进行设置：

```java
bottomNavigationBar.hide();//隐藏
bottomNavigationBar.hide(true);//隐藏是否启动动画，这里并不能自定义动画
bottomNavigationBar.unHide();//显示
bottomNavigationBar.hide(true);//隐藏是否启动动画，这里并不能自定义动画
```

实际上这里都是通过属性动画 ViewPropertyAnimatorCompat 来完成的，所以如果我们要自定义它的隐藏和显示的话，通过属性动画即可实现。

## 为Item添加Badge

至于什么是 Badge ，类似微信上的标记未读消息的小红点，设置也非常简单，先创建，然后为指定的菜单添加

```java
badge=new BadgeItem()
//    .setBorderWidth(2)  //Badge的Border(边界)宽度
//    .setBorderColor("#FF0000")  //Badge的Border颜色
//    .setBackgroundColor("#9ACD32")  //Badge背景颜色
//    .setGravity(Gravity.RIGHT| Gravity.TOP)  //位置，默认右上角
      .setText("2")  //显示的文本
//    .setTextColor("#F0F8FF")  //文本颜色
//    .setAnimationDuration(2000)
//    .setHideOnSelect(true)  //当选中状态时消失，非选中状态显示

// 添加
new BottomNavigationItem(R.mipmap.ic_directions_bike_white_24dp, "骑行").setBadgeItem(badge)
```

## 参考

http://www.jianshu.com/p/134d7847a01e