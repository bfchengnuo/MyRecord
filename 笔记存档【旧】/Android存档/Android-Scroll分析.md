---
title: Android Scroll分析
date: 2016-09-12 10:23:51
tags: [Android]
categories: Android
---

滑动的操作方式有非常好的用户体验，开发中使用的也比较多，其中功能的实现和Scroll类有很多的联系，滑动一个view，本质上也就是移动一个view原理与动画效果非常的相似，通过不断的改变坐标来达到这一效果，所以这就需要对触摸事件(MotionEvent)和窗口坐标有一定的了解。
<!-- more -->
## Android中的坐标系

对于Android中的控件，有两种坐标系，以屏幕左上角为原点的绝对坐标系和以父控件左上角为原点的视图坐标系。
![](/image/dev/%E8%A7%86%E5%9B%BE%E5%9D%90%E6%A0%87%E7%B3%BB.jpg)
![](/image/dev/Android%E5%9D%90%E6%A0%87%E7%B3%BB.jpg)

> View提供了获取坐标的方法:
> getTop(): 获取View自身顶部到父布局顶边的距离
> getLeft(): 获取View自身左边到父布局左边的距离
> getRight(): 获取View自身右边到父布局左边的距离
> getBottom(): 获取View自身底部到父布局顶边的距离
>
> MotionEvent提供的获取坐标的方法
> getX(): 获取View的点击处距离父控件左边的距离，即视图坐标
> getY(): 获取View的点击处距离父控件顶边的距离，即视图坐标
> getRawX(): 获取View的点击处距离屏幕左边的距离，即绝对坐标
> getRawY(): 获取View的点击处距离屏幕顶边的距离，即绝对坐标

用一张图来表示的话就是：
![](/image/dev/%E8%8E%B7%E5%8F%96%E5%9D%90%E6%A0%87%E7%9A%84%E6%96%B9%E6%B3%95.png)

## 触控事件--MotionEvent

相关内容可以看下：
[Android中的事件分发机制](http://bfchengnuo.info/2016/09/03/Android%E4%B8%AD%E7%9A%84%E4%BA%8B%E4%BB%B6%E5%88%86%E5%8F%91%E6%9C%BA%E5%88%B6/#基础知识)
触控事件的运用一般和坐标有着密不可分的联系，一般我们常用的就是onTouchEvent方法了

```java
@Override
public boolean onTouchEvent(MotionEvent e){
    switch(e.getAction()){
        //手指按下
        case MotionEvent.ACTION_DOWN:
        break;
		//手指抬起
      	case MotionEvent.ACTION_UP:
        break;
		//手指移动
        case MotionEvent.ACTION_MOVE:
        break;
		//其他的手指按下
        case MotionEvent.ACTION_POINTER_DOWN:
        break;
		//其他的手指抬起
        case MotionEvent.ACTION_POINTER_UP:
        break;
    }
  	//事件已被消费，不再向上传递
    return true;
}
```
## 实现滑动的几种方法

View的滑动思想基本一致:当触摸View的时候，记录下当前触摸点的坐标，当手指移动时记录下移动后的触摸点坐标，进而获取到手指移动的偏移量并通过偏移量来修改View的坐标，从而产生滑动

### layout方法

在View进行绘制的时候，系统会调用onLayout()方法来确定自己的显示位置，我们可以通过在ACTION_MOVE中让View的left、top、right、bottom四个属性增加偏移量来控制View的坐标，这样每次移动后View都会调用Layout方法来重新布局，从而达到移动View的效果

```java
//使用绝对坐标，从view外(activity)设置
view.setOnTouchListener(new View.OnTouchListener() {
  @Override
  public boolean onTouch(View v, MotionEvent event) {
    int x = (int)event.getRawX();
    int y = (int) event.getRawY();
    switch (event.getAction()){
      case MotionEvent.ACTION_DOWN:
		//记录触摸点坐标
        mLastX = x;
        mLastY = y;
        break;
      case MotionEvent.ACTION_MOVE:
        //计算偏移量
        mOffsetX = x - mLastX;
        mOffsetY = y - mLastY;
        //在当前数值加上偏移量
        v.layout(v.getLeft()+mOffsetX, 
                 v.getTop()+mOffsetY, 
                 v.getRight()+mOffsetX, 
                 v.getBottom()+mOffsetY);
        //另外也可以通过offset方法来设置view的偏移量从而实现滑动，效果和layout一样
        //v.offsetLeftAndRight(mOffsetX);
        //v.offsetTopAndBottom(mOffsetY);

        //也可以使用重设layoutParams的方法，不过更推荐上面的方法
        //ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
        //layoutParams.leftMargin = v.getLeft() + mOffsetX;
        //layoutParams.topMargin = v.getTop() + mOffsetY;
        //v.setLayoutParams(layoutParams);

        //每次移动完成都要重新设置移动起始值
        mLastX = x;
        mLastY = y;
        break;
      case MotionEvent.ACTION_UP:
        break;
      default:
        break;
    }
    return false;
  }
});

```

当我们通过getLayoutParams()获取一个LayoutParams的时候，一般需要根据view所在的父布局来进行指定LayoutParams，比如如果父布局是LinearLayout那就应该是LinearLayout.LayoutParams，当我们通过LayoutParams改变一个view的位置时，通常改变的是这个view的Margin属性，所以除了LayoutParams还可以用ViewGroup.MarginLayoutParams，并且会更加方便，因为不需要考虑父布局
下面的是在自定义View里直接复写onTouchEvent方法，使用的是view的坐标系

```java
@Override
public boolean onTouchEvent(MotionEvent event) {

    int x = (int) event.getX();
    int y = (int) event.getY();

    switch (event.getAction()) {
        //手指按下
        case MotionEvent.ACTION_DOWN:
            lastX = x;
            lastY = y;
            break;
        //手指移动
        case MotionEvent.ACTION_MOVE:
            //计算偏移量
            int offsetX = x - lastX;
            int offsetY = y - lastY;

            //将布局的位置进行重绘
            layout(getLeft() + offsetX,
                    getTop() + offsetY,
                    getRight() + offsetX,
                    getBottom() + offsetY);
            break;
    }

    return true;
}
```
### ScrollTo/ScrollBy之类相关

scrollTo(int x, int y);代表移动到某一个坐标点
scrollBy(int x, int y);其实srollToBy方法在其内部是调用了scrollTo()方法，但是参数x是内容横向移动相应距离，参数y是内容纵向移动相应距离，也就是说直接在原来坐标的基础上再继续移动。
> 这两个方法都是使View的内容移动而不是本身移动，对于ViewGroup则是使其子View移动。

getScrollX() 就是当前view的左上角相对于母视图的左上角的X轴偏移量
getScrollY() 就是当前view的左上角相对于母视图的左上角的Y轴偏移量
这里需要注意的是：
关于Android的视图移动:对于上面需要取反才能按照期望的方向移动。其实，对于Android而言，界面相当于一张很大的画布，而屏幕相当于一块白板，只有处在白板位置的内容才会被现实出来，当我们移动的时候，其实移动的是画布上的白板，因此当我们设置移动距离为(5, 5)时，意味着我们的白板移动了这些距离，而View相对就移动了(-5,-5)的距离，因此会产生和期望值相反的情况，所以需要进行取反

### Scroller

上面提到了ScrollTo/ScrollBy，也就有必要提下Scroller这个类了，它们其实非常相似，有着千丝万缕的关系，我们发现通过上面的方法进行移动view有个特点：瞬时移动，对于一些场景这显得非常让人不爽....于是，通过Scroller就可进行平滑移动的效果。
至于Scroller类的实现原理也是非常的简单，概况来说就是把移动距离分成N个小段，每小段也是进行瞬时移动的，但是由于人眼的暂留性特点，我们看到的却是平滑的移动过程啦，下面就来说下如何使用Scroller类，一般需要三个步骤：

- 初始化Scroller
  用它的构造方法创建一个对象，都很熟了...
  `mScroller = new Scroller(context);`

- 重写computeScroll()方法,实现模拟滑动
  computeScroll方法是使用Scroller类的核心，**系统在绘制View的时候会在draw()方法中调用该方法**，这个方法实际上使用的就是ScrollTo方法啦，通过不断的移动小距离来实现整体上平滑移动的效果，通常我们会使用下面的模版代码：

  ```java
  @Override
  public void computeScroll() {
    	super.computeScroll();
    	//判断Scroller是否执行完毕
      if (mScroller.computeScrollOffset()){
          ((View)getParent()).scrollTo(mScroller.getCurrX(), 
                                       mScroller.getCurrY());
          //通过重绘来不断调用computeScroll
        	invalidate();
      }
  }
  ```

  Scroller提供getCurrX/getCurrX方法来获得当前的滑动坐标，还需要注意下invalidate这个方法，因为只能在computeScroll方法中获取模拟过程中的scrollX和scrollY坐标，computeScroll方法不会自动调用，只能通过invalidate()--->draw()--->computeScroll()来间接调用，模拟过程结束后computeScrollOffset会返回false，终端循环过程。

- startScroll 开启模拟过程
  startScroll 有两个重载，参数基本一致，区别只是是否可以指定持续时长，不设置就使用默认的，获取坐标的时候我们通常使用getScrollX/getScrollY来获取父视图中context所滑动到的点的坐标就可以了，至于这个值的正负和ScrollBy/ScrollTo中是一致的

  > public void startScroll (int startX, int startY, int dx, int dy) 默认持续时间250ms
  > public void startScroll (int startX, int startY, int dx, int dy, int duration)
  > 以提供的起始点和将要滑动的距离开始滚动。滚动会使用缺省值250ms作为持续时间。
  > 参数分别为滚动的偏移值(就是开始滚动的位置)/滚动距离
  > 正值表示上/左
  > (x和Y  分别是内容的left和内容的top并不是组件视图的left和top)
  > 补充：
  > mScroller.getCurrX() //获取mScroller当前水平滚动的位置
  > mScroller.getCurrY() //获取mScroller当前竖直滚动的位置
  > mScroller.getFinalX() //获取mScroller最终停止的水平位置
  > mScroller.getFinalY() //获取mScroller最终停止的竖直位置
  > mScroller.setFinalX(int newX) //设置mScroller最终停留的水平位置，没有动画效果，直接跳到目标位置
  > mScroller.setFinalY(int newY) //设置mScroller最终停留的竖直位置，没有动画效果，直接跳到目标位置

  ```java
  switch (event.getAction()){
      //一般的 我们都写在手指抬起这个事件中
  	case MotionEvent.ACTION_UP:
  	    //使用Scroller开启滚动，前两个参数为开始坐标，后两个为偏移量
  	    mScroller.startScroll(parent.getScrollX(), 
                                parent.getScrollY(), 
                                -100, 
                                -100);
  	    break;
  ```

### ViewDragHelper对象

在Android的support中提供了DrawerLayout和SlidingPaneLayout两个布局帮助我们实现侧边栏的滑动效果，这两个布局都是使用了ViewDragHelper类对滑动进行操作的，它基本可以实现不同的滑动、拖放需求，下面就来看看是如何使用的：

-    初始化ViewDragHelper
     ViewDragHelper通常定义在一个ViewGroup的内部，通过静态工厂的方式进行初始化：
     `mViewDragHelper = ViewDragHelper.create(this, callback);`
     第一个参数是要监听的View，通常需要是一个ViewGroup，第二个参数是一个回调，也是核心

- 拦截事件
    既然我们打算让触摸事件由ViewDragHelper进行处理，当然要进行拦截啦，如下

     ```java
     @Override
     public boolean onInterceptTouchEvent(MotionEvent ev) {
         return mViewDragHelper.shouldInterceptTouchEvent(ev);
     }

     @Override
     public boolean onTouchEvent(MotionEvent event) {
       //将触摸事件传递给ViewDragHelper，必要操作，返回true消费事件，不再向上传递
         mViewDragHelper.processTouchEvent(event);
         return true;
     }
     ```

- 重写ViewGroup的computeScroll()方法
     我们使用ViewDragHelper还是要重写computeScroll方法.....原因嘛，ViewDragHelper内部也是通过Scroller来实现平滑滚动的,下面是一段模版代码：

     ```java
     @Override
     public void computeScroll() {
       	//判断拖拽是否结束
         if (mViewDragHelper.continueSettling(true)) {
             // 即触发了computeScroll()来刷新x和y的值。
             ViewCompat.postInvalidateOnAnimation(this);
         }
     }
     ```

- 处理回调callback

     ```java
     private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

             //何时开始检测触摸事件，传递的child为触摸的view
             @Override
             public boolean tryCaptureView(View child, int pointerId) {
                 //当触摸的是mMainView的时候开始检测，也就是只有mMainView是可以拖动的
               	//返回ture则表示可以捕获该view
                 return mMainView == child;
             }

             //对应水平方向的滑动，left为在水平方向的移动距离，dx为较前一次的增量
             // 一般只需要返回left即可,如果你只需要一个方向上滑动效果可以只重写一个，默认返回值为0
             @Override
             public int clampViewPositionHorizontal(View child, int left, int dx) {
                 return left;
             }

             //对应垂直方向的滑动，返回值为数值方向的滑动距离
       		// 一般只需要返回top即可,如果你只需要一个方向上滑动效果可以只重写一个，默认返回值为0
             @Override
             public int clampViewPositionVertical(View child, int top, int dy) {
                 return top;
             }

             //拖动结束后调用
             @Override
             public void onViewReleased(View releasedChild, float xvel, float yvel) {
                 super.onViewReleased(releasedChild, xvel, yvel);
                 //当mainViwe滑动距离小于500时还原侧边栏，及坐标(0,0)的点
                    if (mMainView.getLeft() < 500) {
                        //相当于Scroller的startScroll方法
                        mViewDragHelper.smoothSlideViewTo(mMainView, 0, 0);
                        ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
                    } else {
                        //手指滑动超过500则打开菜单，及把mMainView移动到(300,0)坐标处
                        mViewDragHelper.smoothSlideViewTo(mMainView, 300, 0);
                        ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
                    }
                }
        	};
     ```

然后下面是一个侧滑菜单的例子,首先来看下布局文件：

```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.bfchengnuo.mydemo.ScrollDemo.DragHelperActivity">

    <com.bfchengnuo.mydemo.ScrollDemo.DragViewGroup
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:layout_width="300dp"
            android:layout_height="match_parent"
            android:background="@color/colorPrimary">
            <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="MenuView"/>
        </LinearLayout>
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="#ccc">
            <Button
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="MainView"/>
        </LinearLayout>
    </com.bfchengnuo.mydemo.ScrollDemo.DragViewGroup>
</RelativeLayout>
```

前面说过ViewDragHelper通常定义在一个ViewGroup的内部，所以我们也进行了自定义

```java
public class DragViewGroup extends FrameLayout {

    private ViewDragHelper mViewDragHelper;
    private View mMenuView,mMainView;
    private int mWidth;

    public DragViewGroup(Context context) {
        super(context);
        initView();
    }

    public DragViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DragViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        mViewDragHelper = ViewDragHelper.create(this,callback);
    }

    /**
     * 加载完布局后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
          //相对应布局文件中的第一个LinearLayout
        mMenuView = getChildAt(0);
          //相对应布局文件中的第二个LinearLayout
        mMainView = getChildAt(1);
    }

    /**
     * 组件大小改变时回调
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    //获取侧滑菜单的宽度
    mWidth = mMenuView.getWidth();
    }

    /**
     * 触摸事件相关
     * 拦截 or 处理/消费
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
      return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将触摸事件传给ViewDragHelper处理
        mViewDragHelper.processTouchEvent(event);
        return true;
    }
  
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        //何时开始检测触摸事件
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //当触摸的child是mMainView的时候开始检测
            return mMainView == child;
        }

        //处理水平方向的滑动(也就是让其能够在水平方向进行滑动)
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        //处理垂直方向的滑动，这里我们用不到，设置return 0
        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return 0;
        }

        //拖动结束后调用
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //让抬起手指后 缓慢/平滑的移动到指定位置
            //mainViwe滑动距离小于500时关闭侧滑菜单
            if (mMainView.getLeft() < 500){
                mViewDragHelper.smoothSlideViewTo(mMainView,0,0);
                ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
            }else {
                //打开菜单
                mViewDragHelper.smoothSlideViewTo(mMainView,300,0);
                ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
            }
        }
    };

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }
}
```

其他常用事件：
在用户触摸到View后调用
`onViewCaptured()`
在拖拽状态改变时回调，如idle、dragging等状态
`onViewDragStateChanged()`
在位置改变时回调，常用于滑动时更改scale进行缩放等效果
`onViewPositionChanged()`

### 属性动画

是的，通过属性动画也可以实现View的滑动，这方面单独写...嗯？
[Android动画笔记](http://bfchengnuo.com/2016/12/17/Android%E5%8A%A8%E7%94%BB%E7%AC%94%E8%AE%B0-%E4%B8%80/)

## 后记

总结自《Android群英传》第五章内容