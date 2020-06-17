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

### SpringBean生命周期

主要关注点：**AbstractAutowireCapableBeanFactory** 的 initializeBean 方法，可以看出它先执行 invokeAwareMethods 方法来处理 Aware 回调接口，然后调用 BeanPostProcessorsBeforeInitialization 前置处理器初始化，接着调用自定义的 init 方法（XML），最后调用 BeanPostProcessorsAfterInitialization 后置处理器；主要就是这四个步骤。

#### 初始化前

#### 初始化

简单说就是执行下面的回调

1. @PostConstruct

   需要注意，在不支持注解驱动的上下文是无效的，在前置阶段就已经完成调用，触发手动添加相应的 PostProcessor：`beanFactory.addBeanPostProcessor(new CommonAnnotationBeanPostProcessor());`

2. InitializingBean#afterPropertiesSet

3. xml#init

一个 BeanFactory 会对应多个 BeanPostProcessor 实现，也就是一个 Bean 的初始化会经过多个后置处理器。

#### 初始化后

初始化后阶段主要是 applyBeanPostProcessorsAfterInitialization 方法来完成，它会把 BeanPostProcessors 都执行一遍。

在 BeanDefinition 走完上面的流程之后，可以说就由 BeanDefinition 变为了 SpringBean，这时候会调用 SmartInitializingSingleton 接口的回调方法，意味着告诉开发者此时 Bean 已经准备好了，你可以放心大胆的用。

也就是 SmartInitializingSingleton 接口也可以做到在初始化之后被调用的效果，通常在 Spring ApplicationContext 场景使用。

#### 销毁阶段

这个阶段也可以分为销毁前（DestructionAwareBeanPostProcessor 接口回调），前面说过，初始化与销毁是成对的，也可以通过上面所说的三个方法来自定义，需要注意的是，这里说的销毁仅仅是 IoC 中的销毁，并不是 GC 掉了。

### SpringBean的回收

过程可简单分为：关闭 Spring 容器（应用上下文）、执行 GC、Spring Bean 覆盖的 finalize 方法被回调。

在 SC 或者 SB 中，上下文是可以被替换的，这时候 SpringBean 的是否完全回收就影响很大。

### 总结

**BeanPostProcessor 的使用场景有哪些？**

BeanPostProcessor 提供 Spring bean 初始化前和初始化后的生命周期回调，分别对应：postProcessBeforelnitialization 和 postProcessAfterInitialization方法；

它允许对关心的Bean进行扩展，甚至是替换（代理、wrap）。

其中，ApplicationContext 相关的 Aware 回调也是基于 BeanPostProcessor 实现，即 ApplicationContextAwareProcessor。

---

**BeanFactoryPostProcessor 与 BeanPostProcessor 的区别？**

BeanFactoryPostProcessor 是 Spring BeanFactory(实际为 ConfigurableListableBeanFactory) 的后置处理器，用于扩展 BeanFactory，或通过 BeanFactory 进行依赖查找和依赖注入。

BeanFactoryPostProcessor 必须有 Spring ApplicationContext 执行，BeanFactory 无法与其直接交互。

而 BeanPostProcessor 则直接与BeanFactory 关联，属于 N 对 1 的关系。

---

**BeanFactory 是怎样处理 Bean 生命周期?**

BeanFactory 的默认实现为 DefaultListableBeanFactory，其中 Bean生命周期与方法映射如下:

- BeanDefinition 注册阶段 - registerBeanDefinition
- BeanDefinition 合并阶段 - getMergedBeanDefinition
- Bean 实例化前阶段 - resolveBeforeInstantiation
- Bean 实例化阶段 - createBeanInstance
- Bean 实例化后阶段 - populateBean
- Bean 属性赋值前阶段 - populateBean
- Bean 属性赋值阶段 - populateBean
- Bean Aware 接口回调阶段 - initializeBean
- Bean 初始化前阶段 - initializeBean
- Bean 初始化阶段 - initializeBean
- Bean 初始化后阶段 - initializeBean
- Bean 初始化完成阶段 - preInstantiateSingletons
- Bean 销毁前阶段 - destroyBean
- Bean 销毁阶段 - destroyBean

## 配置元信息

配置元信息一览：

- Spring Bean 配置元信息 - BeanDefinition
- Spring Bean 属性元信息 - PropertyValues
- Spring 容器配置元信息
- Spring 外部化配置元信息 - PropertySource
- Spring Profile 元信息 - @Profile

下面会挨个说明。

### SpringBean配置元信息

- GenericBeanDefinition：通用型 BeanDefinition
- RootBeanDefinition：无 Parent 的 BeanDefinition 或者合并后 BeanDefinition
- AnnotatedBeanDefinition：注解标注的 BeanDefinition，扩展出了注解相关信息。

注解从 3.0 才开始支持，也是现在使用最多的，通过 AnnotatedBeanDefinitionReader 来进行解析，与其他 Reader 不同，注解没有所谓的 Resource 概念，所以它不继承或者实现任何接口或类，资源就是 class；

- 条件评估 - ConditionEvaluator
- Bean 范围解析 - ScopeMetadataResolver （动态代理还是 CGLIB）
- BeanDefinition 解析 - 内部 API 实现
- BeanDefinition 处理 - AnnotationConfigUtils#processCommonDefinitionAnnotations
- BeanDefinition 注册 - BeanDefinitionRegistry

### SpringBean属性元信息

- Bean 属性元信息-PropertyValues
  - 可修改实现 - MutablePropertyValues
  - 元素成员 - PropertyValue
- Bean 属性上下文存储-AttributeAccessor
- Bean 元信息元素-BeanMetadataElement

### 容器配置元信息

也就是 XML 配置的情况下：

| beans 元素属性              | 默认值     | 使用场景                                                     |
| --------------------------- | ---------- | ------------------------------------------------------------ |
| profile| null(留空) | Spring Profiles 配置值                                       |
| default-lazy-init           | default    | 当 outter beans “default-lazy-init” 属性存 在时，继承该值，否则为“false” |
| default-merge               | default    | 当 outter beans “default-merge” 属性存在 时，继承该值，否则为“false” |
| default-autowire            | default    | 当 outter beans “default-autowire” 属性 存在时，继承该值，否则为“no” |
| default-autowire-candidates | null(留空) | 默认 Spring Beans 名称 pattern                               |
| default-init-method         | null(留空) | 默认 Spring Beans 自定义初始化方法                           |
| default-destroy-method      | null(留空) | 默认 Spring Beans 自定义销毁方法                             |
| ... | ... | ... |

XML 配置相关的东西还是很多的，不过后面基本都是注解化，所以这一块选择性忽略。

解析相关见：XmlBeanDefinitionReader、BeanDefinitionParserDelegate、BeanDefinitionDocumentReader，通过 DOM 方式来解析。

而 property 方式表达过于简单，不推荐使用此种方式配置。

不管哪一种，最后基本都是通过 BeanDefinitionRegistry 来完成注册的。

## 资源管理

Java 标准资源管理（强大，但是复杂）：

| 职责         | 说明                                              |
| ------------ | ------------------------------------------------------------ |
| 面向资源     | 文件系统、artifact(jar、war、ear 文件)以及远程资源(HTTP、FTP 等) |
| API 整合     | java.lang.ClassLoader#getResource、java.io.File 或 java.net.URL |
| 资源定位     | java.net.URL 或 java.net.URI                                 |
| 面向流式存储 | java.net.URLConnection                                       |
| 协议扩展     | java.net.URLStreamHandler 或 java.net.URLStreamHandlerFactory |

Spring 资源接口：

| 类型       | 接口                                    |
| ---------- | --------------------------------------------------- |
| 输入流     | org.springframework.core.io.InputStreamSource       |
| 只读资源   | org.springframework.core.io.Resource                |
| 可写资源   | org.springframework.core.io.WritableResource        |
| 编码资源   | org.springframework.core.io.support.EncodedResource |
| 上下文资源 | org.springframework.core.io.ContextResource         |

Spring 内建 Source 实现：

| 资源来源       | 资源协议       | 实现类                                              |
| -------------- | -------------- | ------------------------------------------------------------ |
| Bean 定义      | 无             | org.springframework.beans.factory.support.BeanDefinit ionResource |
| 数组           | 无             | org.springframework.core.io.ByteArrayResource                |
| 类路径         | classpath:/    | org.springframework.core.io.ClassPathResource                |
| 文件系统       | file:/         | org.springframework.core.io.FileSystemResource               |
| URL            | URL 支持的协议 | org.springframework.core.io.UrlResource                      |
| ServletContext | 无             | org.springframework.web.context.support.ServletConte xtResource |

Spring 资源加载器通过 ResourceLoader 接口完成，路径解析由 ResourcePatternResolver（PathMatchingResourcePatternResolver）或者 ant 风格的 AntPathMatcher 完成。

依赖注入 ResourceLoader：

1. 实现 ResourceLoaderAware 回调
2. @Autowired 注入 ResourceLoader
3. 注入 ApplicationContext 作为 ResourceLoader

## 数据绑定

数据绑定组件 DataBinder 核心属性：

| 属性                 | 说明                           |
| -------------------- | ------------------------------ |
| target               | 关联目标 Bean                  |
| objectName           | 目标 Bean名称                  |
| bindingResult        | 属性绑定结果                   |
| typeConverter        | 类型转换器                     |
| conversionService    | 类型转换服务                   |
| messageCodesResolver | 校验错误文案 Code 处理器       |
| validators           | 关联的 Bean Validator 实例集合 |

留意 bind 方法与 BeanWrapper。

- Spring 底层 JavaBeans 基础设施的中心化接口
- 通常不会直接使用，间接用于 BeanFactory 和 DataBinder
- 提供标准 JavaBeans 分析和操作，能够单独或批量存储 Java Bean 的属性(properties)
- 支持嵌套属性路径(nested path)
- 实现类 org.springframework.beans.BeanWrapperImpl

DataBinder 元数据 - PropertyValues：

| 特征         | 说明                                                      |
| ------------ | ------------------------------------------------------------ |
| 数据来源     | BeanDefinition，主要来源 XML 资源配置 BeanDefinition         |
| 数据结构     | 由一个或多个 PropertyValue 组成                              |
| 成员结构     | PropertyValue 包含属性名称，以及属性值(包括原始值、类型转换后的值) |
| 常见实现     | MutablePropertyValues                                        |
| Web 扩展实现 | ServletConfigPropertyValues、ServletRequestParameterPropertyValues |
| 相关生命周期 | InstantiationAwareBeanPostProcessor#postProcessProperties    |

Web组件

- org.springframework.web.bind.WebDataBinder
- org.springframework.web.bind.ServletRequestDataBinder
- org.springframework.web.bind.support.WebRequestDataBinder
- org.springframework.web.bind.support.WebExchangeDataBinder(since 5.0)

## 类型转换

JavaBeans 中使用 PropertyEditor （包含了 GUI 等事件等）接口扩展，Spring3 之前使用 PropertyEditor 进行扩展（PropertyEditorSupport 等）；

Spring3 之后采用 `Converter<S,T>` （GenericConverter、ConditionalConverter、ConditionalGenericConverter）。

## 其他

国际化接口：MessageSource、ResourceBundle（JDK）、WatchEvent。

校验相关：Validator、ValidationUtils、LocalValidatorFactoryBean（Bean Validation【JSR303、349】 与 Validator 适配）、MethodValidationPostProcessor、@Validated（AOP）、`public void process(@Valid User user)`。

注解相关：GenericTypeResolver（JDK）、~~GenericCollectionTypeResolver~~、ResolvableType、MethodParameter。