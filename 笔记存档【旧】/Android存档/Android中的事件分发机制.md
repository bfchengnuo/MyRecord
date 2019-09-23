---
title: Android中的事件分发机制
date: 2016-09-03 17:11:53
tags: [Android]
categories: Android
---

Android中有时我们不可避免的使用各种控件的嵌套，当使用到ListView和scrollview这种嵌套的时候问题就出现了，各种滑动冲突，各种失效，要解决这个问题就要去了解Android中的事件分发机制。
<!-- more -->
这里~~目前~~也只仅仅列出了基本的一些概念
## 基础知识

我们触摸的时候也是有几种状态的，最基本的手指按下、滑动、抬起这就是三种状态，Android把触摸事件封装在了一个类中：MotionEvent，一般与触摸有关的方法，都可以看到它，下面是主要的事件类型(用event.getAction()获得)：

> ACTION_DOWN: 表示用户开始触摸.按下
>  ACTION_MOVE: 表示用户在移动(手指或者其他)
>  ACTION_UP:表示用户抬起了手指 
> ACTION_CANCEL:表示手势被取消了,一些关于这个事件类型的讨论见:http://stackoverflow.com/questions/11960861/what-causes-a-motionevent-action-cancel-in-android
> 还有一个不常见的:
> ACTION_OUTSIDE: 表示用户触碰超出了正常的UI边界.
> 但是对于多点触控的支持,Android加入了以下一些事件类型.来处理,如另外有手指按下了,
> 有的手指抬起来了.等等:
> ACTION_POINTER_DOWN:有一个非主要的手指按下了.
> ACTION_POINTER_UP:一个非主要的手指抬起来了
> 这些其实都是一些int值，从0-6(非上面顺序)

当然MotionEvent不止封装了这些东西，还有一些其他的，比如触摸点的坐标啦~啥的

> 可以通过event.getX() /getY() 方法获取视图坐标，一般坐标原点在view的左上角
>
> 也可以通过event.getRawX() /getRawY() 来获取Android坐标系的坐标，一般坐标原点在屏幕左上角，也就是获取到的是绝对坐标啦

## 事件的处理过程

假如布局如下图所示：
![](/image/dev/%E5%B8%83%E5%B1%80.jpg)
如果我们在相关方法中进行打印log，我们就能得到下面的结论：
**事件的传递顺序：**
ViewGroupA--->ViewGroupB--->View
事件传递的时候先执行dispatchTouchEvent，再执行onInterceptTouchEvent(ViewGroup特有)  // 调度--拦截
**事件的处理顺序：**
View--->ViewGroupB--->ViewGroupA
事件处理的时候是执行onTouchEvent方法
**关于返回值**
事件传递的返回值：true---拦截，不继续；false---不拦截，继续
事件处理的返回值：true---处理了，不用再看了；false---交给上级处理
初始情况下，默认值都是false
从下面的一幅流程图就可以清楚的看出这其中的过程：
![](/image/dev/touch1.jpg)

## 三个重要方法

```java
public boolean dispatchTouchEvent(MotionEvent ev)
```

**事件传递过来的时候这个方法第一个被调用**，返回结果受当前View的ontouchEvent()方法或者下一级View的dispatchTouchEvent()方法返回值影响。
只要你触摸到了任何一个控件，就一定会调用**该控件的**dispatchTouchEvent方法。

```java
public boolean onInterceptTouchEvent(MotionEvent ev)
```

这个方法是在dispatchTouchEvent()方法内部掉用的，返回值用来判断是否拦截当前事件。

```java
public boolean onTouchEvent(MotionEvent ev)
```

也是在dispatchTouchEvent()方法中掉用，用来处理某一事件。

## 事件的传递规则

书中用了一段伪代码来表示

```java
public boolean dispatchTouchEvent(MotionEvent ev) {
    boolean consume = false;
    if (onInterceptTouchEvent(ev)) {
        consume = onTouchEvent(ev);
    } else {
        consume = child.dispatchTouchEvent(ev);
    }
    return consume;
}
```

也就是说当一个事件到来的时候，当前View的dispatchTouchEvent方法会被调用，在内部首先调用onInterceptTouchEvent判断是否拦截，如果拦截，将事件传递给自己的onTouchEvent对事件进行处理。如果不拦截，就将事件传递给子View，调用子View的dispatchTouchEvent方法，一直到事件被消费。

## 分析源码

以目前的功力，还是挺难的，挖坑...
见参考

根据别人的源码分析得出的一些结论：

首先在dispatchTouchEvent中最先执行的就是onTouch方法，如果在onTouch方法里返回了true，就会让dispatchTouchEvent方法直接返回true，不会再继续往下执行。

```java
button.setOnTouchListener(new OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;  //返回值很关键
    }
});
//----------------分割线----------------------
public boolean dispatchTouchEvent(MotionEvent event) {
  	//第一个条件：mOnTouchListener在setOnTouchListener中被赋值
  	//第二个条件：判断当前点击的控件是否是enable的(应该是是否可以点击，这里可以手动强制修改的)
  	//第三个条件：简单说就是看onTouch的返回值了
    if (mOnTouchListener != null && (mViewFlags & ENABLED_MASK) == ENABLED &&  
        mOnTouchListener.onTouch(this, event)) {
        //如果条件都成立返回true，拦截事件，不会再向下传递
      	//否则就去执行onTouchEvent(event)方法并返回
      	return true;
    }
  	//可以推测出onClick的调用肯定是在onTouchEvent(event)方法中
    return onTouchEvent(event);
}
```

**1. onTouch和onTouchEvent有什么区别，又该如何使用？**

从源码中可以看出，这两个方法都是在View的dispatchTouchEvent中调用的，onTouch优先于onTouchEvent执行。如果在onTouch方法中通过返回true将事件消费掉，onTouchEvent将不会再执行。
另外需要注意的是，onTouch能够得到执行需要两个前提条件，第一mOnTouchListener的值不能为空，第二当前点击的控件必须是enable的。因此如果你有一个控件是非enable的，那么给它注册onTouch事件将永远得不到执行。对于这一类控件，如果我们想要监听它的touch事件，就必须通过在该控件中重写onTouchEvent方法来实现。

**2. 为什么给ListView引入了一个滑动菜单的功能，ListView就不能滚动了？**

如果你阅读了[Android滑动框架完全解析，教你如何一分钟实现滑动菜单特效](http://blog.csdn.net/sinyu890807/article/details/8744400) 这篇文章，你应该会知道滑动菜单的功能是通过给ListView注册了一个touch事件来实现的。如果你在onTouch方法里处理完了滑动逻辑后返回true，那么ListView本身的滚动事件就被屏蔽了，自然也就无法滑动(原理同前面例子中按钮不能点击)，因此解决办法就是在onTouch方法里返回false。

**3. 为什么图片轮播器里的图片使用Button而不用ImageView？**

提这个问题的朋友是看过了[Android实现图片滚动控件，含页签功能，让你的应用像淘宝一样炫起来](http://blog.csdn.net/sinyu890807/article/details/8769904) 这篇文章。当时我在图片轮播器里使用Button，主要就是因为Button是可点击的，而ImageView是不可点击的。如果想要使用ImageView，可以有两种改法。第一，在ImageView的onTouch方法里返回true，这样可以保证ACTION_DOWN之后的其它action都能得到执行，才能实现图片滚动的效果。第二，在布局文件里面给ImageView增加一个android:clickable="true"的属性，这样ImageView变成可点击的之后，即使在onTouch里返回了false，ACTION_DOWN之后的其它action也是可以得到执行的。

来自：[郭神](http://blog.csdn.net/guolin_blog/article/details/9097463)

## 需要注意

- 一般在处理滑动冲突的时候重写相关方法，对于DOWN事件是不会拦截的，也就是返回false，在接下来的MOVE序列中判断是否需要拦截。因为如果拦截了DOWN，那么接下来的事件都不会传给子View了。
- 一般也不会拦截UP事件，因为UP一般为序列的最后一个事件，拦截不拦截对自己没有什么用处，但是子View就可能因为收不到UP而无法触发click事件。
- 关于和点击事件的关系，onTouch是优先于onClick执行的，并且onTouch执行了两次，一次是ACTION_DOWN，一次是ACTION_UP(你还可能会有多次ACTION_MOVE的执行，如果你手抖了一下)。因此事件传递的顺序是先经过onTouch，再传递到onClick。
- 当dispatchTouchEvent在进行事件分发的时候，只有前一个action返回true，才会触发后一个action。如果我们在onTouch事件里返回了false，就一定会进入到onTouchEvent方法中，如果控件可点击，最后不管如何一定会返回一个true，但是如果控件是不可点击的，那么就会返回一个false，最终导致事件无法进行


## 参考

http://blog.csdn.net/a62321780/article/details/51986515

http://gityuan.com/2015/09/19/android-touch/