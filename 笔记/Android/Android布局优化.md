---
title: Android布局优化
date: 2016-05-08 15:31:05
tags: [Android,优化]
categories: Android
---
**写在前面**
看到布局优化的相关内容突然感觉好陌生，于是翻出以前做的笔记以及搜索相关资料重新整理下以便日后方便查询，知识点果然不是看一遍就能记住掌握的，上一篇的Java集合框架也是，需要时不时回头看一下~

## Android常用布局

**从使用量来看（从高到低）**

1. Relativelayout(相对布局)
2. Linearlayout(线性布局)
3. Framelayout(帧布局)
4. Tablelayout(表格布局) //被GridView代替
5. Absoulotelayout(绝对布局)

<!-- more -->
## 布局的优化
**在布局优化中，Androi的官方提到了这三种布局`<include />`、`<merge />`、`<ViewStub />`，并介绍了这三种布局各有的优势**

- 尽量使用LinearLayout和RelativeLayout
- **在布局层次一样的情况下**，建议使用LinearLayout代替RelativeLayout，LinearLayout性能更高
- 将可复用的组件抽取出来并通过include标签使用。
- 使用ViewStub标签来加载一些不常用的布局
- 使用merge标签减少布局的嵌套层次

### 重用之include标签

用法：将公用的组件抽取出来单独放到一个xml文件中，然后使用include标签导入共用布局；

效果：提高UI的制作和复用效率，也能保证制作的UI布局更加规整和易维护。

``` html
	<include
	android:id="@+id/include1"
	layout="@layout/引用的布局文件" />
```
**注意：**
1. android:layout_centerVertical="true"可以调整中间位置
2. 被include进来的布局组件可以通过findViewById()得到并使用
3. `<include />`标签若指定了ID属性，而你的layout也定义了ID，则你的layout的ID会被覆盖
4. 在include标签中所有的Android:layout_*都是有效的，前提是必须要写layout_width和layout_height两个属性。

### 减少视图层级的merge标签

**作用**

合并UI布局，降低嵌套层次

`<merge/>`多用于替换FrameLayout或者当一个布局包含另一个时，<merge/>标签消除视图层次结构中多余的视图组。例如你的主布局文件是垂直布局，引入了一个垂直布局的include，如果include布局使用的LinearLayout就没意义了，使用的话反而减慢你的UI表现。这时可以使用`<merge/>`标签优化。

**注意**

使用merge作为布局的顶节点的时候，我们原来被merge替代的顶节点是不能有多余的属性的，否则merge实现不了.

被include进来的布局文件可以使用merge标签，如果用merge标签作为顶级节点，那么引用的时候会被自动忽略。

``` html
<merge xmlns:android="http://schemas.android.com/apk/res/android">

<ProgressBar
android:id="@+id/progressBar1"
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:layout_gravity="center" />

</merge>
```

### 需要时使用之ViewStub-----惰性加载

ViewStub标签最大的优点是当你需要时才会加载，使用他并不会影响UI初始化时的性能。各种不常用的布局想进度条、显示错误消息等可以使用`<ViewStub />`标签，以减少内存使用量，加快渲染速度。

`<ViewStub />`是一个不可见的，大小为0的View,viewStub引入的布局默认不会扩张，既不会占用显示也不会占用位置，从而在解析layout时节省CPU和内存

**使用方法**
``` html
<ViewStub 
	android:layout="@layout/common_text" 
	android:layout_width="match_parent"
	android:layout_height="wrap_content"
	android:id="@+id/viewStub" />
```
Java代码部分：
``` java
...
private ViewStub stub;
...
stub = (ViewStub)findViewById(R.id.viewStub);
stub.inflate(); //显示布局
//显示布局的另一种方法
((ViewStub) findViewById(R.id.stub_import)).setVisibility(View.VISIBLE);

```

当调用inflate()函数的时候，ViewStub被引用的资源替代，并且返回引用的view。 这样程序可以直接得到引用的view而不用再次调用函数findViewById()来查找了。

**注意：ViewStub目前不支持`<merge/>`**
