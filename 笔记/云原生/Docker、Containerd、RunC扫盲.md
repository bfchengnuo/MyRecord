# Docker、Containerd、RunC扫盲

> 扫盲只限于最基本的了解，不会进行深入探究。

Docker、Google、CoreOS 和其他供应商创建了开放容器计划 (OCI)，目前主要有两个标准文档：容器运行时标准 （runtime spec）和 容器镜像标准（image spec）。

这些标准有助于使这个生态系统在不同的平台和操作系统之间更具互操作性，并减少对单一公司或项目的依赖。

---

传统上我们使用容器技术时：

1. 通过 Docker，Kubernetes 等工具来运行一个容器时会调用**容器运行时（CRI）**比如 containerd，CRI-O 等。
2. 通过**容器运行时**来完成容器的创建、运行、销毁等实际工作

> Docker 使用的是 containerd 作为其运行时；Kubernetes 支持 containerd，CRI-O 等多种容器运行时
>
> 这些容器运行时都遵循了 OCI 规范，并通过 runc 来实现与操作系统内核交互来完成容器的创建和运行

## Docker

它是**管理容器**的最流行的工具。对很多人来说 "Docker" 这个名字本身就是 "容器" 的代名词。

Docker 启动了整个容器的革命，它创造了一个很好用的工具来处理容器也叫 Docker，这里最主要的要明白：

- Docker 并不是这个唯一的容器竞争者
- 容器也不再与 Docker 这个名字紧密联系在一起

目前的容器工具中，Docker 只是其中之一，其他著名的容器工具还包括：**Podman**，**LXC**，**containerd**，**Buildah** 等。

因此，如果你认为容器只是关于 Docker 的，那是片面的不对的。

当你用 Docker 运行一个容器时实际上是通过 Docker 守护程序、containerd 和 runc 来运行它。

## Docker镜像

许多人所说的 Docker 镜像，实际上是以 Open Container Initiative（OCI）格式打包的镜像。

因此，如果你从 Docker Hub 或其他注册中心拉出一个镜像，你应该能够用 docker 命令使用它，或在 Kubernetes 集群上使用，或用 podman 工具以及任何其他支持 OCI 镜像格式规范的工具。

即：镜像因为都是 OCI 规范打包，所以是通用的，只要是支持 OCI 的工具都可以使用

## Runc

RunC 是从 Docker 的 libcontainer 中迁移而来的，实现了容器启停、资源隔离等功能。Docker 将 RunC 捐赠给 OCI **作为 OCI 容器运行时标准的参考实现**。

Docker 默认提供了 docker-runc 实现。事实上，通过 containerd 的封装，可以在 Docker Daemon 启动的时候指定 RunC 的实现。最初，人们对 Docker 对 OCI 的贡献感到困惑。他们贡献的是一种“运行”容器的标准方式，仅此而已。它们不包括镜像格式或注册表推/拉格式。

当你运行一个 Docker 容器时，这些是 Docker 实际经历的步骤：

1. 下载镜像
2. 将镜像文件解开为 bundle 文件，将一个文件系统拆分成多层
3. 从 bundle 文件运行容器

Docker 标准化的仅仅是第三步。

在此之前，每个人都认为容器运行时支持 Docker 支持的所有功能。最终，Docker 方面澄清：原始 OCI 规范指出，只有“运行容器”的部分组成了 runtime。

这种“概念失联”一直持续到今天，并使“容器运行时”成为一个令人困惑的话题。

RunC 就可以按照这个 OCI 文档来创建一个符合规范的容器，既然是标准肯定就有其他 OCI 实现，比如 Kata、gVisor 这些容器运行时都是符合 OCI 标准的。

其中还可细分为 Low-Level 和 High-Level 容器运行时，containerd 和 cri-o，实际上使用 runc 来运行容器，在 High-Level 实现镜像管理和 API。

## Container Runtime Interface (CRI)

CRI（容器运行时接口）是 Kubernetes 用来控制创建和管理容器的不同运行时的 API，它使 Kubernetes 更容易**使用不同的容器运行时**。它一个插件接口，这意味着任何符合该标准实现的容器运行时都可以被 Kubernetes 所使用。

Kubernetes 项目不必手动添加对每个运行时的支持，CRI API 描述了 Kubernetes 如何与每个运行时进行交互，由运行时决定如何实际管理容器，因此只要它遵守 CRI 的 API 即可。

你可以使用你喜欢的 containerd 来运行你的容器，也可以使用 CRI-O 来运行你的容器，因为这两个运行时都实现了 CRI 规范。

## containerd

containerd 是一个来自 Docker 的高级容器运行时，并实现了 CRI 规范。它是从 Docker 项目中分离出来，之后 containerd 被捐赠给云原生计算基金会（CNCF）为容器社区提供创建新容器解决方案的基础。

所以 Docker 自己在内部使用 containerd，当你安装 Docker 时也会安装 containerd。

containerd 通过其 CRI 插件实现了 Kubernetes 容器运行时接口（CRI），它可以管理容器的整个生命周期，包括从镜像的传输、存储到容器的执行、监控再到网络。

## Dockershim

在 Kubernetes 包括一个名为 dockershim 的组件，使它能够支持 Docker。但 Docker 由于比 Kubernetes 更早，没有实现 CRI，所以这就是 dockershim 存在的原因，它支持将 Docker 被硬编码到 Kubernetes 中。

随着容器化成为行业标准，Kubernetes 项目增加了对额外运行时的支持，比如通过 Container Runtime Interface (CRI) 容器运行时接口来支持运行容器。因此 dockershim 成为了 Kubernetes 项目中的一个异类，对 Docker 和 dockershim 的依赖已经渗透到云原生计算基金会（CNCF）生态系统中的各种工具和项目中，导致代码脆弱。

2022 年 4 月 dockershim 将会从 Kubernetes 1.24 中完全移除。**今后 Kubernetes 将取消对 Docker 的直接支持**，而倾向于只使用实现其容器运行时接口的容器运行时，这可能意味着使用 containerd 或 CRI-O。

这并不意味着 Kubernetes 将不能运行 Docker 格式的容器。containerd 和 CRI-O 都可以运行 Docker 格式（实际上是 OCI 格式）的镜像，它们只是无需使用 docker 命令或 Docker 守护程序。

## containerd/docker命令区别

docker 命令都很熟了，就不多介绍了。对于 containerd：

`ctr` 是 containerd 的一个客户端工具。

`crictl` 是 CRI 兼容的容器运行时命令行接口，可以使用它来检查和调试 k8s 节点上的容器运行时和应用程序。

`ctr -v` 输出的是 containerd 的版本，`crictl -v` 输出的是当前 k8s 的版本，从结果显而易见你可以认为 crictl 是用于 k8s 的。

| 命令           | docker         | crictl（推荐）  | ctr                    |
| -------------- | -------------- | --------------- | ---------------------- |
| 查看容器列表   | docker ps      | crictl ps       | ctr -n k8s.io c ls     |
| 查看容器详情   | docker inspect | crictl inspect  | ctr -n k8s.io c info   |
| 查看容器日志   | docker logs    | crictl logs     | 无                     |
| 容器内执行命令 | docker exec    | crictl exec     | 无                     |
| 挂载容器       | docker attach  | crictl attach   | 无                     |
| 容器资源使用   | docker stats   | crictl stats    | 无                     |
| 创建容器       | docker create  | crictl create   | ctr -n k8s.io c create |
| 启动容器       | docker start   | crictl start    | ctr -n k8s.io run      |
| 停止容器       | docker stop    | crictl stop     | 无                     |
| 删除容器       | docker rm      | crictl rm       | ctr -n k8s.io c del    |
| 查看镜像列表   | docker images  | crictl images   | ctr -n k8s.io i ls     |
| 查看镜像详情   | docker inspect | crictl inspecti | 无                     |
| 拉取镜像       | docker pull    | crictl pull     | ctr -n k8s.io i pull   |
| 推送镜像       | docker push    | 无              | ctr -n k8s.io i push   |
| 删除镜像       | docker rmi     | crictl rmi      | ctr -n k8s.io i rm     |
| 查看Pod列表    | 无             | crictl pods     | 无                     |
| 查看Pod详情    | 无             | crictl inspectp | 无                     |
| 启动Pod        | 无             | crictl runp     | 无                     |
| 停止Pod        | 无             | crictl stopp    | 无                     |

## 资料来源

https://www.51cto.com/article/697381.html

https://zhuanlan.zhihu.com/p/494054143

https://www.cnblogs.com/hahaha111122222/p/16034076.html