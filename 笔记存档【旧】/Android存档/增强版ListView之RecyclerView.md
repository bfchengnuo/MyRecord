---
title: 增强版ListView之RecyclerView
date: 2017-04-02 17:21:12
tags: Android
categories: Android
---

ListView可以号称是最常用同时也是最难用的布局，尤其是在处理触摸冲突的时候，扩展性也不强，所以，Google 就提供了它的增强版：RecyclerView ，使用起来也是非常的简单<!-- more -->
在看这个之前，最好搞明白 ListView 的使用，传送门：[ListView整理](https://bfchengnuo.com/2016/08/29/ListView%E6%95%B4%E7%90%86/)

## 基本使用

和百分比布局类似，作为新增的控件，为了能兼容所以的版本，采用了 support 库的模式，使用之前要先添加依赖：
`compile 'com.android.support:recyclerview-v7:24.2.1'`

然后就好说了，定义一个简单的布局：

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/rec"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
</LinearLayout>
```

然后我们来编写它的适配器，这个适配器特别好用，省了很多事呢，相信如果你用过 ListView 就能完全看明白的

```java
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private List<TestBean> mList;

    public RecyclerAdapter(List<TestBean> mList) {
        this.mList = mList;
    }

    // 自定义我们的 ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        View paramsView;

        public ViewHolder(View itemView) {
            super(itemView);
            paramsView = itemView;
            mTextView = (TextView) itemView.findViewById(R.id.test_tx);
            mImageView = (ImageView) itemView.findViewById(R.id.test_image);
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_items, parent, false);

        final ViewHolder viewHolder = new ViewHolder(view);
        // 给 TextView 指定点击事件
        viewHolder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = viewHolder.getAdapterPosition();
                Toast.makeText(v.getContext(), "click " + mList.get(pos).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        // 设置全局的点击事件
        viewHolder.paramsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "click paramsView", Toast.LENGTH_SHORT).show();
            }
        });

        return viewHolder;
    }

    // 每个子条目滚动到屏幕后会执行此方法
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // 绑定数据
        TestBean bean = mList.get(position);
        holder.mTextView.setText(bean.getName());
        holder.mImageView.setImageResource(bean.getImageID());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
```

至于使用的 Bean 我就不多说了，就是一个简单的 ImageView 和 TextView
{% note info %}
需要注意的是：
当继承 RecyclerView.Adapter 的时候最好指定一下泛型，类型就是我们在里面定义的静态的 ViewHolder
同时，ViewHolder 要继承自RecyclerView.ViewHolder

还有需要注意的是：
RecyclerView 不能设置 OnItemClickListener，因为在 ListView 中这个就产生了很多的冲突，所以索性就不加了
我们的监听事件都设置在了 Adapter 中，这样其实也解决了冲突的问题，虽然稍微麻烦点
{% endnote %}

其他的基本都是模板代码了，没啥好说的，在 ListView 我们也这样干

然后在 Activity 中的代码也很简单：

```java
public class RecyclerActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<TestBean> mBeen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);

        mRecyclerView = (RecyclerView) findViewById(R.id.rec);
        initData();
        // 指定布局方式，Linear 的话默认就是类似 ListView 了
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // 默认是纵向的，可以设置为横向
        // layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(new RecyclerAdapter(mBeen));

    }

    private void initData() {
        mBeen = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            TestBean bean1 = new TestBean(R.mipmap.ic_launcher,"Test---" + i);
            mBeen.add(bean1);
            TestBean bean2 = new TestBean(R.mipmap.img1,"Test---" + i);
            mBeen.add(bean2);
            TestBean bean3 = new TestBean(R.mipmap.img2,"Test---" + i);
            mBeen.add(bean3);
            TestBean bean4 = new TestBean(R.mipmap.img3,"Test---" + i);
            mBeen.add(bean4);
        }
    }
}
```

这里我在 initData 方法里造了一些数据~~ 哈
主要就是设置滚动的方向了，LinearLayoutManager 可以提供横向和纵向滚动，默认是纵向的

不过如果要设置横向滚动的话，最外层的布局高度最好固定一下，因为设为 wrap_content 可能会出现高低不平的情况

## 瀑布流布局

LayoutManager 中制定了一套可扩展的布局排列接口，子类只要按照接口规范来实现，就能定制出各种不同排列方式的布局了

除了 LinearLayoutManager 外，RecyclerView 还给我们提供了 GridLayoutManager 和 StaggeredGridLayoutManager 这两种内置布局；前者可用于实现网格布局，后者就是瀑布流布局了，比如瀑布流的就是

```java
StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
```

第一个参数是指定布局的列数，这里就是一个三列的瀑布流
第二个参数是布局的排列方向，这里是纵向排列

好了就是这么简单

## 下拉刷新

配合使用 SwipeRefreshLayout 是非常不错的，[飞机直达](https://bfchengnuo.com/2016/05/24/SwipeRefreshLayout%E5%AE%98%E6%96%B9%E4%B8%8B%E6%8B%89%E5%88%B7%E6%96%B0/)

## 缓存机制

RecyclerView 缓存机制和 ListView 其实差不多，但是它比 ListView 多两级缓存（ListView 是二级缓存，RecyclerView 有四级缓存），在 RecyclerView 中基本上是通过三个内部类管理的，Recycler、RecycledViewPool 和 ViewCacheExtension。

-   Recycler
    Recycler 用于管理已经废弃或者与 RecyclerView 分离的 ViewHolder
-   RecycledViewPool
    RecycledViewPool 类是用来缓存 Item 用，**是一个 ViewHolder 的缓存池**，如果多个 RecyclerView 之间用
    `setRecycledViewPool(RecycledViewPool)` 设置同一个 RecycledViewPool，他们就可以共享 Item。
    其实 RecycledViewPool 的内部维护了一个Map，里面以不同的 viewType 为 Key 存储了各自对应的 ViewHolder 集合。可以通过提供的方法来修改内部缓存的 Viewholder。
-   ViewCacheExtension
    ViewCacheExtension 的代码如果看一下的话什么都没有，没错这是一个需要开发者重写的类。
    上面的 Recycler 里其实是调用 `Recycler.getViewForPosition(int)` 方法获取 View ，Recycler 先检查自己内部的attachedScrap 和一级缓存，再检查 `ViewCacheExtension.getViewForPositionAndType(Recycler, int, int)`，最后检查 RecyclerViewPool，**从上面三个任何一个只要拿到 View 就不会调用下一个方法**。
    所以我们可以重写 `getViewForPositionAndType(Recycler recycler, int position, int type)`，在方法里通过 Recycler 类控制 View 缓存。
    注意：如果你重写了这个类，Recycler 不会在这个类中做缓存 View 的操作，是否缓存 View 完全由开发者控制。

![recycle缓存.png](http://o6lgtfj7v.bkt.clouddn.com/recycle缓存.png)

## 参考

http://www.jianshu.com/p/32c963b1ebc1
http://dev.qq.com/topic/5811d3e3ab10c62013697408
http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2017/0116/7039.html