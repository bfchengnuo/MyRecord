按照官方的文档，快速入门里的配置体验了下 Dubbo，遇到了几个坑爹的问题，感觉瞬间老了好几岁。。。。

## Run...as测试提示 java.lang.NoClassDefFoundError

准确来说是提示找不到 spring-beans 这个类，也就无法实例化 bean，然后 classpathxmlapplicationcontext 就报错了呗。。。

网上搜出来一堆说编译目录问题的，查了下没错，就是输出到 `target/classes` 下，并且特意查看了下，确实已经生成 class 文件了。

---

原因就是 Maven 下载 jar 包的问题，我当时还特意去看了下，确认文件夹下有 xx.jar 这个文件，并且 IDE 可以导入，也不报错，实在没想到 jar 包会有问题。

发现的契机是最后实在无奈，在反复 install 和 compile 后，仔细看看输出吧，然后。。。。

以前都是看最后显示 success 就不管了，没想到上面有 warning（~~毕竟不是 error~~），提示加载 xx.jar 失败  `invalid LOC header (bad signature)`

根据列出的地址，删除相关 jar，然后重新 install 或者 compile，让它重新进行下载，问题解决。

## dubbo的xml头文件解析失败

提示的是：`Unable to locate Spring NamespaceHandler for XML schema namespace [http://dubbo.apache.org/schema/dubbo]`

原因大概也想到了，在 dubbo 的官网上，给出的实例代码 xsd 是 `http://code.alibabatech.com/schema/dubbo` 下的，但是这个地址早已是 404，因为后来 dubbo 进入 apache 的孵化器了嘛，名字也换了....

在用户手册中，给出的 xsd 地址是 `http://dubbo.apache.org/schema/dubbo` 这个地址是可以使用的。

但是在老版本的 dubbo 是不能使用 `http://dubbo.apache.org/schema/dubbo` 的，只能使用 `http://code.alibabatech.com/schema/dubbo`，也就意味着不会有代码提示....

换回老版的头信息，问题解决。

## 可能出现的问题

现在 dubbo 推广使用注解的方式来配置，在 2.5.7 版本后支持，采用的是 @Configuration 大法，

【这个在 web 中，你可以使用 spring 的全 JavaConfig 配置方式（甚至连 web.xml 都不需要了），需要 servlet3.0+ ；或者采用传统的 web.xml 方式，把 @Configuration 当 bean 引入即可】

这里有个大坑是 `<dubbo:annotation>` 不再支持了，在 2.5.8 中应该还能用，但是以后的版本应该都不行了。。。。

---

期间还弹了几个 JVM 的啥啥啥的错误，本来想继续解决的，再次运行又没了.....迷....
