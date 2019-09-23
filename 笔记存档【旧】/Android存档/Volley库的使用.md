---
title: Volley库的使用
date: 2016-07-12 21:19:12
tags: [Android,Volley]
categories: Android
---

## 简介

Volley是一个简化网络任务的库。他负责处理请求，加载，缓存，线程，同步等问题。它可以处理JSON，图片，缓存，文本源，支持一定程度的自定义。
Volley可以轻松设置OkHttp作为其传输层。
Volley是谷歌开发的！！！
**Vollry特别适合数据量不大但是通信频繁的场景。**
<!-- more -->
它能干什么？
1. 使网络通信更快，更简单，更健壮；
2. Get/Post网络请求及网络图像的高效率异步请求；　　
3. 可以对网络请求的优先级进行排序处理；
4. 可以进行网络请求的缓存；
5. 可以取消多级别请求；
6. 可以和Activity生命周期联动。

## 内部架构
![](/image/dev/volley.jpg)
Volley分为三层，每一层都工作在自己的线程中。

**主线程**

在主线程中，你只允许触发请求与处理返回结果，不能多也不能少。
其结果就是你实际上可以忽略在使用AsyncTask的时候doInBackground 方法里面所做的事情。Volley 自动管理http传输同时捕获网络错误，这些都是以前需要我们自己考虑的。

**缓存与网络线程**

当你向队列中添加了一个请求，背后发生了几件事情。Volley会检查这个请求是否可以从缓存中得到。如果可以，缓存将负责读取，解析，和分发。否则将传递给网络线程。
在网络线程中，一些列的轮询线程不断的在工作。第一个可用的网络线程线程让请求队列出列，发出http请求，解析返回的结果，并写入到缓存中。最后，把解析的结果分发给主线程的listener中。

## 使用volley--普通数据请求

导包就不用说了，这里主要写下调用的过程。
在介绍volley之前先看看以前我们是怎样手动封装网络的。
``` java
public class HttpDemo extends AppCompatActivity {

  private static final int S_RESPONSE = 0;
  private TextView mTextView;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case S_RESPONSE:
          String response = (String) msg.obj;
          mTextView.setText(response);
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_http_demo);
    mTextView = (TextView) findViewById(R.id.text);
  }


  private void httpConn(){
    new Thread(new Runnable() {
      @Override
      public void run() {
        HttpURLConnection connection = null;
        try {
          URL url = new URL("http://www.baidu.com");
          connection = (HttpURLConnection) url.openConnection();
          connection.setRequestMethod("GET"); //设置请求方式
          connection.setReadTimeout(8000);  //设置读取超时
          connection.setConnectTimeout(8000); //设置链接超时
          InputStream is = connection.getInputStream();
          //对获取到的流进行读取
          BufferedReader bufr = new BufferedReader(new InputStreamReader(is));
          StringBuilder sb = new StringBuilder(); //用来接收响应数据
          String line;
          while ((line = bufr.readLine()) != null){
            sb.append(line);
          }
          //通过Message将数据发送出去
          Message message = new Message();
          message.what = S_RESPONSE;
          message.obj = sb.toString();
          mHandler.sendMessage(message); //将消息发送出去 用于更新UI
        } catch (IOException e) {
          e.printStackTrace();
        }finally {
          //关闭链接
          if (connection != null)
            connection.disconnect();
        }
      }
    }).start();
  }
}
```
### 正文部分
{% note info %}
通常Volley只会用到两个类 **RequestQueue 和 Request**，你首先创建一个 RequestQueue，RequestQueue 管理工作线程并将解析的结果发送给主线程。**然后你传递一个或者多个Request 对象给他。**
{% endnote %}

Request 的构造函数的参数总是包含类型(GET, POST, 等等)，数据源的url，以及事件监听者。根据请求类型的不同，可能还需要一些其他的参数。
#### 请求队列的设置
首先我们要获取到一个请求队列，最好是全局的，这样我们就可以将一个请求加入到这个全局队列中，并可以管理整个APP的所有请求，包括取消一个或所有的请求。
**不设置全局的话，至少一个 activity 要有一个总的请求队列**，便于管理和增加效率。
``` java
public class MyApplication extends Application{
  private static RequestQueue queues ;
  @Override
  public void onCreate() {
    super.onCreate();
    queues = Volley.newRequestQueue(getApplicationContext());
  }

  public static RequestQueue getHttpQueues() {
    return queues;
  }
}
```
不要忘记在 AndroidManifest 中增加网络权限和设置自定义的 Application 哦。
#### 返回类型与请求方式的使用
Volley的Get和Post请求方式其实是对Android原生Get和Post请求方式进行了二次封装，对效率等进行优化。在使用Get和Post请求方式之前，我们要确定所请求的数据返回什么对象，Volley自带了三种返回类型：

- StringRequest：主要使用在对请求数据的返回类型不确定的情况下，StringRequest涵盖了JsonObjectRequest和JsonArrayRequest。

- JsonObjectRequest：当确定请求数据的返回类型为JsonObject时使用。

- JsonArrayRequest：当确定请求数据的返回类型为JsonArray时使用。

下面看一个使用GET方式查询手机归属地的例子，再次强调，request 必须 add 进 RequestQueue 才会执行，如果没有设置全局的 RequestQueue 需要手动创建一下：
``` java
/**
 *  new StringRequest(int method,String url,Listener listener,ErrorListener errorListener)
 *  method：请求方式，Get请求为Method.GET，Post请求为Method.POST
 *  url：请求地址
 *  listener：请求成功后的回调
 *  errorListener：请求失败的回调
 */
private void volleyGet() {
    String url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=15850781443";
    StringRequest request = new StringRequest(Method.GET, url,
            new Listener<String>() {
                @Override
                public void onResponse(String s) {//s为请求返回的字符串数据
                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                }
            },
            new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
                }
            });
    //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
    request.setTag("testGet");
    //将请求加入全局队列中
    MyApplication.getHttpQueues().add(request);
}
```
然后再看下返回值为json的：
``` java
/**
 *  new JsonObjectRequest(int method,String url,JsonObject jsonObject,Listener listener,ErrorListener errorListener)
 *  method：请求方式，Get请求为Method.GET，Post请求为Method.POST
 *  url：请求地址
 *  JsonObject：Json格式的请求参数。如果使用的是Get请求方式，请求参数已经包含在url中，所以可以将此参数置为null
 *  listener：请求成功后的回调
 *  errorListener：请求失败的回调
 */
private void volleyGet() {
    String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=218.4.255.255";
    JsonObjectRequest request = new JsonObjectRequest(Method.GET, url, null,
            new Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {//jsonObject为请求返回的Json格式数据
                    Toast.makeText(MainActivity.this,jsonObject.toString(),Toast.LENGTH_LONG).show();
                }
            },
            new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
                }
            });

    //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
    request.setTag("testGet");
    //将请求加入全局队列中
    MyApplication.getHttpQueues().add(request);
}
```
下面我们再来看使用Post方式请求数据返回StringRequest对象

使用Post方式需要手动传递请求参数，可以重写Request类的getParams()方法将请求参数名和参数值放入Map中进行传递。
``` java
/**
 * 使用Post方式返回String类型的请求结果数据
 * 
 *  new StringRequest(int method,String url,Listener listener,ErrorListener errorListener)
 *  method：请求方式，Get请求为Method.GET，Post请求为Method.POST
 *  url：请求地址
 *  listener：请求成功后的回调
 *  errorListener：请求失败的回调
 */
private void volleyPost() {
    String url = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm";
    StringRequest request = new StringRequest(Method.POST, url,
            new Listener<String>() {
                @Override
                public void onResponse(String s) {//s为请求返回的字符串数据
                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();
                }
            },
            new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
                }
            }){
            	//重写方法，设置请求参数
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> map = new HashMap<>();
                    //将请求参数名与参数值放入map中
                    map.put("tel","15850781443");
                    return map;
                }
            };
    //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
    request.setTag("testPost");
    //将请求加入全局队列中
    MyApplication.getHttpQueues().add(request);
}
```
以及用POST的方式请求json数据：
``` java
/**
 *  使用Post方式返回JsonObject类型的请求结果数据
 *
 *  new JsonObjectRequest(int method,String url,JsonObject jsonObject,Listener listener,ErrorListener errorListener)
 *  method：请求方式，Get请求为Method.GET，Post请求为Method.POST
 *  url：请求地址
 *  JsonObject：Json格式的请求参数。如果使用的是Get请求方式，请求参数已经包含在url中，所以可以将此参数置为null
 *  listener：请求成功后的回调
 *  errorListener：请求失败的回调
 */
private void volleyPost() {
    String url = "http://www.kuaidi100.com/query";
    Map<String,String> map = new HashMap<>();
    map.put("type","yuantong");
    map.put("postid","229728279823");
    //将map转化为JSONObject对象
    JSONObject jsonObject = new JSONObject(map);

    JsonObjectRequest request = new JsonObjectRequest(Method.POST, url, jsonObject,
            new Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {//jsonObject为请求返回的Json格式数据
                    Toast.makeText(MainActivity.this,jsonObject.toString(),Toast.LENGTH_LONG).show();
                }
            },
            new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(MainActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
                }
            });
    //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
    request.setTag("testPost");
    //将请求加入全局队列中
    MyApplication.getHttpQueues().add(request);
}
```
#### Volley与Activity生命周期的联动

简单来说就是Volley中的请求是与Activity的生命周期进行关联。这样可以在Android销毁时关闭Volley的请求，防止请求在后台运行造成内存溢出等情况发生。
与Activity生命周期进行联动时需要设置Tag标签，因为取消请求需要在请求队列中通过Tag标签进行查找，在Activity的onStop中执行取消请求的操作。

可以在Activity关闭时取消请求队列中的请求。
``` java
@Override
protected void onStop() {
    super.onStop();
    //通过Tag标签取消请求队列中对应的全部请求
    MyApplication.getHttpQueues().cancelAll("abcGet");
}
```
如果没有设置标签，可以尝试下,清除所有的请求：
``` java
@Override
protected void onStop() {
    super.onStop();
    mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
        @Override
        public boolean apply(Request<?> request) {
            // do I have to cancel this?
            return true; // -> always yes
        }
    });
}
```

## 使用volley--图片加载
我们前面也说过了，关于图片加载很多就是用的volley，这符合它的特点。

有三种请求图片的方法。

- ImageRequest 是标准方法。通过提供的Url，她将你请求的图片显示在一个普通的ImageView中。压缩与大小调整的操作都**发生在工作线程中**。
- ImageLoader类。你可以将之想象成数量庞大的ImageRequests，比如生成一个带有图片的ListView。ImageLoader明显要比ImageRequest更加高效，因为它不仅可以帮我们对图片进行缓存，还可以过滤掉重复的链接，避免重复发送请求。
- NetworkImageView，它是一个自定义控制，它是继承自ImageView 的，具备ImageView控件的所有功能，并且在原生的基础之上加入了加载网络图片的功能。NetworkImageView 控件的用法要比前两种方式更加简单。

### 使用ImageRequest加载图片
``` java
/**
 *  通过Volley加载网络图片
 *
 *  new ImageRequest(String url,Listener listener,int maxWidth,int maxHeight,Config decodeConfig,ErrorListener errorListener)
 *  url：请求地址
 *  listener：请求成功后的回调
 *  maxWidth、maxHeight：设置图片的最大宽高，如果均设为0则表示按原尺寸显示
 *  decodeConfig：图片像素的储存方式。Config.RGB_565表示每个像素占2个字节，Config.ARGB_8888表示每个像素占4个字节(建议使用)等。
 *  errorListener：请求失败的回调
 */
private void loadImageByVolley() {
    String url = "http://pic20.nipic.com/20120409/9188247_091601398179_2.jpg";
    ImageRequest request = new ImageRequest(
                        url,
                        new Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                image.setImageBitmap(bitmap);
                            }
                        },
                        0, 0, Config.RGB_565,
                        new ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                image.setImageResource(R.mipmap.ic_launcher);
                            }
                        });

    //设置请求的Tag标签，可以在全局请求队列中通过Tag标签进行请求的查找
    request.setTag("loadImage");
    //通过Tag标签取消请求队列中对应的全部请求
    MyApplication.getHttpQueues().add(request);
}
```
注意 Volley 默认会将这种请求的优先级设置为low。

```java
// Snippet taken from ImageRequest.java, 
// in the Volley source code

@Override
public Priority getPriority() {
    return Priority.LOW;
}
```

另外图片请求总是GET的。
### 使用ImageLoader加载图片
``` java
/**
 *  通过ImageLoader加载及缓存网络图片
 *  new ImageLoader(RequestQueue queue,ImageCache imageCache)
 *  queue：请求队列
 *  imageCache：一个用于图片缓存的接口，一般需要传入它的实现类
 *
 *  getImageListener(ImageView view, int defaultImageResId, int errorImageResId)
 *  view：ImageView对象
 *  defaultImageResId：默认的图片的资源Id
 *  errorImageResId：网络图片加载失败时显示的图片的资源Id
 */
private void loadImageWithCache() {
    String url = "http://pic20.nipic.com/20120409/9188247_091601398179_2.jpg";
    ImageLoader loader = new ImageLoader(MyApplication.getHttpQueues(), new BitmapCache());
    ImageListener listener = loader.getImageListener(image,R.mipmap.ic_launcher,R.mipmap.ic_launcher);
    //加载及缓存网络图片
    loader.get(url,listener);
}
```
需要补充一点，get()方法接收两个参数，第一个参数就是图片的URL地址，第二个参数则是刚刚获取到的ImageListener对象。当然，如果你想对图片的大小进行限制，也可以使用get()方法的重载，指定图片允许的最大宽度和高度，如：
``` java
imageLoader.get("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
            listener, 200, 200);
```

这里创建的ImageCache对象是一个空的实现，完全没能起到图片缓存的作用。其实写一个ImageCache也非常简单，但是如果想要写一个性能非常好的ImageCache，最好就要借助Android提供的LruCache功能了，如果对LruCache还不了解，可以参考郭神的博客[Android高效加载大图、多图解决方案，有效避免程序OOM](http://blog.csdn.net/guolin_blog/article/details/9316683)
``` java
public class BitmapCache implements ImageCache {
    private LruCache<String, Bitmap> mCache;  
  
    public BitmapCache() {
		//这里我们将缓存图片的大小设置为10M	    
        int maxSize = 10 * 1024 * 1024;  
        mCache = new LruCache<String, Bitmap>(maxSize) {  
            @Override  
            protected int sizeOf(String key, Bitmap bitmap) {  
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }
  
    @Override  
    public Bitmap getBitmap(String url) {  
        return mCache.get(url);  
    }  
  
    @Override
    public void putBitmap(String url, Bitmap bitmap) {  
        mCache.put(url, bitmap);
    }
}
```
### 使用NetworkImageView来加载
在XML中定义：
``` html
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="fill_parent"  
    android:layout_height="fill_parent"  
    android:orientation="vertical" >  

    <Button  
        android:id="@+id/button"  
        android:layout_width="wrap_content"  
        android:layout_height="wrap_content"  
        android:text="Send Request" />  
      
    <com.android.volley.toolbox.NetworkImageView   
        android:id="@+id/network_image_view"  
        android:layout_width="200dp"  
        android:layout_height="200dp"  
        android:layout_gravity="center_horizontal"  
        />  

</LinearLayout>
```
接着在Activity获取到这个控件的实例,就是findviewbyId了

得到了NetworkImageView控件的实例之后，我们可以调用它的 setDefaultImageResId() 方法、setErrorImageResId() 方法和setImageUrl()方法来分别设置加载中显示的图片，加载失败时显示的图片，以及目标图片的URL地址。
``` java
networkImageView.setDefaultImageResId(R.drawable.default_image);
networkImageView.setErrorImageResId(R.drawable.failed_image);
networkImageView.setImageUrl("http://img.my.csdn.net/uploads/201404/13/1397393290_5765.jpeg",
                imageLoader);  
```
其中，**setImageUrl()** 方法接收两个参数，第一个参数用于指定图片的URL地址，第二个参数则是前面创建好的ImageLoader 对象。
## 参考
[郭神的博客](http://blog.csdn.net/guolin_blog/article/details/17482165)
[Android热门网络框架Volley详解](http://www.cnblogs.com/dongweiq/p/5082080.html)
[网络请求库Volley详解](http://www.jcodecraeer.com/a/anzhuokaifa/androidkaifa/2015/0526/2934.html)