# CORS充电笔记

今天做一个前端模块，涉及到了跨域问题，发现只是对跨域有些基础了解，解决跨域使用了 SpringBoot 配合 CORS，但是在加上 SpringSecurity 的认证后，就 GG 了。

## 同源策略

这是个最最基础的知识了，应该都知道，回顾一下：

| 说明                           | 是否允许                                |
| ------------------------------ | :-------------------------------------- |
| 同一域名下                     | 允许                                    |
| 同一域名下不同文件夹           | 允许                                    |
| 同一域名，不同端口             | 不允许                                  |
| 同一域名，不同协议             | 不允许                                  |
| 域名和域名对应 ip              | 不允许                                  |
| 主域相同，子域不同             | 不允许                                  |
| 同一域名，不同二级域名（同上） | 不允许（cookie 这种情况下也不允许访问） |
| 不同域名                       | 不允许                                  |

> 注意：Cookies 不区分端口

下面就来讲一下这一种跨域解决方案：CORS（Cross-origin resource sharing）也就是：跨来源资源共享。

## 快速入门CORS

简单说 CORS 需要服务端的支持，就是在响应头中添加几个头信息。

``` javascript
// 允许的域名，* 为全部允许
header('Access-Control-Allow-Origin:*');
// 允许的方法
header('Access-Control-Allow-Methods:POST');
// 服务器支持的头信息
header('Access-Control-Allow-Headers:x-requested-with,content-type');
```

如果没有这些头信息，并且违反了同源策略，**浏览器会拒绝**继续处理这个请求或者响应。

需要注意的是，这个请求其实已经发送了，但是因为浏览器没找到响应的这三个头信息，认为是非法的，拒绝处理。

类似于，通过这三个头信息，告诉浏览器 xxx 网站我是允许它访问我的，你如果碰到 xxx 跨域访问我就放行吧。

## CORS概述

跨域资源共享标准新增了一组 HTTP 首部字段，允许服务器声明哪些源站通过浏览器有权限访问哪些资源。

另外，规范要求，对那些可能对服务器数据产生副作用的 HTTP 请求方法（特别是 GET 以外的 HTTP 请求，或者搭配某些 MIME 类型的 POST 请求），**浏览器必须首先使用 OPTIONS 方法发起一个预检请求（preflight request），从而获知服务端是否允许该跨域请求。**

**服务器确认允许之后，才发起实际的 HTTP 请求**。在预检请求的返回中，服务器端也可以通知客户端，是否需要携带身份凭证（包括 Cookies 和 HTTP 认证相关数据）。

### 简单请求

不会触发 CORS 预检的请求称为简单请求，满足以下**所有条件**的才会被视为简单请求，基本上我们日常开发只会关注前面两点

1. 使用`GET、POST、HEAD`其中一种方法
2. 只使用了如下的安全首部字段，不得人为设置其他首部字段
   - `Accept`
   - `Accept-Language`
   - `Content-Language`
   - Content-Type 仅限以下三种
     - `text/plain`
     - `multipart/form-data`
     - `application/x-www-form-urlencoded`
   - HTML 头部 header field 字段：`DPR、Download、Save-Data、Viewport-Width、WIdth`
3. 请求中的任意 `XMLHttpRequestUpload` 对象均没有注册任何事件监听器；XMLHttpRequestUpload 对象可以使用 XMLHttpRequest.upload 属性访问
4. 请求中没有使用 ReadableStream 对象

### 预检请求

需预检的请求要求必须首先使用 `OPTIONS` 方法发起一个预检请求到服务器，以获知服务器是否允许该实际请求。"预检请求“的使用，可以避免跨域请求对服务器的用户数据产生未预期的影响

下面的请求会触发预检请求，其实非简单请求之外的就会触发预检，就不用记那么多了

1. 使用了`PUT、DELETE、CONNECT、OPTIONS、TRACE、PATCH`方法
2. 人为设置了非规定内的其他首部字段，参考上面简单请求的安全字段集合
3. `XMLHttpRequestUpload` 对象注册了任何事件监听器
4. 请求中使用了`ReadableStream`对象

注意：

> 对于附带身份凭证的请求，服务器不得设置 `Access-Control-Allow-Origin` 的值为`*`， 必须是某个具体的域名

## 过程

CORS 的背后基本思想就是使用自定义的 HTTP 头部让浏览器与服务器进行沟通，从而决定请求响应是应该成功还是应该失败。
当 Http 请求发起的时候（不分跨不跨域）会类似带着以下请求头信息：

```
Origin:http://www.bfchengnuo.com
```

返回头也会夹带着类似如下信息：

```
Access-Control-Allow-Credentials:true 
Access-Control-Allow-Origin:http://www.bfchengnuo.com
```

一来一回的请求决定的请求决定了改请求**是否会被浏览器通过**，如果返回头中没有这个头部，或者有头部但是源信息不匹配（就是说返回头 `*-Allow-Origin` 中没有当前请求站点的域名），那么浏览器就会帮我们驳回这次请求，同源策略在这里发挥了作用。

通过这一来一回我们不难发现其实浏览器判断是否驳回的标准就是返回头中是否有 `Access-Control-Allow-*` 这个信息，并且判断这个信息是否合法（即这个信息是否是与请求头中的 Origin 对应的上），对应的上就通过，对应不上就驳回。

最后来一张图：

![请求流程图](https://user-images.githubusercontent.com/25027560/50205881-c409b080-03a4-11e9-8a57-a2a6d0e1d879.png)

## 请求与响应

下面一组例子：

请求：

``` 
# 请求域
Origin: ”http://localhost:3000“

# 这两个属性只出现在预检请求中，即 OPTIONS 请求
Access-Control-Request-Method: ”POST“
Access-Control-Request-Headers: ”content-type“
```

响应：

```
# 允许向该服务器提交请求的 URI，* 表示全部允许，
# 在 SpringMVC 中，如果设成 *，会自动转成当前请求头中的 Origin
Access-Control-Allow-Origin: ”http://localhost:3000“

# 允许访问的头信息
Access-Control-Expose-Headers: "Set-Cookie"

# 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
Access-Control-Max-Age: ”1800”

# 允许 Cookie 跨域，在做登录校验的时候有用
Access-Control-Allow-Credentials: “true”

# 允许提交请求的方法，* 表示全部允许
Access-Control-Allow-Methods:GET,POST,PUT,DELETE,PATCH
```

## SpringMVC中的应用

这里分为三个维度；

先补充下下面代码所需要的前置代码：

``` java
public class MyCorsRegistration extends CorsRegistration {

  public MyCorsRegistration(String pathPattern) {
    super(pathPattern);
  }

  @Override
  public CorsConfiguration getCorsConfiguration() {
    return super.getCorsConfiguration();
  }
}
```

其实与 CorsRegistration 没啥差别。

### 过滤器阶段的CORS

代码：

``` java
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    // 对响应头进行CORS授权
    MyCorsRegistration corsRegistration = new MyCorsRegistration("/**");
    corsRegistration.allowedOrigins(CrossOrigin.DEFAULT_ORIGINS)
      .allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.PUT.name())
      .allowedHeaders(CrossOrigin.DEFAULT_ALLOWED_HEADERS)
      .exposedHeaders(HttpHeaders.SET_COOKIE)
      .allowCredentials(CrossOrigin.DEFAULT_ALLOW_CREDENTIALS)
      .maxAge(CrossOrigin.DEFAULT_MAX_AGE);

    // 注册CORS过滤器
    UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
    configurationSource.registerCorsConfiguration("/**", corsRegistration.getCorsConfiguration());
    CorsFilter corsFilter = new CorsFilter(configurationSource);
    return new FilterRegistrationBean(corsFilter);
  }
}
```

它可以解决 “简单跨域”和“非简单跨域”

---

另一种写法：

``` java
@Configuration
public class MyConfiguration {

  @Bean
  public FilterRegistrationBean corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(false);
    config.addAllowedOrigin("http://domain.com");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
  }
}
```

或者这么写：

``` java
@Configuration
public class MyConfiguration {

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    source.registerCorsConfiguration("/**", buildConfig());
    return new CorsFilter(source);  
  }

  private CorsConfiguration buildConfig() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();

    // 可以自行筛选
    corsConfiguration.addAllowedOrigin("*");
    corsConfiguration.addAllowedHeader("*");
    corsConfiguration.addAllowedMethod("*");

    return corsConfiguration;
  }
}
```

总之，道理都是一个样。

### 拦截器阶段的CORS

> 既然已经有了 Filter 级别的 CORS，为什么还要 CorsInterceptor 呢？因为控制粒度不一样！Filter 是任意 Servlet 的前置过滤器，而 Inteceptor 只对 DispatcherServlet 下的请求拦截有效，它是请求进入 Handler 的最后一道防线，如果再设置一层 Inteceptor 防线，可以增强安全性和可控性。
>
> 关于这个阶段的 CORS，不得不吐槽几句，Spring 把 CorsInteceptor 写死在了拦截器链上的最后一个，也就是说如果我有自定义的 Interceptor，请求一旦被我自己的拦截器拦截下来，则只能通过 CorsFilter 授权跨域，压根走不到 CorsInterceptor。
>
> 所以说 CorsInterceptor 是专为授权 Handler 中的跨域而写的。

代码参考：

``` java
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

  @Bean
  public FilterRegistrationBean corsFilterRegistrationBean() {
    // 对响应头进行CORS授权
    MyCorsRegistration corsRegistration = new MyCorsRegistration("/**");
    this._configCorsParams(corsRegistration);

    // 注册CORS过滤器
    UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
    configurationSource.registerCorsConfiguration("/**", corsRegistration.getCorsConfiguration());
    CorsFilter corsFilter = new CorsFilter(configurationSource);
    return new FilterRegistrationBean(corsFilter);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    // 配置CorsInterceptor的CORS参数
    this._configCorsParams(registry.addMapping("/**"));
  }

  private void _configCorsParams(CorsRegistration corsRegistration) {
    corsRegistration.allowedOrigins(CrossOrigin.DEFAULT_ORIGINS)
      .allowedMethods(HttpMethod.GET.name(), HttpMethod.HEAD.name(), HttpMethod.POST.name(), HttpMethod.PUT.name())
      .allowedHeaders(CrossOrigin.DEFAULT_ALLOWED_HEADERS)
      .exposedHeaders(HttpHeaders.SET_COOKIE)
      .allowCredentials(CrossOrigin.DEFAULT_ALLOW_CREDENTIALS)
      .maxAge(CrossOrigin.DEFAULT_MAX_AGE);
  }
}
```

同样它可以解决 “简单跨域”和“非简单跨域”

---

另一种写法：

``` java
@Configuration
public class CORSConfiguration {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurerAdapter() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedOrigins("http://domain.com", "http://domain2.com")
          .allowedMethods("GET", "POST", "DELETE", "PUT", "OPTIONS")
          .allowCredentials(false).maxAge(3600);
      }
    };
  }
}
```

### Handler阶段的CORS

其实就是 @CrossOrigin 这个注解了，是用在控制器方法上的，其实 Spring 在这里用的还是 CorsInterceptor，做最后一层拦截，这也就解释了为什么 CorsInterceptor 永远是最后一个执行的拦截器。

这是最小控制粒度了，可以精确到某个请求的跨域控制。

最简单的模板代码：

`@CrossOrigin(origins = "*", allowCredentials = "true", allowedHeaders = "*", maxAge = 3600, methods = {RequestMethod.GET, RequestMethod.OPTIONS, RequestMethod.POST})`

它可以解决 “简单跨域” 的问题。

---

三个阶段的 CORS 配置顺序是后面叠加到前面，而不是后面完全覆盖前面的，所以在设计的时候，每个阶段如何精确控制 CORS，还需要在实践中慢慢探索……

## 参考

https://segmentfault.com/a/1190000007078606

https://github.com/amandakelake/blog/issues/62

https://www.jianshu.com/p/d05303d34222