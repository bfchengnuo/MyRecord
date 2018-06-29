在使用 SpringBoot 时用到 JPA + SpringData，出现了意料之外的错误。。。o(￣▽￣*)ゞ))￣▽￣*)o

@Query 中可以使用 4 种定义：

- `?1`、`?2`、`?3` 这样的数字占位符，匹配方法中参数

- `:name` 配合 `@Param` 来使用名字来占位

- 使用 SpEL 表达式

- 使用 JPQL 语言

相关例子可看官网文档：https://docs.spring.io/spring-data/jpa/docs/current/reference/html/

## @Query中使用对象

在官方文档中，并没有提到，在 spring 的一篇博客中找到了答案，那就是使用 SpEL 表达式

``` java
@Query("select u from User u where u.age = ?#{[0]}")
List<User> findUsersByAge(int age);

@Query("select u from User u where u.firstname = :#{#customer.firstname}")
List<User> findUsersByCustomersFirstname(@Param("customer") Customer customer);
```

分别对应 `?` 和 `:` 这两种情况的。

https://spring.io/blog/2014/07/15/spel-support-in-spring-data-jpa-query-definitions

## GenericJDBCException

前台提示： `could not extract ResultSet; nested exception is org.hibernate.exception.GenericJDBCException: could not extract ResultSet`

后台会提示:  `Can not issue data manipulation statements with executeQuery().`

在使用 @Query 进行更新操作时，比如 update、delete，必须要加 @Modifying，否则抛异常

## TransactionRequiredException

完整异常：
`Executing an update/delete query; nested exception is javax.persistence.TransactionRequiredException`

在进行更新操作时，必须加事务，比如使用 @Transactional ，当然位置可以是 service 层或者 dao 层。
