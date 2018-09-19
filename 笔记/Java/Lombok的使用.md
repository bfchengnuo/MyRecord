# Lombok的使用

lombok 是一个**编译级别**的**插件**，它可以在项目编译的时候生成一些代码。在很多工具类的项目中都有这个功能。比如 dagger。

通俗的说，lombok 可以通过注解来标示生成 `getter` `settter` 等代码，这也应该是我们用的最多的一个功能。我们自然可以通过编译器比如 IDEA 的 `Generate` 生成，为啥要用这个？

在项目开发阶段，一个 class 的属性是一直变化的，今天可能增加一个字段，明天可能删除一个字段。每次变化都需要修改对应的模板代码。另外，有的 class 的字段超级多，多到一眼看不完。如果加上模板代码，更难一眼看出来。更有甚者，由于字段太多，想要使用 builder 来创建。手动创建 builder 和字段和原来的类夹杂在一起，看起来真的难受。lombok 的 `@Builder` 即可解决这个问题。

## 常用注解

这是使用中最重要最常用的一块，基本都是使用注解来完成的，那些不常用的就不多说了。

- @Getter
  不多说，可以用在类上，也可以属性上；
  另外，可以使用 `@Getter(lazy=true)` 来进行属性的懒加载。
- @Setter
  不多说，可以用在类上，也可以属性上
- @ToString
- @Data
  这是一个集合体。
  包含 Getter、Setter、RequiredArgsConstructor、ToString、EqualsAndHashCode
- @EqualsAndHashCode
- 构造函数类
  @NoArgsConstructor、@RequiredArgsConstructor、@AllArgsConstructor
- @NonNull
  标注这个字段不应为 null，初始化的时候会检查是否为空，否则抛出 NullPointException。
  @RequiredArgsConstructor 则会生成带有这些字段的构造器。
- @Builder
  builder 是现在比较推崇的一种构建值对象的方式，也就是生成器模式，下面会介绍的。
- @Log
  再也不用写初始化 log 对象了，直接拿着 log 用就行了。
  具体的有 @Log4j 和 @Slf4j
- @Accessors(chain = true)
  支持链式调用

特别的，关于布尔类型，setter 一致，但 getter 不同，小写的`boolean`，即基本类型，前缀是`is` ；Boolean，即包装类型，前缀是`get`；

## val

如果对其他的语言有研究的会发现，很多语言是使用 var 作为变量申明，val 作为常量申明。这里的val也是这个作用。

```java
public String example() {
  val example = new ArrayList<String>();
  example.add("Hello, World!");
  val foo = example.get(0);
  return foo.toLowerCase();
}
```

翻译成 Java 程序是：

```java
public String example() {
  final ArrayList<String> example = new ArrayList<String>();
  example.add("Hello, World!");
  final String foo = example.get(0);
  return foo.toLowerCase();
}
```

也就是类型推导啦。

## @NonNull

Null 即是罪恶

```java
public class NonNullExample extends Something {
  private String name;

  public NonNullExample(@NonNull Person person) {
    super("Hello");
    this.name = person.getName();
  }
}
```

翻译成 Java 程序是：

```java
public class NonNullExample extends Something {
  private String name;

  public NonNullExample(@NonNull Person person) {
    super("Hello");
    if (person == null) {
      throw new NullPointerException("person");
    }
    this.name = person.getName();
  }
}
```

## @Builder

初始代码：

``` java
@Builder
public class BuilderExample {
  private String name;
  private int age;
  @Singular private Set<String> occupations;
}
```

翻译后：

```java
public class BuilderExample {
  private String name;
  private int age;
  private Set<String> occupations;

  BuilderExample(String name, int age, Set<String> occupations) {
    this.name = name;
    this.age = age;
    this.occupations = occupations;
  }

  public static BuilderExampleBuilder builder() {
    return new BuilderExampleBuilder();
  }

  public static class BuilderExampleBuilder {
    private String name;
    private int age;
    private java.util.ArrayList<String> occupations;

    BuilderExampleBuilder() {
    }

    public BuilderExampleBuilder name(String name) {
      this.name = name;
      return this;
    }

    public BuilderExampleBuilder age(int age) {
      this.age = age;
      return this;
    }

    public BuilderExampleBuilder occupation(String occupation) {
      if (this.occupations == null) {
        this.occupations = new java.util.ArrayList<String>();
      }

      this.occupations.add(occupation);
      return this;
    }

    public BuilderExampleBuilder occupations(Collection<? extends String> occupations) {
      if (this.occupations == null) {
        this.occupations = new java.util.ArrayList<String>();
      }

      this.occupations.addAll(occupations);
      return this;
    }

    public BuilderExampleBuilder clearOccupations() {
      if (this.occupations != null) {
        this.occupations.clear();
      }

      return this;
    }

    public BuilderExample build() {
      // complicated switch statement to produce a compact properly sized immutable set omitted.
      // go to https://projectlombok.org/features/Singular-snippet.html to see it.
      Set<String> occupations = ...;
      return new BuilderExample(name, age, occupations);
    }

    @java.lang.Override
      public String toString() {
      return "BuilderExample.BuilderExampleBuilder(name = " + this.name + ", age = " + this.age + ", occupations = " + this.occupations + ")";
    }
  }
}
```

通过为集合添加 @Singular 注解，可以增加对集合的 add 方法以及 clear 方法，这个注解和 @Builder 一起使用，为 Builder 生成字段是集合类型的 add 方法，字段名不能是单数形式，否则需要指定 value 值。

不明白？再来看一个例子：

``` java
@Builder
public class Example {
  @Singular
  @Setter
  private List<Integer> foos;
}
```

翻译后就是这样的：

``` java
public class Example {
  private List<Integer> foos;

  Example(List<Integer> foos) {
    this.foos = foos;
  }

  public static Example.ExampleBuilder builder() {
    return new Example.ExampleBuilder();
  }

  public void setFoos(List<Integer> foos) {
    this.foos = foos;
  }

  public static class ExampleBuilder {
    private ArrayList<Integer> foos;

    ExampleBuilder() {}

    // 这方法是@Singular作用生成的
    public Example.ExampleBuilder foo(Integer foo) {
      if (this.foos == null) {
        this.foos = new ArrayList();
      }
      this.foos.add(foo);
      return this;
    }

    public Example.ExampleBuilder foos(Collection<? extends Integer> foos) {
      if (this.foos == null) {
        this.foos = new ArrayList();
      }
      this.foos.addAll(foos);
      return this;
    }

    public Example.ExampleBuilder clearFoos() {
      if (this.foos != null) {
        this.foos.clear();
      }
      return this;
    }

    public Example build() {
      List foos;
      switch(this.foos == null ? 0 : this.foos.size()) {
        case 0:
          foos = Collections.emptyList();
          break;
        case 1:
          foos = Collections.singletonList(this.foos.get(0));
          break;
        default:
          foos = Collections.unmodifiableList(new ArrayList(this.foos));
      }
      return new Example(foos);
    }

    public String toString() {
      return "Example.ExampleBuilder(foos=" + this.foos + ")";
    }
  }
}
```

这样稍微能看懂点了吧。

## @SneakyThrows

to RuntimeException 小助手

```java
public class SneakyThrowsExample implements Runnable {
  @SneakyThrows(UnsupportedEncodingException.class)
  public String utf8ToString(byte[] bytes) {
    return new String(bytes, "UTF-8");
  }
  
  @SneakyThrows
  public void run() {
    throw new Throwable();
  }
}
```

翻译后

```java
public class SneakyThrowsExample implements Runnable {
  public String utf8ToString(byte[] bytes) {
    try {
      return new String(bytes, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw Lombok.sneakyThrow(e);
    }
  }

  public void run() {
    try {
      throw new Throwable();
    } catch (Throwable t) {
      throw Lombok.sneakyThrow(t);
    }
  }
}
```

很好的隐藏了异常，有时候的确会有这样的烦恼，从某种程度上也是遵循的了 let is crash

## 参考

http://blog.didispace.com/java-lombok-1/

http://sfau.lt/b5bfLtO