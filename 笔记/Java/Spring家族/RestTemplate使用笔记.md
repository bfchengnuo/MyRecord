# RestTemplate使用笔记

传统情况下在 java 代码里访问 restful 服务，一般使用 Apache 的 HttpClient 。不过此种方法使用起来太过繁琐， Spring 提供了一种简单便捷的模板类来进行操作，这就是 RestTemplate。

PS：现在使用 OkHttp 的越来越多了，原先是为 Android 打造的，由于移动端的环境，OkHttp 需要做到高效，进而也收到 JavaEE 开发者的欢迎。

## 体验

先上一段代码来感受下使用是多么的简单：

``` java
//请求地址
String url = "http://localhost:8080/testPost";
//入参
RequestBean requestBean = new RequestBean();
requestBean.setTest1("1");
requestBean.setTest2("2");
requestBean.setTest3("3");

RestTemplate restTemplate = new RestTemplate();
ResponseBean responseBean = 
  restTemplate.postForObject(url, requestBean, ResponseBean.class);
```

其中都省去了手动转 JSON 的步骤，postForObject 方法的这三个参数分别代表：请求地址、请求参数、HTTP响应转换被转换成的对象类型。

RestTemplate 方法的名称遵循命名约定，第一部分指出正在调用什么 HTTP 方法，第二部分指示返回的内容。

完整的使用可以参考[官方API](https://link.jianshu.com/?t=http://docs.spring.io/spring-framework/docs/4.3.7.RELEASE/javadoc-api/org/springframework/web/client/RestTemplate.html)

## 关于消息转换器

我们知道，调用 reseful 接口传递的数据内容是 json 格式的字符串，返回的响应也是 json 格式的字符串。

然而，上例中 `restTemplate.postForObject` 方法的请求参数 RequestBean 和返回参数 ResponseBean 却都是 java 类。

是 RestTemplate 通过 **HttpMessageConverter** 自动帮我们做了转换的操作。

默认情况下 RestTemplate 自动帮我们注册了一组 HttpMessageConverter 用**来处理一些不同的 contentType 的请求**。

如 StringHttpMessageConverter 来处理 `text/plain` ；MappingJackson2HttpMessageConverter 来处理 `application/json` ；MappingJackson2XmlHttpMessageConverter 来处理 `application/xml`。

你可以在 `org.springframework.http.converter` 包下找到所有 spring 帮我们实现好的转换器。

如果现有的转换器不能满足你的需求，你还可以实现 `org.springframework.http.converter.HttpMessageConverter` 接口自己写一个。

将 HttpMessageConverter 注册到 RestTemplate 中：

``` java
RestTemplate restTemplate = new RestTemplate();
// 获取RestTemplate默认配置好的所有转换器
List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
// 默认的MappingJackson2HttpMessageConverter在第7个 先把它移除掉
messageConverters.remove(6);
// 添加上GSON的转换器
messageConverters.add(6, new GsonHttpMessageConverter());
```

就是写的不是很优雅。。。

## 设置底层的连接方式

要创建一个 RestTemplate 的实例，您可以像上述例子中简单地调用默认的无参数构造函数。**这将使用 `java.net` 包中的标准 Java 类作为底层实现来创建 HTTP 请求。**

但很多时候我们需要像传统的 HttpClient 那样设置 HTTP 请求的一些属性。RestTemplate 使用了一种很偷懒的方式实现了这个需求，那就是直接使用一个 HttpClient 作为底层实现......

``` java
// 生成一个设置了连接超时时间、请求超时时间、异常最大重试次数的httpClient
RequestConfig config = 
  RequestConfig.custom()
  .setConnectionRequestTimeout(10000)
  .setConnectTimeout(10000)
  .setSocketTimeout(30000)
  .build();

HttpClientBuilder builder = 
  HttpClientBuilder.create()
  .setDefaultRequestConfig(config)
  .setRetryHandler(new DefaultHttpRequestRetryHandler(5, false));

HttpClient httpClient = builder.build();

// 使用httpClient创建一个ClientHttpRequestFactory的实现
ClientHttpRequestFactory requestFactory = 
  new HttpComponentsClientHttpRequestFactory(httpClient);

// ClientHttpRequestFactory作为参数构造一个使用作为底层的RestTemplate
RestTemplate restTemplate = new RestTemplate(requestFactory);
```

如果想使用 Okhttp 来作为连接方式，那么除了添加相应的依赖，最简单的只需要：

``` java
@Bean
public RestTemplate restTemplate(){
  return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
}
```

也可以尝试一下 RestTemplateBuilder 来构建。

然后，RestTemplate 还支持设置拦截器，没用到过，暂时就不看了。

## 其他常用方法

**getForEntity 方法**：返回值是一个 `ResponseEntity<T>`，`ResponseEntity<T>` 是 Spring 对 HTTP 请求响应的封装，包括了几个重要的元素，如响应码、contentType、contentLength、响应消息体等。

``` java
public String getHello() {
  // 通过服务名调用而不是服务地址，可实现客户端负载均衡
  ResponseEntity<String> responseEntity = 
    restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class);

  String body = responseEntity.getBody();
  HttpStatus statusCode = responseEntity.getStatusCode();
  int statusCodeValue = responseEntity.getStatusCodeValue();
  HttpHeaders headers = responseEntity.getHeaders();
}

// 传递参数的两种方式
ResponseEntity<String> responseEntity = 
  restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={1}", String.class, "张三");

Map<String, String> map = new HashMap<>();
map.put("name", "李四");
ResponseEntity<String> responseEntity = 
  restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={name}", String.class, map);
```

调用地址也可以是一个 URI 对象而不是字符串，参数神马的都包含在 URI 中了，Spring 中提供了 `UriComponents` 来构建 Uri。

``` java
UriComponents uriComponents = 
  UriComponentsBuilder
  .fromUriString("http://HELLO-SERVICE/sayhello?name={name}")
  .build()
  .expand("王五")
  .encode();

URI uri = uriComponents.toUri();
ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
```

同时，除了 String，这里也可以将返回的结果直接解析成对象：

`ResponseEntity<Book> responseEntity = restTemplate.getForEntity("http://HELLO/getbook1", Book.class);`

---

postForEntity 方法的使用和上面非常类似，不再多说。

关于 Post，还有一种是 postForLocation 方法，它也是提交新资源，**提交成功之后，返回新资源的 URI**， postForLocation 的参数和前面两种的参数基本一致，只不过该方法的返回值为 Uri，这个只需要服务提供者返回一个 Uri 即可，该 Uri 表示新资源的位置。

---

PUT 请求和 DELETE 请求其实也差不多，都是一个套路：

``` java
public void put() {
  Book book = new Book();
  book.setName("红楼梦");
  restTemplate.put("http://HELLO-SERVICE/getbook3/{1}", book, 99);
}

public void delete() {
  restTemplate.delete("http://HELLO-SERVICE/getbook4/{1}", 100);
}
```

以上，应该满足日常的需求了吧。

## 参考

https://www.jianshu.com/p/c9644755dd5e

https://blog.csdn.net/u012702547/article/details/77917939