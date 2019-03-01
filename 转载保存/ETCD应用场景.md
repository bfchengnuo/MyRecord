# ETCD应用场景

原文：https://tonydeng.github.io/2015/10/19/etcd-application-scenarios/

## etcd是什么？

很多人对这个问题的第一反应可能是，它是一个键值存储仓库，却没有重视官方定义的后半句，用于**配置共享**和**服务发现**。

> A highly-available key value store for shared configuration and service discovery.

实际上，etcd作为一个受到Zookeeper和doozer启发而催生的项目，除了拥有与之类似的功能外，更具有以下4个特点。（[引自etcd官方文档](https://github.com/coreos/etcd)）

- 简单： 基于HTTP+JSON的API让你可以用CURL命令就可以轻松使用。
- 安全： 可以选择SSL客户认证机制。
- 快速： 每个实例每秒支持一千次写操作。
- 可信： 使用[Ralf](http://raftconsensus.github.io/)算法充分实现了分布式。

## 应用场景

### 场景一： 服务发现

服务发现（Service Discovery）要解决的是分布式系统中最常见的问题之一，即在同一个分布式集群中的进程或服务如何才能找到对方并建立连接。

从本质上说，服务发现就是要了解集群中是否有进程在监听upd或者tcp端口，并且通过名字就可以进行查找和链接。

要解决服务发现的问题，需要下面三大支柱，缺一不可。

- 一个强一致性、高可用的服务存储目录。
  - 基于Ralf算法的etcd天生就是这样一个强一致性、高可用的服务存储目录。
- 一种注册服务和健康服务健康状况的机制。
  - 用户可以在etcd中注册服务，并且对注册的服务配置`key TTL`，定时保持服务的心跳以达到监控健康状态的效果。
- 一种查找和连接服务的机制。
  - 通过在etcd指定的主题下注册的服务业能在对应的主题下查找到。
  - 为了确保连接，我们可以在每个服务机器上都部署一个proxy模式的etcd，这样就可以确保访问etcd集群的服务都能够互相连接。

[![服务发现](https://tonydeng.github.io/images/blog/etcd/service-discovery.jpg)](https://tonydeng.github.io/images/blog/etcd/service-discovery.jpg)

下面我们来看一下服务发现的具体应用场景

**微服务协同工作架构中，服务动态添加。**

随着Docker容器的流行，多种微服务共同协作，构成一个功能相对强大的架构的案例越来越多。透明化的动态添加这些服务的需求也日益强烈。通过服务发现机制，在etcd中注册某个服务名字的目录，在该目录下存储可用的服务节点的IP。在使用服务的过程中，只要从服务目录下查找可用的服务节点进行使用即可。

微服务协同工作如下图：

[![微服务](https://tonydeng.github.io/images/blog/etcd/micro-services.jpg)](https://tonydeng.github.io/images/blog/etcd/micro-services.jpg)

**PaaS平台中应用多实例与实例故障重启透明化**

PaaS平台中的应用一般都有多个实例，通过域名，不仅可以透明的对多个实例进行访问，而且还可以实现负载均衡。

但是应用的某个实例随时都有可能故障重启，这时就需要动态的配置域名解析（路由）中的信息。通过etcd的服务发现功能就可以轻松解决这个动态配置的问题。

[![多实例透明化](https://tonydeng.github.io/images/blog/etcd/multiple-instances.jpg)](https://tonydeng.github.io/images/blog/etcd/multiple-instances.jpg)

### 场景二： 消息发布和订阅

在分布式系统中，最为适用的组件间通信的方式是消息发布和订阅机制。

具体而言，即配置一个配置共享中心，书籍提供者在这个配置中心发布消息，而消息使用者则订阅他们关心的主题，一旦有关主题有消息发布，就会实时通知订阅者。通过这种方式可以实现发布式系统配种的集中式管理和实时动态更新。

**应用中的一些配置新存放在etcd上进行集中管理**

这类场景的使用方式通常是这样的：

应用在启动的适合主动从etc获取一次配置信息，同时，在etcd节点上注册一个`Watcher`并等待，以后每次配置有更新的适合，etcd都会实时通知订阅者，以此达到获取最新配置信息的目的。

**分布式日志收集系统**

这个系统的核心工作是收集分布在不同机器上的日志。

收集器通常按应用（或主题）来分配收集任务单元，因此可以在etcd上创建一个以应用（或主题）名目的目录，并将这个应用（或主题）相关的所有机器IP以子目录的形式存储在目录下。然后设置一个递归的etcd Watcher，递归式的健康应用（或主题）目录下所有信息的变动。这样就实现了在机器IP（消息）发生变动时，能够实时接受收集器调整任务分配。

**系统中心需要动态自动获取与人工干预修改信息请求内容**

通常的解决方案是对外保留接口，例如JMX接口，来获取一些运行时的信息或提交修改的请求。

而引入etcd后，只需要将这些信息存放在指定的etcd目录中，即可通过http接口直接被外部访问。

[![消息发布与订阅](https://tonydeng.github.io/images/blog/etcd/msg-pub-sub.jpg)](https://tonydeng.github.io/images/blog/etcd/msg-pub-sub.jpg)

### 场景三： 负载均衡

在[场景一](https://tonydeng.github.io/2015/10/19/etcd-application-scenarios/#%E5%9C%BA%E6%99%AF%E4%B8%80%EF%BC%9A_%E6%9C%8D%E5%8A%A1%E5%8F%91%E7%8E%B0)中也提到了负载均衡（即软件负载均衡）。

在分布式系统中，为了保证服务的高可用以及数据一致性，通常都会把数据和服务部署多份，以此达到对等服务，即使其中的某一个服务失效了，也不影响使用。

这样的实现谁让会导致一定程度上数据写入性能的下降，但是却能够实现数据访问时的负载均衡。因为每个对等服务节点上都存有完整的数据，所有用户的访问流量就可以分流道不同的机器上。

etcd**本身分布式架构存储的信息支持负载均衡**

etcd集群化以后，每个etcd的核心节点都可以处理用户的请求。所以，把数据量小但是访问频繁的消息数据直接存储到etcd是一个不错的选择。比如，业务系统中常用的二级代码表。

二级代码表的工作过程一般是这样，在表中存储代码，在etcd存储代码所代表的具体换衣，业务系统调用查表的过程，就需要查看表中代码的含义。所以如果把二级代码表中的小量数据存储到etcd中，不仅方便修改，也易于大量访问。

利用etcd**维护一个负载均衡节点表**

etcd可以监控一个集群中多个节点的状态，当有一个请求发过来后，可以轮询式把请求转发给存活的多个节点。类似KafkaMQ，通过Zookeeper来维护生产者和消费者的负载均衡（也可以用etcd来做Zookeeper的工作）。

[![负载均衡](https://tonydeng.github.io/images/blog/etcd/load-balance.jpg)](https://tonydeng.github.io/images/blog/etcd/load-balance.jpg)

### 场景四： 分布式通知与协调

这里讨论的分布式通知和协调，与消息发布和订阅有点相似。两者都使用了etcd的`Watcher`机制，通过注册与异步通知机制，实现分布式环境下的不同系统之间的通知与协调，从而对数据变更进行实时处理。

实现方式通常为：

不同系统都在etcd上对同一个目录进行注册，同事设置`Watcher`监控该目录的变化（如果对子目录的变化也有需求，可以设置成递归模式），当某个系统更新了etcd的目录，那么设置了`Watcher`的系统就会受到通知，并做出相应的通知，并作出相应处理。

通过etcd**进行低耦合的心跳检测**

检测系统和被检测系统通过etcd上某个目录管理而非直接关联起来，这样可以大大减少系统的耦合性。

通过etcd**完成系统调度**

某系统有控制台和推送系统两部分组成，控制台的职责是控制推送系统进行相应的推送工作。管理人员在控制台做的一些操作，实际上只需要修改etcd上某些目录节点的状态，而etcd就会自动把这些变化通知给注册了`Watcher`的推送系统客户端，推送系统再作出相应的推送任务。

通过etcd**完成工作汇报**

大部分类似的任务分发系统，子任务启动后，到etcd来注册一个临时工作目录，并且定时将自己的进度汇报（将进度写入到这个临时目录），这样任务管理者就能够实时知道任务进度。

[![分布式协同工作](https://tonydeng.github.io/images/blog/etcd/distributed-collaborative-work.jpg)](https://tonydeng.github.io/images/blog/etcd/distributed-collaborative-work.jpg)

### 场景五： 分布式锁

因为etcd使用`Raft`算法保持了数据的强一致性，某次操作存储到集群中的值必然是全局一致的，所以很容易实现分布式锁。

锁服务有两种使用方式，一是保持独占，二是控制时序。

**保持独占**

即所有试图获取锁的用户最终只有一个可以得到。

etcd为此提供了一套实现分布式锁原子操作CAS（`ComparaAndSwap`）的API。通过设置`prevExist`值，可以保证在多个节点同时创建某个目录时，只有一个成功，而该用户即可任务是获得了锁。

**控制时序**

即所有试图获取锁的用户都会进入等待队列，获得锁的顺序是全局唯一的，同时决定了队列执行顺序。

etcd为此也提供了一套API（自动创建有序键），对一个目录建值是指定为`POST`动作，这样etcd就会在目录下生成一个当前最大的值为键，存储这个新的值（客户端编号）。

同时还可以使用API按顺序列出所有目录下的键值。此时这些键的值就是客户端的时序，而这些键中存储的值可以是代表客户端的编号。

[![分布式锁](https://tonydeng.github.io/images/blog/etcd/distributed-lock.jpg)](https://tonydeng.github.io/images/blog/etcd/distributed-lock.jpg)

### 场景六： 分布式队列

分布式队列的常规用法与场景五中所描述的分布式锁的控制时序用法类似，即创建一个先进先出的队列，保证顺序。

另一种比较有意思的实现是**在保证队列达到某个条件时再统一按顺序执行**。这种方法的实现可以在`/queue`这个目录中另外再建立一个`/queue/condition`节点。

1. condition可以表示队列大小。比如一个大的任务需要很多小任务就绪的情况下才能执行，每次有一个小任务就绪，就给这个condition数字加1，直到达到大任务规定的数字，再开始执行队列里的一系列小任务，最终执行大任务。
2. condition可以表示某个任务不在队列。这个任务可以是所有排序任务的首个执行程序，也可以是拓扑结构中没有依赖的点。通常，必须执行这些任务后才能执行队列中的其他任务。
3. condition还可以表示其它的一类开始执行任务的通知。可以由控制程序指定，当condition出现变化时，开始执行队列任务。

[![分布式队列](https://tonydeng.github.io/images/blog/etcd/distributed-queue.jpg)](https://tonydeng.github.io/images/blog/etcd/distributed-queue.jpg)

### 场景七： 集群监控与Leader竞选

通过etcd来进行监控实现起来非常简单并且实时性强，用到了以下两点特性：

1. 前面几个场景已经提到了Watcher机制，当某个节点消失或由变动时，Watcher会第一时间发现并告知用户。
2. 节点可以设置`TTL key`，比如每隔30s向etcd发送一次心跳使代表该节点依然存活，否则说明节点消失。

这样就可以第一时间检测到各节点的健康状态，以完成集群的监控要求。

另外，使用分布式锁，可以完成Leader竞选。对于一些长时间CPU计算或使用IO操作，只需要由竞选出的Leader计算或处理一次，再把结果复制给其他Follower即可，从而避免重复劳动，节省计算资源。

Leader应用的经典场景是在**搜索系统中建立全量索引**。如果每个机器分别进行索引的建立，不仅耗时，而且不能保证索引的一致性。通过在etcd的CAS机制竞选Leader，由Leader进行索引计算，再降计算结果分发到其他节点。

[![Leader竞选](https://tonydeng.github.io/images/blog/etcd/leader-election.jpg)](https://tonydeng.github.io/images/blog/etcd/leader-election.jpg)

### 场景八： 为什么使用etcd而不用Zookeeper？

阅读了“[Zookeeper典型应用场景一览](http://jm-blog.aliapp.com/?p=1232)”的同学可能会发现，etcd实现的这些功能，Zookeeper都能实现。那为什么要用etcd而非直接使用Zookeeper呢？

相比较之下，Zookeeper有如下缺点。

1. 复杂。 Zookeeper的部署维护复杂，管理员必须掌握一系列的知识和技能；而[Paxos](http:)强一致性算法也是素来以复杂难懂而闻名于世；另外，Zookeeper的使用也比较复杂，需要安装客户端，官方只提供Java和C的两种语言的接口。
2. Java编写。这里不是对Java有偏见，而是Java本身就偏向重型应用，它会引入大量的依赖。而运维人员则普遍希望机器集群能尽可能的简单，维护起来也不容易出错。
3. 发展缓慢。 Apache基金会项目特有的“[Apache Way](http://www.infoworld.com/article/2612082/open-source-software/has-apache-lost-its-way-.html)”在开源界也饱受争议，其中一大原因就是由于基金会庞大的结构和松散的管理导致项目发展缓慢。

而etcd作为一个后起之秀，其优点也很明显。

1. 简单。 使用Go编写部署简单；使用HTTP作为接口使用简单；使用Raft算法保证强一致性让用户易于理解。
2. 数据持久化。 etcd默认数据一更新就进行持久化。
3. 安全。 etcd支持SSL客户端安全认证。

最后，etcd作为一个年轻的项目，正在高速迭代和开发中，这既是一个优点，也是一个缺点。

优点是在于它的未来具有无限的可能性，缺点是版本的迭代导致其使用的可靠性无法保证，无法得到大项目长时间使用的校验。

然而，目前CoreOS、Kubernetes和Cloudfoundry等知名项目均在生产环境中使用了etcd，所以总的来说，etcd值得你去尝试。

## 参考

1. <https://github.com/coreos/etcd>
2. <http://jm-blog.aliapp.com/?p=1232>
3. <http://progrium.com/blog/2014/07/29/understanding-modern-service-discovery-with-docker/>
4. <http://devo.ps/blog/zookeeper-vs-doozer-vs-etcd>