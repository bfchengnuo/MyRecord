> 原文：[点我起飞](https://c0d3p1ut0s.github.io/MyBatis%E6%A1%86%E6%9E%B6%E4%B8%AD%E5%B8%B8%E8%A7%81%E7%9A%84SQL%E6%B3%A8%E5%85%A5/)

# MyBatis框架中常见的SQL注入

## 0x00 MyBatis概述&背景

MyBatis 是支持定制化SQL、存储过程以及高级映射的优秀的持久层框架。由于它非常灵活，非常轻量级，受到广大开发者的欢迎，各个大厂也用得比较多。MyBatis框架介绍相关的内容不多说，这类文章网上很多，这里我着重介绍一下MyBatis下常见的SQL注入漏洞。

写到一半发现有些概念要在前面说清楚一下，不然容易晕。

- MySQL：指MySQL服务器。
- MyBatis：指MyBatis框架。
- JDBC：是Java用来规范数据库连接的接口。
- MySQL Connector/J：MySQL提供的、符合JDBC的、用来供java程序连接MySQL数据库的jar包。俗称：MySQL数据库驱动。

## 0x01 MyBatis的SQL注入

MyBatis支持两种参数符号，一种是#，另一种是$。
使用参数符号#的句子：

```xml
<select id="selectPerson" parameterType="int" resultType="hashmap">
  SELECT * FROM PERSON WHERE ID = #{id}
</select>
```

MyBatis会创建一个预编译语句，生成的代码类似于

```java
// Similar JDBC code, NOT MyBatis…
String selectPerson = "SELECT * FROM PERSON WHERE ID=?";
PreparedStatement ps = conn.prepareStatement(selectPerson);
ps.setInt(1,id);
```

参数会在SQL语句中用占位符”?”来标识，然后使用prepareStatement来预编译这个SQL语句。

但是你以为这个SQL语句真的被MySQL数据库预编译了吗？naive！其实在默认情况下，MySQL Connector/J只不过是把selectPerson做了一下转义，前后加了双引号，拼接到SQL语句里面，然后再交给MySQL执行罢了，更多的细节可以看这里 <https://c0d3p1ut0s.github.io/%E7%AE%80%E5%8D%95%E8%AF%B4%E8%AF%B4MySQL-Prepared-Statement/>

另一种使用参数符号$时，MyBatis直接用字符串拼接把参数和SQL语句拼接在一起，然后执行。众所周知，这种情况非常危险，极容易产生SQL注入漏洞。

在使用MyBatis框架时，有以下场景极易产生SQL注入。

1. SQL语句中的一些部分，例如order by字段、表名等，是无法使用预编译语句的。这种场景极易产生SQL注入。推荐开发在Java层面做映射，设置一个字段/表名数组，仅允许用户传入索引值。这样保证传入的字段或者表名都在白名单里面。

2. like参数注入。使用如下SQL语句可防止SQL注入

   ```sql
   like concat('%',#{title}, '%')，
   ```

3. in之后参数的SQL注入。使用如下SQL语句可防止SQL注入

   ```xml
   id in
   <foreach collection="ids" item="item" open="("separator="," close=")">
   #{item} 
   </foreach>
   ```

## 0x02 x-generator的SQL注入

为了提高开发效率，一些generator工具被开发出来，generator是一个从数据库结构 自动生成实体类、Mapper接口以及对应的XML文件的工具。常见的generator有mybatis-generator，renren-generator等。

mybatis-generator是mybatis官方的一款generator。在mybatis-generator自动生成的SQL语句中，order by使用的是$，也就是简单的字符串拼接，这种情况下极易产生SQL注入。需要开发者特别注意。

不过，mybatis-generator产生的like语句和in语句全部都是用的参数符号#，都是非常安全的实现。

# 关于MySQL Prepared Statement

## 0x00 前言

之前在写安全测试报告时，对于SQL注入的修复建议或者防御措施无非是两条：一是白名单限制，二是参数化查询。对于参数化查询的原理，停留于MySQL能先将SQL语句进行词法和语法解析，再将参数绑定执行的阶段。而我们在代码中用Prepared Statement语句实现参数化查询时，很可能事实并不是如此。

## 0x01 问题

网上关于预编译语句的文章有很多，但是，大部分都是基于片面的实验得到以偏概全的结论。争论最多的问题是一般情况下MySQL是否开启了服务端的预编译。网上众说纷纭，经过实验，我得出来的结论是MySQL是否开启服务端的预编译是由客户端连接时的参数useServerPrepStmts决定的，而在MySQL提供的Connector/J版本5.0.5（release 2007-03-02）之后，默认情况下，useServerPrepStmts=false。即如果没有显式设置成true，默认情况下，MySQL不启用服务端预编译。

那么我们在代码中使用预编译函数，例如Java中的prepareStatement，是否存在SQL注入的风险呢？

## 0x02 实验

实验之前先打开MySQL服务器的日志，在my.ini中加一行：

```
log="D:/mysql.log"
```

为了方便查看与MySQL服务器的通信，实验中使用了wireshark，如果MySQL服务器在本地，wireshark可能抓不到包，可以用本地ip连接MySQL服务器，不使用127.0.0.1或者localhost。并以管理员身份运行cmd，运行以下命令添加路由

```
route add 本地ip mask 255.255.255.255 本地网关
```

MySQL官网在Connector/J 5.0.5的变更中有如下内容

> Important change: Due to a number of issues with the use of server-side prepared statements, Connector/J 5.0.5 has disabled their use by default. The disabling of server-side prepared statements does not affect the operation of the connector in any way.
>
> To enable server-side prepared statements, add the following configuration property to your connector string:
>
> useServerPrepStmts=true
>
> The default value of this property is false (that is, Connector/J does not use server-side prepared statements).

大致意思是Connector/J 5.05及以后的版本中，默认情况下useServerPrepStmts的值是false，不使用服务端预编译。这里，我用的Connector/J版本是5.1.46。

首先，我们看一下useServerPrepStmts=true情况下wireshark的抓包和MySQL的日志。代码如下：
[![201803211422.PNG](https://i.loli.net/2018/03/21/5ab1fb5d38555.png)](https://i.loli.net/2018/03/21/5ab1fb5d38555.png)201803211422.PNG

运行代码，抓包如下：
[![201803211425.png](https://i.loli.net/2018/03/21/5ab1fb5d2f323.png)](https://i.loli.net/2018/03/21/5ab1fb5d2f323.png)201803211425.png
上面这个包是发送给要求MySQL服务器Prepare语句”select * from user where id=?”
[![201803211426.png](https://i.loli.net/2018/03/21/5ab1fb5d2ddc8.png)](https://i.loli.net/2018/03/21/5ab1fb5d2ddc8.png)201803211426.png
接下来发送填充到占位符的字符串的值，我们看到，在这里字符串没有被转义。
[![201803211427.png](https://i.loli.net/2018/03/21/5ab1fb5d36e47.png)](https://i.loli.net/2018/03/21/5ab1fb5d36e47.png)201803211427.png
这是MySQL的日志，可以明显看到，MySQL prepare了语句select * from user WHERE id=?，接着执行了select * from user WHERE id=’1\‘ or \‘1\‘=\‘1’。

很明显，MySQL服务器对SQL语句做了预编译。

接着，去掉useServerPrepStmts=true，在普通情况下会发生什么呢？
代码如下：
[![201803211434.png](https://i.loli.net/2018/03/21/5ab1fe3048921.png)](https://i.loli.net/2018/03/21/5ab1fe3048921.png)201803211434.png
[![201803211435.png](https://i.loli.net/2018/03/21/5ab1fe3047411.png)](https://i.loli.net/2018/03/21/5ab1fe3047411.png)201803211435.png

抓包发现，java程序仅向MySQL服务器发送了一个Query请求，而且Query的SQL语句是select * from user WHERE id=’1\‘ or \‘1\‘=\‘1’。

[![201803211437.png](https://i.loli.net/2018/03/21/5ab1fe3049f76.png)](https://i.loli.net/2018/03/21/5ab1fe3049f76.png)201803211437.png
从MySQL日志中，我们发现也确实如此。这是为什么呢？

## 0x03 客户端预编译

我们跟一下`PreparedStatement statement = connection.prepareStatement(sql)`这一句，F7跟进，在com.mysql.jdbc.ConnectionImpl中存在以下逻辑：
2792行：
[![201803211502.png](https://i.loli.net/2018/03/21/5ab20549327d3.png)](https://i.loli.net/2018/03/21/5ab20549327d3.png)201803211502.png

判断了一下useServerPreparedStmts是否打开和canServerPrepare是否为true，均为true则走下面调用服务端预编译的逻辑。而默认情况下，useServerPreparedStmts=false，因此，代码到2837行：
[![201803211503.png](https://i.loli.net/2018/03/21/5ab2054882f15.png)](https://i.loli.net/2018/03/21/5ab2054882f15.png)201803211503.png
调用clientPrepareStatement来对SQL语句进行处理。这样的话，对单引号等关键字符的转义在哪里做的呢？

我们接着来跟一下`statement.setString(1, "1' or '1'='1");`这一句。在com.mysql.jdbc.PreparedStatement中，2238行开始
[![201803211520.png](https://i.loli.net/2018/03/21/5ab20a62051dc.png)](https://i.loli.net/2018/03/21/5ab20a62051dc.png)201803211520.png
省略部分代码
[![201803211529.png](https://i.loli.net/2018/03/21/5ab20a62526a1.png)](https://i.loli.net/2018/03/21/5ab20a62526a1.png)201803211529.png
从2282行开始，对填充的字符串做转义处理，并在转义之后的字符串前后填加单引号。这段代码的主要作用是转义字符串，防止SQL注入。

## 0x04 试着突破它

这里转义的字符比较少，有没有漏掉一些字符，能够逃逸出单引号呢？我对SQL注入并不精通，凭空想出一些payload比较难，这里有两种方法可以试一下，一是参看类似的实现，例如php中addslashes的源代码，二是fuzz。

PHP的addslashes的源代码在ext/standard/string.c中。遗憾的是，并没有发现其他字符。

接下来是fuzz，Java中，字符的编码是utf16，Unicode的编码空间从U+0000到U+10FFFF，共有1,112,064个码位。进过fuzz测试，也没有发现能逃逸单引号的字符。

## 0x05 其他语言的客户端prepare

MySQL官方提供了各种语言连接MySQL的Connectors，其他语言的connectors是不是也默认客户端prepare呢？我测试了Python的，结果也是同样。其余Connectors有兴趣的读者可以自行测试。

# 补充

关于 like 模糊查询的防注入，可以采用字符串拼接的方式：

例如：`select * from fack where username like concat('%', ? , '%')`

这样再填充参数就可以了。当然也可以使用 `||` 来进行字符串的连接。

---

或者使用 `like ?` 的形式，在 set 占位符的时候在值的前面和后面加上 “%”，不过这种方式不确定在纯 JDBC 中是否可用，在常见的 DAO 层框架应该是可以的。