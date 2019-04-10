---
title: ViewPager实现图片轮播广告效果
date: 2016-08-02 11:40:31
tags: [Android,ViewPager]
categories: Android
---

在家都呆了十多天了，完全没有干劲，什么都不想做 = =，关于这个功能，去问谷狗，浏览一遍感觉好复杂的说，于是拼命往后翻找简单的~~结果就是，谷狗的排名还是不错的，后面的更是惨不忍睹....
正好微信群里有人讨论这个，推荐的也是谷狗前面的几篇，于是就仔细看了下，结果发现：真尼玛简单！
果然，不走心，再怎么简单的也看懂，我还没达到那种看一眼就能理解的境界，更何况还是个英语渣o(￣▽￣*)ゞ))￣▽￣*)o

<!-- more -->
我看了下其实大部分都是一个套路，是有改动，不过也不大，这里就选了一个从简书看到的做模板了，~~等有时间可以看看其他的实现~~,先贴个地址：http://www.jianshu.com/p/2e577242fe87#

## 代码实现

还是贴下代码吧，虽然基本都是copy的....

布局文件的：
```html
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
                xmlns:tools="http://schemas.android.com/tools"
                android:id="@+id/container"
                android:layout_width="match_parent"
                android:layout_height="match_parent" >

  <!-- ViewPager -->
  <android.support.v4.view.ViewPager
                                     android:id="@+id/viewpager"
                                     android:layout_width="fill_parent"
                                     android:layout_height="180dip" />

  <LinearLayout
                android:layout_width="fill_parent"
                android:layout_height="wrap_content"
                android:layout_alignBottom="@id/viewpager"
                android:background="#44000000"
                android:gravity="center"
                android:orientation="vertical"
                android:padding="5dip" >

    <!-- Banner的文字描述 -->
    <TextView
              android:id="@+id/tv_banner_text_desc"
              android:layout_width="wrap_content"
              android:layout_height="wrap_content"
              android:textColor="@android:color/white" />

    <!-- 小圆点的父控件 -->
    <LinearLayout
                  android:id="@+id/ll_dot_group"
                  android:layout_width="fill_parent"
                  android:layout_height="wrap_content"
                  android:layout_gravity="center_horizontal"
                  android:layout_marginTop="5dip"
                  android:gravity="center_horizontal"
                  android:orientation="horizontal" >
    </LinearLayout>
  </LinearLayout>

</RelativeLayout>
```

因为小圆点共有两种状态，一个是 enable 为 true 的状态，和 enable 为 false 的状态。所以需要为小圆点编写一个 selector (选择器)的 xml 配置文件，放在 drawable 文件夹中。这里，为它命名为 dot_bg_selector.xml

```html
<?xml version="1.0" encoding="utf-8"?>
<selector xmlns:android="http://schemas.android.com/apk/res/android">

    <item android:drawable="@drawable/point_bg_enable" android:state_enabled="true"></item>
    <item android:drawable="@drawable/point_bg_normal" android:state_enabled="false"></item>

</selector>
```

然后就是两个shape 样式文件了，也是放在drawable文件夹下

```html
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval" >
    <corners android:radius="0.5dip" />
    <solid android:color="#aaFFFFFF" />
</shape>
```

选择状态↑，默认↓

```html
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="oval" >
    <corners android:radius="0.5dip" />
    <solid android:color="#55000000" />
</shape>
```

下面的代码不全部贴了，有点长，选择性的解释下添加控件的方法

```java
ImageView imageView = null;
View dot = null;
LayoutParams params = null;
for (int id : imageIDs) {
  imageView = new ImageView(this);
  imageView.setBackgroundResource(id);
  imageViewContainer.add(imageView);

  // 每循环一次添加一个点到线行布局中
  dot = new View(this);
  dot.setBackgroundResource(R.drawable.dot_bg_selector);
  params = new LayoutParams(5, 5); //参数为父控件的宽和高
  params.leftMargin = 10;  //设置点之间的边距
  dot.setEnabled(false);
  dot.setLayoutParams(params);
  llDotGroup.addView(dot); // 向线性布局中添加"点"
}
```

## 优化

### 触摸手势

我的想法是当手指按下或者滑动的情况下取消轮播，手指抬起的时候继续轮播，这个应该是要进行自定义控件，定义一个类继承viewpager，然后重新下面的方法：

```java
@Override
public boolean onTouchEvent(MotionEvent ev) {
  switch (ev.getAction()) {
    case MotionEvent.ACTION_DOWN:
    case MotionEvent.ACTION_MOVE:
      mIsTouching = true;
      break;
    case MotionEvent.ACTION_CANCEL:
    case MotionEvent.ACTION_UP:
      mIsTouching = false;
      break;
  }
  return super.onTouchEvent(ev);
}
```

然后去判断mIsTouching就行了，关于自定义控件这块还不是很了解，这里并没有进行实践，挖坑(~~其实关键原因就是懒~~)

### 无限循环的其它实现

上面采用的是在适配器的getCount()返回了一个超大的int，然后就是取余循环，但是这样貌似会比较的消耗资源，于是很容易就想：加个判断，到了它的size进行重置不就是了 = =，这样是有问题的，发现从最后跳到第一页的时候会比较卡，动画也是反的，并且在最后一页是拖不动的，因为：

> ViewPager多少页面取决于对应多少条数据，默认情况下PagerAdapter中getCount返回的值对应数据个数，正因为这样，每次当我们滑动页面的时候，都会触发调用getCount方法，等到最后一页后之所以拖动不了ViewPager内部估计是对当前显示的位置和getCount进行了比较判断判断。

我当时想的是：那把getCount写大一些不就行了，然而，应该是缓存机制，如果页卡是null也会崩 = =!

谷狗得，有种方法就是在页卡的最前和最后再加一页，也就是D|ABCD|A，起到缓冲的作用，具体的实现看下面：

> 理论
>
> 当滑到A前面的D图片时，此时再向左滑动是不能滑动的，因为到了viewpager的第一张（索引为0），按照逻辑如果还能滑动的话，再向左滑动的下一张会是C，因此我们需要重写方法onPageScrolled(int arg0, float arg1, int arg2)，当从第一张A完全滑动到D图片时，参数arg1为0.0，此时表示滑动已经完成，这时为了还能继续向做滑动，是不是应该把viewpager的当前页设置到倒数第二个D的位置？关键点就是这样的，此时用方法setCurrentItem(int, boolean)设置viewpager跳转到倒数第二个D那一页，其中boolean参数设置为false，viewpager就会瞬间跳转到倒数第二个D，没有动画效果，在我们的视觉效果来看，完全看不出任何变化，所以此时的页面实际就是倒数第二个D那一页，所以再向左边滑动时，B就显示出来了，给人的感觉就是，可以无限向左滑动，就这样实现了无限左右滑动。以此类推，向右滑动也一样，到了最后一个A的时候，跳转到正数第二个A，然后继续向右滑动就会显示B，也实现了无限向右滑动。此时向左向右无限循环滑动已经实现了。
>
> http://blog.csdn.net/ywl5320/article/details/40797023

下面看下关键代码：

```java
//这里说下适配器的一个关键函数
 	@Override  
    public void destroyItem(ViewGroup container, int position, Object object) {  
        ((ViewPager) container).removeView((View) object);// 完全溢出view,避免数据多时出现重复现象  
    } 

//下面主要贴下监听器的关键处理，不要忘了设置默认现实的是索引为1的页卡

    /** 
     * 这是重点： 此方法会监听viewpager的滑动过程，可获取滑动的百分比（arg1）参数。 
     * 这里判断的方法是：默认我们处于索引为1的页卡，向左滑会加载索引为0的页卡(最后一张)
     * 如果再这里不做相应的处理，再向左滑动就滑不动了，因为已经到了viewpager的第一张（索引为0）
     * 此时我们就要依靠参数arg1的值来判断是否已经完成了滑动到第一张 
     * 当arg1的值为0.0时，即已经滑动完成，此时我们就把viewpager的页面跳转到viewpager的倒数第二张页面上 
     * 使用setcurrentItem(Int,boolean)方法,当Boolean取值为FALSE时，就没有滑动效果，直接跳转过去，由于当前页的图片和要跳转到的页面一样所以在视觉效果上看不出闪烁 
     * 这样就很自然的跳转到了倒数第二张，然后继续向左滑动，就可以继续滑动了。给人的感觉就是能一直无限向左滑动。）向右滑动也类似 
     * 当滑动到最后一张时就跳到索引为1的那张，然后继续向右还可以滑动，这样就实现了左右循环无限滑动的效果。哈哈 
     */  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
        // TODO Auto-generated method stub  
        if(arg1 == 0.0)  
        {  
            if(arg0 == 0)  
            {  
                mvPager.setCurrentItem(imgsize - 2, false);  
                System.out.println("ok");  
            }  
            else if(arg0 == imgsize - 1)  
            {  
                mvPager.setCurrentItem(1, false);  
            }  
        }  
    }
```

## 相关补充

### SystemClock

> SystemClock.currentThreadTimeMillis(); // 在当前线程中已运行的时间 
> SystemClock.elapsedRealtime(); // 从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内） 
> SystemClock.uptimeMillis(); // 从开机到现在的毫秒书(手机睡眠的时间不包括在内)
> SystemClock.sleep(100); // 类似Thread.sleep(100);但是该方法会忽略InterruptedException 
> SystemClock.setCurrentTimeMillis(1000); // 设置时钟的时间，和System.setCurrentTimeMillis类似 
>
> // 时间间隔 
> long timeInterval = SystemClock.uptimeMillis() - lastTime; 
> // do something with timeInterval 
>
> http://blog.csdn.net/charein/article/details/12649157

### runOnUiThread方法更新UI

Android中更新UI的方式应该很多种，比较常用的Thread+Handler，但是这种方式较繁琐，还可以使用Activity的runOnUiThread方法来实现同样的功能。

> 利用Activity.runOnUiThread(Runnable)把更新ui的代码创建在Runnable中，然后在需要更新ui时，把这个Runnable对象传给Activity.runOnUiThread(Runnable)。 这样Runnable对像就能在ui程序中被调用。如果当前线程是UI线程,那么行动是立即执行。如果当前线程不是UI线程,操作是发布到事件队列的UI线程
>
> http://blog.csdn.net/luckyjda/article/details/8601517

附源码：

``` java
public final void runOnUiThread(Runnable action) {  
  if (Thread.currentThread() != mUiThread) {  
    mHandler.post(action);  
  } else {  
    action.run();  
  }  
}  
```

### 其他

关于实现轮播功能还有好多其他方案，比如ScheduledExecutorService、Timer之类的啊~~这里先写这一种吧~
并且在github上应该有不少这种开源库，学习下原理也是不错的嘛~

至于我为什么强调了viewpager，因为滑动滚屏这个功能实现不仅只有这一个，下面三个好像都可以
1.ViewPager
2.ViewFlipper
3.ViewFlow

详情：http://smallwoniu.blog.51cto.com/3911954/1308959