---
title: FragmentTransaction addToBackStack 无效问题
date: 2016-4-23
tags: [Android]
categories: Android
---

**如果当前的类继承的ActionBarActivity，则FragmentManager必须来自v4包**，这样addToBackStack (null)是有效的，按返回键的时候可以返回上一个碎片。
<!-- more -->

``` java
import android.support.v4.app.FragmentManager;  
import android.support.v4.app.FragmentTransaction;  
import android.support.v7.app.ActionBarActivity;  

FragmentManager fm = getSupportFragmentManager();  
FragmentTransaction ft = fm.beginTransaction();  
ft.replace(R.id.right_layout,RightFragment2.newInstance("111","111"));  
ft.addToBackStack(null);  
ft.commit();
```
如果当前的类继承的ActionBarActivity，而FragmentManager来自 android.app.FragmentManager，这样addToBackStack (null)无效，按返回键会一次退出。

如果FragmentManager来自 Android.app.FragmentManager，把继承类改为Activity，这样addToBackStack (null)也是有效的，按返回键的时候会返回上一个碎片。

事务的addToBackStack方法可以传入一个名字用于描述返回栈的状态，一般传入null即可

[原文](http://blog.csdn.net/OnlySnail/article/details/45726235)