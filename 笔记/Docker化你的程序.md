# Docker化你的程序

Docker 是一个**使用 Go 语言开发的开源的应用容器引擎**，让开发者可以打包他们的应用以及依赖包到一个可移植的容器中，然后发布到任何流行的机器上。Docker 的发展速度和火爆程度着实令人惊叹，一发不可收拾，形成了席卷整个IT界的新浪潮。

记得在公众号科普过 Docker 的一些基本概念，简单可以理解为集装箱，可以把你的程序、环境、配置等等全部装进去，这样在其他机器上达到开箱即用，也就是解决了环境不一致的问题。

还有就是每一个 Docker 都是相对隔离的，避免了资源使用上的一些问题。

**Docker 的思想：标准化、集装箱、隔离，核心有镜像、仓库、容器等概念**

Docker 是容器化技术的一个代表，这项技术并不是很新，在内核中很早之前就已经存在了，不过确实是因为 Docker 才火起来的，在云计算的领域，Docker 可谓是更加的火热，~~让程序猿和运维的关系更好了~~

Docker 你可以粗糙的理解为轻量级的**虚拟机**

## 走进Docker

下面来说说核心的三个词：镜像、仓库、容器；仓库就相当于码头，镜像就是集装箱，容器就是运行程序的地方，所以一般的使用步骤就是从仓库（码头）拉取镜像到本地，然后用命令把镜像运行起来。

相关的命令：Build（构建镜像）、ship（运输镜像）、run。

Docker 仓库的地址：[hub.docker.com](https://hub.docker.com/)，如果速度慢，可以尝试国内的 [c.163yun.com](https://c.163yun.com/hub#/m/home/) ，或者 [daocloud](https://www.daocloud.io/) 也不错。

如果我们的镜像比较私密，不想让别人知道，那么可以自己搭一个镜像仓库，就像 Maven 仓库哪样

PS：安装 Docker 推荐是在 ubuntu 上，因为本身就是在 ubuntu 上进行开发的，所以支持应该是最好的。

![](../img/docker.png)

## 基本使用

下面介绍几个常用的命令：

- 从仓库拉取镜像：`docker pull name`

- 查看本机的镜像：`docker images`

- 运行镜像：`docker run name`

  如果需要在后台运行可以使用 `-d` 参数

  还可以加 `-e` 指定用到的环境变量（用 MySQL 的时候可能会用到），指定多个环境变量可使用多次 -e

- 查看本机正在运行的容器：`docker ps` 

- 进入容器：`docker exec -it [id] bash` 

  id 由 run 命令（后台启动）返回，或者使用 ps 来查看，并且不需要输入完整，前几位就可以，与 git 比较类似。

  然后就进入了这个容器，并且是以 bash 这个 shell 的方式，在容器中和 Linux 的操作基本一致，如同一个小型的 Linux 系统，并且是根据需求配置好的。

  使用 exit 命令即可退出容器

- 停止容器：`docker stop id` ，主要是对于那些后台启动的容器来说

- 重启容器：`docker restart id`

- 删除镜像：`docker rmi id`

- 清除运行过的镜像记录（缓存）：`docker rm id` ，可以使用 `docker ps -a` 来查看记录

如果对命令参数不熟悉，可以查看帮助，如：`docker run --help` ；可以看出除了 pull 和 run 大部分命令都是依赖于镜像的 id 的

---

如果我们需要将本机的某个文件放到容器里，有个快捷的命令：

`docker cp xxx.html [id]://usr/share/nginx/html`

id 自然指的就是相应的容器了，不过你得熟悉你容器的文件分布情况才行，**这种改动操作是临时的，当容器停止后改动不会被保存**

如果需要永久保存，需要执行 commit 操作：

`docker commit -m "test" [id] name`

这样就相当于是保存修改了，和 Git 是不是很像？它实际会根据改动生成一个新的镜像，所以要在最后指定新镜像的名字（版本也可以）

---

使用 `docker tag xxx newName` 可以实现镜像的复制....

想要 push 自己的镜像除了必要的账号，需要先进行登陆 `docker login` ，然后 `docker push name` 就可以了

## Docker中的网络

Docker 会虚拟出一个运行环境，这个环境当然包括网络、文件等，也是通过 namespace 来进行区分，对于网络，虚拟有三种方式：

- Bridge

  也就是我们所说的桥接，会虚拟出独立的一套网络配置（网卡），有独立的 ip、端口、iptab 规则等
  这也是启动 Docker 的默认模式

- Host

  使用物理机的网卡，和主机共用一套网络配置

- None

  不配置网络，也就是容器内的程序不会与外界发生通讯

虚拟的方式是不是和配置虚拟机差不多呢~

在配置 Bridge 模式时，通常我们会配置端口映射，简单说就是当我们访问主机的某一个端口时，实际访问的是容器里的某一个端口，这个过程通过 Docker 提供的一个网桥实现（处理请求转发）

映射的配置可以在启动时就指定：

`docker run -d -p 8080:80 name `

`-p` 后的第一个是本机的端口，第二个是对应的容器里的端口，上面的命令就实现了把本机 8080  的请求转发到容器的 80 端口上。

不放心的话可以使用 `netstat -na|grep 8080` 看看是不是处于监听的状态了。

---

另外一种就是使用 `-P` 的方式，**是大写的 P**，这种不需要再指定对应的端口，它会在本机开一些随机端口然后映射到容器里对应应用的端口上。

## 制作镜像

重头戏来了，如何把自己开发的程序打包成一个 Docker 应用呢，就像仓库里的那些一样。

用到的是 Dockerfile 和 build 命令，Dockerfile 可以理解为描述了打包流程，然后使用 build 会根据这个流程进行打包。

### 编写Dockerfile

是的，Dockerfile 只是一个文本文件，内容才是最重要的，可以直接使用 vim 来编写，名字就叫 Dockerfile，主要包括：

``` shell
# 1.设置基础镜像（在某个镜像的基础上）
from tomcat
# alpine 是针对 Docker 做的一个极小的 linux 环境，也常用做基础镜像
# from alpine:latest

# 2.作者信息,也可以不写
MAINTAINER Kerronex bfchengnuo@gmail.com
# 官方现在推荐使用 LABEL maintainer="yourname <xxx@xxx.com>"

# 3.将 war 包放进容器里
COPY xxx.war /usr/local/tomcat/webapps

# 拓展其他
CMD echo "Hello World!"
```

上面是个简单的发布 Java 程序的环境，tomcat 的目录在那是从官网 tomcat 镜像说明页面找到的。

然后下面执行 `docker build -t appName:latest .` 来进行打包就可以了；-t 的作用是可以指定名字和版本，最后一个 `.` 表示当前目录。

然后使用 `docker images` 命令就可以找到我们自己发布的本地镜像了，运行和其他的一样

### 镜像分层

Docker 是分层存储的，在 Dockerfile 中一行就是一层，每一层都有它唯一的 ID。

这些层在 image (镜像)状态都是只读的（RO），当 image 运行为一个容器时，会产生一个容器层 (container layer)，它是可读可写的（RW）。

分层的一个好处就是会减轻不少存储压力，多个 image 难免会有许多的相同的命令，分层后就可以实现共用。

## 存储

再来看看 Volume 吧，它提供**独立于容器之外**的**持久化**存储。

它就可以解决我们在容器内修改不会保存的问题，并且它可以**提供容器与容器之间的共享数据**

### 映射容器里的目录到本机

在运行时执行命令：

`docker run -d --name nginx -v /usr/share/nginx/html nginx`

命令里的路径是**容器里**的地址，还给它起了个名字，运行后就会将配置的目录映射到本机（Host）的一个目录下，想要确定这个目录可以使用下面的命令查看容器信息：

`docker inspect nginx`

后面跟的是我们起的那个名字哦，不是镜像名，然后重点看 **Mounts** 下的 Source 和 Destination 是不是正确，就是把容器内的 Destination 地址映射到了本机的 Source 目录下。

在本机的 Source 下修改会同步到容器里的 Destination 目录。

### 映射主机里的目录到容器

与上面正好相反，把本机的一个目录映射到容器里，命令：

`docker run -d -v $PWD/html:/usr/share/nginx/html nginx`

这条命令就是把当前目录下的 html 文件夹映射到容器里 nginx 的目录下，这种方式用的比较多，非常的方便

### 建立只存数据的容器

这种情况或者说需求也是比较常见的，使用的命令是：

`docker create -v $PWD/data:/var/mydata --name data_container ubuntu`

这样我们就创建了一个仅仅用来存储数据的新容器 data_container ，它会把当前目录下的 data 文件夹挂载到与之关联的容器的 `/var/mydata` 目录下，然后我们运行一个新的容器来测试下：

`docker run --volumes-from data_container ubuntu `

volumes-from 的作用就是从另一个容器挂载，这样就实现了当前容器挂载仅有数据容器的目的，主要想体现的是这种仅有数据的容器可以被多个容器挂载使用，用来做数据的共享。

## 多容器APP

玩这个之前需要一个软件就是 docker-compose，在 Mac/Windows 下是自带的。

然后需要配置 docker-compose.yml 文件，文件名是固定的，使用的是 yaml 语法，也就是用缩进来表示层次关系，非常流行的一种配置文件格式。

> Dockerfile 可以让用户管理一个单独的应用容器；
>
> 而 Compose 则允许用户在一个模板（YAML 格式）中定义一组相关联的应用容器（被称为一个 project，即项目），例如一个 Web 服务容器再加上后端的数据库服务容器等

最后写好 docker-compose 的配置文件后，就可以使用 `docker-compose up -d` 来运行了，停止就是 stop，rm 是删除；当重新修改配置文件后需要用 `docker-compose build` 重新来构建。

PS：在 docker-compose 配置的名字可以在其他的容器里直接用（比如 nginx 的配置文件里），不需要再配置解析。

关于 docker-compose 的使用这里不会多说，具体操作还是蛮复杂的，计划是等用到后再来补充，先暂且知道有这么个多容器的概念，可参考慕课网视频：https://www.imooc.com/video/15735

## 附:Dockerfile示例

一般是创建一个空目录，然后在这个空目录写 Dockerfile 文件，名字就叫 Dockerfile，这样打包的时候可以放心的用 `.` ：

``` shell
from ubuntu
LABEL maintainer="Kerronex <bfchengnuo@gmail.com>"

# 执行命令
# 替换镜像地址，加速下载，把 archive.ubuntu.com 换成 mirrros.ustc.edu.cn
RUN sed -i 's/archive.ubuntu.com/mirrros.ustc.edu.cn/g' /etc/apt/sources.list
RUN apt-get update
RUN apt-get install -y nginx
COPY index.html /var/www/html

# 设置容器的入口，会将数组展开来运行
# 命令的意思是将 nginx 作为前台程序执行，而不是守护进程
ENTRYPOINT ["/usr/sbin/nginx","-g","daemon off;"]
# 暴露端口
EXPOSE 80
```

然后可以使用 `curl http://localhost:80` 来测试一下。

文件中没有用的其他关键字：ADD 也是添加文件，它比 COPY 更加强大，可以添加远程文件；CMD 也是执行命令的意思，可以用于指定容器的入口，也可以使用 ENTRYPOINT 命令

| 命令         | 用途                          |
| ---------- | --------------------------- |
| WORKDIR    | 指定路径                        |
| MAINTAINER | 维护者，现在推荐使用 LABEL maintainer |
| ENV        | 设定环境变量                      |
| ENTRYPOINT | 容器入口                        |
| USER       | 指定用户                        |
| VOLUME     | mount point（容器所挂载的卷）        |

当没有指定 ENTRYPOINT 的时候，就用 CMD 来启动容器，如果指定了 ENTRYPOINT 那么 CMD 指定的字串就变成了 *argus* （参数）