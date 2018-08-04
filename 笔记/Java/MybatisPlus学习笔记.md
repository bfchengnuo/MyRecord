# MybatisPlus学习笔记

官方文档：[Mybatis-Plus](http://mp.baomidou.com/)

> Mybatis-Plus（简称 MP）是一个 Mybatis 的增强工具，在 Mybatis 的基础上只做增强不做改变，为简化开发、提高效率而生。
>
> 我们的愿景是成为 Mybatis 最好的搭档，就像 魂斗罗 中的 1P、2P，基友搭配，效率翻倍。

关于特性，我这里贴几点我觉得特别棒的，全部特性在官网的文档：

- **预防Sql注入**：内置 Sql 注入剥离器，有效预防Sql注入攻击
- **通用CRUD操作**：内置通用 Mapper、通用 Service，仅仅通过少量配置即可实现单表大部分 CRUD 操作，更有强大的条件构造器，满足各类使用需求
- **支持ActiveRecord**：支持 ActiveRecord 形式调用，实体类只需继承 Model 类即可实现基本 CRUD 操作
- **支持代码生成**：采用代码或者 Maven 插件可快速生成 Mapper 、 Model 、 Service 、 Controller 层代码，支持模板引擎，更有超多自定义配置等您来使用（P.S. 比 Mybatis 官方的 Generator 更加强大！）
- **支持关键词自动转义**：支持数据库关键词（order、key......）自动转义，还可自定义关键词
- **内置分页插件**：基于 Mybatis 物理分页，开发者无需关心具体操作，配置好插件之后，写分页等同于普通List查询
- **内置性能分析插件**：可输出 Sql 语句以及其执行时间，建议开发测试时启用该功能，能有效解决慢查询
- **内置全局拦截插件**：提供全表 delete 、 update 操作智能分析阻断，预防误操作

## 快速开始

首先，还是需要进行定义接口：`public interface UserMapper extends BaseMapper<User> { }`

到这里，准备工作就全部完了（其实就是继承了一个 BaseMapper 类），接口方法不需要定义，甚至 XML 也不需要配置。

基本操作演示：

``` java
// 初始化 影响行数
int result = 0;
// 初始化 User 对象
User user = new User();

// 插入 User (插入成功会自动回写主键到实体类)
user.setName("Tom");
result = userMapper.insert(user);

// 更新 User
user.setAge(18);
result = userMapper.updateById(user);

// 查询 User
User exampleUser = userMapper.selectById(user.getId());

// 查询姓名为‘张三’的所有用户记录
List<User> userList = userMapper.selectList(
        new EntityWrapper<User>().eq("name", "张三")
);

// 删除 User
result = userMapper.deleteById(user.getId());


// 分页查询 10 条姓名为‘张三’的用户记录
List<User> userList = userMapper.selectPage(
  new Page<User>(1, 10),
  new EntityWrapper<User>().eq("name", "张三")
);

// 分页查询 10 条姓名为‘张三’、性别为男，且年龄在18至50之间的用户记录
List<User> userList = userMapper.selectPage(
  new Page<User>(1, 10),
  new EntityWrapper<User>().eq("name", "张三")
  .eq("sex", 0)
  .between("age", "18", "50")
);
```

官方文档称 我们提供了多达 17 个方法给大家使用，可以极其方便的实现单一、批量、分页等操作。

**Mybatis-Plus 通过 EntityWrapper（简称 EW，MP 封装的一个查询条件构造器）或者 Condition（与EW类似） 来让用户自由的构建查询条件，简单便捷，没有额外的负担，能够有效提高开发效率。**

---

> ActiveRecord 一直广受动态语言（ PHP 、 Ruby 等）的喜爱，而 Java 作为准静态语言，对于 ActiveRecord 往往只能感叹其优雅，所以我们也在 AR 道路上进行了一定的探索，喜欢大家能够喜欢 。

使用 AR 模式前的准备工作：

``` java
@TableName("sys_user") // 注解指定表名
public class User extends Model<User> {
  // fields
  // getter and setter

  /** 指定主键 */
  @Override
  protected Serializable pkVal() {
    return this.id;
  }
}
```

然后就可以使用 AR 来进行简单的 CRUD：

``` java
// 初始化 成功标识
boolean result = false;
// 初始化 User
User user = new User();

// 保存 User
user.setName("Tom");
result = user.insert();

// 更新 User
user.setAge(18);
result = user.updateById();

// 查询 User
User exampleUser = t1.selectById();

// 查询姓名为‘张三’的所有用户记录
List<User> userList1 = user.selectList(
  new EntityWrapper<User>().eq("name", "张三")
);

// 删除 User（即使删除了 0 条数据，只要没异常就算成功）
result = t2.deleteById();


// 分页查询 10 条姓名为‘张三’的用户记录
List<User> userList = user.selectPage(
  new Page<User>(1, 10),
  new EntityWrapper<User>().eq("name", "张三")
).getRecords();

// 分页查询 10 条姓名为‘张三’、性别为男，且年龄在18至50之间的用户记录
List<User> userList = user.selectPage(
  new Page<User>(1, 10),
  new EntityWrapper<User>().eq("name", "张三")
  .eq("sex", 0)
  .between("age", "18", "50")
).getRecords();
```

> AR 模式提供了一种更加便捷的方式实现 CRUD 操作，其本质还是调用的 Mybatis 对应的方法，类似于语法糖。

MP 提供的代码生成这里就不题了，也是个很好用的功能，默认配置：[懒人直达电梯](https://gitee.com/baomidou/mybatisplus-spring-boot/blob/2.x/src/test/java/com/baomidou/springboot/test/generator/GeneratorServiceEntity.java) 

## 通用CRUD

当我们需要对数据库的表进行简单的 CRUD 操作时，我们除了需要相应的实体类，以及一个 Mapper 接口，下一步就是让这个接口继承 BaseMapper，不需要定义方法，不需要书写 XML 的 SQL 语句。

> Mybatis 会通过动态代理实现对 Mapper 接口里方法的调用，这个代理对象可以获得 SqlSessionFactory 对象；
>
> 在 SqlSessionFactory 中会存有 Configuration 对象，进而就能获得 MappedStatements 对象的集合；
>
> 而 MappedStatements 对象可以理解为就是 XML 中我们写的 SQL，还会有一些映射信息，这样就能根据接口中的方法来确定是执行哪一个 MappedStatements  中的 SQL 了。
>
> MP 就是在启动时扫描我们的接口，将对应的 SQL 存进 Configuration 中的 MappedStatements  中，所以我们不需要在 XML 中写 SQL 语句，甚至不需要有 XML 文件。（这一点可以在日志中看到启动时有很多 addMappedStatement 的字样，当然其中使用了缓存）
>
> 涉及对象主要有：SqlMethod（枚举对象，MP 支持的 SQL 方法）、TableInfo（数据库表反射信息）、SqlSource（sql 语句处理对象，Mybatis 提供）、**MapperBuilderAssistant**（用于缓存、SQL 参数结果集等的处理，Mybatis 提供）

然后，来补充下前置知识：

MP 支持以下4中主键策略，可根据需求自行选用：

| 值               | 描述                                     |
| ---------------- | ---------------------------------------- |
| IdType.AUTO      | 数据库ID自增（默认会自动回写 id）        |
| IdType.INPUT     | 用户输入ID                               |
| IdType.ID_WORKER | 全局唯一ID，内容为空自动填充（默认配置） |
| IdType.UUID      | 全局唯一ID，内容为空自动填充             |

> 什么是Sequence？简单来说就是一个分布式高效有序ID生产黑科技工具，思路主要是来源于`Twitter-Snowflake算法`。 
>
> MP在Sequence的基础上进行部分优化，用于产生全局唯一ID，好的东西希望推广给大家，所以我们将ID_WORDER 设置为默认配置。 

相关注解：

- 表名注解 `@TableName` 
  value 值是表名，如果相同可以忽略；
  resultMap 指的是 xml 字段映射 resultMap ID 
- 主键注解 `@TableId` 
  指定主键的字段名和主键 ID 策略类型，默认会识别 id 作为主键，类型是 ID_WORKER
- 字段注解 `@TableField`
  exist 可以设置是否为数据库表字段；此外还有一些非常好用的，详情 http://mp.baomidou.com/#/generic-crud

---

### 插入操作

默认会做非空判断，如果传入的实体字段为空，那么 sql 中就不会进行拼接了。

如果想进行全插入，还有一个 insertAll 的方法可用；从结果来看没什么区别，如果你是用包装类对象的话。

### 更新操作

和插入类似，默认也会有非空判断，然后它也有一个 All 方法。

### 查询操作

基本的 byId 查询就不说了，MP 还提供了一个 selectOne 方法，传入一个实体对象，会根据设置的属性来查询。

以及查询多个 id 的 selectBatchIds 方法，生成的 sql 语句是使用的 in 来完成。

支持使用字段名的 map 集合查询的 byMap 方法，是数据库的字段名！

关于分页，提供了 selectPage 方法，其也是使用的是 Mybatis 中的 RowBounds 实现的。MP 为了简化，定义了一个 Page 对象，直接传入它就可以了，常用的是两个参数的构造方法，一个是获取那页一个是每页的条数。

> 默认的分页是内存分页，采用 RowBounds ，当配置了分页插件后即可进行物理分页。

如果不需要分页，可以直接使用 selectList 方法。

### 删除操作

和其他的类似，也分为按 id、map（列名）、batchIds（使用 in 关键字） 来进行删除。

## 条件构造器EntityWrapper

实体包装器，用于处理 sql 拼接，排序，实体参数查询等！可以理解为封装了查询条件的构造器。

> 补充说明： 使用的是数据库字段，不是 Java 属性!

实体包装器 EntityWrapper 继承 Wrapper（Condition 也继承了 Wrapper）

``` java
// 拼接 SQL 方式一（使用 EntityWrapper）
public void testTSQL11() {
  /*
   * 实体带查询使用方法  输出看结果
   */
  EntityWrapper<User> ew = new EntityWrapper<User>();
  ew.setEntity(new User(1));
  ew.where("user_name={0}", "'zhangsan'").and("id=1")
    .orNew("user_status={0}", "0").or("status=1")
    .notLike("user_nickname", "notvalue")
    .andNew("new=xx").like("hhh", "ddd")
    .andNew("pwd=11").isNotNull("n1,n2").isNull("n3")
    .groupBy("x1").groupBy("x2,x3")
    .having("x1=11").having("x3=433")
    .orderBy("dd").orderBy("d1,d2");
  System.out.println(ew.getSqlSegment());
}

// 拼接 SQL 方式二（使用 Condition）
int buyCount = selectCount(Condition.create()
                           .setSqlSelect("sum(quantity)")
                           .isNull("order_id")
                           .eq("user_id", 1)
                           .eq("type", 1)
                           .in("status", new Integer[]{0, 1})
                           .eq("product_id", 1)
                           .between("created_time", startDate, currentDate)
                           .eq("weal", 1));
```

### 自定义 SQL 方法如何使用 Wrapper

mapper java 接口方法

```java
List<User> selectMyPage(RowBounds rowBounds, @Param("ew") Wrapper<T> wrapper);
```

mapper xml 定义

```xml
<select id="selectMyPage" resultType="User">
  SELECT * FROM user 
  <where>
  ${ew.sqlSegment}
  </where>
</select>
```

关于 `${ew.sqlSegment}` 使用了 `$` 不要误以为就会被 sql 注入，请放心使用 mp 内部对 wrapper 进行了字符转义处理！

### 条件参数说明

| 查询方式     | 说明                              |
| ------------ | --------------------------------- |
| setSqlSelect | 设置 SELECT 查询字段              |
| where        | WHERE 语句，拼接 + `WHERE 条件`   |
| and          | AND 语句，拼接 + `AND 字段=值`    |
| andNew       | AND 语句，拼接 + `AND (字段=值)`  |
| or           | OR 语句，拼接 + `OR 字段=值`      |
| orNew        | OR 语句，拼接 + `OR (字段=值)`    |
| eq           | 等于=                             |
| allEq        | 基于 map 内容等于=                |
| ne           | 不等于<>                          |
| gt           | 大于>                             |
| ge           | 大于等于>=                        |
| lt           | 小于<                             |
| le           | 小于等于<=                        |
| like         | 模糊查询 LIKE                     |
| notLike      | 模糊查询 NOT LIKE                 |
| in           | IN 查询                           |
| notIn        | NOT IN 查询                       |
| isNull       | NULL 值查询                       |
| isNotNull    | IS NOT NULL                       |
| groupBy      | 分组 GROUP BY                     |
| having       | HAVING 关键词                     |
| orderBy      | 排序 ORDER BY                     |
| orderAsc     | ASC 排序 ORDER BY                 |
| orderDesc    | DESC 排序 ORDER BY                |
| exists       | EXISTS 条件语句                   |
| notExists    | NOT EXISTS 条件语句               |
| between      | BETWEEN 条件语句                  |
| notBetween   | NOT BETWEEN 条件语句              |
| addFilter    | 自由拼接 SQL                      |
| last         | 拼接在最后，例如：last("LIMIT 1") |

> 注意！ xxNew 都是另起 `( ... )` 括号包裹。 也就是用了 xxNew 后面会另起一个括号，之前所有的在一个括号里。

## 分页插件

配置就不说了，Mybatis 的插件都是基于动态代理的，官方推荐使用传参区分模式：

也就是在 Mapper 接口的方法参数里传入一个 Pagination  对象，后面可以再穿你的条件，这样 MP 会自动给你分页

``` java
public interface UserMapper{ //可以继承或者不继承 BaseMapper
  /**
   * 查询 : 根据state状态查询用户列表，分页显示
   *
   * @param page
   *            翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
   * @param state
   *            状态
   * @return
   */
  List<User> selectUserList(Pagination page, Integer state);
}

// service 层
public Page<User> selectUserPage(Page<User> page, Integer state) {
  // 不进行 count sql 优化，解决 MP 无法自动优化 SQL 问题
  // page.setOptimizeCountSql(false);
  // 不查询总记录数
  // page.setSearchCount(false);
  // 注意！！ 分页 total 是经过插件自动 回写 到传入 page 对象
  return page.setRecords(userMapper.selectUserList(page, state));
}
```

Mapper 对应的 XML 文件可以正常书写，不需要关注分页了：

``` xml
<select id="selectUserList" resultType="User">
  SELECT * FROM user WHERE state=#{state}
</select>
```

可以看出的是想要分页需要传入一个 Page 对象，如果不开启分页插件默认使用的是内存分页，注册分页插件后就是物理分页了，并且在执行分页查询后，Page 对象会被填充一些分页相关的数据，你可以获取它。