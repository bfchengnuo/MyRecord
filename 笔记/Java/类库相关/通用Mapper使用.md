# 通用Mapper使用

什么是 Mapper ？

一句话形容就是：极其方便的使用 Mybatis **单表**的增删改查。

>   通用 Mapper 都可以极大的方便开发人员。可以随意的按照自己的需要选择通用方法，还可以很方便的开发自己的通用方法。
>
>   极其方便的使用 MyBatis 单表的增删改查。
>
>   支持单表操作，不支持通用的多表联合查询。

**支持 Mybatis-3.2.4 及以上版本**

官方文档：https://mapperhelper.github.io/docs/

## 简单使用

最大的好处就是我们不需要在写 mapper.xml  文件了，只需要定义一个接口，来一个实体，其他的单表 CRUD 操作就可以直接用了！

导入依赖就不用说了，Maven：

``` xml
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper</artifactId>
    <!-- 建议使用最新版本,最新版本请从项目首页查找 -->
    <version>3.4.2</version>
</dependency>
```

然后我用的是 Spring 的集成方式，配置 Bean：

``` xml
<bean class="tk.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="com.bfchengnuo.mapper"/>
</bean>
```

这就基本的环境就配置好了，还是非常的简单的，下面就开始写代码了。

### 定义Mapper接口

从数据库找了张表，记得继承 Mapper 类就可以了，指定对应的泛型（实体）：

``` java
public interface GirlMapper extends Mapper<Girl> {}
```

感觉和 SpringBoot 的方式比较类似呢.....

### 定义实体

实体是需要有一定规则的，需要使用 JPA 的注解，官方给了十条说明：

1.  表名默认使用类名,驼峰转下划线(只对大写字母进行处理),如`UserInfo`默认对应的表名为`user_info`。
2.  表名可以使用`@Table(name = "tableName")`进行指定,对不符合第一条默认规则的可以通过这种方式指定表名.
3.  字段默认和`@Column`一样,都会作为表字段,表字段默认为Java对象的`Field`名字驼峰转下划线形式.
4.  可以使用`@Column(name = "fieldName")`指定不符合第3条规则的字段名
5.  **使用`@Transient`注解可以忽略字段,添加该注解的字段不会作为表字段使用.**
6.  **建议一定是有一个@Id注解作为主键的字段,可以有多个@Id注解的字段作为联合主键.**
7.  **默认情况下,实体类中如果不存在包含@Id注解的字段,所有的字段都会作为主键字段进行使用(这种效率极低).**
8.  实体类可以继承使用,可以参考测试代码中的`tk.mybatis.mapper.model.UserLogin2`类.
9.  由于基本类型,如 int 作为实体类字段时会有默认值 0,而且无法消除,所以实体类中建议不要使用基本类型.
10.  @NameStyle 注解，用来配置 对象名/字段和表名/字段之间的转换方式，该注解优先于全局配置style，可选值：
     -   normal:使用实体类名/属性名作为表名/字段名
     -   camelhump:这是默认值，驼峰转换为下划线形式
     -   uppercase:转换为大写
     -   lowercase:转换为小写

通过[使用Mapper专用的MyBatis生成器插件](http://git.oschina.net/free/Mapper/blob/master/wiki/mapper3/7.UseMBG.md)可以直接生成符合要求带注解的实体类。

``` java
public class Girl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
```

后面的调用就好说了，不多说：

``` java
@Service
public class GirlService {
    @Resource
    private GirlMapper girlMapper;

    public List<Girl> queryGirlList() {
        // 指定排序方式
        Example example = new Example(Girl.class);
        example.setOrderByClause("age DESC");
        // return girlMapper.selectAll();
        return girlMapper.selectByExample(example);
    }
}
```

暂时就先说说这最基本的使用，等用到更多功能了再补充

## 解决问题

这个问题不知道是不是因为这个依赖的原因还是其他什么原因，我记得曾经日志是可以正常打印的啊，反正现在是不行了，提示：

``` 
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
```

尝试了各种依赖都是不行，最后发现 `<scope>test</scope> ` 这个的原因，去掉后再**重启服务器**就可以正常使用了