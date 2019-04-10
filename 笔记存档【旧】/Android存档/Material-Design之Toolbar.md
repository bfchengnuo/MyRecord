---
title: MaterialDesign风格的使用(一)
date: 2017-03-16 20:18:39
tags: [Android,MaterialDesign]
categories: Android
---

传统的AndroidUI真的是不好看，很丑，Google大概也是意识到了这个问题，所以在2014年的IO大会上就有了Material Design，称为是全新的界面设计语言<!-- more -->

## 什么是Material Design

>   我们挑战自我，为用户创造了崭新的视觉设计语言。与此同时，新的设计语言除了遵循经典设计定则，还汲取了最新的科技，秉承了创新的设计理念。这就是**原质化设计(Material Design)**。这份文档是动态更新的，将会随着我们对 Material Design 的探索而不断迭代、升级。

看这介绍看的我是一脸懵逼，以我的理解来看，就是一种设计风格吧，如果接触过5.0+的系统应该都有明显的UI变化的感觉，对，这就是Material Design风格，从5.0版本开始，所有的内置应用全部采用了这种风格，最有代表性的是Google自家的应用，像youtube这样的，很有感觉

刚开始这种风格是需要开发者自己去实现的，比较复杂，毕竟我们也不是设计师，没啥热情，Google也许是发现了这个问题，在15年的IO大会上推出了Design Support库，这个库包含了一些代表性的控件及效果，这样使用起来就比较简单了

Material Design常用的有Toolbar、滑动菜单DrawerLayout、悬浮按钮、卡片式布局、下拉刷新、可折叠式标题栏等

要使用这些控件最好检查下是否导入了相应的 `appcompat` 和 `design`  库。

```
dependencies {  
    compile 'com.android.support:appcompat-v7:X.X.X'
    compile 'com.android.support:design:X.X.X'
}
```

如何确定版本呢，我没什么好法子，全靠 AS 的提示，这方面不是很懂，我知道的是可以根据 buildToolsVersion 的版本来确定

```xml
android {
    compileSdkVersion 24
    buildToolsVersion "24.0.0"
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    testCompile 'junit:junit:4.12'
    compile 'com.android.support:appcompat-v7:24.2.0'
    compile 'com.android.support:design:24.2.0'
    compile 'com.android.support:support-v4:24.2.0'
    compile 'com.android.support:palette-v7:24.2.0'
 }
```

## Toolbar

这就是一个Material 控件，上次写的DrawerLayout也属于Material 控件，官方说Toolbar是用来取代过去 Actionbar 的控件（官方在某些程度上认为 ActionBar 限制了 android app 的开发与设计的弹性），而现在于 material design 中也对之有一个统一名称：**app bar**，在未来的 Android App 中，就以 toolbar 这个元件来实作之。
既然我们要用Toolbar替代Actionbar，那么首先要把原来的Actionbar给隐藏掉，在AppTheme进行设置NoActionBar，NoActionBar自带的应该有两种，一种深色主体，一种浅色主体的，根据自己的喜好来选择，当然也可以手动隐藏：

```xml
<item name="windowActionBar">false</item>
<!-- 使用 API Level 22 编译的话，要去掉前缀字 -->
<item name="windowNoTitle">true</item>
```

然后我们要定义几个item来设计Toolbar的样式等，就颜色而说

```xml
<!-- Customize your theme here. -->
<!--导航栏底色-->
<item name="colorPrimary">@color/accent_material_dark</item>
<!--状态栏底色-->
<item name="colorPrimaryDark">@color/accent_material_light</item>
<!--导航栏上的标题颜色-->
<item name="android:textColorPrimary">@android:color/black</item>
<!--Activity窗口的颜色-->
<item name="android:windowBackground">@color/material_blue_grey_800</item>
<!--按钮选中或者点击获得焦点后的颜色-->
<item name="colorAccent">#00ff00</item>
<!--和 colorAccent相反，正常状态下按钮的颜色-->
<item name="colorControlNormal">#ff0000</item>
<!--Button按钮正常状态颜色-->
<item name="colorButtonNormal">@color/accent_material_light</item>
<!--EditText 输入框中字体的颜色-->
<item name="editTextColor">@android:color/white</item>
```

我来补两张图：
![](http://obb857prj.bkt.clouddn.com/bar%E9%A2%9C%E8%89%B2.jpg)
![](http://obb857prj.bkt.clouddn.com/bar%E9%A2%9C%E8%89%B22.jpg)
然后就是要在布局中加入Toolbar了，这个在V7包里

```xml
<android.support.v7.widget.Toolbar
  android:id="@+id/toolbar"
  android:layout_width="match_parent"
  android:layout_height="?attr/actionBarSize"
  android:background="?attr/colorPrimary"
  app:popupTheme="@style/AppTheme.PopupOverlay"/>
```

我们看到上面使用了一个app的命名空间，在根布局是有定义的：`xmlns:app="http://schemas.android.com/apk/res-auto"` ，因为在5.0以前的系统不存在Material，如果继续使用Android命名空间会出现一些兼容性问题，所以又增加了一个新的命名空间加以区分
popupTheme也是5.0新增的属性，指定的是弹出菜单的主题，向下兼容

>   引用自定义资源文件：
>   xml: `@[<package_name>:]<resource_type>/<resource_name>` ，如：@string/hello
>   code:`R.<resource_type>.<resource_name>`  ，如：R.string.hello
>   引用系统资源文件：
>   系统资源包括public和非public，public的资源引用为：
>   xml: `@android:<resource_type>/<resource_name>`，例如：@android:drawable/dialog_frame
>   code:` android.R.<resource_type>.<resource_name>` ，例如：android.R.drawable.dialog_frame
>   非public的系统资源引用为：在以上情况下加 `*`，如：
>   `@*android:drawable/mz_btn_l....`
>   代码访问最后使用XML来进行中转，再用 getResources.getDrawable() 方法来获取
>   引用主题属性：
>   xml格式为：`?[<package_name>:]<resource_type>/<resource_name>`，例如：
>   android:textColor=”?android:textDisabledColor”
>   引用当前theme中定义的属性值或者style，不需要`<resource_type>`，也就是上面我们写的形式
>   原文：http://johnnyshieh.github.io/android/2014/09/12/android-accessing-resources/

然后在Activity中进行设置：

```java
Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
// App Logo
toolbar.setLogo(R.drawable.ic_launcher);
// Title
toolbar.setTitle("My Title");
// Sub Title
toolbar.setSubtitle("Sub title");

setSupportActionBar(toolbar);
// 必须设置在setSupportActionBar后才有效
toolbar.setNavigationIcon(R.drawable.ab_android);
```

就这样来看的话，和ActionBar也没啥区别，但是它确实以及具备了Material的功能，下面就来定制一下，上面写了一些设置方法，当然在xml也可以定义，比如我们知道在AndroidManifest文件中，找到当前Activity设置label属性，就是定义标题了，默认会显示应用名称
另外说下NavigationIcon在Logo的前面，它们两个后面才是设置的标题信息啥的
定义菜单和其他一致，new一个菜单资源文件，item里包括id、icon、 title，还有一个特殊属性：`app:showAsAction`，命名空间和上面在xml中是一样的，它有三个值，**always表示永远显示在Toolbar上，如果屏幕空间不够就隐藏；ifRoom类似，只是它在空间不足的情况下会显示在菜单中；never就是只显示在菜单中**
ps：菜单里的只显示文字，Toolbar上的只显示图标
下面就是在Activity中进行加载了和设置监听了

```java
@Override
public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true; //返回false将不显示
}

@Override
public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_settings) {
        return true; // 已被消费
    }
    return super.onOptionsItemSelected(item);
}
```

## 悬浮按钮FloatingActionButton

立面设计是Material Design的一个很重要的思想，最简单的体现就是这悬浮按钮了，FloatingActionButton是Design Support库提供的一个控件，在布局文件这样写

```xml
<android.support.design.widget.FloatingActionButton
	android:id="@+id/fab"
	android:layout_width="wrap_content"
	android:layout_height="wrap_content"
	android:layout_gravity="bottom|end"
	android:layout_margin="@dimen/fab_margin"
	app:srcCompat="@android:drawable/ic_dialog_email"/>

<!-- XML的常用属性 -->
android:src=""			<!-- 悬浮按钮上的图标 -->
app:backgroundTint=""	<!-- 背景颜色，默认为 Theme 主题中的 "colorAccent" -->
app:elevation=""		<!-- 阴影的深度，默认是有阴影的 -->
app:fabSize=""			<!-- 大小，仅支持两种大小：normal、mini -->
app:rippleColor=""		<!-- 点击产生的波纹颜色 -->
```

一般我们也都是放在右下角，使用`android:src`来指定图标也是可以的，还有一个属性是`app:elevation`，指的是高度，和投影范围有些关系，默认的其实已经很好了，然后在Activity中设置它的点击事件

```java
FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
fab.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
});
```

这里并没有使用传统的Toast啥的，Snackbar是Design Support库提供的一个比较先进的提示工具，它是可以和用户进行交互的，当然**它的出现并不是为了取代Toast**，只是有些场景下，有这样的需求
通过make方法创建出一个Snackbar对象，它会通过传入的view对象自动查找最外层的布局，所以是可以随便传入一个当前页面的view
setAction用来设置一个动作，简单说己是设置一个按钮，以及它的监听事件
Snackbar自带动画，过一段时间也是会消失的，毕竟有显示时长嘛~~

## CoordinatorLayout

CoordinatorLayout可以说是一个加强版的FrameLayout，它也是属于Design Support库的，通常情况下和FrameLayout没啥区别，设置可以直接把FrameLayout的名字换成CoordinatorLayout(`android.support.design.widget.CoordinatorLayout`)，其他的都不需要改
但是既然属于Design Support库就一定有点特殊的功能，**它可以监听其所有子控件的各种事件，然后自动帮我们做出最合理的响应**
举个例子就是上面的Snackbar在弹出的时候有可能会挡住右下角的按钮，如果在FloatingActionButton的外层加个CoordinatorLayout，当弹出Snackbar的时候按钮会同步上移相同高度，消失的时候会自动下移，保证不会被挡住
也许你会问，Snackbar不是CoordinatorLayout的子布局啊，是通过make创建出来的嘛，为什么还有效果，因为我们make的时候传入了个view啊，也就是FloatingActionButton，它是CoordinatorLayout的子布局，所以也就没问题了

## 参考

[AppTheme属性颜色](http://www.jianshu.com/p/bc38d6a15809)