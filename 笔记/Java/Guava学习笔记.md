# Guava学习笔记

> Guava 工程包含了若干被 Google 的 Java 项目广泛依赖 的核心库，例如：集合 [collections] 、缓存 [caching] 、原生类型支持 [primitives support] 、并发库 [concurrency libraries] 、通用注解 [common annotations] 、字符串处理 [string processing] 、I/O 等等。 所有这些工具每天都在被 Google 的工程师应用在产品服务中。 

在 JDK8 中，util 中基本已经实现了大部分的 Guava 功能，足以证明 Guava 的优秀，即使这样，学习下 Guava 的风格也是很不错的！

## 基本工具

### 使用和避免null

主要就是 Optional 这个类了，在 JDK8 中也引入了这个类，null 可表达的意思有：已经有一个默认值，或没有值，或找不到值。

使用可以解决这个歧义，简单使用：

``` java
Optional<Integer> possible = Optional.of(5);
possible.isPresent(); // returns true
possible.get(); // returns 5
```

常用方法一览：

**创建 Optional 实例（以下都是静态方法）：**

| 方法           | 说明 |
| ------------------------ | ------------------------------------------------------ |
| Optional.of(T)           | 创建指定引用的 Optional 实例，若引用为 null 则快速失败 |
| Optional.absent()        | 创建引用缺失的 Optional 实例                           |
| Optional.fromNullable(T) | 创建指定引用的 Optional 实例，若引用为 null 则表示缺失 |

**用 Optional 实例查询引用（以下都是非静态方法）：**

| 方法| 说明  |
| ------------------- | ------------------------------------------------------------ |
| boolean isPresent() | 如果 Optional 包含非 null 的引用（引用存在），返回true |
| T get()             | 返回 Optional 所包含的引用，若引用缺失，则抛出 java.lang.IllegalStateException |
| T or(T)             | 返回 Optional 所包含的引用，若引用缺失，返回指定的值         |
| T orNull()          | 返回 Optional 所包含的引用，若引用缺失，返回 null            |
| Set asSet()         | 返回 Optional 所包含引用的单例不可变集，如果引用存在，返回一个只有单一元素的集合，如果引用缺失，返回一个空集合。 |

设置对象为空时的默认值可以使用这个：`Optional.of(first).or(second)` （测试 of 会快速失败，并不能实现，但是用 fromNullable 可以）

还有其它一些方法专门处理 null 或空字符串（可看看 Strings 类）：emptyToNull(String)，nullToEmpty(String)，isNullOrEmpty(String)。

还有其他的一些静态方法也很实用，例如 checkNotNull。

### 常见的Object方法

#### equal

当一个对象中的字段可以为 null 时，实现 Object.equals 方法会很痛苦，因为不得不分别对它们进行 null 检查。使用 Objects.equal 帮助你执行 null 敏感的 equals 判断，从而避免抛出 NullPointerException。例如:

``` java
Objects.equal("a", "a"); // returns true
Objects.equal(null, "a"); // returns false
Objects.equal("a", null); // returns false
Objects.equal(null, null); // returns true
```

**注意：JDK7 引入的 Objects 类提供了一样的方法 Objects.equals。**

---

#### hashCode

用对象的所有字段作散列[hash]运算应当更简单。Guava 的 Objects.hashCode(Object...) 会对传入的字段序
列计算出合理的、顺序敏感的散列值。你可以使用 Objects.hashCode(field1, field2, …, fieldn) 来代替手动计算
散列值。

注意：JDK7 引入的 Objects 类提供了一样的方法 Objects.hash(Object...)

这个在重写 hashCode 方法的时候会很有用！

---

#### toString

好的 toString 方法在调试时是无价之宝，但是编写 toString 方法有时候却很痛苦。使用 Objects.toStringHelper 可以轻松编写有用的 toString 方法。例如：

``` java
// Returns "ClassName{x=1}"
Objects.toStringHelper(this).add("x", 1).toString();
// Returns "MyObject{x=1}"
Objects.toStringHelper("MyObject").add("x", 1).toString();
```

同样这个在重写 toString 时会很有用，~~但是都用 IDE 自动生成嘛。。~~

---

#### compare/compareTo

相比之前需要写繁琐的 implements Comparable 实现，Guava 提供了 ComparisonChain。

ComparisonChain 执行一种懒比较：它执行比较操作直至发现非零的结果，在那之后的比较输入将被忽略。

``` java
public int compareTo(Foo that) {
  return ComparisonChain.start()
    .compare(this.aString, that.aString)
    .compare(this.anInt, that.anInt)
    .compare(this.anEnum, that.anEnum, Ordering.natural().nullsLast())
    .result();
}
```

这种 Fluent 接口风格的可读性更高，发生错误编码的几率更小，并且能避免做不必要的工作。

### 排序

排序器 [Ordering] 是 Guava 流畅风格比较器 [Comparator] 的实现，它可以用来为构建复杂的比较器，以完成集合排序的功能。

从实现上说，Ordering 实例就是一个特殊的 Comparator 实例。Ordering 把很多基于 Comparator 的静态方法（如 Collections.max）包装为自己的实例方法（非静态方法），并且提供了链式调用方法，来定制和增强现有的比较器。

**创建排序器**：常见的排序器可以由下面的静态方法创建

| **方法**         | **描述**                                                |
| ---------------- | ------------------------------------------------------- |
| natural()        | 对可排序类型做自然排序，如数字按大小，日期按先后排序    |
| usingToString()  | 按对象的字符串形式做字典排序 [lexicographical ordering] |
| from(Comparator) | 把给定的 Comparator 转化为排序器                        |

实现自定义的排序器时，除了用上面的 from 方法，也可以跳过实现 Comparator，而直接继承 Ordering：

``` java
Ordering<String> byLengthOrdering = new Ordering<String>() {
  public int compare(String left, String right) {
    return Ints.compare(left.length(), right.length());
  }
};
```

当然，我最喜欢的是链式调用了！（Guava 中很多方法都是可以链式的）

| **方法**             | **描述**                                                     |
| -------------------- | ------------------------------------------------------------ |
| reverse()            | 获取语义相反的排序器                                         |
| nullsFirst()         | 使用当前排序器，但额外把 null 值排到最前面。                 |
| nullsLast()          | 使用当前排序器，但额外把 null 值排到最后面。                 |
| compound(Comparator) | 合成另一个比较器，以处理当前排序器中的相等情况。             |
| lexicographical()    | 基于处理类型 T 的排序器，返回该类型的可迭代对象 `Iterable<T>` 的排序器。 |
| onResultOf(Function) | 对集合中元素调用 Function，再按返回值用当前排序器排序。      |

当阅读链式调用产生的排序器时，应该从后往前读。

用 compound 方法包装排序器时，就不应遵循从后往前读的原则。为了避免理解上的混乱，请不要把 compound 写在一长串链式调用的中间，你可以另起一行，在链中最先或最后调用 compound。

## 集合

### 不可变集合

不可变对象有很多优点，包括：

- 当对象被不可信的库调用时，**不可变形式是安全的**；
- 不可变对象被多个线程调用时，不存在竞态条件问题
- 不可变集合不需要考虑变化，因此**可以节省时间和空间**。所有不可变的集合都比它们的可变形式有更好的内存利用率（分析和测试细节）；
- 不可变对象因为有固定不变，**可以作为常量来安全使用**。

创建对象的不可变拷贝是一项很好的防御性编程技巧。Guava 为所有 JDK 标准集合类型和 Guava 新集合类型都提供了简单易用的不可变版本。

JDK 也提供了 `Collections.unmodifiableXXX` 方法把集合包装为不可变形式，但我们认为不够好：

- 笨重而且累赘：不能舒适地用在所有想做防御性拷贝的场景；
- 不安全：要保证没人通过原集合的引用进行修改，返回的集合才是事实上不可变的；
- 低效：包装过的集合仍然保有可变集合的开销，比如并发修改的检查、散列表的额外空间，等等。

> 重要提示：**所有 Guava 不可变集合的实现都不接受 null 值。**
>
> 我们对 Google 内部的代码库做过详细研究，发现只有 5% 的情况需要在集合中允许 null 元素，剩下的 95%场景都是遇到 null 值就快速失败。
>
> 如果你需要在不可变集合中使用 null，请使用 JDK 中的 Collections.unmodifiableXXX 方法。更多细节建议请参考“使用和避免 null”。

下面就来看一个例子：

``` java
public static final ImmutableSet<String> COLOR_NAMES = ImmutableSet.of(
  "red",
  "orange",
  "yellow",
  "green",
  "blue",
  "purple");

class Foo {
  Set<Bar> bars;
  Foo(Set<Bar> bars) {
    this.bars = ImmutableSet.copyOf(bars); // defensive copy!
  }
}
```

不可变集合可以用如下多种方式创建：

- copyOf 方法，如 `ImmutableSet.copyOf(set);`

- of 方法，如 `ImmutableSet.of("a", "b", "c")` 或 `ImmutableMap.of("a", 1, "b", 2);`

- Builder 工具

  ``` java
  public static final ImmutableSet<Color> GOOGLE_COLORS =
    ImmutableSet.<Color>builder()
    .addAll(WEBSAFE_COLORS)
    .add(new Color(0, 191, 255))
    .build();
  ```

此外，对有序不可变集合来说，**排序是在构造集合的时候完成的** ，例如：`ImmutableSortedSet.of("a", "b", "c", "a", "d", "b");`

### 更智能的 copyOf

`ImmutableXXX.copyOf` 方法会尝试在安全的时候避免做拷贝——实际的实现细节不详，但通常来说是很智能的，比如：

```java
ImmutableSet<String> foobar = ImmutableSet.of("foo", "bar", "baz");
thingamajig(foobar);

void thingamajig(Collection<String> collection) {
  ImmutableList<String> defensiveCopy = ImmutableList.copyOf(collection);
  // ...
}
```

在这段代码中，`ImmutableList.copyOf(foobar)` 会智能地直接返回 `foobar.asList()`，它是一个 ImmutableSet 的常量时间复杂度的 List 视图。

作为一种探索，`ImmutableXXX.copyOf(ImmutableCollection)` 会在可能的情况下**避免线性拷贝**，可以最大限度地减少防御性编程风格所带来的性能开销。

---

### 附录

| **可变集合接口**       | 属于JDK还是Guava | **不可变版本**              |
| ---------------------- | ---------------- | --------------------------- |
| Collection             | JDK              | ImmutableCollection         |
| List                   | JDK              | ImmutableList               |
| Set                    | JDK              | ImmutableSet                |
| SortedSet/NavigableSet | JDK              | ImmutableSortedSet          |
| Map                    | JDK              | ImmutableMap                |
| SortedMap              | JDK              | ImmutableSortedMap          |
| Multiset               | Guava            | ImmutableMultiset           |
| SortedMultiset         | Guava            | ImmutableSortedMultiset     |
| Multimap               | Guava            | ImmutableMultimap           |
| ListMultimap           | Guava            | ImmutableListMultimap       |
| SetMultimap            | Guava            | ImmutableSetMultimap        |
| BiMap                  | Guava            | ImmutableBiMap              |
| ClassToInstanceMap     | Guava            | ImmutableClassToInstanceMap |
| Table                  | Guava            | ImmutableTable              |

### 新集合类型

#### Multiset

> Guava 提供了一个新集合类型 Multiset，它可以**多次添加相等的元素**。**Multiset 元素的顺序是无关紧要的**：Multiset {a, a, b}和{a, b, a}是相等的”。
>
> Multiset继承自 JDK 中的 Collection 接口，而不是 Set 接口，所以包含重复元素并没有违反原有的接口契约。

可以用两种方式看待 Multiset：

- 没有元素顺序限制的 ArrayList
- `Map<E, Integer>`，键为元素，**值为计数**

Guava 的 Multiset API 也结合考虑了这两种方式：

当把 Multiset 看成普通的 Collection 时，它表现得就像无序的 ArrayList：

- add(E)
  添加单个给定元素
- iterator()
  返回一个迭代器，包含 Multiset 的所有元素（包括重复的元素）
- size()
  返回所有元素的总个数（包括重复的元素）

当把 Multiset 看作 `Map<E, Integer>` 时，它也提供了符合性能期望的查询操作：

- count(Object)
  返回给定元素的计数。HashMultiset.count 的复杂度为 O(1)，TreeMultiset.count 的复杂度为 O(log n)。
- entrySet()
  返回 Set<Multiset.Entry>，和 Map 的 entrySet 类似。
- elementSet()
  返回所有不重复元素的 Set，和 Map 的 keySet()类似。
- **所有 Multiset 实现的内存消耗随着不重复元素的个数线性增长。**

值得注意的是，除了极少数情况，Multiset 和 JDK 中原有的 Collection 接口契约完全一致——具体来说，TreeMultiset 在判断元素是否相等时，与 TreeSet 一样用 compare，而不是 Object.equals。

| **方法**         | **描述**                                                     |
| ---------------- | ------------------------------------------------------------ |
| count(E)         | 给定元素在 Multiset 中的计数                                 |
| elementSet()     | Multiset 中不重复元素的集合，类型为 `Set<E>`                 |
| entrySet()       | 和 Map 的 entrySet 类似，返回 `Set<Multiset.Entry<E>>`，其中包含的 Entry 支持 getElement()和 getCount()方法 |
| add(E, int)      | 增加给定元素在 Multiset 中的计数                             |
| remove(E, int)   | 减少给定元素在 Multiset 中的计数                             |
| setCount(E, int) | 设置给定元素在 Multiset 中的计数，不可以为负数               |
| size()           | 返回集合元素的总个数（包括重复的元素）                       |

Multiset 不是 Map！

请注意，Multiset 不是 `Map<E, Integer>`，虽然 Map 可能是某些 Multiset 实现的一部分。**准确来说 Multiset 是一种 Collection 类型，并履行了 Collection 接口相关的契约**。关于 Multiset 和 Map 的显著区别还包括：

- Multiset 中的元素计数只能是正数。
  任何元素的计数都不能为负，也不能是 0。elementSet() 和 entrySet() 视图中也不会有这样的元素。
- `multiset.size()` 返回集合的大小，**等同于所有元素计数的总和。**
  对于不重复元素的个数，应使用 `elementSet().size()` 方法。（因此，add(E) 把 `multiset.size()` 增加 1）
- `multiset.iterator()` 会迭代重复元素，因此迭代长度等于 `multiset.size()`。
- Multiset 支持直接增加、减少或设置元素的计数。
  `setCount(elem, 0)` 等同于移除所有 elem。
- 对 multiset 中没有的元素，`multiset.count(elem)` 始终返回 0。

Guava 提供了多种 Multiset 的实现，大致对应 JDK 中 Map 的各种实现：

| **Map**           | 对应的Multiset         | 是否支持null元素               |
| ----------------- | ---------------------- | ------------------------------ |
| HashMap           | HashMultiset           | 是                             |
| TreeMap           | TreeMultiset           | 是（如果 comparator 支持的话） |
| LinkedHashMap     | LinkedHashMultiset     | 是                             |
| ConcurrentHashMap | ConcurrentHashMultiset | 否                             |
| ImmutableMap      | ImmutableMultiset      | 否                             |

#### Multimap

每个有经验的 Java 程序员都在某处实现过 `Map<K, List>` 或 `Map<K, Set>`，并且要忍受这个结构的笨拙。Guava 的 Multimap 可以很容易地把一个键映射到多个值。换句话说，Multimap 是把键映射到任意多个值的一般方式。

> 可理解为两种映射：
>
> 键-单个值映射 ：a -> 1， a -> 2， a ->4 b -> 3， c -> 5 ；
>
> 键-值集合映射 ：a -> [1, 2, 4]， b -> 3， c -> 5 ；

一般来说，Multimap 接口应该用第一种方式看待，但 asMap() 视图返回 `Map<K, Collection>`，让你可以按另一种方式看待 Multimap。

举个例子：

``` java
public void testMultimap() {
  ListMultimap<String, String> myMultimap = ArrayListMultimap.create();
  myMultimap.put("Fruits", "Bannana");
  myMultimap.put("Fruits", "Apple");
  myMultimap.put("Fruits", "Pear");
  myMultimap.put("Fruits", "Pear");
  myMultimap.put("Vegetables", "Carrot");

  myMultimap.entries().forEach(entry -> {
    System.out.println(entry.getKey() + "::" + entry.getValue());
  });
}
```

**重要的是，不会有任何键映射到空集合：一个键要么至少到一个值，要么根本就不在 Multimap 中。**

很少会直接使用 Multimap 接口，更多时候你会用 ListMultimap 或 SetMultimap 接口，它们分别把键映射到 List 或 Set。

``` java
// 即使没有任何对应的值，也返回空集合
Set<Person> aliceChildren = childrenMultimap.get(alice);
aliceChildren.clear();
aliceChildren.add(bob);
aliceChildren.add(carol);
```

此外 Multimap 还可以返回多个视图，它虽然使用了 Map 作为实现方式，但并不能说它是一个 Map。

- `Multimap.get(key)` 总是返回非 null、但是可能空的集合。
  这并不意味着 Multimap 为相应的键花费内存创建了集合，而只是提供一个集合视图方便你为键增加映射值；

  如果有这样的键，返回的集合只是包装了 Multimap 中已有的集合；如果没有这样的键，返回的空集合也只是持有 Multimap 引用的栈对象，让你可以用来操作底层的 Multimap。因此，返回的集合不会占据太多内存，数据实际上还是存放在 Multimap 中。

- 如果你更喜欢像 Map 那样，为 Multimap 中没有的键返回 null，请使用 asMap() 视图获取一个 `Map<K, Collection>`。

- 当且仅当有值映射到键时，`Multimap.containsKey(key)` 才会返回 true。
  尤其需要注意的是，如果键 k 之前映射过一个或多个值，但它们都被移除后，会返回 false。

- `Multimap.entries()` 返回 Multimap 中所有”键-单个值映射”——包括重复键。
  如果你想要得到所有”键-值集合映射”，请使用 `asMap().entrySet()`。

- `Multimap.size()` 返回所有”键-单个值映射”的个数，而非不同键的个数。要得到不同键的个数，请改用 `Multimap.keySet().size()`。

| **实现**              | **键行为类似** | **值行为类似** |
| --------------------- | -------------- | -------------- |
| ArrayListMultimap     | HashMap        | ArrayList      |
| HashMultimap          | HashMap        | HashSet        |
| LinkedListMultimap    | LinkedHashMap  | LinkedList     |
| LinkedHashMultimap    | LinkedHashMap  | LinkedHashMap  |
| TreeMultimap          | TreeMap        | TreeSet        |
| ImmutableListMultimap | ImmutableMap   | ImmutableList  |
| ImmutableSetMultimap  | ImmutableMap   | ImmutableSet   |

除了两个不可变形式的实现，其他所有实现都支持 null 键和 null 值 ；

`LinkedListMultimap.entries()` 保留了所有键和值的迭代顺序 

`LinkedHashMultimap` 保留了映射项的插入顺序，包括键插入的顺序，以及键映射的所有值的插入顺序。 

请注意，并非所有的 Multimap 都和上面列出的一样，使用 `Map<K, Collection>` 来实现（特别是，一些 Multimap 实现用了自定义的 hashTable，以最小化开销） 

#### BiMap

传统上，实现键值对的双向映射需要维护两个单独的 map，并保持它们间的同步。但这种方式很容易出错，而且对于值已经在 map 中的情况，会变得非常混乱。 

`BiMap<K, V>` 是特殊的 Map：

- 可以用 `inverse()` 反转 `BiMap<K, V>` 的键值映射
- 保证值是唯一的，因此 values() 返回 Set 而不是普通的 Collection

在 BiMap 中，如果你想把键映射到已经存在的值，会抛出 IllegalArgumentException 异常。如果对特定值，你想要强制替换它的键，请使用 `BiMap.forcePut(key, value)`。

---

其他的一些集合用的比较少，不再列举

### 强大的集合工具类

任何对 JDK 集合框架有经验的程序员都熟悉和喜欢 java.util.Collections 包含的工具方法。Guava 沿着这些路线提供了更多的工具方法：适用于所有集合的静态方法。这是 Guava 最流行和成熟的部分之一。

然而我常用的也就是几个，比如 Lists、Maps、Collections2 等的静态工厂。

``` java
List<Type> exactly100 = Lists.newArrayListWithCapacity(100);
List<Type> approx100 = Lists.newArrayListWithExpectedSize(100);
Set<Type> approx100Set = Sets.newHashSetWithExpectedSize(100);
```

仔细想想，我好像也就用这几个。。。

## 字符串处理

### 连接器[Joiner]

用分隔符把字符串序列连接起来也可能会遇上不必要的麻烦。如果字符串序列中含有 null，那连接操作会更难。Fluent 风格的 Joiner 让连接字符串更简单。

``` java
Joiner joiner = Joiner.on("; ").skipNulls();
return joiner.join("Harry", null, "Ron", "Hermione");
```

另外，`useForNull(String) ` 方法可以给定某个字符串来替换 null，而不像 skipNulls() 方法是直接忽略 null。 Joiner 也可以用来连接对象类型，在这种情况下，它会把对象的 toString() 值连接起来。 

> 警告：joiner 实例总是不可变的。用来定义 joiner 目标语义的配置方法总会返回一个新的 joiner 实例。这使得 joiner 实例都是线程安全的，你可以将其定义为 static final常量。

### 拆分器[Splitter]

JDK 内建的字符串拆分工具有一些古怪的特性。比如，String.split 悄悄丢弃了尾部的分隔符。

 Splitter 使用令人放心的、直白的流畅 API 模式对这些混乱的特性作了完全的掌控。

``` java
Splitter.on(',')
  .trimResults()
  .omitEmptyStrings()
  .split("foo,bar,,   qux");
```

上述代码返回 Iterable，其中包含"foo"、"bar" 和 "qux"。Splitter 可以被设置为按照任何模式、字符、字符串或字符匹配器拆分。 

拆分器工厂：

| **方法**                                              | **描述**                                               | **范例**                                     |
| ----------------------------------------------------- | ------------------------------------------------------ | -------------------------------------------- |
| Splitter.on(char)                                     | 按单个字符拆分                                         | Splitter.on(";")                             |
| Splitter.on(CharMatcher)                              | 按字符匹配器拆分                                       | Splitter.on(CharMatcher.BREAKING_WHITESPACE) |
| Splitter.on(String)                                   | 按字符串拆分                                           | Splitter.on(",")                             |
| Splitter.on(Pattern) <br />Splitter.onPattern(String) | 按正则表达式拆分                                       | Splitter.onPattern("\r?\n")                  |
| Splitter.fixedLength(int)                             | 按固定长度拆分；最后一段可能比给定长度短，但不会为空。 | Splitter.fixedLength(3)                      |

拆分器修饰符：

| **方法**                 | **描述**                                               |
| ------------------------ | ------------------------------------------------------ |
| omitEmptyStrings()       | 从结果中自动忽略空字符串                               |
| trimResults()            | 移除结果字符串的前导空白和尾部空白                     |
| trimResults(CharMatcher) | 给定匹配器，移除结果字符串的前导匹配字符和尾部匹配字符 |
| limit(int)               | 限制拆分出的字符串数量                                 |

如果你想要拆分器返回 List，只要使用 `Lists.newArrayList(splitter.split(string))` 或类似方法。 

警告：splitter 实例总是不可变的。用来定义 splitter 目标语义的配置方法总会返回一个新的 splitter 实例。这使得 splitter 实例都是线程安全的，你可以将其定义为 static final 常量。

### 字符匹配器[CharMatcher]

直观上，你可以认为一个 CharMatcher 实例代表着某一类字符，如数字或空白字符。事实上来说，CharMatcher 实例就是**对字符的布尔判断** ;

CharMatcher 确实也实现了 Predicate——但类似”所有空白字符”或”所有小写字母”的需求太普遍了，Guava 因此创建了这一 API。

然而使用 CharMatcher 的好处更在于它提供了一系列方法，让你对字符作特定类型的操作：修剪[trim]、折叠[collapse]、移除[remove]、保留[retain]等等。

```java
String noControl = CharMatcher.JAVA_ISO_CONTROL.removeFrom(string); //移除control字符
String theDigits = CharMatcher.DIGIT.retainFrom(string); //只保留数字字符
String spaced = CharMatcher.WHITESPACE.trimAndCollapseFrom(string, ' ');
//去除两端的空格，并把中间的连续空格替换成单个空格
String noDigits = CharMatcher.JAVA_DIGIT.replaceFrom(string, "*"); //用*号替换所有数字
String lowerAndDigit = CharMatcher.JAVA_DIGIT.or(CharMatcher.JAVA_LOWER_CASE).retainFrom(string);
// 只保留数字和小写字母
```

注：CharMatcher 只处理 char 类型代表的字符；它不能理解 0x10000 到 0x10FFFF 的 Unicode 增补字符。 

---

获取字符匹配器可以使用 is(char)、anyOf(CharSequence) 、inRange(char, char)  等方法。

| **方法**                                | **描述**                                                     |
| --------------------------------------- | ------------------------------------------------------------ |
| collapseFrom(CharSequence, char)        | 把每组连续的匹配字符替换为特定字符。如 WHITESPACE.collapseFrom(string, ‘ ‘)把字符串中的连续空白字符替换为单个空格。 |
| matchesAllOf(CharSequence)              | 测试是否字符序列中的所有字符都匹配。                         |
| removeFrom(CharSequence)                | 从字符序列中移除所有匹配字符。                               |
| retainFrom(CharSequence)                | 在字符序列中保留匹配字符，移除其他字符。                     |
| trimFrom(CharSequence)                  | 移除字符序列的前导匹配字符和尾部匹配字符。                   |
| replaceFrom(CharSequence, CharSequence) | 用特定字符序列替代匹配字符。                                 |

### 字符集[Charsets]

Charsets 针对所有 Java 平台都要保证支持的六种字符集提供了常量引用。尝试使用这些常量，而不是通过名称获取字符集实例。

比如这样引用 U8 编码：`Charsets.UTF_8`

## 其他

还剩下缓存和并发感觉很有必要，待补充。