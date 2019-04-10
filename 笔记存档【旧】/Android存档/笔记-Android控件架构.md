---
title: 笔记-Android控件架构
date: 2016-08-11 16:32:42
tags: [Android,控件架构]
categories: Android
---
最近抽空看了下群英传，控件啥的以前就只会用，背后的原理啥的都不造，看过才知道还是蛮复杂的，一会半会也消化不了，慢慢来~
对于以后自定义控件，自定义动画是很有帮助的，是基础....
<!-- more -->
在Android中控件大致被分为两种，view控件和ViewGroup控件，ViewGroup作为父控件可以包含多个view控件，通过ViewGroup整个界面上的控件形成了一个树形结构，这就是我么说的控件树。
**上层控件负责下层控件的测量与绘制，并传递交互事件**
我们经常用到的`Button btn = (Button)findViewById(R.id.btn);`通常情况下，就是在View树中按照[深度优先](https://zh.wikipedia.org/wiki/%E6%B7%B1%E5%BA%A6%E4%BC%98%E5%85%88%E6%90%9C%E7%B4%A2)遍历算法查找到该View．
每棵控件树的顶部都有一个ViewParent对象，是整棵树的核心，所有的交互和管理事件都是由他来统一的调度和调配，从而对整个视图进行整体的控制。

![view树结构](http://obb857prj.bkt.clouddn.com/view%E6%A0%91%E7%BB%93%E6%9E%84.png)

上面我们介绍了View树，接下来我们对照View树引入Android界面架构，在Android中，每一个Activity都包含一个Window对象，在Android中Window对象通常由PhoneWindow实现，PhoneWindow将一个DecorView设置成整个应用窗口的根View．在显示上，DecorView将整个屏幕分为两部分，TitleView和ContentView，关于ContentView，是一个id叫作content的FrameLayout，我们通常`setContentView(R.layout.activity_main)`就是把布局设置在FrameLayout里的。
这里面所有的view的监听事件都通过WindowManagerService来进行接收，并通过activity对象来回调相应的onClickListener。
这里就可以解答我们的一个疑惑，为什么通过`requestWindowFeature(Window.FEATURE_NO_TITLE)`设置全屏必须在setContentView之前调用了(当程序在onCreate方法调用setContentView后，ActivityManagerService会回调onResume方法，此时会把DecorView添加到PhoneWindow中，让其显示出来)

![UI界面架构](http://obb857prj.bkt.clouddn.com/UI%E7%95%8C%E9%9D%A2%E6%9E%B6%E6%9E%84.png)

## view的测量

我们要绘制(画)View首先要知道它的大小和位置吧，所以必定先要进行测量，这个过程在onMeasure方法中进行。

**View测量的标尺-----MeasureSpec类**

MeasureSpec是一个32位的int值，其中高两位代表着View的测量模式，低三十位为测量的大小，在MeasureSpec类中，计算时使用位运算提高并优化了效率，这里又提到一个新名词---**测量模式**，在Android系统中，View的测量模式有以下三种：

- EXACTLY
  精确值模式，在手动指定了控件的 layout _ width 或 layout _ heigth 属性为**具体数值**的时候，或者指定为 match _ parent 时，系统使用的是 EXACTLY 模式。

- AT_MOST
  最大值模式，在手动指定了控件的 layout _ width 或 layout _ heigth 属性为 wrap _ content 的时候，控件大小一般随着控件的子控件或内容变化而变化，只要不超过父控件允许的最大尺寸即可。

- UNSPECIFIED
  表示开发人员可以将视图按照自己的意愿设置成任意的大小，没有任何限制。这种情况比较少见，不太会用到。

View 类默认的 onMeasure() 方法只支持 EXACTLY 模式，所以如果自定义控件的时候不重写 onMeasure() 方法的话，就只能使用 EXACTLY 模式。控件可以响应你指定的具体宽高值或者是 match _ parent 属性。但是如果需要 View 支持 wrap _ content 属性，就必须重写 onMeasure() 方法来指定 wrap _ content 模式时的大小。

通过MeasureSpec类我就获取到了测量模式和大小，就可以进行绘制了。

下面我们就以view为例来分析下onMeasure：

```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
}
//我们进去view类，就是调用的下面
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
	getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
}
```

在这里通过 getDefaultSize() 来从 MeasureSpec 中获取相应的大小以及模式，最后转换为一个 int 类型给 setMeasuredDimension() 作为参数进行最后测量的结果，我们看看这个 getDefaultSize()。

```java
public static int getDefaultSize(int size, int measureSpec) {
    int result = size;
    int specMode = MeasureSpec.getMode(measureSpec); //获取测量模式 前2位
    int specSize = MeasureSpec.getSize(measureSpec); //获取测量数值 后30位

    switch (specMode) {
    case MeasureSpec.UNSPECIFIED:
        result = size;
        break;
    case MeasureSpec.AT_MOST:
    case MeasureSpec.EXACTLY:
        result = specSize;
        break;
    }
    return result;
}
```

从这里就可以证明前面所说的View 在默认情况下只支持 EXACTLY 模式，而且在不设置指定的宽高的情况下会把父控件的宽高传过来，但是如果需要 View 支持 wrap _ content 属性，也就是 AT _ MOST，就必须重写 onMeasure() 方法来指定 AT _ MOST 模式时的大小。

下面我们就来重写下，让它支持 AT _ MOST 模式：

```java
@Override
protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
  setMeasuredDimension(getSize(widthMeasureSpec),getSize(heightMeasureSpec));
}
//重写测量方法
private int getSize(int  MeasureSpec) {
  //初始化一个返回值变量
  int result;
  //获得测量模式
  int specMode = View.MeasureSpec.getMode(MeasureSpec);
  //获得测量大小
  int specSize = View.MeasureSpec.getSize(MeasureSpec);
  //判断模式是否是 EXACTLY
  if (specMode == View.MeasureSpec.EXACTLY) {
    //如果模式是 EXACTLY 则直接使用specSize的测量大小
    result  = specSize;
  }else{
    //如果是其他两个模式，先设置一个默认大小值 200
    result = 200;
    //如果是 AT_MOST 也就是 wrap_content 的话，就取默认值 200 和 specSize 中小的一个为准。
    if (specMode == View.MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
    }
  }
  return result;
}
```

## View的布局

布局的前提是已经对View进行了量算，View通过调用layout()方法进行布局，布局的目的是让Android知道View在其父控件中的位置(可以看看后面的ViewGroup的测量)，即距父控件四边的距离left、right、top、bottom。布局是绘图的基础，只有完成了布局，才能对View进行绘图。

Android在对View树进行自上而下的布局时，采用的是深度优先算法，而非广度优先算法，即遍历到某个View时，Android会首先沿着该View一直纵向遍历并布局到处于叶子节点的View，只有对该View及其所有子孙View（如果存在子孙View的话）完成布局后，才会布局该View的兄弟节点View。

详细解析可以看[源码解析Android中View的layout布局过程](http://blog.csdn.net/iispring/article/details/50366021)

## View的绘制

View 的绘制过程是在 onDraw() 方法中，并且这个方法是空的，每个view都有自己的表现(绘制)方式嘛~
绘制必须要用到一个叫 Canvas 的对象，这个对象等于一块画布，我们可以在上面作画，那现在有了画布，我们还需要一支笔，那就是 Paint 对象。

下面有一个例子：

```java
public class MyView extends View {
  //创建一个画笔对象
  private Paint mPaint;
  public MyView(Context context, AttributeSet attrs) {
    super(context, attrs);
    //初始化画笔对象
    mPaint = new Paint();
  }
  
  @Override
  protected void onDraw(Canvas canvas) {
    //设置画笔的颜色为蓝色
    mPaint.setColor(Color.BLUE);
    //使用画笔画一个矩形
    canvas.drawRect(0,0,50,50,mPaint);
    //设置画笔的颜色的黄色
    mPaint.setColor(Color.YELLOW);
    //设置画笔的字体大小为40
    mPaint.setTextSize(40);
    //使用画笔写出一行字
    canvas.drawText("我可是用笔写的", 0, 80, mPaint);
  }
}
```

效果就是这个样子：

![QQ截图20160128161413](http://blog.qiji.tech/wp-content/uploads/2016/01/QQ%E6%88%AA%E5%9B%BE20160128161413.png)

## ViewGroup的测量和绘制

- 测量

  我们都知道ViewGroup会去管理其子view，如果把ViewGroup的大小设置为wrap_content的时候，ViewGroup就要对其子view进行遍历，调用子view的Measure方法获得测量结果，从而来决定自己的大小。
  前面所说的测量就是在这个地方被调用的。

- 绘制

  ViewGroup一般不需要绘制，因为本身也没什么要绘制的东西，如果不是指定了背景颜色那么ViewGroup的onDraw()都不会被调用，但是它会使用dispatchDraw()方法来绘制其子view，过程还是通过遍历所有的子view，并调用子view的绘制方法来完成绘制工作。

## 关于LayoutParams

今天在看关于实现侧滑菜单的教程，看到使用getLayoutParams()的时候有点懵逼，一直认为获取到的就是个ViewGroup，结果就是怎么都想不通，找谷狗才知道了以下内容，总结一句话就是：
LayoutParams保存了一个View的布局参数，因此经常通过改变LayoutParams来达到动态修改一个布局的位置参数，从而达到改变View位置的效果。

> 其实这个LayoutParams类是用于child view（子视图） 向 parent view（父视图）传达自己的意愿的一个东西（孩子想变成什么样向其父亲说明）其实子视图父视图可以简单理解成:
> 一个LinearLayout 和 这个LinearLayout里边一个 TextView 的关系, TextView 就算LinearLayout的子视图 child view 。需要注意的是LayoutParams只是ViewGroup的一个内部类，也就是ViewGroup里边*这个LayoutParams类是 base class 基类*，**实际上每个不同的ViewGroup都有自己的LayoutParams子类**

如果还是不太明白，下面的描述更加直白：

> LayoutParams相当于一个Layout的信息包，它封装了Layout的位置、高、宽等信息。假设在屏幕上一块区域是由一个Layout占领的，如果将一个View添加到一个Layout中，最好告诉Layout用户期望的布局方式，也就是将一个认可的layoutParams传递进去。
> 可以这样去形容LayoutParams，在象棋的棋盘上，每个棋子都占据一个位置，也就是每个棋子都有一个位置的信息，如这个棋子在4行4列，这里的“4行4列”就是棋子的LayoutParams。

## 参考

http://blog.qiji.tech/archives/4410
http://blog.csdn.net/liuhaomatou/article/details/22899925

[加个我整理的自定义控件初步链接](http://bfchengnuo.com/2016/08/05/%E8%87%AA%E5%AE%9A%E4%B9%89%E6%8E%A7%E4%BB%B6%E5%88%9D%E6%AD%A5/)