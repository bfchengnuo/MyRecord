---
title: Android中对话框的使用
date: 2017-03-08 16:02:43
tags: Android
categories: Android
---

Android使用对话框就是使用AlertDialog这个对象，除了基本的文字提示，其实提示框可以展示的东西很丰富的，并且使用起来是很容易的，但是如果涉及到对话框的美好就得好好搞一番了
通过主题好像可以改变其样式，类似的Activity也是，话说主题其实有很大作用的
以及Google推荐的DialogFragment方式创建对话框也很帅
<!-- more -->
然而现在我依然不会用.....通过编写XML文件来实现
对话框的使用是很简单的，主要是....太长时间不动Android忘了....

## 基本语法

对话框是通过`AlertDialog.Builder`来获得的，曾经的new方法已经过时了

```java
AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(this);
AlertDialog alertDialog = alertDialogBuilder.create();
alertDialog.show();//将dialog显示出来
```

使用Builder对象能进行各种自定义，填充内容，它的一些常用方法：

>   setTitle();  //设置标题
>   setIcon();  //设置图标
>
>   设置下方按钮，分别为确定、取消、中性
>   setPositiveButton();
>   setNegativeButton();
>   setNeutralButton();
>
>   setMessage();  //设置显示文本
>   setItems();  //设置对话框内容为简单列表项
>   setSingleChoiceItems();  //设置对话框内容为单选列表项
>   setMultiChoiceItems();  //设置对话框内容为多选列表项
>   setAdapter();  //设置对话框内容为自定义列表项
>   setView();  //设置对话框内容为自定义View
>
>   //设置对话框是否可取消
>   setCancelable(booleab cancelable);
>   // 设置取消的监听事件
>   setCancelListener(onCancelListener);

## 基本的对话框

通过上面的方法应该也会用了，直接给一段代码吧
我还是要说下，上面的方法返回的还是Builder对象，所以是可以连用的

```java
AlertDialog.Builder builder = new AlertDialog.Builder(this);
builder.setTitle("这是标题");
builder.setIcon(R.mipmap.ic_launcher);
builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialog, int which) {
        Toast.makeText(DialogActivity.this, "23333333", Toast.LENGTH_SHORT).show();
    }
});
builder.setNegativeButton("取消",null);
builder.setNeutralButton("啊咧",null);
builder.setMessage("文本信息啦啦啦");

//设置对话框是否可取消,默认就是true
builder.setCancelable(true);
builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialog) {
        Toast.makeText(DialogActivity.this, "被取消？", Toast.LENGTH_SHORT).show();
    }
});

AlertDialog alertDialog = builder.create();
alertDialog.show();
```

## 单选列表对话框

上面的如果看懂了其他的也问题不大，大部分都是重复代码呢，只是多了一个方法**setSingleChoiceItems**而已，所以重复代码就不贴了，只贴新加的变化的部分

```java
// 算是数据源吧
final String[] Items={"Items_one","Items_two","Items_three"};
// 第二个参数是已经选中的编号
builder.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        // which是选择的拿一个
        Toast.makeText(getApplicationContext(), "You clicked "+items[which], Toast.LENGTH_SHORT).show();
    }
});
```

## 多选对话框

使用的就是**setMultiChoiceItems**这个方法，同样接受三个参数，第一个是数组，也就是数据源，第二个是选择的条目，可以传null，第三个就是监听事件了

```java
final String[] items={"Items_one","Items_two","Items_three"};
builder.setMultiChoiceItems(items, new boolean[]{true, false, true}, new DialogInterface.OnMultiChoiceClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int which, boolean isChecked) {
        Toast.makeText(getApplicationContext(),"You clicked "+items[which]+" "+isChecked,Toast.LENGTH_SHORT).show();
    }
});
```

## 列表对话框

这个....真没啥好说的了，都懂~~

```java
final String[] Items={"Items_one","Items_two","Items_three"};
builder.setItems(Items, new DialogInterface.OnClickListener() {
    @Override
    public void onClick(DialogInterface dialogInterface, int which) {
        Toast.makeText(getApplicationContext(), "You clicked "+Items[which], Toast.LENGTH_SHORT).show();
    }
});
```

## 自定义对话框

嗯，这个才是重点吧，可以使用**setView**方法来设置自己定义的XML文件，通过inflate来获取我们的自定的view
`LinearLayout loginDialog= (LinearLayout) getLayoutInflater().inflate(R.layout.custom_view,null);`
然后设置到对话框中：
`builder.setView(loginDialog);`
需要的话我会在这设置监听事件，不知道这样写合不合理
如果使用的是复杂一点的布局比如有ListView，还可以用**setAdapter**方法来设置其的适配器

待补充...

## ProgressDialog

ProgressDialog  顾名思义就是进度对话框，一般有两种，圆形进度条和条形进度条，它继承自 AlertDialog，AlertDialog 继承自Dialog, 实现 DialogInterface 接口。
ProgressDialog 的创建方式有两种，一种是 `new Dialog` ,一种是调用 Dialog 的静态方法 `Dialog.show()`。

```java
final ProgressDialog dialog = new ProgressDialog(this); 
dialog.show();

// 第四个参数为：是否是不明确的状态（圆形）
// 第五个参数为：是否可以取消
// 第六个参数为：被取消后的回调
ProgressDialog dialog = ProgressDialog.show(this, "提示", "正在登陆中", true, 
true, cancelListener);
```

### 圆形进度条

也就是不确定状态下

```java
final ProgressDialog dialog = new ProgressDialog(this);
dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
dialog.setIcon(R.drawable.ic_launcher);// 如果没有设置title的话只设置Icon是不会显示图标的
dialog.setTitle("提示");
// dismiss 监听
dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
    @Override
    public void onDismiss(DialogInterface dialog) {
        // TODO Auto-generated method stub
    }
});
// 监听cancel 事件
dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
    @Override
    public void onCancel(DialogInterface dialog) {
        // TODO Auto-generated method stub
    }
});
//设置可点击的按钮，最多有三个(默认情况下)
dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
dialog.setButton(DialogInterface.BUTTON_NEUTRAL, "中立",
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
dialog.setMessage("这是一个圆形进度条");
dialog.show();
```

cancel 和 dismiss 方法本质都是一样的，都是从屏幕中删除 Dialog, 唯一的区别是：
调用 cancel 方法会回调 `DialogInterface.OnCancelListener` 如果注册的话， dismiss 方法不会回掉

### 水平进度条

也就是说它是可以显示进度的

```java
// 和上面一样的属性就不再写了
final ProgressDialog dialog = new ProgressDialog(this);
dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
dialog.setTitle("提示");
dialog.setMax(100);
dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
        new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
dialog.setMessage("这是一个水平进度条");
dialog.show();

// 更新进度条的方法和上面的基本对话框一样，比如
dialog.incrementProgressBy(1);
```

参见：http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2014/0703/1628.html

## 拓展-DialogFragment

DialogFragment在android 3.0时被引入。是一种特殊的Fragment，用于**在Activity的内容之上展示一个模态的对话框**。典型的用于：展示警告框，输入框，确认框等等。
**Google推荐使用DialogFragment 创建对话框的**，既然推荐就肯定有什么优点啦

>   使用DialogFragment来管理对话框，当旋转屏幕和按下后退键时可以更好的管理其声明周期，它和Fragment有着基本**一致的生命周期**。且DialogFragment也允许开发者把Dialog作为内嵌的组件进行重用，类似Fragment（可以在大屏幕和小屏幕显示出不同的效果）。

使用DialogFragment至少需要实现**onCreateView或者onCreateDIalog**方法。
**onCreateView**即使用定义的xml布局文件展示Dialog。
**onCreateDialog**即利用AlertDialog或者Dialog创建出Dialog。

### onCreateView方式创建

因为是使用的自定义的XML来展示布局的，所有首先要自己创建XML文件来设计对话框的布局，这里关于布局的代码就不贴了，简单的测试用布局
然后就是创建一个类，继承DialogFragment来关联布局文件，毫无疑问DialogFragment是继承自Fragment的，所以特性也就都有啦

```java
public class EditNameDialogFragment extends DialogFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // 去掉默认的标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_edit_name, container);
        return view;
    }
}
```

然后你就可以在Activity中进行调用了

```java
public void showEditDialog(View view)
{
    EditNameDialogFragment editNameDialog = new EditNameDialogFragment();
    editNameDialog.show(getFragmentManager(), "EditNameDialog");
}
```

这里说名一下show方法的第二个参数吧，第一个没什么好说的，第二个是一个tag（String）通过这个tag可以告诉fragment是谁启动了它，当然这仅仅是这个tag的一种使用方式啦。在fragment中可以通过`getTag()`方法来获取这个tag

>   如果你的DialogFragment是Activity的内部类，必须将DialogFragment定义为静态的。否则会报错！！！

### onCreateDialog方式创建

在onCreateDialog中一般可以使用AlertDialog或者Dialog创建对话框，不过既然google不推荐直接使用Dialog，所以还是使用AlertDialog
其他的和第一种一样，建布局、继承DialogFragment然后在Activity中使用，就是重写的方法不一样而已

```java
public class LoginDialogFragment extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // 使用的完全就是上面常规创建对话框的形式
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_login_dialog, null);
        builder.setView(view)
                .setPositiveButton("Sign in",null)
          		.setNegativeButton("Cancel", null);  
        return builder.create();  
    }  
} 
```

### 传递数据

如果DialogFragment要传递数据给Activity，比较好的方法就是使用回调，在DialogFragment定义一个接口，然后Activity去实现，然后比如当点击对话框按钮的时候可以调用接口的方法以此来回调Activity，只需要这样写就可以了`interface xxx = getActivity()`

更详细的解释可以去<u>https://www.cnblogs.com/tianzhijiexian/p/4161811.html</u>

## 设置对话框透明

我尝试过修改style中的windowBackground属性，但是并不会透明，这个属性会使对话框外层的窗体透明，如果换成老版本的主题就会出现对话框外层又一个窗口，很难看，这样使用windowBackground可以把它去掉，但是对话框本身不会变透明
找到的方法是代码实现，在show的前面加上几句代码：

```java
Window window = alertDialog.getWindow();
WindowManager.LayoutParams lp = window.getAttributes();
lp.alpha = 0.6f;
window.setAttributes(lp);

alertDialog.show();
```

## 参考

http://blog.csdn.net/lmj623565791/article/details/37815413