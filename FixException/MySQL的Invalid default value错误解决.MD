今天在还原数据库的时候给我抛了这样一个错误：

> Invalid default value for 'upload_time'

因为我是用 Navicat Premium 进行还原的，在我同学的 PC 上用 Navicat for MySQL 进行还原是可以的，所以我以为是版本的问题，后来换了 Navicat for MySQL 依然无果

还搜到了一堆说**将 DATETIME 改为 TIMESTAMP**；对于我来说这就是误导....

## 解决

### 原因：

最终发现是 MySQL 版本的问题，我所用的版本太高了，在高版本中（从 5.6.17 这个版本开始）就默认设置了**不允许插入 0 日期了**；
术语是 `NO_ZERO_IN_DATE` 、 `NO_ZERO_DATE`

可以在 MySQL 的命令行中使用下面的命令进行查询：
`SHOW VARIABLES LIKE 'sql_mode';`
看看有木有 ：NO_ZERO_IN_DATE,NO_ZERO_DATE 这两个参数，这两个参数限制不能为 0

### 方案1

这是我采用的，实测有效！
在命令行或者 Navicat 下新建查询(必须是 mysql 数据库下)：

``` sql
SET GLOBAL sql_mode = '';
commit;
```

这就是临时取消全部了...然后再执行还原操作就 OK 了

### 方案2

在 MySQL 的配置文件中的 **[mysqld]** 下面添加如下列：
`sql_mode=ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION`

这样就永久生效了
不过此方法我未测试，因为我也就是临时改改

## 参考

http://blog.csdn.net/myboyliu2007/article/details/50583088
http://www.youyong.top/article/1158cf96a49
