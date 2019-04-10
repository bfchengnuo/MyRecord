# 进军WebFlux

Spring 5 是流行的 Spring 框架的下一个重大的版本升级。Spring 5 中最重要改动是把反应式编程的思想应用到了框架的各个方面，Spring 5 的反应式编程以 Reactor 库为基础。

Spring 5 框架所包含的内容很多，我最感兴趣的就是新增的 WebFlux 模块。

开发人员可以使用 WebFlux 创建高性能的 Web 应用和客户端（包括其中的 HTTP、服务器推送事件和 WebSocket 支持）。

## WebFlux 简介

WebFlux 模块的名称是 spring-webflux，名称中的 Flux 来源于 Reactor 中的类 Flux。

该模块中包含了对反应式 HTTP、服务器推送事件和 WebSocket 的客户端和服务器端的支持。对于开发人员来说，比较重要的是服务器端的开发。

在服务器端，WebFlux 支持两种不同的编程模型：

1. Spring MVC 中使用的基于 Java 注解的方式；
2. 基于 Java 8 的 lambda 表达式的函数式编程模型。

**这两种编程模型只是在代码编写方式上存在不同。它们运行在同样的反应式底层架构之上，因此在运行时是相同的**。

WebFlux 需要底层提供运行时的支持，WebFlux 可以运行在支持 Servlet 3.1 非阻塞 IO API 的 Servlet 容器上，或是其他异步运行时环境，如 Netty 和 Undertow。

> 对标 JSR-315 和 JSR-340，分别对应 Servlet 规范的 3.0 和 3.1
>
> 3.0 提供了异步化；而 3.1 提供了非阻塞。

最方便的构建 WebFlux  应用的方式是使用 SpringBoot 的初始化器，选择 Reactive Web 依赖。

## Java注解编程模型

基于 Java 注解的编程模型，对于使用过 Spring MVC 的开发人员来说是再熟悉不过的。在 WebFlux 应用中使用同样的模式，容易理解和上手。我们先从最经典的 Hello World 的示例开始说明。

``` java
@RestController
public class BasicController {
  @GetMapping("/hello_world")
  public Mono<String> sayHelloWorld() {
    return Mono.just("Hello World");
  }
}
```

BasicController 是 REST API 的控制器，通过 @RestController 注解来声明。在 BasicController 中声明了一个 URI 为 `/hello_world` 的映射。其对应的方法 `sayHelloWorld()` 的返回值是 `Mono<String>` 类型，其中包含的字符串 "Hello World" 会作为 HTTP 的响应内容。

使用 WebFlux 与 Spring MVC 的不同在于，**WebFlux 所使用的类型是与反应式编程相关的 Flux 和 Mono 等，而不是简单的对象**。对于简单的 Hello World 示例来说，这两者之间并没有什么太大的差别。对于复杂的应用来说，反应式编程和负压的优势会体现出来，可以带来整体的性能的提升。

### REST API

简单的 Hello World 示例并不足以说明 WebFlux 的用法。

先从 REST API 开始说起。REST API 在 Web 服务器端应用中占据了很大的一部分。我们通过一个具体的实例来说明如何使用 WebFlux 来开发 REST API。

``` java
@Service
class UserService {
  private final Map<String, User> data = new ConcurrentHashMap<>();

  Flux<User> list() {
    return Flux.fromIterable(this.data.values());
  }

  Flux<User> getById(final Flux<String> ids) {
    return ids.flatMap(id -> Mono.justOrEmpty(this.data.get(id)));
  }

  Mono<User> getById(final String id) {
    return Mono.justOrEmpty(this.data.get(id))
      .switchIfEmpty(Mono.error(new ResourceNotFoundException()));
  }

  Mono<User> createOrUpdate(final User user) {
    this.data.put(user.getId(), user);
    return Mono.just(user);
  }

  Mono<User> delete(final String id) {
    return Mono.justOrEmpty(this.data.remove(id));
  }
}
```

类 UserService 中的方法都以 Flux 或 Mono 对象作为返回值，这也是 WebFlux 应用的特征。

在方法 `getById()` 中，如果找不到 ID 对应的 User 对象，会返回一个包含了 **ResourceNotFoundException** 异常通知的 Mono 对象。

方法 `getById()` 和 `createOrUpdate()` 都可以接受 String 或 Flux 类型的参数。Flux 类型的参数表示的是有多个对象需要处理。这里使用 `doOnNext()` 来对其中的每个对象进行处理。

---

再来看一个例子：

``` java
@RestController
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
  @ExceptionHandler(ResourceNotFoundException.class)
  public void notFound() {
  }

  @GetMapping("")
  public Flux<User> list() {
    return this.userService.list();
  }

  @GetMapping("/{id}")
  public Mono<User>getById(@PathVariable("id") final String id) {
    return this.userService.getById(id);
  }

  @PostMapping("")
  public Mono<User> create(@RequestBody final User user) {
    return this.userService.createOrUpdate(user);
  }

  @PutMapping("/{id}")
  public Mono<User>  update(@PathVariable("id") final String id, @RequestBody final User user) {
    Objects.requireNonNull(user);
    user.setId(id);
    return this.userService.createOrUpdate(user);
  }

  @DeleteMapping("/{id}")
  public Mono<User>  delete(@PathVariable("id") final String id) {
    return this.userService.delete(id);
  }
}
```

类 UserController 是具体的 Spring MVC 控制器类。

它使用类 UserService 来完成具体的功能。类 UserController 中使用了注解 @ExceptionHandler 来添加了 ResourceNotFoundException 异常的处理方法，并返回 404 错误。类 UserController 中的方法都很简单，只是简单地代理给 UserService 中的对应方法。

## 服务器推送事件

服务器推送事件（Server-Sent Events，SSE）**允许服务器端不断地推送数据到客户端**。

相对于 WebSocket 而言，服务器推送事件只支持服务器端到客户端的**单向数据传递**。虽然功能较弱，但优势在于 SSE **在已有的 HTTP 协议上使用简单易懂的文本格式来表示传输的数据**。

作为 W3C 的推荐规范，SSE 在浏览器端的支持也比较广泛，除了 IE 之外的其他浏览器都提供了支持。在 IE 上也可以使用 polyfill 库来提供支持。

在服务器端来说，SSE 是一个不断产生新数据的流，非常适合于用反应式流来表示。在 WebFlux 中创建 SSE 的服务器端是非常简单的。只需要返回的对象的类型是 `Flux<ServerSentEvent>`，就会被自动按照 SSE 规范要求的格式来发送响应。

``` java
@RestController
@RequestMapping("/sse")
public class SseController {
  @GetMapping("/randomNumbers")
  public Flux<ServerSentEvent<Integer>> randomNumbers() {
    return Flux.interval(Duration.ofSeconds(1))
      .map(seq -> Tuples.of(seq, ThreadLocalRandom.current().nextInt()))
      .map(data -> ServerSentEvent.<Integer>builder()
           .event("random")
           .id(Long.toString(data.getT1()))
           .data(data.getT2())
           .build());
  }
}
```

 SseController 是一个使用 SSE 的控制器的示例。其中的方法 `randomNumbers()` 表示的是每隔一秒产生一个随机数的 SSE 端点。我们可以使用类 `ServerSentEvent.Builder` 来创建 ServerSentEvent 对象。这里我们指定了事件名称 random，以及每个事件的标识符和数据。事件的标识符是一个递增的整数，而数据则是产生的随机数。

PS：我记得在我写的 SB2.x 的初尝试那篇文章中关于这个有个小例子。

### WebSocket

WebSocket 支持客户端与服务器端的**双向通讯**。当客户端与服务器端之间的交互方式比较复杂时，可以使用 WebSocket。

WebSocket 在主流的浏览器上都得到了支持。WebFlux 也对创建 WebSocket 服务器端提供了支持。在服务器端，我们需要实现接口 `org.springframework.web.reactive.socket.WebSocketHandler` 来处理 WebSocket 通讯。接口 WebSocketHandler 的方法 handle 的参数是接口 WebSocketSession 的对象，可以用来获取客户端信息、接送消息和发送消息。

``` java
@Component
public class EchoHandler implements WebSocketHandler {
  @Override
  public Mono<Void> handle(final WebSocketSession session) {
    return session.send(
      session.receive()
      .map(msg -> session.textMessage("ECHO -> " + msg.getPayloadAsText())));
  }
}
```

EchoHandler 对于每个接收的消息，会发送一个添加了 "ECHO -> " 前缀的响应消息。WebSocketSession 的 receive 方法的返回值是一个 `Flux<WebSocketMessage>` 对象，表示的是接收到的消息流。而 send 方法的参数是一个 `Publisher<WebSocketMessage>` 对象，表示要发送的消息流。在 handle 方法，使用 map 操作对 receive 方法得到的 `Flux<WebSocketMessage>` 中包含的消息继续处理，然后直接由 send 方法来发送。

在创建了 WebSocket 的处理器 EchoHandler 之后，下一步需要把它注册到 WebFlux 中。我们首先需要创建一个类 WebSocketHandlerAdapter 的对象，该对象负责把 WebSocketHandler 关联到 WebFlux 中。

``` java
@Configuration
public class WebSocketConfiguration {
  @Autowired
  @Bean
  public HandlerMapping webSocketMapping(final EchoHandler echoHandler) {
    final Map<String, WebSocketHandler> map = new HashMap<>(1);
    map.put("/echo", echoHandler);

    final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
    mapping.setOrder(Ordered.HIGHEST_PRECEDENCE);
    mapping.setUrlMap(map);
    return mapping;
  }

  @Bean
  public WebSocketHandlerAdapter handlerAdapter() {
    return new WebSocketHandlerAdapter();
  }
}
```

其中的 HandlerMapping 类型的 bean 把 EchoHandler 映射到路径 `/echo`。

## 函数式编程模型

WebFlux 还支持基于 lambda 表达式的函数式编程模型。与基于 Java 注解的编程模型相比，函数式编程模型的抽象层次更低，代码编写更灵活，可以满足一些对动态性要求更高的场景。不过在编写时的代码复杂度也较高，学习曲线也较陡。开发人员可以根据实际的需要来选择合适的编程模型。目前 Spring Boot 不支持在一个应用中同时使用两种不同的编程模式。

为了说明函数式编程模型的用法，我们使用 Spring Initializ 来创建一个新的 WebFlux 项目。在函数式编程模型中，**每个请求是由一个函数来处理的**， 通过接口 `org.springframework.web.reactive.function.server.HandlerFunction` 来表示。

HandlerFunction 是一个函数式接口，其中只有一个方法 `Mono<T extends ServerResponse> handle(ServerRequest request)`，因此可以用 labmda 表达式来实现该接口。

接口 ServerRequest 表示的是一个 HTTP 请求。通过该接口可以**获取到请求的相关信息**，如请求路径、HTTP 头、查询参数和请求内容等。方法 handle 的返回值是一个 `Mono<T extends ServerResponse>` 对象。

接口 ServerResponse 用来表示 HTTP 响应。ServerResponse 中包含了很多静态方法来创建不同 HTTP 状态码的响应对象。

下面是一个简单的计算器实现来展示函数式编程模型的用法。

``` java
@Component
public class CalculatorHandler {
  public Mono<ServerResponse> add(final ServerRequest request) {
    return calculate(request, (v1, v2) -> v1 + v2);
  }

  public Mono<ServerResponse> subtract(final ServerRequest request) {
    return calculate(request, (v1, v2) -> v1 - v2);
  }

  public Mono<ServerResponse>  multiply(final ServerRequest request) {
    return calculate(request, (v1, v2) -> v1 * v2);
  }

  public Mono<ServerResponse> divide(final ServerRequest request) {
    return calculate(request, (v1, v2) -> v1 / v2);
  }

  private Mono<ServerResponse> calculate(final ServerRequest request,
                                         final BiFunction<Integer, Integer, Integer> calculateFunc) {
    final Tuple2<Integer, Integer> operands = extractOperands(request);
    return ServerResponse
      .ok()
      .body(Mono.just(calculateFunc.apply(operands.getT1(), operands.getT2())), Integer.class);
  }

  private Tuple2<Integer, Integer> extractOperands(final ServerRequest request) {
    return Tuples.of(parseOperand(request, "v1"), parseOperand(request, "v2"));
  }

  private int parseOperand(final ServerRequest request, final String param) {
    try {
      return Integer.parseInt(request.queryParam(param).orElse("0"));
    } catch (final NumberFormatException e) {
      return 0;
    }
  }
}
```

上述代码给出了处理不同请求的类 CalculatorHandler，其中包含的方法 add、subtract、multiply 和 divide 都是接口 HandlerFunction 的实现。这些方法分别对应加、减、乘、除四种运算。每种运算都是从 HTTP 请求中获取到两个作为操作数的整数，再把运算的结果返回。

在创建了处理请求的 HandlerFunction 之后，下一步是为这些 HandlerFunction **提供路由信息**，也就是这些 HandlerFunction 被调用的条件。这是通过函数式接口 `org.springframework.web.reactive.function.server.RouterFunction` 来完成的。接口 RouterFunction 的方法 `Mono<HandlerFunction<T extends ServerResponse>> route(ServerRequest request)` 对每个 ServerRequest，都返回对应的 0 个或 1 个 HandlerFunction 对象，以 `Mono<HandlerFunction>` 来表示。

当找到对应的 HandlerFunction 时，该 HandlerFunction 被调用来处理该 ServerRequest，并把得到的 ServerResponse 返回。在使用 WebFlux 的 Spring Boot 应用中，只需要创建 RouterFunction 类型的 bean，就会被自动注册来处理请求并调用相应的 HandlerFunction。

``` java
@Configuration
public class Config {
    @Bean
    @Autowired
    public RouterFunction<ServerResponse> routerFunction(final CalculatorHandler calculatorHandler) {
        return RouterFunctions.route(
                RequestPredicates.path("/calculator"),
                request -> request.queryParam("operator").map(operator ->
                        Mono.justOrEmpty(ReflectionUtils.findMethod(
                                CalculatorHandler.class,
                                operator,
                                ServerRequest.class))
                                .flatMap(method -> (Mono<ServerResponse>) ReflectionUtils.invokeMethod(method, calculatorHandler, request))
                                .switchIfEmpty(ServerResponse.badRequest().build())
                                .onErrorResume(ex -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).build()))
                        .orElse(ServerResponse.badRequest().build()));
    }
}
```

上面的代码是相关的配置类 Config。方法 `RouterFunctions.route` 用来根据 Predicate 是否匹配来确定 HandlerFunction 是否被应用。RequestPredicates 中包含了很多静态方法来创建常用的基于不同匹配规则的 Predicate。如 `RequestPredicates.path` 用来根据 HTTP 请求的路径来进行匹配。此处我们检查请求的路径是 `/calculator`。

使用 ServerRequest 的 queryParam 方法来获取到查询参数 operator 的值，然后通过反射 API 在类 CalculatorHandler 中找到与查询参数 operator 的值名称相同的方法来确定要调用的 HandlerFunction 的实现，最后调用查找到的方法来处理该请求。如果找不到查询参数 operator 或是 operator 的值不在识别的列表中，服务器端返回 400 错误；如果反射 API 的方法调用中出现错误，服务器端返回 500 错误。

## 客户端

除了服务器端实现之外，WebFlux 也提供了反应式客户端，可以访问 HTTP、SSE 和 WebSocket 服务器端。

分别对应：Web 的 HTTP、SSE、WebSocket，这里不再多说，见参考。

相关实例代码：

``` java
// HTTP
public class RESTClient {
  public static void main(final String[] args) {
    final User user = new User();
    user.setName("Test");
    user.setEmail("test@example.org");
    final WebClient client = WebClient.create("http://localhost:8080/user");
    final Monol<User> createdUser = client.post()
      .uri("")
      .accept(MediaType.APPLICATION_JSON)
      .body(Mono.just(user), User.class)
      .exchange()
      .flatMap(response -> response.bodyToMono(User.class));
    System.out.println(createdUser.block());
  }
}

// SSE
public class SSEClient {
  public static void main(final String[] args) {
    final WebClient client = WebClient.create();
    client.get()
      .uri("http://localhost:8080/sse/randomNumbers")
      .accept(MediaType.TEXT_EVENT_STREAM)
      .exchange()
      .flatMapMany(response -> response.body(BodyExtractors.toFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
      })))
      .filter(sse -> Objects.nonNull(sse.data()))
      .map(ServerSentEvent::data)
      .buffer(10)
      .doOnNext(System.out::println)
      .blockFirst();
  }
}

// WebSocket
public class WSClient {
  public static void main(final String[] args) {
    final WebSocketClient client = new ReactorNettyWebSocketClient();
    client.execute(URI.create("ws://localhost:8080/echo"), session ->
                   session.send(Flux.just(session.textMessage("Hello")))
                   .thenMany(session.receive().take(1).map(WebSocketMessage::getPayloadAsText))
                   .doOnNext(System.out::println)
                   .then())
      .block(Duration.ofMillis(5000));
  }
}

// 测试类
public class UserControllerTest {
  private final WebTestClient client = WebTestClient.bindToServer().baseUrl("http://localhost:8080").build();
  @Test
  public void testCreateUser() throws Exception {
    final User user = new User();
    user.setName("Test");
    user.setEmail("test@example.org");
    client.post().uri("/user")
      .contentType(MediaType.APPLICATION_JSON)
      .body(Mono.just(user), User.class)
      .exchange()
      .expectStatus().isOk()
      .expectBody().jsonPath("name").isEqualTo("Test");
  }
}
```

客户端也会涉及到一些概念性的知识，但是我并没有列出。

## 参考

https://www.ibm.com/developerworks/cn/java/spring5-webflux-reactive/index.html