---
title: 官方侧滑菜单DrawerLayout基本使用
date: 2017-03-15 20:30:49
tags: [Android,MaterialDesign]
categories: Android
---

DrawerLayout是Support Library包中实现了侧滑菜单效果的控件，可以说drawerLayout是因为第三方控件如MenuDrawer等的出现之后，google借鉴而出现的产物。drawerLayout分为侧边菜单和主内容区两部分，侧边菜单可以根据手势展开与隐藏（drawerLayout自身特性），主内容区的内容可以随着菜单的点击而变化（这需要使用者自己实现）。<!-- more -->

## 布局编写

DrawerLayout最好为界面的**根布局**，官网是这样说的，否则可能会出现触摸事件被屏蔽的问题；
**主内容区的布局代码要放在侧滑菜单布局的前面**, 因为 XML 顺序意味着按 z 序（层叠顺序）排序，并且抽屉式导航栏必须位于内容顶部(如果有的话)；这里我偷个懒，没有写导航栏，~~其实是有点看不懂~~
侧滑菜单部分的布局（这里是ListView）必须设置**layout_gravity**属性，他表示侧滑菜单是在左边还是右边，而且如果不设置在打开关闭抽屉的时候会报错，设置了`layout_gravity="start/left"`的视图才会被认为是侧滑菜单。
理论上是可以写三个布局的，对应内容、左边的菜单、右边的菜单，如果只需要左边的，可以只写前两个

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:id="@+id/drawer_layout"
    android:layout_height="match_parent">

    <!--内容部分放在前面-->
    <FrameLayout
        android:id="@+id/frame_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <!--菜单，可以设置一个Linearlayout来进行自定复杂布局-->
    <ListView
        android:id="@+id/left_drawer"
        android:layout_width="240dp"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:divider="@android:color/transparent"
        android:dividerHeight="0dp"
        android:background="@color/colorPrimary"/>
    <!--android:choiceMode="singleChoice"  只能有一个处于选择的状态-->
</android.support.v4.widget.DrawerLayout>
```

如果想加一个导航栏啥的，可以去看看简书这篇文章的布局：http://www.jianshu.com/p/3fe2acac0ddb
以及...http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/0925/1713.html
什么Actionbar(貌似能配合AppTheme耍)、ToolBar(官方推荐使用)真是是一大问题，~~抽空研究一下~~
待补充...

## Activity编写

作为测试，我这里写的就比较简单了，加载了一个fragment，侧拉菜单就用了一个简单的数组，使用的是数组适配器，简单嘛~~

```java
public class DrawerActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawer;
    private String[] mStrings = new String[]{"菜单1","菜单2","菜单3","菜单4","菜单5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawer = (ListView) findViewById(R.id.left_drawer);

        mLeftDrawer.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,mStrings));
        // mLeftDrawer.setOnItemClickListener();  自行完成

        //获得一个管理者
        FragmentManager fm = getFragmentManager();
        //开启一个事务
        FragmentTransaction transaction = fm.beginTransaction();
        mFragment = new DrawerFragment();
        transaction.replace(R.id.frame_layout, mFragment);
        //提交事务
        transaction.commit();
    }
  
    public void openMenu(){
        // 判断左侧菜单是否打开，END是右边
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
```

这个简单的栗子只用了一个fragment，看不出啥效果了，最好是每个菜单项对应一个，在点击事件里设置更改内容区的fragment就行了，方法还是和上面一样

## 监听事件

DrawerLayout可以设置其的监听事件：**addDrawerListener(@NonNull DrawerListener listener)**

```java
private class DrawerViewListener implements DrawerLayout.DrawerListener {
    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
    //滑动时调用
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    //打开菜单时调用
    }

    @Override
    public void onDrawerClosed(View drawerView) {
    //关闭菜单时调用
    }

    @Override
    public void onDrawerStateChanged(int newState) {
    //菜单状态改变时调用
    }
}
```
## NavgationDrawer的使用

NavgationDrawer是Design Support库中提供的一个插件，也是一个侧滑菜单，在我们新建工程的时候是可以看到的，界面也就类似那样，让侧滑菜单分为两部分
![](http://obb857prj.bkt.clouddn.com/%E4%BE%A7%E6%BB%91%E8%8F%9C%E5%8D%95.jpg)
依赖是`compile 'com.android.support:design:24.2.1'`
上面说过，它主要可分为两部分，上面的头部和下面的列表，首先就来搞下下面的列表吧，可以右键menu文件夹New一个Menu resource file，下面是一个AS自动创建的

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">
    <group android:checkableBehavior="single">
        <item
            android:id="@+id/nav_camera"
            android:icon="@drawable/ic_menu_camera"
            android:title="Import"/>
        <item
            android:id="@+id/nav_gallery"
            android:icon="@drawable/ic_menu_gallery"
            android:title="Gallery"/>
        <item
            android:id="@+id/nav_slideshow"
            android:icon="@drawable/ic_menu_slideshow"
            android:title="Slideshow"/>
        <item
            android:id="@+id/nav_manage"
            android:icon="@drawable/ic_menu_manage"
            android:title="Tools"/>
    </group>
  
    <item android:title="Communicate">
          <menu>
              <item
                  android:id="@+id/nav_share"
                  android:icon="@drawable/ic_menu_share"
                  android:title="Share"/>
              <item
                  android:id="@+id/nav_send"
                  android:icon="@drawable/ic_menu_send"
                  android:title="Send"/>
          </menu>
      </item>
</menu>
```

需要注意一下的是：`checkableBehavior="single"`的意思是在下面的组中，只能选一个，也就是菜单只能单选
至于头部的布局文件就随意了，和平常的布局没啥区别，关键是与Activity关联的布局了

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v4.widget.DrawerLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="true"
    tools:openDrawer="start">

    <include
        layout="@layout/app_bar_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

    <android.support.design.widget.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:fitsSystemWindows="true"
        app:headerLayout="@layout/nav_header_main"
        app:menu="@menu/activity_main_drawer"/>

</android.support.v4.widget.DrawerLayout>
```

include引用的是内容区和一个ToolBar，这个不是重点，嗯？！下面我们加了个NavigationView来代替我们自定义的侧滑菜单布局，最后的两个app的引用看着眼熟吧，就是我们写的那两部分，布局这样基本就完成了，接下来就是在Activity进行搞了，偷了个懒，用的是AS自动生成的模板，把ToolBar部分删了，因为有点长啊，关键是我还不会用....

```java
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        // 按下返回键，如果菜单开着就关闭
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // 当菜单的某一项被选择就触发
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true; //消费事件
    }
}
```

这是效果图：
![](http://obb857prj.bkt.clouddn.com/%E4%BE%A7%E6%BB%91%E8%8F%9C%E5%8D%95.gif)
这都是基本的使用，要做出更好看的效果配合ToolBar比较好，然而.....
自行Google吧