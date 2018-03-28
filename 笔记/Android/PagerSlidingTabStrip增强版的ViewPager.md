---
title: PagerSlidingTabStrip---增强版的ViewPager
date: 2016-05-25 19:56:16
tags: [Android]
categories: Android
---
PagerSlidingTabStrip应该是我所接触的第一个第三方开源项目，这要多多感谢kaywu女神的文章，这个开源项目感觉现在已经废了，但是这也算是一个入门的标志吧。
<!-- more -->

## 关于导包
因为AS默认就是用的gradle方式，所以就直接在依赖文件build.gradle里加入
``` 
dependencies {
    compile 'com.astuetz:pagerslidingtabstrip:1.0.1'  
}
```
使用远程依赖确实方便多了呢，，，

## 使用
官方主页写的已经足够详细了，这里还是记录下
### XML文件中
首先是在XML文件的使用，通常布局声明在 viewpager 上面。
``` html
<com.astuetz.PagerSlidingTabStrip
        android:id="@+id/tabs"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        app:pstsShouldExpand="true"/>

    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center"/>
```
### Java代码中
在 oncreate 方法中（或 Fragment 的 onCreateView）中，绑定 PagerSlidingTabStrip 到 Viewpager
``` java
 // 初始化 ViewPager 和 Adapter
 ViewPager pager = (ViewPager) findViewById(R.id.pager);
 pager.setAdapter(new TestAdapter(getSupportFragmentManager()));

 // 绑定 PagerSlidingTabStrip 到 ViewPager 上
 PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
 tabs.setViewPager(pager);
```
如果你的 view pager 使用到 OnPageChangeListener。你应该通过这个 PagerSlidingTabStrip 控件设置而不是 Viewpager。如下：
``` java
     // continued from above
     tabs.setOnPageChangeListener(mPageChangeListener);
```
这里的ViewPager的适配器必须是继承的FragmentPagerAdapter，并重写getPageIconResId(int position)或者getPageTitle(int position)方法

``` java
public class MyPagerAdapter extends FragmentPagerAdapter {

    //存放标题数据
    private String[] titles;

    public MyPagerAdapter(FragmentManager fm, String[] titles) {
        super(fm);
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Fragment myFragment;
        //判断如果是第一页就加载list列表，否则显示自定fragment
        if (position == 0){
            myFragment = new RefreshFragment();
        }else {
            myFragment = new PagerFragment();
        }
        //保存传递当前是第几页，用于在fragment中显示
        bundle.putInt("pager_num",position);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
```
### 自定义个性方法
pstsIndicatorColor 滑动指示器的颜色
pstsUnderlineColor 整个 view【PagerSlidingTabStrip】下划线的颜色
pstsDividerColor tabs 之间分割线的颜色
pstsIndicatorHeight 滑动指示器的高度
pstsUnderlineHeight 整个 View【PagerSlidingTabStrip】下滑线的高度
pstsDivviderPadding 分割线上部、下部的内间距
pstsTabPaddingLeftRight 每个 tab 左右内间距
pstsScrollOffset 选中 tab 的滑动的距离
pstsTabBackground 每个 tab 的背景图片，使用 StateListDrawable
pstsShouldExpand 如果设置为 true，每个 tab 的宽度拥有相同的权重
pstsTextAllCaps 如果设置为 true，所有的 tab 字体转为大写

当然是也可以在xml文件中设置的
``` html
 <com.astuetz.PagerSlidingTabStrip
        android:id="@+id/pager_tabs"
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:textColor="#ff8f8f8f"
        android:textSize = "18sp"
        android:background="#ff2d2d2d"
        app:pstsShouldExpand="true"
        app:pstsIndicatorHeight="4dp"
        app:pstsIndicatorColor="#ff00cd79"/>
```

## 参考

[最好的资料还是官方](https://github.com/astuetz/PagerSlidingTabStrip)

[源码解析](http://p.codekk.com/blogs/detail/5595d64ed6459ae793499764)

[PagerSlidingTabStrip介绍及使用，让ViewPager更绚丽](http://blog.csdn.net/harryweasley/article/details/42290595)