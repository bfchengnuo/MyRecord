目前，基本大部分跑在服务器的 Java 应用都是用的 OpenJDK，其他的可能是 adoptopenjdk 之类，这里以 OpenJDK 为例。

---

将 Java 应用作成 docker 镜像时，需要镜像中带有 jdk 或者 jre 环境，通常有三种情况：

- 在 Dockerfile 中加入安装 jdk 环境的脚本；
- 镜像中只有应用 jar 包，启动容器时通过数据卷映射（ -v 参数）将宿主机的jdk文件夹映射到容器内；
- 使用带有 jdk 的镜像作为基础镜像；

上述三种方式各有优劣： 

- 第一种，完全自己动手，可控性强，但增加了 Dockerfile 编写的工作量，脚本质量要自己保证； 
- 第二种，要求宿主机预先部署 jdk，增加了宿主机工作量；
- 第三种，相对工作量小一些，如果找的基础镜像不合适，会导致做成的镜像体积过大，或者多了些不需要的内容；

当然，最常用的还是第三张；那么选择镜像就成了一个问题。

## 镜像选择

打开 OpenJDK 的官方 Docker 仓库，里面的 tag 非常之多，眼花缭乱，我们应该选那个呢？

### stretch&jessie关键字

以 8-jre-stretch 这个 tag 为例，其中的 stretch 表明这个镜像的操作系统是 debian 9，这是 debian 的一个稳定版本。

类似的标签还有 jessie，这是 debian 的上一个稳定版本。

PS：不知道为什么，很多官方都喜欢用 debian 进行构建。

### alpine关键字

以 13-ea-19-jdk-alpine3.9 这个 tag 为例，其中的 alpine 表明镜像的操作系统是 alpine linux，alpine linux 本身很小，alpine 镜像的大小是 5M 左右，因此以 alpine 作为基础镜像构建出的 openjdk 镜像也很小：

### oraclelinux7关键字

以 13-ea-oraclelinux7 这个 tag 为例，其中的 oraclelinux7 表明镜像的操作系统是 Oracle Linux 7，从 jdk12 开始，openjdk 官方开始提供基于 Oracle Linux 7 的 jdk 镜像；

### slim关键字

以 8-jre-slim 这个 tag 为例，其中的 slim 表明当前的 jre 并非标准 jre 版本，而是 headless 版本，该版本的特点是去掉了 UI、键盘、鼠标相关的库，因此更加精简，适合服务端应用使用，官方 的建议是除非有明确的体积限制是再考虑使用该版本；

### ea关键字

以 13-ea-19-jdk-alpine3.9 这个 tag 为例，其中的 ea 的意思是 "Early  Access"，这里代表 jdk13 正是发布之前的预览版本，该版本带有新特性并且修复了若干 bug，但毕竟是预览版，质量还未达到 release 要求，不推荐生产环境使用；

## 参考

https://cloud.tencent.com/developer/article/1453353