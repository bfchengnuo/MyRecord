---
title: fragment关于导包的错误
date: 2016-4-23
tags: [Android,fragment]
categories: Android
---

## XXXfragment that is not a fragment错误，fragment认不出来

要注意的是fragment其实是有两个版本的，一个是

- import android.support.v4.app.Fragment; 
  <!-- more -->
  另外一个是

- import android.app.Fragment; 

**这两个版本的fragment是不会兼容的。也就是说要不就全用fragment，要不就全用v4 fragment，不能混搭着用。**

强烈建议初学者用第二个，也就是简单的fragment， 接下来我说说两者的区别大家就知道为什么了。

**1.最低支持版本不同**

android.app.Fragment 兼容的最低版本是android:minSdkVersion="11" 即3.0版

android.support.v4.app.Fragment 兼容的最低版本是android:minSdkVersion="4" 即1.6版

**2.需要导jar包**

fragment android.support.v4.app.Fragment 需要引入包android-support-v4.jar 

**3.在Activity中取的方法不同**

android.app.Fragment使用
`(ListFragment)getFragmentManager().findFragmentById(R.id.userList)` 获得  ， 继承Activity（这个仅仅需要继承自最简单的activity）

android.support.v4.app.Fragment使用 
`(ListFragment)getSupportFragmentManager().findFragmentById(R.id.userList)` 获得 ， 需要继承android.support.v4.app.FragmentActivity

**4.我感觉最重要的，是XML标签的使用 **

android.app.Fragment可以使用`<fragment>`标签的，这点很重要，如果是用`android.support.v4.app.Fragment`的话，是不能是用`<fragment>`标签的，会抛出`android.view.InflateException: Binary XML file line #7: Error inflating class fragment`异常。 

因为这个标签的使用还是比较简单的，所以还是比较倾向前者

[原文](http://www.tuicool.com/articles/2mmIjq)

## android app报错Binary XML file line #6: Error inflating class fragment

如果是v4的fragment，那么 acitivity 也要继承 FragmentActivity 才行，只是为了兼容 3.0 以前的版本，本质和继承 Activity 没有什么区别
不过继承 AppCompatActivity 也是可以的，因为它就是 FragmentActivity 的子类，这样就省事了