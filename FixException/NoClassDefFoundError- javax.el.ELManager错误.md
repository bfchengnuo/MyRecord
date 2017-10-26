错误原因：

在搭建 SSM 的时候用到校验功能，使用了性能比较好的 **hibernate-validator**

使用的版本是 6.0+ ，然后运行报错：

具体的错误信息已经记不清了，主要是：`java.lang.NoClassDefFoundError: javax/el/ELManager ` 这一句，很明细是缺少这个 el 的依赖，然后找了个依赖加入：

``` xml
<dependency>
    <groupId>javax.el</groupId>
    <artifactId>javax.el-api</artifactId>
    <version>3.0.0</version>
</dependency>
```

后来在 Maven 的页面看到它需要 el-api 3.0+ 的依赖，好吧.....

加入后刚开始测试还不错，后来发现只要访问页面就会报另一个错误，大体是：

``` 
java.lang.LinkageError: loader constraint violation: when resolving....
javax.servlet.ServletException: java.lang.NoSuchMethodError.....
```

然后搜索后答案多数是因为 Tomcat 已经有相关依赖，el 这个包是多余的，我观察了下 Tomcat 的 lib 目录里确实有这个包

然后我就把它的作用域改为了： `<scope>provided</scope>`

但是这样就启动不起来了和最开始的错误一样，这样就等于回到了原点....

## 解决

原因还是因为版本的问题，版本太高了......

起码在 `hibernate-validator5.4+` 开始就开始依赖 `el-api 3.0+` 的版本，但是我用的 Tomcat7.0 自带的 el-api 包是 2.2 的版本

所以：

1. 升级 Tomcat 为 8.0+ 这样默认的 el-api 是 3.0+ 的，就不会有冲突了
2. 降级 hibernate-validator 版本

我采用的是降级 hibernate-validator ，降级到依赖为 2.2 左右的版本，这样连 el-api 的依赖都不需要导了

参考：https://stackoverflow.com/questions/45841464/java-lang-noclassdeffounderror-javax-el-elmanager
