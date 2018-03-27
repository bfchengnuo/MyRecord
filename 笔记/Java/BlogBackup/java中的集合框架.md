---
title: java中的集合框架
date: 2016-05-07 15:23:33
tags: [Java,集合框架]
categories: Java
---

今天看到关于集合排序的，找到了这篇，发现没写，并且原来写的实在看不下去了，这次基本又重写了一遍....
补充了排序相关，也重新整理了下排版

最后更新于：2017-7-19

## 集合类的由来

对象常用于封装特有数据，对象多了就需要存储，如果对象的个数不确定怎么办？于是集合出现，用集合**容器**进行存储。
## 集合的特点

1. 是用于存储**对象**的容器。
2. 集合的长度是可变的。
3. 集合中**不可以**存储基本数据类型值。

集合容器因为内部的数据结构不同，有多种具体容器。如果将它们不断的向上抽取，就形成了集合框架。

### 框架的顶层-Collection接口

Collection:
- List：有序(存入和取出的顺序一致),元素都有索引(角标)，元素可以重复。
- Set：元素不能重复,无序。

Collection的常见方法：

 ``` java
// 增加
boolean add(Object obj):
boolean addAll(Collection coll):
// 删除
boolean remove(object obj):
boolean removeAll(Collection coll);
void clear();
// 判断，是否含有某元素
boolean contains(object obj):
boolean containsAll(Colllection coll);
boolean isEmpty() // 判断集合中是否有元素。

boolean retainAll(Collection coll);  //取交集。
Object[] toArray();  //将集合转成数组。 
 ```
取出元素一般用的方式：迭代器。

该对象必须依赖于具体容器，因为每一个容器的数据结构都不同。**所以该迭代器对象是在容器中进行内部实现的。**

对于使用容器者而言，具体的实现不重要，只要通过容器获取到该实现的迭代器的对象即可，也就是iterator方法。

Iterator接口就是对所有的Collection容器进行元素取出的公共接口。

 ``` java
//利用迭代器
Iterator it = v.iterator();
while(it.hasNext()){
	System.out.println(it.next());
}
//利用枚举，仅部分支持
Enumeration en = v.elements;
while(en.hasMoreElements()){
	System.out.println(en.nextElement());
}
 ```

### List集合

List:
- ~~Vector:内部是数组数据结构，是同步的。增删，查询都很慢！~~

- ArrayList:内部是数组数据结构，是不同步的。替代了Vector。**查询的速度快。**

- LinkedList:内部是链表数据结构，是不同步的。**增删元素的速度很快。**

特有的常见方法：有一个共性特点就是都可以操作角标。list集合是可以完成对元素的增删改查。

 ``` java
// 添加
void add(index,element);
void add(index,collection);
//删除
Object remove(index):
//修改
Object set(index,element);
//获取
Object get(index);
int indexOf(object);
int lastIndexOf(object);
List subList(from,to); //包括from不包括to
 ```

关于LinkedList:
``` java
addFirst();
addLast():
//jdk1.6+支持以下，和上面两个比，有了返回值 boolean
offerFirst();
offetLast();

//获取但不移除，如果链表为空，抛出NoSuchElementException.
getFirst();
getLast();
//jdk1.6+支持以下，获取但不移除，如果链表为空，返回null.
peekFirst();
peekLast():

//获取并移除，如果链表为空，抛出NoSuchElementException.
removeFirst();
removeLast();
//jdk1.6+支持以下，获取并移除，如果链表为空，返回null.
pollFirst();
pollLast();
```

### set集合

元素不可以重复，是无序。Set接口中的方法和Collection一致。

+ HashSet: 内部数据结构是哈希表 ，是不同步的。

  是通过对象的hashCode和equals方法来完成对象唯一性的。

  如果对象的hashCode值不同，那么不用判断equals方法，就直接存储到哈希表中。 

  如果对象的hashCode值相同，那么要再次判断对象的equals方法是否为true。

  如果为true，视为相同元素，不存。如果为false，那么视为不同元素，就进行存储。

  记住：**如果元素要存储到HashSet集合中，必须覆盖hashCode方法和equals方法。**

  一般情况下，如果定义的类会产生很多对象，通常都需要覆盖equals，hashCode方法。来建立对象判断是否相同的依据。 
  `if(this.hashCode()== obj.hashCode() && this.equals(obj))`


+ TreeSet:可以对Set集合中的元素进行排序。是不同步的。

  判断元素唯一性的方式：就是根据比较方法的返回结果是否是0，是0，就是相同元素，不存。 	

  TreeSet对元素进行排序的方式一：

  **让元素自身具备比较功能，就需要实现Comparable接口。覆盖compareTo方法。**

  如果不要按照对象中具备的自然顺序进行排序，或者对象中不具备自然顺序，可以使用TreeSet集合第二种排序方式：

  **让集合自身具备比较功能，定义一个类实现Comparator接口，覆盖compare方法。**

  将该类对象作为参数传递给TreeSet集合的构造函数。

### map集合

**注：Map集合不属于Collection**

Map一次添加一对元素。Collection 一次添加一个元素。

Map也称为双列集合，Collection集合称为单列集合。

其实map集合中存储的就是键值对。 map集合中必须**保证键的唯一性**。

常用方法：

 ``` java
// 添加
// 返回前一个和key关联的值，如果没有返回null.
value put(key,value);
// 删除
void  clear(); //清空map集合。
value remove(key); //根据指定的key删除这个键值对。 
// 判断
boolean containsKey(key):
boolean containsValue(value):
//如果此映射未包含键-值映射关系，则返回 true。
boolean isEmpty(); 
// 获取
//通过键获取值，如果没有该键返回null。当然可以通过返回null，来判断是否包含指定键。
value get(key);
int size();
 ```

 Map常用的子类：

-   Hashtable :内部结构是哈希表，是同步的。不允许null作为键、null作为值。
    -   Properties：用来存储键值对型的配置文件的信息，可以和IO技术相结合。
-   TreeMap : 内部结构是二叉树，不是同步的。可以对Map集合中的键进行排序。
-   HashMap : 内部结构是哈希表，不是同步的。允许null作为键、null作为值。

## 集合的一些技巧

**需要唯一吗？**

- 需要【Set】

    需要制定顺序么？

    -   需要： TreeSet
    -   不需要：HashSet
    -   想要一个和**存储一致的**顺序(有序):LinkedHashSet
- 不需要【List】

    需要频繁增删吗？

    -   需要：LinkedList
    -   不需要：ArrayList

看到array：就要想到数组，就要想到查询快，有角标.

看到link：就要想到链表，就要想到增删快，就要想要 add get remove+frist last的方法 

看到hash:就要想到哈希表，就要想到唯一性，就要想到元素需要覆盖hashcode方法和equals方法。 

看到tree：就要想到二叉树，就要想要排序，就要想到两个接口Comparable，Comparator 。

而且通常这些常用的集合容器都是不同步的。 

## Map的迭代(遍历)

上面说到过 Map集合不属于Collection，只有 Collection 才可以进行迭代，所以在 map 中有个转换的方法 entrySet；使用这个方法可以将 Map 转换为一个具有映射关系的 set 集合，然后就可以迭代了

关于使用高级 for 和迭代器的区别博客后面的 java知识补充有写，主要是在动态删除方面，一般使用二或者四就行，简单

遍历 Map 的几种方法有：

```java
// 方法一：使用迭代器
private static void method1() {
  Iterator<Map.Entry<String, Object>> iterator = mMap.entrySet().iterator();
  while (iterator.hasNext()) {
    Map.Entry<String, Object> entry = iterator.next();
    System.out.println(entry.getKey() + "::" + entry.getValue());
  }
}

// 方法二：使用高级 for  ；jdk1.5+
private static void method2() {
  for (Map.Entry<String, Object> entry : mMap.entrySet()) {
    System.out.println(entry.getKey() + "::" + entry.getValue());
  }
}

// 方法三：使用 keySet 配合迭代器
private static void method3() {
  for (Iterator<String> iterator = mMap.keySet().iterator(); iterator.hasNext(); ) {
    String key = iterator.next();
    System.out.println(key + "::" + mMap.get(key));
  }
}

// 方法四：使用 keySet 配合高级 for
private static void method4() {
  for (String key : mMap.keySet()) {
    System.out.println(key + "::" + mMap.get(key));
  }
}
```

## compare和compareTo

前面说Set的时候也提到过，它们主要是用来实现集合中比较(排序)的，也可用于对象之间的比较排序

compare是让集合具有比较的功能，compareTo是让元素具有比较的功能(自然排序)

-   Collections.sort(List list)：对List的元素进行自然排序
-   Collections.sort(List list, Comparator comparator)：对List中的元素进行客户化排序 

`compareTo(Object o)`方法是`java.lang.Comparable<T>`接口中的方法，也是仅有的一个
`compare(Object o1,Object o2)`方法是`java.util.Comparator<T>`接口的方法，该接口里有两个方法`compare()`和`equals()`

### compare

`int compare(Object o1,Object o2)`从这个方法也可以看出，是让我们自己写这两个对象的比较方式，也就是按照什么属性去比较，然后传入上面所说的第二个方法即可

>   如果第一个参数小于第二个参数，就返回一个负数，如果等于就返回0，如果大于就返回一个正数。

可以这样想，返回正数就是第一个对象在前，负数就是第二个参数在前，也就是正序排列，从小到大，如果反过来写那就是倒序排列(:大雾)

首先搞一个需要排序的类，就用经典的Person吧

```java
class Person {
  private int age;
  private String name;
  public Person(String name, int age) {
    this.age = age;
    this.name = name;
  }
  public int getAge() {
    return age;
  }
  public void setAge(int age) {
    this.age = age;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Person[name=" + name + ", age=" + age + "]";
  }
}

//这是一个比较器，用于比较Person对象
class PersonComparator implements Comparator<Person> {
  @Override
  public int compare(Person o1, Person o2) {
    //两个Person对象的比较过程，当然，这里可以实现更多更复杂的比较过程
    return o1.getAge() - o2.getAge();
  }
}
```

类有了，比较方法也有了，然后就是进行比较了

```java
public static void main(String[] args) {
  List<Person> list = new ArrayList<>();
  list.add(new Person("凝萱",12));
  list.add(new Person("欣妍",10));
  list.add(new Person("诗茵",7));
  list.add(new Person("茹雪",17));
  list.add(new Person("娅楠",22));
  list.add(new Person("沛凝",5));

  System.out.println(list);

  Collections.sort(list, new PersonComparator());

  System.out.println(list);
}
```

### compareTo

和compare类似，它可以理解为：可比较的，也就是要定义在要比较的元素内部，比如Integer类就已经实现了这个方法，定义的`List<Integer>`集合是可以直接使用`Collections.sort(List list)`进行排序的

下面我们就自己搞一个！就还是采用非常经典的Person类了！23333，和上面的比，其实只是实现了一个接口

```java
class Person implements Comparable<Person> {
  private int age;
  private String name;
  public Person(int age, String name) {
    this.age = age;
    this.name = name;
  }
  public int getAge() {
    return age;
  }
  public void setAge(int age) {
    this.age = age;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  //实现Comparable接口的compareTo方法
  @Override
  public int compareTo(Person o) {
    // 如果是和自己比，直接返回0，就是相等
    if (this == o)
      return 0;
    // 实际就是按照年龄进行排序
    return this.age - o.age;
  }

  @Override
  public String toString() {
    return "Person[name=" + name + ", age=" + age + "]";
  }
}
```

然后就是进行排序了，结果就是按照年龄从小到大进行输出：

```java
public static void main(String[] args) {
  List<Person> list = new ArrayList<>();
  list.add(new Person("凝萱",12));
  list.add(new Person("欣妍",10));
  list.add(new Person("诗茵",7));
  list.add(new Person("茹雪",17));
  list.add(new Person("娅楠",22));
  list.add(new Person("沛凝",5));

  System.out.println(list);

  Collections.sort(list);

  System.out.println(list);
}
```

### 补充

对于数组有一个方法是：`Arrays.sort()`来进行排序，如果数组保存的是对象，并且实现了compareTo方法，也是可以用的，虽然并不常用

貌似使用`list.sort()`也是可以进行排序的，并且推荐使用，传入一个Comparable来进行比较（貌似需要jdk1.8+），看了下实现，是先转成数组再通过`Arrays.sort()`方法按照我们传入的Comparable来进行排序

## Collection和Collections

Collection 是所有集合的顶级接口，前面介绍的很详细了，不多说，然后说下 Collections 这是个**类**，是的，它不是接口只有一个 s 之差，并且 Collections 是不能被实例化的，它提供了一些静态方法，用于对集合的“处理”（比如排序），更像是 Collection 的**工具类**，类似的命名在其他地方应该也能看到，大都是这样

## 其它补充

我们知道list大部分是不同步的，但是是可以进行转换的，我们可以给非同步的集合进行加锁，这样就相当于是个同步的了。
下面的写法可能有点问题，但是意思就是这个意思啦~~

``` java
class MyCollections{
  public static List synList(List list){
    return new MyList(list);
  }

  private class MyList implements List{
    private List list;
    private static final Object lock = new Object();

    public MyList(List list){	
      this.list = list;	
    }

    public boolean add(Object obj){
      synchronized(lock)
      {
        return list.add(obj);
      }
    }

    public boolean remove(Object obj){
      synchronized(lock)
      {
        return list.remove(obj);
      }
    }
  }
}
```
然后就是获取同步的List了

```java
List list = new ArrayList();//非同步的。
list = MyCollections.synList(list);//返回一个同步的list.
```