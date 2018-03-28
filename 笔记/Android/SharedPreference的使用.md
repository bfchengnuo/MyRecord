---
title: SharedPreference的使用
date: 2016-05-22 14:08:17
tags: [Android]
categories: Android
---
SharedPreference在我们保存一些数据的时候是经常使用的一种保存方式，因为是轻量级的对性能的损坏很低，今天就写写它的基本使用方法。
更新：2016.9.4
原内容：SharedPreference.Editor的apply和commit方法
<!-- more -->
它的两种提交方式到底有什么不同呢，今天用commit提交数据AS警告，意思是说,建议用apply()方法代替。commit()方法保存数据是马上执行的,而apply()方法会在后台执行。

## SharedPreference的基本使用

首先需要说一下，SharedPreference特别适合用于保存软件配置参数。其背后是用xml文件存放数据，文件存放在/data/data/`package name`/shared_prefs目录下.
通常我们一般把写入/获取的方法写成工具类来进行调用

### 获取SharedPreferences

获取SharedPreferences的两种方式:

1. 调用Context对象的getSharedPreferences()方法
2. 调用Activity对象的getPreferences()方法  (这个方法默认使用当前类不带包名的类名作为文件的名称)

两种方式的区别:
- 调用Context对象的getSharedPreferences()方法获得的SharedPreferences对象可以被同一应用程序下的其他组件共享.
- 调用Activity对象的getPreferences()方法获得的SharedPreferences对象只能在该Activity中使用.

### 方法解释

getSharedPreferences(name,mode)方法的第一个参数用于指定该文件的名称，名称不用带后缀，后缀会由Android自动加上。方法的第二个参数指定文件的操作模式，共有四种操作模式:

> Context.MODE_PRIVATE：为默认操作模式,代表该文件是私有数据,只能被应用本身访问,在该模式下,写入的内容会覆盖原文件的内容
>
> Context.MODE_APPEND：模式会检查文件是否存在,存在就往文件追加内容,否则就创建新文件。
>
> Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
>
> MODE_WORLD_READABLE：表示当前文件可以被其他应用读取。
>
> MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。

### 存放数据

```java
//1、打开Preferences，名称为setting，如果存在则打开它，否则创建新的Preferences
SharedPreferences settings = getSharedPreferences("setting", 0);
//2、让setting处于编辑状态
SharedPreferences.Editor editor = settings.edit();
//3、存放数据
editor.putString("name","ATAAW");
//4、完成提交
editor.commit();
```

### 获取数据

```java
//1、获取Preferences
SharedPreferences settings = getSharedPreferences("setting", 0);
//2、取出数据
String name = settings.getString("name","默认值");
```

## SharedPreference.Editor的apply和commit方法

### 相同点

1. 二者都是提交preference修改数据
2. 二者都是原子过程。

什么是原子操作？？[点击这里](http://blog.csdn.net/zhaoyu_android4311/article/details/8434060)
### 区别

1. apply没有返回值而commit返回boolean表明修改是否提交成功
2. apply是将修改数据原子提交到内存，而后异步真正提交到硬件磁盘；而commit是同步的提交到硬件磁盘，因此，在多个并发的提交commit的时候，他们会等待正在处理的commit保存到磁盘后在操作，从而降低了效率。而apply只是原子的提交到内容，后面有调用apply的函数的将会直接覆盖前面的内存数据，这样从一定程度上提高了很多效率。
3. apply方法不会提示任何失败的提示。

   > commit方法有boolean返回值,表示保存是否成功的.
   > apply方法是void的.
   > commit方法是同步执行保存.
   > apply方法是异步执行保存

### 建议
综合上述，由于在一个进程中，sharedPreference是单实例，一般不会出现并发冲突，如果对提交的结果不关心的话，建议使用apply，当然需要确保提交成功且有后续操作的话，还是需要用commit的。

如果不关注返回值或在程序的main线程使用时，推荐使用apply().

---

最好是视实际情况而定,如不关心是否保存成功,就可以用异步的apply方法,相反,在乎保存返回值的,则用commit方法.如果出现并发情况,那么肯定是用异步的apply方法,这是如果用了commit方法的话，就有可能会导致阻塞. apply方法是现将数据立马存到内存中,然后会异步的去保存到目录文件去.

apply的效率高一些，如果没有必要确认是否提交成功建议使用apply。???

## 参考

[SharedPreferences数据的两种保存方法： apply、commit  ](http://tanqi0508.blog.163.com/blog/static/1883557772012111104326404/)
[SharedPreference.Editor中commit和apply区别](http://9leg.com/android/2015/07/12/whats-the-difference-between-commit-and-apply-in-shared-preference.html)