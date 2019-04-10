---
title: 水平侧滑菜单SlidingPaneLayout
date: 2017-04-08 12:48:03
tags: Android
categories: Android
---

SlidingPaneLayout 可能很多人都没用过，但这并不是一个新控件。它是 Support V4 包中提供的，2013 年 Google I/O 大会期间更新的。
和侧滑菜单 DrawerLayout 相比，我认为直观的区别就是 SlidingPaneLayout  是平面的侧滑菜单，就是说滑动内容区的内容也会向右偏移，除此之外确实感觉没多大区别，当然我感觉扩展性感觉是不如 DrawerLayout 的<!-- more -->

## 布局编写

那就说一下简单的使用，首先当然是先布局：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.bfchengnuo.mytemp.SlidingActivity">

    <android.support.v4.widget.SlidingPaneLayout
        android:id="@+id/slidingPL"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >

        <include
            layout="@layout/menu_layout" />

        <include
            layout="@layout/content_layout" />
    </android.support.v4.widget.SlidingPaneLayout>
</LinearLayout>
```

注意，SlidingPaneLayout 中的子布局，第一个是菜单，第二个是布局，菜单其实我就引用了一个 ListView，内容区一般都是使用 fragment，这里就不多说了，和其他布局也类似

其实也看得出，和其他的侧滑菜单控件是很类似的

## Java代码

代码也非常简单，无非就是设置适配器监听事件之类的，当然这是基本使用，会基本使用就行了，毕竟使用的不多，等用到了其他功能再来补充吧

```java
public class SlidingActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private SlidingPaneLayout mSlidingPL;
    private ListView mLvMenu;
    private FrameLayout mFrContent;
    private List<HashMap<String,Object>> mapList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding);

        mSlidingPL = (SlidingPaneLayout) findViewById(R.id.slidingPL);
        mLvMenu = (ListView) findViewById(R.id.lv_menu);
        mFrContent = (FrameLayout) findViewById(R.id.fr_content);

        initData();
        mLvMenu.setOnItemClickListener(this);
    }

    private void initData() {
        mapList = new ArrayList<>();
        // 造数据~~
        for (int i = 0; i < 3; i++) {
            HashMap<String,Object> map = new HashMap<>();
            map.put("icon", R.mipmap.ic_launcher);
            map.put("text","item" + i);
            mapList.add(map);
        }

        SimpleAdapter adapter = new SimpleAdapter(this,mapList, R.layout.left_list_fragment_item,new String[]{"icon","text"},new int[]{R.id.menu_icon, R.id.menu_name});
        mLvMenu.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                Toast.makeText(this, "你点击了菜单1", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mSlidingPL.isOpen())
            mSlidingPL.closePane();
        else
            finish();
    }
}
```

## 滑动关闭Activity

这个功能可以用 SlidingPaneLayout 来实现
思路就是，把菜单设置为透明的，然后给 SlidingPaneLayout 加个 setPanelSlideListener 监听就可以了，当菜单完全展开时就把当前的 Activity 给 finish 掉，这就就实现效果了
http://www.jianshu.com/p/67ce59c9e747


