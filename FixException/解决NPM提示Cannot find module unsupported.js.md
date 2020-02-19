# 解决NPM提示Cannot find module unsupported.js

这个问题怎么说呢，就是手贱 brew 升级了吧。

结果是最新的 node 不稳定还是怎么着，npm 就挂了。

---

解决方案大多数都是让删除 `/usr/local/lib/node_modules/` 下的内容，然后使用 brew 重新安装。

但是我舍不得删除安装的全局 model，终于，发现了另一个方案：

``` shell
sudo rm -rf /usr/local/lib/node_modules/npm
brew reinstall node


sudo rm -rf /usr/local/lib/node_modules/npm
brew uninstall --force node
brew uninstall --ignore-dependencies node
brew install node
```

这起码比删除全部好多了。

参考：https://www.cnblogs.com/wanghui-garcia/p/9947347.html

遗憾的是，这两种方法我都 G 了，node 倒是可以用了，但是 npm 提示找不到，忍无可忍，直接卸载使用官方安装包安装。

或者参考一下：https://yamdestiny.xyz/2019/04/24/how-to-reinstall-node-js-on-mac/

``` shell
brew install nvm

nvm install node
```

---

心好累，brew 安装的 MySQL、nginx 全部 GG，真的不要手贱升级啊！！！

---

在重新安装了 nginx 和 MySQL 后，终于。。。。能启动了，使用 reinstall 可以解决，配置文件和数据没丢，万幸。



然后，，不知道为什么 brew services 不能用了。。。。。

于是一顿操作，最后使用 brew update 解决了。。。。

然而，，好像 python 又不行了。。。。

---

果然，试了下使用 python3 可以运行，想起来当时配置的绝对路径，果然是因为版本升级了。





当使用 `brew update` 更新后有问题时，可以执行命令：`brew update-reset`，重新设置下 brew 的配置。

---

当遇到提示：`env: node\r: No such file or directory` 的时候，最好的简单方案就是转换执行文件。

例如使用 vim 打开，执行 `:set ff=unix` 即可。