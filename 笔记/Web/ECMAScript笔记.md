# ECMAScript笔记

集百家之介绍：

> ECMAScript 是一种由 Ecma 国际（前身为欧洲计算机制造商协会）通过 ECMA-262 标准化的脚本程序设计语言。这种语言在万维网上应用广泛，它往往被称为 JavaScript 或 JScript，但实际上后两者是 ECMA-262 标准的实现和扩展。
>
> ECMAScript 6.0（以下简称 ES6）是 JavaScript 语言的下一代标准，已经在 2015 年 6 月正式发布了。它的目标，是使得 JavaScript 语言可以用来编写复杂的大型应用程序，成为企业级开发语言。

目前 ES 已经到达了 ES2018 版本，Google 的 V8 引擎支持率 100%，其他的并不友好，而我们常用 **JavaScript** 稳定版本的实现目前在 ES2016 版本，所以这里主要学习 ES6 的特性了。

如果真的有什么原因不能使用 ES6 可以使用 Babel 将 ES6 语法转为 ES5.

## let和const

使用 `let` 声明的变量只在它所在的代码块内有效：

``` javascript
{
  let a = 10;
  var b = 1;
}

a // ReferenceError: a is not defined.
b // 1

var a = [];
for (let i = 0; i < 10; i++) {
  a[i] = function () {
    console.log(i);
  };
}
a[6](); // 6，使用 var 则都是 10
```

例如 for 循环就合适使用 let 定义 i

> `for` 循环还有一个特别之处，就是设置循环变量的那部分是一个父作用域，而循环体内部是一个单独的子作用域。

`var` 命令会发生“变量提升”现象，即变量可以在声明之前使用，值为 `undefined`。这种现象多多少少是有些奇怪的，按照一般的逻辑，变量应该在声明语句之后才可以使用。

为了纠正这种现象，`let` 命令改变了语法行为，它所声明的变量一定要在声明后使用，否则报错。

> ES6 明确规定，如果区块中存在 `let` 和 `const` 命令，这个区块对这些命令声明的变量，从一开始就形成了封闭作用域。凡是在声明之前就使用这些变量，就会报错。
>
> ES6 规定，块级作用域之中，函数声明语句的行为类似于 `let`，在块级作用域之外不可引用。

总之，在代码块内，使用 `let` 命令声明变量之前，该变量都是不可用的。这在语法上，称为“暂时性死区”（temporal dead zone，简称 TDZ）

---

`const` 声明一个只读的常量。一旦声明，常量的值就不能改变。

这意味着，`const` 一旦声明变量，就必须立即初始化，不能留到以后赋值。

`const`的作用域与`let`命令相同：只在声明所在的块级作用域内有效，不提升、存在暂时性死区。

> `const`实际上保证的，并不是变量的值不得改动，而是变量指向的那个内存地址所保存的数据不得改动。
>
> 对于简单类型的数据（数值、字符串、布尔值），值就保存在变量指向的那个内存地址，因此等同于常量。
>
> 但对于复合类型的数据（主要是对象和数组），变量指向的内存地址，保存的只是一个指向实际数据的指针，`const`只能保证这个指针是固定的（即总是指向另一个固定的地址），至于它指向的数据结构是不是可变的，就完全不能控制了。

## 字符串

### 模板字符串

模板字符串（template string）是增强版的字符串，用反引号（`）标识。它可以当作普通字符串使用，也可以用来定义**多行字符串**，或者在字符串中**嵌入变量**。

``` javascript
// 普通字符串
`In JavaScript '\n' is a line-feed.`

// 多行字符串
`In JavaScript this is
 not legal.`

// 字符串中嵌入变量
let name = "Bob", time = "today";
`Hello ${name}, how are you ${time}?`

// 模板字符串之中还能调用函数
function fn() {
  return "Hello World";
}
`foo ${fn()} bar`
// foo Hello World bar
```

如果模板字符串中的变量没有声明，将报错。

如果大括号中的值不是字符串，将按照一般的规则（toString）转为字符串。

### 新增方法

ES5 字符串的实例方法很有限，基本就是 indexOf 了，在 ES6 新加入了一些：

- **includes()**：返回布尔值，表示是否找到了参数字符串。
- **startsWith()**：返回布尔值，表示参数字符串是否在原字符串的头部。
- **endsWith()**：返回布尔值，表示参数字符串是否在原字符串的尾部。
- repeat()：返回一个新字符串，表示将原字符串重复 n 次。

在 ES2017 和 ES2019 又引入了 `padStart()` 用于头部补全，`padEnd()` 用于尾部补全和 `trimStart()` 和 `trimEnd()` 这两个方法。

## 函数

ES6 允许为函数的参数设置默认值，即直接写在参数定义的后面。

``` javascript
function log(x, y = 'World') {
  console.log(x, y);
}

log('Hello') // Hello World
log('Hello', 'China') // Hello China
log('Hello', '') // Hello


// 与解构赋值默认值结合使用
function foo({x, y = 5}) {
  console.log(x, y);
}
foo({}) // undefined 5
foo({x: 1}) // 1 5
foo({x: 1, y: 2}) // 1 2
foo() // TypeError: Cannot read property 'x' of undefined


// 如果没有提供参数，函数 foo 的参数默认为一个空对象
function foo({x, y = 5} = {}) {
  console.log(x, y);
}
foo() // undefined 5
```

ES6 引入 rest 参数（形式为 `...变量名`），用于获取函数的多余参数，本质是个数组，跟 Java 很类似：

``` javascript
function add(...values) {
  let sum = 0;

  for (var val of values) {
    sum += val;
  }

  return sum;
}

add(2, 5, 3) // 10
```

其次还有函数的 `name` 属性，返回该函数的函数名。

### 箭头函数

ES6 允许使用“箭头”（`=>`）定义函数。

``` javascript
var f = v => v;
// 等同于
var f = function (v) {
  return v;
};

var f = () => 5;
// 等同于
var f = function () { return 5 };

var sum = (num1, num2) => num1 + num2;
// 等同于
var sum = function(num1, num2) {
  return num1 + num2;
};

// 正常函数写法
[1,2,3].map(function (x) {
  return x * x;
});

// 箭头函数写法
[1,2,3].map(x => x * x);
```

怎么说呢，这个其实就是简化的匿名函数，用在回调的地方非常好用。

箭头函数有几个使用注意点。

1. 函数体内的`this`对象，就是定义时所在的对象，而不是使用时所在的对象。

2. 不可以当作构造函数，也就是说，不可以使用`new`命令，否则会抛出一个错误。

3. 不可以使用`arguments`对象，该对象在函数体内不存在。如果要用，可以用 rest 参数代替。

4. 不可以使用`yield`命令，因此箭头函数不能用作 Generator 函数。

其中第一点尤其值得注意。`this` 对象的指向是可变的，**但是在箭头函数中，它是固定的**。

``` javascript
function foo() {
  setTimeout(() => {
    console.log('id:', this.id);
  }, 100);
}

var id = 21;

foo.call({ id: 42 });
// id: 42
```

关于 this 的这个问题，版本对比为：

``` javascript
// ES6 版本
function foo() {
  setTimeout(() => {
    console.log('id:', this.id);
  }, 100);
}

// ES5 版本
function foo() {
  var _this = this;

  setTimeout(function () {
    console.log('id:', _this.id);
  }, 100);
}

// 不适用情况
// 对象不构成单独的作用域，导致 jumps 箭头函数定义时的作用域就是全局作用域。
const cat = {
  lives: 9,
  jumps: () => {
    this.lives--;
  }
}
```

实际原因是箭头函数根本没有自己的`this`，导致内部的`this`就是外层代码块的`this`。正是因为它没有`this`，所以也就不能用作构造函数。

### 其他

使用 `JSON.stringify()` 方法可以将对象转为字符串类型的 json 格式。

## 数组的扩展

扩展运算符（spread）是三个点（`...`）。它好比 rest 参数的逆运算，将一个数组转为用逗号分隔的参数序列。

``` javascript
console.log(...[1, 2, 3])
// 1 2 3

console.log(1, ...[2, 3, 4], 5)
// 1 2 3 4 5

[...document.querySelectorAll('div')]
// [<div>, <div>, <div>]
```

对于数组的克隆与合并，有了扩展运算符也变得简单多了：

``` javascript
const a1 = [1, 2];
// 写法一
const a2 = [...a1];
// 写法二
const [...a2] = a1;

// ES6 的合并数组
[...arr1, ...arr2, ...arr3]
// [ 'a', 'b', 'c', 'd', 'e' ]
```

ES5 中只能使用 concat 函数间接达到目的。

字符串也可以被展开：`[...'hello']`

还可以用于 Generator 函数：

``` javascript
const go = function*(){
  yield 1;
  yield 2;
  yield 3;
};

[...go()] // [1, 2, 3]
```

## 对象扩展

现在对象的属性有了更简洁的写法：

``` javascript
const baz = {foo};
// 等同于
const baz = {foo: foo};

function f(x, y) {
  return {x, y};
}
// 等同于
function f(x, y) {
  return {x: x, y: y};
}
```

简单说就是当 key 和 val 一样时，可以进行简写。

其实，方法也可以进行简写：

``` javascript
const o = {
  method() {
    return "Hello!";
  }
};

// 等同于
const o = {
  method: function() {
    return "Hello!";
  }
};
```

这种写法会非常的简洁，另外常用的还有 setter 和 getter，就是采用的这种方案：

``` javascript
const cart = {
  _wheels: 4,

  get wheels () {
    return this._wheels;
  },

  set wheels (value) {
    if (value < this._wheels) {
      throw new Error('数值太小了！');
    }
    this._wheels = value;
  }
}
```

需要注意的一点就是简洁写法的属性名总是字符串。

在对象定义上，也变得更加灵活了：

``` javascript
let propKey = 'foo';

let obj = {
  [propKey]: true,
  ['a' + 'bc']: 123
};
```

ES6 又新增了另一个类似的关键字`super`，指向当前对象的原型对象。

---

另外，对象也有扩展运算符，例如：

``` javascript
let z = { a: 3, b: 4 };
let n = { ...z };
n // { a: 3, b: 4 }

var ll = {name:'loli', age: 12, getVal(val){console.log(val)}}
var test = {...User, dd:'dd'}
test.getVal(test.dd)
```

简单说就是把对象里的方法进行拷贝，Vuex 中的这种写法算是明白了。

## 遍历

变量数组或者对象，可以使用 forEach 这个函数（ES5 中也可使用）：

``` javascript
[1, 2 ,3, 4].forEach(alert);

[1, 2 ,3, 4].forEach((item, index) => {console.log(item)})

arr.forEach(function callback(currentValue, index, array) {
    //your iterator
}[, thisArg]);
```

> 使用 forEach 函数进行遍历时，中途无法跳过或者退出；
>
> 在 forEach 中的 return、break、continue 是无效的。
>
> see：https://www.jianshu.com/p/bdf77ee23089

然后遍历除了基本的 fori，还有两种：for...in 和 for...of ，那么他们俩有啥区别呢？

1. 推荐在循环对象属性的时候，使用 `for...in`，在遍历数组的时候的时候使用 `for...of`。
2. `for...in` 循环出的是 key，`for...of` 循环出的是 value
3. 注意，`for...of` 是 ES6 新引入的特性。修复了 ES5 引入的 `for...in` 的不足
4. `for...of` **不能循环普通的对象**，需要通过和 `Object.keys()` 搭配使用

下面是一段示例代码：

``` javascript
let aArray = ['a',123,{a:'1',b:'2'}]

for(let index in aArray){
    console.log(`${aArray[index]}`);
}
// 结果：
// a
// 123
// [object Object]


for(let value of aArray){
    console.log(value);
}
// 结果：
// a
// 123
// {a: "1", b: "2"}


// 可以使用 for...of 遍历 Map，它部署了 Iterator 接口
const map = new Map();
map.set('first', 'hello');
map.set('second', 'world');

for (let [key, value] of map) {
  console.log(key + " is " + value);
}
// first is hello
// second is world
```

作用于数组的 `for-in` 循环除了遍历数组元素以外，还会遍历自定义属性。

`for...of` 循环不会循环对象的 key，只会循环出数组的 value，因此 `for...of` 不能循环遍历**普通对象**，对普通对象的属性遍历推荐使用 `for...in`

## forEach与map

MDN 上的描述：

> `forEach()`：针对每一个元素执行提供的函数 (executes a provided function once for each array element)。
>
> `map()`：**创建一个新的数组**，其中每一个元素由调用数组中的每一个元素执行提供的函数得来 (creates a new array with the results of calling a provided function on every element in the calling array)。

`forEach` 方法不会返回执行结果，而是 `undefined`。也就是说，`forEach()` 会修改原来的数组。而 `map()` 方法会得到一个新的数组并返回。

``` javascript
// 将数组中的数据翻倍
let arr = [1, 2, 3, 4, 5];

arr.forEach((num, index) => {
  return (arr[index] = num * 2);
});

let doubled = arr.map(num => {
  return num * 2;
});

// 结果都为： [2, 4, 6, 8, 10]
```

如果你习惯使用函数是编程，那么肯定喜欢使用 `map()`。因为 `forEach()` 会改变原始的数组的值，而 `map()` 会返回一个全新的数组，原本的数组不受到影响。

总之，能用`forEach()`做到的，`map()`同样可以。反过来也是如此。

一般来说，使用 map 速度会更快，测试地址：https://jsperf.com/map-vs-foreach-speed-test

## 其他

关于 a 标签默认行为（href 跳转）：

常见的阻止默认行为的方式：`<a href="javascript:void(0);"  onclick= "myjs( )">  Click Me  </a>`

函数 onclick 要优于 href 执行，而 void 是一个操作符，`void(0)` 返回 undefined，地址不发生跳转，使用 `javascript:;` 也是一样的效果。

在 onclick 函数中，如果返回的是 true，则认为该链接发生了点击行为；如果返回为 false，则认为未被点击。