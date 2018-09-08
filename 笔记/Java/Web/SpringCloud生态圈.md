# SpringCloud生态圈

本篇主要解释 SpringCloud 中涉及的几个组件的解释，知道他们是干什么用的，为了解决什么问题。

与 SpringBoot 类似，SpringCloud 也不是具体的一个框架实现，而是许许多多的 SpringCloud 子项目整合起来的结果，SpringCloud 统一对使用到的组件做了封装与自动配置等，使我们能更加轻松使用它们。

SpringCloud 的重点在分布式系统，使用前最好对微服务、模块化等有一定了解，不同于单体应用，分布式系统需要考虑的东西要更多，架构设计层面会显得非常复杂，而 SpringCloud 就是来简化这些问题。

> 本笔记只做介绍，不记录代码与使用步骤相关，入门待更新到 BLOG

## 引子

相比微服务，单体应用存在下面的一些缺点：

- 复杂性高
  例如：模块间的边界模糊、依赖关系不清晰、代码质量参差不齐等
- 技术债务
  需求的不断变更和人员的迭代大多都是“不坏不修”的思想
- 部署频率低
  部署一次耗时太长、风险大
- 可靠性差
- 扩展能力受限
- 阻碍技术创新

于是微服务就来了，关于微服务，简单提一下吧：

> 微服务 (Microservices) 是一种**软件架构风格**，它是以专注于单一责任与功能的小型功能区块 (Small Building Blocks) 为基础，利用模组化的方式组合出复杂的大型应用程序，**各功能区块使用与语言无关** (Language-Independent/Language agnostic) 的 API 集相互通讯。

不过要注意微服务与 SOA （面向服务）的区分，SOA 说的话实在是太大了。

## 初探

首先，关于 SpringCloud 的版本，前面说过它只做整合，为了不和其版本冲突，SpringCloud 的版本命名比较特别，使用的是伦敦地铁站的名字，按字符顺序排序，并且更新很快。

| **Component**             | **备注**                                                     |
| ------------------------- | ------------------------------------------------------------ |
| spring-cloud-aws          | 用于简化整合Amazon Web Service的组件                         |
| spring-cloud-bus          | 事件、消息总线，用于传播集群中的状态变化或事件。             |
| spring-cloud-cli          | 用于在Groovy平台创建Spring Cloud应用。                       |
| spring-cloud-commons      | 服务发现、负载均衡、熔断机制这种模式为Spring Cloud客户端提供了一个通用的抽象层。 |
| spring-cloud-contract     | -                                                            |
| spring-cloud-config       | 配置管理工具，支持使用git、svn等存储配置文件。并在支持客户端配置信息的刷新，加密解密配置内容等 |
| spring-cloud-netflix      | 核心组件，对多个Netflix OSS开源套件进行整合                  |
| spring-cloud-security     | 安全工具包                                                   |
| spring-cloud-cloudfoundry | 整合Pivotal Cloudfoundry（Vmware推出的业界第一个开源PaaS云平台）支持 |
| spring-cloud-consul       | 服务发现与配置管理工具                                       |
| spring-cloud-sleuth       | Spring Cloud应用的分布式跟踪实现                             |
| spring-cloud-stream       | 通过Redis、RabbitMQ、Kafka实现的消息微服务                   |
| spring-cloud-zookeeper    | 基于ZooKeeper的服务发现与配置管理组件                        |
| spring-boot               | -                                                            |
| spring-cloud-task         | 用于快速构建数据处理的应用                                   |
| spring-cloud-vault        | -                                                            |
| spring-cloud-gateway      | Spring Cloud网关相关的整合实现                               |

SpringCloud 的特点：

- 约定优于配置
- 适用于各种开发环境
- 隐藏复杂性，提供声明式、无 XML 配置方式
- 开箱即用，快速启动
- 轻量级组件，组件丰富。
- 选型中立，灵活。

SpringCloud 中，推荐使用的组件很多是 Netflix 家的，关于这家公司感兴趣的可以了解下。

## 注册中心Eureka

SpringCloud 提供了多种注册中心的支持，如：Eureka、ZooKeeper 等。官方推荐使用 Eureka。

PS：虽然 Eureka 现在宣布闭源了。。。。

Eureka 包含两个组件：Eureka Server 和 Eureka Client。

Eureka Server 提供**服务注册**，各个节点启动后，会在 Eureka Server 中进行注册，这样 EurekaServer 中的服务注册表中将会存储所有可用服务节点的信息，服务节点的信息可以在界面中直观的看到。

Eureka Client 是一个 java 客户端，用于简化与 Eureka Server 的交互，客户端同时也就别一个内置的、**使用轮询(round-robin)负载算法的负载均衡器**。

在应用启动后，将会向 Eureka Server 发送心跳,默认周期为 30 秒，如果 Eureka Server 在多个心跳周期内没有接收到某个节点的心跳，Eureka Server 将会从服务注册表中把这个服务节点移除(默认90秒)。

**Eureka Server 之间（集群）通过复制的方式完成数据的同步**，Eureka 还提供了客户端缓存机制，即使所有的 Eureka Server 都挂掉，客户端依然可以利用缓存中的信息消费其他服务的 API。

综上，Eureka 通过心跳检查、客户端缓存等机制，确保了系统的高可用性、灵活性和可伸缩性。

## 负载均衡Ribbon

Ribbon 是 Netflix 开源的一款用于客户端软负载均衡的工具软件。Spring Cloud 对 Ribbon 进行了一些封装以更好的使用 Spring Boot 的自动化配置理念。

它有助于控制 HTTP 和 TCP 的客户端的行为。为 Ribbon 配置服务提供者地址后，Ribbon 就可基于某种负载均衡算法，自动地帮助服务消费者去请求。

Ribbon 默认为我们提供了很多负载均衡算法，例如轮询、随机等。当然，我们也可为 Ribbon 实现自定义的负载均衡算法。

> Spring Cloud Ribbon 是基于 Netflix Ribbon 实现的一套客户端负载均衡的工具。
>
> 它是一个基于 HTTP 和 TCP 的客户端负载均衡器。它可以通过在客户端中配置 ribbonServerList 来设置服务端列表去轮询访问以达到均衡负载的作用。

例如，我们可以使用 `@LoadBalanced` 注解配合 RestTemplate 来快速简单的启用负载均衡。

## 容错保护Hystrix

在微服务架构中通常会有多个服务层调用，基础服务的故障可能会导致**级联故障**，进而造成整个系统不可用的情况，这种现象被称为服务**雪崩效应**。

服务雪崩效应是一种因“服务提供者”的不可用导致“服务消费者”的不可用，并将不可用逐渐放大的过程。

---

在 Spring Cloud Hystrix 中实现了**线程隔离、断路器**等一系列的服务保护功能，它也是基于 Netflix 的开源框架  Hystrix 实现的，该框架目标在于通过控制那些访问远程系统、服务和第三方库的节点，从而对延迟和故障提供更强大的容错能力。

Hystrix 具备了**服务降级、服务熔断、线程隔离、请求缓存、请求合并以及服务监控**等强大功能。

---

Hystrix 是由 Netflix 开源的一个延迟和容错库，**用于隔离访问远程系统、服务或者第三方库，防止级联失败，从而提升系统的可用性与容错性**。Hystrix 主要通过以下几点实现延迟和容错：

1. **包裹请求：**
   使用 HystrixCommand 包裹对依赖的调用逻辑，**每个命令在独立线程中执行**。
   这使用了设计模式中的“命令模式”。

2. **跳闸机制：**
   当某服务的错误率超过一定的阈值时，Hystrix 可以自动或手动跳闸，停止请求该服务一段时间。

3. **资源隔离：**
   Hystrix 为每个依赖都维护了一个小型的线程池（或者信号量）。
   如果该线程池已满，发往该依赖的请求就被立即拒绝，而不是排队等待，从而加速失败判定。

4. **监控：**
   Hystrix 可以近乎实时地监控运行指标和配置的变化，例如成功、失败、超时、以及被拒绝的请求等。

5. **回退机制：**
   当请求失败、超时、被拒绝，或当断路器打开时，执行回退逻辑。
   回退逻辑由开发人员自行提供，例如返回一个缺省值。

6. **自我修复：**
   断路器打开一段时间后，会自动进入“半开”状态。

例如，当对特定服务的呼叫达到一定阈值时（Hystrix 中的默认值为 5 秒内的 20 次故障），电路打开，不进行通讯。并且是一个隔离的线程中进行的。

例如使用 `@HystrixCommand(fallbackMethod = xxx)` 注解来配置一个方法，当开路时，自动调用这个方法而进行“快速失败”，按照套路还需要在启动类加 `@EnableHystrix`

## HTTP客户端Feign

Feign 是 Netflix 开发的声明式、模块化的 HTTP 客户端，其灵感来自 Retrofit, JAXRS-2.0 以及 WebSocket。Feign 可帮助我们更好更快的便捷、优雅地调用 HTTP API。

在 Spring Cloud 中，使用 Feign 非常简单——创建一个接口，并在接口上添加一些注释，代码就 OK 了。Feign 支持多种注释，例如 Feign 自带的注解或者 JAX-RS 注解等。

Spring Cloud 对 Feign 进行了增强，使 Feign 支持了 Spring MVC 注解，并整合了 Ribbon 和 Eureka ，从而让 Feign  的使用更加方便。

---

通过在接口上使用 `@FeignClient(value = "xxx")` 注解来表明这是个 Feign 客户端，然后 Value 值就是我们的服务名（Eureka 中）。

在 FeignClient 中的定义方法使用了 SpringMVC 的注解，Feign 就会根据注解中的内容生成对应的 URL，然后基于 Ribbon 的负载均衡去调用 REST 服务（相应的组件都加的情况下）。

其实 Feign 有自己的注解，但是 SpringCloud 为了避免增加开发人员的学习成本，直接使用 SpringMVC 的注解做了适配，效果还是非常棒的。

## 服务网关SpringCloudZuul

为了保证对外服务的安全性，我们需要实现对服务访问的权限控制，而开放服务的权限控制机制将会贯穿并污染整个开放服务的业务逻辑，这会带来的最直接问题是，破坏了服务集群中 REST API 无状态的特点。

从具体开发和测试的角度来说，在工作中除了要考虑实际的业务逻辑之外，还需要额外可续对接口访问的控制处理。

于是，服务网关就出现了！为了解决上面这些问题，我们需要将权限控制这样的东西**从我们的服务单元中抽离出去**，而最适合这些逻辑的地方就是**处于对外访问最前端的地方**，我们需要一个更强大一些的均衡负载器：服务网关。

服务网关是微服务架构中一个不可或缺的部分。通过服务网关统一向外系统提供 REST API 的过程中，除了具备服务路由、均衡负载功能之外，它还具备了权限控制等功能。

Spring Cloud Netflix 中的 Zuul 就担任了这样的一个角色，**为微服务架构提供了前门保护的作用**，同时将权限控制这些较重的非业务逻辑内容迁移到服务路由层面，使得服务集群主体能够具备更高的可复用性和可测试性。

有点拦截器或者过滤器的感觉。

---

> Zuul 是 Netflix 开源的微服务网关，它可以和 Eureka、Ribbon、Hystrix 等组件配合使用（都是自家的当然可以）。
>
> Zuul 的核心是一系列的过滤器，这些过滤器可以完成以下功能：
>
>  1、身份认证与安全：识别每个资源的验证要求，并拒绝那些与要求不符的请求；
>
>  2、审查与监控：在边缘位置追踪有意义的数据和统计结果，从而带来精确的生产视图；
>
>  3、动态路由：动态地将请求路由到不同的后端集群；
>
>  4、压力测试：逐渐增加指向集群的流量，以了解性能；
>
>  5、负载分配：为每一种负载类型分配对应容量，并弃用超出限定值的请求；
>
>  6、静态响应处理：在边缘位置直接建立部分响应，从而避免其转发到内部集群；
>
>  7、多区域弹性：跨域AWS Region进行请求路由。
>
> Spring Cloud 对 Zuul 进行了整合与增强。目前，Zuul 使用的默认 HTTP 客户端是 Apache HTTP Client。
>
> 也可以使用 Rest Client，可以设置 `ribbon.restclient.enabled=true`. 或者使用 Okhttp：`ribbon.okhttp.enabled=true`

这样就可以将一些类似于校验的业务逻辑放到 zuul 中完成，而微服务自身只需要关注自己的业务逻辑即可。

可理解为它本身就是一个微服务。

并且 Zuul  是支持拦截器的，继承 ZuulFilter 这个抽象类，加入到 Spring 的 IoC 容器即可。

## 统一管理微服务配置SpringCloudConfig

在我们开发项目时，需要有很多的配置项需要写在配置文件中，如：数据库的连接信息等。

如果我们的项目已经启动运行，那么数据库服务器的ip地址发生了改变，我们该怎么办？

SpringCloudConfig 提供了解决方案，可以实时同步更新，并不需要重新启动应用程序。

> Spring Cloud Config 为服务端和客户端提供了分布式系统的外部化配置支持。
>
> 配置服务器为各应用的所有环境提供了一个中心化的外部配置。它实现了对服务端和客户端对 Spring Environment 和 PropertySource 抽象的映射，所以它除了适用于 Spring 构建的应用程序，也可以在任何其他语言运行的应用程序中使用。

Config Server 是一个可横向扩展、集中式的配置服务器，它用于集中管理应用程序各个环境下的配置，**默认使用 Git 存储配置文件内容**，也可以使用 SVN 存储，或者是本地文件存储。

Config Client 是 Config Server 的客户端，用于操作存储在 Config Server 中的配置内容。微服务在启动时会请求 Config Server 获取配置文件的内容，请求到后再启动容器。

---

文件的命名规则一般是：`{application}-{profile}.properties/yml` ，然后就是 push 到 Git 的仓库上。

对于服务端，支持以下的请求规则：

请求配置文件的规则如下：

``` 
/{application}/{profile}/[label]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

其中 `{label}` 是指分支，默认是 master，并且支持 properties 和 yml 的自动转换，也就是传一份 properties 文件可以使用 xx.yml 请求的方式获取对应的 YAML 文件格式。

对于客户端，除了要单独编写一个 `bootstrap.yml` 文件来指定坐标，和以前的方式无任何区别。

### 自动/手动刷新

为了能够及时同步配置，还需要为 Config Client 添加 refresh 支持，就是加一个 `spring-boot-starter-actuator` 依赖。

然后在需要刷新的配置 Bean 上加上 `@RefreshScope` 注解就可以了。然后，post 请求 `/refresh` 来更新配置内容。

可以借助 git 的 webhook（web 钩子）实现自动更新（当 push 时，Git 服务器自动请求这个地址）