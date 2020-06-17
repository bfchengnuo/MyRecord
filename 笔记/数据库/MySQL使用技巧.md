发现对 MySQL 的了解真的就是皮毛，虽然我真的比较讨厌 DB

但为了效率，还是得学是不

## MySQL中避免重复插入的方法

这种场景非常常见，对于一条数据（有 id 信息），我们不知道它在数据库是否还存在，一般正常方案是先 select 查一下，如果没有就新增，如果有就更新。

这个操作可以一步完成，涉及的相关语法有：

### ignore

 如果是用**主键 primary 或者唯一索引 unique 区分了记录的唯一性**，避免重复插入记录可以使用 ignore：

``` sql
INSERT IGNORE INTO `table_name` (`email`, `phone`, `user_id`) 
VALUES ('test9@163.com', '99999', '9999');

-- 复制表,避免重复记录：
INSERT IGNORE INTO `table_1` (`name`) SELECT `name` FROM `table_2`;
```

 这样当有重复记录就会忽略，执行后返回数字 0 

 IGNORE 只关注主键对应记录是不存在，无则添加，有则忽略。 

### Replace

REPLACE 的运行与 INSERT 很相像，但是如果旧记录与新记录有相同的值，则在新记录被插入之前，旧记录被删除，即：

尝试把新行插入到表中，当因为对于主键或唯一关键字出现重复关键字错误而造成插入失败时：从表中删除含有重复关键字值的冲突行，再次尝试把新行插入到表中；

旧记录与新记录有相同的值的判断标准就是：表有一个 PRIMARY KEY 或 UNIQUE 索引，否则，使用一个 REPLACE 语句没有意义。

该语句会与 INSERT 相同，因为没有索引被用于确定是否新行复制了其它的行。

``` sql
REPLACE INTO `table_name` (`email`, `phone`, `user_id`) 
VALUES ('test569', '99999', '123');
```

REPLACE 语句会返回一个数，来指示受影响的行的数目。该数是被删除和被插入的行数的和。

受影响的行数可以容易地确定是否 REPLACE 只添加了一行，或者是否 REPLACE 也替换了其它行：检查该数是否为1（添加）或更大（替换，删除+新增就轻易大于 1 了）。 

### ON DUPLICATE KEY UPDATE 

你也可以在 `INSERT INTO…..` 后面加上 `ON DUPLICATE KEY UPDATE` 方法来实现。如果您指定了 `ON DUPLICATE KEY UPDATE`，并且插入行后会导致在一个 UNIQUE 索引或 PRIMARY KEY 中出现重复值，则执行旧行 UPDATE。

``` sql
-- 如果列 a 被定义为 UNIQUE，并且包含值 1
INSERT INTO `table` (`a`, `b`, `c`) VALUES (1, 2, 3) 
ON DUPLICATE KEY UPDATE `c`=`c`+1;
-- 等价于
UPDATE `table` SET `c`=`c`+1 WHERE `a`=1;

-- 如果列 b 也是唯一列，则 INSERT 与此 UPDATE 语句相当：
UPDATE `table` SET `c`=`c`+1 WHERE `a`=1 OR `b`=2 LIMIT 1;
```

 如果行作为新记录被插入，则受影响行的值为 1；如果原有的记录被更新，则受影响行的值为 2。 

第二句中，如果 `a=1 OR b=2` 与多个行向匹配，**则只有一个行被更新**。通常，您应该尽量避免对带有多个唯一关键字的表使用 ON DUPLICATE KEY 子句。 

``` sql
INSERT INTO `table` (`a`, `b`, `c`) VALUES (1, 2, 3), (4, 5, 6) 
ON DUPLICATE KEY UPDATE `c`=VALUES(`a`)+VALUES(`b`);

-- 等价于
INSERT INTO `table` (`a`, `b`, `c`) VALUES (1, 2, 3) ON DUPLICATE KEY UPDATE `c`=3; 
INSERT INTO `table` (`a`, `b`, `c`) VALUES (4, 5, 6) ON DUPLICATE KEY UPDATE c=9;

NSERT INTO `class` SELECT * FROM `class1` 
ON DUPLICATE KEY UPDATE `class`.`course`=`class1`.`course`
```

 当您使用 ON DUPLICATE KEY UPDATE 时，DELAYED 选项被忽略。 

### 效率比较

主要比较的是后两种方式：

在数据库数据量很少的时候，这两种方式都很快，无论是直接的插入还是有冲突时的更新，都不错，但在数据库表的内容数量比较大（如百万级）的时候，两种方式就不太一样了 。

首先是直接的插入操作，两种的插入效率都略低， 在向大数据表批量插入数据的时候，每次的插入都要维护索引的，索引固然可以提高查询的效率，但在更新表尤其是大表的时候，索引就成了一个不得不考虑的问题了。 

其次是更新表，这里的更新的时候是带主键值的，**replace 的操作要比 insert on duplicate 的操作低太多太多**，replace 慢的原因，要先删除旧的，然后插入新的，在这个过程中，还要重新维护索引，所以速度慢；

但为何 insert　on duplicate的更新却那么快呢。insert on duplicate 的更新操作虽然也会更新数据，**但其对主键的索引却不会有改变**（如果更新的字段包括主键，那就要另说了）。

另外，大量更新时，开启事务，处理完一批再提交会提高速度。

### 锁

> 读锁又称为共享锁，简称 S 锁，顾名思义，共享锁就是多个事务对于同一数据可以共享一把锁，都能访问到数据，但是只能读不能修改。
>
> 写锁又称为排他锁，简称 X 锁，顾名思义，排他锁就是不能与其他所并存，如一个事务获取了一个数据行的排他锁，其他事务就不能再获取该行的其他锁，包括共享锁和排他锁，但是获取排他锁的事务是可以对数据就行读取和修改。

默认的修改数据语句，`update,delete,insert` 都会自动给涉及到的数据加上排他锁，`select` 语句默认不会加任何锁类型。

如果加排他锁可以使用 `select ...for update` 语句，加共享锁可以使用 `select ... lock in share mode` 语句。

#### for update

有些需求需要在查询的时候进行加锁，保证数据的准确，常见的有悲观锁：

``` sql
select stock
from product
where id = 12
for update;
```

最重要的是最后面的那句，特别注意的是 where 条件 id（本例）最好是主键，否则就会进行锁表；如果能保证查询结果唯一，那只会锁行。也就是说，明确主键的情况会产生行锁；无明确主键会产生表锁。

另一种是乐观锁的方式，就是增加一列表示 version，可以使用毫秒数。

---

for update 是一种行级锁，又叫排它锁，一旦用户对某个行施加了行级加锁，则该用户可以查询也可以更新被加锁的数据行，其它用户只能查询但不能更新被加锁的数据行．

如果其它用户想更新该表中的数据行，则也必须对该表施加行级锁．即使多个用户对一个表均使用了共享更新，但也不允许两个事务同时对一个表进行更新，真正对表进行更新时，是以独占方式锁表，一直到提交或复原该事务为止。行锁永远是独占方式锁。

只有当出现如下之一的条件，才会释放共享更新锁：

1. 执行提交（COMMIT）语句
2. 退出数据库（LOG　OFF）
3. 程序停止运行

这就是所谓的手工加锁语句。在数据库中执行 `select … for update` ，大家会发现会对数据库中的表或某些行数据进行锁表，在 mysql 中，**如果查询条件带有主键，会锁行数据，如果没有，会锁表**。

由于 InnoDB 预设是 Row-Level Lock，所以只有「明确」的指定主键，MySQL 才会执行 Row lock (只锁住被选取的资料例) ，否则 MySQL 将会执行 Table Lock (将整个资料表单给锁住)。

---

参考：

https://github.com/bfchengnuo/MyRecord/issues/22#issuecomment-424749698

https://mp.weixin.qq.com/s/hOdEMgRqjZAg1ND5nqwFQA

https://www.cnblogs.com/banma/p/11797560.html

#### lock in share mode

用法跟 for update 一样，说下它的效果；

使用其语法后，无锁查 OK，共享查 OK，排它锁（for update）No。

### 动态SQL的运用

起因：

mysql 中，当你在 trigger、function 中编写动态的 sql 时，编译时就会出现 `"Error 1336: Dynamic SQL is not allowed in stored function or trigger"`

- trigger：触发器，在执行动作前后触发

- function：方法，函数，可以在 SQL、存储过程中调用

- procedure：存储过程，可以编写比较复杂的逻辑

- 动态的 SQL 语句：就是单纯的 SQL 语句中，含有变量。

出现上述异常的原因就是:

1. 在 function 或者是 trigger 里面含有并执行了动态的 SQL 语句

2. 在 function 或者是 trigger 里调用了含有动态 SQL 语句的存储过程

**若单独在存储过程中执行动态的 SQL 语句是不会报错的，是允许的。**

function、trigger 还是不支持动态 sql 语句，你硬要将动态语句写入 trigger、function 的话，我建议可以考虑写在存储过程中，然后用程序去调用存储过程.

### 参考

http://blog.sae.sina.com.cn/archives/3491

https://segmentfault.com/a/1190000002527333