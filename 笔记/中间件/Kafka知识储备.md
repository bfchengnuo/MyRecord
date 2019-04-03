# Kafka知识储备

消息队列，Apache 出品，专注于大数据流式处理，极高的吞吐量。

## 概念

- **Broker**：消息中间件处理节点；每个 Kafka 服务节点称之为一个 Broker，一个 Kafka 集群由一个或多个 Broker 组成
- **Topic**：一类特定数据集合的统称；可类比 DB 中 Table 的概念；是逻辑概念
- Producer：消息的生产者，向 Broker 发送消息的客户端
- Consumer：消息的消费者，向 Broker 读取消息的客户端
- Consumer Group：每一个 Consumer 隶属于一个特定的 Consumer Group，一条消息可以被不同 Group 中的 Consumer 消费，但同一 Group 内的消息只能被一个 Consumer 消费
- **Partition**：是对 Topic 中所包含数据集的物理分区；物理概念
- **Replication**：副本集；是 Kafka 高可用的一种保障机制

最重要的两个：分区（partition）和副本（replica），副本不能存在自己的机器，只能存到集群其他的机器，分区则没有限制。

每一个 Topic 都可以划分成多个 Partition (每一个 Topic 都至少有一个 Partition)，不同的 Partition 会分配在不同的 Broker 上以对 Kafka 进行水平扩展从而增加 Kafka 的并行处理能力。

每一个消息在被添加到 Partition 的时候都会被分配一个 offset，他是消息在此分区中的唯一编号，此外 Kafka 通过 offset 保证消息**在一个 Partition 中的顺序**，offset 的顺序性不跨 Partition，也就是说在 Kafka 的同一个 Partition 中的消息是有序的，不同 Partition 的消息可能不是有序的。

同一个组的消费者，不能同时消费同一个分区上的数据。

## Topic

一个 Topic（主题）对应一个消息队列。Kafka 支持多生产者，多消费者。

Kafka 在概念上将一个 Topic 分成了多个 Partition，写入 topic 的消息会被（平均）分配到其中一个 Partition（通过哈希等方式）。

Partition 中会为消息保存一个 Partition 内唯一的 ID ，一般称为偏移量(offset)。这样当性能/存储不足时 Kafka 就可以通过增加 Partition 实现横向扩展。

## 消费模型

一般有两种消费模型，不同模型下消费者的行为是不同的：

- 队列模式（也叫点对点模式）。多个消费者共同消费一个队列，每条消息只发送给一个消费者。
- 发布/订阅模式。多个消费者订阅主题，每个消息会发布给所有的消费者。

要构建一个大数据下的消息队列，两种模式都是必须的。因此 Kafka 引入了 Consumer Group（消费组）的概念，Consumer Group 是以发布/订阅模式工作的；一个 Consumer Group 中可以有多个 Consumer（消费者），Group 内的消费者以队列模式工作。

也就是上面说的同一个组里的消费者不能同时消费一个 Partition 的消息（同一个 Consumer Group 内，一个 Partition 只能被一个 Consumer 消费）。

---

关于顺序：Kafka 只会保证在 Partition 内消息是有序的，而不管全局的情况。

无论消息是否被消费，除非消息到期 Partition 从不删除消息。例如设置保留时间为 2 天，则消息发布 2 天内任何 Group 都可以消费，2 天后，消息自动被删除。

Partition 会为每个 Consumer Group 保存一个偏移量（保存在 Topic 中），记录 Group 消费到的位置。

## 物理存储

考虑到物理机可能会损坏的问题，这会导致某个 Partition 失效，上面存储的消息丢失，那还说什么高可用？所以一般需要对数据做冗余 (replication)。换言之，需要存储多份 Partition 在不同的 Broker 上，并为它们的数据进行同步。

同一个 Partition 有多个副本，并分布在不同的 Broker 上，那么 Producer 应该写入到哪一个副本上呢？Consumer 又应该从哪个副本上读取呢？

1. Kafka 的各个 Broker 需要与 Zookeeper 进行通信，每个 Partition 的多个副本之间通过 Zookeeper 的 Leader 选举机制选出主副本。**所有该 Partition 上的读写都通过这个主副本进行**。
2. 其它的冗余副本会从主副本上同步新的消息。就像其它的 Consumer 一样。

## 小总结

1. Topic 是顶级概念，对应于一个消息队列。
2. Kafka 是以 Partition 为单位存储消息的，Consumer 在消费时也是按 Partition 进行的。即 Kafka 会保证一个 Consumer 收到的消息中，来自同一个 Partition 的所有消息是有序的。而来自不同 Partition 的消息则不保证有序。
3. Partition 会为其中的消息分配 Partition 内唯一的 ID，一般称作偏移量(offset) 。Kafka 会保留所有的消息，直到消息的保留时间（例如设置保留 2 天）结束。这样 Consumer 可以自由决定如何读取消息，例如读取更早的消息，重新消费等。
4. Kafka 有 Consumer Group 的概念。每个 Group 独立消费某个 Topic 的消息，互相不干扰。事实上，Kafka 会为每个 Group 保存一个偏移量，记录消费的位置。每个 Group 可以包含多个 Consumer，它们共同消费这个 Topic。
5. 对于一个 Consumer Group，一个 Partition 只能由 Group 中的一个 Consumer 消费。具体哪个 Consumer 监听哪个 Partition 是由 Kafka 分配的。算法可以指定为 `Range` 或 `RoundRobin`。
6. 物理上，消息是存在 Broker 上的，一般对应为一台物理机或集群。存储时，每个 Partition 都可以有多个副本。它们会被“均匀”地存储在各个 Broker 中。
7. 对于一个 Partition，它的多个复本存储一般存储在不同 Broker 中，在同一时刻会由 Zookeeper 选出一个主副本来负责所有的读写操作。

https://lotabout.me/2018/kafka-introduction/

## 扩展性

在一个 tipic 对应一个 partition 的情况下会将流量都划分到同一个 broker（partition 所在的机器）上， 如果节点的压力过大只能纵向提升机器的处理能力；很明显机器的性能不可能无限强。

所以 kafka 讲 topic 可以划分为多个 partition，每个 partition 提供一部分服务,从而将系统可以横向扩展到其他机器上.

其分配过程由broker选举出来的某个controller来决定, 分配的结果就是是要将partition均匀的分配到不同的broker节点上.

---

新添加的 Kafka 节点并不会自动地分配数据，所以无法分担集群的负载，除非我们新建一个 topic。但是现在我们想手动将部分分区移到新添加的 Kafka 节点上，Kafka 内部提供了相关的工具来重新分布某个 topic 的分区。

使用 Kafka 自带的`kafka-reassign-partitions.sh`工具来重新分布分区。该工具有三种使用模式：

　　1、generate 模式，给定需要重新分配的 Topic，自动生成 reassign plan（并不执行）
　　2、execute 模式，根据指定的 reassign plan 重新分配 Partition
　　3、verify 模式，验证重新分配 Partition 是否成功

https://www.iteblog.com/archives/1611.html