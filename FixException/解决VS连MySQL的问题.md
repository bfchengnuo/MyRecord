MySQL 毕竟不说微软自家的，相比 SQL Server 真是复杂太多了

## 安装支持库

在创建实体模型的时候，数据源选项很可能没有 MySql database，这问题很容易解决，只需要安装 mysql-for-visualstudio 即可解决，这里推荐版本是 `mysql-for-visualstudio-1.2.3` 。

还需要安装一个 MySQL Connector/Net 驱动程序，推荐版本：`mysql-connector-net-6.8.3`

这个版本不是越高的越合适，得配合你 Mysql.data 的版本，目前大部分是使用 mysql.data version 6.8.3 这版本。

## 引用实体框架错误

按照上面完成后基本会遇到这么个错误：

> 您的项目引用了最新实体框架；但是，找不到数据链接所需的与版本兼容的实体框架数据库.....

**解决方案：**

在 - 工具 - 库程序包管理器 - 程序包管理器控制台 这里，在 PM> 后输入： 

``` shell
Install-Package EntityFramework -Version 6.0.0
Install-Package EntityFramework.zh-Hans -Version 6.0.0
Install-Package MySql.Data.Entity.EF6
```

最后一个可能会安装失败，不用管它，我测试也是可以的

最后在 `.config` 文件里的 providers 标签下增加一个子节点：

``` xml
<provider invariantName="MySql.Data.MySqlClient" type="MySql.Data.MySqlClient.MySqlProviderServices, MySql.Data.Entity.EF6"></provider>
```

重新生成解决方案，就可以了！

## 生成模型时错误

选好表后悲催的我又出现了个错误：

> 生成模型时出现意外错误....表 tabledetails 中列 IsprimaryKey 的值为 DBNull

解决方案来自 stackoverflow

**解决方案**
1. 重启 MySQL
2. 进入 MySQL 执行下面两条语句：
   ``` sql
   use <<database name>>;
   set global optimizer_switch='derived_merge=OFF';
   ```

然后重新试一下，应该就可以了，原文：

> 1. Open Services (services.msc) and restart MySQL57 service.
> 2. Execute the following commands in MySQL.
>    use <<database name>>;
>    set global optimizer_switch='derived_merge=OFF';
> 3. Update the .edmx.

## 创建控制器错误

在创建控制器的时候，提示：运行所选代码生成器时出错:“调用的目标发生了异常。”

原因大概是创建 EF 模型需要相应的驱动支持，而本机并没有安装，大概....
> 看看 wiki？ https://zh.wikipedia.org/zh-cn/Entity_Framework

**解决方案**
使用 NuGet 安装相应的扩展包：
如果是微软自家的数据库就安装 EntityFramework.sqlServerCompact
如果是 MySQL 可以尝试：Entity Framework with MySQL （参考下面的最后一个链接）

## 参考

http://www.cnblogs.com/Imaigne/p/4153397.html

http://bbs.csdn.net/topics/392145081

http://www.cnblogs.com/keatkeat/p/3995599.html
