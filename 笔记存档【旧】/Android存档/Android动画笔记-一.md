---
title: Android动画笔记(一)
date: 2016-12-17 18:01:09
tags: [Android,动画]
categories: Android
---

记录Android中关于Activity的切换动画、属性动画、补间动画(视图动画)的知识点
Android动画入门篇，无深入分析
<!-- more -->
## Android动画

本篇主要介绍了Activity的切换动画、属性动画以及补间动画(视图动画)
在刚开始的时候Android给我们提供了两种动画：逐帧动画和补间动画(视图动画)
逐帧动画的工作原理很简单，其实就是将一个完整的动画拆分成一张张单独的图片，然后再将它们连贯起来进行播放，类似于动画片的工作原理。
补间动画则是可以对View进行一系列的动画操作，包括淡入淡出、缩放、平移、旋转四种。
Android 3.0版本开始，系统给我们提供了一种全新的动画模式，属性动画。如今，它基本已经替代了早期的那两种动画
至于原因，可以看到原来的补间动画只支持四种模式，在可扩展方面也有很大的局限性，并且它只**支持对View进行操作**，还有一个最大的缺陷就是它**只是改变了View的显示效果而已，而不会真正去改变View的属性。**而属性动画就没有上面的那些问题了

## Activity切换动画

首先先简单说下Activity动画，主要是设置Activity的切换动画，相对来说比较简单..吧..

### overridePendingTransition方式

设置动画是调用`overridePendingTransition`方法，可以直接在Activity当中直接调用，它接受两个参数，第一个是起始动画(目标Activity的进入动画)，第二个是结束动画(当前Activity的离开动画)，如果不需要动画则可以传值为0，**此方法在`startActivity()`或者是`finish()`后调用**，在切换或是退出时就会调用此动画。

调用系统自带的淡入淡出：
`overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);`

使用的系统的从左向右滑动的效果
`overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);`

#### Activity动画切换编写

通常我们使用XML文件来进行描述，常使用的标签有四种类型组成：alpha、scale、translate、rotate。动作定义文件应该存放在`res/anim`文件夹下，访问时采用`R.anim.XXX.xml`的方式

XML的文件配置中：

| XML文件属性   | 作用           |
| --------- | ------------ |
| alpha     | 渐变透明度动画效果    |
| scale     | 渐变尺寸伸缩动画效果   |
| translate | 画面转换位置移动动画效果 |
| rotate    | 画面转移旋转动画效果   |

例如：slide_out_right

```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android">
    <translate
        android:duration="@android:integer/config_mediumAnimTime"
        android:fromXDelta="0"
        android:toXDelta="100%p"/>
    <alpha
        android:duration="@android:integer/config_mediumAnimTime"
        android:fromAlpha="1.0"
        android:toAlpha="1.0"/>
</set>
```

[四种动画的基本使用点这里查看](https://github.com/bfchengnuo/Android_learn/blob/master/%E7%AC%AC%E4%BA%8C%E9%98%B6%E6%AE%B5code/XML%E4%B9%8Banimation%E5%8A%A8%E7%94%BB/test.xml)

### style方式定义切换动画

*首先要定义Application的style*

```xml
<!-- 系统Application定义 -->
<application
        Android:allowBackup="true"
        Android:icon="@mipmap/ic_launcher"
        Android:label="@string/app_name"
        Android:supportsRtl="true"
        Android:theme="@style/AppTheme">
```

*然后定义具体的AppTheme样式*
其中这里的`windowAnimationStyle`就是我们定义Activity切换动画的style。而`@anim/slide_in_top`就是我们**定义的动画文件**，也就是说通过为Appliation设置style，然后为`windowAnimationStyle`设置动画文件就可以**全局的为Activity的跳转配置动画效果。**

```xml
<!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="Android:windowAnimationStyle">@style/activityAnim</item>
    </style>

<!-- 使用style方式定义activity切换动画 -->
    <style name="activityAnim">
        <item name="Android:activityOpenEnterAnimation">@anim/slide_in_top</item>
        <item name="Android:activityOpenExitAnimation">@anim/slide_in_top</item>
    </style>
```

而在`windowAnimationStyle`中存在四种动画：

```java
activityOpenEnterAnimation // 用于设置打开新的Activity并进入新的Activity展示的动画
activityOpenExitAnimation  // 用于设置打开新的Activity并销毁之前的Activity展示的动画
activityCloseEnterAnimation  // 用于设置关闭当前Activity进入上一个Activity展示的动画
activityCloseExitAnimation  // 用于设置关闭当前Activity时展示的动画
```

### ActivityOptions实现跳转动画

上面我们讲解的通过`overridePendingTransition`方法基本上可以满足我们日常中对Activity跳转动画的需求了，但是MD风格出来之后，`overridePendingTransition`这种老旧、生硬的方式怎么能适合我们的MD风格的App呢？好在google在新的sdk中给我们提供了另外一种Activity的过度动画——ActivityOptions。并且提供了兼容包——ActivityOptionsCompat。
`ActivityOptionsCompat`是一个静态类，提供了相应的Activity跳转动画效果，通过其可以实现不少炫酷的动画效果。

在跳转的Activity中设置contentFeature

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // 设置contentFeature,可使用切换动画
    getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
    Transition explode =
          TransitionInflater.from(this).inflateTransition(Android.R.transition.explode);
    getWindow().setEnterTransition(explode);

    setContentView(R.layout.activity_three);
}
```

这里的setFeature(requestFeature)就是为activity的窗口设置特性，不同的特性对应不同的布局方式，比如可以设置无toolbar模式，有toolbar模式等等。
而这里设置的是需要过渡动画，并且我们获取了Android中内置的explode动画，并设值给了Activity的window窗口对象，这样当Activity被启动的时候就会执行explode所带便的动画效果了。

然后就是在`startActivity()`执行跳转逻辑的时候调用`startActivity()`的重写方法(`public void startActivity(Intent intent, @Nullable Bundle options)`)
这里我们传入了`ActivityOptions.makeSceneTransitionAnimation`，该方法表示将Activity a平滑的切换到Activity b，其还有几个重载方法可以指定相关的View，即以View为焦点平滑的从Activity a切换到Activity b。

```java
/**
 * 点击按钮,实现Activity的跳转操作
 * 通过Android5.0及以上代码的方式实现activity的跳转动画
 */
button3.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, ThreeActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
    }
});
```

### ActivityOptions与style的方式

使用ActivityOptions之后内置的动画效果通过style的方式来实现，这种方式其实就是通过style的方式展示和使用ActivityOptions过度动画
*编写过度动画文件*

```xml
<explode xmlns:Android="http://schemas.Android.com/apk/res/Android"
    Android:duration="300" />
```

首先我们需要在Application项目res目录下新建一个**transition**目录，然后创建资源文件，然后使用这些系统自带的过渡动画效果，这里设置了过度时长为300ms。
*定义style文件*
在Application的style文件中添加我们刚刚编写的动画文件：

```xml
<item name="Android:windowEnterTransition">@transition/activity_explode</item>
<item name="Android:windowExitTransition">@transition/activity_explode</item>
```

*调用*
这里不多说，和上面重新`startActivity`的调用是完全一样的，这种动画效果是全局的

## 属性动画

关于属性动画是怎么回事前面已经说明了，在学习属性动画前，最好先搞清楚一个小问题

> X、Y，是View左上角在父容器中的坐标（在View没有平移的情况下X=left，Y=top）。 translationX，translationY则是View当前位置相对于初始化位置的偏移量，也就是说，如果你的View创建之后，没有进行过相关平移操作，translationX和translationY的值是始终为0的。

### ValueAnimator

ValueAnimator是整个属性动画机制当中最核心的一个类，属性动画的运行机制是通过不断地对值进行操作来实现的，而初始值和结束值之间的动画过渡就是由ValueAnimator这个类来负责计算的。它的内部使用一种时间循环的机制来计算值与值之间的动画过渡，我们只需要将初始值和结束值提供给ValueAnimator，并且告诉它动画所需运行的时长，那么ValueAnimator就会自动帮我们完成从初始值平滑地过渡到结束值这样的效果。除此之外，ValueAnimator还负责管理动画的播放次数、播放模式、以及对动画设置监听器等，确实是一个非常重要的类。
它的使用非常简单：

```java
//将0平滑过渡到1  时间为300毫秒
//参数可传多个值，会按顺序进行过渡，类似的还有ofInt()
ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
anim.setDuration(300);
//可以设置一个监听器
anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float currentValue = (float) animation.getAnimatedValue();
        Log.d("TAG", "cuurent value is " + currentValue);
    }
});
//开始
anim.start();
```

那么除此之外，我们还可以调用`setStartDelay()`方法来设置动画延迟播放的时间，调用`setRepeatCount()`和`setRepeatMode()`方法来设置动画**循环播放的次数**以及**循环播放的模式**
循环模式包括*RESTART*和*REVERSE*两种，分别表示*重新播放*和*倒序播放*的意思。

### ObjectAnimator

相比于ValueAnimator，**ObjectAnimator可能才是我们最常接触到的类**，因为ValueAnimator只不过是**对值进行了一个平滑的动画过渡**，但我们实际使用到这种功能的场景好像并不多。而ObjectAnimator则就不同了，它是可以直接**对任意对象的任意属性进行动画操作的**，比如说View的alpha属性。

不过虽说ObjectAnimator会更加常用一些，但是它其实是继承自ValueAnimator的，底层的动画实现机制也是基于ValueAnimator来完成的，所以ValueAnimator中可以使用的方法在ObjectAnimator中也是可以正常使用的，他们非常的相似：

```java
//关于更多参数：rotation为旋转 0f-360f之间，translationX水平移动，scaleY缩放倍数
ObjectAnimator animator = ObjectAnimator.ofFloat(textview, "alpha", 1f, 0f, 1f);
animator.setDuration(5000);
animator.start();
```

关于第二个参数可以是那些值，理论上是任何值都可以的，因为在设计的时候并不是只对于View的，其实ObjectAnimator内部的工作机制并不是直接对我们传入的属性名进行操作的，而是会去寻找这个属性名对应的get和set方法，所以只要保证传进的属性有对应的get/set方法理论上就可以使用了。
下面列一下常用的属性：

- translationX和translationY：这两个属性作为一种**增量**，来控制View对象从它的布局容器的左上角坐标偏移的位置
- rotation、rotationX、rotationY控制View对象绕它的支点进行2D和3D旋转
- scaleX、scaleY控制View对象绕它的支点进行2D缩放
- pivotX、pivotY这两个属性控制View对象支点的位置。默认情况下支点的位置是该对象的中心点
- X、Y 两个最简单实用的属性，它描述了View对象的最终位置，也就是最初左上角坐标和translation值的累计和
- alpha 表示View对象的透明度，默认值1(不透明)，0就是完全透明(也就是不可见)

此外，ObjectAnimator还有很多方法，比如比较常用的差值器，让其变化的时候不是均匀的，Google貌似一共提供了九种，足够用了，使用也非常简单，就一句代码

```java
// 比如这个一个有回弹效果的，就想小球落地弹起
animator.setInterpolator(new BounceInterpolator());
```

### 组合动画

这里就要用到AnimatorSet这个类，这个类提供了一个`play()`方法，如果我们向这个方法中传入一个Animator对象(ValueAnimator或ObjectAnimator)将会返回一个`AnimatorSet.Builder`的实例，`AnimatorSet.Builder`中包括以下四个方法：

- after(Animator anim)   将现有动画插入到传入的动画之后执行
- after(long delay)   将现有动画延迟指定毫秒后执行
- before(Animator anim)   将现有动画插入到传入的动画之前执行
- with(Animator anim)   将现有动画和传入的动画同时执行

可以看看下面的让TextView先从屏幕外移动进屏幕，然后开始旋转360度，旋转的同时进行淡入淡出操作的例子：

```java
ObjectAnimator moveIn = ObjectAnimator.ofFloat(textview, "translationX", -500f, 0f);
ObjectAnimator rotate = ObjectAnimator.ofFloat(textview, "rotation", 0f, 360f);
ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(textview, "alpha", 1f, 0f, 1f);
AnimatorSet animSet = new AnimatorSet();
animSet.play(rotate).with(fadeInOut).after(moveIn);
animSet.setDuration(5000);
animSet.start();
```

其实组合动画还可以使用另一种方式，用`PropertyValuesHolder`来实现，不过相比AnimatorSet有个缺点就是不能控制顺序

```java
PropertyValuesHolder pvh1 =
                PropertyValuesHolder.ofInt("translationX", 300f);
PropertyValuesHolder pvh1 =
                PropertyValuesHolder.ofInt("scaleX", 1f, 0, 1f);
PropertyValuesHolder pvh1 =
                PropertyValuesHolder.ofInt("scaleY", 1f, 0, 1f);

ObjectAnimator.ofPropertyValuesHolder(view, pvh1, pvh2, pvh3);
	.setDuration(1000).start();
```

### Animator监听器

在很多时候，我们希望可以监听到动画的各种事件，比如动画何时开始，何时结束，然后在开始或者结束的时候去执行一些逻辑处理。这个功能是完全可以实现的，Animator类当中提供了一个`addListener()`方法，这个方法接收一个AnimatorListener，我们只需要去实现这个AnimatorListener就可以监听动画的各种事件了。由于多数属性动画继承的关系`addListener()`这个方法算是个通用的方法。

```java
anim.addListener(new AnimatorListener() {
    @Override
    public void onAnimationStart(Animator animation) {
      //动画开始的时候执行
    }
  
    @Override
    public void onAnimationRepeat(Animator animation) {
      //动画重复的时候执行
    }
  
    @Override
    public void onAnimationEnd(Animator animation) {
      //动画结束的时候执行
    }
  
    @Override
    public void onAnimationCancel(Animator animation) {
      //动画被取消的时候执行
    }
});  
```

如果你不想写这么多的方法，可以用`AnimatorListenerAdapter`类，默认已经全部实现了，只需要复写需要的方法即可。

### 用XML编写动画

如果想要使用XML来编写动画，首先要在res目录下面新建一个animator文件夹，所有属性动画的XML文件都应该存放在这个文件夹当中。然后在XML文件中我们一共可以使用如下三种标签：

- `<animator> ` 对应代码中的ValueAnimator
- `<objectAnimator>`  对应代码中的ObjectAnimator
- `<set>`  对应代码中的AnimatorSet

比如如果用XML实现刚才我们的那个组合动画

```xml
<set xmlns:android="http://schemas.android.com/apk/res/android"  
    android:ordering="sequentially" >
  
    <objectAnimator
        android:duration="2000"
        android:propertyName="translationX"
        android:valueFrom="-500"
        android:valueTo="0"
        android:valueType="floatType" >
    </objectAnimator>
  
    <set android:ordering="together" >
        <objectAnimator
            android:duration="3000"
            android:propertyName="rotation"
            android:valueFrom="0"
            android:valueTo="360"
            android:valueType="floatType" >
        </objectAnimator>
  
        <set android:ordering="sequentially" >
            <objectAnimator
                android:duration="1500"
                android:propertyName="alpha"
                android:valueFrom="1"
                android:valueTo="0"
                android:valueType="floatType" >
            </objectAnimator>
            <objectAnimator
                android:duration="1500"
                android:propertyName="alpha"
                android:valueFrom="0"
                android:valueTo="1"
                android:valueType="floatType" >
            </objectAnimator>
        </set>
    </set>
</set>
```

然后就是读取XML文件的内容来执行动画了：

```java
Animator animator = AnimatorInflater.loadAnimator(context, R.animator.anim_file);
//设置到一个对象上
animator.setTarget(view);  
animator.start();  
```
### View的animate方法

在Android3.0后，Google给View增加了animate方法来**直接驱动属性动画**，可以认为是属性动画的一种简写形式，需要注意的是它并不能提供全部的属性动画的方法，它有一些特性：

- 整个**ViewPropertyAnimator**的功能都是建立在View类新增的`animate()`方法之上的，**这个方法会创建并返回一个ViewPropertyAnimator的实例**，之后的调用的所有方法，设置的所有属性都是通过这个实例完成的。
- 在使用ViewPropertyAnimator时，我们无需调用`start()`方法，因为新的接口中使用了隐式启动动画的功能，**只要我们将动画定义完成之后，动画就会自动启动。**并且这个机制对于组合动画也同样有效，只要我们不断地连缀新的方法，那么动画就不会立刻执行，等到所有在ViewPropertyAnimator上设置的方法都执行完毕后，动画就会自动启动。当然如果不想使用这一默认机制的话，**我们也可以显式地调用`start()`方法来启动动画。**
- ViewPropertyAnimator的所有接口都是使用连缀的语法来设计的，**每个方法的返回值都是它自身的实例**，因此调用完一个方法之后可以直接连缀调用它的另一个方法，这样把所有的功能都串接起来，我们甚至可以仅通过一行代码就完成任意复杂度的动画功能。

`mButton.animate().x(500).y(500).setDuration(5000); `

SDK16又分别添加了**withStartAction**和**withEndAction**用于在动画前，和动画后执行一些操作。当然也可以`.setListener(listener)`等操作。

```java
.withEndAction(new Runnable()
	{
	    @Override
	    public void run()
	    {
	        Log.e(TAG, "END");
	        runOnUiThread(new Runnable()
	        {
	            @Override
	            public void run()
	            {
	                mBlueBall.setY(0);
	                mBlueBall.setAlpha(1.0f);
	            }
	        });
	    }
	})
```

## 补间动画(视图动画)

这个现在其实用的已经很少了，所以就只是简单说下了
在Android中使用Tween补间动画需要得到Animation的支持，是一个抽象类，其中抽象了一些动画必须的方法，**其子类均有对其进行实现**，而在Android下完成补间动画也就是在操作Animation的几个子类。
同样，补间动画既可以使用java代码实现也可以用XML文件来定义，下面是一些对应：

- android:duration/**setDuration(long)**：动画单次播放时间。
- android:fillAfter/**setFillAfter(boolean)**：动画是否保持播放结束位置。
- android:fillBefore/**setFillBefore(boolean)**：动画是否保持播放开始位置。
- android:interpolator/**setInterpolator(Interpolator)**：指定动画播放的速度曲线，不设定默认为匀速。
- android:repeatCount/**setRepeatCount(int)**：动画持续次数，如2，会播放三次。
- android:repeatMode/**setRepeatMode(int)**：动画播放模式。
- android:startOffset/**setStartOffset(long)**：动画延迟播放的时长，单位是毫秒。

上面提到，Android下对于补间动画的支持，主要是使用Animation的几个子类来实现，例如：

- AlphaAnimation：控制动画透明度的变化。
- RotateAnimation：控制动画旋转的变化。
- ScaleAnimation：控制动画成比例缩放的变化。
- TranslateAnimation：控制动画移动的变化。
- AnimationSet：以上几种变化的组合。

### 一个例子

比如一个透明度的动画：

```java
protected void toAlpha() {
    // 动画从透明变为不透明
    AlphaAnimation anim = new AlphaAnimation(1.0f, 0.5f);
    // 动画单次播放时长为2秒
    anim.setDuration(2000);
    // 动画播放次数
    anim.setRepeatCount(2);
    // 动画播放模式为REVERSE
    anim.setRepeatMode(Animation.REVERSE);
    // 设定动画播放结束后保持播放之后的效果
    anim.setFillAfter(true);
    // 开始播放，iv_anim是一个ImageView控件
    iv_anim.startAnimation(anim);
}
```

用XML定义：

```xml
<?xml version="1.0" encoding="utf-8"?>
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="2000"
    android:fillAfter="true"
    android:fromAlpha="1.0"
    android:repeatCount="2"
    android:repeatMode="reverse"
    android:toAlpha="0.5" >
</alpha>
```

在使用XML资源文件的时候，使用`AnimationUtils.loadAnimation()`方法加载它即可

## 参考

[实现Activity跳转动画的五种方式](http://blog.csdn.net/qq_23547831/article/details/51821159)
[ Android属性动画完全解析(上)，初识属性动画的基本用法](http://blog.csdn.net/guolin_blog/article/details/43536355)
[自定义控件三部曲之动画篇（一）](http://blog.csdn.net/harvic880925/article/details/39996643)
[Android--Tween补间动画](http://www.cnblogs.com/plokmju/p/android_TweenAnimation.html)