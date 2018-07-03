为了达到低耦合，很多配置我们更倾向于独立出来，比如使用 properties 或者 yml 文件来配置。

最经典的就是 JDBC 数据源的配置了吧，写在 Spring 文件也挺乱的，直接独立出来，在 Spring 配置文件中可以使用 “EL” 表达式来取

在最近的几次尝试中，好多次取值都是 null，不解，出现问题大多是在使用 Java 配置的时候，xml 一般是不需要操心的，所以这里就只说 Java 配置的情况了。

## Spring中加载外部配置

加载配置文件用到的是 `@PropertySource` 注解，然后一般会配合 `@Value` 来进行取值，如果用了 SpringMVC，稍微注意下父子容器的关系即可。

然后我要说的是其和 `@Configuration` 连用的情况，在一般的 Bean 中使用 PropertySource 一般都会取的到值，并且只需要加载一次其他 Bean 也可以取得的，本质是存储到 Spring 中的 Environment 中。

但是我发现在 Configuration 中用的话会变的有些奇怪。

---

比如我定义了下面的代码：

``` java
@Configuration
@PropertySource("classpath:jdbc.properties")
public class JdbcConfig {

	@Value("${jdbc.url:NaN}")
	private String url;

	@Autowired
	private Environment env;
}
```

其中，你如果打印 url 的话得到的是 `${jdbc.url:NaN}` 这个值，如果使用 `env.getProperty("jdbc.url")` 那么获取到的就是配置文件中配置的值。

这里如果需要在 @Value 中使用 `${...}` 表达式，那么还需要配置一个 Bean 才行：

``` java
// 解决 @Value 占位符问题
@Bean
public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
  return new PropertySourcesPlaceholderConfigurer();
}
```

然后就一切正常了，总的来说在 Spring 中解决起来还是很简单的。

## SpringBoot中加载外部配置

同样，在非 Configuration  注解下的 Bean 加载是很正常的，但是在 Configuration 中加载就取不到。

### 问题一览

如下面的代码（使用的是 SpringBoot2.x 版本）：

``` java
@Configuration
@PropertySource(value = "classpath:jdbc.properties", encoding = "UTF-8", ignoreResourceNotFound=false)
public class DataSourceConfig {
  @Value("${sp.datasource.url}")
  private String test;
}
```

如上，这样配置 test 完全取不到值，是 null，就算加了上面的 Bean 也是空；

甚至你用 Environment 取，还是空！

---

然后，其实最“正统”的是这样写的：

``` java
@Configuration
@ConfigurationProperties(prefix = "com.test") 
@PropertySource("classpath:test.properties")
public class ConfigTestBean {
    private String name;
    private String want;
    // 省略getter和setter
}
```

这样应该是可以注入成功的，网上也大多数全是这样的，但是，在我用的 2.x 版本，这样还是 null ！！

> 1.5 以前的版本，那么可以通过 ConfigurationProperties 注解的 locations 指定 properties 文件的位置 ；
>
> 但是 1.5 版本后就没有这个属性了，需要添加 @Configuration 和 @PropertySource()后才可以读取 

---

我也尝试过直接把配置放在 application.properties（yml） 中，直接使用 @ConfigurationProperties 进行读取，但是还是 null，当时在 SpringBoot 1.x 的版本测试是一切正常的。

PS：在 2.x 的版本我测试 @PropertySource 基本都可以正确加载 yml 文件。

---

然后，最奇怪的是，我在另一个 @Configuration 标注的 Bean 中测试，打印后居然不是空！

他们是完全相同的代码，就是放在了不同的 @Configuration 标注的类中，这真是一个迷......

~~和优先级有关？和加载顺序有关？~~

### 临时解决

目前还没有弄清楚原因，找到了一个曲线救国的方法：

``` java
// 使用默认的配置文件中（application.properties/yml）定义的值
@Bean(name="dataSource")
// 将 properties 中以 ds 为前缀的参数值，写入方法返回的对象中
@ConfigurationProperties(prefix="ds")
public DataSource getDS() {
  return new DriverManagerDataSource();
}


// 使用自定义的文件 jdbc.properties
@Configuration
@PropertySource(value = "classpath:jdbc.properties", encoding = "UTF-8")
public class DataSourceConfig {
  @Bean(name="dataSource")
  @ConfigurationProperties(prefix = "sp.datasource")
  public DataSource getDS() {
    return new DriverManagerDataSource();
  }
}
```

解释下，@ConfigurationProperties 如果放在方法上，会根据指定的前缀往方法里的属性填值，经测试这种方案是可行的，并且好像更简单了....

### 完美解决

在 SegmentFault 上提问有人给解决了！

原地址：https://segmentfault.com/q/1010000015454422

原因就在与没有导入依赖：`spring-boot-configuration-processor`

``` xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-configuration-processor</artifactId>
  <optional>true</optional>
</dependency>
```

然后该怎么用就怎么用就行了！

> 官方中对于 spring-boot-configuration-processor 是这么说明的：
>
> 通过使用 spring-boot-configuration-processor jar， 你可以从被 @ConfigurationProperties 注解的节点轻松的产生自己的配置元数据文件。该 jar 包含一个在你的项目编译时会被调用的 Java 注解处理器。想要使用该处理器，你只需简单添加 spring-boot-configuration-processor 依赖。

springboot 翻译的中文文档中的附录也有写：[点击查看](https://qbgbook.gitbooks.io/spring-boot-reference-guide-zh/X.%20Appendices/B.2.%20Generating%20your%20own%20meta-data%20using%20the%20annotation%20processor.html)

## 其他

其他有 `@EnableConfigurationProperties` 注解之类的，它的作用可理解为将配置注入到一个对象中，其中可指定 class：

``` java
@ConfigurationProperties(prefix = "com.test")
public class ConfigBean {
    private String name;
    private String want;

    // 省略getter和setter
}

@SpringBootApplication
@EnableConfigurationProperties({ConfigBean.class})
public class Application {}
```

如果不想指定 class，可以直接在配置类上加 @EnableConfigurationProperties。