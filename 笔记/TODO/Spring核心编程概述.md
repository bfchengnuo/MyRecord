# Spring核心编程概述

现在的 Spring 越来越精细化，做到了按需分配，目前被拆分成了二十多个模块，下面举几个例子说一下：

- Spring-beans 与 spring-context
  他们两个组成了 Spring 的核心之一也就是 IoC，当然无论哪一个都需要 spring-core 支持。
  beans：依赖注入，依赖查找
  context：注解驱动、事件驱动、模块驱动
- spring-expression
  Spring 表达式的支持，也就是 SpEL
- spring-instrument
  对 Java 装配的支持
- spring-jcl
  从 5.x 版本引入，新型的日志框架，统一 Spring 的日志管理~~（被新出的 slf4j 与 logback 以及不断变化的外部日志 API 接口逼的，哈哈）~~
- spring-messaging
  对消息框架的统一，包括了 JMS、Kafka、RabbitMQ、RocketMQ 这些。
- spring-oxm
  关于 XML 的序列化与反序列化
- spring-web、spring-webflux、spring-webmvc、spring-websocket
  可以看出也是想统一 web，不管是传统的基于 servlet API 的 web，还是 JAX-RS 也就是使用注解规范来开发的 restful 的 web，最后就是对 websocket 的进一步封装。

例如 Aware 接口，它的实现例如 ApplicationContextAware，也可以称作 Aware 回调，会把相应的对象通过回调的方式给你。

这一类可称为契约接口。

## IoC

主要职责就是依赖处理，包含依赖查找与依赖注入甚至其他的类型转换也包含在其中；其次就是生命周期的管理，它包含容器的生命周期与其管理的资源的生命周期。

最后不要忘记了相关的配置，容器的配置（含外部化配置）托管的资源的配置。

另外，IoC 并不是什么新奇玩意，很早之前就有了，有很多实现，spring 是比较成功的一个，其他的例如死掉的 EJB，在 JavaSE 中也有涉及，例如 JavaBeans、SPI、JNDI（依赖查找），开源方面 Apache、Goggle （大名的 Guice）也都推出过。

- 依赖查找
  按名称、按类型、名称+类型、按注解等；实时查找、延迟查找、单个 Bean、集合 Bean。
- 依赖注入
  同样，也是按名称、类型等，还可以注入内建 Bean。

依赖的来源并不仅仅是自定义 Bean，还有可能是容器内建的 Bean，或者容器内建依赖（非 Bean）；
IoC 的配置可以有三个方式：XML、注解、Java API。

BeanFactory 和 ApplicationContext 谁才是 Spring loc容器？

可以说 ApplicationContext 就是 BeanFactory，或者说 BeanFactory 是最基本的 IoC，但是 ApplicationContext 具有更多的企业级特性，也就是 ApplicationContext 是一个超集。

BeanFactory 与 FactoryBean？

BeanFactory 是 IoC 的底层容器。FactoryBean 是创建 Bean 的一种方式，解决复杂的初始化逻辑。

### SpringBean

元信息记录在 BeanDefinition （AnnotatedBeanDefinition）这个对象，可以通过 BeanDefinitionBuilder （BeanDefinitionBuilder#genericBeanDefinition）来构建。

关于 Bean 的名字，并没有强制指定，如果没有会通过类似 BeanNameGenerator 的名字生成器来进行生成，每一个 Bean 都有自己的一个或者多个标识符，都是唯一的，但是一般一个 Bean 只有一个，可以用别名来替代其他。

名字的生成方式如果存在内部类就是 `#` 分割，多个实例后面会跟数字，也是为了便于统计。

对于注解方式，通常我们都不会指定名字，使用的是 AnnotationBeanNameGenerator 来生成。

另外一种比较特殊的实例化 Bean 的方式有 ServiceLoader 的方式。

### 初始化与销毁

初始化的方式有很多，如果都存在，顺序是：

1. Java 提供的 @PostConstruct（@PreDestroy） 注解
2. 实现 InitializingBean （DisposableBean）接口
3. Spring 的 @Bean 中的 initMethod （destroyMethod）属性指定

### 依赖查找

通过 BeanFactory 的 getBean （getObject）方法可以获取 Bean，但是这个方法是不安全的，当没有或者有多个时会抛出异常，又因为它是同步的，所以如果你 catch 了异常，那么可能就会发生死锁。

所以推荐使用 ObjectProvider 来进行查找，单一类型和集合类型都适用。

Spring 的一些内建依赖：

### 依赖注入

选择：

- 强制依赖：构造器注入
- 多依赖：setter 方法注入（无顺序）
- 便利性：字段注入（官方不推荐，淘汰状态）
- 组合方式：方法注入，一般在 @Bean 的时候