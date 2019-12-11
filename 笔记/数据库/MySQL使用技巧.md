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

### 参考

http://blog.sae.sina.com.cn/archives/3491

https://segmentfault.com/a/1190000002527333