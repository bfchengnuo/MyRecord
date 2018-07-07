---
title: AsyncTask线程异步加载
date: 2016-04-30 18:14:25
tags: [Android]
categories: Android
---

## 写在前面

- 启动子线程，既可以保证Android的单线程模型，又可以防止ANR。

- Android的UI单线程模型，所有的UI相关的操作都需要在主线程(UI线程) 

- 线程可以分为两种 MainThread(UI线程) 和 WorkerThread

**最常用的方式：**
1. 多线程\线程池
2. AsyncTask
   <!-- more -->

## 实现方法

- AsyncTask是一个抽象类，一般是继承后实现未实现的方法

**泛型的参数：**
- Params:启动任务时输入参数的类型
- Progress:后台任务执行中返回进度值的类型
- Result:后台执行任务完成后返回结果的类型

**主线程中调用new MyAsyncTask(这里可写构造函数设置的参数).execute(这里写泛型定义的第一个参数)开启一个异步任务**

--------


``` java
//采用内部类的形式实现
class myAsyncTask extends AsyncTask<String, Void, Bitmap> {

  //耗时部分执行前运行，主线程中运行，可用来初始化需要的数据
  @Override
  protected void onPreExecute() {
    super.onPreExecute();
  }

  //耗时执行部分,只有此部分是运行在WorkerThread线程中
  //有关网络的一般都是耗时操作，因为网络状况不确定
  @Override
  protected Bitmap doInBackground(String... params) {
    String url = params[0];
    Bitmap bitmap = null;
    URLConnection urlConnection;
    InputStream ins;
    try {
      //获取网络连接对象
      urlConnection = new URL(url).openConnection();
      ins = urlConnection.getInputStream();
      BufferedInputStream bis = new BufferedInputStream(ins);
      //通过decodeStream解析输入流
      bitmap = BitmapFactory.decodeStream(bis);
      //关闭流
      ins.close();
      bis.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return bitmap;
  }

  //处理结束后执行，运行在主线程，可以操作UI
  @Override
  protected void onPostExecute(Bitmap bitmap) {
    super.onPostExecute(bitmap);
    //操作UI 设置图片
    mImageView.setImageBitmap(bitmap);
    mProgressBar.setVisibility(View.GONE); //隐藏进度条
  }
  //一般用来承接我们传出来的进度,操作UI，参数对应泛型的第二个
  @Override
  protected void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);
    if (isCancelled())
      return;
    progressBar.setProgress(values[0]);

  }
}
```
## 优化操作
**异步加载——LruCache缓存**
1. Lrc——Least Recently Used，LruCache类来实现缓存算法。LruCache类将内容保存在内存中，并以一定的方法管理这些内容，来实现缓存管理。

2. LrcCache本质是一个Map，底层是通过HashMap实现的。所以在使用LruCache时可以通过调用set()和get()方法使用。


--------


``` java
public ImageLoader() {
  //获取最大内存
  int maxMemory = (int) Runtime.getRuntime().maxMemory();
  int cacheSize = maxMemory / 4;
  mLruCache = new LruCache<String, Bitmap>(cacheSize) {
    @Override
    protected int sizeOf(String key, Bitmap value) {
      //每次存入缓存时调用，告诉系统当前存储的内容有多大
      return value.getByteCount();
    }
  };
}
//增加到缓存
private void addBitmapToCache(String url, Bitmap bitmap) {
  if (getBitmapFromURL(url) != null) {
    //缓存中存入数据
    mLruCache.put(url, bitmap);
  }
}

//获取缓存数据
private Bitmap getBitmapFromCache(String url) {
  //本身就是个map集合，url对应bitmap
  return mLruCache.get(url);
}
```

## 注意事项
- AsyncTask默认情况下会等待前一个线程执行完毕后再执行下一个线程，要取消该机制，可以让AsyncTask和Activity的生命周期保持一致

- AsyncTask.cancel()方法只是发送了一个取消请求，将AsyncTask标记为cancel状态，但未真正取消线程的执行

- isCancelled()用来判断线程的状态


--------


``` java
//在Activity失去焦点的时候调用的方法
@Override
protected void onPause() {
  super.onPause();
  //只是发送了一个取消请求，将AsyncTask标记为cancel状态，但未真正取消线程的执行
  //实际上JAVA语音没办法粗暴地直接停止一个正在运行的线程
  //标记，true就表示继续完成任务，通常都是设置为true
  if (mAsyncTask != null && mAsyncTask.getStatus() == AsyncTask.Status.RUNNING){
    mAsyncTask.cancel(true);
  }
}
```

## 补充其他

在.xml设置的onclick   可以在Java文件中直接设置点击事件不用FindViewById

``` xml
<Button
android:layout_width="match_parent"
android:layout_height="wrap_content"
android:onClick="show"/>
```
Java代码中直接这样写：
``` java
public void show(View view) {
  startActivity(new Intent(this, xxx.class));
}
```