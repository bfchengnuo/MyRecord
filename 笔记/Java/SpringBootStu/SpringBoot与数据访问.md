# SpringBoot与数据访问

## JDBC

Maven 依赖：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
  <groupId>mysql</groupId>
  <artifactId>mysql-connector-java</artifactId>
  <scope>runtime</scope>
</dependency>
```

SpringBoot 主配置文件：

```yaml
spring:
  datasource:
    username: root
    password: 123456
    url: jdbc:mysql://192.168.15.22:3306/jdbc
    driver-class-name: com.mysql.jdbc.Driver
```

效果：

默认是用 `org.apache.tomcat.jdbc.pool.DataSource` 作为数据源；

数据源的相关配置都在 **DataSourceProperties** 里面；

---

自动配置原理：`org.springframework.boot.autoconfigure.jdbc`：

可参考 DataSourceConfiguration，根据配置创建数据源，默认使用 Tomcat 连接池；可以使用 `spring.datasource.type` 指定自定义的数据源类型；

SpringBoot默认可以支持；org.apache.tomcat.jdbc.pool.DataSource、HikariDataSource、BasicDataSource 等

---

也可以自定义数据源类型，相关源码：

```java
/**
 * Generic DataSource configuration.
 */
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(name = "spring.datasource.type")
static class Generic {
  @Bean
  public DataSource dataSource(DataSourceProperties properties) {
    //使用DataSourceBuilder创建数据源，利用反射创建响应type的数据源，并且绑定相关属性
    return properties.initializeDataSourceBuilder().build();
  }
}
```

其他关键的组件：**DataSourceInitializer：ApplicationListener**；

作用：

- runSchemaScripts()
  运行建表语句；

- runDataScripts()
  运行插入数据的 sql 语句；

默认只需要将文件命名为：`schema-*.sql`、`data-*.sql` 就会自动进行建表、填充数据。

```properties
# 默认规则：schema.sql，schema-all.sql，也可自定义
schema:
  - classpath:department.sql
```

另外 SpringBoot 还自动配置了JdbcTemplate 用来便捷的操作数据库

## 整合Druid数据源

```java
@Configuration
public class DruidConfig {
  @ConfigurationProperties(prefix = "spring.datasource")
  @Bean
  public DataSource druid(){
    return new DruidDataSource();
  }

  //配置Druid的监控
  //1、配置一个管理后台的 Servlet
  @Bean
  public ServletRegistrationBean statViewServlet(){
    ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
    Map<String,String> initParams = new HashMap<>();

    initParams.put("loginUsername","admin");
    initParams.put("loginPassword","123456");
    initParams.put("allow","");//默认就是允许所有访问
    initParams.put("deny","192.168.15.21");

    bean.setInitParameters(initParams);
    return bean;
  }

  //2、配置一个web监控的 filter
  @Bean
  public FilterRegistrationBean webStatFilter(){
    FilterRegistrationBean bean = new FilterRegistrationBean();
    bean.setFilter(new WebStatFilter());

    Map<String,String> initParams = new HashMap<>();
    initParams.put("exclusions","*.js,*.css,/druid/*");

    bean.setInitParameters(initParams);
    bean.setUrlPatterns(Arrays.asList("/*"));
    return  bean;
  }
}
```

## 整合MyBatis

```xml
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>1.3.1</version>
</dependency>
```

步骤：

1. 配置数据源相关属性（见 Druid）

2. 给数据库建表

3. 创建 JavaBean

### 注解版

```java
@Mapper
public interface DepartmentMapper {

  @Select("select * from department where id=#{id}")
  public Department getDeptById(Integer id);

  @Delete("delete from department where id=#{id}")
  public int deleteDeptById(Integer id);

  @Options(useGeneratedKeys = true,keyProperty = "id")
  @Insert("insert into department(departmentName) values(#{departmentName})")
  public int insertDept(Department department);

  @Update("update department set departmentName=#{departmentName} where id=#{id}")
  public int updateDept(Department department);
}
```

问题：自定义 MyBatis 的配置规则；给容器中添加一个 ConfigurationCustomizer；

```java
@Configuration
public class MyBatisConfig {
  @Bean
  public ConfigurationCustomizer configurationCustomizer(){
    return new ConfigurationCustomizer(){
      @Override
      public void customize(Configuration configuration) {
        configuration.setMapUnderscoreToCamelCase(true);
      }
    };
  }
}
```

在配置类上使用 @MapperScan 批量扫描所有的 Mapper 接口；

```java
@MapperScan(value = "com.atguigu.springboot.mapper")
@SpringBootApplication
public class SpringBoot06DataMybatisApplication {
  public static void main(String[] args) {
    SpringApplication.run(SpringBoot06DataMybatisApplication.class, args);
  }
}
```

### 配置文件版

```yaml
mybatis:
  # 指定全局配置文件的位置
  config-location: classpath:mybatis/mybatis-config.xml
  # 指定sql映射文件的位置
  mapper-locations: classpath:mybatis/mapper/*.xml
```

更多使用参照

http://www.mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/

## 整合SpringData JPA

编写一个实体类（bean）和数据表进行映射，并且配置好映射关系；

```java
//使用JPA注解配置映射关系
@Entity //告诉JPA这是一个实体类（和数据表映射的类）
@Table(name = "tbl_user") //@Table来指定和哪个数据表对应;如果省略默认表名就是user；
public class User {
  @Id //这是一个主键
  @GeneratedValue(strategy = GenerationType.IDENTITY)//自增主键
  private Integer id;

  @Column(name = "last_name",length = 50) //这是和数据表对应的一个列
  private String lastName;
  @Column //省略默认列名就是属性名
  private String email;
```

编写一个 Dao 接口来操作实体类对应的数据表（Repository）

```java
//继承JpaRepository来完成对数据库的操作
public interface UserRepository extends JpaRepository<User,Integer> {}
```

基本的配置 JpaProperties

```yaml
spring:  
  jpa:
  hibernate:
    # 更新或者创建数据表结构
    ddl-auto: update
    # 控制台显示SQL
    show-sql: true
```