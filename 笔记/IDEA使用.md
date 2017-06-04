# IDEA使用

快捷键、基本配置等就不说了，主要说开发 java web 时候的使用

总之，信 JB 就对了，软件做的棒！

## 新建一个web工程

>   感觉这篇已经写的很全了，我就文字说下简单的步骤
>
>   https://segmentfault.com/a/1190000007088964

主要步骤为：

1.  新建工程，选择 `Java Enterprise` 如有需要可以选择一些框架，比如 SSH 
2.  配置输出目录和库目录（IDEA 默认的输出目录很....不喜欢）
    在 `WEB-INF` 目录下新建 classes 目录和 lib 目录
    在项目设置的 Modules 中的 Path 为 classes 目录
    在项目设置的 Modules 中的 Dependencies 中添加库，目录为新建的 lib，类型是 jar Dir （这样 lib 下的库会在编译的时候输出）
3.  配置web服务器
    在运行的左边，点击 `Edit Configurations...` 取消 After launch ，我是不喜欢，勾上后点运行会自动打开浏览器
    下面的网址中在后面加上本项目的名字，这个都知道哈，在 Deployment 中的右栏也写一下
    其他配置像 Tomcat 的路径啊就不说了，简单

PS：如果使用了框架，比如 struts2 ，在 Artifacts 中的 OutputLayout 里点击 struts2 鼠标右键 put into lib

## web项目的运行原理

可以注意到，IDEA 中运行 Tomcat 进行调试的时候，在 webapps 目录下是不存在相关文件的，于是我就好奇了，驱使我要搞明白

>   原文：http://www.voidcn.com/blog/yangcheng33/article/p-6265449.html

这个需要先从 `CATALINA_HOME` 和 `CATALINA_BASE` 这两个“环境变量”的区别入手。

简单的说，`CATALINA_HOME` 是 Tomcat 的安装目录，`CATALINA_BASE` 是 Tomcat 的工作目录。

当我们想要运行多个 Tomcat 实例，但是不想拷贝多个 Tomcat 副本时，那么我们可以配置多个不同工作目录，在运行 tomcat 时对每个实例指派不同的工作目录，它们共享安装目录的运行文件（bin目录下）。

这么看来 `CATALINA_BASE` 所指向的就是 conf、logs、temp、webapps、work 和 shared 目录。
而 `CATALINA_HOME` 则包括了 Tomcat 的二进制文件和脚本目录，也就是 bin 和 lib 目录。

---

**下面就是正文了**
首先 Intellij 会为每个 web 项目建立一个单独的文件夹，以 `“Unnamed_项目名”` 命名（可在 **.idea/workspace.xml** 中修改）。
在每次启动项目时，它先将 tomcat 目录下原始的 `CATALINA_BASE` 目录拷贝一份**到该目录下**，也就是将当前 tomcat 的**配置文件**拷贝到 `“Unnamed_项目名”` 文件夹下。
然后将 `CATALINA_BASE` 的路径修改为该目录的路径，**再在 `Unnamed_项目名/conf/Catalina/localhost` 下添加项目的配置文件，如 code.xml**，内容为

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Context path="/code" docBase="/Users/didi/project/CODE-PROJ/didi-code-web/target/code" />
```

最后启动 tomcat，tomcat 除了会启动 webapps 下应用外还会加载 `/conf/Catalina/localhost` 下配置的应用，而 Intellij 就是通过这种方式**“隐蔽”**地加载 web 项目。

看到这儿你可能还会发现为什么在 tomcat 安装目录下始终找不到项目 log 文件的原因了，因为 `CATALINA_BASE` 指向了 `/Users/xxxx/Library/Caches/IntelliJIdea2016.1/tomcat/Unnamed_didi-code`，所以指定相对路径 `${catalina.base}` 的 log 文件就存在了该目录下。

>   `startup.sh` 设置环境变量时调用 `catalina.sh` 脚本，此脚本会读取 **CATALINA_BASE** 的值，在 **$CATALINA_BASE/conf** 目录，得到 **server.xml**。
>
>   这个文件是 Tomcat 的核心配置，它包含所有的配置信息，如 shutdown 端口，connector 端口，主机名称，应用目录等。例如，Tomcat通常使用 8080 作为连接端口，所以我们可以通过 http://localhost:8080/ 访问。
>
>   如果我们已经设置 **$CATALINA_BASE**，Tomcat 就会从该变量所对应的目录搜索得到 **server.xml**。

## 其他

正常情况下，从 IDEA 启动 Tomcat 是不能访问以前 webapps 里的应用的，如果需要，在服务器配置页面记得勾选 `Deploy applications configured in tomcat instance`

或者可以在服务器的配置选择 deployment 然后进行手动添加

待补充...