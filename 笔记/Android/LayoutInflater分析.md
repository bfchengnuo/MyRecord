---
title: LayoutInflater分析
date: 2016-08-03 20:13:40
tags: [Android]
categories: Android
---

最近打算学习下UI绘制，脱离入门进入新手，看了郭神的博客提炼了下，关于源码的部分还是看不懂，只好先去掉了，郭神写的很赞的~
<!-- more -->
说起LayoutInflater它主要是用于加载布局的，刚接触Android的(比如我)对LayoutInflater不怎么熟悉，因为加载布局的任务通常都是在Activity中调用setContentView()方法来完成的。其实setContentView()方法的内部也是使用LayoutInflater来加载布局的，只不过这部分源码是internal的，不太容易查看到，下面就来详细了解下。

## 基本用法

首先我们要先获取实例啦
获取实例非常简单，有两种方法：

``` java
LayoutInflater layoutInflater = LayoutInflater.from(context); 
```

 还有一种具有同样效果

```java
LayoutInflater layoutInflater = (LayoutInflater) context  
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
```

其实第一种就是第二种的简单写法，只是Android给我们做了一下封装而已。得到了LayoutInflater的实例之后就可以调用它的inflate()方法来加载布局了，如下所示：

```java
layoutInflater.inflate(resourceId, root); 
```

inflate()方法一般接收两个参数，第一个参数就是要加载的布局id，第二个参数是指给该布局的外部再嵌套一层父布局，如果不需要就直接传null。这样就成功成功创建了一个布局的实例，之后再将它添加到指定的位置就可以显示出来了。

``` java
public class MainActivity extends Activity {  
  private LinearLayout mainLayout;  

  @Override  
  protected void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
    setContentView(R.layout.activity_main);
    //获取一个ViewGroup作为容器
    mainLayout = (LinearLayout) findViewById(R.id.main_layout);
    //获取实例
    LayoutInflater layoutInflater = LayoutInflater.from(this);
    //加载控件---这个XML布局文件只有一个button标签而已
    View buttonLayout = layoutInflater.inflate(R.layout.button_layout, null);
    //调用ViewGroup的addView方法把控件添加进去
    mainLayout.addView(buttonLayout);  
  }
} 
```

LayoutInflater技术广泛应用于需要动态添加View的时候，比如在ScrollView和ListView中，经常都可以看到LayoutInflater的身影。

## 进一步深入

说是深入，因为是刚入门，源码果然还是看不懂的.....这里也就不贴了，可以去郭神的博客去看，见参考。

通过inflate的源码能知道LayoutInflater其实就是使用Android提供的pull解析方式来解析布局文件的，它是用于根据节点名用反射的方式来创建View对象的，最终会把最顶层的根布局返回。
另外，inflate方法还有一个接收三个参数的重载:

```java
inflate(int resource, ViewGroup root, boolean attachToRoot)
```

具有下面四个特点：

1. 如果root为null，attachToRoot将失去作用，设置任何值都没有意义。
2. 如果root不为null，attachToRoot设为true，则会给加载的布局文件的指定一个父布局，即root。
3. 如果root不为null，attachToRoot设为false，则会将布局文件最外层的所有layout属性进行设置，当该view被添加到父view当中时，这些layout属性会自动生效。
4. 在不设置attachToRoot参数的情况下，如果root不为null，attachToRoot参数默认为true。

----

其实上面还有一个遗留问题，我们在定义测试view的时候只写了一个button标签，这里如果设置宽高的话其实会失效，也就是这也样子：

```html
<Button xmlns:android="http://schemas.android.com/apk/res/android"  
    android:layout_width="300dp"
    android:layout_height="80dp"
    android:text="Button" >
</Button> 
```

layout_width和layout_height的值修改成多少，都不会有任何效果的，因为这两个值现在已经完全失去了作用。平时我们经常使用layout_width和layout_height来设置View的大小，并且一直都能正常工作，就好像这两个属性确实是用于设置View的大小的。而实际上则不然，它们其实是用于**设置View在布局中的大小的**，也就是说，首先View必须存在于一个布局中，之后如果将layout_width设置成match_parent表示让View的宽度填充满布局，如果设置成wrap_content表示让View的宽度刚好可以包含其内容，如果设置成具体的数值则View的宽度会变成相应的数值。这也是为什么这两个属性叫作layout_width和layout_height，而不是width和height。
再来看一下我们的button_layout.xml吧，很明显Button这个控件目前不存在于任何布局当中，所以layout_width和layout_height这两个属性理所当然没有任何作用。解决方法其实有很多种，最简单的方式就是在Button的外面再嵌套一层布局。但是同理外层的ViewGroup布局的layout_width和layout_height则会失去作用。
但是平时我们新建的activity指定的布局是可以使用这两个属性的，确实，这主要是因为，**在setContentView()方法中，Android会自动在布局文件的最外层再嵌套一个FrameLayout**，所以layout_width和layout_height属性才会有效果。可以用下面的代码证实下：

```java
public class MainActivity extends Activity {  

  private LinearLayout mainLayout;  

  @Override  
  protected void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mainLayout = (LinearLayout) findViewById(R.id.main_layout);
    //获取父布局
    ViewParent viewParent = mainLayout.getParent();
    Log.d("TAG", "the parent of mainLayout is " + viewParent);  
  }
} 
```

实际上Android界面显示的原理要比我们所看到的东西复杂得多(在UI绘制可以了解到)。任何一个Activity中显示的界面其实主要都由两部分组成，标题栏和内容布局。标题栏就是在很多界面顶部显示的那部分内容，可以在代码中控制让它是否显示。而内容布局就是一个FrameLayout，这个布局的id叫作content，我们调用setContentView()方法时所传入的布局其实就是放到这个FrameLayout中的，这也是为什么这个方法名叫作setContentView()，而不是叫setView()。

## 更新(2016-9-4)

今天突然看到了另一种用法：

```java
view = View.inflate(getApplicationContext(), R.layout.list_item_callsms, null);
```

这两者到底有什么关系呢，去查了下官方的API，大体和LayoutInflater的差不多，不过，最后一句挺有意思的：
> **See also:**
>  `LayoutInflater`

这就很让人在意了，内部是不是用LayoutInflater实现的呢？

```java
public static View inflate(Context context, int resource, ViewGroup root) {
  LayoutInflater factory = LayoutInflater.from(context);
  return factory.inflate(resource, root);
}
```

看样子果然是呢，封装了一下，虽然不知道为什么，但是这也让我们多了一个选择，不过还是LayoutInflater的方法比较常见呢~~
话说好像郭神在文章中也提到过，不过结合的源码，以目前的功力理解还是有点困的的，嘛，先这样吧！

> 不管你是使用的哪个inflate()方法的重载，最终都会辗转调用到LayoutInflater的方法里

## 参考

[Android LayoutInflater原理分析，带你一步步深入了解View(一)](http://blog.csdn.net/guolin_blog/article/details/12921889)