---
title: 'Intent,IntentFilter和PendingIntent'
date: 2016-05-10 17:14:09
tags: [Android,Intent]
categories: Android
---
**前言**
今天看到媒体那块直接懵了，关于 IntentFilter 和 PendingIntent 这几个词有印象，就是TMD想不起来是干啥的....intent还好一点经常能用到，就整理下以备日后翻阅，当然是不全面不深层次的解析，以后有时间再深入学习，先挖一个坑。。。
<!-- more -->

## Intent

简单说，intent 一般被用于启动活动，启动服务，以及发送广播等场景

Intent 在这里起着一个媒体中介的作用，专门提供组件互相调用的相关信息，实现调用者与被调用者之间的解耦

intent 的用法大致分为两种，一种是显示启动，另一种隐式启动。并且它还有多个构造函数的重载。

>显式Intent：通过组件名指定启动的目标组件,比如 startActivity(new Intent(A.this,B.class));  每次启动的组件只有一个~

>隐式显式Intent:不指定组件名,而指定 Intent 的 Action , Data ,或 Category , 当我们启动组件时, 会去匹配AndroidManifest.xml 相关组件的 Intent-filter, 逐一匹配出满足属性的组件,当不止一个满足时, 会弹出一个让我们选择启动哪个的对话框~

``` java
//显式启动
Intent it = new Intent();
it.setClass(this,Second.class);
//以上两句可简写 Intent it = new Intent(this,Second.class);
startActivity(it);
//返回home桌面代码
Intent it = new Intent();
it.setAction(Intent.ACTION_MAIN); //设置动作
it.addCategory(Intent.CATEGORY_HOME);  //设置类别
startActivity(it);
//一个intent只能有一个action，但是可以有多个category
```
隐式启动中如果定义了action和category，必须要同时匹配才能够响应intent
``` html
<!--隐式启动-->
<activity android:name=".SecondActivity"
            android:label="第二个Activity">
    <intent-filter>
        <action android:name="com.bfchengnuo.test.ACTION_START"/>
        <category android:name="这里是附加内容"/>
    </intent-filter>           
 </activity>
```
其他启动
``` java
startActivity(Intent)/startActivityForResult(Intent);//来启动一个Activity
startService(Intent)/bindService(Intent);//来启动一个Service
sendBroadcast(); //发送广播到指定BroadcastReceiver
//另外别忘了我们在注册四大组件时，写得很多的Intent-Filter哦~
```
## IntentFilter

简单理解就是一个过滤器，用于描述 intent 的各种属性， 比如 action, category 等，一般在动态注册广播接收器的时候使用，我都已经忘记了在 AndroidManifest.xml 文件中的内容了。。。
``` html
<receiver android:name=".ForceofflineReceiver">
    <intent-filter>
        <action android:name="com.bfchengnuo.broadcast.FORCE_TEST"/>
    </intent-filter>
</receiver>
```
对，这就是一个静态注册广播接收者的代码。
如果用动态生成的方式写就是：

``` java
//定义过滤器
IntentFilter intentFilter = new IntentFilter();
//添加规则 就是<action />标签的内容
intentFilter.addAction("com.bfchengnuo.broadcast.FORCE_TEST");
//以上两句可以合并为IntentFilter intentFilter1 = new IntentFilter("com.bfchengnuo.broadcast.FORCE_TEST");
BC test = new BC();
//进行注册
registerReceiver(test,intentFilter);
//---------------------------------------------------------------
 class BC extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        //。。。。
        //注意不要做耗时操作，因为广播接收器的生命周期很短 5-10S
    }
}
//最后要记得在程序退出的时候卸载广播
 @Override
protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(BC)；
}
```

PS：动态注册的优先级高于静态注册的，但是同时如果程序没有运行，动态注册的是没有效果的。

## PendingIntent

intent 英文意思是意图，pending 表示即将发生或来临的事情。

PendingIntent 这个类用于处理即将发生的事情，比如在通知 Notification 中用于跳转页面，但不是马上跳转。

pendingIntent 执行的操作实质上是参数传进来的 Intent 的操作，但是使用 pendingIntent 的目的在于它所包含的 Intent 的操作的执行是需要满足某些条件的。

**Intent 是及时启动，intent 随所在的 activity 消失而消失。 **

>PendingIntent 可以看作是对 intent 的包装，通常通过 getActivity，getBroadcast，getService 来得到 pendingintent的实例，当前 activity 并不能马上启动它所包含的 intent，而是在外部执行 pendingintent 时，调用 intent 的。
>正由于 pendingintent 中保存有当前 App 的 Context，使它赋予外部 App 一种能力，使得外部 App 可以如同当前 App一样的执行 pendingintent 里的 Intent， 就算在执行时当前 App 已经不存在了，也能通过存在 pendingintent 里的 Context 照样执行 Intent。
>另外还可以处理 intent 执行后的操作。PendingIntent 常和 alermanger 和 notificationmanager 一起使用。 

Intent一般是用作Activity、Service、BroadcastReceiver之间传递数据；而Pendingintent一般用在 Notification上，可以理解为延迟执行的 intent，PendingIntent 是对 Intent 一个包装。

关于获取 PendingIntent 对象有几个方法：

```java
public static PendingIntent getActivity(Context context, int requestCode, Intent intent, int flags)
public static PendingIntent getBroadcast(Context context, int requestCode, Intent intent, int flags)
public static PendingIntent getService(Context context, int requestCode, Intent intent, int flags)
public static PendingIntent getActivities(Context context, int requestCode, Intent[] intents, int flags)
public static PendingIntent getActivities(Context context, int requestCode, Intent[] intents, int flags, Bundle options)
```

关于参数的中的 flags 的类型有几种可选：

FLAG_ONE_SHOT：得到的 pi 只能使用一次，第二次使用该 pi 时报错 
FLAG_NO_CREATE： 当 pi 不存在时，不创建，返回 null 
FLAG_CANCEL_CURRENT： 每次都创建一个新的 pi 
FLAG_UPDATE_CURRENT： 不存在时就创建，创建好了以后就一直用它，每次使用时都会更新 pi 的数据(使用较多)

API 文档里虽然未使用 requestCode 参数，但实际上是通过该参数来区别不同的 Intent 的，一个的话，一般我们习惯传 0

下面写一个简单的例子：

```java
//intent 和一般的定义没有区别
PendingIntent pi = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
pi.send(); //相当于执行

//通知中
Notification.Builder builder = new Notification.Builder(context);
Notification notification = builder.setSmallIcon(R.drawable.icon)
	.setContentTitle(sPushData[1])
	.setDefaults(Notification.DEFAULT_ALL)
	.setContentIntent(pi)
	.setContentText(sPushData[2])
	.build();
notificationManager.notify(1, notification);
```

## 参考
[Intent的基本使用](http://www.runoob.com/w3cnote/android-tutorial-intent-base.html)
[Android中pendingIntent的深入理解](http://blog.csdn.net/yuzhiboyi/article/details/8484771)
[Intent 和 PendingIntent 区别](http://www.bozhiyue.com/anroid/boke/2016/0409/16491.html)

关于Intent 和 Intent 过滤器官方已经有了中文版
[AndroidDevelopers](http://developer.android.com/intl/zh-cn/guide/components/intents-filters.html)