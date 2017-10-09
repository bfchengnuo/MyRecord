在 SSH 的整合道路上遇到了不少坑，大多是小问题，但是经验不足，短时间内找不到原因，浪费了好多时间呐.....

## ClassNotFoundException错误

使用是 IDEA，新创建的项目手动整合完成后启动时报：

> 严重: Error configuring application listener of class org.springframework.web.context.ContextLoaderListener
> Java.lang.ClassNotFoundException: org.springframework.web.context.ContextLoaderListener

**原因：** 

jar 包未部署...

**解决：** 

进项目设置，选择 **Artifacts**，选择打包部署项目，选择 **Output Layout --> Web-INF**，查看下面是否有 lib 目录，右边的 jar 包是否添加到 lib 目录下。
如果没有就选择右边的 jar 包右键添加到 Web-INF 的 lib 文件夹中，然后重新打包部署就行了

## NoSuchMethodError错误

解决上面的一个错误后然后接着又是....

> Java.lang.NoSuchMethodError: org.springframework.beans.factory.annotation.InjectionMet

果然事情是没有那么简单的，这个错误大多也就是两种情况吧

1.jar 包缺失

2.jar 包重复

我是属于第二种情况，之前通过 IDEA 的 Maven 引入了 `spring-web-x.x.x.RELEASE.jar` 和 `struts2-spring-plugin-xxxxx.jar` 依赖包，仔细看看其实还引入了好多其他的包，大多都已经和原来的 spring 冲突了，所以引入这两个包的时候注意，把其他的都干掉，只留一个核心就行

## createquery is not valid without active transaction

这个错误真是不应该浪费这么多时间，以前还强调过，脑子是个好东西....

原因就是我使用的 **getCurrentSession()** 方法来获取 session，这个方法是从当前线程中获取的，当时强调了使用它的时候一定要在配置文件中进行设置

在 sessionFactory 配置文件中将 `hibernate.current_session_context_class` 设为 `org.springframework.orm.hibernate3.SpringSessionContext`（默认为此值），并应用 spring 管理事务。

如果为 `<prop key="hibernate.current_session_context_class">thread</prop>` 则会报异常

然而代码生成的是 thread ，所以就翻车了呗......

## java.sql.SQLException: The server time zone value

这个问题准确说是在整合 SSM 的时候遇到的，这就是欠的，原因是 **引用了最新的 MySQL 驱动** ，当时引用的是 6.0.3 版本，选了个最新的结果.....

异常描述其实还是很清楚的，就是没有设置时区，然后就可以在 url 后面加上就可以了：

`jdbcUrl=jdbc:mysql://localhost:3306/spring?serverTimezone=UTC`

说是设置为 UTC+8 也是可以的，记得转义就行，不过目前还不清楚这玩意有啥用
