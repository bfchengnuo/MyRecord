## 总览

### SpringBoot 三大特性：

- 组件自动装配

  Web MVC、Web Flux、JDBC

- 嵌入式 Web 容器

  Tomcat、Jetty、Netty

- 生产准备特性

  指标、健康检查、外部化配置，2.x 增加了 `/actuator` 前缀。

Spring Boot Starter 是个好东西。

国内不太注重运维管理这块，也就是 Spring Boot Actuator，也可使用 jconsole 去连。

---

### 关于嵌入式 Web 容器：

**Web Servlet**：Tomcat、Jetty

**Web Reactive**：Netty Web Server

PS：Netty 这类异步非阻塞服务器速度并不快，解决的不是速度而是吞吐量问题。

---

### Spring Web MVC 应用：

**Web MVC 视图**：模板引擎、内容协商、异常处理等

**Web MVC REST**：资源服务、资源跨域、服务发现等

### Spring Web Flux 应用：

**Reactor 基础**：Java Lambda、Mono、Flux

**Web Flux 核心**：Web MVC 注解、函数式声明、异步非阻塞

---

### @Enable 模块：

具备相同领域的功能组件集合，组合形成一个独立的单元，注意：它是 Spring 里的！。

| 框架实现         | @Enable 注解模块               | 激活模块            |
| ---------------- | ------------------------------ | ------------------- |
| Spring Framework | @EnableWebMvc                  | Web MVC 模块        |
|                  | @EnableTransactionManagement   | 事务管理模块        |
|                  | @EnableCaching                 | Caching 模块        |
|                  | @EnableMBeanExport             | JMX 模块            |
|                  | @EnableAsync                   | 异步处理模块        |
|                  | @EnableWebFlux                 | Web Flux 模块       |
|                  | @EnableAspectJAutoProxy        | AspectJ 代理模块    |
| Spring Boot      | @EnableAutoConfiguration       | 自动装配模块        |
|                  | @EnableManagementContext       | Actuator 管理模块   |
|                  | @EnableConfigurationProperties | 配置属性绑定模块    |
|                  | @EnableOAuth2Sso               | OAuth2 单点登录模块 |
| Spring Cloud     | @EnableEurekaServer            | Eureka服务器模块    |
|                  | @EnableConfigServer            | 配置服务器模块      |
|                  | @EnableFeignClients            | Feign客户端模块     |
|                  | @EnableZuulProxy               | 服务网关 Zuul 模块  |
|                  | @EnableCircuitBreaker          | 服务熔断模块        |

可以通过注解方式或者编程方式。

注解驱动的方式一般是通过 `@Import(xxxConfiguration.class)` 实现；还有一种就是接口编程的方式，使用的是 `@Import(xxxConfigurationSelector.class)` ，通过这个 Selector 来确定加载那个配置类，Selector 里需要实现一个方法，这个方法返回一个 String 数组，就是配置类的全类名，也可以选择返回 null。

---

### 条件装配：

主要使用的是 @Profile 和 @Conditional。

Profile 相对比较简单，只能通过简单的字符串配置来确定是否加载，是基于配置的；

Conditional 就比较灵活了，通过一个类实现 Condition 接口来单独实现加载控制，返回 true 表示加载，基于编码实现的。

### SB自动装配：

首先要激活自动装配，一般是使用 @EnableAutoConfiguration 开启，然后还要编写好 XXAutoConfiguration，最后在 Classpath 下的 META-INF 文件夹下将写好的自动配置类配好在 spring.factories 文件中。

在编写的自动配置类上，可以使用模式注解、@Enable 模块装配、条件装配（可以同时使用），Spring 会自动加载这个类。

底层装配技术：

- Spring 模式注解装配
- Spring @Enable 模块装配
- Spring 条件装配装配
- Spring 工厂加载机制
  实现类： SpringFactoriesLoader
  配置资源： META-INF/spring.factories

## Spring应用对象

调整 SpringApplication 有两种方式，使用自带的 API 进行 setter 操作，或者使用建造者模式：

``` java
// 使用 API
SpringApplication springApplication = new SpringApplication(DiveInSpringBootApplication.class);
springApplication.setBannerMode(Banner.Mode.CONSOLE);
springApplication.setWebApplicationType(WebApplicationType.NONE);
springApplication.setAdditionalProfiles("prod");
springApplication.setHeadless(true);

// 使用建造者
new SpringApplicationBuilder(DiveInSpringBootApplication.class)
  .bannerMode(Banner.Mode.CONSOLE)
  .web(WebApplicationType.NONE)
  .profiles("prod")
  .headless(true)
  .run(args);
```

---

### **SpringApplication 准备阶段：**

- 配置 SpringBean 来源（Java 配置 or XML 配置，Spring Boot 可使用 **BeanDefinitionLoader** 读取）

- 推断 Web 应用类型，主引导类

  根据当前应用 ClassPath 中是否存在相关实现类来推断 Web 应用的类型；

  主引导类的判断通过线程的执行栈，向上搜寻 main 方法。

- 通过 SpringFactoriesLoader 和 spring.factories 文件加载应用上下文初始器（ApplicationContextInitializer）和应用事件监听器（ApplicationListener）

### **SpringApplication 运行阶段：**

- 加载 SpringApplication 的运行监听器（SpringApplicationRunListeners）

- 运行 SpringApplication 的运行监听器（SpringApplicationRunListeners）

- 监听 SpringBoot 事件、Spring 事件

- 创建应用上下文（ConfigurableApplicationContext）、Environment（ConfigurableEnvironment） 等

  不同的环境会有不同的上下文和 Environment ：

  Web Reactive： AnnotationConfigReactiveWebServerApplicationContext；
  Web Servlet： AnnotationConfigServletWebServerApplicationContext；
  非 Web： AnnotationConfigApplicationContext；

  Web Reactive： StandardEnvironment；
  Web Servlet： StandardServletEnvironment；
  非 Web： StandardEnvironment。

- 回调两个 Runner

- 失败会有故障分析报告

然后关键是注意一个顺序问题，都是使用 Ordered 来控制的。

Spring Boot 通过 SpringApplicationRunListener 的实现类 EventPublishingRunListener 利用 Spring Framework 事件
API ，广播 Spring Boot 事件。

### **Spring Framework 事件/监听器编程模型**

- **Spring 应用事件**
  - 普通应用事件： ApplicationEvent
  - 应用上下文事件： ApplicationContextEvent
- **Spring 应用监听器**
  - 接口编程模型： ApplicationListener
  - 注解编程模型： @EventListener
- **Spring 应用事广播器**
  - 接口： ApplicationEventMulticaster
  - 实现类： SimpleApplicationEventMulticaster
    执行模式：同步或异步，根据是否有线程池

### SpringApplicationRunListener 监听多个运行状态方法：

| 监听方法                                         | 阶段说明                                                     | Spring Boot 起始版本 |
| ------------------------------------------------ | ------------------------------------------------------------ | -------------------- |
| starting()                                       | Spring 应用刚启动                                            | 1.0                  |
| environmentPrepared(ConfigurableEnvironment)     | ConfigurableEnvironment 准备妥当，允许将其调整               | 1.0                  |
| contextPrepared(ConfigurableApplicationContext)  | ConfigurableApplicationContext 准备妥当，允许将其调整        | 1.0                  |
| contextLoaded(ConfigurableApplicationContext)    | ConfigurableApplicationContext 已装载，但仍未启动            | 1.0                  |
| started(ConfigurableApplicationContext)          | ConfigurableApplicationContext 已启动，此时 Spring Bean 已初始化完成 | 2.0                  |
| running(ConfigurableApplicationContext)          | Spring 应用正在运行                                          | 2.0                  |
| failed(ConfigurableApplicationContext,Throwable) | Spring 应用运行失败                                          | 2.0                  |

### EventPublishingRunListener 监听方法与 Spring Boot 事件对应关系：

| 监听方法                                         | Spring Boot 事件                    | Spring Boot 起始版本 |
| ------------------------------------------------ | ----------------------------------- | -------------------- |
| starting()                                       | ApplicationStartingEvent            | 1.5                  |
| environmentPrepared(ConfigurableEnvironment)     | ApplicationEnvironmentPreparedEvent | 1.0                  |
| contextPrepared(ConfigurableApplicationContext)  |                                     |                      |
| contextLoaded(ConfigurableApplicationContext)    | ApplicationPreparedEvent            | 1.0                  |
| started(ConfigurableApplicationContext)          | ApplicationStartedEvent             | 2.0                  |
| running(ConfigurableApplicationContext)          | ApplicationReadyEvent               | 2.0                  |
| failed(ConfigurableApplicationContext,Throwable) | ApplicationFailedEvent              | 1.0                  |

在自定义 SpringBoot 的配置时，要重点留意这几个事件。

## WebMVC

流程都已经很了解了，@EnableWebMvc 之前没怎么用过，它类似我们用的注解驱动，配合 @Configuration 使用后达到自动配置 SpringMVC 的目的，Mapping、Adapter 会自动注入，这是在 Spring 3.1 就有的功能。

### 常用注解

- 注册模型属性： @ModelAttribute

  用的可能不多，平常我们直接在 Handle 方法里使用形参来接收 Model 然后往里存数据，其实可以写一个方法使用 ModelAttribute 注解实现，返回值就是具体的数据了，会在 Handle 之前执行。

- 读取请求头： @RequestHeader，可在方法形参注入使用

- 读取 Cookie： @CookieValue

- 校验参数： @Valid 、 @Validated

- 注解处理： @ExceptionHandler

- 切面通知： @ControllerAdvice

  这个平常我们用的可能也不多，不过非常好用，使用这个注解指定要切的 class 就可以在里面实现各种功能，例如可以用 @ModelAttribute 来往 Model 里添加数据、使用 @ExceptionHandler 来处理异常。

在 Servlet 3.0 规范后，提供了 ServletContainerInitializer SPI，它允许动态的配置 web.xml 的内容，在容器启动时会进行回调，配合 @HandlesTypes 进行筛选 lib 的类，同时 Spring 进行了适配（SpringServletContainerInitializer），并且提供了 Spring 对应的 SPI：

基础接口： WebApplicationInitializer
编程驱动： AbstractDispatcherServletInitializer
注解驱动： AbstractAnnotationConfigDispatcherServletInitializer

一般来说，我们继承 AbstractAnnotationConfigDispatcherServletInitializer 实现必要的三个方法，就可以完全忽略 web.xml 了，分别对应 web.xml 、前端控制器、映射地址。

### SB使用JSP

我们都知道在 SpringBoot 中默认是不支持 JSP 的，但是其实真的想的话也是可以的，只不过比较麻烦。

首先按照官网上，你需要加相关依赖：

``` xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-tomcat</artifactId>
  <!-- Provided -->
</dependency>
<dependency>
  <groupId>org.apache.tomcat.embed</groupId>
  <artifactId>tomcat-embed-jasper</artifactId>
  <!-- Provided -->
</dependency>
```

然后打包方式要改成 war，然后用 Maven 来进行打包（`mvn -Dmaven.test.skip -U clean package`），打包的时候 SpringBoot 的 Maven 插件会进行一个 repackage 操作，最后使用 `java -jar` 运行 war 包即可。

PS：这个插件的作用其实就是把 war 包打成 jar 包，方便执行；pom 里改成 war 后相当于把 war 再打成 war，不过做了一点手脚，也可以直接 `Java -jar` 运行的。

使用 `spring.mvc.view.prefix` 和 `spring.mvc.view.suffix` 设置前缀后缀。

### 视图内容协商

核心组件：

- 视图解析
  - **ContentNegotiatingViewResolver**
    - InternalResourceViewResolver
    - **BeanNameViewResolver**
    - ThymeleafViewResolver
- 配置策略
  - 配置 Bean： **WebMvcConfigurer**
  - 配置对象： **ContentNegotiationConfigurer**
- 策略管理
  - Bean： ContentNegotiationManager
  - FactoryBean ： ContentNegotiationManagerFactoryBean
- 策略实现
  - ContentNegotiationStrategy
    - 固定 MediaType ： FixedContentNegotiationStrategy
    - "Accept" 请求头： HeaderContentNegotiationStrategy
    - 请求参数： ParameterContentNegotiationStrategy（默认使用 format）
    - 路径扩展名： PathExtensionContentNegotiationStrategy
- View 匹配规则
  - ViewResolver 优先规则
  - MediaType 匹配规则

![](E:/git/github/MyRecord/img/SpringMVC内容协商.png)

PS：由于很多条件判断是根据 Bean 的名字来的，所以在自定义 Configuration 的名字要注意一下，一旦名字重复可能会导致无效或者覆盖的问题。

与之相关的自动装配的 Bean：

- 视图处理器
  - InternalResourceViewResolver
  - BeanNameViewResolver
  - ContentNegotiatingViewResolver
  - ViewResolverComposite
  - ThymeleafViewResolver ( Thymeleaf 可用)
- 内容协商
  - ContentNegotiationManager
- 外部化配置
  - WebMvcProperties
  - WebMvcProperties.Contentnegotiation
  - WebMvcProperties.View

## 异步

基于 servlet3.0 的异步示例：

``` java
@WebServlet(urlPatterns = "/async", asyncSupported = true)
public class AsyncTest extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    AsyncContext asyncContext = req.startAsync();

    asyncContext.start(() -> {
      try {
        resp.getWriter().println("Hello World!");
        // 手动触发完成
        asyncContext.complete();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }
}
```

