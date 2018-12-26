关于“函数式”这个术语，听的也不少了，也稍微听说过他的特性，比如副作用、不变性、声明式编程、引用透明性；以及函数式编程的技术，包括高阶函数、科里化、持久化数据结构、延迟列表、模式匹配以及结合器。

## 共享可变的数据

假设几个类同时都保存了指向某个列表的引用，那么会无法确定谁会在什么时候修改这个列表，我们很难追踪你程序的各个组成部分所发生的变化。

如果一个方法既不修改它内嵌类的状态，也不修改其他对象的状态，使用 return 返回所有的计算结果，那么我们称其为纯粹的或者无副作用的。

简而言之，副作用就是函数的效果已经超出了函数自身的范畴，例如：

- 除了构造器内的初始化操作，对类中数据结构的任何修改，包括字段的赋值操作（一个典型的例子是 setter 方法）
- 抛出一个异常
-  进行输入/输出操作，比如向一个文件写数据

从另一个角度来看“无副作用”的话，我们就应该考虑不可变对象。
不可变对象是这样一种对象，它们一旦完成初始化就不会被任何方法修改状态。这意味着一旦一个不可变对象初始化完毕，它永远不会进入到一个无法预期的状态。你可以放心地共享它，无需保留任何副本，并且由于它们不会被修改，还是线程安全的。

## 声明式编程

一般通过编程实现一个系统，有两种思考方式。一种专注于如何实现，比如：“首先做这个，紧接着更新那个，然后……”

这种“**如何做**”风格的编程非常适合经典的面向对象编程，有些时候我们也称之为**“命令式”编程**，因为它的特点是它的指令和计算机底层的词汇非常相近。

另一种方式则更加关注**要做什么**。使用 Stream API 的时候会比较明显；
一般把最终如何实现的细节留给了函数库。我们把这种思想称之为**内部迭代**。它的巨大优势在于你的查询语句现在读起来就像是问题陈述，由于采用了这种方式，我们马上就能理解它的功能，比理解一系列的命令要简洁得多。

函数式编程具体实践了前面介绍的声明式编程（“你只需要使用不相互影响的表达式，描述想要做什么，由系统来选择如何实现”）和无副作用计算。

## 函数式编程

在函数式编程的上下文中，一个“函数”对应于一个数学函数：
它接受零个或多个参数，生成一个或多个结果，**并且不会有任何副作用**。你可以把它看成一个黑盒，它接收输入并产生一些输出。

要像数学函数那样没有副作用，每个函数都只能使用函数和像 if-then-else 这样的数学思想来构建吗？
或者，我们也允许函数内部执行一些非函数式的操作，只要这些操作的结果不会暴露给系统中的其他部分？换句话说，**如果程序有一定的副作用，不过该副作用不会为其他的调用者感知，是否我们能假设这种副作用不存在呢**？
调用者不需要知道，或者完全不在意这些副作用，因为这对它完全没有影响。

当我们希望能界定这二者之间的区别时，我们将第一种称为纯粹的函数式编程，后者称为函数式编程。

---

编程实战中，你是无法用 Java 语言以纯粹的函数式来完成一个程序的，例如 I/O 中读取一行，通常每一次读取的返回值都是不同的。

在 Java 语言中，如果你希望编写函数式的程序，首先需要做的是确保没有人能觉察到你代码的副作用，这也是函数式的含义。

> 假设这样一个函数或者方法，它没有副作用，进入方法体执行时会对一个字段的值加一，退出方法体之前会对该字段减一。对一个单线程的程序而言，这个方法是没有副作用的，可以看作函数式的实现.
>
> 如果该方法被多线程并发调用，那么就不能称为函数式实现了，当然你可以加锁封装来掩盖这一问题，虽然这样它的副作用对程序而言是不可见的，但是对程序员来说是可见的，其中之一是程序变慢了！

我们的准则是，被称为“函数式”的函数或方法都只能修改本地变量。除此之外，它引用的对象都应该是不可修改的对象。通过这种规定，我们期望所有的字段都为 final 类型，所有的引用类型字段都指向不可变对象。

我们实际也允许对方法中全新创建的对象中的字段进行更新，不过这些字段对于其他对象都是不可见的，也不会因为保存对后续调用结果造成影响。

我们前述的准则是不完备的，要成为真正的函数式程序还有一个附加条件，不过它在最初时不太为大家所重视。要被称为函数式，**函数或者方法不应该抛出任何异常**。
关于这一点，有一个极为简单而又极为教条的解释：你不应该抛出异常，因为一旦抛出异常，就意味着结果被终止了；不再像我们之前讨论的黑盒模式那样，由 return 返回一个恰当的结果值。

但是，如果不能用异常，那如何对形如 1/0 这样的算式结果建模呢？答案是使用 `Optional<T>` 类型！

作为函数式的程序，你的函数或方法调用的库函数如果有副作用，你必须设法隐藏它们的非函数式行为，否则就不能调用这些方法，你可以通过首次复制，或者捕获任何可能抛出的异常实现这一目的；例如，我们通过复制列表的方式，有效地隐藏了方法 insertAll 调用库函数 List.add 所产生的副作用。
这些方法通常会使用注释或者使用标记注释声明的方式进行标注，我们可以将其作为参数传递给并发流处理操作。

~~PS：大多数函数式方法还是会打印日志，严格来说并非函数式~~

## 引用透明

“没有可感知的副作用”（不改变对调用者可见的变量、不进行I/O、不抛出异常）的这些限制都隐含着引用透明性。

> 如果一个函数只要传递同样的参数值，总是返回同样的结果，那这个函数就是引用透明的。

`String.replace` 方法就是引用透明的，调用总是返回同样的结果，而不是更新它的 this 对象，所以它可以被看成函数式的。

换句话说，函数无论在何处、何时调用，如果使用同样的输入总能持续地得到相同的结果，就具备了函数式的特征。

**引用透明性是理解程序的一个重要属性。它还包含了对代价昂贵或者需长时间计算才能得到结果的变量值的优化（通过保存机制而不是重复计算），我们通常将其称为记忆化或者缓存。**

Java 语言中，关于引用透明性还有一个比较复杂的问题。假设你对一个返回列表的方法调用了两次。这两次调用会返回内存中的两个不同列表，不过它们包含了相同的元素。
如果这些列表被当作可变的对象值（因此是不相同的），那么该方法就**不是引用透明的**。
如果你计划将这些列表作为单纯的值（不可修改），那么把这些值看成相同的是合理的，这种情况下该方法是引用透明的。

## 面向对象和函数式对比

因为硬件（多核）与大数据等技术的发展，促使 Java 的软件工程风格在某种程度上愈来愈向函数式的方向倾斜，这样能更简单的处理并发。

关于这两种编程方式，有两种观点：

- 一种支持极端的面向对象：任何事物都是对象，程序要么通过更新字段完成操作，要么调用对与它相关的对象进行更新的方法
- 另一种观点支持引用透明的函数式编程，认为方法不应该有（对外部可见的）对象修改。

实际操作中，Java 程序员经常混用这些风格。你可能会使用包含了可变内部状态的迭代器遍历某个数据结构，同时又通过函数式的方式计算数据结构中的变量之和。

书中有个例子很经典，给定一个列表（`List<value>` ）例如：{1, 4, 9}，构造一个 `List<List<Integer>>` 就是它的子集：{1, 4, 9}、{1, 4}、{1, 9}、{4, 9}、{1}、{4}、{9} 以及 {}。

对于“{1, 4, 9} 的子集可以划分为包含1和不包含 1 的两部分”（有些机智的同学还用二进制来表示，进行排列组合），不包含 1 的子集很简单就是 {4, 9}，包含 1 的子集可以通过将 1 插入到 {4, 9} 的各子集得到。

``` java
static List<List<Integer>> subsets(List<Integer> list) {
  // 如果是空就返回一个“空集合”
  if (list.isEmpty()) {
    List<List<Integer>> ans = new ArrayList<>();
    ans.add(Collections.emptyList());
    return ans;
  }
  Integer first = list.get(0);
  List<Integer> rest = list.subList(1,list.size());
  
  // 递归
  List<List<Integer>> subans = subsets(rest);
  List<List<Integer>> subans2 = insertAll(first, subans);
  return concat(subans, subans2);
}

/**
 * 函数式，不要直接修改传入的对象
 * 利用了 Integer 对象无法修改这一优势，否则需要为每个元素创建一个副本
 */
static List<List<Integer>> insertAll(Integer first,
                                     List<List<Integer>> lists) {
  List<List<Integer>> result = new ArrayList<>();
  for (List<Integer> list : lists) {
    List<Integer> copyList = new ArrayList<>();
    copyList.add(first);
    copyList.addAll(list);
    result.add(copyList);
  }
  return result;
}

// 第一种（推荐）
static List<List<Integer>> concat(List<List<Integer>> a,
                                  List<List<Integer>> b) {
  List<List<Integer>> r = new ArrayList<>(a);
  r.addAll(b);
  return r;
}

// 第二种
static List<List<Integer>> concat(List<List<Integer>> a,
                                  List<List<Integer>> b) {
  a.addAll(b);
  return a;
}
```

第一种 concat 实现是纯粹的函数式。虽然它在内部会对对象进行修改（向列表 r 添加元素），但是它返回的结果基于参数却没有修改任何一个传入的参数。

第二种执行完 concat(subans, subans2) 方法调用后，没人需要再次使用 subans 的值。对于我们定义的 subsets ，这的确是事实，所以使用简化版本的 concat 是个不错的选择，省去了创建对象的开销。
不过，日后如果要复用这段代码那噩梦就开始了。

---

PS：函数式编程中，一般来说会尽量让你避免写像 while 或者 for 这样的迭代构造器，比如 while 条件的改变会导致结果的不一致，它们诱使你修改对象。
一般来说会让你用递归来代替迭代，但是 Java 中的递归效率一般很低。

``` java
// 迭代式阶乘计算
static int factorialIterative(int n) {
  int r = 1;
  for (int i = 1; i <= n; i++) {
    r *= i;
  }
  return r;
}

// 递归式阶乘计算
static long factorialRecursive(long n) {
  return n == 1 ? 1 : n * factorialRecursive(n-1);
}

// 基于Stream的阶乘计算
static long factorialStreams(long n){
  return LongStream.rangeClosed(1, n)
    .reduce(1, (long a, long b) -> a * b);
}
```

每次执行 factorialRecursive 方法调用都会在调用栈上创建一个新的栈帧，用于保存每个方法调用的状态（即它需要进行的乘法运算），这个操作会一直指导程序运行直到结束。这意味着你的递归迭代方法会依据它接收的输入成比例地消耗内存。

一般来说，函数式语言都会对尾递归进行优化，如果用函数式语言，推荐多用尾递归（But Java 貌似还不支持这种优化，起码在 8 版本是不支持，应当尽量使用 Stream 取代迭代操作，从而避免变化带来的影响）。

PS：猜测是因为 JVM 对 goto 指令支持不完善，所以没有专门用于尾递归的指令。

## 函数式编程的技巧

前面说过，函数应该像数学里的一样没有任何副作用，这个术语的范畴更加宽泛，它还意味着函数可以像任何其他值一样随意使用：**可以作为参数传递，可以作为返回值，还能存储在数据结构中**。
能够像普通变量一样使用的函数称为一等函数（first-class function）。这是 Java 8 补充的全新内容：通过 `::` 操作符，你可以创建一个方法引用，像使用函数值一样使用方法；也能使用 Lambda 表达式（比如， `(int x) -> x + 1` ）直接表示方法的值。
Java 8 中使用下面这样的方法引用将一个方法引用保存到一个变量是合理合法的：
`Function<String, Integer> strToInt = Integer::parseInt;`

### 高阶函数

函数式编程的世界里，如果函数，比如 Comparator.comparing ，能满足下面**任一要求**就可以被称为高阶函数（higher-order function）：

- 接受至少一个函数作为参数
- 返回的结果是一个函数

无副作用这一原则在你使用高阶函数时也同样适用。编写高阶函数或者方法时，你无法预知会接收什么样的参数，一旦传入的参数有某些副作用，我们将会一筹莫展！
如果作为参数传入的函数可能对你程序的状态产生某些无法预期的改变，一旦发生问题，你将很难理解程序中发生了什么；因此，将所有你愿意接收的作为参数的函数可能带来的副作用以文档的方式记录下来是一个不错的设计原则，最理想的情况下你接收的函数参数应该没有任何副作用！

### 科里化

科里化是一种将具备 2 个参数（比如， x 和 y ）的函数 f 转化为使用一个参数的函数 g ，并且这个函数的返回值也是一个函数，它会作为新函数的一个参数。后者的返回值和初始函数的返回值相同，即 `f(x,y) = (g(x))(y)` 。

当然，我们可以由此推出：你可以将一个使用了 6 个参数的函数科里化成一个接受第 2、4、6 号参数，并返回一个接受 5 号参数的函数，这个函数又返回一个接受剩下的第 1 号和第 3 号参数的函数。
一个函数使用所有参数仅有部分被传递时，通常我们说这个函数是 部分应用的（partiallyapplied）。

``` java
// 单位转换；乘以转换因子,如果需要，进行基线调整
// x 是你希望转换的数量， f 是转换因子， b 是基线值
static double converter(double x, double f, double b) {
  return x * f + b;
}

static DoubleUnaryOperator curriedConverter(double f, double b){
  return (double x) -> x * f + b;
}

// 使用
DoubleUnaryOperator convertCtoF = curriedConverter(9.0/5, 32);
DoubleUnaryOperator convertUSDtoGBP = curriedConverter(0.6, 0);
double gbp = convertUSDtoGBP.applyAsDouble(1000);
```

### 持久化数据结构

这一术语和数据库中的持久化概念有一定的冲突，数据库中它代表的是“生命周期比程序的执行周期更长的数据。

我们应该注意的第一件事是，函数式方法**不允许修改任何全局数据结构或者任何作为参数传入的参数**。为什么呢？因为一旦对这些数据进行修改，两次相同的调用就很可能产生不同的结构——这违背了引用透明性原则，我们也就无法将方法简单地看作由参数到结果的映射。

函数式编程解决这一问题的方法是禁止使用带有副作用的方法。如果你需要使用表示计算结果的数据结果，那么请创建它的一个副本而不要直接修改现存的数据结构。这一最佳实践也适用于标准的面向对象程序设计。不过，对这一原则，也存在着一些异议，比较常见的是认为这样做会导致过度的对象复制。

所以，简单说，持久化的数据结构可以理解为**数据结构的值始终保持一致，不受其他部分变化的影响**。
此外，一般还会伴随着一个附加原则：所有使用持久化数据结构的用户都必须遵守这一“不修改”原则。使用 final 是个好的想法，不过我们也需要注意 final 只能应用于类的字段，无法应用于它指向的对象，如果你想要对对象进行保护，你需要将其中的字段声明为 final ，以此类推。

## Stream的延迟计算

使用 Stream 确实非常的方便，不过也应该意识到了它的一个比较大的局限性：你无法声明一个递归的 Stream，因为 Stream 仅能使用一次。

Java 8 的 Stream 以其延迟性而著称。它们被刻意设计成这样，即延迟操作，有其独特的原因：Stream 就像是一个黑盒，它接收请求生成结果。
当你向一个 Stream 发起一系列的操作请求时，这些请求只是被一一保存起来。只有当你向 Stream 发起一个终端操作时，才会实际地进行计算。这种设计具有显著的优点，特别是你需要对 Stream 进行多个操作时（你有可能先要进行 filter 操作，紧接着做一个 map ，最后进行一次终端操作 reduce ）；这种方式下 Stream 只需要遍历一次，不需要为每个操作遍历一次所有的元素。

---

然后来说说延迟列表（或者延迟树），它是一种更加通用的 Stream 形式，它提供了一种极好的方式去理解高阶函数；你可以将一个函数作为值放置到某个数据结构中，大多数时候它就静静地待在那里，一旦对其进行调用（即根据需要），它能够创建更多的数据结构。

例如相比 LinkedList 来说 LazyList 的元素由函数在需要使用时动态创建，你可以将它们看成实时延展的。

下面以一个自定义列表的例子来说，传统实现：

``` java
interface MyList<T> {
  T head();
  MyList<T> tail();
  default boolean isEmpty() {
    return true;
  }
}

class MyLinkedList<T> implements MyList<T> {
  private final T head;
  private final MyList<T> tail;
  public MyLinkedList(T head, MyList<T> tail) {
    this.head = head;
    this.tail = tail;
  }
  public T head() {
    return head;
  }
  public MyList<T> tail() {
    return tail;
  }
  public boolean isEmpty() {
    return false;
  }
}

class Empty<T> implements MyList<T> {
  public T head() {
    throw new UnsupportedOperationException();
  }
  public MyList<T> tail() {
    throw new UnsupportedOperationException();
  }
}

// 使用
MyList<Integer> l = new MyLinkedList<>(5, 
                                       new MyLinkedList<>(10, new Empty<>()));
```

然后，如果要把它转换为一个基础的延迟列表：

``` java
class LazyList<T> implements MyList<T>{
  final T head;
  final Supplier<MyList<T>> tail;
  public LazyList(T head, Supplier<MyList<T>> tail) {
    this.head = head;
    this.tail = tail;
  }
  public T head() {
    return head;
  }
  public MyList<T> tail() {
    // 延迟性
    return tail.get();
  }
  public boolean isEmpty() {
    return false;
  }
}

// 使用
public static LazyList<Integer> from(int n) {
	return new LazyList<Integer>(n, () -> from(n+1));
}
LazyList<Integer> numbers = from(2);
int two = numbers.head();
int three = numbers.tail().head();
int four = numbers.tail().tail().head();
System.out.println(two + " " + three + " " + four);
```

### 延迟计算生成质数

必要的基础实现：

``` java
// 1.构造数字组成的 Stream
static Intstream numbers(){
  return IntStream.iterate(2, n -> n + 1);
}
// 2.取得首元素
static int head(IntStream numbers){
  return numbers.findFirst().getAsInt();
}
// 3.对尾元素进行筛选
static IntStream tail(IntStream numbers){
  return numbers.skip(1);
}

IntStream numbers = numbers();
// int head = head(numbers);
// IntStream filtered = tail(numbers).filter(n -> n % head != 0);

// 递归创建由质数组成的 Stream
static IntStream primes(IntStream numbers) {
  int head = head(numbers);
  return IntStream.concat(
    IntStream.of(head),
    primes(tail(numbers).filter(n -> n % head != 0))
  );
}
```

想法是好的，但是 primes 是编译不过的，还是因为那个 Stream 只能用一次，所以不能递归。

使用 Stream 来延迟计算生成：

``` java
public static MyList<Integer> primes(MyList<Integer> numbers) {
  return new LazyList<>(
    numbers.head(),
    () -> primes(
      numbers.tail().filter(n -> n % numbers.head() != 0)
    )
  );
}

// 需要增加的方法
public MyList<T> filter(Predicate<T> p) {
  return isEmpty() ? this : p.test(head()) ?
    new LazyList<>(head(), () -> tail().filter(p)) 
    : tail().filter(p);
}

// 使用
LazyList<Integer> numbers = from(2);
int two = primes(numbers).head();
int three = primes(numbers).tail().head();
int five = primes(numbers).tail().tail().head();
System.out.println(two + " " + three + " " + five);
// 循环
static <T> void printAll(MyList<T> list){
  while (!list.isEmpty()){
    System.out.println(list.head());
    list = list.tail();
  }
}
printAll(primes(from(2)));
// 递归，因为没有尾递归优化，最终会 OOM
static <T> void printAll(MyList<T> list){
  if (list.isEmpty())
    return;
  System.out.println(list.head());
  printAll(list.tail());
}
```

剩下的问题就是性能了，我们很容易得出结论，延迟操作的性能会比提前操作要好，不过，实际情况并非如此简单；开销有可能超过你猜测会带来的好处，除非你仅仅只访问整个数据结构的 10%，甚至更少。
最后，还有一种微妙的方式会导致你的 LazyList 并非真正的延迟计算，一般需要配合缓存来优化。

## 杂项

关于缓存，如果你能保证引用透明性，并且计算的结果代价比较高，可以使用 Map 来封装一个缓存或者说记忆表。

``` java
final Map<Range,Integer> numberOfNodes = new HashMap<>();
Integer computeNumberOfNodesUsingCache(Range range) {
  Integer result = numberOfNodes.get(range);
  if (result != null){
    return result;
  }
  result = computeNumberOfNodes(range);
  numberOfNodes.put(range, result);
  return result;
}

// Java 8改进了 Map 接口，提供了一个名为 computeIfAbsent 的方法处理这样的情况
// 这个方法是引用透明的
Integer computeNumberOfNodesUsingCache(Range range) {
  return numberOfNodes.computeIfAbsent(range,
                                       this::computeNumberOfNodes);
}
```

第一个实现中可以认识到一旦并发和可变状态的对象揉到一起，它们引起的复杂度要远超我们的想象，而函数式编程能从根本上解决这一问题，不过涉及缓存就比较复杂了。