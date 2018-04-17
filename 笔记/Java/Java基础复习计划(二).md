# Java基础复习计划(二)

## 散碎知识点

- 通过 `HttpServletRequest. getParameter()` 获取的参数编码格式由浏览器决定。

  浏览器根据 html 中指定的编码格式进行编码，tomcat 根据指定的格式进行解码，**tomcat 默认解码是 ISO-8859-1.**

  get 请求使用 `new String(username.getBytes("ISO-8859-1"), "UTF-8");`  解决乱码；

  post 请求使用 `request.setCharacterEncoding("utf-8");`

- `for(;;)` 和 `while(true)` 都是无条件循环，使用 javac 编译后他们两个是一样的字节码.

- final 只是指向不变，但是指向的值有可能变，所以依然不是线程安全

- 包装类的 `equals()` 方法不处理数据转型，也就是说用 Integer 的 equals 比较 Long 类型，即使值相同也不返回 true

- 调用 `Object.wait()` 会释放锁，获得执行权后会再尝试获取锁。

- null 可以被强制类型转换成任意类型（不是任意类型对象），于是可以通过它来执行静态方法。

- 字符串常量池在 Java6 之前存放在方法区，而 Java7 中又把常量池移到了堆中（运行时常量池在方法区）

  字符串本身是一个对象啊，对象在堆中，常量池里面放的只是一些引用，这些引用指向了堆中的具体的对象。

- private 和 protected 修饰符不能修饰类。

  一个类如果是私有的，其他都不能访问，那么它只能自己玩自己，没有意义。

  一个类如果是受保护的，那么继承后可以访问，既然未继承之前是不可见的，那么也就无法进行继承，这样就显得毫无意义，不如直接用 default。

- 普通的初始化块 用于初始化非静态的属性；静态初始化块用于初始化静态属性，静态属性只有一份，也就是只会执行一次

  ``` java
  class Test{
    int i = 3;
    // 等价于下面（会被翻译成下面的形式）
    int i;
    {
      i = 3;
    }
    
    // 静态变量同理
    static int a = 2;
    // 等价于
    static int a;
    static {
      a = 2;
    }
    
    // 一个例子
    static {
      x = 3;
      System.out.println(x);
    }
    static int x = 2;
  }
  ```

  可以看到初始化操作时，还是会翻译成代码块的形式依次执行；在最后的一个例子中，打印语句会报错，JVM 不允许完全初始化之前使用变量，然后就是变量定义的初始化优先（语法优先），所以即使把 x 的定义放在下面也是可以编译运行的，顺序就成了：先执行定义语句 `static int x;` 然后依次开始执行 `x = 3;x = 2;` 最终 x 的值就是 2 了。

## 构造方法

是在创建对象的时候需要调用的方法 => 它是收尾的步骤。

注意：并不是用构造方法来创建对象，创建对象的过程是很复杂的，构造方法会在收尾时调用。

构造方法的修饰符默认和类名一致，构造方法的首行默认就是 `super()` 也就是调用父类的无参构造方法。

构造方法的首行 还可能出现 `this()` ，代表在执行当前的构造方法之前先去执行本类的其他构造方法。

> 由于 super() 和 this() 都必须出现在首行，所以它们无法同时存在，并且 super() 是默认值

**构造方法是不可能覆盖的，因为它不会被继承，所以覆盖无从谈起。**

## 参数传递

总结一下就是下面的两条总则：

- 基本数据类型传参赋值的时候 其实就是把值直接复制了一份
- 引用数据类型传引用的值 而引用的值就是一个内存指向的地址

然后来看下面的代码：

``` java
public class TestArgs3{
  public static void main(String[] args){
    String a = new String("O");
    String b = new String("K");
    change(a,b);
    System.out.println(a);//?
    System.out.println(b);//?
  }
  public static void change(String x,String y){
    // 相当于是 String x = a;String y = b;
    String temp = x;
    x = y;
    y = temp;
  }
}
```

虽然 String 是引用数据类型，打印结果依然是 “OK”，a 和 b 的内存地址确实是赋给 x 和 y了，然后执行的交换操作是**交换的局部变量！！** 也就是 x 和 y 的地址确实改变了，但是和 a 、b 没有半毛钱关系。

然后来看个完整版：

``` java
public class TestArgsFinal{
  public static void main(String[] args){
    int num = 2;
    change(num);
    System.out.println(num);//2
    Int ok = new Int(num);
    change(ok);
    System.out.println(ok.i);//3
    changeRef(ok);
    System.out.println(ok.i);//3
  }
  public static void change(int x){
    x = 5;
  }
  public static void change(Int ia){
    ia.i = 3;
  }
  public static void changeRef(Int ia){
    ia = new Int(7);
  }
}
class Int{
  int i;
  public Int(int i){
    this.i = i;
  }
}
```

也就是说，只有通过 `.` 属性的方式修改的，方法结束后才会保留，通过 new 的当方法结束，也就随之消亡了。

##  字符串

在给字符串赋值时，通过 `""` 的话涉及到字符串常量池，new 不会涉及常量池。

当使用 `""` 进行赋值时，内容会被收录到常量池当中，而当再次出现双引号直接赋值的时候，会进行常量池的过滤查找，如果已经出现过，则不会再分配新的空间，而直接指向原有（已经存在的）空间。

StringBuffer/StringBuilder 常用方法：

- append()
- insert()

  在指定的**下标**插入内容，使用的其实是 `System.arraycopy()` 来移动“数组”的。
- reverse()

  反转整个字符串

SB 中默认设置 16 个缓冲区，new 的时候可以手动进行指定。

String 其实就是个 `char[]` ，与 c 不同的是，末尾没有 `\0` 标识。

## 抽象类

重要的几个特点：

1. 抽象类中可以构造方法 
2. 抽象类中可以存在普通属性，方法，静态属性和方法。 
3. 抽象类中可以 存在/不存在 抽象方法。 
4. 如果一个类中有一个抽象方法，那么当前类一定是抽象类；抽象类中不一定有抽象方法。 
5. 抽象类中的抽象方法，需要有子类实现，如果子类不实现，则子类也需要定义为抽象的。 
6. 抽象类不能被实例化，抽象类和抽象方法必须被 abstract 修饰

关键字使用注意： 

抽象类中的抽象方法（其前有 abstract 修饰）不能用 private、static、synchronized、native 访问修饰符修饰。

## 接口

接口的几个重要特点：

1. 在接口中只有方法的声明，没有方法体（JDK1.8 以前）。 
2. **在接口中只有常量，因为定义的变量，在编译的时候都会默认加上 public static final** 
3. **在接口中的方法，永远都被public abstract来修饰。** 
4. 接口中没有构造方法，也不能实例化接口的对象。（所以接口不能继承类） 
5. **接口可以实现多继承**
6. 接口中定义的方法都需要有实现类来实现，如果实现类不能实现接口中的所有方法则实现类定义为抽象类。 
7. 接口可以继承接口，用 extends

JDK8.0 开始 接口当中有两种情况是可能出现方法体的：

1. static 修饰的静态方法
2. default 修饰的默认方法

> Java 中的四大金刚：class、interface、enum、annotation
>
> 他们是同一级别的，编译都会产生 class 文件，后两个是 JDK6.0 出现的吧

## 类加载相关

关于这方面，我找到了一篇写的很棒的文章，备份了一份在：[Github](https://github.com/bfchengnuo/MyRecord/blob/master/%E8%BD%AC%E8%BD%BD%E4%BF%9D%E5%AD%98/Java%E4%B8%AD%E7%B1%BB%E7%9A%84%E5%8A%A0%E8%BD%BD%E6%97%B6%E6%9C%BA.md)

类的加载顺序可以简单归纳为（具有继承关系的情况下）：

1. **父类**静态代码块 (包括静态初始化块，静态属性，但不包括静态方法)
2. **子类**静态代码块 (包括静态初始化块，静态属性，但不包括静态方法 )
3. **父类**非静态代码块 ( 包括非静态初始化块，非静态属性 )
4. **父类**构造函数
5. **子类**非静态代码块 ( 包括非静态初始化块，非静态属性 )
6. **子类**构造函数

其中：**类中静态块按照声明顺序执行（因为是同级的）**，并且 1 和 2 不需要调用 new 类实例的时候就执行了(意思就是在类加载到方法区的时候执行的)

然后再来关注一下方法覆盖的问题，看下面的代码：

``` java
public class Base{
  private String baseName = "base";
  public Base(){
    callName();
  }

  public void callName(){
    System.out.println(baseName);
  }

  static class Sub extends Base{
    private String baseName = "sub";
    public void callName(){
      System.out.println(baseName) ;
    }
  }
  
  public static void main(String[] args){
    Base b = new Sub();
  }
}
```

最后打印的是 null；对于这种动态的多态，编译时表现为 Base 类特性，运行时表现为 Sub 类特性。

当子类覆盖了父类的方法后，意思是父类的方法已经被重写，**父类初始化调用的方法为子类实现的方法**，子类实现的方法中调用的 baseName 为子类中的私有属性。

这时候才执行到第四个步骤，子类非静态代码块和初始化步骤还没有到，子类中的 baseName 还没有被初始化。所以此时  baseName 为空，所以为 null。

## 集合框架

JavaSE 中最重要的知识点之一，Java Collections Framework。

Java当中的集合当中只能存放对象在内存当中的地址，也就是说基本数据类型无法放入，中间会有自动装箱/拆箱的过程，关于包装类有两个特殊的（其他的都是首字母大写而已）就是 Character 和 Integer。

> PS： `Integer.parseInt()` 是将字符串转换为 int 类型；`Integer.valueOf()` 是转换为 Integer 类型。
>
> 自动装箱默认就是调用的 valueOf ，并且默认会缓存 -128 ~ 127 的数，其他的包装类也类似。
>
> 自动拆箱默认调用的是 intValue、floatValue 等等。

总的来说，集合框架可分为两大类，**Collection（单值类型集合）** 和 **Map（键值对集合，主键对象唯一）**；

Collection 又分为 **List（有序，不唯一，可添加空值）** 和 **Set（无序，唯一）**；Map 下还有 **SortedMap（主键对象唯一且有序）**；

Set 想要有序就用 SortedSet 咯，有序唯一。

Collection 系列通用的常用方法：

- addAll

  添加另一个集合

- retainAll

  求交集

- removeAll

  批量“删除”，如果传入的集合包含（contains）调用集合（挨个遍历）就“删除”。

### List

首先看最出名的 ArrayList 的基本使用。

`List<Integer> list = new ArrayList<>();`

JDK5+ 后支持泛型，JDK7+ 后支持泛型自动推断；添加元素除了使用 add 还以可以使用 `Collections.addAll(list,a1,a2,a3)` 后面是一个可变参数，就是相当于一个数组（**重载时，数组和可变参数视为一样的**），可变参数也是 JDK5 加入的。

遍历 List 一般有四种方法：

1. fori + get()

2. foreach

   内部还是使用的迭代器实现，所以**在迭代过程中不允许增加、删除元素**

3. Iterator

   建议的书写方式：

   ``` java
   for(Iterator<?> car = list.iterator(); car.hasNext(); ){
     System.out.println(car.next());
     // 使用迭代器删除元素是安全的
     // car.remove();  // 删除当前指针所处位置的元素
   }
   ```

   使用 for 的目的是可以控制 iterator 的生命周期

4. lambda表达式

   JDK8 的新特性，用起来确实爽：

   ``` java
   list.forEach(System.out::println);
   ```

   使用到了函数式编程，可以往里传一个函数（方法），方法名与类名使用 `::` 分割。

然后下面来看删除，remove 方法有两种重载，可以按照下标来删除，也可以按照对象来删除；这里需要注意的是按照对象删除的时候会用**要删除的对象的 equals 和集合里的元素进行比较，如果返回 true 就进行删除**，所以有时可能需要进行重写 equals 方法。

PS：当删除一个元素后，后面的元素会向前移动，fori 操作时需要注意一下；并且因为要移动所以效率不高，不如从最后删除的快。

针对 ArrayList 有两个常用的专有方法，因为它内部使用的是数组结构：

``` java
ArrayList list = new ArrayList(2);
//扩容，扩容到指定的大小
list.ensureCapacity(20);
//缩容，将空余的空间给释放	
list.trimToSize();
```

建议如果你知道 ArrayList 具体装多少数，那么就在 new 的时候直接指定，避免扩容带来的耗时，也可以使用两个专有方法进行扩容和缩容。

---

下面就来详细说说 ArrayList，从名字就可以看出它是采用数组实现的，下面一些代码参考：

``` java
class MyList{
  private Object[] data;
  private int size;

  public MyList(int x){
    data = new Object[x];
  }
  // 默认空间为 10
  public MyList(){
    this(10);
  }

  public int size(){
    return size;
  }

  public Object get(int x){
    return data[x];
  }

  //添加元素的方法 add()
  public void add(Object obj){
    if(size == data.length){
      Object[] temp = new Object[size*3/2+1];
      System.arraycopy(data,0,temp,0,size);
      data = temp;
    }
    data[size++] = obj;
  }

  //删除元素的方法1 remove(int)
  public void remove(int x){
    // TODO 需要检查是否越界
    System.arraycopy(data,x+1,data,x,size-- - x-1);
  }

  //删除元素的方法2 remove(Object)
  public void remove(Object obj){
    for(int i = 0;i<size;i++){
      if(obj.equals(data[i])){
        remove(i);
        return;
      }
    }
  }
}
```

可以看出内部使用一个计数器来实现记录数组里已经装了多少元素，add 的时候就加一，remove 的时候就减一，add 时还要判断是不是满了，满了就扩容。

至于每次扩容多少呢，在 JDK6- 是 `x*3/2+1` 也就是 1.5 倍，后面的 +1 是为了防止 new 的时候就创建了一个大小为 1 的数。

到了 JDK7+，变成了 `x+(x>>1)` ，还是 1.5 ，变成了位操作更加高效，也不再考虑 1 的情况，为了这一种情况让所有的都 +1 不同值得，前面加个判断就行了，如果是 1 直接变为 2。

然后还有一个就是 Iterator，我们知道 Iterator 为了防止并发错误，在迭代的过程是不允许进行操作的，那么它是如何做到的？

就是 List 中有个 int 类型的值 **modCount** ，它会记录 List 一些进行的操作，每次操作就将其加一，使用 `list.iterator()` 的时候会将其拷贝一份到迭代器中，然后进行 next 操作的时候如果发现拷贝的值和 List 中的值不一样，就意味着发生了变化，那么就会抛出异常了。

#### ArrayList和LinkedList

ArrayList 底层是采用数组实现，数组结构的最大优势就是连续存储，为了保证连续存储所以它的添加和删除都复杂些（有时需要涉及扩容），优势在于查找、遍历和随机访问效率较高。

LinkedList 底层采用链表实现，优势在于添加和删除效率较高，但是查找和随机访问效率较低，所以应该尽量回避它的 get 方法，遍历的话使用 foreach 或者 Iterator 效率会较高。

#### ArrayList和Vector

- 同步特性不同

  也就是说 ArrayList 是线程不安全的，运行多个线程同时操作；

  Vector 是线程安全的，同一时间只允许一个线程操作。

- 底层实现不同

  主要体现在扩容机制上；

  ArrayList 前面已经说过在 JDK6- 是 `x*3/2+1` ，在 JDK7+ 是 `x+(x>>1)` 。

  Vector 的扩容机制体现在 new 对象的时候，如果是 `new Vector(x)` 每一次就是 `x*2` ；如果是 `new Vector(10,x)` 那么每一次就是 `+x` ，初始空间为 10。

- 出现版本不同

  ArrayList 是 JDK1.2 出现的；

  Vector 是 JDK1.0 出现的，集合两大鼻祖之一。

另外，Stack 继承自 Vector 利用数组来模拟的栈结构，主要方法为 push 和 pop

### Set

Collection 的另一大分支，无序、唯一的特性。

**因为它是无序的，所以不再提供 get 方法来获取元素。**

#### HashSet

平常 Set 集合中用的最多的吧，采用哈希表的结构存储数据。

然后，来说一下它的唯一：

**即便是内存当中完全不同的两个对象，也有可能被视作同一个对象；**

**即使是内存当中完全相同的两个对象，也有可能被视为不同的对象。**

这关键就看程序员怎么定义 hashCode 和 equals 方法了。

##### 添加元素的流程

比较流程一共有三个步骤：

1. 比较 hashCode
2. 使用 == 比较
3. 使用 equals 比较

使用代码来表达就是 `1st && (2nd || 3rd)` 。

首先来看哈希码是不是相同，如果不同那肯定不是一个对象；

如果哈希码相同，那么分三种情况：

1. 内存中的同一个对象

   使用 `==` 检查是否是相同的对象，如果是直接就舍弃了。

2. 程序员想要视作相同

   尊重程序员的意愿，调用 equals 进行确认

3. 不是同一个对象，程序员也没想视作相同，是出现了哈希码冲突

   当 equals 返回 false，就是这种情况了，那么就比较这个分组的下一个/添加进集合

还有一个问题，当确认是重复元素时，**采用的是先入为主的方式，后来的就被丢弃了**。

删除也同样尊重这些步骤，同时不再提供 remove(int) 的方式，只支持 remove(Object) 的方式删除元素，更多的是用迭代器来删除。

##### 元素的修改

当需要修改元素时，不能直接修改，尤其是参与了 hashCode 的属性，因为修改以后 hashCode 会随之变化，但是元素在集合中的分组却不会变化，所以就会导致删删不掉，添能重复。

解决方案是在需要修改元素时，按照下面的三个步骤：

1. 删除
2. 修改
3. 重新添加

这样就没什么问题了，就是操作有些繁琐。

就算修改后的 hashCode 处理后还是被分到了一组，也不可能是相同的，因为当添加时，为了避免每次都需要调用这组的 hashCode 方法比较，会把当时的 HashCode 存下来，后面就算修改了这个值也不会再变。

##### 关于存储结构

HashSet 在 new 的时候可以传入两个参数：

1. int 类型的分组组数

   默认为 16，最终一定是 2 的 n 次方

2. float 类型的加载因子

   默认是 0.75F，可以是大于 1.0 的数。

至于分组组数为什么是 2 的 n 次方呢，这是因为在散列分组的时候可以把 `%` 替换为更高效的 `&` 操作，如果你传入的数是 2 的 n 次方，会自动给你设置一个接近的大于这个数的 2 的 n 次方的数。

加载因子的作用是计算阈值，`阈值 = (int)(分组组数*加载因子);` ，作用是控制扩容的最小临界值；也就是说，当超过这个值时才有可能进行扩容，一次扩容就是原来的分组 * 2。

**扩容操作发生时，所有的老元素会重新散列，会很影响效率**，所以应该尽量保证 **分组组数*加载因子>元素总量**

在这个前提下，**分组组数变大，就是牺牲空间，提高效率；分组组数不变，加载因子变大，就是牺牲效率，保证节约空间**。

PS：HashSet 理论是可以无限延伸的，进行扩容是因为效率的原因。

``` java
// 打印距离传入的数的最小的 2 的 n 次方
public static void get(int x){
  int okay = 1;
  while(okay < x){
    okay <<= 1;
  }
  System.out.println(okay);
}

// 计算传入这个数的最小 7 的倍数
public static void get(int x){
  int okay = x/7 * 7 + 2;
}

// 传入一个三位数，抹掉个位数的零头
public static void get(int x){
  int okay = x/10 * 10;
}
```

补充一些相关的算法。

然后 HashSet 内部其实还是使用的是 HashMap，就是做了一层包装，大部分都是直接原封不动的调用 HashMap 的同名方法。

#### TreeSet

有序且唯一的单值类型集合，是 SortedSet 接口的实现。

它有一些专有方法，比如：first()、 last()、 pollFirst()、 pollLast()。

想要放入 TreeSet 集合就必须要实现 Comparable 接口，也就是实现 compareTo 方法，因为要保证有序，所以得和它说按照什么来比较。

compareTo 方法的返回值是 int，**它决定着有序和唯一** ，一般来说 this 指的就是新元素，形参就是老元素。

- 正数

  新元素更大，放在右子树（后面）

- 负数

  新元素更小，放在左子树（前面）

- 零

  元素重复，舍弃

因为 TreeSet 使用的是红黑树来存储的，它是一种自平衡二叉树，每次添加元素会从根依次向下比较，最终找到自己的位置，添加元素后为保证平衡（高效）可能会进行旋转修复，也就是一天是根，不一定永世是根。

PS：优先尊重什么属性就先描述 假如什么属性不同，避免 if 的多层嵌套。

使用 TreeSet 应当尽量保证 compareTo 方法是能返回 0 的，因为它的 remove 方法依赖于 compareTo 的返回值，如果返回值永远不会返回 0（确实有这样的需求），那么就无法通过 remove 删除，只能用迭代器删。

修改元素还是要按照那三个步骤，删除、修改、重新添加；但是在迭代过程中是无法完成添加的，只能先创建一个临时的集合（不一定是 Set，可以是 LinkedList 增删快）将修改的元素加进去，等迭代完成后通过 addAll 方法放进去。

### Map

然后就到键值对集合了，保存的是映射关系，主要的类有两个 HashMap 和 TreeMap。

常用的方法：put(k,v)、get(k)、containsKey(k)、containsValue(v)、remove(k)、putAll(map)

注意最后一个是 putAll 不是 addAll ！！

遍历 Map 的几种方式：keySet() 、values() 、entrySet()、forEach()；无论使用那种方式，得到的其实都不是一个新集合而是原本的 Map 换了个视角而已，也就是说，**如果你在这些集合删除了元素，那么 map 中也会相应的删除**。

PS：values() 返回的是  Collection 类型的。

在 JDK8+ 中使用 forEach 更加的简单，官方 API 解释了默认实现：

``` java
for (Map.Entry<K, V> entry : map.entrySet())
     action.accept(entry.getKey(), entry.getValue());

// 使用 forEach + lambda
map.forEach((k,v) -> System.out.println(k + "--" + v));
```

本质上还是调用的 entrySet，但是写法上真是方便了太多太多。

Map集合添加新的键值对的时候，如果遭遇了重复的主键，那么**新的主键直接舍弃，新来的值替换原来的值**

#### HashMap

HashMap 它的 put(k,v) 、get(k) 、containsKey(k) 、remove(k)，所有和主键有关的方法都尊重 `hashCode/==/equals` 比较机制，前面 hashSet 的时候已经说明了，毕竟 hashSet 的实现就是用的 hashMap。

包括 new 的时候也可以指定分组和加载因子。

#### TreeMap

它的所有主键相关的方法都尊重 compareTo 或 compare 方法，如果它们不返回 0，则：put() 永远不会舍弃元素、get() 永远直接返回 null，containsKey() 永远返回 false，remove() 永远删除失败。

#### HashMap和Hashtable

着重点就是他们的比较，也就是区别：

- 它们的同步特性不同 [多线程是否安全]

  HashMap 同一时间允许多个线程同时进行操作，效率高，但是是线程不安全的。

  Hashtable 同一时间只允许一个线程进行操作，效率低，但是是线程安全的。

- 它们对于 null 的处理不同

  HashMap 无论主键对象还是值对象都可以添加 null（主键唯一，所以主键只能放一个 null）

  Hashtable 无论主键还是值对象都不可以存放 null，否则直接 NPE

- 它们底层实现有些许区别

  HashMap 底层默认分为 16 个小组，可以指定分组，但是最终结果一定是 2 的 n 次方数。

  Hashtable 底层默认分为 11 个小组，可以随意指定分组，最后是 `% (分组组数)` 实现分组。

- 它们出现的版本不同

  HashMap 在 JDK1.2 出现；Hashtable 在 JDK1.0 出现，鼻祖之一。

再补充些关于高并发的：

> JDK5.0 开始 集合的并发包当中提供了多线程高并发的场景下 更高效的 ConcurrentHashMap
>
> 并且出现了一批可以将不安全的集合转换为安全的集合的方法：
>
> Collections.synchronizedMap(hashMap);
>
> Collections.synchronizedList(arrayList);
>
> Collections.synchronizedCollection();
>
> Collections.synchronizedSet();
>
> Collections.synchronizedSortedSet();
>
> Collections.synchronizedSortedMap();
>
> 为什么高效？主要体现在锁的机制上，可以简单理解为 Hashtable 的锁是把整张哈希表锁了，而 ConcurrentHashMap 是锁哈希表中的某一列（每一列可以看作是一条 LinkedList），这样不同的列可以同时操作，只有同一列才会等待，既安全又高效（相比 hashtable）

### 关于迭代器

foreach 的实现就是用的迭代器，并且我们知道用 foreach 遍历如果进行删除（remove）操作是会抛 CME 异常的，这是因为调用 next 的时候发现 modCount 发生了变化。

那么就有一种情况是可以删除不抛异常的，那就是删除倒数第二个元素，每次循环完一次都要调用 hasNext 方法来判断是不是还有元素，这个方法的实现是这样的：

``` java
public boolean hasNext() {
  return cursor != size;
}
```

所以，当删除倒数第二个元素后，size 会减一，cursor 表示的是当前遍历到第几个了，那么这时 cursor 就等于了 size，认为遍历完成，所以也就不会执行 next 方法，也就不会抛出 CME(并发修改异常)。

这个了解一下就行了。

### 关于比较器

也就是实现了 `Comparator<T>` 接口的类，制定一个类的比较规则，就是如何使用比较器。

需要实现的是 `public int compare(T i1,T i2)` 这个方法，第一个为新元素，第二个为老元素，规则和 Comparable 一样。

使用比较器的地方常见的有两处，一个是 List 系列，使用 `Collections.sort(list, com)` 这个方法只适用于 List 系列；

还有一个就是 Set 系列，在 new 的时候直接传比较器就行了。

或者还可以在 lambda 里用，JDK8  的新特性。

``` java
//JDK8.0新特性 lambda表达式
Set<Integer> set = new TreeSet<>((a,b) -> b-a);
// Set<Integer> set = new TreeSet<>(new QQB());
Collections.addAll(set,55,33,11,44,22);
//我要降序
System.out.println(set);

class QQB implements Comparator<Integer>{
  @Override		//i1新来的 i2老元素
  public int compare(Integer i1,Integer i2){
    return i2 - i1;
  }
}
```

这就是常用的几种形式了，用在需要排序的需求上。

## 其他

Java 中没有函数这一叫法，统称为方法。

关于导包，JDK5 以后有一种新形势：`import static java.xxx` 这种相比之前加了一个 static，表示的是只导入此包的静态方法。