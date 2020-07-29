## MySQL优化

什么是优化？

> 合理安排资源、调整系统参数使 MySQL 运行更快、更节省资源。
>
> 优化是多方面的，包括查询、更新、服务器等。
>
> 原则：**减少系统瓶颈，减少资源占用，增加系统的反应速度。**

查询数据库的状态信息可使用 sql ：`SHOW STATUS` ，结果比较多，可以使用 like 关键字来进行过滤，例如：`SHOW STATUS LIKE 'Slow_queries'` 。

常看的几个信息：

- Slow_queries ： 慢查询次数
- Com_(CRUD) ： 操作的次数（例如 Com_select）
- Uptime ： 上线时间
- Handler_read%：查看索引使用情况
  handler_read_key：这个值越高越好，越高表示使用索引查询到的次数；
  handler_read_rnd_next：这个值越高，说明查询低效

## 查询优化

在 MySQL 中可以使用 **EXPLAIN** 查看 SQL 执行计划，以执行计划的结果分析来进行对查询的调优，用法：`EXPLAIN SELECT * FROM tb_item` 

对返回结果的说明：

### id

识别符，编号，不重要

### select_type

表示 SELECT 语句的类型。 类型有下面几种：

- SIMPLE
  表示简单查询，其中不包含连接查询和子查询。
- PRIMARY
  表示主查询，或者是最外面的查询语句。
- DERIVED
  SELECT(FROM 子句的子查询)。
  如果查询语句包含子查询，那么就会有一个 PRIMARY 一个 DERIVED。

- UNION
  表示连接查询的第 2 个或后面的查询语句。

- DEPENDENT UNION
  UNION 中的第二个或后面的 SELECT 语句，取决于外面的查询。

- UNION RESULT
  连接查询的结果。

- SUBQUERY
  子查询中的第 1 个 SELECT 语句。

- DEPENDENT SUBQUERY
  子查询中的第 1 个 SELECT 语句，取决于外面的查询。

只是提供了查询语句的基本信息，不是优化关注的重点。

### table

表示查询的表。没什么好说的。

### type+

表示表的连接类型。优化重要的参考依据！

以下的连接类型的顺序是从最佳类型到最差类型：

- system
  表仅有一行，这是 const 类型的特列，平时不会出现，这个也可以忽略不计。

- **const**
  数据表最多只有一个匹配行，因为只匹配一行数据，所以很快，常用于 PRIMARY KEY 或者 UNIQUE 索引的查询，**可理解为 const 是最优化的。**

- **eq_ref**
  mysql 手册是这样说的:"对于每个来自于前面的表的行组合，从该表中读取一行。这可能是最好的连接类型，除了 const 类型。它用在一个索引的所有部分被联接使用并且索引是 UNIQUE 或 PRIMARY KEY"。
  eq_ref 可以用于使用 = 比较带索引的列。

- ref
  查询条件索引既不是 UNIQUE 也不是 PRIMARY KEY 的情况。ref 可用于 = 或 < 或 > 操作符的带索引的列。

- ref_or_null
  该联接类型如同 ref，但是添加了 MySQL 可以专门搜索包含 NULL 值的行。在解决子查询中经常使用该联接类型的优化。

上面这五种情况都是很理想的索引使用情况。

---

出现下面的任何一种，都表示 sql 需要进行优化了（还可以优化）：

- index_merge
  该联接类型表示使用了索引合并优化方法。在这种情况下，key 列包含了使用的索引的清单，key_len 包含了使用的索引的最长的关键元素。
- unique_subquery
  该类型替换了下面形式的 IN 子查询的 ref： `value IN (SELECT primary_key FROM single_table WHERE some_expr)` 
  unique_subquery 是一个索引查找函数,可以完全替换子查询,效率更高。
- index_subquery
  该联接类型类似于 unique_subquery。可以替换 IN 子查询,但只适合下列形式的子查询中的非唯一索引: `value IN (SELECT key_column FROM single_table WHERE some_expr)`
- range
  只检索给定范围的行,使用一个索引来选择行。
- index
  该联接类型与 ALL 相同,除了只有索引树被扫描。这通常比 ALL 快,因为索引文件通常比数据文件小。
- ALL
  对于每个来自于先前的表的行组合,进行完整的表扫描。**（性能最差）**

最常见的，尽量将 in（not in）替换为 exists（not exists）

### possible_keys

指出 MySQL 能使用哪个索引在该表中找到行。
如果该列为 NULL，说明没有使用索引，可以对该列创建索引来提高性能。

### key

显示 MySQL 实际决定使用的键(索引)。如果没有选择索引，键是 NULL。

可以强制使用索引或者忽略索引：

``` sql
-- 强制忽略
EXPLAIN select * from user IGNORE INDEX(age) where age > 10;
-- 强制使用
EXPLAIN select * from user USE INDEX(age) where age > 10;
```

### key_len

显示 MySQL 决定使用的键长度。如果键是 NULL，则长度为 NULL。

注意：key_len 是确定了 MySQL 将实际使用的索引长度。

### ref

显示使用哪个列或常数与 key 一起从表中选择行。

### rows

显示 MySQL 认为它执行查询时必须检查的行数。

### Extra

该列包含 MySQL 解决查询的详细信息

- Distinct:
  MySQL 发现第 1 个匹配行后,停止为当前的行组合搜索更多的行。
- Not exists:
  MySQL 能够对查询进行 LEFT JOIN 优化,发现 1 个匹配 LEFT JOIN 标准的行后,不再为前面的的行组合在该表内检查更多的行。
- range checked for each record (index map: #):
  MySQL 没有发现好的可以使用的索引,但发现如果来自前面的表的列值已知,可能部分索引可以使用。
- Using filesort:
  MySQL 需要额外的一次传递,以找出如何按排序顺序检索行。
- Using index:
  从只使用索引树中的信息而不需要进一步搜索读取实际的行来检索表中的列信息。
- Using temporary:
  为了解决查询,MySQL需要创建一个临时表来容纳结果。
- Using where:
  WHERE 子句用于限制哪一个行匹配下一个表或发送到客户。
- Using sort_union(...), Using union(...), Using intersect(...):
  这些函数说明如何为 index_merge 联接类型合并索引扫描。
- Using index for group-by:
  类似于访问表的 Using index 方式,Using index for group-by 表示 MySQL 发现了一个索引,可以用来查询 GROUP BY 或 DISTINCT 查询的所有列,而不要额外搜索硬盘访问实际的表。

## 关于索引

索引可以提供查询的速度，但并不是使用了带有索引的字段查询都会生效，有些情况下是不生效的，需要注意！

- 使用 LIKE 关键字的查询
  在使用 LIKE 关键字进行查询的查询语句中，如果匹配字符串的**第一个字符为 “%”，索引不起作用。只有“%”不在第一个位置，索引才会生效。**
- 使用联合索引的查询
  MySQL 可以为多个字段创建索引，一个索引可以包括 16 个字段。
  对于联合索引，只有查询条件中使用了这些字段中**第一个字段时，索引才会生效**。
- 使用 OR 关键字的查询
  查询语句的查询条件中只有 OR 关键字，且 OR 前后的两个条件中的列都是索引时，索引才会生效，否则，索引不生效。
- 如果列类型是字符串，那一定要在条件中将数据使用引号引用起来，否则不会使用索引
- 如果 MySQL 预计使用全表扫描要比使用索引快，则不使用索引

~~还有上面的 in、not in 也不会使用索引。。。除非涉及的列都加了索引~~

## 子查询优化

MySQL从 4.1 版本开始支持子查询，使用子查询进行 SELECT 语句嵌套查询，可以一次完成很多逻辑上需要多个步骤才能完成的 SQL 操作。

**子查询虽然很灵活，但是执行效率并不高。**

执行子查询时，MYSQL 需要创建临时表，查询完毕后再删除这些临时表，所以，子查询的速度会受到一定的影响。

优化：**可以使用连接查询（JOIN）代替子查询，连接查询时不需要建立临时表，其速度比子查询快。**

## 插入数据优化

有时我们需要往数据库批量导入数据，在插入数据时，影响插入速度的主要是索引、唯一性校验、一次插入的数据条数等。

插入数据的优化，不同的存储引擎优化手段不一样，这了说使用最广泛的 InnoDB 引擎。

- 禁用唯一性检查
  唯一性校验会降低插入记录的速度，可以在插入记录之前禁用唯一性检查，插入数据完成后再开启。
  禁用唯一性检查的语句：`SET UNIQUE_CHECKS = 0;`
  开启唯一性检查的语句：`SET UNIQUE_CHECKS = 1;`
- 禁用外键检查
  插入数据之前执行禁止对外键的检查，数据插入完成后再恢复，可以提供插入速度。
  禁用：`SET foreign_key_checks = 0;`
  开启：`SET foreign_key_checks = 1;`
- 禁止自动提交
  插入数据之前执行禁止事务的自动提交，数据插入完成后再恢复，可以提高插入速度。
  禁用：`SET autocommit = 0;`
  开启：`SET autocommit = 1;`

## 其他

[Binlog 介绍](http://laijianfeng.org/2019/03/MySQL-Binlog-%E4%BB%8B%E7%BB%8D/)