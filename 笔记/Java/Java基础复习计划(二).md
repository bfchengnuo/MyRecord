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

1. 在接口中只有方法的声明，没有方法体。 
2. **在接口中只有常量，因为定义的变量，在编译的时候都会默认加上 public static final** 
3. **在接口中的方法，永远都被public来修饰。** 
4. 接口中没有构造方法，也不能实例化接口的对象。（所以接口不能继承类） 
5. **接口可以实现多继承**
6. 接口中定义的方法都需要有实现类来实现，如果实现类不能实现接口中的所有方法则实现类定义为抽象类。 
7. 接口可以继承接口，用 extends

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

## 其他

Java 中没有函数这一叫法，统称为方法。