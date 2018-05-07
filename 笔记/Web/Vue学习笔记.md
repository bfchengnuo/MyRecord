## 介绍

vueJS 是一种轻量级的 MVVM 框架，它同时吸收了 react 和 angular 的优点，强调了 react 组件化的概念，可以轻松的实现数据和展现的分离。也吸收了 angular 灵活的指令和页面操作的一些方法。

Vue 引入建议放在头部，避免发生抖屏的现象。

Vue 使用后不再需要任何 Dom 操作，Vue 接管了 Dom 的操作。

**Vue 只会处理挂载点下的内容**

当数据发生变化时（比如被函数改变），Vue 会自动去更新页面的数据，整个过程不需要操作 Dom

> 关于 MVC、MVP、 MVVM 的介绍：http://www.ruanyifeng.com/blog/2015/02/mvcmvp_mvvm.html

## 挂载点、模板、实例之间的关系

先来看一段示例代码：

``` html
<!DOCTYPE html>
<html lang="zh-Hans">
  <head>
    <meta charset="utf-8">
    <title>Vue入门</title>
    <!-- 放在头部引用，避免抖屏现象 -->
    <script src="vue.js"></script>
  </head>
  <body>
    <div id="root">
      <h1>{{msg}}</h1>
      <p v-text="num" v-on:click="handleClick"></p>
    </div>

    <script>
      new Vue({
        el: "#root",
        // template: "<h1>{{msg}}</h1>",
        data: {
          msg: "Hello World!",
          num: 123
        },
        methods: {
          handleClick: function () {
            // alert("test")
            this.msg = "Loli"
          }
        }
      })
    </script>
  </body>
</html>
```

通过 `new Vue()` 出的对象自然就称作是实例了。

其中，那个 div 就可以称作是挂载点；也可以说是 Vue 实例中 el 所指向的元素。

在挂载点内部的内容，都可以称作是模板内容；同时模板也可以写在 Vue 实例中，效果是一样的。

## 数据&事件&方法

通过两对花括号的方式取值方式我们称之为“插值表达式”.

在实例定义的方法中，可以直接通过 `this.name` 的方式来获取定义的数据对象（data 属性里的），相当于是个别名了，不需要太在意 this 指向，Vue 会进行进一步的处理的。

指令：
- v-text

    标签的内容就是 v-text 指向的变量，例如：`<p v-text="num"></p>`

    **特殊字符会被转义**
- v-html

    和 v-text 基本一致，区别在于它不会转义那些 HTML 字符
- v-on:(@)

    绑定事件函数，比如点击事件（v-on:click），函数的定义可以写在实例的 methods 对象内。

    其中 `v-on:` 可以简写为 @
- v-bind:(:)

    属性绑定，和双大括号类似，只不过是用在属性里的，例如：`<div v-bind:title="is + title">Test</div>`

    同样，它可以简写为 `:`

使用了指令后，比如 `v-xxx:` 之类的形式，后面跟的是一个 js 表达式，也就是说可以使用 js 中的基本表达方式，比如 + 之类的连接符。

### 双向绑定

上面一顿操作都是单向绑定的，也就是说实例的数据决定页面的显示，但是页面的显示不能改变实例中的数据，比如：
``` html
<body>
  <div id="root">
    <input type="text" v-model="content">
    <div>{{content}}</div>
  </div>

  <script>
    new Vue({
      el: "#root",
      data: {
        content: "is Content"
      }
    })
  </script>
</body>
```
这里就需要使用模板指令：v-mode，使用格式直接是：`v-model="msg"`

## 计算属性

计算属性就是某一个属性的结果是其他几个属性值计算得出来的，并且在其他属性没有改变的情况下，再次使用会直接返回缓存值，避免重复计算。

侦听器就是监听某一个数据的变化，一旦发生变化就执行相应的逻辑。

``` html
<body>
  <div id="cal">
    <input type="text" v-model="firstName">
    <input type="text" v-model="lastName">
    <div>{{fullName}}</div>
    <div>{{count}}</div>
  </div>

  <script>
    new Vue({
      el: "#cal",
      data: {
        firstName: "",
        lastName: "",
        count: 0
      },
      computed: {
        // 计算属性声明
        fullName: function () {
          // 再次使用 fullName 时，如果这两个变量没有改变会使用缓存值
          // 避免重复进行计算
          return this.firstName + " " + this.lastName
        }
      },
      watch: {
        // 侦听器
        firstName: function () {
          this.count++
        },
        lastName: function () {
          this.count++
        }
      }
    })
  </script>
</body>
```

PS：不要忘记使用 this，要不然找不到，Vue 会自动处理这个“别名”。

## 其他指令

来看 v-if 和 v-show

他们**控制标签的显示和隐藏**，当为 true 时就显示，false 时就隐藏，他们的区别在于，v-if 的表现形式是将标签直接删除，v-show 则是通过 display 来实现。

**性能上来说，频繁更改的话 v-show 更好，如果只是改一次那么 v-if 可能就更好了。**

---

然后就是 v-for 用来遍历数据的，举个例子：

`<li v-for="(item,index) of list" :key="index">{{item}}</li>`

list 就是定义的数组数据，item 是每次遍历的值，index 是索引，使用 `:key` 可以提高效率，**但是要保证 key 的唯一**；所以这里我加了个 index，如果 item 是唯一的那么可以直接使用 item：

`<li v-for="item of list" :key="item">{{item}}</li>`

如果还要对其进行排序之类的操作，那么使用 index 也不是很合适了。

``` html
<body>
  <div id="root">
    <button @click="myClick">Test</button>
    <div v-if="flag">{{text}}</div>

    <ul>
      <li v-for="(item,index) of list" :key="index">{{item}}</li>
      <!-- <li v-for="item of list" :key="item">{{item}}</li> -->
    </ul>
  </div>

  <script>
    new Vue({
      el: "#root",
      data: {
        text: "我是内容",
        flag: true,
        list: [1,2,3]
      },
      methods: {
        myClick: function () {
          this.flag = !this.flag
        }
      }
    })
  </script>
</body>
```

## 模板

当某一块的布局复杂后就需要抽取出来，形成了一个模板，模板又分为全局的和局部的，它们的使用也各不相同，在下面的代码中可以体现出来。

因为模板中取不到外部的 item 属性，所以使用了属性传值的方式来将数据传进去，在模板中接收一下就可以使用了。

**模板也可以看作是一个实例，可以说在一个 Vue 项目中，是由很多很多 Vue 实例组成的**。

根据“发布-订阅”模型，可以在子组件中向父组件发布消息，然后父组件可以监听子组件的自定义消息，然后调用相应的方法来进行处理：
``` html
<body>
  <div id="root">
    <input type="text" v-model="content">
    <button @click="mSubmit">提交</button>

    <ul>
      <li v-for="(item, index) of list" :key="index">
        {{item}}
      </li>
    </ul>
    <hr>
    
    <ul>
      <!-- 使用组件 -->
      <!-- 根据发布-订阅，监听内部组件的自定义事件 -->
      <todo-item v-for="(item, index) of list" :key="index" 
                 :content="item"
                 :index="index"
                 @delete="handleDelete"
                 >
      </todo-item>
    </ul>
  </div>

  <script>
    // 定义全局组件
    Vue.component('todo-item', {
      // 接收传入的属性
      props: ['content', 'index'],
      template: '<li @click="handleClick">{{content}}</li>',
      // 因为模板也是一个实例可以定义事件
      methods: {
        handleClick: function () {
          // 向外发布一个自定义事件（订阅-发布模型）, 并且将 index 传递过去
          this.$emit('delete', this.index)
        }
      }
    })

    // 定义局部组件,必须要在 Vue 实例中声明才能用
    var TodoItem = {
      template: '<li>item</li>'
    }

    new Vue({
      el: "#root",
      // components: {
      //   // 声明局部组件，如果键和值相同可以写一个
      //   'todo-item': TodoItem
      // },
      data: {
        content: '',
        list: []
      },
      methods: {
        mSubmit: function () {
          this.list.push(this.content)
          this.content = ''
        },
        handleDelete: function (index) {
          this.list.splice(index, 1)
        }
      }
    })
  </script>
</body>
```

PS：**模板中要求只能有一个根标签**

## 使用脚手架

官方提供的安装和初始化：
```shell
# 全局安装 vue-cli
npm install --global vue-cli

# 创建一个基于 webpack 模板的新项目
vue init webpack my-project

# 安装依赖，走你
cd my-project
npm run dev
```

因为是采用的 webpack 的方式，所以方便了很多，直接可以在浏览器中进行预览。

这里就可以看出模块化了，其中 Vue 实例都集中放在了 `.vue` 结尾的文件中，vue  文件分成三部分，模板、js代码、样式。

工程的入口是根目录下的 index.html 文件，其中有个 id 为 app 的 div，这就是 Vue 的挂载点了，然后看主要的代码在 src 中。

在 main.js 中创建了 Vue 实例，也就是所谓的启动配置，并且定义了模板，导入了相应的子模板，这里就来修改下看看：

``` javascript
// ES6 模块化语法
import Vue from 'vue'
import App from './App'

Vue.config.productionTip = false

new Vue({
  el: '#app',
  // 引入子模板
  components: { App },
  template: '<App/>'
})
```

和上面写的差不多，创建了一个 Vue 实例，挂载到 app 这个 id 下面，模板采用的是实例内定义的，里面就是引用了个 `<App/>` ，也就是 `App.vue` 中定义的那一堆。

``` html
<template>
  <div>
    <input type="text" v-model="content">
    <button @click="mSubmit">提交</button>

    <ul>
      <!-- 根据发布-订阅，监听内部组件的自定义事件 -->
      <todo-list v-for="(item, index) of list" 
                 :key="index" 
                 :content="item"
                 :index="index"
                 @delete="handleDelete"
                 ></todo-list>
    </ul>
  </div>
</template>

<script>
  import TodoItem from './components/TodoItem'

  export default {
    // 相当于子模板的定义区
    name: 'App',
    components: {
      // 声明局部组件，如果键和值相同可以写一个
      'todo-list': TodoItem
    },
    // vue 文件中 data 只能使用函数来定义
    // ES6 简便写法
    data () {
      return {
        content: '',
        list: []
      }
    },
    methods: {
      mSubmit () {
        this.list.push(this.content)
        this.content = ''
      },
      handleDelete (index) {
        this.list.splice(index, 1)
      }
    }
  }
</script>

<style>
</style>
```

在 vue-cli 中，当写一个实例的数据的时候使用的是函数方式，而不再是对象！！

然后，App 这个模块中又引入了一个 TodoItem 的模板，就是曾经的子模板，完整的例子到 GitHub 仓库查看。

再次说明，Vue 底层会处理 this 的指向，不需要太过担心，指的就是此实例