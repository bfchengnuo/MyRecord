---
title: Android中的Application类
date: 2016-06-09 20:00:02
tags: [Android]
categories: Android
---
## 介绍

Application和Activity,Service一样是Android框架的一个系统组件，当Android程序启动时系统会创建一个Application对象，用来存储系统的一些信息。
程序运行时Android系统自动创建一个Application类的对象且只创建一个，所以Application可以说是单例（singleton）模式的一个类。
**通常我们是不需要指定一个Application的，系统会自动帮我们创建，如果需要创建自己的Application，那也很简单！创建一个类继承Application并在AndroidManifest.xml文件中的application标签中进行注册（只需要给application标签增加name属性，并添加自己的 Application的名字即可）。**
<!-- more -->
启动Application时，系统会创建一个PID，即进程ID，所有的Activity都会在此进程上运行。那么我们在Application创建的时候初始化全局变量，**同一个应用的所有Activity都可以取到这些全局变量的值**，换句话说，我们在某一个Activity中改变了这些全局变量的值，那么在同一个应用的其他Activity中值就会改变。

Application对象的生命周期是整个程序中最长的，它的生命周期就等于这个程序的生命周期。因为它是全局的单例的，所以在不同的Activity,Service中获得的对象都是同一个对象。所以可以通过Application来进行一些，如：数据传递、数据共享和数据缓存等操作。

**其实Android官方并不太推荐我们使用自定义的Application，基本上只有需要做一些全局初始化的时候可能才需要用到自定义Application，多数项目只是把自定义Application当成了一个通用工具类，而这个功能并不需要借助Application来实现，使用单例可能是一种更加标准的方式。**

## 应用场景
在Android中，可以通过继承Application类来实现应用程序级的全局变量，这种全局变量方法相对静态类更有保障，直到应用的所有Activity全部被destory掉之后才会被释放掉。

## 代码实现
### 定义一个类继承Application
``` java
public class MyApp extends Application
{
    private static final String VALUE = "Harvey";
    
    private String value;
    
    @Override
    public void onCreate()
    {
        super.onCreate();
        setValue(VALUE); // 初始化全局变量
    }
    
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
}
```
**继承Application类，主要重写里面的onCreate（）方法（android.app.Application包的onCreate（）才是真正的Android程序的入口点），就是创建的时候，初始化变量的值。然后在整个应用中的各个文件中就可以对该变量进行操作了。**

### 配置自定义Application
在ApplicationManifest.xml文件中配置：
``` html
<application android:icon="@drawable/icon"
 	    android:label="@string/app_name"
 	    android:name=".MyApp">
```

### 获取数据
MainActivity：
``` java
public class MainActivity extends Activity {
    
    private MyApp mApp;    
    
    @Override    
    public void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.main);    
    
        mApp = (MyApp) getApplication();  // 获得自定义的应用程序   
        Log.i("mLog", "InitLabel:" + mApp.getLabel());  // 将我们放到进程中的全局变量拿出来，看是不是我们曾经设置的值    
            
        mApp.setLabel("Set Label");  //修改一下    
        Log.i("mLog", "ChangeLabel:" + mApp.getLabel());  // 看下，这个值改变了没有    
    
        Intent intent = new Intent();  // 再看一下在另一个 Activity中 是取到初始化的值，还是取到修改后的值    
        intent.setClass(this, otherActivity.class);    
        startActivity(intent);    
    }    
    
}
```
otherActivity:
``` java
public class OtherActivity extends Activity{
    
    private MyApp mApp;    
    
    @Override    
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.main);    
    
        mApp = (MyApp) getApplication();  // 获得自定义的应用程序 QApp    
        Log.i("mLog", "OhterActivity receive the Label:" + mApp.getLabel());  // 查看变量值是否修改了    
    }
}
```
getApplication()方法的语义性非常强，一看就知道是用来获取Application实例的，但是这个方法只有在Activity和Service中才能调用的到。那么也许在绝大多数情况下我们都是在Activity或者Service中使用Application的，但是如果在一些其它的场景，比如BroadcastReceiver中也想获得Application的实例，**这时就可以借助getApplicationContext()方法**，他们两个获取到的内存地址是完全相同的，因为Application本身就是一个Context。
## 其他补充
### 生命周期
**onCreate 在创建应用程序时创建**

onTerminate 当终止应用程序对象时调用，不保证一定被调用，当程序是被内核终止以便为其他应用程序释放资源，那么将不会提醒，并且不调用应用程序的对象的onTerminate方法而直接终止进程

onLowMemory 当后台程序已经终止资源还匮乏时会调用这个方法。好的应用程序一般会在这个方法里面释放一些不必要的资源来应付当后台程序已经终止，前台应用程序内存还不够时的情况。

onConfigurationChanged 配置改变时触发这个方法

**application的启动顺序**
构造函数--->attachBaseContext()--->onCreate()

要注意的是attachBaseContext()方法执行完毕后才会得到一个context对象，才能调用一些列的context方法，所以，Application中在onCreate()方法里去初始化各种全局的变量数据是一种比较推荐的做法。
### PitFalls(易犯的错误)
使用Application如果保存了一些不该保存的对象很容易导致内存泄漏。如果在Application的oncreate中执行比较 耗时的操作，将直接影响的程序的启动时间。不些清理工作不能依靠onTerminate完成，因为android会尽量让你的程序一直运行，所以很有可能 onTerminate不会被调用。
### MemoryLeak(内存泄漏)
在Java中内存泄漏是只，某个(某些)对象已经不在被使用应该被gc所回收，但有一个对象持有这个对象的引用而阻止这个对象被回收。比如我 们通常会这样创建一个View TextView tv = new TextView(this);这里的this通常都是Activity。所以这个TextView就持有着这个Activity的引用。
通常情况下，当用户转动手机的时候，android会重新调用OnCreate()方法生成一个新的Activity，原来的 Activity应该被GC所回收。但如果有个对象比如一个View的作用域超过了这个Activity(比如有一个static对象或者我们把这个 View的引用放到了Application当中)，这时候原来的Activity将不能被GC所回收，Activity本身又持有很多对象的引用，所以 整个Activity的内存被泄漏了。

Runnable对象：
比如在一个Activity中启用了一个新线程去执行一个任务，在这期间这个Activity被系统回收了， 但Runnalbe的   任务还没有执行完毕并持有Activity的引用而泄漏，但这种泄漏一般来泄漏一段时间，只有Runnalbe的线程执行完闭，这个 Activity又可以被正常回收了。

## 参考
[郭大神的博客](http://blog.csdn.net/guolin_blog/article/details/47028975)
[Android中Application类用法](http://www.cnblogs.com/renqingping/archive/2012/10/24/Application.html)
[在Android中使用Application保存全局变量](http://blog.csdn.net/xiaoyuan511/article/details/17001225)
[Android Application对象必须掌握的七点](http://blog.csdn.net/lilu_leo/article/details/8649941)