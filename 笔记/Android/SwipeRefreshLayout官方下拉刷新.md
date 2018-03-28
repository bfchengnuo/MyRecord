---
title: SwipeRefreshLayout官方下拉刷新
date: 2016-05-24 19:03:06
tags: [Android,MaterialDesign]
categories: Android
---
SwipeRefreshLayout 是google官方提供的下拉刷新控件，继承自 ViewGroup，在v4包里的，它提供了下拉刷新的功能以及对应的动画效果，使用起来非常简便。但是其中只能包含一个View，且该View必须是可滑动的（不可滑动的话动画显示有bug），如ListView。<!-- more -->
不过它应该是属于 MD 风格系列的，在 Design Support 库中

## 主要方法

-   setOnRefreshListener(OnRefreshListener)
     为布局添加一个Listener
-   setRefreshing(boolean)
    显示或隐藏刷新进度条
-   isRefreshing()
    检查是否处于刷新状态
-   setColorScheme()
    设置进度条的颜色主题，最多能设置四种

## 使用方法

### XML文件的声明
只需要在最外层加上 SwipeRefreshLayout，然后他的内部套一个可滚动的 view 即可，如 ScrollView 或者 ListView 以及 RecyclerView 都可以 。
``` html
<android.support.v4.widget.SwipeRefreshLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/swipe"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <ListView
        android:id="@+id/list_item"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</android.support.v4.widget.SwipeRefreshLayout>
```
如果要和 AppBarLayout 一起使用，注意要记得加:
`app:layout_behavior="@string/appbar_scrolling_view_behavior"` 
以来控制布局的行为，以免产生遮挡现象

### java代码实现

下拉时若要触发事件， 需实现 SwipeRefreshLayout.OnRefreshListener，重写 onRefresh 方法。
``` java
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
    // fragment implements SwipeRefreshLayout.OnRefreshListener
    mRefreshLayout.setOnRefreshListener(this);
    // 设置动画颜色
    mRefreshLayout.setColorSchemeResources(
            android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
}

// 下拉时显示动画3秒
// 一般在onRefresh()里面执行更新操作
//postDelayed定义一个定时器
public void onRefresh() {
    mRefreshLayout.setRefreshing(true);
    new Handler().postDelayed(new Runnable() {
            public void run() {
            // 通过setRefreshing(false)使动画停止
                mRefreshLayout.setRefreshing(false);
            }
    }, 3000);
}
```

## 参考

[kaywu](http://kaywu.github.io/2015/04/03/DoubanDemo/)
[Android SwipeRefreshLayout](http://stormzhang.com/android/2014/03/29/android-swiperefreshlayout/)
~~ActionBarPullToRefresh第三方开源库~~