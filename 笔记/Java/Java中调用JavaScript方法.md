# Java中调用JavaScript方法

我们都知道脚本语言非常灵活，在处理某些问题的时候 Java 实现用十几行来写，用 js 可能不到十行就写完，并且非常简洁，那么有没有一种优雅的方式将 Java 与脚本语言结合呢，在 Java SE6（代号 Mustang）中，这将成为现实。

Nashorn，一个新的 JavaScript 引擎随着 Java 8 一起公诸于世，它允许在 JVM 上开发运行某些 JavaScript 应用。Nashorn 就是 `javax.script.ScriptEngine` 的另一种实现，并且它们俩遵循相同的规则，允许 Java 与 JavaScript 相互调用。

## Mustang 的脚本引擎

**JSR 233** 为 Java 设计了一套脚本语言 API。这一套 API 提供了在 Java 程序中调用**各种脚本语言引擎的接口**。

任何实现了这一接口的脚本语言引擎都可以在 Java 程序中被调用。在 Mustang 的发行版本中包括了一个基于 Mozilla Rhino 的 JavaScript 脚本引擎，也就是说在 JavaSE6+ 的版本默认可以直接调用执行 JavaScript。

### Mozilla Rhino

Rhino 是一个纯 Java 的开源的 JavaScript 实现。他的名字来源于 O'Reilly 关于 JavaScript 的书的封面，是的，就是那本犀牛书.....

Rhino 项目可以追朔到 1997 年，当时 Netscape 计划开发一个纯 Java 实现的 Navigator，为此需要一个 Java 实现的 JavaScript —— Javagator。

它也就是 Rhino 的前身。起初 Rhino 将 JavaScript 编译成 Java 的二进制代码执行，这样它会有最好的性能。后来由于编译执行的方式存在垃圾收集的问题并且编译和装载过程的开销过大，不能满足一些项目的需求，Rhino 提供了解释执行的方式。

随着 Rhino 开放源代码，越来越多的用户在自己的产品中使用了 Rhino，同时也有越来越多的开发者参与了 Rhino 的开发并做出了很大的贡献。如今 Rhino 将被包含在 Java SE 中发行，更多的 Java 开发者将从中获益。

Rhino 提供了如下功能

- 对 JavaScript 1.5+ 的完全支持
- 直接在 Java 中使用 JavaScript 的功能
- 一个 JavaScript shell 用于运行 JavaScript 脚本
- 一个 JavaScript 的编译器，用于将 JavaScript 编译成 Java 二进制文件

### 支持的脚本语言

在 [dev.java.net](https://scripting.dev.java.net/) 可以找到官方的脚本引擎的实现项目。这一项目基于[BSD License](http://www.opensource.org/licenses/bsd-license.html) ，表示这些脚本引擎的使用将十分自由。

目前该项目已对包括 Groovy, JavaScript, Python, Ruby, PHP 在内的二十多种脚本语言提供了支持。这一支持列表还将不断扩大。

### 在Java中的基本使用

在 Mustang 中对脚本引擎的检索使用了工厂模式。首先需要实例化一个工厂 ： ScriptEngineManager

`ScriptEngineManager factory = new ScriptEngineManager();`

ScriptEngineManager 将在 Thread Context ClassLoader 的 Classpath 中根据 jar 文件的 META-INF 来查找可用的脚本引擎。它提供了 3 种方法来检索脚本引擎：

``` java
// create engine by name
ScriptEngine engine = factory.getEngineByName ("JavaScript");
// create engine by name
ScriptEngine engine = factory.getEngineByExtension ("js");
// create engine by name
ScriptEngine engine = factory.getEngineByMimeType ("application/javascript");
```

下面的代码将会打印出当前的 JDK 所支持的所有脚本引擎:

``` java
ScriptEngineManager factory = new ScriptEngineManager();
for (ScriptEngineFactory available : factory.getEngineFactories()) {
  System.out.println(available.getEngineName());
  // 打印脚本具体名称信息
  System.out.println(available.getNames());
}
```

即可看到 `[nashorn, Nashorn, js, JS, JavaScript, javascript, ECMAScript, ecmascript]` 等输出

## 执行Js脚本

啥也别说了，都知道要干嘛，来打印句 HelloWorld 再说：

``` java
public class RunJavaScript {
  public static void main(String[] args){
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName ("JavaScript");
    engine.eval("print('Hello World')");
  }
}
```

如果你的 Js 有语法错误，就会抛出 `javax.script.ScriptException` 异常

如果我们要解释一些更复杂的脚本语言，或者想在运行时改变该脚本该如何做呢？脚本引擎支持一个重载的 eval 方法，它可以从一个 Reader 读入所需的脚本，或者得到 Js 文件的绝对路径，直接用自带的 load 函数读取：

``` java
ScriptEngineManager factory = new ScriptEngineManager();
ScriptEngine engine = factory.getEngineByName ("JavaScript");
engine.eval(new Reader("HelloWorld.js"));


File file = new File(Main.class.getClassLoader().getResource("test.js").getFile());
engine.eval(new FileReader(file));

String scriptPath = Main.class.getClassLoader().getResource("test.js").getPath();
engine.eval("load('" + scriptPath + "')");
```

这里注意下 FileReader 中相对路径到底是相对谁的（JVM 启动的位置），可使用 ClassLoader 读取 src 下的文件

Java 程序将动态的去读取脚本文件并解释执行，这意味着，在程序运行中你可以随意修改 Js 文件里的代码，会得到实时修改结果。

对于这一简单的 Hello World 脚本来说，IO 操作将比直接执行脚本损失 20% 左右的性能（SE6 时，个人测试），但他带来的灵活性 --- 在运行时动态改变代码的能力，在某些场合是十分激动人心的。

最后来看看完整的测试代码：

``` java
package com.bfchengnuo.javascript;

import javax.script.*;
import java.io.FileNotFoundException;

/**
 * Created by 冰封承諾Andy on 2017/12/30.
 */
public class Main {
  public static void main(String[] args) throws ScriptException, NoSuchMethodException, FileNotFoundException {
    ScriptEngineManager manager = new ScriptEngineManager();

    System.out.println("当前 JDK 支持的脚本语言引擎：");
    for (ScriptEngineFactory available : manager.getEngineFactories()) {
      System.out.println(available.getEngineName());
      System.out.println(available.getNames());
    }

    ScriptEngine engine = manager.getEngineByName("JavaScript");
    if (!(engine instanceof Invocable)) {
      System.out.println("Invoking methods is not supported.");
      return;
    }
    engine.eval("print('Hello World')");

    Invocable inv = (Invocable) engine;

    // File file = new File(Main.class.getClassLoader().getResource("test.js").getFile());
    // engine.eval(new FileReader(file));
    String scriptPath = Main.class.getClassLoader().getResource("test.js").getPath();
    engine.eval("load('" + scriptPath + "')");

    // 获取对象
    Object calculator = engine.get("calculator");

    int x = 3;
    int y = 4;
    Object addResult = inv.invokeMethod(calculator, "add", x, y);
    Object subResult = inv.invokeMethod(calculator, "subtract", x, y);
    Object mulResult = inv.invokeMethod(calculator, "multiply", x, y);
    Object divResult = inv.invokeMethod(calculator, "divide", x, y);

    System.out.println(addResult);
    System.out.println(subResult);
    System.out.println(mulResult);
    System.out.println(divResult);
  }
}
```

对应的 Js 文件：

``` javascript
var calculator = {};

calculator.add = function (n1, n2) { return n1 + n2};
calculator.subtract = function (n1, n2) {return n1 - n2};
calculator.multiply = function (n1, n2) {return n1 * n2};
calculator.divide = function (n1, n2) {return n1 / n2};
```

## 脚本语言与 Java 的通信

ScriptEngine 的 put 方法用于**将一个 Java 对象映射成一个脚本语言的变量**。现在有一个 Java Class，它只有一个方法:

``` java
public class HelloWorld {
  String s = "Hello World";
  public void sayHello(){
    System.out.println(s);
  }
}
```

接下来就是让 Js 使用这个类！还是看代码最实在：

``` java
public class TestPut {
  public static void main(String[] args) throws ScriptException {
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("JavaScript");
    HelloWorld hello = new HelloWorld();
    engine.put("script_hello", hello);
    engine.eval("script_hello.sayHello()");
  }
}
```

首先我们实例化一个 HelloWorld，然后用 put 方法将这个实例映射为脚本语言的变量 script_hello。那么我们就可以在 eval() 函数中像 Java 程序中同样的方式来调用这个实例的方法。

使用 invokeFunction 来执行 Js 函数：

``` java
public class TestInv {
  public static void main(String[] args) throws Exception {
    ScriptEngineManager factory = new ScriptEngineManager();
    ScriptEngine engine = factory.getEngineByName("JavaScript");
    String script = "function say(first,second) { print(first +' '+ second); }";
    engine.eval(script);
    Invocable inv = (Invocable) engine;
    inv.invokeFunction("say", "Hello", "Tony");
  }
}
```

这里使用了 ScriptEngine 的两个可选接口之一 ： Invocable，Invocable 表示当前的 engine 可以作为函数被调用。

这里我们将 engine 强制转换为 Invocable 类型，使用 invokeFunction 方法将参数传递给脚本引擎。invokeFunction 这个方法使用了可变参数的定义方式，可以一次传递多个参数，并且将脚本语言的返回值作为它的返回值。

---

Invocable 接口还有一个方法用于从一个 engine 中得到一个 Java Interface 的实例，它接受一个 Java 的 Interface 类型作为参数，返回这个 Interface 的一个实例。

也就是说你可以完全用脚本语言来写一个 Java Interface 的所有**实现**！然后直接调用使用返回的实现类就可以了！

``` java
engine.eval(script);
Invocable inv = (Invocable) engine;
MaxMin maxMin = inv.getInterface(MaxMin.class);
```

是不是感觉很爽？

## 其他

其他的还可以对 Js 进行编译、在 Js 中调用 Java 的代码，但感觉用的不多，最常用的还是 Java 调用 Js 的方法

## 参考

https://www.ibm.com/developerworks/cn/java/j-lo-mustang-script/index.html

https://www.w3cschool.cn/java/scripting-in-java-call-javascript-function.html