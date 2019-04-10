---
title: Android HTTP请求方式:HttpURLConnection
date: 2016-4-22
tags: [Android]
categories: Android
---

## 引言：

本节学习的是Android为我们提供的Http请求方式之一：HttpURLConnection， 除了这种，还有一种还有一种~~HttpClient.~~不过前者一旦请求复杂起来，使用起来 非常麻烦，而后者我们Java抓包也经常会用到，是Apache的，毕竟不是谷歌亲儿子，而在4.4版本 HttpURLConnection已被替换成OkHttp了！对了，一般我们实际开发并不会用HttpURLConnection和HttpClient，使用别人封装 好的第三方网络请求框架，诸如：Volley，android-async-http，loopj等，因为网络操作涉及到 异步以及多线程，自己动手撸的话，很麻烦，所以实际开发还是直接用第三方！！当然学习下也 无妨，毕竟第三方也是在这些基础上撸起来的，架构逼格高，各种优化！好的，话不多说，开始 本节内容！
<!-- more -->
更新：2017-3-15
[**推荐原文**](http://www.runoob.com/w3cnote/android-tutorial-httpurlconnection.html)

## HttpURLConnection的介绍

答：一种多用途、轻量极的HTTP客户端，使用它来进行HTTP操作可以适用于大多数的应用程序。 虽然HttpURLConnection的API提供的比较简单，但是同时这也使得我们可以更加容易地去使 用和扩展它。继承至URLConnection，抽象类，无法直接实例化对象。通过调用openCollection() 方法获得对象实例，默认是带gzip压缩的；

## 使用步骤

使用HttpURLConnection的步骤如下：

- 创建一个URL对象： `URL url = new URL(http://www.baidu.com);`
- 调用URL对象的openConnection()来获取HttpURLConnection对象实例
   `HttpURLConnection conn = (HttpURLConnection) url.openConnection();`
- 设置HTTP请求使用的方法:GET或者POST
  ` conn.setRequestMethod("GET");`
- 设置连接超时，读取超时的毫秒数，以及服务器希望得到的一些消息头
  `conn.setConnectTimeout(6*1000); `和`conn.setReadTimeout(6 * 1000);`
- 调用getInputStream()方法获得服务器返回的输入流，然后输入流进行读取了
   `InputStream in = conn.getInputStream();`
- 最后调用disconnect()方法将HTTP连接关掉 `conn.disconnect();`

PS:除了上面这些外,有时我们还可能需要对响应码进行判断,比如200: 
`if(conn.getResponseCode() != 200)`
然后一些处理 还有，可能有时我们 并不需要传递什么参数，而是直接去访问一个页面，我们可以直接用： `final InputStream in = new URL("url").openStream();` 然后直接读流，不过这个方法适合于直接访问页面的情况，底层实现其实也是 `return openConnection().getInputStream()`，而且我们还不能设置一些 请求头的东东，所以要不要这样写，自己要考虑好！

## 使用示例

先搞一个工具类吧，主要是从字节流中读取数据，同时下面也写了个使用字符流读的方式拓展下，这个比较简单但是只能读文本信息啦
``` java
public class StreamTool {
  //从字节流中读取数据
  public static byte[] read(InputStream inStream) throws Exception{
    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    int len = 0;
    while((len = inStream.read(buffer)) != -1)
    {
      outStream.write(buffer,0,len);
    }
    inStream.close();
    return outStream.toByteArray();
  }

  //转换为字符流进行读取
  public static byte[] read2(InputStream inStream) throws Exception{
    // 使用bufr自带的缓存区读取字符流，将获取到的字节流转换为字符流才能处理
    BufferedReader read = new BufferedReader(new InputStreamReader(inStream));
    final StringBuilder sb = new StringBuilder();
    String str;
    while ((str = read.readLine()) != null){
      sb.append(str);
    }
  }
}
```

再下面是一个Android获取图片的栗子，至于效果，看原文吧，这里没贴
```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
android:layout_width="match_parent"
android:layout_height="match_parent"
android:orientation="vertical">

<TextView
    android:id="@+id/txtMenu"
    android:layout_width="match_parent"
    android:layout_height="48dp"
    android:background="#4EA9E9"
    android:clickable="true"
    android:gravity="center"
    android:text="长按我，加载菜单"
    android:textSize="20sp" />

<ImageView
    android:id="@+id/imgPic"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:visibility="gone" />

<ScrollView
    android:id="@+id/scroll"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:visibility="gone">

    <TextView
        android:id="@+id/txtshow"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
</ScrollView>

<WebView
    android:id="@+id/webView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
</LinearLayout>
```
下面是获取数据类，使用的都是字节流，和java的写法一样，当然获取html的时候也是可以使用字符流的

``` java
public class GetData {
  // 定义一个获取网络图片数据的方法:
  public static byte[] getImage(String path) throws Exception {
    URL url = new URL(path);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    // 设置连接超时为5秒
    conn.setConnectTimeout(5000);
    // 设置请求类型为Get类型
    conn.setRequestMethod("GET");
    // 判断请求Url是否成功
    if (conn.getResponseCode() != 200) {
      throw new RuntimeException("请求url失败");
    }
    InputStream inStream = conn.getInputStream();
    byte[] bt = StreamTool.read(inStream);
    inStream.close();
    return bt;
  }

  // 获取网页的html源代码
  public static String getHtml(String path) throws Exception {
    URL url = new URL(path);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setConnectTimeout(5000);
    conn.setRequestMethod("GET");
    if (conn.getResponseCode() == 200) {
      InputStream in = conn.getInputStream();
      byte[] data = StreamTool.read(in);
      String html = new String(data, "UTF-8");
      return html;
    }
    return null;
  }
}
```

Android的主活动Activity，很简单，就是多线程下载网页或图片，然后利用handler更新UI

``` java
public class MainActivity extends AppCompatActivity {

  private TextView txtMenu, txtshow;
  private ImageView imgPic;
  private WebView webView;
  private ScrollView scroll;
  private Bitmap bitmap;
  private String detail = "";
  private boolean flag = false;
  private final static String PIC_URL = "http://ww2.sinaimg.cn/large/7a8aed7bgw1evshgr5z3oj20hs0qo0vq.jpg";
  private final static String HTML_URL = "http://www.baidu.com";

  // 用于刷新界面
  private Handler handler = new Handler() {
    public void handleMessage(android.os.Message msg) {
      switch (msg.what) {
        case 0x001:
          hideAllWidget();
          imgPic.setVisibility(View.VISIBLE);
          imgPic.setImageBitmap(bitmap);
          Toast.makeText(MainActivity.this, "图片加载完毕", Toast.LENGTH_SHORT).show();
          break;
        case 0x002:
          hideAllWidget();
          scroll.setVisibility(View.VISIBLE);
          txtshow.setText(detail);
          Toast.makeText(MainActivity.this, "HTML代码加载完毕", Toast.LENGTH_SHORT).show();
          break;
        case 0x003:
          hideAllWidget();
          webView.setVisibility(View.VISIBLE);
          webView.loadDataWithBaseURL("", detail, "text/html", "UTF-8", "");
          Toast.makeText(MainActivity.this, "网页加载完毕", Toast.LENGTH_SHORT).show();
          break;
        default:
          break;
      }
    }

    ;
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setViews();
  }

  private void setViews() {
    txtMenu = (TextView) findViewById(R.id.txtMenu);
    txtshow = (TextView) findViewById(R.id.txtshow);
    imgPic = (ImageView) findViewById(R.id.imgPic);
    webView = (WebView) findViewById(R.id.webView);
    scroll = (ScrollView) findViewById(R.id.scroll);
    registerForContextMenu(txtMenu);
  }

  // 定义一个隐藏所有控件的方法:
  private void hideAllWidget() {
    imgPic.setVisibility(View.GONE);
    scroll.setVisibility(View.GONE);
    webView.setVisibility(View.GONE);
  }

  @Override
  // 重写上下文菜单的创建方法
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    MenuInflater inflator = new MenuInflater(this);
    inflator.inflate(R.menu.menus, menu);
    super.onCreateContextMenu(menu, v, menuInfo);
  }

  // 上下文菜单被点击是触发该方法
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.one:
        new Thread() {
          public void run() {
            try {
              byte[] data = GetData.getImage(PIC_URL);
              bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            } catch (Exception e) {
              e.printStackTrace();
            }
            handler.sendEmptyMessage(0x001);
          };
        }.start();
        break;
      case R.id.two:
        new Thread() {
          public void run() {
            try {
              detail = GetData.getHtml(HTML_URL);
            } catch (Exception e) {
              e.printStackTrace();
            }
            handler.sendEmptyMessage(0x002);
          };
        }.start();
        break;
      case R.id.three:
        if (detail.equals("")) {
          Toast.makeText(MainActivity.this, "先请求HTML先嘛~", Toast.LENGTH_SHORT).show();
        } else {
          handler.sendEmptyMessage(0x003);
        }
        break;
    }
    return true;
  }
}
```
最后别忘了加上联网权限：`<uses-permission android:name="android.permission.INTERNET" />`

**注意事项：**
用handler的原因就不用讲了吧~ 另外我们加载html代码的使用的是webView的loadDataWithBaseURL而非LoadData， 如果用LoadData又要去纠结中文乱码的问题，so…用loadDataWithBaseURL就可以不用纠结那么多了 另外有些页面可能需要我们提交一些参数，比如账号密码要需要POST方式提交，另外还有一点要注意的就是**Android从4.0开始就不允许在非UI线程中进行UI操作!**

## 发送POST请求代码示例

有GET自然有POST，我们通过openConnection获取到的HttpURLConnection默认是进行Get请求的, 所以我们使用POST提交数据，应提前设置好相关的参数:`conn.setRequestMethod("POST")`; 
还有:`conn.setDoOutput(true);conn.setDoInput(true)`;设置允许输入、输出
还有:`conn.setUseCaches(false)`; POST方法不能缓存，要手动设置为false, 具体实现看代码:

``` java
public class PostUtils {
  public static String LOGIN_URL = "http://172.16.2.54:8080/HttpTest/ServletForPost";
  public static String LoginByPost(String number,String passwd)
  {
    String msg = "";
    try{
      HttpURLConnection conn = (HttpURLConnection) new URL(LOGIN_URL).openConnection();
      //设置请求方式,请求超时信息
      conn.setRequestMethod("POST");
      conn.setReadTimeout(5000);
      conn.setConnectTimeout(5000);
      //设置运行输入,输出:
      conn.setDoOutput(true);
      conn.setDoInput(true);
      //Post方式不能缓存,需手动设置为false
      conn.setUseCaches(false);
      //我们请求的数据:这里可以写一些请求头的东东...
      String data = "passwd="+ URLEncoder.encode(passwd, "UTF-8")+
        "&number="+ URLEncoder.encode(number, "UTF-8");
      //获取输出流，向服务器写数据
      OutputStream out = conn.getOutputStream();
      out.write(data.getBytes());
      out.flush();
      if (conn.getResponseCode() == 200) {  
        // 获取响应的输入流对象  
        InputStream is = conn.getInputStream();  
        // 创建字节输出流对象  
        ByteArrayOutputStream message = new ByteArrayOutputStream();  
        // 定义读取的长度  
        int len = 0;  
        // 定义缓冲区  
        byte buffer[] = new byte[1024];  
        // 按照缓冲区的大小，循环读取  
        while ((len = is.read(buffer)) != -1) {  
          // 根据读取的长度写入到os对象中  
          message.write(buffer, 0, len);  
        }  
        // 释放资源  
        is.close();  
        message.close();  
        // 返回字符串  
        msg = new String(message.toByteArray());  
        return msg;
      }
    }catch(Exception e){e.printStackTrace();}
    return msg;
  }
}
```
其实和Get方法也没什么差别，只是参数的位置在请求头中