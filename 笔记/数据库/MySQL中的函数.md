# MySQL中的函数

## 常用函数

不定期更新，用到来记一下，缓慢整理

###  FIND_IN_SET

 MySQL 提供了一个名为 `FIND_IN_SET()` 的内置字符串函数，允许您在逗号分隔的字符串列表中查找指定字符串的位置。 

``` sql
FIND_IN_SET(needle,haystack);

-- 示例
SELECT FIND_IN_SET('y','x,y,z');
-- where 条件中
SELECT 
    name, belts
FROM
    divisions
WHERE
    FIND_IN_SET('red', belts);
```

`FIND_IN_SET()` 函数接受两个参数：

- 第一个参数 `needle` 是要查找的字符串。
- 第二个参数 `haystack` 是要搜索的逗号分隔的字符串列表。

`FIND_IN_SET()` 函数根据参数的值返回一个整数或一个 `NULL` 值：

- 如果 `needle` 或 `haystack` 为 `NULL`，则函数返回 `NULL` 值。
- 如果 `needle` 不在 `haystack` 中，或者 `haystack` 是空字符串，则返回零。
- 如果 `needle` 在 `haystack` 中，则返回一个正整数。

请注意，如果 `needle` 包含逗号( `，` )，该函数将无法正常工作。 此外，如果 `needle` 是一个常量字符串，而且 `haystack` 是一个类型为 `SET` 的列，MySQL 将使用位算术优化。

## 自定义函数

今天遇到的一个需求，之前因为要做权限控制，并且它这个设计好像是有问题的，通过简单的 sql 连接无法完全过滤，最开始的方案，我配合 J8 点 stream 流进行了 3 次过滤才完成，我就称它为二次过滤吧。

但是呢，后来发现一个问题，这样办的话，分页就难弄了，仔细想了想也没想到有什么好的解决方案，基本是个死局。

想要解决，也就只能从 SQL 上下手，于是我想着把 Java 代码转换为 Mysql 的函数，额效率啥的先不考虑了。。。。

下面是一个模版：

``` sql
CREATE DEFINER=`skip-grants user`@`skip-grants host` FUNCTION `isMatchRange`(S_IDS VARCHAR(3000), pid bigint) RETURNS int(1)
BEGIN
	-- 定义变量
  DECLARE s int DEFAULT 0;
	DECLARE PID_C_ID BIGINT;
	DECLARE flag INT DEFAULT 0;
	
	-- 定义游标，并将sql结果集赋值到游标中
	DECLARE report CURSOR FOR SELECT ROW_ID FROM sm_group where GROUP_PID = pid and DELETED_FLAG='0';
	-- 声明当游标遍历完后将标志变量置成某个值
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET s=1;

	-- 打开游标
	open report;
		-- 将游标中的值赋值给变量，注意：变量名不要和返回的列名同名，变量顺序要和sql结果列的顺序一致
		fetch report into PID_C_ID;
		-- 当s不等于1，也就是未遍历完时，会一直循环
		while s<>1 do
			-- 执行业务逻辑-使用 find_in_set 函数判断 PID_C_ID 是否存在于 S_IDS 中
			IF !find_in_set(PID_C_ID, S_IDS) THEN
				SET flag = 1;
			END IF;
			
			-- 将游标中的值再赋值给变量，供下次循环使用
			fetch report into PID_C_ID;
		end while;
	-- 关闭游标
	close report;

	RETURN flag;
END
```

我感觉基本上很全了，判断循环变量赋值都有了，差点都忘记游标这回事了。