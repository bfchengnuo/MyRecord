# RestTemplate使用笔记

传统情况下在 java 代码里访问 restful 服务，一般使用 Apache 的 HttpClient 。不过此种方法使用起来太过繁琐， Spring 提供了一种简单便捷的模板类来进行操作，这就是 RestTemplate。

PS：现在使用 OkHttp 的越来越多了，原先是为 Android 打造的，由于移动端的环境，OkHttp 需要做到高效，进而也收到 JavaEE 开发者的欢迎。

## 基础知识

最常用的 getForEntity 方法的返回值是一个 `ResponseEntity<T>`，`ResponseEntity<T>` 是 Spring 对 HTTP 请求响应的封装，包括了几个重要的元素，如响应码、contentType、contentLength、响应消息体等。

而类似的 getForObject 函数实际上是对 getForEntity 函数的进一步封装，如果你只关注返回的消息体的内容，对其他信息都不关注，此时可以使用 getForObject。

类似的 postForEntity 和 getForEntity 都是特定的请求方式特化，其他的还有 put、delete 、postForLocation 都不怎么常用

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

## 常用API示例

首先说几个需要注意的地方：

- post 方式如果传参数只能用 **LinkedMultiValueMap**
- get 请求参数只能手动在 URL 中进行拼接（有多种拼接方式）

目前暂时只发现这俩，尤其是第二个，总感觉应该可以直接传 map，但是真没找到相应的 API

### 使用Get

按照最常用的 getForObject 和 getForEntity 这两个方法。

``` java
public void testGet(){
  try {
    String url = "http://localhost:8080/selectSmallVideo?sdt=20180531&edt=20180531";
    String result = template.getForObject(url, String.class);
    System.err.println(result);
  } catch (Exception e) {
    e.printStackTrace();
  }
}

public void testGet2(){
  try {
    String url = "http://localhost:8080/selectSmallVideo?sdt=20180531&edt=20180531";
    ResponseEntity<String> entity = template.getForEntity(url, String.class);
    HttpStatus code = entity.getStatusCode();
    System.err.println(code);
    System.err.println(entity.toString());
  } catch (Exception e) {
    e.printStackTrace();
  }
}


// 传递参数的两种方式
ResponseEntity<String> responseEntity = 
  restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={1}", String.class, "张三");

Map<String, String> map = new HashMap<>();
map.put("name", "李四");
ResponseEntity<String> responseEntity = 
  restTemplate.getForEntity("http://HELLO-SERVICE/sayhello?name={name}", String.class, map);
```

注意一下传参的方式，get 中一般就这两种了。

### 使用Post

和 get 类似，简单说明

``` java
public void testPost(){
  try {
    String url = "http://localhost:8080/selectSmallVideo2";
    LinkedMultiValueMap<String, Integer> map = new LinkedMultiValueMap<>();
    map.add("sdt", 20180531);
    map.add("edt", 20180531);
    String result = template.postForObject(url,map, String.class);
    System.err.println(result);
  } catch (Exception e) {
    e.printStackTrace();
  }
}
```

传参可以参考最开始的 bean 的方式。

### 使用Exchange

另外还会关注到 RestTemplate 还提供了一个 exchange 方法，这个相当于一个公共的请求模板，使用姿势和 get/post 没有什么区别，只是可以由调用发自己来选择具体的请求方法。

``` java
public void testPostHeader() {
  String url = "http://localhost:8080/post";
  String nick = "一灰灰Blog";

  MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  params.add("nick", nick);

  HttpHeaders headers = new HttpHeaders();
  headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
              "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
  HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

  RestTemplate restTemplate = new RestTemplate();
  ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
  System.out.println(response.getStatusCode() + " | " + response.getBody());
}
```

例如，你可以用它来给 get 加请求头，进行个性封装等。

### 自定义header

自定义 header 使用 Map 处理参数与返回值的例子：

``` java
public String getToken(String userName, String password) {
  // 请求地址
  String url = "http://127.0.0.1/oauth/token";
  // 入参
  MultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
  paramMap.add("username", userName);
  paramMap.add("password", password);
  paramMap.add("grant_type", "password");
  paramMap.add("scope", "server");

  // 封装头信息
  HttpHeaders headers = new HttpHeaders();
  headers.add("TENANT_ID", "1");
  headers.add("Authorization", "Basic YWdya546165=");
  HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(paramMap, headers);

  RestTemplate restTemplate = new RestTemplate();
  // 可能会抛出异常
  ResponseEntity<Map> responseEntity = restTemplate.postForEntity(url, httpEntity, Map.class);

  if (responseEntity.getStatusCode() == HttpStatus.OK) {
    Map entityBody = responseEntity.getBody();
    // dosomething
  }

  return "凭证验证失败！";
}
```

暂时用到这些，之后再补充

## 其他常用方法

**getForEntity 方法**：返回值是一个 `ResponseEntity<T>`，`ResponseEntity<T>` 是 Spring 对 HTTP 请求响应的封装，包括了几个重要的元素，如响应码、contentType、contentLength、响应消息体等。

```java
public String getHello() {
  // 通过服务名调用而不是服务地址，可实现客户端负载均衡
  ResponseEntity<String> responseEntity = 
    restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class);

  String body = responseEntity.getBody();
  HttpStatus statusCode = responseEntity.getStatusCode();
  int statusCodeValue = responseEntity.getStatusCodeValue();
  HttpHeaders headers = responseEntity.getHeaders();
}
```

调用地址也可以是一个 URI 对象而不是字符串，参数神马的都包含在 URI 中了，Spring 中提供了 `UriComponents` 来构建 Uri。

```java
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

------

postForEntity 方法的使用和上面非常类似，不再多说。

关于 Post，还有一种是 postForLocation 方法，它也是提交新资源，**提交成功之后，返回新资源的 URI**， postForLocation 的参数和前面两种的参数基本一致，只不过该方法的返回值为 Uri，这个只需要服务提供者返回一个 Uri 即可，该 Uri 表示新资源的位置。

------

PUT 请求和 DELETE 请求其实也差不多，都是一个套路：

```java
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

## 可能存在的问题

### 乱码问题

对于这个问题，最常见的就是让你重新定义消息转换器  StringHttpMessageConverter ，因为它默认使用的是 ISO_8859_1。

``` java
private static RestTemplate setRestTemplateEncode(RestTemplate restTemplate) {
  if (null == restTemplate || ObjectUtils.isEmpty(restTemplate.getMessageConverters())) {
    return null;
  }

  List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
  for (int i = 0; i < messageConverters.size(); i++) {
    HttpMessageConverter<?> httpMessageConverter = messageConverters.get(i);
    if (httpMessageConverter.getClass().equals(StringHttpMessageConverter.class)) {
      messageConverters.set(i, new StringHttpMessageConverter(StandardCharsets.UTF_8));
      return restTemplate;
    }
  }

  return restTemplate;
}


// 另一种常见的粗暴的方法
RestTemplate restTemplate = new RestTemplate();
restTemplate.getMessageConverters()
  .set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
```

目前，源码不发生变化的情况下，StringHttpMessageConverter 是位于 index 为 1 的位置，要不然第二种粗暴的方法就不好用了。

然后下面配合一个使用 apache 的 httpclient 的例子：

``` java
@Configuration
public class RestConfiguration {
  @Bean
  public RestTemplate httpClientRestTemplate() {
    // 使用 HttpClient，支持GZIP
    RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    return setRestTemplateEncode(restTemplate);
  }

  private static RestTemplate setRestTemplateEncode(RestTemplate restTemplate) {
    if (null == restTemplate || ObjectUtils.isEmpty(restTemplate.getMessageConverters())) {
      return null;
    }

    List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters();
    for (int i = 0; i < messageConverters.size(); i++) {
      HttpMessageConverter<?> httpMessageConverter = messageConverters.get(i);
      if (httpMessageConverter.getClass().equals(StringHttpMessageConverter.class)) {
        messageConverters.set(i, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
      }
    }

    return restTemplate;
  }
}
```

不过还需要添加 httpclient 的依赖。

``` xml
<dependency>
  <groupId>org.apache.httpcomponents</groupId>
  <artifactId>httpclient</artifactId>
</dependency>
<dependency>
  <groupId>org.apache.httpcomponents</groupId>
  <artifactId>httpcore</artifactId>
</dependency>
<dependency>
  <groupId>org.apache.httpcomponents</groupId>
  <artifactId>httpmime</artifactId>
</dependency>
```

以上，仅供参考。

---

在需要自定义 Accept-Charset 头的情况下，需要设置 ` stringHttpMessageConverter.setWriteAcceptCharset(false); ` 要不然你设置 httpHeaders 也不会有效。

### put&delete无返回值

默认的 put 与 delete 是没有返回值的，为了符合需求，一般会基于底层的 exchange 进行重写，例如这里有一个例子：

``` java
public class RestTester {
  private final String url;

  private final Map<String, String> params = new HashMap<>();

  public void set(String key, String value) {
    params.add(key, value);
  }

  /**
     * 构造方法,请求url.
     *
     * @param url 请求地址
     */
  public RestTester(String url) {
    super();
    this.url = url;
  }

  /**
     * 发送get请求.
     *
     * @return 返回请求结果
     */
  public <T> T get(Class<T> cls) {
    String fullUrl = UriComponentsBuilder.fromHttpUrl(url).queryParams(params).build().toUriString();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<T> resultEntity =  restTemplate.getForEntity(fullUrl, cls);
    return resultEntity.getBody();
  }

  /**
     * 发送post请求.
     *
     * @return 返回请求结果
     */
  public <T> T post(Class<T> cls) {
    String fullUrl = UriComponentsBuilder.fromHttpUrl(url).build().toUriString();
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<T> resultEntity = restTemplate.postForEntity(fullUrl, params, cls);
    return resultEntity.getBody();
  }

  /**
     * 发送/获取 服务端数据(主要用于解决发送put,delete方法无返回值问题).
     *
     * @param url      绝对地址
     * @param method   请求方式
     * @param bodyType 返回类型
     * @param <T>      返回类型
     * @return 返回结果(响应体)
     */
  public <T> T exchange(String url, HttpMethod method, Class<T> bodyType) {
    // 请求头
    HttpHeaders headers = new HttpHeaders();
    MimeType mimeType = MimeTypeUtils.parseMimeType("application/json");
    MediaType mediaType = new MediaType(mimeType.getType(), mimeType.getSubtype(), Charset.forName("UTF-8"));
    // 请求体
    headers.setContentType(mediaType);
    //提供json转化功能
    ObjectMapper mapper = new ObjectMapper();
    String str = null;
    try {
      if (!params.isEmpty()) {
        str = mapper.writeValueAsString(params);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    // 发送请求
    HttpEntity<String> entity = new HttpEntity<>(str, headers);
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<T> resultEntity = restTemplate.exchange(url, method, entity, bodyType);
    return resultEntity.getBody();
  }
}
```

## 参考

https://www.jianshu.com/p/c9644755dd5e
https://blog.csdn.net/u012702547/article/details/77917939
[中级使用篇](https://segmentfault.com/a/1190000016026290#articleHeader4)