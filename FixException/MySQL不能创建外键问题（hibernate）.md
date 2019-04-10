## 问题描述

今天在学 jpa 的时候使用 hibernate 作为 ORM 实现，然后定义了一对多的双向关联，自动建表后发现并没有创建出外键，倒是有一个索引，虽然功能并不是很影响

## 原因分析

首先，MySQL 创建外键需要几个条件：
1. 必须使用 innodb 表引擎
2. 两个表的字符编码必须一致
3. 被引用的 typeid，即父表中的 typeid 必须是主键或者唯一建

于是我核对了下 hibernate 给我创建的表引擎，竟然是 Myisam ....

## 解决

问题应该在方言的配置上，当时我配置的是：`org.hibernate.dialect.MySQL5Dialect` 感觉是很对的，5 版本够新了...结果默认创建的表即为 Myisam

于是我发现还有个更高的： `org.hibernate.dialect.MySQL57Dialect` 使用这个默认创建的表即为 innodb

就是 5 和 57 的区别.....

## InnoDB与Myisam的区别

InnoDB 和 MyISAM 是 MySQL 最重要的两种数据存储引擎，两者都可用来存储表和索引，各有优缺点，视具体应用而定。

基本的差别为：

- MyISAM 类型不支持事务处理等高级处理，而 InnoDB 类型支持。

- MyISAM 类型的表强调的是性能，其执行数度比 InnoDB 类型更快，但是不提供事务支持，而 InnoDB 提供事务支持以及外部键等高级数据库功能。

InnoDB 给 MySQL 提供了具有事务(commit)、回滚(rollback)和崩溃修复能力(crash recovery capabilities)、多版本并发控制(multi-versioned concurrency control)的事务安全(transaction-safe (ACID compliant))型表。

InnoDB 提供了行级锁(locking on row level)，提供与 Oracle 类似的不加锁读取(non-lockingread in SELECTs)

另外 **InnoDB 是为处理巨大数据量时的最大性能设计**。它的 CPU 效率可能是任何其它基于磁盘的关系数据库引擎所不能匹敌的。MySQLInnoDBDialect 基于上也就有 InnoDB 相同的功能.InnoDB 表可以是任何尺寸，即使在文件尺寸被限制为2GB的操作系统上。

如果你的数据执行大量的INSERT或UPDATE,出于性能方面的考虑，应该使用 InnoDB 表。

---

**MyISAM 是 MySQL 默认存储引擎**。每个 MyISAM 在磁盘上存储成三个文件。第一个文件的名字以表的名字开始，扩展名指出文件类型。`.frm` 文件存储表定义。数据文件的扩展名为 `.MYD`  (MYData)。索引文件的扩展名是 `.MYI` (MYIndex)。

MyISAM 基于传统的 ISAM 类型,ISAM 是 Indexed Sequential Access Method (有索引的顺序访问方法) 的缩写,它是存储记录和文件的标准方法.与其他存储引擎比较,MyISAM 具有检查和修复表格的大多数工具. **MyISAM 表格可以被压缩,而且它们支持全文搜索.它们不是事务安全的,而且也不支持外键。**如果事物回滚将造成不完全回滚，不具有原子性。**如果执行大量的 SELECT，MyISAM 是更好的选择。**

---

那么下面就来了解下它们的详细区别吧：

1. InnoDB 不支持 FULLTEXT 类型的索引。 

2. InnoDB 中不保存表的具体行数，也就是说，执行 `select count(*) from table` 时，InnoDB 要扫描一遍整个表来计算有多少行，但是 MyISAM 只要简单的读出保存好的行数即可。注意的是，当 count(*) 语句包含 where 条件时，两种表的操作是一样的。

3. 对于 **AUTO_INCREMENT** 类型的字段，InnoDB 中必须包含只有该字段的索引，但是在 MyISAM 表中，可以和其他字段一起建立联合索引。

4. `DELETE FROM table` 时，InnoDB 不会重新建立表，而是一行一行的删除。

5. `LOAD TABLE FROM MASTER` 操作对 InnoDB 是不起作用的，解决方法是首先把 InnoDB 表改成 MyISAM 表，导入数据后再改成 InnoDB 表，但是对于使用的额外的 InnoDB 特性（例如外键）的表不适用。

另外，InnoDB 表的行锁也不是绝对的，如果在执行一个 SQL 语句时 MySQL 不能确定要扫描的范围，InnoDB 表同样会锁全表，例如 `update table set num=1 where name like “%aaa%”`

任何一种表都不是万能的，只用恰当的针对业务类型来选择合适的表类型，才能最大的发挥MySQL的性能优势。

ps: MySQL 的另两种存储引擎，MEMORY 和 MERGE 

## 参考

http://www.cnblogs.com/jasonHome/p/5929451.html

https://my.oschina.net/junn/blog/183341
