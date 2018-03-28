---
title: fragment相关整理
date: 2016-09-02 17:06:52
tags: [Android,fragment]
categories: Android
---
fragment同样又是Android开发中必须掌握的知识点，使用频率非常之高，和activity还是非常相似的，整理存档！
<!-- more -->
然而我还是只会简单的基础使用...

## 生命周期

因为fragment必须依赖于activity，所以自然fragment的生命周期和activity的生命周期是有着紧密的联系，下面的图就说明了这一关系：
![](http://obb857prj.bkt.clouddn.com/20140719225005356.png)

主要关注下几个特有的生命周期函数：

- onAttach(Activity)
  当Fragment与Activity发生关联时调用。

- onCreateView(LayoutInflater, ViewGroup,Bundle)
  创建该Fragment的视图

- onActivityCreated(Bundle)
  当Activity的onCreate方法返回时调用

- onDestoryView()
  与onCreateView想对应，当该Fragment的视图被移除时调用

- onDetach()
  与onAttach相对应，当Fragment与Activity关联被取消时调用
  注意：除了onCreateView，其他的所有方法如果你重写了，必须调用父类对于该方法的实现

## 使用fragment

一般我们有两种方式来使用fragment，静态加载和动态加载

### 静态加载

最重要的就是继承Fragment，重写onCreateView决定Fragemnt的布局了，下面举一个例子，首先是fragment的布局文件：

```html
<?xml version="1.0" encoding="utf-8"?>  
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"  
    android:layout_width="match_parent"  
    android:layout_height="match_parent">
    <TextView  
        android:layout_width="match_parent" 
        android:layout_height="match_parent"
        android:gravity="center"
        android:text="测试"
        android:textSize="20sp" />  
</RelativeLayout>  
```

然后是相关联的Java文件：

```java
public class TitleFragment extends Fragment  
{  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState)  
    {  
        View view = inflater.inflate(R.layout.fragment_title, container, false);  
        return view;  
    }  
}
```

然后就是进行静态的引用了，我们只需要在主activity的XML文件中进行如下定义：

```html
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:tools="http://schemas.android.com/tools"  
    android:layout_width="match_parent"  
    android:layout_height="match_parent" >  
    <fragment  
        android:id="@+id/id_fragment_title"  
        android:name="com.bfcn.fragments.TitleFragment"  
        android:layout_width="match_parent"  
        android:layout_height="match_parent" />
</RelativeLayout>
```

可以看出，主要就是在name的属性中进行引用fragment，注意是要写完整路径的，包括包名，这样我就可以大大简化activity的代码，逻辑可以转移到fragment中。

### 动态加载

首先在布局文件中我们要给它准备一个位置才可以啊

```html
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"  
    xmlns:tools="http://schemas.android.com/tools"  
    android:layout_width="match_parent"  
    android:layout_height="match_parent" >  
  
    <FrameLayout  
        android:id="@+id/id_content"  
        android:layout_width="match_parent"  
        android:layout_height="match_parent" />  
  
</RelativeLayout> 
```

动态加载当然也就是在代码中实现了，在activity中进行动态添加：

```java
public class MainActivity extends Activity{  
    private TestFragment mFragment;  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState)  
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
  
        // 动态添加fragment  
        setFragment();  
    }  
  
    private void setFragment()
    {
      	//获得一个管理者
        FragmentManager fm = getFragmentManager();
        //开启一个事务
      	FragmentTransaction transaction = fm.beginTransaction();
        mFragment = new TestFragment();
        transaction.replace(R.id.id_content, mFragment);
        //提交事务
      	transaction.commit();
    }
}
```

### 添加到回退栈

有时候我们不想按了返回按钮后就退出应用而是返回上一个fragment，这时候就要用到回退栈了，这是事务的一个功能，使用也很简单，但是一般我们不会把第一个fragment加入到回退栈，这样的话回退到最后会是一片空白(就是最下面的activity的布局了)。
下面是一段示例代码：

```java
@Override
public void onClick(View v)
{
    FragmentTwo fTwo = new FragmentTwo();
    FragmentManager fm = getFragmentManager();
    FragmentTransaction tx = fm.beginTransaction();
    tx.replace(R.id.id_content, fTwo, "TWO");
    tx.addToBackStack(null);
    tx.commit();
}
```

这里需要注意的是：FragmentOne实例不会被销毁，但是视图层次依然会被销毁，即会调用onDestoryView和onCreateView。
如果想保留视图的数据那可以用隐藏布局的方式来处理`tx.hide(this)`,添加用add就可以啦~这样视图就不会重绘了

### Fragment与Activity通信

因为所有的Fragment都是依附于Activity的，所以通信起来并不复杂，大概归纳为：

- **如果你Activity中包含自己管理的Fragment的引用，可以通过引用直接访问所有的Fragment的public方法**
- **如果Activity中未保存任何Fragment的引用，那么没关系，每个Fragment都有一个唯一的TAG或者ID,可以通过getFragmentManager.findFragmentByTag()或者findFragmentById()获得任何Fragment实例，然后进行操作。**
  **如果想获得Fragment的视图，可以通过上面获取到的Fragment实例来调用fragment.getView()获取**
- **在Fragment中可以通过getActivity得到当前绑定的Activity的实例，然后进行操作。**

注：如果在Fragment中需要Context，可以通过调用getActivity(),如果该Context需要在Activity被销毁后还存在，则使用getActivity().getApplicationContext()。  

为了让activity能够调用fragment的事件，我们可以定义一个接口，activity只需要实现这个接口即可，实例代码：

```java
public class FragmentOne extends Fragment implements OnClickListener
{
    private Button mBtn;
    /*
     * 定义一个接口，用作按钮点击的回调
     */
    public interface FOneBtnClickListener
    {
        void onFOneBtnClick();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        mBtn = (Button) view.findViewById(R.id.id_fragment_one_btn);
        mBtn.setOnClickListener(this);
        return view;
    }

    /*
     * 回调，交给宿主Activity处理，如果它希望处理
     */
    @Override
    public void onClick(View v)
    {
      	//instanceof 运算符是用来在运行时指出对象是否是特定类的一个实例
      	//也可以通过get方法进行手动设置
        if (getActivity() instanceof FOneBtnClickListener)
        {
            ((FOneBtnClickListener) getActivity()).onFOneBtnClick();
        }
    }
}
```

虽然Fragment和Activity可以通过getActivity与**findFragmentByTag**或者**findFragmentById**，进行任何操作，甚至在Fragment里面操作另外的Fragment，但是没有特殊理由是绝对不提倡的。Activity担任的是Fragment间类似总线一样的角色，应当由它决定Fragment如何操作。

### 补充

如果使用Android3.0以下的版本，需要引入v4的包，然后Activity继承FragmentActivity，然后通过getSupportFragmentManager获得FragmentManager。不过还是建议版Menifest文件的uses-sdk的minSdkVersion和targetSdkVersion都改为11以上，这样就不必引入v4包了。

这里有必要提一下关于事务的一些方法，因为主要的操作都是FragmentTransaction的方法
FragmentTransaction transaction = fm.benginTransatcion();//开启一个事务
transaction.add() //往Activity中添加一个Fragment
transaction.remove() //从Activity中移除一个Fragment，如果被移除的Fragment没有添加到回退栈，这个Fragment实例将会被销毁。
transaction.replace() //使用另一个Fragment替换当前的，实际上就是remove()然后add()的合体~
transaction.hide() //隐藏当前的Fragment，仅仅是设为不可见，并不会销毁
transaction.show() //显示之前隐藏的Fragment
detach() //会将view从UI中移除,和remove()不同,此时fragment的状态依然由FragmentManager维护。
attach() //重建view视图，附加到UI上并显示。
transatcion.commit() //提交一个事务
注意：常用Fragment的哥们，可能会经常遇到这样Activity状态不一致：State loss这样的错误。主要是因为：commit方法一定要在Activity.onSaveInstance()之前调用。
值得注意的是：如果你喜欢使用Fragment，一定要清楚这些方法，哪个会销毁视图，哪个会销毁实例，哪个仅仅只是隐藏，这样才能更好的使用它们。

**也就是说，希望保留用户操作的面板，你可以使用hide和show，当然了不要使劲在那new实例，进行下非null判断。**
**remove和detach有一点细微的区别，在不考虑回退栈的情况下，remove会销毁整个Fragment实例，而detach则只是销毁其视图结构，实例并不会被销毁。那么二者怎么取舍使用呢？如果你的当前Activity一直存在，那么在不希望保留用户操作的时候，你可以优先使用detach**

对于fragment事务，也可以应用动画。在commit()之前调用setTransition()就行啦。

## 需要注意的

**布局重叠问题**
因为fragment是可以对横屏和竖屏进行自动适配，旋转屏幕的时候Activity发生重新启动，默认的Activity中的Fragment也会跟着Activity重新创建；这样造成当旋转的时候，本身存在的Fragment会重新启动，然后当执行Activity的onCreate时，又会再次实例化一个新的Fragment，另外一种情况：当你的应用被至于后台（例如用户点击了home），长时间没有返回的时候，你的应用也会被重新启动，onCreate方法会重新执行，界面上就会有一些错乱
关于解决方法，其实通过检查onCreate的参数Bundle savedInstanceState就可以判断，当前是否发生Activity的重新创建：
默认的savedInstanceState会存储一些数据，包括Fragment的实例，所以我们只要在只有在savedInstanceState==null时，才进行创建Fragment实例就可以避免此类问题。

```java
@Override
protected void onCreate(Bundle savedInstanceState)
{
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main);

    if(savedInstanceState == null)
    {
        mFOne = new FragmentOne();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction tx = fm.beginTransaction();
        tx.add(R.id.id_content, mFOne, "ONE");
        tx.commit();
    }
}
```

现在还存在一个问题，就是重新绘制时，Fragment发生重建，原本的数据如何保持？
其实和Activity类似，Fragment也有onSaveInstanceState的方法，在此方法中进行保存数据，然后在onCreate或者onCreateView或者onActivityCreated进行恢复都可以。

## 拓展

- 没有布局的Fragment的作用
  没有布局文件Fragment实际上是为了保存数据，当Activity重启时，保存大量数据准备的
  请参考博客：[Android 屏幕旋转 处理 AsyncTask 和 ProgressDialog 的最佳方案](http://blog.csdn.net/lmj623565791/article/details/37936275)

- 使用Fragment创建对话框
  这是Google推荐的方式，具体的使用可以
  参考：[Android 官方推荐 : DialogFragment 创建对话框](http://blog.csdn.net/lmj623565791/article/details/37815413)

### 关于保存数据相关(fragment也支持)

在系统资源紧张的情况下，系统会杀死非栈顶的activity来释放资源，为了让这些被杀死的activity能够恢复状态，Android提供了onSaveInstanceState方法来保存状态信息，onRestoreInstanceState来恢复状态，这里要注意的是正常的销毁(点击了back或者执行了finish)是不会调用onSaveInstanceState的，相对的只有activity被系统非正常杀死，恢复的时候才会调用onRestoreInstanceState。经常我们还是在onCreate方法里直接恢复状态的，onCreate方法里本身会有一个Bundle参数的。

需要注意的是，不能再onPause中做重量级的耗时操作，因为必须在onPause执行完成后新的Activity才能onResume显示出来.

## 参考

[Android Fragment 真正的完全解析（上）](http://blog.csdn.net/lmj623565791/article/details/37970961)
[Android Fragment 真正的完全解析（下）](http://blog.csdn.net/lmj623565791/article/details/37992017)