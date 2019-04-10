---
title: 层级列表ExpandableListView
date: 2017-04-09 12:47:30
tags: [Android,ListView]
categories: Android
---

**ExpandableListView 是 ListView 的子类**，从名字也可以看出，它其实就是 ListView 的一个扩展，所以 ExpandableListView 就是一个可以扩展的、有层级的 ListView。
其实在很早以前就有这个控件，只是我一种不知道.....<!-- more -->
通俗来说，其实就是一个可以显示垂直**两级列表项**的视图，就是 ListView 里 再套 ListView 的感觉
就把第一层叫做分组吧，分组可以单独展开显示子选项，类似 QQ 好友列表那样的，这些选项的数据是通过 ExpandableListAdapter 关联的。

至于布局文件，没什么好说的，和 ListView 一样，直接扔上就可以了

```xml
<ExpandableListView
     android:id="@+id/expand_list"
     android:layout_width="match_parent"
     android:layout_height="match_parent" />
```

关于数据源说一下，外面的分组可以用一个字符串数组来，但是里面的子选项一定要用一个二维数组来放，并且和外面的分组相对应起来，当然集合也是可以的

最重要的就是适配器了，看下面一张图
![层级列表adapter.png](http://o6lgtfj7v.bkt.clouddn.com/层级列表adapter.png)

## 常用属性/方法

由于 ExpandableListView  继承自 ListView，所以 ListView 可用的属性，ExpandableListView 也可以使用，除此之外，由于它是双层的所以肯定有几个特殊的属性的

-   android:groupIndicator
    组指示器。取值可以是任意的 Drawable 对象(外层标识图标)。显示在 该分组的最左侧。如果不设置的话，默认是一个向下的箭头，点击展开内容之后会变成向上的箭头。
    当然是可以自定义一个 selector 文件来进行自定义的，使用 `android:state_expanded` 属性来控制展开或关闭时的图片
    如果出现遮挡显示，需要手动在 XML 或者代码中进行右移一下
-   android:childIndicator
    子条目指示器。取值可以是任意的 Drawable 对象。显示在分组中的每一个 子条目 的最左侧。没有默认图标。

还有一点是，如果不想要默认的 gropIndicator 可以设置属性为：`android:groupIndicator="@null"`

其他属性：

-   android:childDivider
    来分离**子列表项**的图片或者是颜色。注：图片不会完全显示，分离子列表项的是一条直线
-   android:childIndicatorLeft
    **子列表项**指示符的左边约束位置。注：即从左端0位置开始计数，比如，假设指示符是一个图标，给定这个属性值为3dip，则表示从左端起3dip开始显示此图标。
-   android:childIndicatorRight
    子列表项指示符的右边约束位置。注：表示右端到什么位置结束
-   android:indicatorLeft
    组列表项指示器的左边约束位置。注：表示左端从什么位置开始。
-   android:indicatorRight
    组列表项指示器的右边约束位置。注：表示右端到什么位置结束。

>   在XML布局文件中，如果 ExpandableListView **上一级视图的大小**没有严格定义的话，则不能对 ExpandableListView 的 **android:layout_height** 属性使用 **wrap_content**  值。 
>   例如，如果上一级视图是 ScrollView 的话，则不应该指定 wrap_content 的值，因为它可以是任意的长度。
>   如果由于开发的时候粗心，对 ExpandableListView 指定 wrap_content 的值，则会报一个在 SetContentView 处的空指针错误。

顺便说下常用的方法：

-   setOnChildClickListener(OnChildClickListener listener)
    设置分组中子条目的点击监听器
-   setOnGroupClickListener(OnGroupClickListener listener)
    设置分组的点击监听器
-   setOnGroupCollapseListener(OnGroupCollapseListener listener)
    设置分组收起监听器
-   setOnGroupExpandListener(OnGroupExpandListener listener)
    设置分组展开监听器
-   collapseGroup( int position)
    收起 pos 位置的分组
-   expandGroup(int position)
    展开 pos 位置的分组
-   isGroupExpanded(int position)
    判断 pos 位置的分组是否展开

最后的效果图类似：
![](http://upload-images.jianshu.io/upload_images/1820210-94ca54283bfee208.jpg?imageMogr2/auto-orient/strip)

## 定义适配器

ExpandableListView 本质是一个 AdapterView，既然是 AdapterView，那么在展示数据的时候就需要使用到 Adapter ——在展示 ExpandableListView  的数据时使用就是 ExpandableListAdapter，但是它需要复写的方法太多了，我们一般也是用不太到，所以我们定义的适配器一般是下面的几种方式：

-   直接自定义 Adapter **继承BaseExpandableListAdapter.**
    这种方式的关键是实现四个方法：
    `getCroupCount()` 返回组的数量；
    `getCroupView()` 返回组的view对象；
    `getChildCount()`  返回某组的子View数量；
    `getChildView()`  返回某组中的子view.
-   使用 SimpleExpandableListAdapter 将两个List集合包装成 ExpandableListAdapter
    这个貌似用于和 ExpandableListActivity 进行连用的，单独也是可以的，参数比较多
    第一个参数： 应用程序接口 this
    第二个参数： 父列 `List<?extends Map<String,Object>>` 集合 为父列提供数据
    第三个参数： 父列显示的组件资源文件
    第四个参数： 键值列表 父列 Map 字典的 key
    第五个参数： 要显示的父列组件 id
    第六个参数： 子列的显示资源文件
    第七个参数： 键值列表的子列 Map 字典的 key
    第八个参数： 要显示子列的组件 id
-   使用 SimpleCursorTreeAdapter 将 Cursor 中的数据包装成 SimpleCursorTreeAdapter

首先来看看 ExpandableListAdapter ，虽然一般不会直接使用它，这个里面的方法比较多，挑一些比较常用的，其他的一般保持默认就好，用到再补充

```java
// 获取分组的个数
 @Override
 public int getGroupCount() {
     return groupStrings.length;
 }

 // 获取指定分组中的子选项的个数
 @Override
 public int getChildrenCount(int groupPosition) {
     return childStrings[groupPosition].length;
 }

 // 获取指定的分组数据
 @Override
 public Object getGroup(int groupPosition) {
     return groupStrings[groupPosition];
 }

 // 获取指定分组中的指定子选项数据
 @Override
 public Object getChild(int groupPosition, int childPosition) {
     return childStrings[groupPosition][childPosition];
 }

 // 获取指定分组的ID, 这个ID必须是唯一的，一般直接就传 pos 了
 @Override
 public long getGroupId(int groupPosition) {
     return groupPosition;
 }

 // 获取子选项的ID, 这个ID必须是唯一的，同上
 @Override
 public long getChildId(int groupPosition, int childPosition) {
     return childPosition;
 }

 // 分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
 @Override
 public boolean hasStableIds() {
     return true;
 }

 // 获取显示指定分组的视图
 @Override
 public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
     GroupViewHolder groupViewHolder;
     if (convertView == null) {
         convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_expand_group, parent, false);
         groupViewHolder = new GroupViewHolder();
         groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_group);
         convertView.setTag(groupViewHolder);
     } else {
         groupViewHolder = (GroupViewHolder) convertView.getTag();
     }
     groupViewHolder.tvTitle.setText(groupStrings[groupPosition]);
     return convertView;
 }

 // 获取显示指定分组中的指定子选项的视图
 @Override
 public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
     ChildViewHolder childViewHolder;
     // ... 同上，和 ListView 也差不多
     return convertView;
 }

 // 指定位置上的子元素是否可选中
 @Override
 public boolean isChildSelectable(int groupPosition, int childPosition) {
     return true;
 }

static class GroupViewHolder {
 TextView tvTitle;
}
```

是不是感觉和 ListView 非常的相似，就是多了一层而已
一般都是继承 BaseExpandableListAdapter 需要复写的方法在上面都已经包含了

## 其他问题

如果遇到了显示错乱等缓存问题，可以参考 ListView 的解决方案，毕竟它们是父子关系~~我是这样想的，待补充...

ExpandableListActivity/ExpandableListView 的关系和 ListActivity/ListView 的关系差不多

## 关于ListActivity的补充

额...虽然这篇主要写的是 ExpandableListView，但是它也有个 ExpandableListActivity 它和 LIstActivity 其实差不多，相比之下 ListView 用的比较多所以就说说 LIstActivity 吧（嗯？逻辑好像不太对）
但是，使用 ListActivity 确实不多，起码我发现的不多，大部分还是用 LIstView ，所以就简单写写，不加到 LIstView 那篇了，那篇也确实有点长了

### 什么是ListActivity

ListActivity 类继承 Activity 类，默认绑定了一个 ListView（列表视图）界面组件，并提供一些与列表视图、处理相关的操作。
{% note info %}
注意：
**ListActivity 简单的说就是 ListView 和 Activity 的结合。** 
ListActivity 可以不用 `setContentView(R.layout.main)`，它默认是 ListView 占满屏。 
如果想在屏幕中显示其他控件，如文本框和按钮之类，可以采用如下步骤： 

1.  代码中添加：
    通过 `setContentView(R.layout.main); ` 来设定自己的布局
2.  xml 文件中：
    必须添加一个 ListView 控件和一个 TextView 控件，注意它们 id 必须分别为`@id/android:list` , `@id/android:empty` ,前一个表示表示匹配的 ListView，后一个表示若 ListView 没有内容则显示的提示。 
    除此之外可以自由添加控件

{% endnote %}

### 使用

XML 中补充个属性，比较常用，当然和 LIstView 是通用的：

-   android:drawSelectorOnTop="true"
    点击某一条记录，颜色会显示在最上面，记录上的文字被遮住，所以点击文字不放，文字就看不到
    如果是 false，就表示点击某条记录不放，颜色会在记录的后面，成为背景色，但是记录内容的文字是可见的

使用非常简单，如果使用默认的布局，直接设置适配器就可以了，如果是自定义的就多加一句代码

```java
public class ListActivityDemo extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        // 造数据的方法不多说
        List<String> items = fillList();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);
    }
}
```

对了，就算是 LIstView 也可以直接在 XML 中设置一个 **android:entries** 属性来展示字符串数组，这样就不需要设置适配器了

## 参考

http://www.jianshu.com/p/9fa82c15fe1e
http://www.jianshu.com/p/05df9c17a1d8
[ListView整理](https://bfchengnuo.com/2016/08/29/ListView%E6%95%B4%E7%90%86/)


