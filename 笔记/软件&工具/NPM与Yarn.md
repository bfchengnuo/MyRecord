# NPM与Yarn

NPM 不用多说，Node.js 默认的包管理工具，常见的：

``` shell
git clone xxx
npm install
npm run dev
```

最近几年，你还会经常看到另一种方式：

``` shell
git clone xxx
yarn
yarn start
```

按照常理，新的工具流行起来，必然要比之前的有较大优势才对。

## Yarn是什么？

Facebook、Google、Exponent 和 Tilde 联合推出了一个新的 JS 包管理工具 — Yarn，正如官方文档 中写的，Yarn 是为了弥补 npm 的一些缺陷而出现的： 

- npm 安装包（packages）的速度不够快，拉取的 packages 可能版本不同

- npm 允许在安装 packages 时执行代码，这就埋下了安全隐患

别慌，Yarn 没想要完全替代 npm，**它只是一个新的 CLI 工具，拉取的 packages 依然来自 npm 仓库**。仓库本身不会变，所以获取或者发布模块的时候和原来一样。

---

对此，我表示  `npm install` 的时候真的**巨慢**，服务器是一回事，就算使用了 cnpm 也需要等一段时间，等待过程中内心祈祷千万不要 error。

再说 package.json 文件中版本号也挺坑的，老老实实写固定版本号，例如  1.1.0 还好，偏偏有的就是 `~1.1.0` 或者 `^1.1.0` 这种，一个版本不兼容就 GG。

PS： `1.1.0` 表示安装指定的 1.1.0 版本，`～1.1.0` 表示安装 1.1.X 中最新的版本，`^1.1.0` 表示安装 1.X.X 中最新的版本。

## Yarn特性

### 速度快

**并行安装**：无论 npm 还是 Yarn 在执行包的安装时，都会执行一系列任务。npm 是按照队列执行每个 package，也就是说必须要等到当前 package 安装完成之后，才能继续后面的安装。而 Yarn 是同步执行所有任务，提高了性能。

**离线模式**：如果之前已经安装过一个软件包，用 Yarn 再次安装时之间从缓存中获取，就不用像 npm 那样再从网络下载了。

### 安装版本统一

为了防止拉取到不同的版本，Yarn 有一个锁定文件 (lock file) **记录了被确切安装上的模块的版本号**。每次只要新增了一个模块，Yarn 就会创建（或更新）yarn.lock 这个文件。

这么做就保证了，每一次拉取同一个项目依赖时，使用的都是一样的模块版本。npm 其实也有办法实现处处使用相同版本的 packages，但需要开发者执行 npm shrinkwrap 命令。这个命令将会生成一个锁定文件，在执行 npm install 的时候，该锁定文件会先被读取，和 Yarn 读取 yarn.lock 文件一个道理。

npm 和 Yarn 两者的不同之处在于，Yarn 默认会生成这样的锁定文件，而 npm 要通过 shrinkwrap 命令生成 npm-shrinkwrap.json 文件，只有当这个文件存在的时候，packages 版本信息才会被记录和更新。

> npm 也感受到了压力，在 npm5 的版本， 默认新增了类似 yarn.lock 的 package-lock.json，在处理依赖方面也有一定优化，不过相比之下还是 yarn 更快一点。

### 更简洁的输出

 npm 的输出信息比较冗长。在执行 npm install 的时候，命令行里会不断地打印出所有被安装上的依赖。

相比之下，Yarn 简洁太多：默认情况下，结合了 emoji直观且直接地打印出必要的信息，也提供了一些命令供开发者查询额外的安装信息。 

###  多注册来源处理 

 所有的依赖包，不管他被不同的库间接关联引用多少次，安装这个包时，只会从一个注册来源去装，要么是 npm 要么是 bower, 防止出现混乱不一致。 

### 更好的语义化 

  yarn 改变了一些 npm 命令的名称，比如  yarn add/remove，感觉上比 npm 原本的 install/uninstall 要更清晰。 

| npm                                   | yarn                  |
| :------------------------------------ | :-------------------- |
| npm install                           | yarn                  |
| npm install react --save              | yarn add react        |
| npm uninstall react --save            | yarn remove react     |
| npm install react --save-dev          | yarn add react --dev  |
| npm update --save                     | yarn upgrade          |
| npm -g(--global) install react --save | yarn global add react |

另外，如果想分析包的依赖关系，可以试试 `yarn why` 这个命令。

## 其他

看到这么个新闻：

> 根据一项研究，NPM JS 库生态系统比大多数人想象的**更相互交织**。
>
> 德国的研究人员分析了（PDF）NPM 生态系统的依赖图，他们下载了 2018 年 4 月前发布的所有 NPM JS 包的元数据，创建了一幅巨大的依赖图。
> 研究人员还分析了相同包的不同版本，历史版本，以及包维护者和已知安全漏洞。
>
> 研究人员想知道，入侵一名或多名维护者的帐号、一个包或多个包的漏洞对 NPM 生态系统会有多大影响，以及一次影响成千上万的项目的安全事故临界点。
>
> 研究人员发现，临界点很容易达到，因为一个 NPM 包有着异常高数量的依赖，平均一个包加载了 39 名不同维护者的 79 个第三方包。一些流行的包使用了 100 多名维护者写的代码。
>
> 研究人员发现，只要入侵 20 名最有影响力的包维护者帐号就可能危及半数 NPM 生态系统。

还有其他很多的骚操作，npm 安装信息中插广告的，携带挖矿程序的。。。

## 参考

https://juejin.im/post/5ab89cc4f265da237506e367