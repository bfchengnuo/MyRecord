---
title: 关于页卡ViewPager
date: 2016-05-23 09:55:56
tags: [Android]
categories: Android
---
ViewPager这个类开发中应该是使用的比较频繁的，来自android-support-v4.jar这个类包，~~在设置页卡的标题时有两个标签可选就是PagerTabStrip和PagerTitleStrip~~，于是产生疑问，随google学习ViewPager，存档。
<!-- more -->

## ViewPager介绍
### 说明
首先看下它的继承关系
![](http://img.mukewang.com/566134e20001a19003320178.png)
从图里可以看出，ViewPager继承自ViewGroup，也就是ViewPager是一个容器类，可以包含其他的View类。

### 页卡的声明
在xml中引用时必须要写全包名，否则无法找到此类。
``` html
<android.support.v4.view.ViewPager
    android:id="@+id/viewPager"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="center">
        
</android.support.v4.view.ViewPager>
```

### 页卡标题的声明
这里就用到了控件PagerTitleStrip和PagerTabStrip，这两个控件必须当作ViewPager的子控件来用，否则会报错。
``` html
<android.support.v4.view.ViewPager
    android:id="@+id/viewPager"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="center">

    <android.support.v4.view.PagerTitleStrip
        android:id="@+id/pagertitle"
        android:layout_width="fill_parent"
        android:layout_height="45dp"
        android:layout_gravity="top"/>

    <android.support.v4.view.PagerTabStrip
        android:id="@+id/pagertab"
        android:layout_width="fill_parent"
        android:layout_height="45dp"
        android:layout_gravity="bottom" />
</android.support.v4.view.ViewPager>
```
### 区别
PagerTabStrip：交互式
PagerTitleStrip：非交互式

1、PagerTabStrip在当前页面下，会有一个下划线条来提示当前页面的Tab是哪个。
2、PagerTabStrip的Tab是可以点击的，当用户点击某一个Tab时，当前页面就会跳转到这个页面，而PagerTitleStrip则没这个功能。

总结就是区别在：是否可点击与下划线指示条

**不过实际使用中基本上没他们什么事情，一般会重写tab(可以去看参考2)**

## 用布局文件作为数据源
**我们先在layout文件夹下新建几个视图资源**
view1:
``` html
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="0sp"
        android:layout_height="wrap_content"
        android:text="第一页的内容"
        android:textSize="20sp"
        android:gravity="center"
        android:layout_weight="1"/>
</LinearLayout>
```
view2:
``` html
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="0sp"
        android:layout_height="wrap_content"
        android:text="第二页的内容"
        android:textSize="20sp"
        android:gravity="center"
        android:layout_weight="1"/>
</LinearLayout>
```
view3:
``` html
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:layout_width="0sp"
        android:layout_height="wrap_content"
        android:text="第三页的内容"
        android:textSize="20sp"
        android:gravity="center"
        android:layout_weight="1"/>
</LinearLayout>
```
**然后在Java代码中进行初始化数据等**
``` java
	private List<String>title;
    private List<View>viewList;
    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		//通过inflate方法进行转换成view视图
        View view1 = View.inflate(this,R.layout.view1,null);
        View view2 = View.inflate(this,R.layout.view2,null);
        View view3 = View.inflate(this,R.layout.view3,null);
        View view4 = View.inflate(this,R.layout.view4,null);

        viewList = new ArrayList<View>();
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        viewList.add(view4);
        title = new ArrayList<String>();
        title.add("第一页");
        title.add("第二页");
        title.add("第三页");
        title.add("第四页");
```
**这样关于view的list数据源和页卡标题的数据源就都初始化好了，下面开始定义适配器**

### 使用PageAdapter适配器

这里我们用PageAdapter这个适配器 
必须重写的四个函数：

- boolean isViewFromObject(View arg0, Object arg1)
- int getCount() 
- void destroyItem(ViewGroup container, int position,Object object)
- Object instantiateItem(ViewGroup container, int position)

MyPagerAdapter.java
``` java
public class MyPagerAdapter extends PagerAdapter{
    private List<String> title;
    private List<View>viewList;

	//通过构造函数得到需要的数据
    public MyPagerAdapter(List<View> viewList,List<String>title){
        this.viewList = viewList;
        this.title = title;
    }

    //返回页卡的数量
    @Override
    public int getCount() {
        return viewList.size();
    }
    //view是否来自与对象
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
    //实例化一个页卡
    //做了两件事，第一把view添加到container，第二返回这个view
    //这里的返回值其实就是所谓的key，理论上只要是可以代表view的变量即可，比如position，这里返回view是为了方便isViewFromObject方法中的比较
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }
    //销毁一个页卡
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }
    //设置标题，必须声明了标题子标签才有效
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
```
**下面就是给页卡绑定适配器了**
``` java
	//新建一个适配器
    MyPagerAdapter myPagerAdapter = new MyPagerAdapter(viewList,title);
    //绑定适配器
    viewPager.setAdapter(myPagerAdapter);
```
## 使用Fragment作为数据源
### 前言
ViewPager更多的时候会与Fragment一起使用，这是一种很好的方法来管理各个页面的生命周期。Android提供了FragmentPagerAdapter适配器和FragmentStatePagerAdapter适配器来让ViewPager与Fragment一起工作。
- FragmentPagerAdapter拥有自己的缓存策略，当和ViewPager配合使用的时候，会缓存当前Fragment以及左边一个、右边一个，一共三个Fragment对象。每一个用户访问过的fragment都会被保存在内存中，尽管他的视图层级可能会在不可见时被销毁。这可能导致大量的内存因为fragment实例能够拥有任意数量的状态。对于较多的页面集合，更推荐使用FragmentStatePagerAdapter。
- FragmentStatePagerAdapter这个适配器对实现多个Fragment界面的滑动是非常有用的，它的工作方式和listview是非常相似的。当Fragment对用户不可见的时候，整个Fragment会被销毁，只会保存Fragment的保存状态。基于这样的特性，FragmentStatePagerAdapter比FragmentPagerAdapter更适合用于很多界面之间的转换，而且消耗更少的内存资源。

使用fragment作为数据源首先当然是新建fragment了

```java
public class Fragment1 extends Fragment {
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view= inflater.inflate(R.layout.layout1, container, false);
        return view;
    }
}
```

类似的还有几个，略，然后在在主activity中添加到页卡

```java
public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //构造适配器
        List<Fragment> fragments=new ArrayList<Fragment>();
        fragments.add(new Fragment1());
        fragments.add(new Fragment2());
        fragments.add(new Fragment3());
        FragAdapter adapter = new FragAdapter(getSupportFragmentManager(), fragments);
        //设定适配器
        ViewPager vp = (ViewPager)findViewById(R.id.viewpager);
        vp.setAdapter(adapter);
    }
}
```

### 使用FragmentPagerAdapter适配

首先我们定义一个适配器 去继承FragmentPagerAdapter
MyFragmentAdapter.java
``` java
public class MyFragmentAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> title;

    public MyFragmentAdapter(FragmentManager fm,List<String>title,List<Fragment>fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.title = title;
    }
	//返回要显示的fragment对象
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
	//设置标题
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
}
```
然后去绑定这个适配器

``` java
	//新建适配器
    MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),title,fragmentList);
    //绑定适配器
    viewPager.setAdapter(myFragmentAdapter);
```
### 使用FragmentStatePagerAdapter适配
同样要去继承FragmentStatePagerAdapter这个类
MyFragmentAdapter2.java
``` java
public class MyFragmentAdapter2 extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> title;

    public MyFragmentAdapter2(FragmentManager fm, List<String> title, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.title = title;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //设置标题
    @Override
    public CharSequence getPageTitle(int position) {
        return title.get(position);
    }
	/*
	下面的创建和销毁方法不用改写，能达到自动管理页卡的目的
	自动的删除销毁 保留3个
	 */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
}
```
然后进行绑定
``` java
MyFragmentAdapter2 myFragmentAdapter2 = new MyFragmentAdapter2(getSupportFragmentManager(),title,fragmentList);
        viewPager.setAdapter(myFragmentAdapter2);
```
## 关于监听器
设置监听器要实现`ViewPager.OnPageChangeListener`这个接口未实现的方法
``` java
 //设置监听器
 //setOnPageChangeListener已过时
 viewPager.addOnPageChangeListener(this);
```
关于监听器的三个方法
- onPageScrollStateChanged(int arg0)
  此方法是在状态改变的时候调用，其中arg0这个参数有三种状态（0，1，2）。arg0 ==1表示正在滑动，arg0==2表示滑动完毕了，arg0==0表示什么都没做。当页面开始滑动的时候，三种状态的变化顺序为（1，2，0）。

- onPageScrolled(int arg0,float arg1,int arg2) 
  当页面在滑动的时候会调用此方法，在滑动被停止之前，此方法回一直得到调用。其中三个参数的含义分别为：arg0 :当前页面，及你点击滑动的页面。arg1:当前页面偏移的百分比。arg2:当前页面偏移的像素位置。

- onPageSelected(int arg0) 
  此方法是页面跳转完后得到调用，arg0是你当前选中的页面的position。

## 相关补充

### 建议
- 关于标题栏实现的效果很不好，不能指定一个页面一次显示一个，或者全部显示，而且标题还滑动。所以注定主流的App都没有用这个玩意的。所以这里也只是一个过渡，在开发中，我们也不建议使用这两个东东。
- ViewPager更多的时候会与Fragment一起使用，这是一种很好的方法来管理各个页面的生命周期。Android提供了一些专门的适配器来让ViewPager与Fragment一起工作，也就是FragmentPagerAdapter与FragmentStatePagerAdapter。他们基本上可以满足大部分常见的永续需求，并且他们都有简单的代码样例来展示如何用他们来建立一个完整的用户页面。

### 需要注意的
不管是FragmentPagerAdapter还是FragmentStatePagerAdapter都继承于同一个父类-PagerAdapter。
PagerAdapter是基类适配器是一个通用的ViewPager适配器，相比PagerAdapter，FragmentPagerAdapter和FragmentStatePagerAdapter更专注于每一页是Fragment的情况。
使用fragment作为数据源我们用的更多的是封装好的FragmentPagerAdapter或者FragmentStatePagerAdapter。

--------

mViewPager.setOffscreenPageLimit()
这个方法是用来控制fragment不重新走生命周期的个数的(viewpager 屏幕外保存的 view 的个数)，打个比方一共4个fragment页面，如果mViewPager.setOffscreenPageLimit(3)，那么就是缓存3个，所有的fragment都只走一次生命周期，如果是mViewPager.setOffscreenPageLimit(2)，那么缓存2个，其中有一个fragment会在切换的时候重新走一遍生命周期，FragmentStatePagerAdapter和FragmentPagerAdapter都是这样，但是FragmentPagerAdapter设置setOffscreenPageLimit不影响fragment缓存的个数,而FragmentStatePagerAdapter缓存的fragment实例个数就是setOffscreenPageLimit设置的值+1。另外setOffscreenPageLimit的缺省值是1，设置0是无效的会被强制赋值成1。
缓存的越多切换就会越流畅，同时也消耗更多的内存。

### 关于适配器的文档
**PagerAdapter**

> PagerAdapter是用于“将多个页面填充到ViewPager”的适配器的一个基类，大多数情况下，你们可能更倾向于使用一个实现了PagerAdapter并且更加具体的适配器，例如FragmentPagerAdapter或者FragmentStatePagerAdapter。 当你实现一个PagerAdapter时，你至少需要重写下面的几个方法：
>
> - instantiateItem(ViewGroup, int) 
> - destroyItem(ViewGroup, int, Object) 
> - getCount() 
> - isViewFromObject(View, Object)
>
> PagerAdapter比很多AdapterView的适配器更加通用。ViewPager使用回调机制来显示一个更新步骤，而不是直接使用视图回收机制。如果需要时，PagerAdapter也可以实现视图回收方法，或者直接使用一种更加巧妙的方法来管理页面，比如直接使用能够管理自身事务的Fragment。
> ViewPager并不直接管理页面，而是通过一个key将每个页面联系起来。这个key用来跟踪和唯一标识一个给定的页面，且该key独立于adapter之外。PagerAdapter中的startUpdate(ViewGroup)方法一旦被执行，就说明ViewPager的内容即将开始改变。紧接着，instantiateItem(ViewGroup, int)和/或destroyItem(ViewGroup, int, Object)方法将会被执行，然后finishUpdate(ViewGroup)的执行就意味着这一次刷新的完成。当finishUpdate(ViewGroup)方法执行完时，与instantiateItem(ViewGroup, int)方法返回的key相对应的视图将会被加入到父ViewGroup中，而与传递给destroyItem(ViewGroup, int, Object)方法的key相对应的视图将会被移除。isViewFromObject(View, Object)方法则判断一个视图是否与一个给定的key相对应。
> 一个简单的PagerAdapter会选择将视图本身作为key，在将视图创建并加入父ViewGroup之后通过instantiateItem(ViewGroup, int)返回。这种情况下，destroyItem(ViewGroup, int, Object) 的实现方法只需要将View从ViewGroup中移除即可，而isViewFromObject(View, Object)的实现方法可以直接写成return view == object;。 PagerAdapter支持数据集的改变。数据集的改变必须放在主线程中，并且在结束时调用notifyDataSetChanged()方法，这与通过BaseAdapter适配的AdapterView类似。
> 一个数据集的改变包含了页面的添加、移除或者位移。ViewPager可以通过在适配器中实现getItemPosition(Object)方法来保持当前页面处于运行状态。
> 可以总结为：
> - instantiateItem(ViewGroup, int)负责初始化指定位置的页面，并且需要返回当前页面本身（其实不一定要View本身，只要是能唯一标识该页面的key都可以，不过初学者一般就先用View本身作为key就可以啦）；
> - destroyItem(ViewGroup, int, Object)负责移除指定位置的页面； 
> - isViewFromObject(View, Object)里直接写“return view == object;”即可（当然，如果你在instantiateItem(ViewGroup, int)里返回的不是View本身，那就不能这么写哦）； 
> - 在描述中并未提及到getCount()方法，不过这个比较简单，也很常见，就是返回要展示的页面数量。

**FragmentPagerAdapter**

> FragmentPagerAdapter继承自PagerAdapter ，主要用来展示多个Fragment页面，并且每一个Fragment都会被保存在fragment manager中。 
> FragmentPagerAdapter最适用于那种少量且相对静态的页面，例如几个tab页。每一个用户访问过的fragment都会被保存在内存中，尽管他的视图层级可能会在不可见时被销毁。这可能导致大量的内存因为fragment实例能够拥有任意数量的状态。对于较多的页面集合，更推荐使用FragmentStatePagerAdapter。
> 当使用FragmentPagerAdapter的时候对应的ViewPager必须拥有一个有效的ID集。 FragmentPagerAdapter的派生类只需要实现getItem(int)和getCount()即可。

**FragmentStatePagerAdapter**

>FragmentStatePagerAdapter继承自PagerAdapter，主要使用Fragment来管理每个页面。这个类同样用来保存和恢复fragment页面的状态。
>FragmentStatePagerAdapter更多用于大量页面，例如视图列表。当某个页面对用户不再可见时，他们的整个fragment就会被销毁，仅保留fragment状态。相比于FragmentPagerAdapter，这样做的好处是在访问各个页面时能节约大量的内存开销，但代价是在页面切换时会增加非常多的开销。 
>当使用FragmentPagerAdapter（注：API里这里写的是FragmentPagerAdapter，不过貌似应该是FragmentStatePagerAdapter？）的时候对应的ViewPager必须拥有一个有效的ID集。
>FragmentStatePagerAdapter的派生类只需要实现getItem(int)和getCount()即可。

## 参考
[ViewPager深入解析1](http://www.imooc.com/article/2580)
[ViewPager深入解析2](http://www.imooc.com/article/2742)
[ViewPager 详解](http://blog.csdn.net/harvic880925/article/details/38453725)
[FragmentPagerAdapter与FragmentStatePagerAdapter使用详解与区别](http://blog.csdn.net/zhaokaiqiang1992/article/details/40342411)
[ViewPager滑动事件OnPageChangeListener](http://blog.csdn.net/wwzqj/article/details/17053577)