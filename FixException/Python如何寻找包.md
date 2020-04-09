# Python如何寻找包

现在大家的电脑上很可能不只有一个 Python，还有更多的虚拟环境，导致安装包的时候，一不小心你就忘记注意安装包的路径了。

假如你的 Python 解释器的路径是 `<path_prefix>/bin/python`，那么你启动 Python 交互环境或者用这个解释器运行脚本时，会默认寻找以下位置：

1. `<path_prefix>/lib`（标准库路径）
2. `<path_prefix>/lib/pythonX.Y/site-packages`（三方库路径，X.Y 是对应 Python 的主次版本号）
3. 当前工作目录（`pwd`命令的返回结果）

这里如果你用的是 Linux 上的默认 Python，`<path_prefix>` 就是 `/usr`，如果你是自己使用默认选项编译的，`<path_prefix>` 就是 `/usr/local`。

从上面第二条可以看到不同次版本号的 Python 的三方库路径不同，如果你把 Python 从 3.6 升级到 3.7 那么之前装的三方库都没法用了。当然你可以整个文件夹都拷贝过去，**大部分情况**不会出问题。

## 有用的函数

- `sys.executable` 当前使用的 Python 解释器路径
- `sys.path` 当前包的搜索路径列表
- `sys.prefix` 当前使用的`path_prefix`

示例：

``` python
import sys

sys.path
// out ....
sys.prefix
// out ....
```

方便你查看具体的路径。

如果需要自定义添加包，可以设置 **PYTHONPATH** 环境变量，其中的路径是优先于默认搜索路径的，多个目录使用 `:` 分割。

## 如何安装包

这里就说 pip 了，运行 pip 有两种：

- `pip ...`
- `python -m pip ...`

第一种方式和第二种方式大同小异，区别是第一种方式使用的 Python 解释器是写在 pip 里的，一般情况下，如果你的 pip 路径是 `<path_prefix>/bin/pip`，那么 Python 路径对应的就是 `<path_prefix>/bin/python`。

第二种方式则显式地指定了 Python 的位置。这条规则，对于所有 Python 的可执行程序都是适用的。

不加任何自定义配置时，使用 pip 安装包就会自动安装到 `<path_prefix>/lib/pythonX.Y/site-packages` 下

----

虽然可以使用 prefix 或者 root 参数自定义路径，但是不推荐。

当然更好还是直接用 conda 这种软件。