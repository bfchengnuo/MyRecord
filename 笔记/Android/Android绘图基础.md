---
title: Android绘图基础
date: 2016-12-14 20:35:09
tags: [Android,UI布局]
categories: Android
---

想要绘制一个漂亮的页面真的不是一般的难呐，其中颜色、图片处理那块跳过了，因为需要矩阵的相关知识，无奈已经忘光了......
于是只好看下简单的绘图知识了呢....
<!-- more -->
数学很重要！对那些漂亮的开源控件肃然起敬
我发现我再来回顾的时候看不太懂了，要配合 [Android控件架构](http://bfchengnuo.com/2016/08/11/%E7%AC%94%E8%AE%B0-Android%E6%8E%A7%E4%BB%B6%E6%9E%B6%E6%9E%84/) 和 [View与ViewGroup，Window之间的关系](http://bfchengnuo.com/2016/06/17/View%E4%B8%8EViewGroup%EF%BC%8CWindow%E4%B9%8B%E9%97%B4%E7%9A%84%E5%85%B3%E7%B3%BB/) 以及 [自定义控件初步](http://bfchengnuo.com/2016/08/05/%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8E%A7%E4%BB%B6%E5%88%9D%E6%AD%A5/) 比较好

## 2D绘图基础

在Android中，系统通过提供的Canvas对象来提供绘图的方法，它提供了各种绘制图像的API：

- `drawPoint(x, y, paint)`---->//点
- `drawLine(startX, startY, endX, endY, paint)`----->//线(可传入数组绘制多条直线)
- `drawRect(left, top, right, bottom, paint)`---->//矩形
- `drawRoundRect(left, top, right, bottom, radiusX, radiusY, paint)`---->//圆角矩形
- `drawCircle(circleX, circleY, radius, paint)`---->//圆
- `drawArc(left, top, right, bottom, startAngle, sweepAngle, useCenter, paint)`---->//扇形和弧形
  扇形：useCenter为true
  弧形：useCenter为false
- `drawOval(left, top, right, bottom, paint)`---->//椭圆(通过椭圆的外接矩形来实现的)
- `drawText(text, startX, startY, paint)`---->//文本
  `drawPosText(text, floatArr, paint)` //传入一个数组在指定位置绘制文本
- `drawPath(path, paint)`---->//绘制路径 (path是个对象)
- `drawColor(color,mode)`---->//填充颜色，整个画布进行填充
  可使用`canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);` 进行清屏处理

可以看出画笔Paint也是很重要的，它的一些常用属性：

- setAntiAlias()---->//设置画笔的锯齿效果
- setColor()
- setARGB()
- setAlpha()
- setTextSize()
- setStyle()---->//设置画笔的风格如空心或实心
  空心：`setStyle(Paint.Style.STROKE)`
  实心：`setStyle(Paint.Style.FILL)`
- setStrokeWidth()---->//设置空心边框的宽度

## 了解Canvas

首先看下最常用的几个方法：

- Canvas.save()
- Canvas.restore()
- Canvas.translate()
- Canvas.rotate()

### Android绘图的坐标体系

在Android中，默认的坐标零点位于**屏幕左上角**，**向下**为Y轴的正方向，**向右**为X轴的正方向
Android中的绘图一般就困难在坐标的计算上

### 方法的解释

先说Canvas.save()，字面意思就是保存画布，它的作用就是**将之前的所有已绘制的图像保存起来**，让后续的操作像是在新的画布上一样，类似PS中的图层概念

Canvas.restore()这个方法可以理解为PS中合并图层的操作，它的作用就是**将save()之前的图像与save()之后的图像合并**

至于Canvas.translate()字面意思是画布平移，理解成**坐标系的平移**会更好一点，如前面所说，默认的坐标系零点在屏幕左上角，在调用了`Canvas.translate(X, Y)`之后原点就变成了(X,Y)，让绘图更加灵活

Canvas.rotate()和上面的translate类似，字面意思是画布翻转，也是理解成坐标系的翻转会更好些，作用就是**将坐标系旋转一定的角度**

### Layer图层

Android中也借鉴了PS中的图层概念，比如可以使用`saveLayer()`来创建一个图层，需要注意的是，图层同样是基于栈(先进后出)的结构进行管理的
一般的，Android中调用[`saveLayer()/savaLayerAlpha()`]方法将一个图层入栈，使用[`restore()/restoreToCount()`]方法将一个图层出栈。
入栈的时候，后面所有的操作都发生在这个图层上，而出栈的时候，则会把图像绘制到上层的Canvas

## 关于SurfaceView

SurfaceView是View类的子类，**可以直接从内存或者DMA等硬件接口取得图像数据**，是个非常重要的绘图视图。它的特性是：**可以在主线程之外的线程中向屏幕绘图上。这样可以避免画图任务繁重的时候造成主线程阻塞，从而提高了程序的反应速度。**在游戏开发中多用到SurfaceView，游戏中的背景、人物、动画等等尽量在画布canvas中画出。
如果自定义的view需要频繁刷新，或者刷新时数据处理量比较大，那么可以考虑使用SurfaceView来取代View
为什么使用SurfaceView：

> 首先我们知道View类如果需要更新视图，必须我们主动的去调用invalidate()或者postInvalidate()方法来再走一次onDraw()完成更新。但是呢，Android系统规定屏幕的刷新间隔为16ms，如果这个View在16ms内更新完毕了，就不会卡顿，但是如果逻辑操作太多，16ms内没有更新完毕，剩下的操作就会丢到下一个16ms里去完成，这样就会造成UI线程的阻塞，造成View的运动过程掉帧，自然就会卡顿了。
>
> 文／逝水比喻时光
> 原文链接：http://www.jianshu.com/p/07fa4180f0a5

### 与View的区别

与View比他们很像是孪生兄弟，但当然是有不同的，主要体现在：

- View主要适用于主动更新的情况下(因为需要手动调用方法进行UI的刷新)，而SurfaceView主要适用于被动更新，例如频繁的刷新
- View在主线程中对画面进行刷新，而SurfaceView通常会通过一个子线程来对页面进行刷新
- View在绘图时没有使用双缓冲机制，而SurfaceView在底层实现机制中就已经实现了双缓冲机制

### SurfaceView的使用

首先SurfaceView也是一个View，它也有自己的生命周期。因为它需要另外一个线程来执行绘制操作，所以我们可以在它生命周期的初始化阶 段开辟一个新线程，然后开始执行绘制，当生命周期的结束阶段我们插入结束绘制线程的操作。这些是由其内部一个SurfaceHolder对象完成的。 SurfaceHolder，顾名思义，**它里面保存了一个队Surface对象的引用**，而我们执行绘制方法就是操作这个 Surface，SurfaceHolder因为保存了对Surface的引用，所以使用它来处理Surface的生命周期，说到底 SurfaceView的生命周期其实就是Surface的生命周期，因为SurfaceHolder保存对Surface的引用，所以使用 SurfaceHolder来处理生命周期的初始化。首先我们先看看建立一个SurfaceView的大概步骤

- 首先这个自定义的SurfaceView类必须继承SurfaceView实现`SurfaceHolder.Callback`接口。

- 实现`SurfaceHolder.Callback`中的抽象方法

  ```java
  public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){}　　//在surface的大小发生改变时激发

  public void surfaceCreated(SurfaceHolder holder){}　　//在创建时激发，一般在这里调用画图的线程。

  public void surfaceDestroyed(SurfaceHolder holder) {}　　//销毁时激发，一般在这里将画图的线程停止、释放。
  ```

- 初始化SurfaceView，一般写在构造方法中，进行SurfaceHolder的获取以及设置回调方法

  ```java
  // 给SurfaceView当前的持有者一个回调对象。
  abstract void addCallback(SurfaceHolder.Callback callback);

  private void init() {
      mHolder = getHolder();
      mHolder.addCallback(this);
  }
  ```

- 在`surfaceCreated`方法里开启一个子线程，在这个子线程中开启一个由Flag控制的While循环，用于不断地绘制。
  在循环中通过`SurfaceHolder`对象的`lockCanvas`方法获得一个Canvas对象用于绘制。
  每次绘制完成通过`SurfaceHolder`对象`unlockCanvasAndPost`方法传入Canvas对象完成更新。
  最后要在`surfaceDestroyed`方法中去改变while循环的Flag为false，结束子线程的绘制。
  这里说明的是，对于开启一个子线程可以通过线程内部类(继承Thread)，也可以在本类中实现Runnable接口的方式

  ```java
  // 锁定画布，一般在锁定后就可以通过其返回的画布对象Canvas，在其上面画图等操作了。
  abstract Canvas lockCanvas();

  // 锁定画布的某个区域进行画图等..因为画完图后，会调用下面的unlockCanvasAndPost来改变显示内容。
  // 相对部分内存要求比较高的游戏来说，可以不用重画dirty外的其它区域的像素，可以提高速度。
  abstract Canvas lockCanvas(Rect dirty);

  // 结束锁定画图，并提交改变。
  abstract void unlockCanvasAndPost(Canvas canvas);
  ```

这里需要注意一点：在调用doDraw执行绘制时，因为SurfaceView的特点，**它会保留之前绘制的图形(继续上一次的Canvas对象)**，所以你需要先清空掉上一次绘制时留下的图形。(View则不会，它默认在调用View.onDraw方法时就自动清空掉视图里的东西)，如果需要擦除，则可以在绘制之前通过`drawColor()`方法来进行清屏操作。

### 示例代码

下面基本可以当模版用

```java
public class PencilView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	private boolean mIsRunning;

    public PencilView(Context context) {
    this(context, null);
    }

    public PencilView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PencilView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRunning = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRunning = false;
    }

    @Override
    public void run() {
        while (mIsRunning) {
            draw();
        }
    }

    private void draw() {
        mCanvas = mHolder.lockCanvas();
        if (mCanvas != null) {
            try {
               //使用获得的Canvas做具体的绘制
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
```

需要注意下，最好把`unlockCanvasAndPost(mCanvas)`放到finally代码块中，以保证每次都能将内容提交。

有时候，绘制不需要那么频繁，因此我们可以在子线程中进行sleep操作，尽可能的节省系统资源。

```java
@Override
public void run() {
    long start = System.currentTimeMillis();
    while (mIsRunning) {
    	draw();
    }
    long end = System.currentTimeMillis();
    //50-100
    if(end - start){
      try{
        Thread.sleep(100 - (end - start));
      }catch(InterruptedException e){
        e.printStackTrace();
      }
    }
}
```

通过判断draw()方法所使用的逻辑时长来判断sleep的时长，这是一个非常通用的解决方案，代码中100ms是一个大致的经验值，这个值的取值一般在50ms到100ms左右。

### 参考

[Android之SurfaceView使用总结](http://www.cnblogs.com/devinzhang/archive/2012/02/03/2337559.html)
[Android SurfaceView的基本使用](http://www.jianshu.com/p/07fa4180f0a5)
[Android中SurfaceView的使用详解](http://blog.csdn.net/listening_music/article/details/6860786)