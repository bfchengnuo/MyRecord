---
title: Android图片加载和缓存库Glide
date: 2016-06-30 15:23:13
tags: [Android]
categories: Android
---
对于这个库，目前只是单纯的用来加载图片，最简单的用法，如此强大的库当然是有很多~~奇技淫巧~~，能实现很多~~丧心病狂~~的功能，这个我也想要去了解过，但作为刚刚入门的感觉能力还是不够，只能先放着了，挖坑。
<!-- more -->
关于图片加载的库我们开发中基本是必须的，现在市面上知名的图片加载库有UniversalImageLoader、Picasso、Fresco、Volley ImageLoader以及我们今天的主角Glide。它们各有千秋，不能评定谁一定比谁好，只能说哪一个更适合你。

## Glide简介
Glide 是 Google 一位员工的大作，他完全是基于 Picasso 的，沿袭了 Picasso 的简洁风格，但是在此做了大量优化与改进。

- Glide 默认的 Bitmap 格式是 RGB_565 格式，而 Picasso 默认的是 ARGB_8888 格式，这个内存开销要小一半。

- 在磁盘缓存方面，Picasso 只会缓存原始尺寸的图片，而 Glide 缓存的是多种规格，也就意味着 Glide 会根据你 ImageView 的大小来缓存相应大小的图片尺寸，比如你 ImageView 大小是200*200，原图是 400*400 ，而使用 Glide 就会缓存 200*200 规格的图，而 Picasso 只会缓存 400*400 规格的。这个改进就会导致 Glide 比 Picasso 加载的速度要快，毕竟少了每次裁剪重新渲染的过程。

- 最重要的一个特性是 Glide 支持加载 Gif 动态图，而 Picasso 不支持该特性。

- 除此之外，还有很多其他配置选项的增加。

总体来说，Glide 是在 Picasso 基础之上进行的二次开发，各个方面做了不少改进，不过这也导致他的包比 Picasso 大不少，不过也就不到 500k，Picasso 是100多k，方法数也比 Picasso 多不少，不过毕竟级别还是蛮小的，影响不是很大。
同时也是Google推荐的图片加载库，专注于流畅的滚动。
## 使用
先贴下github的地址[Glide](https://github.com/bumptech/glide)
### 添加依赖
我用的是AS，打开Android Studio，在builde.gradle里面添加上以下内容：
``` java
repositories {
  mavenCentral() // jcenter() works as well because it pulls from Maven Central
}

dependencies {
  compile 'com.github.bumptech.glide:glide:3.7.0'
    compile 'com.android.support:support-v4:19.1.0'
}
```
### 使用Glide
先来看看官方给的实例代码：
``` java
// For a simple view:
@Override public void onCreate(Bundle savedInstanceState) {
  ...
    ImageView imageView = (ImageView) findViewById(R.id.my_image_view);
  //简单使用的话，下面这一句足矣
  Glide.with(this).load("http://goo.gl/gEgYUd").into(imageView);
}

// For a simple image list:
@Override public View getView(int position, View recycled, ViewGroup container) {
  final ImageView myImageView;
  if (recycled == null) {
    myImageView = (ImageView) inflater.inflate(R.layout.my_image_view, container, false);
  } else {
    myImageView = (ImageView) recycled;
  }

  String url = myUrls.get(position);

  Glide
    .with(myFragment)
    .load(url)
    .centerCrop()
    .placeholder(R.drawable.loading_spinner) //设置图片占位符
    .crossFade() // 动画
    .into(myImageView);

  return myImageView;
}
```
然后是一个我们习惯调用的例子：

```java
Glide.with(context)
  .load(url)// 加载图片资源
  // .skipMemoryCache(false)//是否将图片放到内存中
  // .diskCacheStrategy(DiskCacheStrategy.ALL)//磁盘图片缓存策略
  // .dontAnimate()//不执行淡入淡出动画
  .crossFade(100)// 默认淡入淡出动画300ms
  // .override(300,300)//图片大小
  .placeholder(R.drawable.shouye_haibao)// 占位图片
  // .error(R.drawable.shouye_haibao)//图片加载错误显示
  .centerCrop()//  fitCenter()
  // .animate()// 执行的动画
  // .bitmapTransform(null)// bitmap操作
  // .priority(Priority.HIGH)// 当前线程的优先级
  // .signature(new StringSignature("ssss"))
  .into(iv);
```

我们还经常会用到缩略图加载 - 有时我们希望减少用户等待的时间又不想牺牲图片的质量，我们可以同时加载多张图片到一个View中，先加载缩略图（只有view的1/10大小），然后再加载一个完整的图像覆盖在上面。使用下面的代码

```java
Glide.with(yourFragment).load(yourUrl).thumbnail(0.1f).into(yourView)
```

### 方法解释

.load()
加载资源：1,drawable资源。2，本地File文件。3，uri。4，网络图片url 
.placeholder() 
设置图片占位符
.error() 
图片加载失败时显示
.crossFade()
显示图片时执行淡入淡出的动画默认300ms 
.dontAnimate() 
不执行显示图片时的动画 
.override() 
设置图片的大小 
.centerCrop()
会按原始比例缩小图像，使宽或者高的一边等于给定的值，另外一边会等于或者大于给定值。裁剪掉多余部分。和Android中的ScaleType.CENTER_CROP效果相同。
.fitCenter() 
按原始比例缩小图像，使图像可以在放在给定的区域内。会尽可能少地缩小图片，使宽或者高的一边等于给定的值。另外一边会等于或者小于给定值。 和Android中的ScaleType.FIT_CENTER效果相同。 
.animate() 
相当于view动画 2个重构方法 
.transform()
bitmap转换
.bitmapTransform() 
bitmap转换。 比如:旋转，方法，缩小，高斯模糊等等（**当用了转换后你就不能使用 .centerCrop() 或 .fitCenter() 了。**） 
.priority(Priority.HIGH)
// 当前线程的优先级  Priority.LOW后加载
.signature(new StringSignature(“ssss”)) 
.thumbnail(0.1f) 
缩略图，3个重构方法：优先显示原始图片的百分比(10%) 
.listener() 
异常监听 
.into(); 
将图片显示到控件，有3个构造方法。

这里特别解释下with这个方法：

1. with(Context context). 使用**Application**上下文，Glide请求将不受Activity/Fragment生命周期控制。
2. with(Activity activity).使用**Activity**作为上下文，Glide的请求会受到Activity生命周期控制。
3. with(FragmentActivity activity).Glide的请求会受到FragmentActivity生命周期控制。
4. with(android.app.Fragment fragment).Glide的请求会受到Fragment 生命周期控制。
5. with(android.support.v4.app.Fragment fragment).Glide的请求会受到Fragment生命周期控制。

总的来说就是建议传入Activity/fragment，同步生命周期。

**更详细的方法解释可以看**[这里](http://blog.csdn.net/shangmingchao/article/details/51125554)
## 你应该知道的一些东西
1. Glide.with(context).resumeRequests()和 Glide.with(context).pauseRequests()
   当列表在滑动的时候，调用pauseRequests()取消请求，滑动停止时，调用resumeRequests()恢复请求。这样是不是会好些呢？
2. Glide.clear()
   当你想清除掉所有的图片加载请求时，这个方法可以帮助到你。
3. ListPreloader
   如果你想让列表预加载的话，不妨试一下ListPreloader这个类。
4. with()方法
   相比Picasso，Glide的with方法不光接受Context，还接受Activity 和 Fragment，Context会自动的从他们获取。
   将Activity/Fragment作为with()参数的好处是：图片加载会和Activity/Fragment的生命周期保持一致，比如 Paused状态在暂停加载，在Resumed的时候又自动重新加载。所以我建议传参的时候传递Activity 和 Fragment给Glide，而不是Context。
5. 加载动态图

   ``` java
   // 你想加载Gif为一张静态图片
   Glide.with(context).load(...).asBitmap()
   // 或者你想只有加载对象是Gif时才能加载成功
   Glide.with(context).load(...).asGif()
   ```
6. 一些关于缓存的设置
   通过GlideBuilder配置全局配置文件
   Glide默认是用InternalCacheDiskCacheFactory类来创建硬盘缓存的，这个类会在应用的内部缓存目录下面创建一个最大容量250MB的缓存文件夹，使用这个缓存目录而不用sd卡，意味着除了本应用之外，其他应用是不能访问缓存的图片文件的。
   **详细设置可移步**[这里(注意第六条)](http://blog.csdn.net/fandong12388/article/details/46372255)


**更多的技巧可以看**[这里](http://blog.csdn.net/shangmingchao/article/details/51125554)

## 关于其他图片加载库

### Universal Image Loader

UIL可以算是老牌最火的图片加载库了，使用过这个开源库的项目可以说是多的令人发指，即使到现在 GitHub 上他的 Star 数仍然是众多图片加载库最多的。
可惜的是该作者在项目中说明，从去年的9月份，他就已经停止了对该项目的维护。这就意味着以后任何的 bug 都不会修复，任何的新特性都不会再继续开发，所以毫无疑问 UIL 不推荐在项目中使用了。

### Picasso

Square出品，必属精品。他的调用也是如此简洁文艺：
`Picasso.with(context).load("http://i.imgur.com/DvpvklR.png").into(imageView)`
另：和OKHttp搭配起来更配呦！

### Fresco

Facebook出的，天生骄傲！不是一般的强大。
它是新一代的图片加载库，我们知道 Android 应用程序可用的内存有限，经常会因为图片加载导致 OOM，虽然我们有各种手段去优化，尽量减少出现 OOM 的可能性，但是永远没法避免，尤其某些低端手机 OOM 更是严重。而 Facebook 就另辟蹊径，既然没法在 Java 层处理，我们就在更底层的 Native 堆做手脚。于是 Fresco 将图片放到一个特别的内存区域叫 Ashmem 区，就是属于 Native 堆，图片将不再占用 App 的内存，Java 层对此无能为力，这里是属于 C++ 的地盘，所以能大大的减少 OOM。
所以此库很强大，不过用起来也比较复杂，包也比较大，貌似有2、3M，底层涉及到的 C++ 领域，想读源码也比较困难。

## END

这个库目前只是采用了最简单的方法，刚刚入门也没做几个APP，等以后用到了再详细研究，回来补充。

## 参考
[中文wiki翻译--Blog](http://limuzhi.com/2016/01/24/Android%E5%9B%BE%E7%89%87%E5%BA%93-Glide/)
[Android图片库-Glide3.X Wiki官方文档](https://muzhi1991.gitbooks.io/android-glide-wiki/content/index.html)
[系列教程](http://mrfu.me/2016/02/27/Glide_Getting_Started/)
[stormzhang](http://mp.weixin.qq.com/s?__biz=MzA4NTQwNDcyMA==&mid=2650661949&idx=1&sn=09aececd879bd8b4635e6a63a8249808&scene=4#wechat_redirect)
[Google推荐的图片加载库Glide介绍](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0327/2650.html)