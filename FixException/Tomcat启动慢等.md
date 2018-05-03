测试版本 Tomcat9.x

## 扫描TLD

启动时提示：`At least one JAR was scanned for TLDs yet contained no TLDs`

> 猜测原因：
>
> Tomcat7/Servlet3.0 新增特性“可插性支持”实现的原理应该是自动扫描jar，当jar中并不包含相关组件时会打印该信息

当然，忽略也是完全可以的，对于强迫症，那么只需要将这些 jar 添加到不扫描列表就好了~

首先修改打印配置，让其打印详细信息，先修改 tomcat 的 logging.properties 配置文件，加入：

`org.apache.jasper.level = FINEST`

这里网上有好多的版本，对于 Tomcat9.x 我只测试到这个才行。

其他有说加下面第一句，如果失效加全三句：
``` xml
org.apache.jasper.compiler.TldLocationsCache.level = FINE
org.apache.catalina.startup.TldConfig.level = FINE
org.apache.jasper.servlet.TldScanner.level = FINE
```

然后根据 log 去 catalina.properties 配置文件中跳过你需要扫描的 jar 包就行了，如果是 linux 可以执行这条快捷命令：

`egrep "No TLD files were found in \[file:[^\]+\]" /var/log/tomcat7/catalina.out -o | egrep "[^]/]+.jar" -o | sort | uniq | sed -e 's/.jar/.jar,\\/g' > skips.txt`

我简单整理了下：
```
annotations-api.jar,\
catalina-ant.jar,\
catalina-ha.jar,\
catalina-storeconfig.jar,\
catalina-tribes.jar,\
catalina.jar,\
ecj-4.6.3.jar,\
el-api.jar,\
jasper-el.jar,\
jasper.jar,\
jaspic-api.jar,\
jsp-api.jar,\
servlet-api.jar,\
tomcat-api.jar,\
tomcat-coyote.jar,\
tomcat-dbcp.jar,\
tomcat-i18n-es.jar,\
tomcat-i18n-fr.jar,\
tomcat-i18n-ja.jar,\
tomcat-jdbc.jar,\
tomcat-jni.jar,\
tomcat-util-scan.jar,\
tomcat-util.jar,\
tomcat-websocket.jar,\
websocket-api.jar,\
bootstrap.jar,\
tomcat-juli.jar,\
access-bridge-64.jar,\
cldrdata.jar,\
dnsns.jar,\
jaccess.jar,\
jfxrt.jar,\
localedata.jar,\
nashorn.jar,\
sunec.jar,\
sunjce_provider.jar,\
sunmscapi.jar,\
sunpkcs11.jar,\
zipfs.jar
```

## Eclipse 中 Tomcat 启动慢

在外部 run 方式启动 1s 内就能启动起来，但是到了 Eclipse 里启动，使用发布到源目录的方式要 30s，使用默认方式也要 10s左右

无解中...

## 其他

随机数生成导致启动慢？配置JRE使用非阻塞的 Entropy Source：

在 bin 目录下的 catalina.sh 中的最后加入：

`JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"`

---

eclipse 清理项目缓存：

`workspace\.metadata\.plugins\org.eclipse.core.resources\.projects`

最重要的个人配置文件夹：

- 【org.eclipse.core.runtime】
- 【org.eclipse.e4.workbench】
