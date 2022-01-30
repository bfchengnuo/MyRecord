# eladmin二次开发手册

首先是前端，基于 vue-element-admin 做的。

自己做项目玩，从零开始简直太烦了，尤其是这种后台管理界面，这种现成的是不错的方案，官方文档中也有说：定位是后台集成方案，不太适合当基础模板来进行二次开发。因为本项目集成了很多你可能用不到的功能，会造成不少的代码冗余。

前端 UI 还可以花点心思，使用纯 Vue 的脚手架来构建，后台就随便怼了，怎么方便怎么来；

在官方给的文档 [vue-element-admin](https://panjiachen.github.io/vue-element-admin-site/zh/) 里写的比较全面了，但是不一定所有的点都需要修改，下面我结合自己实际把玩过程中的改造点重点说说那些地方是需要关注的，应该怎么改。

## 目录结构

拿到代码首先关注的肯定是目录结构：

```
├── build                      // 构建相关  
├── config                     // 配置相关
├── src                        // 源代码
│   ├── api                    // 所有请求
│   ├── assets                 // 主题 字体等静态资源
│   ├── components             // 全局公用组件
│   ├── directive              // 全局指令
│   ├── filtres                // 全局 filter
│   ├── icons                  // 项目所有 svg icons
│   ├── lang                   // 国际化 language
│   ├── mock                   // 项目mock 模拟数据
│   ├── router                 // 路由
│   ├── store                  // 全局 store管理
│   ├── styles                 // 全局样式
│   ├── utils                  // 全局公用方法
│   ├── vendor                 // 公用vendor
│   ├── views                   // view
│   ├── App.vue                // 入口页面
│   ├── main.js                // 入口 加载组件 初始化等
│   └── permission.js          // 权限管理
├── static                     // 第三方不打包资源
│   └── Tinymce                // 富文本
├── .babelrc                   // babel-loader 配置
├── eslintrc.js                // eslint 配置项
├── .gitignore                 // git 忽略项
├── favicon.ico                // favicon图标
├── index.html                 // html模板
└── package.json               // package.json
```

打交道最多的就是 src 下的 views 界面和与之对应的 api 下的 Ajax 请求，他们之间都是根据文件夹分层严格对照的，这是规范不强制，但是为了看着舒服后面还是最好保持这种规则。

components 下是抽取的**全局**公共组件，一般情况不需要改动，页面级的组件建议还是放在各自 views 文件下。

store 下就是使用 vuex 来做的存储相关，大部分情况也用不到，每个页面耦合度其实不会太大，各自用各自的基本足够。

## 封装axios

处理 Ajax 请求肯定是要用 axios 的，但是直接用肯定还是有点麻烦的，ela 的封装可以参考下，还不错：

``` js
import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.BASE_API, // api的base_url
  timeout: 5000 // 请求超时时间
})

// request拦截器
service.interceptors.request.use(config => {
  // Do something before request is sent
  if (store.getters.token) {
    config.headers['X-Token'] = getToken() // 让每个请求携带token--['X-Token']为自定义key 请根据实际情况自行修改
  }
  return config
}, error => {
  // Do something with request error
  console.log(error) // for debug
  Promise.reject(error)
})

// respone拦截器
service.interceptors.response.use(
  response => response,
  /**
  * 下面的注释为通过response自定义code来标示请求状态，当code返回如下情况为权限有问题，登出并返回到登录页
  * 如通过xmlhttprequest 状态码标识 逻辑可写在下面error中
  */
  //  const res = response.data;
  //     if (res.code !== 20000) {
  //       Message({
  //         message: res.message,
  //         type: 'error',
  //         duration: 5 * 1000
  //       });
  //       // 50008:非法的token; 50012:其他客户端登录了;  50014:Token 过期了;
  //       if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
  //         MessageBox.confirm('你已被登出，可以取消继续留在该页面，或者重新登录', '确定登出', {
  //           confirmButtonText: '重新登录',
  //           cancelButtonText: '取消',
  //           type: 'warning'
  //         }).then(() => {
  //           store.dispatch('FedLogOut').then(() => {
  //             location.reload();// 为了重新实例化vue-router对象 避免bug
  //           });
  //         })
  //       }
  //       return Promise.reject('error');
  //     } else {
  //       return response.data;
  //     }
  error => {
    console.log('err' + error)// for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  })

export default service

// ------------------------------------
// 引入使用
import request from '@/utils/request'

//使用
export function getInfo(params) {
  return request({
    url: '/user/info',
    method: 'get',
    params
  });
}

```

对于 eladmin 封装文件可以在  `src/utils/request.js` 查看，使用在 api 文件夹下随处可见。

简单来看就是俩过滤器，一个请求一个响应，请求是自动添加 token 到请求头，响应是查看状态码是不是正常。

> 这里需要注意一下变量引用，例如 `process.env.BASE_API` 这个玩意到底是引用的哪里呢？
> 官方文档中说的是注意一下 `@/config/dev.env.js` 这个文件夹，但是我在 eladmin 中并未发现这个配置，取而代之是根目录下的几个以 `.env.*` 开头的文件。
> 可以去看一下参考里的 vue cli3 新增的环境变量相关内容，当你项目里有了上述 `.env.*` 文件，使用 `process.env` 可以查看当前的环境，例如在 package.json 中可以使用 `vue-cli-service build --mode prod` 来指定环境，serve 默认使用 development。

最后，关于跨域问题，dev 环境也可以通过 `webpack-dev-server`的`proxy`来解决（vue cli2 和 3 不同版本的配置文件位置不同，参考之前写的文章，eladmin 可以参考 `vue.config.js` 这个文件），或者通过 Nginx 的反代、cors 都可以。

## 路由

这是比较重要的一块，如果想新加一个 view，最好还是要理解下路由是怎么处理的，因为侧边栏和路由是绑定在一起的，所以你只有在 `@/router/index.js` 下面配置对应的路由，侧边栏就能动态的生成了，相应的你就需要遵循一定的规则了。
PS：在 eladmin 中做了一些改动，最原始的配置在 `@/router/routers.js` 然后又在 index.js 中进行了二次处理。

这里的路由分为两种，`constantRoutes` 和 `asyncRoutes`。全部使用懒加载模式。

1. constantRoutes
   代表那些不需要动态判断权限的路由，如登录页、404、等通用页面。

2. asyncRoutes
   代表那些需求动态判断权限并通过 `addRoutes` 动态添加的页面。

所以路由这里还要配合权限来看。eladmin 中在 index 文件中主要就是做了权限的处理。
也就是判断一下当前是否已登录状态，如果不是就尝试自动登陆，最终执行的是一个 loadMenus 方法来进行加载菜单，然后将请求的响应转换为 view 对象，通过 GenerateRoutes 分发进行存储路由信息之后动态添加到路由表，这样就能达到当前用户只拥有他该有的路由。

## 数据加载

这应该是 eladmin 中独有的，介绍中数据加载使用了两种模式：

1. 混入模式，相对简单
2. 组件模式，功能强大

至于他们的工作原理，只丢下了一句查看源码，虽然我前端小白，但是本着好奇心理，决定稍微看一下。

关于混入模式，这个主要是 mixin 这个关键词，我当年学 Vue 的时候完全跳过了这个，发现这是 Vue 官方支持的，所以不知道这玩意的需要先补一下 Vue 的官方文档。

> Mixin 提供了一种非常灵活的方式，来分发 Vue 组件中的可复用功能。一个 mixin 对象可以包含任意组件选项。当组件使用 mixin 对象时，所有 mixin 对象的选项将被“混合”进入该组件本身的选项。

对于组件模式，文档里以字典为例，我理解的就是我们接触最多的组件式开发，只需要定义一个 dicts 数组变量，指定字典名称就会自动加载，使用 `this.dict` 直接引用。
用 WebStorm 查一下引用就知道是 src/components/Dict/index.js 这个文件在处理，然后这个文件又在全局的 main.js 中被 import，所以搞懂这个文件就知道它是怎么做到的了。
然后看到的还是 Vue.mixin，是个全局混入。。。。接下来只要看懂了 this.$nextTick 与 的意思，剩下的代码就非常好懂了，我在瞎搞的项目里做了详细注释。

> 关于 this.$nextTick 的用法，原理涉及 Vue 如何实现响应式 DOM 刷新，里面有一个异步更新队列的概念，而作为我们小白，只需要理解在我们改变了数据后，DOM 不一定会立即刷新，所以如果你的代码接下来执行的环境需要是刷新后的 DOM，那么为了保证一定会在刷新后执行，你可以使用 `this.$nextTick` 通过回调的方式来执行期望的代码。

## 页面

我二次定制最主要的目的是加一些自定义页面，可以直接 copy 一个差不多的改改，打开一个 views 下的页面文件，模板部分没啥好说的，它封装的几个组件目前我还没啥需要修改的，暂时不看它的封装了；主要就是看 JS 了。

想要看懂 JS，主要是要理解它的一个叫 Crud 的全局组件封装，JS 与模板中大量使用了这个模块；先看 vue 文件：

``` js
// 搜索与重置按钮
import rrOperation from '@crud/RR.operation'
// 增删改查、导出按钮，以及右边的刷新搜索按钮等 
import crudOperation from '@crud/CRUD.operation'
// 表格操作里的删除、编辑
import udOperation from '@crud/UD.operation'
// 分页封装
import pagination from '@crud/Pagination'
```

关于样式，这里提一嘴，基本的 crud 组件那一套是有全局样式的，在 `src/assets/styles` 文件夹下可以清楚看到，然后你查找引用的话就会发现在 main.js 中被 import 了，采用的是 scss 语法。

接下来就是最核心的 crud.js 这个文件了。

## Curd组件封装

每个页面几乎都用到了这个组件，相关内容可以在 JS 看到：

``` js
import CRUD, { presenter, header, form, crud } from '@crud/crud'

const defaultForm = { id: null, mark: null, name: null, gender: null, createBy: null }
export default {
  name: 'User',
  components: { pagination, crudOperation, rrOperation, udOperation },
  mixins: [presenter(), header(), form(defaultForm), crud()],
  dicts: ['gender'],
  cruds() {
    return CRUD({ title: 'XX管理', url: 'api/user', idField: 'id', sort: 'id,desc', crudMethod: { ...crudStudent }})
  },
  data() {
    return {
      permission: {
        add: ['admin', 'commonRole'],
        edit: ['admin', 'commonRole'],
        del: ['admin', 'commonRole']
      },
      rules: {
        name: [
          { required: true, message: '姓名不能为空', trigger: 'blur' }
        ]
      },
      queryTypeOptions: [
        { key: 'gender', display_name: '性别' }
      ]
    }
  },
  methods: {
    // 钩子：在获取表格数据之前执行，false 则代表不获取数据
    [CRUD.HOOK.beforeRefresh]() {
      return true
    }
  }
}
```

其中，defaultForm 是表单对应的字段、dicts 用于全局字典加载、permission 是封装的几个操作权限、rules 是表达校验相关、queryTypeOptions 是模糊搜索相关、methods 里只有一些钩子函数，这些一看就明白不需要细说了；

先看导入，从 `@crud/crud` 中导入了几个组件，导入的语法就不说了标准的 ES6，进入核心 crud js，又导入了一些 url 请求处理相关的工具类，这个没啥特别说的；

接下来是 CRUD 函数的定义，开始做了一个配置 merge 操作，之后定义了一个 data 对象进行一些参数的初始化；
其中使用到了 get 关键字语法，不熟悉的我举个例子：

``` js
const obj = {
  log: ['a', 'b', 'c'],
  get latest() {
    if (this.log.length === 0) {
      return undefined;
    }
    return this.log[this.log.length - 1];
  }
};

console.log(obj.latest);
// expected output: "c"
```

简单说就是 get 语法将对象属性绑定到查询该属性时将被调用的函数上，很便捷的一个语法。
在 crud 中的 status 中用来判断添加和编辑的状态（cu）和动态标题（使用了一个模板字符串的语法）。

到这里 data 方法基本就结束了，也没啥太复杂的内容，所以继续往下看。

---

在 methods 中定义了一些友好提示，主要是使用到了 crud 这个对象的属性，后面会说；

## 参考

[vue-cli 3.0 配置环境变量](https://blog.csdn.net/yun_hou/article/details/101208332)

