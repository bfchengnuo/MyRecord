---
title: ListView整理
date: 2016-08-29 12:24:35
tags: [Android]
categories: Android
---
ListView的使用频率非常之高，然而我现在也就只会一些简单的使用，ListView能做的东西远远比我想象的要强大，打算用本文来整理ListView相关的知识。
<!-- more -->
更多拓展慢慢学习中...

## 关于ListView自带缓存机制

![](/image/dev/listview%E7%9A%84%E7%BC%93%E5%AD%98%E6%9C%BA%E5%88%B6.jpg)
无论有多少个条目，最终显示到屏幕的条目是一定的，我们能看到的才会存在内存中，其他的都在Recycler(回收器)中。
ListView先请求一个type1视图(getView)然后请求其他可见的项目。convertView在getView中是空(null)的，第一次都是为空的，只要显示过了convertView都不为空，会保存在Recycler中。
当item1**完全滚出屏幕**，并且一个新的项目从屏幕低端上来时，ListView再请求一个type1视图。convertView此时不是空值了，它的值是item1。**只需设定新的数据**然后返回convertView，不必重新创建一个视图，省去了inflate和findViewById的时间(是很耗时的)，性能就得到了优化。
了解了它的工作原理后，我们就可以重复利用convertView，只要不为空就直接使用，改变它的内容就行了。

简单来说就是：

> 为了使得性能更优，ListView会缓存行item(某行对应的View)。ListView通过adapter的getView函数获得每行的item。滑动过程中
> a. 如果某行item已经滑出屏幕，若该item不在缓存内，则put进缓存，否则更新缓存；
> b. 获取滑入屏幕的行item之前会先判断缓存中是否有可用的item，**如果有，做为convertView参数传递给adapter的getView。**

在带来性能上的优化的同时，也带来了一些小问题：

**a. 行item图片显示重复**
这个显示重复是指当前行item显示了之前某行item的图片。
比如ListView滑动到第2行会异步加载某个图片，但是加载很慢，加载过程中listView已经滑动到了第14行，且滑动过程中该图片加载结束，第2行已不在屏幕内，根据上面介绍的缓存原理，第2行的view可能被第14行复用，这样我们看到的就是第14行显示了本该属于第2行的图片，造成显示重复。
**b. 行item图片显示错乱**
这个显示错乱是指某行item显示了不属于该行item的图片。
比如ListView滑动到第2行会异步加载某个图片，但是加载很慢，加载过程中listView已经滑动到了第14行，第2行已不在屏幕内，根据上面介绍的缓存原理，第2行的view可能被第14行复用，第14行显示了第2行的View，这时之前的图片加载结束，就会显示在第14行，造成错乱。
**c. 行item图片显示闪烁**
上面b的情况，第14行图片又很快加载结束，所以我们看到第14行先显示了第2行的图片，立马又显示了自己的图片进行覆盖造成闪烁错乱。

知道了问题所在，解决也就简单了，**我们可以给图片设置一个标识(setTag)，设置位图片的网址什么的，加载的时候进行对比，如果相同才加载缓存，**关于缓存可以使用[Android ImageCache图片缓存](http://www.trinea.cn/android/android-imagecache/)，或者Android自带的LruCache

## 关于ListView适配的写法

知道了listview的缓存机制，相对的，适配器的写法也就有一些相关的内容，最多的还是使用ViewHolder来提高效率，我个人习惯这样写：

```java
public class MyAdapter extends BaseAdapter {

    private List<Dynamic> mList;
    private LayoutInflater mInflater;
    private Context mContext;

    public MyDynamicAdapter(Context context, List<Dynamic> list) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.view_dynamic_item, null);

            viewHolder.initView(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.initData(position);
        return convertView;
    }

    class ViewHolder implements View.OnClickListener{
        private TextView mTvTime;
        private TextView mTvName;

        private void initView(View view) {
          //初始化view相关，findViewById和设置监听事件
            mTvTime = (TextView) view.findViewById(R.id.tv_time);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
        }

        public void initData(int position) {
			//初始化数据相关操作
            mTvName.setText(XXXX);
        }
    }
}
```

使用ViewHolder设计模式来保存控件，避免了多次重复进行findViewById，大大提高了效率。

## 含有多种视图的布局ListView

如果想要ListView含有不同的布局，官方在适配器中提供了两个方法必须去重写：

- getViewTypeCount //用来返回在这个listview中有几种不同的item类型
- getItemViewType //用来返回某个具体位置上面的item的类型

其实适配器和我们以前的写法也差不了多少，就是多了几个ViewHolder，因为要缓存不同种类的布局
下面举个简单例子，假设我们现在有两种不同的布局，那就可以这样写：

```java
@Override
public int getItemViewType(int position) {
	//也就是第一条一个布局，其他的一个布局	
  	return position>0?1:0;
}

@Override
public int getViewTypeCount() {
  	//一共有两种类型
	return 2;
}
```

然后就是重要的在适配器中的getView方法了：

```java
@Override
public View getView(int position, View convertView, ViewGroup parent) {
     View view = null;
     if(getItemViewType(position) == 1)//也就是第一条(头布局)下面的布局类型
     {
       //下面的就很属性了，没什么变化
        ViewHolder holder = null; 
        if(convertView==null) {
         	view = m_inflater.inflate(R.layout.list_item, null);
         	holder = new ViewHolder();
         	holder.initView();
         	view.setTag(holder);
        } else {
            view  = convertView;
            holder = (ViewHolder)view.getTag();
        }
        holder.initData();
     }else if(getItemViewType(position) == 0){//如果是顶部viewpager
       //只是ViewHolder变化了 
       	ViewPagerHolder holder = null; 
        if(convertView==null){
		  //同上，如果第一条(头布局)是个ViewPage，可以在这里进行初始化数据
          ViewPageAdapter viewadapter = new ViewPageAdapter(listtemp);
          holder.viewPager.setAdapter(viewadapter);
         }else{
            view  = convertView;
            holder = (ViewPagerHolder)view.getTag();
         }              
     }
     return view;
}
```

其实最后还是要使用ListView的复用概念，先判断是那种类型的布局，然后再判断convertView是不是空，来决定要不要进行复用。

## 添加头布局/尾布局

当listview需要添加headerview时，可以通过调用listview的`addHeaderView(headView, null, false) `方法，该方法还有一个重载方法 `addHeaderView(headView)`;这两个方法的区别是前一个方法可以控制header是否可以被selected，如果不想被selected则将第三个参数设置成false；

这里要注意一点，在添加布局的时候它是从父容器开始添加的,而不可以单独添加某个父容器中的某个子控件。

添加header时调用的addHeaderView方法**必须放在listview.setadapter前面**，意思很明确就是如果想给listview添加头部则必须在给其绑定adapter前添加，否则会报错。原因是当我们在调用setAdapter方法时会Android会判断当前listview是否已经添加header，如果已经添加则会生成一个新的tempadapter，这个新的tempadapter包含我们设置的adapter所有内容以及listview的header和footer。
所以当我们在给listview添加了header后在程序中调用listview.getadapter时返回的是tempadapter而不是我们通过setadapter传进去的adapter。所以listview.getadapter().getcount()方法返回值会比我们预期的要大，原因是添加了header。

接着上面的tempadapter说，我们自定义adapter里面的getitem方法里面返回的position是不包括header的，是我们自定义adapter中数据position编号从0开始，也就是说与我们传进去的list的位置是一样的。

但是在而listview的onitemclick方法中：

```java
//position是当前click的位置，这个位置是指在tempadapter中的位置
//从0开始如果listview中添加了header则0代表header。
//这里的id就是适配器中getItemId所返回的
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	//点击逻辑
    }
```

这里可以配合上面的ListView多种布局实现更好的效果，也可以看看这篇文章：

[实现顶部轮播，下部listview经典布局的两种方式](http://www.cnblogs.com/gaoteng/p/4162749.html)

## 分页加载

当ListView需要加载的数据非常多的时候，并且如果还是布局非常复杂数据量很大的情况下，这样如果加载全部数据就会非常耗时，这样用户体验就会很差，所以就有了分页加载，下面看一个经典的实现：
详细分析见参考地址。
布局方面没什么内容，主activity就是一个ListView，加载更多的布局用了上面所说的尾布局(暂且这样说吧),一个button控件还有一个进度条来现实，通过监听按钮的点击事件进行追加数据和控制相关的现实/隐藏，下面的代码只是为了突出重点，想复制粘贴的去原文，见参考。

```java
public class MoreDateListActivity extends Activity implements OnScrollListener {
    
    // ListView的Adapter,数据很简单略了...
    private MyAdapter mAdapter;
    private ListView lv;
    private Button bt;
    private ProgressBar pg;
  	//嗯，，list原来还可以这样用...
    private ArrayList<HashMap<String,String>> list;
    // ListView底部View
    private View moreView;
    // 设置一个最大的数据条数，超过即不再加载
    private int MaxDateNum;
    // 最后可见条目的索引
    private int lastVisibleIndex;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        MaxDateNum = 22; // 设置最大数据条数

        lv = (ListView) findViewById(R.id.lv);

        // 实例化底部布局
        moreView = getLayoutInflater().inflate(R.layout.moredate, null);

        bt = (Button) moreView.findViewById(R.id.bt_load);
        pg = (ProgressBar) moreView.findViewById(R.id.pg);

        // 用map来装载数据，模拟数据源
        list = new ArrayList<HashMap<String,String>>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("ItemTitle", "第" + i + "行标题");
            map.put("ItemText", "第" + i + "行内容");
            list.add(map);
        }
        
        // 加上底部View，注意要放在setAdapter方法前
        lv.addFooterView(moreView);
        lv.setAdapter(mAdapter);
        // 绑定监听器
        lv.setOnScrollListener(this);

      	//加载更多的按钮
        bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                pg.setVisibility(View.VISIBLE);// 将进度条可见
                bt.setVisibility(View.GONE);// 按钮不可见

              	//模拟追加数据的逻辑...
              	loadMoreDate();
              	//记得完成后设置回Visibility
              	mSimpleAdapter.notifyDataSetChanged();// 通知listView刷新数据
            }
        });
    }

    private void loadMoreDate() {
        int count = mSimpleAdapter.getCount();
        if (count + 5 < MaxDateNum) {
            // 每次加载5条
          for (int i = count; i < count + 5; i++) {
                //省略获取数据逻辑
            	list.add(map);
            }
        } else {
            // 数据已经不足5条
            for (int i = count; i < MaxDateNum; i++) {
                list.add(map);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // 计算最后可见条目的索引，第一个加可见的item数减一
        lastVisibleIndex = firstVisibleItem + visibleItemCount - 1;

        // 所有的条目已经和最大条数相等，则移除底部的View
        if (totalItemCount == MaxDateNum + 1) {
            lv.removeFooterView(moreView);
            Toast.makeText(this, "数据全部加载完成，没有更多数据！", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // 滑到底部后自动加载，判断listview已经停止滚动并且最后可视的条目等于adapter的条目
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && lastVisibleIndex == mSimpleAdapter.getCount()) {
            // 当滑到底部时自动加载
            //类似于loadMoreDate();方法
        }
    }
}
```

我当时是偷懒了，直接从gayhub找了个开源库，就简单了一些~

## 监听事件

### 使用onTouchListener()监听触摸事件

```java
mListView.setOnTouchListener(new View.OnTouchListener() {
       @Override
       public boolean onTouch(View v, MotionEvent event) {
           switch (event.getAction()){
               case MotionEvent.ACTION_DOWN:
                   //触摸时操作
                   break;
               case MotionEvent.ACTION_MOVE:
                   //移动时操作
                   break;
               case MotionEvent.ACTION_UP:
                   //离开时操作
                   break;
               default:
                   break;
           }
           return false;
       }
});
```

### 通过onScrollListener来实现监听

```java
mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
          @Override
          public void onScrollStateChanged(AbsListView view, int scrollState) {
              
              switch (scrollState){
                  case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                      //滑动停止时
                      break;
                  case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                      //正在滚动
                      break;
                  case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                      //手指抛动时操作(手指离开ListView后ListView由于惯性继续滑动)
                      break;
              }
          }
          /**
           * 
           * @param view
           * @param firstVisibleItem   第一个可见item的索引
           * @param visibleItemCount   ListView的可见item数，注意，这里的可见是只要稍微露出就视为可见,不必全部显示
           * @param totalItemCount     ListView的总item数
           */
          @Override
          public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
               //滚动时一直调用
          }
});
```
### 使用onItemClick监听条目点击事件

```java
 @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      //parent.getAdapter();
      //区分ListView
      if (parent.getId() == R.id.listview) {
        
      }
      if (view.getId() == R.id.listview) {
        
      }
    }
```

解释下几个参数：
**parent：**是识别是哪个listview；我基本是在使用多个listView时用于获取其的适配器：`parent.getAdapter()`
**view：**是当前listview的item的view的布局，就是可以用这个view，获取里面的控件的id后操作控件
**position：**是当前item在listview中**适配器**里的位置
**id：**是当前item**在listview里**的第几行的位置，大部分时候position和id的值是一样的。

当然还有长按的监听： setOnItemLongClickListener

## 一些补充

### 点击事件的冲突

item内如果有button等控件时，在监听listview的onitemclick事件时，焦点会被item内的button、imagebutton等控件抢走，从而导致在listview设置了onitemclick事件后不会被触发。解决方法是在初始化item的时候屏蔽掉其内部button等控件的焦点获取，可以在自定义item的根控件中调执行下面代码屏蔽：

``` java
setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS); 
```

当然在 XML 中使用 `focusale=”false”` 设置也是可以的
当然也可以用一些其他非点击功能的控件代替，例如textview，也没什么别扭。
这样就能阻塞字控件抢夺焦点，listview的onitemclick就能被正确触发，同时对item内部的button等控件也没有影响，他们在被点击时照样可以触发自身的点击事件。

### 具有弹性的ListView

在IOS中，当列表下拉到底部是会继续滑动一段距离提升了用户体验，但是Android并没有提供这样的效果，仅仅是在5.0之后加入了一个半月形的阴影，但是ListView中提供了一个方法overScrollBy()方法来控制滑动到边缘的处理方式，可以通过重写该方法为ListView提供滑动到边缘时额外的滑动距离。
下面是一段示例代码：

```java
public class MyListView extends ListView {
    private Context context;
    private int maxOverScrollYDistance = 50;
    public MyListView(Context context) {
        super(context);
        this.context = context;
        init();
    }

    private void init() {
      	//通过density来计算弹性高度，让不同屏幕的手机高度基本一致
        float density = context.getResources().getDisplayMetrics().density;
        maxOverScrollYDistance = (int) (density * maxOverScrollYDistance);
    }

    //当ListView滑动到边缘的时候调用,覆写该方法传递自己距离,只是修改了Y轴的数值
    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, maxOverScrollYDistance, isTouchEvent);
    }
}
```

### 关于一些xml的属性

使用ListView的时候不妨看看下面的一些常见属性

```html
android:dividerHeight="10dp"  //设置分割线高度
android:divider="@color/gray" //设置分割线颜色
android:divider="@null"       //设置分割线为透明
android:scrollbars="none"		//隐藏滚动条
android:listSelector="#00000000"	//设置点击效果为透明(取消点击效果)
android:listSelector="@android:color/transparent"	//同上，可以设置为透明色
```

### 一些技巧

```java
//让ListView平滑滚动到指定条目
listView.smoothScrollBy(distance, duration);//平滑滚动 distance 个像素，持续 duration 毫秒。
listView.smoothScrollByOffset(offset);  //参数为偏移量，滚过多少条目(未测试，大概)
listView.smoothScrollToPosition(index); //平滑滚动到指定条目位置
//adapter有一个方法，当数据更改后，可以通知listview进行更新
//注意，记得数据源要是同一个list
adapter.notifyDataSetChanged();
//如果list为空，显示指定view
listView.setEmptyView(view);
//获取当前可视item的位置信息
mListView.getFirstVisiblePosition()
mListView.getLastVisiblePosition()
```

## 参考

http://www.trinea.cn/android/android-listview-display-error-image-when-scroll/
http://blog.csdn.net/ysc20052006/article/details/7417841
http://www.cnblogs.com/gaoteng/p/4162749.html
[分页加载](http://www.cnblogs.com/noTice520/archive/2012/02/10/2345057.html)