---
title: Android定时器的几种实现
date: 2017-03-07 11:38:02
tags: [Android,定时器]
categories: Android
---

Android中实现定时器一般常用的有三种方式，主要是由多线程和handler实现
定时这种需求其实也挺普遍的，三种方式的性能问题没测试，不知道如何....
感觉还可以用广播来做，貌似是使用AlarmManager和handler实现，本文没写，因为我也没测试，这种方式貌似是用于精准定时的，用到再来补充吧
<!-- more -->
是因为广播啊、服务啊还不算很熟，用的比较少

## Handle与线程的sleep

从名字就能看出是怎么回事来了，就是开一个线程，让其执行一个死循环或者设置一个flag然后让他每次循环睡眠指定时间，每循环一次发送一条消息，然后Handler就会每隔一定时间收到这个消息，由此进行一些操作
我感觉这个可能不会太稳定......
关键代码为：

```java
class MyThread implements Runnable {
    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(5000);
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

当然上面的代码是个内部类，所在的Activity以及调用代码是：

```java
private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                Toast.makeText(TimerActivity.this, "收到方式一的信息", Toast.LENGTH_SHORT).show();
                break;
        }
    }
};

// 调用
new Thread(new MyThread()).start();
```

## Handler的postDelayed方法

从方法名也能看出，就是延迟一定时间发送的意思，但是它可以形成自循环啊！所以就可以搞一下了啊
并且感觉这样方式应该很不错

```java
private Runnable runnable = new Runnable() {
    @Override
    public void run() {
        Toast.makeText(TimerActivity.this, "方式二发送的消息", Toast.LENGTH_SHORT).show();
        // 隔3秒再发送一次”本体“
        mHandler.postDelayed(this,3000);
    }
};

// 这里还是用方法一的handler吧，无所谓啦
// 首次调用，传入runnable对象
mHandler.postDelayed(runnable,3000);

// 如果想取消，只需要
mHandler.removeCallbacks(runnable);
```

handler会执行runnable中的run方法，但是是在UI线程，不会重新开一个线程执行的，所以是可以操作UI的
~~好眼熟，貌似以前写过？~~

## Handler/timer/TimerTask结合法

但从名字来看，我感觉这个比较靠谱，使用起来也很简单

```java
private Timer mTimer;
private TimerTask mTimerTask = new TimerTask() {
    @Override
    public void run() {
        Message msg = new Message();
        msg.what = 2;
        msg.obj = System.currentTimeMillis();
        // 还是用方法一的handler吧，区分了下what，别在意
        mHandler.sendMessage(msg);
    }
};

// 执行代码
mTimer = new Timer();
// 延迟一秒后执行，每隔两秒执行一次
mTimer.schedule(mTimerTask,1000,2000);

// 取消可以使用
mTimer.cancel();
mTimerTask.cancel();
```

TimerTask的run方法跑的代码确实是开了个线程跑的，所以如果想要操作UI还是用个handler吧
handler真是好用啊！设计的非常巧妙，曾经看过源码解析，很可惜现在已经忘的差不多了，这里也不多嘴了，反正就是很强的一个东西....