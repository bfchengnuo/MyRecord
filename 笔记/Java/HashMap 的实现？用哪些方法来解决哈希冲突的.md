**在 JDK1.6，JDK1.7 中，HashMap 采用位桶+链表实现，即使用链表处理冲突，**同一 hash 值的对象都存储在一个链表里。

但是当位于一个桶中的元素较多，即 hash 值相等的元素较多时，通过 key 值依次查找（相当于遍历这个链表，尊重`hashCode/==/equals` 比较机制）的效率较低。

**而 JDK1.8 中，HashMap 采用位桶 + 链表 + 红黑树实现，当链表长度超过阈值（8）时，将链表转换为红黑树**，这样大大减少了查找时间。 

简单说 HashMap 实现原理：

> 首先有一个每个元素都是链表结构的数组（可能表述不准确），当添加一个元素（key-value）时，就首先计算元素 key 的 hash 值，以此确定插入数组中的位置；
>
> 但是可能存在同一 hash 值的其他元素已经被放在数组同一位置了，这时就添加到这个同一 hash 值的元素的后面，他们在数组的同一位置，但是形成了链表，**同一各链表上的 Hash 值是相同的**，所以说数组存放的是链表。
>
> 而当链表长度太长时，链表就转换为红黑树，这样大大提高了**查找的效率**。

当链表数组的容量超过初始容量的 0.75 时，再散列将链表数组扩大 2 倍，把原链表数组的搬移到新的数组中。

关于红黑树，我转载了一篇很通俗易懂的存档在 Github。

---

> 以下分割线前的内容可能与主题有点偏离，但是我克制不住简单这么牛逼的代码，得贴下！

这里和 List 的扩容也差不多，不过 List 每次是 1.5 倍扩容，Vector 倒是是 2 倍。

这里的 0.75 嘛~，和 HashSet 中非常相似（毕竟 hashSet 的实现就是用的 hashMap），就是加载因子，这还让我想起了在读源码时看到 tableSizeFor 时直接惊呆了，牛逼啊！Σ( ° △ °|||)︴

``` java
static final int MAXIMUM_CAPACITY = 1 << 30;
/**
 * Returns a power of two size for the given target capacity.
 */
static final int tableSizeFor(int cap) {
  int n = cap - 1;
  n |= n >>> 1;
  n |= n >>> 2;
  n |= n >>> 4;
  n |= n >>> 8;
  n |= n >>> 16;
  return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
}
```

这是用来保证分组组数是 2 的 n 次方，即使你传入的初始组数不是那也会转成最近的符合条件的数。原因很好猜就是为了能使用位运算，这在以前的笔记中也提到过。

这里的 `cap - 1` 是为了防止本身传入的就是一个 2 的 n 次方的数，如果不加，那么运算完成后就成了这个数的 2 倍了。

如果 n 这时为 0 了（经过了 cap-1 之后），则经过后面的几次无符号右移依然是 0，最后返回的 capacity 是 1（最后有个三目 n+1 的操作）。  

然后就是下面的一系列无符号右移操作（不再考虑 n 为 0  的情况）：

- 第一次右移（1）
  由于 n 不等于 0，则 n 的二进制表示中总会有一 bit 为 1，这时只考虑最高位的 1（前面自然都是 0）。
  通过无符号右移 1 位，则将最高位的 1 右移了 1 位，再做或操作，使得 n 的二进制表示中**与最高位的 1 紧邻的右边一位也为 1**
- 第二次右移（2）
  继续将 n 无符号右移两位，会将最高位两个连续的 1 右移两位（第一步已经保证最高位是两个连续的 1），然后再与原来的 n 做或操作，这样 n 的二进制表示的**高位中会有 4 个连续的 1**
- 第三次右移（4）
  这次把已经有的高位中的连续的 4 个 1，右移 4 位，再做或操作，这样 n 的二进制表示的**高位中会有 8 个连续的 1**
- 最终结果
  容量最大也就是 32bit 的正数，因此最后 `n |= n >>> 16;` ，最多也就 32 个 1，但是这时已经大于了 `MAXIMUM_CAPACITY` ，所以取值到 `MAXIMUM_CAPACITY ` 就可以了。 

结果简单说就是把你传进来的这个不符合规范的数，从最高位的 1 开始，后面全部补为 1，这样就是一个最接近的规范的数了。

最终，这个 capacity 却被赋值给了 threshold ，请注意，在构造方法中，并没有对 table 这个成员变量进行初始化，table 的初始化被推迟到了 put 方法中，在 put 方法中会对 threshold 重新计算。

参考（内还有 hash() 方法的源码解析）：https://blog.csdn.net/fan2012huan/article/details/51097331

---

继续之前的主题~

加载因子（默认 0.75）：**为什么需要使用加载因子，为什么需要扩容呢？**

因为如果填充比很大（加载因子），说明利用的空间很多，如果一直不进行扩容的话，链表就会越来越长，这样查找的效率很低；

因为链表的长度很大（当然最新版本使用了红黑树后会改进很多），扩容之后，**将原来链表数组的每一个链表分成奇偶两个子链表分别挂在新链表数组的散列位置**，这样就减少了每个链表的长度，增加查找效率。

HashMap 本来是以空间换时间，所以填充比没必要太大。但是填充比太小又会导致空间浪费。**如果关注内存，填充比可以稍大，如果主要关注查找性能，填充比可以稍小。**

## 如何getValue值

简单流程：

1. bucket 里的第一个节点，直接命中；
2. 如果有冲突，则通过 `key.equals(k)` 去查找对应的 entry
   若为树，则在树中通过 `key.equals(k)` 查找，O(logn)；
   若为链表，则在链表中通过 `key.equals(k)` 查找，O(n)。

使用 `get(key)` 方法时获取 key 的 hash 值，计算 `hash&(n-1)` 得到在链表数组中的位置 `first=tab[hash&(n-1)]` ，先判断 first 的 key 是否与参数 key 相等，不等就遍历后面的链表找到相同的 key 值返回对应的 Value 值即可  

``` java
public V get(Object key) {
  Node<K,V> e;
  return (e = getNode(hash(key), key)) == null ? null : e.value;
}

final Node<K,V> getNode(int hash, Object key) {
  Node<K,V>[] tab; //Entry对象数组
  Node<K,V> first,e; //在tab数组中经过散列的第一个位置
  int n;
  K k;
  
  // 找到插入的第一个Node，方法是 hash 值和 n-1 相与，tab[(n - 1) & hash]
  // 也就是说在一条链上的 hash 值相同的
  if ((tab = table) != null && (n = tab.length) > 0 &&(first = tab[(n - 1) & hash]) != null) {
    // 检查第一个Node是不是要找的Node (直接命中)
    if (first.hash == hash && // always check first node
        ((k = first.key) == key || (key != null && key.equals(k)))) // 判断条件是hash值要相同，key值要相同
      return first;
    
    // 检查first后面的 node (未命中)
    if ((e = first.next) != null) {
      // 在树中查找
      if (first instanceof TreeNode)
        return ((TreeNode<K,V>)first).getTreeNode(hash, key);
      // 遍历后面的链表（在链表中查找），找到 key 值和 hash 值都相同的 Node
      do {
        if (e.hash == hash &&
            ((k = e.key) == key || (key != null && key.equals(k))))
          return e;
      } while ((e = e.next) != null);
    }
  }
  return null;
}
```

这段源码就能证实其尊重`hashCode/==/equals` 比较机制，其实 put 也是类似了。

## 如何putValue值

下面简单说下添加键值对 `put(key,value)` 的过程：

1. 判断键值对数组 `tab[]` 是否为空或为 null，否则以默认大小 resize()；
2. 根据键值 key 计算 hash 值得到插入的数组索引 i，如果 `tab[i]==null`，直接新建节点添加，否则转入 3
3. 判断当前数组中处理 hash 冲突的方式为链表还是红黑树 (check 第一个节点类型即可)，分别处理

尤其是冲突的处理，这块需要重点看看。

``` java
public V put(K key, V value) {
  return putVal(hash(key), key, value, false, true);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
  Node<K,V>[] tab; 
  Node<K,V> p; 
  int n, i;
  if ((tab = table) == null || (n = tab.length) == 0)
    // 如果为空，进行初始化
    n = (tab = resize()).length;
  
  // 如果table的在（n-1）&hash的值是空，就新建一个节点插入在该位置
  if ((p = tab[i = (n - 1) & hash]) == null)
    tab[i] = newNode(hash, key, value, null);
  // 表示有冲突,开始处理冲突
  else {
    Node<K,V> e; 
    K k;
    // 检查第一个 Node - p 是不是要找的值
    if (p.hash == hash &&((k = p.key) == key || (key != null && key.equals(k))))
      e = p;
    else if (p instanceof TreeNode)
      // 该链为树
      e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
    else {
      // 该链为链表
      for (int binCount = 0; ; ++binCount) {
        // 指针为空就挂在后面
        if ((e = p.next) == null) {
          p.next = newNode(hash, key, value, null);
          // 如果冲突的节点数已经达到 8 个，看是否需要改变冲突节点的存储结构，　　　　　　　　　　　　　
          // treeifyBin 首先判断当前 hashMap 的长度，如果不足 64，只进行
          // resize，扩容 table，如果达到 64，那么将冲突的存储结构为红黑树
          if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
            treeifyBin(tab, hash);
          break;
        }
        // 如果有相同的 key 值就结束遍历
        if (e.hash == hash &&((k = e.key) == key || (key != null && key.equals(k))))
          break;
        p = e;
      }
    }
    // 就是链表上有相同的 key 值
    if (e != null) { // existing mapping for key，就是key的Value存在
      V oldValue = e.value;
      if (!onlyIfAbsent || oldValue == null)
        e.value = value;
      afterNodeAccess(e);
      return oldValue;// 返回存在的 Value 值
    }
  }
  ++modCount;
  // 如果当前大小大于门限，门限原本是 初始容量*0.75
  if (++size > threshold)
    resize();// 扩容两倍
  afterNodeInsertion(evict);
  return null;
}
```

这样证明了前面所说的 table 的初始化被推迟到了 put 方法中。

## 扩容机制resize()

构造 hash 表时，如果不指明初始大小，默认大小为 16（即 Node 数组大小 16），如果 Node[] 数组中的元素达到（填充比*Node.length）重新调整 HashMap 大小 变为原来 2 倍大小，**扩容很耗时！**

``` java
final Node<K,V>[] resize() {
  Node<K,V>[] oldTab = table;
  int oldCap = (oldTab == null) ? 0 : oldTab.length;
  int oldThr = threshold;
  int newCap, newThr = 0;

  // 如果旧表的长度不是空
  if (oldCap > 0) {
    // 超过最大值就不再扩充了，就只好随你碰撞去吧
    if (oldCap >= MAXIMUM_CAPACITY) {
      threshold = Integer.MAX_VALUE;
      return oldTab;
    }
    // 把新表的长度设置为旧表长度的两倍，newCap=2*oldCap
    else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
             oldCap >= DEFAULT_INITIAL_CAPACITY)
      // 把新表的门限设置为旧表门限的两倍，newThr=oldThr*2
      newThr = oldThr << 1; // double threshold
  }
  // 如果旧表的长度的是0，就是说第一次初始化表
  else if (oldThr > 0) // initial capacity was placed in threshold
    newCap = oldThr;
  else {               // zero initial threshold signifies using defaults
    newCap = DEFAULT_INITIAL_CAPACITY;
    newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
  }

  // 计算新的resize上限
  if (newThr == 0) {
    float ft = (float)newCap * loadFactor;// 新表长度乘以加载因子
    newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
              (int)ft : Integer.MAX_VALUE);
  }
  threshold = newThr;
  @SuppressWarnings({"rawtypes","unchecked"})
  // 下面开始构造新表，初始化表中的数据
  Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
  table = newTab; // 把新表赋值给table
  if (oldTab != null) { // 原表不是空要把原表中数据移动到新表中	
    // 遍历原来的旧表，把每个bucket都移动到新的buckets中
    for (int j = 0; j < oldCap; ++j) {
      Node<K,V> e;
      if ((e = oldTab[j]) != null) {
        oldTab[j] = null;
        if (e.next == null) // 说明这个node没有链表直接放在新表的 e.hash & (newCap - 1) 位置
          newTab[e.hash & (newCap - 1)] = e;
        else if (e instanceof TreeNode)
          ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
        else { // preserve order 保证顺序
          // 如果e后边有链表,到这里表示e后面带着个单链表，需要遍历单链表，将每个结点重
          // 新计算在新表的位置，并进行搬运
          Node<K,V> loHead = null, loTail = null;
          Node<K,V> hiHead = null, hiTail = null;
          Node<K,V> next;

          do {
            next = e.next; // 记录下一个结点
            // 新表是旧表的两倍容量，实例上就把单链表拆分为两队，
            // e.hash&oldCap为偶数一队，e.hash&oldCap为奇数一对
            if ((e.hash & oldCap) == 0) {
              if (loTail == null)
                loHead = e;
              else
                loTail.next = e;
              loTail = e;
            }
            else {
              if (hiTail == null)
                hiHead = e;
              else
                hiTail.next = e;
              hiTail = e;
            }
          } while ((e = next) != null);

          if (loTail != null) { // lo队不为null，放在新表原位置
            loTail.next = null;
            newTab[j] = loHead;
          }
          if (hiTail != null) { // hi队不为null，放在新表j+oldCap位置
            hiTail.next = null;
            newTab[j + oldCap] = hiHead;
          }
        }
      }
    }
  }
  return newTab;
}
```

这里的扩容就有些复杂了，核心就是单链表拆分为双链表（偶数一队，奇数一队）

元素在重新计算 hash 之后，因为n变为 2 倍，那么 n-1 的 mask 范围在高位多 1bit ，因此，我们在扩充 HashMap 的时候，不需要重新计算 hash，只需要看看原来的 hash 值新增的那个 bit 是 1 还是 0 就好了，是 0 的话索引没变，是 1 的话索引变成“原索引 + oldCap” ；

这个设计确实非常的巧妙，既省去了重新计算 hash 值的时间，而且同时，由于新增的 1bit 是 0 还是 1 可以认为是随机的，因此 resize 的过程，均匀的把之前的冲突的节点分散到新的 bucket 了。 

## hash函数的实现

在 get 和 put 的过程中，计算下标时，先对 hashCode 进行 hash 操作，然后再通过 hash 值进一步**计算下标** ，这样就可以将 hash 转换为数组的下标 了。

``` java
static final int hash(Object key) {
  int h;
  return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

可以看到这个函数大概的作用就是：高 16bit 不变，低 16bit 和高 16bit 做了一个异或。 

在设计 hash 函数时，因为目前的 table 长度 n 为 2 的幂，而计算下标的时候，是这样实现的 ( 使用 `&` 位操作，而非 `%` 求余)： ` (n - 1) & hash `

设计者认为这方法很容易发生碰撞。为什么这么说呢？不妨思考一下，在 n - 1 为 15(0x1111) 时，其实散列真正生效的只是低 4bit 的有效位，当然容易碰撞了。

因此，设计者想了一个顾全大局的方法 (综合考虑了速度、作用、质量)，就是把高 16bit 和低 16bit 异或了一下。

设计者还解释到因为现在大多数的 hashCode 的分布已经很不错了，就算是发生了碰撞也用 `O(logn)` 的 tree 去做了。

仅仅异或一下，既减少了系统的开销，也不会造成的因为高位没有参与下标的计算(table长度比较小时)，从而引起的碰撞。

## JDK1.8使用红黑树的改进

问题分析：

> 哈希碰撞会对 hashMap 的性能带来灾难性的影响。如果多个 hashCode() 的值落到同一个桶内的时候，这些值是存储到一个链表中的。
>
> 最坏的情况下，所有的key都映射到同一个桶中，这样hashmap就退化成了一个链表——查找时间从O(1)到O(n)。 

所以在 jdk8 中对 HashMap 的源码进行了优化，在 jdk7 中，HashMap 处理“碰撞”的时候，都是采用链表来存储，当碰撞的结点很多时，查询时间是O（n）。

在 jdk8 中，HashMap 处理“碰撞”增加了红黑树这种数据结构，当碰撞结点较少时，采用链表存储，当较大时（ > 8个），采用红黑树（特点是查询时间是O（logn））存储；

其中有一个阀值控制，大于阀值(8个)，将链表存储转换成红黑树存储。

也就是说：**如果某个桶中的记录过大的话（当前是 TREEIFY_THRESHOLD = 8），HashMap 会动态的使用一个专门的 treemap 实现来替换掉它。这样做的结果会更好，是O(logn)，而不是糟糕的O(n)。** 

---

它是如何工作的？

前面产生冲突的那些 KEY 对应的记录只是简单的追加到一个链表后面，这些记录只能通过遍历来进行查找。

但是超过这个阈值后 HashMap 开始将列表升级成一个**二叉树（红黑树）**，使用哈希值作为树的分支变量，如果两个哈希值不等，但指向同一个桶的话，较大的那个会插入到右子树里（此时是在数组中）。

如果哈希值相等，HashMap 希望 key 值最好是实现了 Comparable 接口的，这样它可以按照顺序来进行插入。

这对 HashMap 的 key 来说并不是必须的，不过如果实现了当然最好。如果没有实现这个接口，在出现严重的哈希碰撞的时候，你就并别指望能获得性能提升了。

## 相关问题

**1. 什么时候会使用HashMap？他有什么特点？**

是基于 Map 接口的实现，存储键值对时，它可以接收 null 的键值，是非同步的，HashMap 存储着 Entry(hash, key, value, next) 对象。

---

**2. 你知道HashMap的工作原理吗？**

通过 hash 的方法，通过 put 和 get 存储和获取对象。

存储对象时，我们将 K/V 传给 put 方法时，它调用 hashCode 计算 hash 从而得到 bucket 位置，进一步存储，HashMap 会根据当前 bucket 的占用情况自动调整容量(超过 Load Facotr 则 resize 为原来的 2 倍)。

获取对象时，我们将 K 传给 get，它调用 hashCode 计算 hash 从而得到 bucket 位置，并进一步调用 equals() 方法确定键值对。

如果发生碰撞的时候，Hashmap 通过链表将产生碰撞冲突的元素组织起来，在 Java 8 中，如果一个bucket中碰撞冲突的元素超过某个限制(默认是8)，则使用红黑树来替换链表，从而提高速度。

---

**3. 你知道get和put的原理吗？equals()和hashCode()的都有什么作用？**
通过对 key 的 hashCode() 进行 hashing，并计算下标 ( n-1 & hash)，从而获得 buckets 的位置。如果产生碰撞，则利用 `key.equals()` 方法去链表或树中去查找对应的节点

---

**4. 你知道hash的实现吗？为什么要这样实现？**

在 Java 1.8 的实现中，是通过 hashCode() 的高 16 位异或低 16 位实现的：`(h = k.hashCode()) ^ (h >>> 16)`，主要是从速度、功效、质量来考虑的，这么做可以在 bucket 的 n 比较小的时候，也能保证考虑到高低 bit 都参与到 hash 的计算中，同时不会有太大的开销。

---

**5. 如果HashMap的大小超过了负载因子(load factor)定义的容量，怎么办？**

如果超过了负载因子(默认0.75)，则会重新 resize 一个原来长度两倍的 HashMap，并且重新调用 hash 方法。

## 参考

https://blog.csdn.net/tuke_tuke/article/details/51588156

https://yikun.github.io/2015/04/01/Java-HashMap%E5%B7%A5%E4%BD%9C%E5%8E%9F%E7%90%86%E5%8F%8A%E5%AE%9E%E7%8E%B0/

