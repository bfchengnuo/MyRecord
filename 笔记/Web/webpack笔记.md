# webpack笔记

前端必备打包工具！

中文文档地址：https://webpack.docschina.org

## 介绍

Webpack 是一个开源的前端打包工具。Webpack 提供了前端开发缺乏的模块化开发方式，将各种静态资源视为模块，并从它生成优化过的代码。

要使用 Webpack 前须先安装 Node.js。Webpack 其中一个特性是使用加载器来将资源转化成模块。开发者可以自定义加载器的顺序、格式来因应项目的需求。

## 安装

webpack 不推荐全局安装，安装命令为：

``` shell
npm install --save-dev webpack
# 安装指定版本
npm install --save-dev webpack@<version>

# 如果你使用 webpack 4+ 版本，你还需要安装 CLI。
npm install --save-dev webpack-cli
```

快速创建一个 webpack 工程可以这样：

``` shell
mkdir webpack-demo && cd webpack-demo
npm init -y
npm install webpack webpack-cli --save-dev
```

## 基本使用

最简单的打包命令：`webpack abc.js abc.bundle.js`

或者可以使用配置文件来进行打包，配置文件默认名为：`webpack.config.js` ，或者可以使用 --config 来指定那个文件。

``` javascript
module.exports = {
  // 打包的入口文件
  entry: './src/index.js',
  output: {
    filename: 'bundle.js',
    path: './dist/js'
  }
};
```

有了这个配置文件，就可以直接在命令行中执行 webpack 了。

除了这两种，还可以使用 npm 的脚本来运行，在 **package.json** 文件中新加一个自定义脚本：

``` json
{
  "name": "webpack-demo",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "test": "echo \"Error: no test specified\" && exit 1",
		"build": "webpack --progress --colors"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "webpack": "^4.0.1",
    "webpack-cli": "^2.0.9",
    "lodash": "^4.17.5"
  }
}
```

然后执行 `npm run build` 执行就可以得到相同的效果。

这样在 HTML 中只需要引用这个打包后的文件就 ok 了，其他不需要考虑。

> 关于模块化，webpack 支持 CommonJs, AMD, ES6 语法格式；其中最常用的就是 `import` 、`require`和 `export`语句了。 
>
> PS：require 属于 CommonJs；

默认 webpack 只支持 js 进行打包，想要打包 css 文件需要安装相应的 load：

`npm install css-loader style-loader --save-dev`

然后引用的时候这样写：`require('style-loader!css-loader!./style.css')` ，这里使用的是 CommonJs 的写法，因为暂时写在 js 文件里做测试，所以就先这样了；其中使用了两个 loader ，style-loader 是让 css 生效，css-loader 是让 webpack 可以正确的打包。

通过 style-loader 会直接将其打包的 css 插入到 head 标签里。

另一种写法就是在命令行中指定 loader ：

`webpack xx xx.bundle.js --module-bind 'css=style-loader!css-loader'`

## 参数

使用 webpack 命令可以在后面追加一些参数，比如：

- `--watch`  ：实时监视部署生效
- `--progress` ：显示进度
- `--display-modules` ：查看所有的引用的（打包的）模块
- `--display-reasons` ：显示打包的原因（为什么要打包）

## 打包配置

前面简单提到过使用 `webpack.config.js` 进行打包，但是如果其中的 entry 有多个呢？可以使用数组进行添加~

如果每个之间是独立的呢？可以使用对象的形式来书写：

``` javascript
module.exports = {
  // 打包的入口文件
  entry: {
    app: './src/app.js',
    search: './src/search.js'
  },
  output: {
    filename: '[name].js',
    path: '/dist',
    // 多用于线上部署，引用地址会被替换为 publicPath 开头的地址
    // 差不多是 publicPath + path 的拼接形式
    publicPath: 'http://abc.com'
  }
};
```

entry （或者叫 chunks）使用对象的形式打包多个的时候，如果 output 中 filename不做相应的处理写死的话，那么后面的其实会覆盖前面的，最终只会生成一个文件。

所以就需要使用所谓的“占位符”了，常见的占位符有：

- `[name]`

  使用模块名称

- `[id]`

  使用内部 chunk id ，或者叫模块标识符(module identifier) 

- `[chunkhash]`

  chunk 内容的 hash 

## 管理输出

到目前为止，我们在 `index.html` 文件中手动引入所有资源，然而随着应用程序增长，并且一旦开始对文件名使用哈希 (hash)] 并输出多个 bundle，手动地对 index.html 文件进行管理，一切就会变得困难起来。然而，可以通过一些插件，会使这个过程更容易操控。

这里，我们用 [`HtmlWebpackPlugin`](https://webpack.docschina.org/plugins/html-webpack-plugin) 来解决这个问题。 安装命令：

`npm install --save-dev html-webpack-plugin`

然后在 **webpack.config.js** 文件中更新配置：

``` javascript
const HtmlWebpackPlugin = require('html-webpack-plugin');
module.exports = {
  entry: {
    app: './src/index.js',
    print: './src/print.js'
  },
  plugins: [
    new HtmlWebpackPlugin({
      title: 'Output Management'
    })
  ],
  output: {
    filename: 'js/[name].bundle.js',
    path: './dist'
  }
};
```

注意 plugins 中添加的内容，然后观察一下就可以看出，输出的 HTML 文件自动添加了 打包后 js 的引用，位置在配置的 output 中的 path 路径下.

当配置了多个 entry 后，最终生成、引入多个 entry，前提是你 output 使用了占位符。如果不指定 inject 默认是放在 body 中进行引用。

除此之外，可以指定其他的一些 HtmlWebpackPlugin 参数：

``` javascript
{
  // ...
  plugins: [
    new HtmlWebpackPlugin({
      // 插入引用在什么地方，比如 head
      inject: false,
      // 根据某个文件为模板，在此基础上添加引用
      template: 'index.html',
      // 默认就是 index.html
      filename: 'index.html',
      title: 'webpack is good',
      // 启用压缩
      minify: {
        removeComments:true, //删除注视
        collapseWhitespace: true //删除空格
      }
      // And any other config options from html-webpack-plugin:
      // https://github.com/ampedandwired/html-webpack-plugin#configuration
    })
  ]
}
```

并且是可以进行传值的，这里可以通过 title 属性(或者自己随便写一个)来测试，在模板文件中使用 ejs 模板取值：

`<%= htmlWebpackPlugin.options.title %>`

模板中遍历其属性的例子：

``` javascript
<% for (var key in htmlWebpackPlugin.files){ %>
  // 为了避免 key/val 是一个对象情况，直接转成 json 输出
  <%= key %> : <%= JSON.stringify(htmlWebpackPlugin.files[key]) %>
<% } %>

<% for (var key in htmlWebpackPlugin.options){ %>
  <%= key %> : <%= JSON.stringify(htmlWebpackPlugin.options[key]) %>
<% } %>
```

PS：`htmlWebpackPlugin.files.chunks['app'].entry` 就可以拿到 chunk app 定义的文件名称，然后就可以使用 `<%= %>` 的方式写入到 script 标签的 src 引用中；或者更简单的让其自动添加（inject）。 

## 多页面

多页面就是输出生成多个页面，可以看到在配置文件中 plugins 对应的是一个数组，也就意味着可以传入多个插件，同时也可以传入多个相同的插件，比如 HtmlWebpackPlugin，可以根据相同的页面指定不同的模板，或者不同的页面相同的模板。

``` javascript
{
  // ...
  entry: {
    app: './src/index.js',
    print: './src/print.js'
  },
  plugins: [
    new HtmlWebpackPlugin({
      // 插入引用在什么地方，比如 head
      inject: body,
      // 根据某个文件为模板，在此基础上添加引用
      template: 'index.html',
      // 默认就是 index.html
      filename: 'index.html',
      title: 'webpack is a',
      // 排除不需要引用的 entry
      excludeChunks: ['app']
    }),
    new HtmlWebpackPlugin({
      // 插入引用在什么地方，比如 head
      inject: body,
      // 根据某个文件为模板，在此基础上添加引用
      template: 'index.html',
      // 默认就是 index.html
      filename: 'index.html',
      title: 'webpack is b',
      // 执行需要引入的 entry
      chunks: [app]
    })
  ]
}
```

在 entry 中有多个文件的情况下，根据需求，配置好 excludeChunks 或者 chunks 就好了。

## 处理资源文件

css、图片压缩、ES6 语法转换等等

### 使用Loader

Loader 专门用来处理资源，前面其实已经简单的接触过了（通过 require 的方式和 CLI 就是命令行的方式），可以使用它来支持那些 webpack 默认不支持的，支持串联的方式。

然后是第三种使用方式，上面没有学过的，使用配置文件来定义规则。

在 **webpack.config.js** 中定义：

``` javascript
module.exports = {
  module: {
    rules: [
      // 根据正则进行匹配，loader 使用串联
      { test: /\.css$/, use: 'style-loader!css-loader' }, // js 中可以导入 css，并且自动插入到 HTML
      { test: /\.ts$/, use: 'ts-loader' },
      // 处理 ES6 语法
      {
        test: /\.js$/,
        loader: 'babel',
        // loaders: []
        // 也可以在 package.json 中指定
        query: {
          presets: ['latest']  // 指定 ES6 的版本，比如 es2015 等
        },
        // 还可以使用 exclude、include 进行优化速度
        exclude: './node_modules/'
      }
    ]
  }
};
```

引用的这些插件是需要安装的，npm 安装下就好。

loader 的处理方式是从右到左的，并且是可以省略后面的 `-loader` 的。

### 处理图片资源

处理图片资源可以使用 file-loader，引用方式和上面一样：

``` javascript
module.exports = {
  module: {
    rules: [
      // ...
      {
        test: /\.(png|jpg))$/i,
        loader: 'file-loader',
        query: {
          // 将名字按照指定格式处理
          name: 'assets/[name]-[hash:5].[ext]'
        }
      }
    ]
  }
};
```

在模板中引用图片地址时，尽量使用绝对地址，使用相对地址不会处理的；实在是想用相对地址可以通过 require 实现：

`src="${ require('../abc.jpg') }"`

再说一个相似的 loader ：url-loader，它和 file-loader 非常相似，它可以设定一个 limit 值（query 中，单位字节），当图片超过这个大小就丢给 file-loader 处理，如果小于这个值就直接转成 url 图片编码的格式。

想要压缩图片可以使用 image-loader，官方建议配合 url-loader 或者 file-loader 使用。

``` javascript
module.exports = {
  module: {
    rules: [
      // ...
      {
        test: /\.(png|jpg))$/i,
        loaders: [
          'url-loader?limit=10000&name=assets/[name]-[hash:5].[ext]',
          'image-webpack'
        ]
      }
    ]
  }
};
```

可以看出参数的设置可以使用 url 的方式；加载规则还是从右到左，也就是会先进行压缩。

对于每一种图片格式都有相应的优化器，在官网可以找到这些配置。

## 其他

使用 Node 的自带 API 生成绝对路径：

``` javascript
var path = require('path');

// 使用
path.resolve(__dirname, 'src');
```

其他常用的模块比如：postcss-loader 能自动生成浏览器前缀，配合 autoprefixer 插件使用。

使用 Vue 的话可以使用 jsx 语法的 loader，babel 已经支持。