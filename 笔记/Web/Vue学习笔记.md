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

这里补充一个生命周期的图示：

![Vue生命周期](https://cn.vuejs.org/images/lifecycle.png)

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
        // 侦听器，也会有缓存
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

计算属性 computed 可以使用 get 和 set，用来提供获取和设置的情况。

## 其他指令

来看 v-if 和 v-show

他们**控制标签的显示和隐藏**，当为 true 时就显示，false 时就隐藏，他们的区别在于，v-if 的表现形式是将标签直接删除，v-show 则是通过 display 来实现。

这就带来了 dom 复用的问题，例如 input 框不会清空，这种情况下可以使用 key 值来绑定唯一，这样 Vue 就会不复用了。

**性能上来说，频繁更改的话 v-show 更好，如果只是改一次那么 v-if 可能就更好了。**

其次还支持紧贴 v-if 的 v-else-if 和 v-else

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

## 样式控制

关于样式的控制，可以使用 class 对象绑定： `:class="{className: isActivated}"` 然后通过控制 isActivated 变量来控制 class 的显示或隐藏。

 还可以通过 `:class="[activated, className]"` 这样通过 activated 这个变量来控制。

如果是内联样式（`:style`），可以直接引用一个 js 对象，在对象里面定义 css 样式就行，同样也可以使用数组来挂载多个对象。

## 组件参数校验

在父子组件之间传值都已经知道了，那么接下来就看看如果子组件要对父组件传递的数据进行校验要怎么办，使用的还是子组件里的 props 属性，只不过这里由本来的字符串数组变成了对象。

``` javascript
Vue.component('child', {
  // 不是校验的话可以直接用字符串数组来标识
  props: {
    // content: [Number,String]
    content: {
      type: String,
      required: true,
      default: 'def val',
      validator: function (value) {
        return value.length > 5
      }
    }
  },
  template: "<div>{{content}}</div>"
})

var vm = new Vue({
  el: "#root"
})
```

补充一下这个 props 特性，也就是如果你在子组件的 props 中接收了传递的属性，那么 Vue 在视图渲染的时候就不会再在 HTML 中加上这个属性了。

在子组件上绑定的事件默认都是自定义事件，也就是说原生的事件可能会失效，例如在子组件标签里使用 `@click` 是无效的，不过你可以在子组件的模板里来绑定，这样就不是自定义事件了。

触发自定义事件就是手动的调用 emit 了；但是有些时候就想用子组件的原生事件，就想让它生效怎么办，也是有办法的，只需要一点点的改动：`@click.native="fun"`。

同时，为了解决手机端兼容问题，可以使用类似 `@touchstart.prevent` 的方式阻止事件的默认行为。

## 复杂组件之间的传值

这里主要说的就是非父子组件之间的传值，例如父与子的子、兄弟组件，虽然可以间接完成，但是过于麻烦，由于 Vue 的定义轻量级，它并不具备解决这个问题的能力，但是我们可以借助其他的方案。

1. Vue 官方推荐的 VueX 框架。
2. 发布-订阅模式，也就是总线机制，可以理解为是观察者模式。

这里仅说一下总线的这种方式，其实就是在所有子组件上挂一个 Vue 实例，然后通过这个实例来进行事件的发送与处理。

``` javascript
// 设置 bus
Vue.prototype.bus = new Vue();

Vue.component('child', {
  // 子组件的 data 必须是函数
  data: function () {
    return {
      selfContent: this.content
    }
  },
  props: {
    content: String
  },
  template: "<div @click='handleClick'>{{selfContent}}</div>",
  methods: {
    handleClick: function () {
      // 向总线发送事件
      this.bus.$emit("change", this.selfContent);
    }
  },
  // 生命周期，挂载时触发
  mounted: function () {
    var this_ = this;
    this.bus.$on("change", function (val) {
      this_.selfContent = val;
    })
  }
})

var vm = new Vue({
  el: "#root"
})
```

就是通过一个生命周期来完成的。

> 在 Vue 中，类似 `vm.$xx` 这种的调用，后面跟一个 `$` 符号，意思是调用 Vue 实例的方法。

### 使用Vuex

简单说 Vuex 就是一个单向数据的改变流，把需要改变的数据单独存储起来，然后通过指定的流程来进行更改。

![](https://vuex.vuejs.org/vuex.png)

一般情况下，我们在单独的一个 js 中设置 Vuex，例如：

``` javascript
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    name: localStorage.name || "xxx"
  },
  actions: {
    changeName (ctx, name) {
      // 调用 mutations
      ctx.commit('changeName', name)
    }
  },
  mutations: {
    changeCity (state, name) {
      state.name = name
      // 本地存储 (低版本浏览器或者隐身模式可能会抛异常)
      try {
        localStorage.name = name
      } catch (e) {}
    }
  }
})
```

然后在 App 入口 js 中进行引用：

``` javascript
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
```

这样在任何子组件中就可以通过 `this.$store.state` 来获取 Vuex 中 state 的数据啦。

``` javascript
// 触发修改
this.$store.dispatch('changeName', name)
// 如果没有异步获取数据逻辑，可以直接调用 mutations
this.$store.commit('changeName', name)
```

其中我们还使用了本地存储 localStorage。

实际中，大多会拆分 index.js 将 state、actions、mutations 单独放在一个文件中。

---

另外，Vuex 还提供了高级 API 允许我们更精简的写代码，例如：

``` javascript
import { mapState, mapActions } from 'vuex'
...
methods: {
  handleClick (name) {
    // 使用 Vuex 改变全局数据
    // this.$store.dispatch('changeName', name)
    this.changeName(name)
    // 或者可以直接调用 mutations
    // this.$store.commit('changeName', name)
    this.$router.push('/')
  },
  // 展开运算符，
  ...mapActions(['changeName'])
},
computed: {
  // 使用 Vuex 的便捷映射, 数组、对象皆可
  ...mapState({
    name: 'loli'
  })
}
```

基本的 Vuex 操作就是这些了。

## 使用插槽

简单来说，当子组件有一部分内容是由父组件传递过来的 dom 来显示的时候，就可以使用插槽来处理。

要解决的问题，之前：

``` javascript
// <child content="<p>hello</p>">
Vue.component('child', {
  props: ['content'],
  // ES6 语法
  template: `<div>
              <p>Dear</p>
              <div v-html="this.content"></div>
            </div>`
})

var vm = new Vue({
  el: "#root"
})
```

之后：

``` html
<div id="root">
  <!-- <child content="<p>hello</p>"></child> -->
  <child>
    <p>hello</p>
  </child>
</div>

<script>
  Vue.component('child', {
    props: ['content'],
    // ES6 语法
    template: `<div>
                <p>Dear</p>
                <slot>默认内容</slot>
              </div>`
  })

  var vm = new Vue({
    el: "#root"
  })
</script>
```

可以看出子组件里的内容会被 slot 标签插入。

如果需要将子组件里内容分片，那么也是可以的：

``` html
<div id="root">
  <child>
    <p slot="one">one</p>
    <p slot="two">two</p>
  </child>
</div>

<script>
  Vue.component('child', {
    props: ['content'],
    // ES6 语法
    template: `<div>
                <slot name="one"></slot>
                <p>Dear</p>
                <slot name="two"></slot>
               </div>`
  })

  var vm = new Vue({
    el: "#root"
  })
</script>
```

就是稍微改变了下，进行了标识。

---

最后来看一下高级用法-作用域插槽，从例子开始：

``` html
<div id="root">
  <show>
    <!-- 固定写法，template 开始 -->
    <template slot-scope="props">
      <h2>{{props.item}}</h2>
    </template>
  </show>
</div>

<script>
  Vue.component('show', {
    data: function () {
      return {
        list: [1,2,3,4]
      };
    },
    template: `<div>
                <slot 
                  v-for="item of list"
                  :item=item
                ></slot>
              </div>`
  })

  var vm = new Vue({
    el: "#root"
  })
</script>
```

稍微解释一下，在 template 中，使用 for 来“循环插槽”，将每次循环的数据绑定到了 item 变量，然后视图中通过 slot-scope 来接收。

## 动态组件

这里说的是动态的切换组件，可以手动实现，也可以通过 Vue 提供的 component 标签来实现，例子：

``` html
<div id="root">
  <component :is='type'></component>

  <child-one v-if="type === 'child-one'"></child-one>
  <child-two v-if="type === 'child-two'"></child-two>
  <button @click="handleClick">change</button>
</div>

<script>
  Vue.component('child-one', {
    template: '<div v-once>one</div>'
  });

  Vue.component('child-two', {
    template: '<div v-once>two</div>'
  });

  var vm = new Vue({
    el: "#root",
    data: {
      type: 'child-one'
    },
    methods: {
      handleClick: function () {
        this.type = this.type === 'child-one' ? 'child-two' : 'child-one';
      }
    }
  })
</script>
```

component 和下面使用 v-if 控制的标签是一样的，因为每次切换都需要销毁、重新创建，所以性能上会有点损耗，可以在模板上使用 v-once 来将实例放到内存中，这样就省去了创建、销毁的时间。

## 注意事项

使用 v-for 无论是遍历数组还是遍历对象，直接使用下标增加、修改数组 View 不一定会刷新，**想要视图跟着刷新就必须用方法来增加**，例如数组的 pop、push 等方法；对于对象的属性增加，可以使用 Vue 的全局方法 set（`Vue.set(obj, key, val)` 或者使用实例的 set 方法，`vm.$set(obj, key, val)`），当然 set 方法也可以用来修改数组 key 就是下标了。

---

使用 v-for 的时候，为了不引入多余的 HTML 结构，可以使用 template 标签占位，在这个标签里使用 v-for 这样渲染后就没有痕迹了。

---

可以通过绑定 class 属性的方式来改变样式，支持对象、数组。

---

解决组件与 HTML5 规范冲突，可以使用 is 属性来标识其真正的组件，例如：`<tr is:"row"></tr>`。

使用 Vue 提供的标签也是类似，例如：`<router-link tag="li" :to="/index/">`

---

子组件里，data 属性必须是函数，可以是这个函数返回一个对象，里面包含一些属性；这样也就达到了多个子组件数据互不影响的目的。

---

必要的操作 Dom 时，通过 ref 属性来标识，在事件中可以 `this.$refs.name` 来获取 Dom 元素；如果 ref 加在了组件上，那么得到的就是这个组件的引用了。
特殊情况下，如果 ref 和 v-for 连用，那么使用 `:ref=` 的形式，并且获取的是数组，需要 `name[0]` 使用。

---

子组件向父组件传值是通过事件的形式，一般来说在子组件中使用 `this.$emit('name', data)` 来进行手动触发；配合子组件的 HTML 标签中使用 `@name="fun"` 来进行监听。

---

在导入语法中，使用 `@` 来表示 src 目录；在组件样式编写的时候，如果不想影响到其他组件的样式，在 style 标签里加一个 scoped 即可。

使用 @import 导入 css 变量域，`~` 固定前缀：`@import '~@style/varibles.styl'`

如果使用 stylus 语法，可以使用 `>>>` 来做样式穿透。

---

在 webpack 的配置文件里，可以使用 alias 来定义别名，快速引用文件夹，例如默认的 @ 表示 src 就是这样设置的。

比较混乱，待整理。

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

## 路由

使用路由来根据访问 url 动态切换的目的，在 App 主入口加入 `<router-view/>` 即可启用，在 `src\router\index.js` 进行配置路由规则即可。

其中路由规则的 url 映射可以使用变量来进行区分，例如：`/detail/:id` ，然后可以在子组件通过 `this.$route.params.id` 来获得变量。

使用 `<router-link to="/list">跳转</router-link>` 可进行单页应用的跳转，在跳转过程中不需要请求新的 HTML，但是首屏加载会慢一点，SEO 也不是很好。

除了使用标签来路由，在 JS 环境下，可以使用编程式导航：`this.$router.push('/')`

---

为了防止路由调整后滚动条不会重置，官方文档的解决方案是在路由的 js 配置中加入下面的代码：

``` javascript
export default new Router({
  routes: [
    {
      path: '/',
      name: 'Home',
      component: Home
    }
  ],
  scrollBehavior (to, from, savedPosition) {
    return {x: 0, y: 0}
  }
})
```

这样就解决了跳转后滚动条错乱的情况。

## 动画

最简单的淡出淡然，可以使用 transition 标签进行包裹，作用简单说就是：

在开始前，会给被包裹的标签增加两个 css 样式：v-enter 和 v-enter-active，当动画运行到第二帧的时候会去除 v-enter 这个样式，再增加一个 v-enter-to 样式；最后动画结束时去除所有样式。

在使用 transition 标签的时候，如果加了 name 属性，那么样式就以你定义的名字作为前缀代替 v。

上面说的是显示的动画，隐藏的过度也是类似，只需要把 enter 换成 leave 就可以了。

如果想自定义 css 的名字，可以使用 enter-active-class 属性来定义，其他同理。

或者可以使用 `animated.css` 提供的样式，快速开发，使用起来非常简单，引入不要的 css 库，然后利用上面所说来自定义 css 名字，格式就是：`animated 动画名` 名字可以在官网找，其实就是封装了下 css3 的 @keyframes 特性。

PS：想要初始化的时候就展示动画需要使用 appear 属性来配合。

---

除了使用 css 来做动画，也可以使用 js 实现，在标签中通过 @before-enter、@enter、@after-enter 等来绑定方法，会传递一个参数过去，也就是包裹的 dom 元素 el。

其中 @enter 会传递两个参数，第一个与上面一样，第二个是个函数引用 done，在动画完成后调用一下它告诉 Vue 动画结束，这样就会再继续执行下面的 after。

如果嫌麻烦，可以使用像 **Velocity** 这样的 js 动画库。

---

对于列表动画，可以使用 transition-group 标签来包裹，其实它的作用就是将里面的循环每一个都包裹一个 transition 标签。

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

## 优化相关

使用 `<keep-alive>` 标签 Vue 会自动帮你进行优化请求，例如 ajax 请求，如果启用了 `<keep-alive>` 标签，当第二次进行 ajax 请求时，会直接从内存里拿数据（mounted 函数不会执行）。

一般就直接在 App.vue 中使用了：

``` html
<template>
  <div id="app">
    <!--启用缓存,重新路由后不再发请求-->
    <keep-alive exclude="Detail">
      <router-view/>
    </keep-alive>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>
```

然后有的数据我们是不想进行缓存的，所以就可以使用 exclude 指定组件的名字（就是 name 属性）进行排除。

或者可以使用生命周期函数 activated 来刷新数据（可能需要进行一定的逻辑判断·），这个函数在页面重新加载时执行。

---

默认情况下 webpack-server 是不支持 ip 访问的，如果就想 ip 访问，可以在 **package.json** 文件中的 dev 加一个配置：`"dev": "webpack-dev-server --host 0.0.0.0 --inline --progress --config build/webpack.dev.conf.js"`

## CSS相关

使用 CSS 的 flex （`flex:1`）属性撑开盒模型