---
title: Android中的适配器
date: 2016-05-27 12:27:23
tags: [Android]
categories: Android
---
Android中常见的一些适配器有BaseAdapter、ArrayAdapter、SimpleAdapter、SimpleCursorAdapter等，还可以自定义适配器，那么就必须了解下这些相关知识了，关于适配器辣么多，看着倒是挺眼熟的就是分不清区别，这里稍微了解下，存档以便以后查阅。
<!-- more -->

## 概述
Adapter相当于一个数据源，大多用来给AdapterView提供数据，并根据数据创建对应的UI。

常见的AdapterView的子类有ListView、GridView、ExpandableListView和Spinner等。

以下Adapter相关类的关系图很清晰明了：
![](/image/dev/Adapter.jpg)

## 顶级Adapter接口

Adapter接口定义了如下方法：

- `public abstract void registerDataSetObserver (DataSetObserver observer) `

  Adapter表示一个数据源，这个数据源是有可能发生变化的，比如增加了数据、删除了数据、修改了数据，当数据发生变化的时候，它要通知相应的AdapterView做出相应的改变。为了实现这个功能，**Adapter使用了观察者模式，Adapter本身相当于被观察的对象，AdapterView相当于观察者**，通过调用registerDataSetObserver方法，给Adapter注册观察者。

- `public abstract void unregisterDataSetObserver (DataSetObserver observer)`
  通过调用unregisterDataSetObserver方法，反注册观察者。

- `public abstract int getCount ()` 
  返回Adapter中数据的数量。

- `public abstract Object getItem (int position)` 
  Adapter中的数据类似于数组，里面每一项就是对应一条数据，每条数据都有一个索引位置，即position，根据position可以获取Adapter中对应的数据项。

- `public abstract long getItemId (int position)` 
  获取指定position数据项的id，**通常情况下会将position作为id**。在Adapter中，相对来说，position使用比id使用频率更高。
  在某些方法（如onclicklistener的onclick方法）有id这个参数，而这个id参数就是取决于getItemId()这个返回值的。

- `public abstract boolean hasStableIds ()` 
  hasStableIds表示当数据源发生了变化的时候，原有数据项的id会不会发生变化，如果返回true表示Id不变，返回false表示可能会变化。Android所提供的Adapter的子类（包括直接子类和间接子类）的hasStableIds方法都返回false。

- `public abstract View getView (int position, View convertView, ViewGroup parent)` 
  getView是Adapter中一个很重要的方法，该方法会根据数据项的索引为AdapterView创建对应的UI项。

## ListAdapter接口

从上面的结构图我们也能看出ListAdapter接口继承自Adapter接口

ListAdapter可以作为AbsListView的数据源，**AbsListView的子类有ListView、GridView和ExpandableListView。**

ListAdapter相比Adapter新增了两个方法。

//是否在ListAdapter中的所有项都可用，即是否所有项都可以被选择和被点击
//返回True表示所有条目均可用。
public boolean areAllItemsEnabled(); 

//指定位置的项是否是enabled(可用)的 //是否启用
boolean isEnabled(int position);

## SpinnerAdapter接口

SpinnerAdapter接口同样继承自Adapter接口

SpinnerAdapter可以作为AbsSpinner的数据源，**AbsSpinner的子类有Gallery, Spinner和AppCompatSpinner**。

相比Adapter，SpinnerAdapter中新增了getDropDownView方法，该方法与Adapter接口中定义的getView方法类似，该方法主要是供AbsSpinner调用，**用于生成Spinner下拉弹出区域的UI**。在SpinnerAdapter的子类BaseAdapter中，getDropDownView方法默认直接调用了getView方法。

PS：
ArrayAdapter和SimpleAdapter都重写了getDropDownView方法，这两个类中的getDropDownView方法与其getView的方法都调用了createViewFromResource方法，所以这两个类中方法getView与方法getDropDownView代码基本一致。

CursorAdapter也重写了getView与getDropDownView方法，虽然这两个方法没有使用公共代码，但是这两个方法代码逻辑一致。

综上，我们可知当我们在覆写getDropDownView方法时，应该尽量使其与getView的代码逻辑一致。

## BaseAdapter抽象类

BaseAdapter是抽象类，是Android应用程序中经常用到的基础数据适配器，它的主要用途是将一组数据传到像ListView、Spinner、Gallery及GridView等UI显示组件，它是继承自接口类Adapter其实现了ListAdapter接口和SpinnerAdapter接口

BaseAdapter主要实现的功能： 

- BaseAdapter实现了观察者模式
  Adapter接口定义了方法registerDataSetObserver和unregisterDataSetObserver，BaseAdapter中维护了一个DataSetObservable类型的变量mDataSetObservable，并实现了方法registerDataSetObserver和unregisterDataSetObserver。
  关于什么是DataSetObservable请参考后文的**相关**部分

- BaseAdapter重写了getDropDownView方法，其调用了getView方法
``` java
public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getView(position, convertView, parent);
}
```
- 覆写其他一些方法，设置了默认值，比如覆写hasStableIds方法，使其默认返回false

## ArrayAdapter类
ArrayAdapter是最简单的Adapter，AdapterView会将ArrayAdapter中的数据项调用toString()方法，作为文本显示出来。

ArrayAdapter的构造函数既可以接收List作为数据源，又可以接收一个数组作为数据源，如果传入的是一个数组，那么在构造函数中也会通过Arrays.asList()将数组转换成list，最终用mObjects存储该list。

ArrayAdapter重写了getCount、getItem、getItemId、getView与getDropDownView，其中getView与getDropDownView这两个方法都调用了createViewFromResource方法

ArrayAdapter还增加了add、addAll、insert、remove、clear等方法，当调用了这些方法时，数据会发生变化，ArrayAdapter就会在这些方法里面调用notifyDataSetChanged方法，比如remove源码如下：
``` java
public void remove(T object) {
    synchronized (mLock) {
        if (mOriginalValues != null) {
            mOriginalValues.remove(object);
        } else {
            mObjects.remove(object);
        }
    }
    if (mNotifyOnChange) notifyDataSetChanged();
}
```
如果我们在一个for循环中多次调用add方法添加数据，那么默认会多次触发notifyDataSetChanged方法的执行，由于每次notifyDataSetChanged方法执行后，AdapterView都会重新渲染UI,所以多次触发notifyDataSetChanged方法执行会导致效率比较低。最好的办法是在所有数据变化完成后，我们自己调用notifyDataSetChanged方法。

为此，ArrayAdapter内部提供了一个boolean类型的变量mNotifyOnChange，默认值为true，每次调用add、addAll、insert、remove、clear等方法，都会先判断mNotifyOnChange的值，只有当mNotifyOnChange为true，才会执行notifyDataSetChanged方法。我们可以通过调用setNotifyOnChange方法将mNotifyOnChange设置为false，然后在for循环中多次调用add方法，这样不会触发notifyDataSetChanged方法，在执行完for循环之后，我们自己再调用notifyDataSetChanged方法。

还有一点需要说明的是，如果我们将一个数组作为数据源传递给ArrayAdapter，那么当调用ArrayAdapter的add、addAll、insert、remove、clear等写操作的方法时就会抛出异常Java.lang.UnsupportedOperationException。这是为什么呢？
我们之前在上面提到，如果在构造函数中传入数组，会调用Arrays.asList()将数组转换成List，并赋值给字段mObjects存储。但是Arrays.asList()返回的List是只读的，不能够进行add、remove等写操作，Arrays.asList()返回的List是其实是一个java.util.AbstractList对象，其add、remove方法的默认实现就是抛出异常,详见[《为什么Java里的Arrays.asList不能用add和remove方法？》](http://blog.csdn.net/loveaborn/article/details/39754031)

所以，在使用ArrayAdapter的时候，最好传给构造函数一个可写的List，这样才能正常使用ArrayAdapter的写操作方法。

对此又一个比较好的方法：`List<String> list = new ArrayList<>(Arrays.asList(values));`

我们将Arrays.asList(values)得到的只读的list作为参数实例化了一个ArrayList，新得到的ArrayList是可写的，所以我们将它作为参数传递给ArrayAdapter之后，可以正常调用其remove()方法。

## SimpleAdapter类
类SimpleAdapter继承并实现了BaseAdapter抽象类

SimpleAdaper的作用是方便地将数据与XML文件定义的各种View绑定起来，从而创建复杂的UI。

SimpleAdapter只有一个构造函数，签名如下所示：
``` java
public SimpleAdapter (Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
```
- data表示的是List数据源，其中List中的元素都是Map类型，并且Map的key是String类型，Map的value可以是任意类型，我们一般使用`HashMap<String, Object>`作为List中的数据项。

- resource 表示数据项UI所对应的layout文件

- 每个数据项对应一个Map，from表示的是Map中key的数组。（前面传入的list是：`List<Map<key,val>>`）

- 数据项Map中的每个key都在layout中有对应的View，to表示数据项对应的View的ID数组。

SimpleAdapter是怎么把数据和layout自动关联起来的呢？
SimpleAdapter实现了以下方法：getCount、getItem、getItemId、getView和getDropDownView，其中getView和getDropDownView都调用了createViewFromResource方法，下面为源码：
``` java
private View createViewFromResource(LayoutInflater inflater, int position, View convertView,
        ViewGroup parent, int resource) {
    View v;
    if (convertView == null) {
        v = inflater.inflate(resource, parent, false);
    } else {
        v = convertView;
    }

    bindView(position, v);

    return v;
}
```
在createViewFromResource中，会调用bindView方法，bindView方法的作用就是将数据项与对应的View绑定起来，从而使得View在界面上展现出数据内容。 
bindView的源码如下所示：
``` java
private void bindView(int position, View view) {
    final Map dataSet = mData.get(position);
    if (dataSet == null) {
        return;
    }

    final ViewBinder binder = mViewBinder;
    final String[] from = mFrom;
    final int[] to = mTo;
    final int count = to.length;

    //在for循环中遍历一条数据项中所有的view
    for (int i = 0; i < count; i++) {           
        final View v = view.findViewById(to[i]);
        if (v != null) {
            final Object data = dataSet.get(from[i]);
            String text = data == null ? "" : data.toString();
            if (text == null) {
                text = "";
            }

            boolean bound = false;
            if (binder != null) {
                //首先尝试用binder对View和data进行绑定
                bound = binder.setViewValue(v, data, text);
            }

            //如果binder不存在或binder没有绑定成功，SimpleAdapter会尝试进行自动绑定
            if (!bound) {
                if (v instanceof Checkable) {
                    if (data instanceof Boolean) {
                        ((Checkable) v).setChecked((Boolean) data);
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text);
                    } else {
                        throw new IllegalStateException(v.getClass().getName() +
                                " should be bound to a Boolean, not a " +
                                (data == null ? "<unknown type>" : data.getClass()));
                    }
                } else if (v instanceof TextView) {
                    // Note: keep the instanceof TextView check at the bottom of these
                    // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                    setViewText((TextView) v, text);
                } else if (v instanceof ImageView) {
                    if (data instanceof Integer) {
                        setViewImage((ImageView) v, (Integer) data);                            
                    } else {
                        setViewImage((ImageView) v, text);
                    }
                } else {
                    throw new IllegalStateException(v.getClass().getName() + " is not a " +
                            " view that can be bounds by this SimpleAdapter");
                }
            }
        }
    }
}
```
bindView方法会遍历to数组中View的id，然后通过view.findViewById()方法找到相应的View。那怎么将数据和View绑定起来呢？

主要分两步：
**(一)**
首先通过ViewBinder实现开发者自己绑定数据 
SimpleAdapter中内部有一个ViewBinder类型的成员变量mViewBinder，通过SipmleAdater的setViewBinder方法可以对其赋值，mViewBinder的默认值是null。

ViewBinder是SimpleAdapter的一个内部接口，其定义了setViewValue方法。我们可以定义一个对象，实现ViewBinder接口的setViewValue方法，然后通过setViewBinder赋值给mViewBinder。

在bindView方法中，会首先判断mViewBinder存不存在，如果存在就调用mViewBinder的setViewValue方法，该方法会返回一个boolean值，如果返回true表示开发者自己已经成功将数据和View绑定起来了， 
bound值为true，后面就不会再执行其他逻辑。

**(二)**
如果开发者没有自己绑定数据（这是常见的情形），那么SimpleAdapter会自己尝试去绑定数据

具体来说，如果mViewBinder不存在或者mViewBinder的setViewValue方法返回false，那么bound值为false，这时候Android就会按照自己的逻辑尽量去将数据和View进行绑定。
## 相关知识补充
### Observable-观察者相关
Observable是观察者模式的典型应用。在Android下，Observable是一个泛型的抽象类，表示一个观察者对象，提供了观察者注册、反注册及清空三个方法。
Observable的直接继承者有两个：DataSetObservable和ContentObservable。ContentObservable实现比较复杂，不过功能与DataSetObservable类似。
DataSetObservable使用**DataSetObserver**实例化了Observable。DataSetObserver表示了一个数据集对象的观察者，主要提供了两个方法
``` java
public abstract class DataSetObserver { 

    public void onChanged() {  
        // Do nothing  
    }  

    public void onInvalidated() {  
        // Do nothing  
    }  
}
```
DataSetObservable实现如下:
``` java
public class DataSetObservable extends Observable<DataSetObserver> {

    public void notifyChanged() {  
        synchronized(mObservers) {  
            for (DataSetObserver observer : mObservers) {  
                observer.onChanged();  
            }  
        }  
    }  

    public void notifyInvalidated() {  
        synchronized (mObservers) {  
            for (DataSetObserver observer : mObservers) {  
                observer.onInvalidated();  
            }  
        }  
    }  
}
```
当数据集有变化时，会调用DataSetObserver的onChanged()方法；当数据集失效时，会调用DataSetObserver的onINvalidated()方法。

### 其他 SimpleCursorAdapter类等
暂时还没有用到，有需要再看。
请参考下面连接
## ViewPager适配器相关

参考[这篇博客](http://bfchengnuo.com/2016/05/23/%E5%85%B3%E4%BA%8E%E9%A1%B5%E5%8D%A1ViewPager)

## 参考

[使用详解及源码解析Android中的Adapter、BaseAdapter、ArrayAdapter、SimpleAdapter和SimpleCursorAdapter](http://blog.csdn.net/iispring/article/details/50793455)

[Android中的观察者](http://www.xuebuyuan.com/1665838.html)