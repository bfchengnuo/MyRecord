# PostgreSQL笔记

PostgreSQL 是一个免费的开源的 对象-关系数据库服务器（ORDBMS），在灵活的 BSD 许可证下发行。
PostgreSQL 开发者把它念作 **post-gress-Q-L**。
PostgreSQL 的 Slogan 是 "世界上最先进的开源关系型数据库"。

> PostgreSQL 可以说是目前功能最强大、特性最丰富和结构最复杂的开源数据库管理系统，其中有些特性甚至连商业数据库都不具备。这个起源于加州大学伯克利分校的数据库，现已成为一项国际开发项目，并且拥有广泛的用户群，尤其是在海外，目前国内使用者也越来越多。
>
> PostgreSQL 基本上算是见证了整个数据库理论和技术的发展历程，由 UCB 计算机教授 Michael Stonebraker 于 1986 年创建。在此之前，Stonebraker 教授主导了关系数据库 Ingres 研究项目，88 年，提出了 Postgres 的第一个原型设计。
>
> **MySQL 号称是使用最广泛的开源数据库，而 PG 则被称为功能最强大的开源数据库。**

特性：

- **函数**：通过函数，可以在数据库服务器端执行指令程序。
  一个内置的名为 PL/pgSQL 的过程语言，类似于 Oracle 的 PL/SQL；
  包括 PL/Perl，PL/PHP，PL/Python，PL/Ruby，PL/sh，PL/Tcl 与 PL/Scheme 在内的脚本语言；
  编译语言：C，C++，或 Java（通过 PL/Java）。
  R 统计语言（PL/R）。
- **索引**：用户可以自定义索引方法，或使用内置的 B 树，哈希表与 GiST 索引。
- **触发器**：触发器是由 SQL 语句查询所触发的事件。
  如：一个 INSERT 语句可能触发一个检查数据完整性的触发器。
  触发器通常由 INSERT 或 UPDATE 语句触发。 
  多版本并发控制：PostgreSQL 使用多版本并发控制（MVCC，Multiversion concurrency control）系统进行并发控制，该系统向每个用户提供了一个数据库的"快照"，用户在事务内所作的每个修改，对于其他的用户都不可见，直到该事务成功提交。
- **规则**：规则（RULE）允许一个查询能被重写，通常用来实现对视图（VIEW）的操作，如插入（INSERT）、更新（UPDATE）、删除（DELETE）。
- **数据类型**：包括文本、任意精度的数值数组、JSON 数据、枚举类型、XML 数据等。
- **全文检索**：通过 Tsearch2 或 OpenFTS，8.3版本中内嵌 Tsearch2。
- **NoSQL**：JSON，JSONB，XML，HStore 原生支持，至 NoSQL 数据库的外部数据包装器。
- **数据仓库**：能平滑迁移至同属 PostgreSQL 生态的 GreenPlum，DeepGreen，HAWK 等，使用 FDW 进行 ETL。

安装过程就省略了，  正常情况下，安装完成后，PostgreSQL 服务器会自动在本机的 5432 端口开启，图形化管理界面可以尝试 pgadmin。

自从 MySQL 被 Oracle 收购以后，PostgreSQL 逐渐成为开源关系型数据库的首选。

## 基本使用

初次安装后（以 Linux 为例），默认生成一个名为 postgres 的数据库和一个名为 postgres 的数据库用户。这里需要注意的是，同时还生成了一个名为 postgres 的 Linux 系统用户。
使用 postgres 用户，来生成其他用户和新数据库。好几种方法可以达到这个目的，这里介绍两种。
**第一种方法，使用PostgreSQL控制台。**

``` shell
# 新建一个 Linux 新用户，这里为 dbuser
sudo adduser dbuser
# 然后，切换到postgres用户。
sudo su - postgres
# 下一步，使用 psql 命令登录 PostgreSQL 控制台。
# 这是不用输入密码的。如果一切正常，系统提示符会变为 "postgres=#"
psql

# 为 postgres 用户设置一个密码
> \password postgres
# 创建数据库用户 dbuser，并设置密码
> CREATE USER dbuser WITH PASSWORD 'password';
# 创建用户数据库，这里为exampledb，并指定所有者为dbuser
> CREATE DATABASE exampledb OWNER dbuser;
# 将 exampledb 数据库的所有权限都赋予 dbuser，否则 dbuser 只能登录控制台，没有任何数据库操作权限
> GRANT ALL PRIVILEGES ON DATABASE exampledb to dbuser;

# 使用 \q 命令退出控制台（也可以直接按 ctrl+D）
```

 **第二种方法，使用shell命令行**

``` shell
# 创建数据库用户 dbuser，并指定其为超级用户
sudo -u postgres createuser --superuser dbuser
# 登录数据库控制台，设置 dbuser 用户的密码，完成后退出控制台
sudo -u postgres psql
> \password dbuser
> \q

# 在 shell 命令行下，创建数据库 exampledb，并指定所有者为 dbuser
sudo -u postgres createdb -O dbuser exampledb
```

 因为 PostgreSQL 提供了命令行程序 createuser 和 createdb，所以可以通过 shell 来进行操作。
最后补充一下控制台命令：

``` shell
\h：查看SQL命令的解释，比如\h select。
\?：查看psql命令列表。
\l：列出所有数据库。
\c [database_name]：连接其他数据库。
\d：列出当前数据库的所有表格。
\d [table_name]：列出某一张表格的结构。
\du：列出所有用户。
\e：打开文本编辑器。
\conninfo：列出当前数据库和连接的信息。
```

当前登陆名与数据库用户名相同，并且数据库名与用户名相同时可以直接使用  psql  快速连接数据库。

## 数据库基本操作

基本的 CRUD 操作演示：

``` shell
psql -U dbuser -d exampledb -h 127.0.0.1 -p 5432

# 创建新表
CREATE TABLE user_tbl(name VARCHAR(20), signup_date DATE);

# 插入数据
INSERT INTO user_tbl(name, signup_date) VALUES('张三', '2013-12-22');

# 选择记录
SELECT * FROM user_tbl;

# 更新数据
UPDATE user_tbl set name = '李四' WHERE name = '张三';

# 删除记录
DELETE FROM user_tbl WHERE name = '李四' ;

# 添加栏位
ALTER TABLE user_tbl ADD email VARCHAR(40);

# 更新结构
ALTER TABLE user_tbl ALTER COLUMN signup_date SET NOT NULL;

# 更名栏位
ALTER TABLE user_tbl RENAME COLUMN signup_date TO signup;

# 删除栏位
ALTER TABLE user_tbl DROP COLUMN email;

# 表格更名
ALTER TABLE user_tbl RENAME TO backup_tbl;

# 删除表格
DROP TABLE IF EXISTS backup_tbl;
```

## 数据库与模式

 简单来说模式 (Schema) 就是对数据库 (Database) 的逻辑分割，而且在数据库创建的时候，已经默认创建了一个 public 模式，**在此数据库中创建的对象，如表、函数、试图、索引、序列等都保存在这个模式中**。 
 也就是说，数据库通过模式做逻辑区分，而且一个数据库至少包含一个模式，接到一个数据库后，可以通过 `search_path` 设置搜索顺序。 

## 数据库与表空间

在通过 `CREATE DATABASE dbname` 语句创建数据库时，默认的数据库所有者是当前创建数据库的角色，默认表空间是系统的默认表空间 pg_default ，其主要原因是创建是通过克隆数据库模板实现的。 
如上创建数据库时，如果没有指明数据库模板，系统将默认克隆 template1 数据库，其默认表空间是 pg_default 。
链接查看新数据库时，实际上存在一个表，而且有上述写入的数据。表空间是一个存储区域，在一个表空间中可以存储多个数据库，尽管 PostgreSQL 不建议这么做，例如将索引保存到 SSD 中，而数据保存到 SATA 中。 

# 与MySQL对比

对比 MySQL 的优势：

- 在 SQL 的标准实现上要比 MySQL 完善，而且功能实现比较严谨；
- 存储过程的功能支持要比 MySQL 好，具备本地缓存执行计划的能力；
- 对表连接支持较完整，优化器的功能较完整，支持的索引类型很多，复杂查询能力较强；
- PG 主表采用堆表存放，MySQL 采用索引组织表，能够支持比 MySQL 更大的数据量。
- PG 的主备复制属于物理复制，相对于 MySQL 基于 binlog 的逻辑复制，数据的一致性更加可靠，复制性能更高，对主机性能的影响也更小。
- MySQL 的存储引擎插件化机制，存在锁机制复杂影响并发的问题，而 PG 不存在。
- 对运维人员非常友好
- 子查询，视图优化，性能比较高

当然不可能是完全的优势，要不然 MySQL 就没有存在的必要了，那么 MySQL 的优势在于：

- innodb 的基于回滚段实现的 MVCC 机制，相对 PG 新老数据一起存放的基于 XID 的 MVCC 机制，是占优的。~~因此 MySQL 的速度是高于 PG 的；~~

- MySQL 采用索引组织表，这种存储方式非常适合基于主键匹配的查询、删改操作，但是对表结构设计存在约束；

- MySQL 的优化器较简单，系统表、运算符、数据类型的实现都很精简，非常适合简单的查询操作；

- MySQL 分区表的实现要优于 PG 的基于继承表的分区实现，主要体现在分区个数达到上千上万后的处理性能差异较大。

PG 具备更高的**可靠性**，对数据一致性完整性的支持高于 MySQL，因此 PG 更加适合严格的企业应用场景（比如金融、电信、ERP、CRM）；

而 MySQL 查询速度较快，更加适合业务逻辑相对简单、数据可靠性要求较低的互联网场景（比如 google、facebook、alibaba）。

[去 O 唯有 PG](https://mp.weixin.qq.com/s/hieS4AScOyUpiyMBI0sG7w)

## 参考

https://www.runoob.com/postgresql/postgresql-tutorial.html
http://www.ruanyifeng.com/blog/2013/12/getting_started_with_postgresql.html 
https://jin-yang.github.io/post/postgresql-introduce.html