# 自定义starter

starter：

1. 这个场景需要使用到的依赖是什么？

2. 如何编写自动配置

回忆下曾看过的几个注解：

```java
// 指定这个类是一个配置类
@Configuration
// 在指定条件成立的情况下自动配置类生效
@ConditionalOnXXX
// 指定自动配置类的顺序
@AutoConfigureAfter
// 给容器中添加组件
@Bean
// 结合相关xxxProperties类来绑定相关的配置
@ConfigurationPropertie
// 让xxxProperties生效加入到容器中
@EnableConfigurationProperties
```

自动配置类要能加载，就要将需要启动就加载的自动配置类配置在 `META-INF/spring.factories`

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
org.springframework.boot.autoconfigure.admin.SpringApplicationAdminJmxAutoConfiguration,\
org.springframework.boot.autoconfigure.aop.AopAutoConfiguration,\
```

编写模式（规范）：

- 启动器只用来做依赖导入；

- 专门来写一个自动配置模块；

- 启动器依赖自动配置；别人只需要引入启动器（starter）

可以参考 mybatis-spring-boot-starter，命名为：`自定义启动器名-spring-boot-starter`

步骤：

1）、启动器模块

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.atguigu.starter</groupId>
  <artifactId>atguigu-spring-boot-starter</artifactId>
  <version>1.0-SNAPSHOT</version>

  <!--启动器-->
  <dependencies>
    <!--引入自动配置模块-->
    <dependency>
      <groupId>com.atguigu.starter</groupId>
      <artifactId>atguigu-spring-boot-starter-autoconfigurer</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
  </dependencies>
</project>
```

2）、自动配置模块

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>com.atguigu.starter</groupId>
  <artifactId>atguigu-spring-boot-starter-autoconfigurer</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <packaging>jar</packaging>

  <name>atguigu-spring-boot-starter-autoconfigurer</name>
  <description>Demo project for Spring Boot</description>

  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.10.RELEASE</version>
    <relativePath/>
  </parent>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <java.version>1.8</java.version>
  </properties>

  <dependencies>
    <!--引入spring-boot-starter；所有starter的基本配置-->
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
    </dependency>
  </dependencies>
</project>
```

编写配置类 xxxProperties ：

```java
@ConfigurationProperties(prefix = "atguigu.hello")
public class HelloProperties {
  private String prefix;
  private String suffix;

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }
}
```

编写一个“核心”类，使用我们的配置文件：

```java
public class HelloService {
  HelloProperties helloProperties;

  public HelloProperties getHelloProperties() {
    return helloProperties;
  }
  public void setHelloProperties(HelloProperties helloProperties) {
    this.helloProperties = helloProperties;
  }

  public String sayHellAtguigu(String name){
    return helloProperties.getPrefix() + "-" + name + helloProperties.getSuffix();
  }
}
```

自动配置类，用来配置我们自己的 service 等：

```java
@Configuration
@ConditionalOnWebApplication //web应用才生效
@EnableConfigurationProperties(HelloProperties.class)
public class HelloServiceAutoConfiguration {
  @Autowired
  HelloProperties helloProperties;
  @Bean
  public HelloService helloService(){
    HelloService service = new HelloService();
    service.setHelloProperties(helloProperties);
    return service;
  }
}
```

---

更多 SpringBoot 整合示例

https://github.com/spring-projects/spring-boot/tree/master/spring-boot-samples