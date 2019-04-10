---
title: View与ViewGroup，Window之间的关系
date: 2016-06-17 08:23:02
tags: [Android,ViewGroup,UI布局]
categories: Android
---
## View与ViewGroup
Android系统中的所有UI类都是建立在View和ViewGroup这两个类的基础上的。所有的view都是widget（控件），所有的viewgroup都是layout（布局），viewgroup可以包涵多个view。View和ViewGroup之间采用了组合设计模式，可以使得“部分-整体”同等对待。ViewGroup作为布局容器类的最上层，布局容器里面又可以有View和ViewGroup。
<!-- more -->
他们之间的继承关系：
![](http://o6lgtfj7v.bkt.clouddn.com/%E7%BB%A7%E6%89%BF%E5%9B%BE.png)

### 运用
ViewGroup的绘图流程：
ViewGroup绘制包括两个步骤：1.measure 2.layout
在两个步骤中分别调用回调函数：1.onMeasure()   2.onLayout()

绘制UI方面还没学，这里先挖个坑。

更新：UI的绘制流程可以初步先看看[Android控件架构](http://bfchengnuo.com/2016/08/11/%E7%AC%94%E8%AE%B0-Android%E6%8E%A7%E4%BB%B6%E6%9E%B6%E6%9E%84/)

## Android窗口系统
Android的窗口系统是Client/Server模式的，我在这里只讲窗口系统的客户端：
![](http://o6lgtfj7v.bkt.clouddn.com/%E7%AA%97%E5%8F%A3%E7%B3%BB%E7%BB%9F%E5%AE%A2%E6%88%B7%E7%AB%AF.JPG)
Android中的Window是表示Top Level等顶级窗口的概念。DecorView是Window的Top-Level View，这个View可以称之为主View，DecorView会缺省的attach到Activity的主窗口中。

ViewRoot建立了主View(DecorView)与窗口系统Server端的通讯桥梁, ViewRoot是 Handler的子类，即它其实是个Handler，它接受窗口系统服务器端的消息并将消息投递到窗口系统的客户端，然后消息就从客户端的主View往其下面的子View传递，直到消息被完全处理掉为止。

DecorView实际上是一个ViewGroup。在依存关系上来讲，对单个主窗口来讲，DecorView是Top-Level View。
### Activity Window View
Activity相当于控制器，负责调用业务类的方法。简单的业务可以直接在Activity中处理。Activity通过内置是Window对象的setContentView(资源位置.资源类型.资源)方法来展示界面。用户通过View操作界面。与用户交互时，通过View来捕获事件，再通过WindowManagerService传递消息(当前操作的控件，事件的类型)。Android框架再回调相应的Activity方法，实现与用户的交互。

Window：是Android中的窗口，表示顶级窗口，也就是主窗口，每一个主窗口，都有一个view，称之为DecorView（装饰视图），它是主窗口的顶级View（DecorView必须是一个布局容器，因为它要容纳其他的View）。当Activity调用setContentView()时，实际上就是调用Window对象的setContentView()方法，执行该方法，把用户定义的View添加到DecorView中，最终完成对View的展示。

View：视图，是用户接口组件的基本构建块，它在屏幕中占用一个矩形区域，它是所 有UI控件的基类，如一个按钮或文本框。View负责图形界面渲染及事件处理
### 常用属性方法
**ViewGroup.LayoutParams 布局参数类属性：**

android:layout_width 相对于父控件的宽度 (wrap_content, match_parent,fill_parent)

android:layout_height 相对于父控件高度 (wrap_content,match_parent,fill_parent)

**ViewGroup常用的方法**

addView(): 向视图组添加View

removeView():从视图组移去View

getChildCount：获得视图组子控件的数量

getChildAt() : 获得具体某个子控件
## view的绘制
view的绘制流程大致是首先绘制父视图，然后递归的遍历view树。细节上解析xml，使用反射机制新建view实例。

绘制按照视图树的顺序执行。视图绘制时会先绘制子控件。如果视图的背景可见，视图会在调用onDraw函数之前绘制背景。强制重绘，可以使用invalidate()。

**事件的基本流程**

1. 事件分配给相应视图，视图处理它，并通知相关监听器。
2. 操作过程中如果发生视图的尺寸变化，则该视图用调用requestLayout()方法，向父控件请求再次布局。
3. 操作过程中如果发生视图的外观变化，则该视图用调用invalidate()方法，请求绘。
4. 如果requestLayout()或invalidate()有一个被调用，框架会对视图树进行相关的测量、布局和绘制。

注意，视图树是单线程操作，直接调用其它视图的方法必须要在UI线程里。跨线程的操作必须使用句柄Handler。

## 补充
**关于LayoutInflater，LayoutInflater.inflate()：**
LayoutInflater是一个用来实例化XML布局文件为View对象的类
LayoutInflater.infalte(R.layout.test,null)用来从指定的XML资源中填充一个新的View
## 参考
[Android的View和ViewGroup分析](http://blog.csdn.net/gemmem/article/details/7783525)
[Android中Activity Window View ViewGroup之间的关系](http://www.bkjia.com/Androidjc/1017970.html)