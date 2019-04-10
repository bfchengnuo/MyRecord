---
title: Intent传值相关
date: 2016-12-25 14:41:35
tags:  [Android,Intent]
categories: Android
---

之前一直认为这块是很简单的就没做笔记，但是这短时间没怎么搞过Android现在突然发现忘得差不多了.....脑子果然还是不如....记下来比较好，方便到时候快速捡起来
<!-- more -->
这篇专注于关于intent的传值，其他的暂时先忽略

## Activity到Activity

这个是我们使用频率最高的了，一般我会这么干：

```java
Intent intent =new Intent(MainActivity.this,otherActivity.class);
//传字符串之和基本数据类型
intent.putExtra("data", "当前是页面2，信息来自页面1");
//传递对象时 对象要实现Serializable接口，进行序列化
intent.putExtra("book",book);
//使用bundle打包数据
Bundle bundle = new Bundle();//该类用作携带数据
bundle.putString("name","Alice");
//为意图追加额外的数据，意图原来已经具有的数据不会丢失，但key同名的数据会被替换
intent.putExtras(bundle);

startActivity(intent);//启动Activity
```

然后可以在启动的Activity中接收数据：

```java
//通过Activity.getIntent()获取当前页面接收到的Intent。
Intent intent =getIntent();
//getXxxExtra方法获取Intent传递过来的数据
String msg=intent.getStringExtra("data");
//接收对象  反序列化
Book book = intent.getSerializableExtra("book");
//获取bundle数据包
Bundle bundle =intent.getExtras();
```

关于传对象方面还可以实现Parcelable接口，这个稍微有点复杂，不过效率是很高的！更多传对象方法可以看这里：
[Android中传递对象的三种方法](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0104/2256.html)

>   Bundle类用作携带数据，它类似于Map，用于存放key-value名值对形式的值。相对于Map，它提供了各种常用类型的`putXxx()`/`getXxx()`方法，如:`putString()`/`getString()`和`putInt()`/`getInt()`，Bundle的内部实际上是使用了`HashMap<String, Object>`类型的变量来存放`putXxx()`方法放入的值。简单地说，Bundle就是一个封装好的包，专门用于导入Intent传值的包。

## 从Activity返回值

这个我使用的频率也是蛮高的，基本使用方法就是下面几个步骤：

1.  传递数据需要使用**`Activity.startActivityForResult()`**方法启动Activity，**需要传递请求码**，而不是`Activity.startActivity()`。
2.  返回数据的时候，调用**`Activity.setResult()`**方法设置返回Intent以及**返回码**。
3.  需要重写源Activity的**`onActivityResult()`**方法以便于接受返回的Intent，在`onActivityResult()`中会**判断请求码和响应码以确定是那个Activity回传的已经是否成功**。

```java
Intent intent=new Intent(MainActivity.this, otherActivity.class);
intent.putExtra("one", ione);
intent.putExtra("two", itwo);

//启动需要监听返回值的Activity，并设置请求码：requestCode
startActivityForResult(intent, 1);

....

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //当otherActivity中返回数据的时候，会响应此方法
    //requestCode和resultCode必须与请求startActivityForResult()和返回setResult()的时候传入的值一致。
    if(requestCode==1&&resultCode==RESULT_OK)
    {
        int three=data.getIntExtra("three", 0);
        result.setText(String.valueOf(three));
    }
}
```

重要的一点，还需要在启动的Activity中调用setResult方法：

```java
//新声明一个Intent用于存放放回的数据
Intent i=new Intent();
int result=Integer.parseInt(etResult.getText().toString());
i.putExtra("three", result);
setResult(RESULT_OK, i);//设置resultCode，onActivityResult()中能获取到
finish();//使用完成后结束当前Activity的生命周期
```

## Activity与Fragment通信

因为Activity与Fragment密切相关，他们之间通信完全没必要使用intent(和标题貌似不太搭)，他们之间的通信更加的简单点**Activity获取Fragment**：为了方便碎片和活动之间进行通信，FragmentManager提供了一个类似于`findViewById()`的方法：`findFragmentById()`，专门用于从布局文件中获取碎片的实例，然后就可以调用Fragment中的方法了
**Fragment获取Activity**：在每个碎片中都可以通过调用`getActivity()`方法来得到和当前碎片相关联的活动实例:`MainActivity activity = (MainActivity) getActivity(); `另外当碎片中需要使用Context对象时，除了使用`getContext()`外，也可以使用 `getActivity()`方法，因为获取到的活动本身就是一个 Context对象了。
利用上面的两种方法也是可以实现碎片与碎片之间的通讯的，比如首先在一个碎片中可以得到与它相关联的活动，然后再通过这个活动去获取另外一个碎片的实例
最后注意下这样的用法：**在Fragment中**`getActivity.getIntent()`

## 利用接口传值--回调

话说接口回调这块我也没怎么看懂，只是我认为是这样，这样确实可以做到，说不好说，通过一个例子就能很明显的看出来了：

```java
//定义一个接口
interface K{
   void poi();
}

class A implements K{
    //在A中调用B中的方法，想要在B方法执行完毕后再回来执行A的某个方法
    private B testB = new B();
    //参数是个接口，我们把A本身传进去
    testB.show(this);

    //实现接口的方法，也就是回调的方法
    public void poi(){
        System.out.println("A class")；
    }
}

class B{
    public void show(interface callA){
        //要执行的逻辑...
        //完成后回调A的poi方法
        callA.poi();
    }
}
```

这个例子可能不太恰当，一般在构造函数传入接口，在多线程这样的无法预估函数什么时候执行完的情况下使用回调比较好，大概.....

## 参考

[Activity中使用Intent传值](http://www.cnblogs.com/plokmju/p/3140607.html)