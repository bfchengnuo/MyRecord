# Eclipse安装Hibernate Tools

使用 Hibernate 的时候为了方便直接从数据库逆向生成实体类，那么就需要这个工具了，首先是安装这个插件，下面说的是在线安装的方式，在 Eclipse 输入的地址为：

> http://download.jboss.org/jbosstools/updates/webtools/oxygen

注意最后的是版本，我当前的 Eclipse 版本是 Oxygen，如果版本不一致可能会出现错误。

安装很简单，一路 Next，然后同意几个协议，最后让你重启就 OK 了，不再多说。

## 配置数据库连接

MySQL 不多说，很简单，下面说 Oracle （~~万恶的 Oracle，实在是不想用~~）

数据库的驱动可以去 Oracle 的安装路径下找：`app\Kerronex\product\11.2.0\dbhome_1\jdbc`

地址写 `jdbc:oracle:thin:@localhost:1521:orcl`

数据库的名字写你的用户名（大写）或者直接写 orcl（默认是 SID 一般是这个），然后就没什么好说的了。

## 使用Tools

安装完成后，在 new 的菜单栏里，搜索 Hibernate 就可以看到多出了几个选项，比如我们可以让它生成 cfg 配置文件。

然后选择文件放在那：

其中有几个配置：

![img](http://hi.csdn.net/attachment/201202/20/0_1329708867ad9H.gif) 

>Database dialect ：数据库方言，我用的是 Oracle 11g 版本数据库，选择的是 Oracle 10g，里面有 Oracle 8i,Oracle 9i，没有更高的了，网上查了下 10g 也支持。
>
>Driver class：驱动类，**别忘了在你的项目的 lib下添加数据库 jar 包**，并在项目上 build path 建立引用路径；
>
>Connection URL：连接字符串，没什么说的了，其中1521为我的端口，orcl 为要连接的数据库名,每个人可能不一样；
>
>Default schema：这个据我验证，填写的应该是你连接 orcl 数据库的用户名，和下面的 username 的值一样，但是区分大小写，用大写的，不填的话在后面会进行大量扫描，有你哭的；
>
>Username,password：不用说了,连接orcl数据库的用户名和密码

然后勾选下面的复选框，next 下一步 ，然后 finish 这就 cfg 就可以说是创建完成了。

### 逆向生成Bean

点 Hibernate Tools 给的下拉按钮选择 hibernate code generation configurations（在工具栏） ，然后新 new 一个配置，填写

![img](http://hi.csdn.net/attachment/201202/20/0_1329709077NZls.gif) 

配置如下：
>console configuration：选择你的项目
>
>output directory：选择目录，我这里是 src
>
>勾选 reverse...那个复选框
>
>package：填写映射到的包
>
>reveng xml：选择 setup，然后选择 create new..,选择存放位置（项目下即可）

然后会让你选择逆向生成那个表，选完以后确定即可，最后勾选这几个：

![img](http://hi.csdn.net/attachment/201202/20/0_1329709222voD5.gif) 

点击 run 即可生成实体类。

不知道是不是我的机器问题，这个过程实在是太慢了。。。。还不如用 IDEA 呢 ╭(╯^╰)╮

