---
title: MaterialDesign风格的使用(二)
date: 2017-04-04 19:25:10
tags: [Android,MaterialDesign]
categories: Android
---

接上次，MD 风格确实非常的漂亮的，有了 Design Support 库使用起来也不难，这次就把剩下的几个给写完，和上次一样都是参考自郭神的《第一行代码》的内容<!-- more -->
另外，在 MaterialDesign 中经常会使用 app 命名空间，记得要加入哦，写在根布局省事！
`xmlns:app="http://schemas.android.com/apk/res-auto"`

## 卡片式布局CardView

CardView 是由 appcompat-v7 库提供的，要保证兼容性嘛~~实际上 CardView 也是一个 FrameLayout，只是额外提供了圆角和阴影等效果，看上去会有立体的感觉。
所以使用之前记得要添加依赖：
`compile 'com.android.support:cardview-v7:25.0.1'`
既然类似一个帧布局，那么使用起来也是非常简单的

```xml
<android.support.v7.widget.CardView 
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:cardCornerRadius="4dp"
    android:elevation="5dp">
    <TextView
        android:id="@+id/info_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
</android.support.v7.widget.CardView>
```

**app:cardCornerRadius 属性**：指定卡片圆角的弧度
**android:elevation 属性**：指定卡片的高度(立体高度)；高度越大投影范围越大，但投影效果越淡，这一点和前面所说的 FloatingActionButton 是一致的

同时因为和 FrameLayout 类似，由于太简单是显示不出太复杂的效果，通常在里面再套一个其他的布局，不过和 RecyclerView 或者 ListView 配合使用的话，比如作为每个条目的布局，效果会非常的不错，比如上次写 RecyclerView 的时候的那个简单布局，就是一个图片和文字，使用 CardView 包装一下的话也是非常不错的：

```xml
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent" 
    android:layout_height="wrap_content"
    android:layout_margin="5dp"
    app:cardCornerRadius="4dp">
    <LinearLayout
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <ImageView
            android:id="@+id/fruit_image"
            android:layout_width="match_parent"
            android:layout_height="100dp"
            android:scaleType="centerCrop"/>
        <TextView
            android:id="@+id/fruit_name"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center_horizontal"
            android:layout_margin="5dp"
            android:textSize="16sp"/>
    </LinearLayout>
</android.support.v7.widget.CardView>
```

这样效果就出来了，其实也没啥好说的，其实就是一个布局

## APPBarLayout

这里还有一个问题，那就是 RecyclerView (其他布局也一样) 会把 ToolBar 给挡住吧。其实并不难理解，由于 RecyclerView 和 ToolBar **都是放置在 CoordinatorLayout 中的**，而前面文章已将说过，CoordinatorLayout 就是一个加强版的 FrameLayout，那么 FrameLayout 中的所有控件在不进行明确定位的情况下，默认都会摆放在布局的左上角，从而也就产生了遮挡的现象。

对于这种问题，使用偏移是唯一的解决办法，即让 RecyclerView 向下偏移一个 ToolBar 的高度，从而保证不会遮挡到 ToolBar。
这里 Design Support 库给我们准备了 APPBarLayout ，能非常容易的解决这个问题，其实它实际上是一个垂直方向的 LinearLayout，并且应用了 MD 的理念，实现起来非常的简单:

```xml
<android.support.design.widget.CoordinatorLayout
      android:layout_width="match_parent"
      android:layout_height="match_parent">

      <android.support.design.widget.AppBarLayout
          android:layout_width="match_parent"
          android:layout_height="wrap_content">
          <android.support.v7.widget.Toolbar
              android:id="@+id/toolbar"
              android:layout_width="match_parent"
              android:layout_height="?attr/actionBarSize"
              android:background="?attr/colorPrimary"
              android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
              app:popupTheme="@style/ThemeOverlay.AppCompat.Light" 
              app:layout_scrollFlags="scroll|enterAlways|snap"/>
      </android.support.design.widget.AppBarLayout>

      <android.support.v7.widget.RecyclerView
          android:id="@+id/recycler_view"
          android:layout_width="match_parent"
          android:layout_height="match_parent"
          app:layout_behavior="@string/appbar_scrolling_view_behavior"/>
</android.support.design.widget.CoordinatorLayout>
```

其实就是把 ToolBar 套了一层 AppBarLayout，然后给 RecyclerView 加了一个 `app:layout_behavior` 的属性，用来指定布局的行为，属性值也是 Design Support 库提供的
还有 Toolbar 的属性：**app:layout_scrollFlags** 可以理解为受影响后的事件：

>   scroll 表示当 RecyclerView 向上滚动的时候， Toolbar 会跟着一起向上滚动并实现隐藏；
>   enterAlways 表示当 RecyclerView 向下滚动的时候，ToolBar 会跟着一起向下滚动并重新显示。
>   snap 表示当 ToolBar 还没有完全隐藏或显示的时候，会根据当前滚动的距离，自动选择是隐藏还是显示。

**不过呢，貌似 APPBarLayout 只能用于 CoordinatorLayout 的子布局中**

## 下拉刷新 SwipeRefreshLayout

这个原来我以前写过了呢，稍微补充了下，[飞机直达](https://bfchengnuo.com/2016/05/24/SwipeRefreshLayout%E5%AE%98%E6%96%B9%E4%B8%8B%E6%8B%89%E5%88%B7%E6%96%B0/)

## 可折叠式标题栏

从外观来看 Toolbar 和传统的 ActionBar 其实没什么两样
在 MaterialDesign 中，我很可以根据自己的喜好来定制标题栏，比如可折叠式标题栏 **CollapsingToolBarLayout**

顾名思义，CollapsingToolbarLayout 是一个作用于 Toolbar 基础之上的布局，它也是由 Design Support 库提供的。
CollapsingToolbarLayout 可以让Toolbar的效果变得更加丰富，不仅仅是一个标题栏，而是能够实现非常华丽的效果。

{% note default %}
**不过，CollapsingToolbarLayout 是不能独立存在的，它在设计的时候就被限定只能作为 AppBarLayout 的直接子布局来使用。而 AppBarLayout 又必须是 CoordinatorLayout 的子布局**。
{% endnote %}

### XML 布局

好，既然明白了层次关系那就开始布局吧！

```xml
<android.support.design.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.design.widget.AppBarLayout
        android:id="@+id/appBar"
        android:layout_width="match_parent"
        android:layout_height="250dp">
        <android.support.design.widget.CollapsingToolbarLayout
            android:id="@+id/collapsing_toolbar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:contentScrim="?attr/colorPrimary"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">
          <ImageView
                android:id="@+id/fruit_image_view"
                android:layout_width="match_parent"
                android:layout_height="match_parent" 
                android:scaleType="centerCrop"
                app:layout_collapseMode="parallax"/>
            <android.support.v7.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin"/>
        </android.support.design.widget.CollapsingToolbarLayout>
    </android.support.design.widget.AppBarLayout>
  
    <android.support.v4.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

    </android.support.v4.widget.NestedScrollView>

</android.support.design.widget.CoordinatorLayout>
```

CollapsingToolbarLayout 的属性也很好理解主题和 Toolbar 都是一致的，只不过这里要实现更加高级的 Toolbar 效果，因此需要将这个主题提到上一层来。
**app:contentScrim属性** ，用于指定 CollapsingToolbarLayout 在趋于折叠状态以及折叠之后的背景色，其实它在折叠之后就是一个普通的 Toolbar，那么背景色肯定应该是 colorPrimary了
**app:layout_scrollFlags属性** ，这个也是见过的，只不过之前是给 Toolbar 指定的，现在也移到外面来了。
scroll ：表示会随着水果内容详情的滚动一起滚动
exitUntilCollapsed ：表示当随着滚动完成折叠之后就保留在界面上，不在移出屏幕。

---

下面再来说下 CollapsingToolbarLayout 内的控件，只是加了一张图片，关键是它们的属性，有一个比较陌生
**app:layout_collapseMode属性**：表示折叠过程中的折叠模式
Toolbar 指定成**pin**，表示在折叠的过程中位置始终保持不变
ImageView 指定成 **parallax** ,表示会在折叠的过程中产生一定的错位偏移，这种模式的视觉效果会非常好

---

再来说说 NestedScrollView 这个控件
从名字看应该和 ScrollView 差不多，事实上也是这样，注意**它和 AppBarLayout 是平级的**。
**NestedScrollView 具有嵌套响应滚动事件的功能**。由于 CoordinatorLayout 本身已经可以响应滚动事件了，因此我们在它的内部就需要使用 NestedScrollView 或 RecyclerView 这样的布局。另外，这里还通过 `app:layout_behavior` 属性指定了一个布局行为，这和之前的 RecyclerView 中的用法是一样的。

**不管是ScrollView还是NestedScrollView，它们的内部都只允许存在一个直接子布局。**因此，如果我们想要在里面放更多东西的话，通常都会嵌套一个 LinearLayout，然后再在 LinearLayout 中放入具体的内容就可以了

以上。当然也可以加一个悬浮按钮，这里就不搞了，如果要加，它和 AppBarLayout  也是平级的

### Activity 代码

布局有了，和他对应的 Activity 也就好写了，参考 《第一行代码》中的代码

```java
public class FruitActivity extends AppCompatActivity {
    public static final String FRUIT_NAME="fruit_name";
    public static final String FRUIT_IMAGE_ID="fruit_image_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit);

        Intent intent=getIntent();
        String fruitName=intent.getStringExtra(FRUIT_NAME);
        int fruitImageId=intent.getIntExtra(FRUIT_IMAGE_ID,0);

        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        ImageView fruitImageView=(ImageView)findViewById(R.id.fruit_image_view);
        TextView fruitContentText=(TextView)findViewById(R.id.fruit_content_text);
        
        // 设置 toolbar ，并将它作为 ActionBar 使用
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbarLayout.setTitle(fruitName);
        Glide.with(this).load(fruitImageId).into(fruitImageView);
        String fruitContent=generatedFruitContent(fruitName);
        fruitContentText.setText(fruitContent);
    }
  
    // “制造” 数据....
    private String generatedFruitContent(String fruitName){
        StringBuilder fruitContent=new StringBuilder();
        for (int i = 0; i <300 ; i++) {
            fruitContent.append(fruitName);
        }
        return fruitContent.toString();
    }

    // 设置 HomeAsUp 按钮的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
```

代码其实非常的简单，那个 HomeAsUp 按钮其实就是向左的后退箭头图标，这是默认的，正好也不需要改
效果图类似这样：
![](http://upload-images.jianshu.io/upload_images/2856867-4e92017420670709.gif?imageMogr2/auto-orient/strip)

## 充分利用系统状态栏

其实这个和我们常说的沉浸式状态栏差不多，就是让背景和状态栏进行融合，不过需要注意的是这个效果只有在 Android 5.0 + 才会支持，按照上面的那个例子，就是把 ImageView 的内容和状态栏进行融合了
使用  **android:fitsSystemWindows** 这个属性来实现，不过只给 ImageView 设置这个属性是没用的，**我们必须将 ImageView 布局中的所有父布局都设置上这个属性才可以**
所以要给 CoordinatorLayout、AppBarLayout、CollapsingToolbarLayout 和 ImageView 添加下面一行属性：
`android:fitsSystemWindows="true"`

但是，这样还是看不到效果，因为还必须在程序的主题中**将状态栏颜色指定成透明色才行**。
指定成透明色的方法很简单，在主题中将 `android:statusBarColor` 属性的值指定成 `@android:color/transparent` 就可以了。但问题在于，这个属性是从 API21，也就是 Android5.0 系统才有的，之前的系统无法使用这个属性。

所以，为了进行兼容性适配，在 res 目录下新建一个 **values-v21** 目录，在里面新建一个 `styles.xml` 资源文件

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="FruitActivityTheme" parent="AppTheme">
        <item name="android:statusBarColor">@android:color/transparent</item>
    </style>
</resources>
```

是的，这个目录只有在 API21+ 才会被载入，然后别忘了在 `values/styles.xml` 中进行引用

```xml
<resources>
    <!-- Base application theme. -->
    <style name="AppTheme" parent="Theme.AppCompat.Light.NoActionBar">
        <!-- Customize your theme here. -->
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
    </style>
    <style name="FruitActivityTheme" parent="AppTheme"/>
</resources>
```

然后修改 `AndroidManifest.xml` 文件把这个主题设置给 Activity ，这样就完成了！

```xml
<activity android:name=".FruitActivity"
            android:theme="@style/FruitActivityTheme"/>
```

这样用户体验就会好很多呢！