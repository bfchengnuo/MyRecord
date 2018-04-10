# Select 语句执行顺序以及如何提高Oracle 基本查询效率

> http://www.cnblogs.com/likeju/p/5039128.html

今天把这几天做的练习复习了一下，不知道自己写得代码执行的效率如何以及要如何提高，于是乎上网开始研究一些材料，现整理如下：

首先，要了解在 Oracle 中 Sql 语句运行的机制。以下是 sql 语句的执行步骤：

1. 语法分析，分析语句的语法是否符合规范，衡量语句中各表达式的意义。
2. 语义分析，检查语句中涉及的所有数据库对象是否存在，且用户有相应的权限。
3. 视图转换，将涉及视图的查询语句转换为相应的对基表查询语句。
4. 表达式转换， 将复杂的 SQL 表达式转换为较简单的等效连接表达式。
5. 选择优化器，不同的优化器一般产生不同的“执行计划”
6. 选择连接方式， ORACLE 有三种连接方式，对多表连接 ORACLE 可选择适当的连接方式。
7. 选择连接顺序， 对多表连接 ORACLE 选择哪一对表先连接，选择这两表中哪个表做为源数据表。
8. 选择数据的搜索路径，根据以上条件选择合适的数据搜索路径，如是选用全表搜索还是利用索引或是其他的方式。
9. 运行“执行计划”。

这里不得不提的是 Oracle 共享原理：

> 将执行过的 SQL 语句存放在内存的共享池(shared buffer pool)中，可以被所有的数据库用户共享当你执行一个 SQL 语句(有时被称为一个游标)时，**如果它和之前的执行过的语句完全相同**， Oracle 就能很快获得已经被解析的语句以及最好的执行路径， 这个功能大大地提高了 SQL 的执行性能并节省了内存的使用。

在了解了 SQL 语句的运行机制与 Oracle 共享原理后，我们可以知道 **SQL 语句的书写方式对 SQL 语句的执行效率有很大的影响。**那么下面我们了解一下 SQL 中 Select 语句中各个关键字执行的顺序。

SQL 语言不同于其他编程语言的最明显特征是处理代码的顺序。

在大多数据库语言中，代码按编码顺序被处理。但在 SQL 语句中，第一个被处理的子句是 FROM，而不是第一出现的 SELECT。

SQL查询处理的步骤序号：

``` sql
(8) SELECT (9) DISTINCT (11) <TOP_specification> <select_list> 
(1) FROM <left_table> 
(3) <join_type> JOIN <right_table> 
(2) ON <join_condition> 
(4) WHERE <where_condition> 
(5) GROUP BY <group_by_list> 
(6) WITH {CUBE | ROLLUP} 
(7) HAVING <having_condition> 
(10) ORDER BY <order_by_list> 
```

**以上每个步骤都会产生一个虚拟表，该虚拟表被用作下一个步骤的输入**。这些虚拟表对调用者(客户端应用程序或者外部查询)不可用。只有最后一步生成的表才会会给调用者。如果没有在查询中指定某一个子句，将跳过相应的步骤。

**逻辑查询处理阶段简介：**

1. FROM：对 FROM 子句中的前两个表执行笛卡尔积(交叉联接)，生成虚拟表VT1。
   表名执行顺序是从后往前，所以数据较少的表尽量放后。
2. ON：对 VT1 应用 ON 筛选器，只有那些使为真才被插入到 TV2。
3. OUTER (JOIN)：如果指定了 OUTER JOIN(相对于CROSS JOIN或INNER JOIN)，保留表中未找到匹配的行将作为外部行添加到 VT2，生成 TV3。
   如果 FROM 子句包含两个以上的表，则对上一个联接生成的结果表和下一个表重复执行步骤1到步骤3，直到处理完所有的表位置。
4. WHERE：对 TV3 应用 WHERE 筛选器，只有使为 true 的行才插入 TV4。
   **执行顺序为从前往后或者说从左到右。**
5. GROUP BY：按 GROUP BY 子句中的列列表对 TV4 中的行进行分组，生成 TV5。
   **执行顺序从左往右分组。**
6. CUTE|ROLLUP：把超组插入 VT5，生成 VT6。
7. HAVING：对 VT6 应用 HAVING 筛选器，只有使为 true 的组插入到 VT7。
   **Having 语句很耗资源，尽量少用**
8. SELECT：处理 SELECT 列表，产生 VT8。
9. DISTINCT：将重复的行从 VT8 中删除，产生 VT9。
10. ORDER BY：将 VT9 中的行按 ORDER BY 子句中的列列表顺序，生成一个游标(VC10)。
    **执行顺序从左到右，是一个很耗资源的语句。**
11. TOP：从 VC10 的开始处选择指定数量或比例的行，生成表 TV11，并返回给调用者。

看到这里，应该是清楚了整个 SQL 语句整个执行的过程，那么我们就接下来进一步要坐得就是在实现功能同时有考虑性能的思想，努力提高 SQL 的执行效率。

## 只返回需要的数据

返回数据到客户端至少需要数据库提取数据、网络传输数据、客户端接收数据以及客户端处理数据等环节，如果返回不需要的数据，就会增加服务器、网络和客户端的无效劳动，其害处是显而易见的，避免这类事件需要注意：  

### 横向来看

1. 不要写 `SELECT *` 的语句，而是选择你需要的字段。
2. 当在 SQL 语句中连接多个表时, 请使用表的别名并把别名前缀于每个 Column上.这样一来,就可以减少解析的时间并减少那些由 Column 歧义引起的语法错误。

### 纵向来看

1. 合理写WHERE子句，不要写没有WHERE的SQL语句。
2. `SELECT TOP N *` --没有 WHERE 条件的用此替代

## 尽量少做重复的工作

- 控制同一语句的多次执行，特别是一些基础数据的多次执行是很多程序员很少注意的。
- 减少多次的数据转换，也许需要数据转换是设计的问题，但是减少次数是程序员可以做到的。
- 杜绝不必要的子查询和连接表，子查询在执行计划一般解释成外连接，多余的连接表带来额外的开销。
- 合并对同一表同一条件的多次UPDATE。
- UPDATE操作不要拆成DELETE操作+INSERT操作的形式，虽然功能相同，但是性能差别是很大的。

## 注意临时表和表变量的用法

在复杂系统中，临时表和表变量很难避免，关于临时表和表变量的用法，需要注意：

- 如果语句很复杂，连接太多，可以考虑用临时表和表变量分步完成。
- 如果需要多次用到一个大表的同一部分数据，考虑用临时表和表变量暂存这部分数据。
- 如果需要综合多个表的数据，形成一个结果，可以考虑用临时表和表变量分步汇总这多个表的数据。
- 其他情况下，应该控制临时表和表变量的使用。
- 关于临时表和表变量的选择，很多说法是表变量在内存，速度快，应该首选表变量，但是在实际使用中发现
  1. 主要考虑需要放在临时表的数据量，在数据量较多的情况下，临时表的速度反而更快。
  2. 执行时间段与预计执行时间(多长)
- 关于临时表产生使用 `SELECT INTO` 和 `CREATE TABLE` +  `INSERT INTO` 的选择，一般情况下，SELECT INTO 会比 CREATE TABLE + INSERT INTO 的方法快很多；
  但是 SELECT INTO 会锁定 TEMPDB 的系统表 SYSOBJECTS、SYSINDEXES、SYSCOLUMNS，在多用户并发环境下，容易阻塞其他进程；
  **所以我的建议是，在并发系统中，尽量使用 CREATE TABLE + INSERT INTO，而大数据量的单个语句使用中，使用SELECT INTO。**

## 注意子查询的用法

子查询是一个 SELECT 查询，它嵌套在 SELECT、INSERT、UPDATE、DELETE 语句或其它子查询中。

任何允许使用表达式的地方都可以使用子查询，子查询可以使我们的编程灵活多样，可以用来实现一些特殊的功能。

但是在性能上，往往一个不合适的子查询用法会形成一个性能瓶颈。如果子查询的条件中使用了其外层的表的字段，这种子查询就叫作相关子查询。

相关子查询可以用 IN、NOT IN、EXISTS、NOT EXISTS引入。 关于相关子查询，应该注意：

1. NOT IN、NOT EXISTS 的相关子查询可以改用 LEFT JOIN 代替写法。

   ``` sql
   -- 比如：
   SELECT PUB_NAME
   FROM PUBLISHERS
   WHERE PUB_ID NOT IN (SELECT PUB_ID FROM TITLES WHERE TYPE = 'BUSINESS')

   -- 可以改写成：
   SELECT A.PUB_NAME
   FROM PUBLISHERS A LEFT JOIN TITLES B ON B.TYPE = 'BUSINESS' AND A.PUB_ID=B. PUB_ID
   WHERE B.PUB_ID IS NULL

   -----------------------------------
   SELECT TITLE
   FROM TITLES
   WHERE NOT EXISTS (SELECT TITLE_ID FROM SALES WHERE TITLE_ID = TITLES.TITLE_ID)

   -- 可以改写成：
   SELECT TITLE
   FROM TITLES LEFT JOIN SALES ON SALES.TITLE_ID = TITLES.TITLE_ID
   WHERE SALES.TITLE_ID IS NULL
   ```

2. 如果保证子查询没有重复 ，IN、EXISTS 的相关子查询可以用 INNER JOIN 代替。

   ``` sql
   -- 比如：
   SELECT PUB_NAME
   FROM PUBLISHERS
   WHERE PUB_ID IN (SELECT PUB_ID FROM TITLES
   WHERE TYPE = 'BUSINESS')

   -- 可以改写成：
   SELECT DISTINCT A.PUB_NAME
   FROM PUBLISHERS A INNER JOIN TITLES B ON B.TYPE = 'BUSINESS' AND A.PUB_ID=B. PUB_ID
   ```

3.  IN 的相关子查询用 EXISTS 代替

   ``` sql
   -- 比如
   SELECT PUB_NAME
   FROM PUBLISHERS
   WHERE PUB_ID IN (SELECT PUB_ID FROM TITLES WHERE TYPE = 'BUSINESS')

   -- 可以用下面语句代替：
   SELECT PUB_NAME
   FROM PUBLISHERS
   WHERE EXISTS (SELECT 1 FROM TITLES WHERE TYPE = 'BUSINESS' AND PUB_ID= PUBLISHERS.PUB_ID) 
   ```

4. 不要用 COUNT(*) 的子查询判断是否存在记录，最好用 LEFT JOIN 或者 EXISTS

   ``` sql
   -- 比如有人写这样的语句：
   SELECT JOB_DESC
   FROM JOBS
   WHERE (SELECT COUNT(*) FROM EMPLOYEE WHERE JOB_ID=JOBS.JOB_ID)=0

   -- 应该改成：
   SELECT JOBS.JOB_DESC
   FROM JOBS LEFT JOIN EMPLOYEE ON EMPLOYEE.JOB_ID=JOBS.JOB_ID
   WHERE EMPLOYEE.EMP_ID IS NULL

   ------------------------------------
   SELECT JOB_DESC FROM JOBS WHERE (SELECT COUNT(*) FROM EMPLOYEE WHERE JOB_ID=JOBS.JOB_ID)<>0

   -- 应该改成：
   SELECT JOB_DESC FROM JOBS
   WHERE EXISTS (SELECT 1 FROM EMPLOYEE WHERE JOB_ID=JOBS.JOB_ID)  
   ```

## 尽量使用索引,并注意对含索引列的运算

建立索引后，并不是每个查询都会使用索引，在使用索引的情况下，索引的使用效率也会有很大的差别。

只要我们在查询语句中没有强制指定索引，索引的选择和使用方法是 SQLSERVER 的优化器自动作的选择，而它选择的根据是查询语句的条件以及相关表的统计信息，这就要求我们在写 SQL 语句的时候尽量使得优化器可以使用索引。

为了使得优化器能高效使用索引，写语句的时候应该注意：

- 不要对索引字段进行运算，而要想办法做变换。
  比如 `SELECT ID FROM T WHERE NUM/2=100`
  应改为:
  `SELECT ID FROM T WHERE NUM=100*2`

- 不要对索引字段进行格式转换。
  日期字段的例子：`WHERE CONVERT(VARCHAR(10), 日期字段,120)='2010-07-15'`
  应该改为：`WHERE日期字段〉='2010-07-15' AND 日期字段<'2010-07-16' IS NULL`。
  转换的例子：

  ``` sql
  WHERE ISNULL(字段,'')<>''  --应改为:WHERE字段<>'
  WHERE ISNULL(字段,'')=''   --不应修改
  WHERE ISNULL(字段,'F') ='T'  --应改为: WHERE字段='T'
  WHERE ISNULL(字段,'F')<>'T'  --不应修改
  ```

- 不要对索引字段使用函数。
  `WHERE LEFT(NAME, 3)='ABC'` 或者 `WHERE SUBSTRING(NAME,1, 3)='ABC'`

  应改为: `WHERE NAME LIKE 'ABC%'`。
  日期查询的例子：

  ``` sql
  WHERE DATEDIFF(DAY, 日期,'2010-06-30')=0
  -- 应改为:WHERE 日期>='2010-06-30' AND 日期 <'2010-07-01'

  WHERE DATEDIFF(DAY, 日期,'2010-06-30')>0
  -- 应改为:WHERE 日期 <'2010-06-30'

  WHERE DATEDIFF(DAY, 日期,'2010-06-30')>=0
  -- 应改为:WHERE 日期 <'2010-07-01'

  WHERE DATEDIFF(DAY, 日期,'2010-06-30')<0
  -- 应改为:WHERE 日期>='2010-07-01'

  WHERE DATEDIFF(DAY, 日期,'2010-06-30')<=0
  -- 应改为:WHERE 日期>='2010-06-30'
  ```

- 不要对索引字段进行多字段连接。
  比如：`WHERE FAME+ '. '+LNAME='HAIWEI.YANG'`
  应改为：`WHERE FNAME='HAIWEI' AND LNAME='YANG'`

## 注意多表连接的连接条件的选择与表示

多表连接的连接条件对索引的选择有着重要的意义，所以我们在写连接条件条件的时候需要特别注意。

- 多表连接的时候，连接条件必须写全，宁可重复，不要缺漏。
- 连接条件尽量使用聚集索引
- 注意 ON、WHERE 和 HAVING 部分条件的区别：
  ON 是最先执行， WHERE 次之，HAVING 最后；
  因为 ON 是先把不符合条件的记录过滤后才进行统计，它就可以减少中间运算要处理的数据，按理说应该速度是最快的；
  WHERE 也应该比 HAVING 快点的，因为它过滤数据后才进行 SUM，在两个表联接时才用 ON 的；
  所以在一个表的时候，就剩下 WHERE 跟 HAVING 比较了。

---

1. 考虑联接优先顺序：
2. INNER JOIN
3. LEFT JOIN (注：RIGHT JOIN 用 LEFT JOIN 替代)
4. CROSS JOIN

其它注意和了解的地方有：

- 在 IN 后面值的列表中，将出现最频繁的值放在最前面，出现得最少的放在最后面，减少判断的次数
- 注意 UNION 和 UNION ALL 的区别。--允许重复数据用 UNION ALL 好
- **注意使用 DISTINCT，在没有必要时不要用**
- TRUNCATE TABLE 与 DELETE 区别
- 减少访问数据库的次数

还有就是我们写存储过程，如果比较长的话，最后用标记符标开，因为这样可读性很好，即使语句写的不怎么样但是语句工整。如：

``` sql
--startof 查询在职人数
sql语句
--end of
```

正式机器上我们一般不能随便调试程序，但是很多时候程序在我们本机上没问题，但是进正式系统就有问题，但是我们又不能随便在正式机器上操作，那么怎么办呢?

我们可以用回滚来调试我们的存储过程或者是 sql 语句，从而排错。

``` sql
BEGIN TRAN
UPDATE a SET 字段='  '
ROLLBACK
```

作业存储过程可以加上下面这段，这样检查错误可以放在存储过程，如果执行错误回滚操作，但是如果程序里面已经有了事务回滚，那么存储过程就不要写事务了，这样会导致事务回滚嵌套降低执行效率，但是我们很多时候可以把检查放在存储过程里，这样有利于我们解读这个存储过程和排错。

``` sql
BEGIN TRANSACTION
-- 事务回滚开始
--检查报错
  IF ( @@ERROR > 0 )
    BEGIN
    --回滚操作
    ROLLBACK TRANSACTION
    RAISERROR('删除工作报告错误', 16, 3)
    RETURN
    END
--结束事务
COMMIT TRANSACTION 
```

## 有效使用Decode函数

使用 Decode 函数可以有效避免重复扫描相同数据或重复连接相同表。