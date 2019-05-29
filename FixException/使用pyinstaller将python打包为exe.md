起因是从某 hub 发现了一个B站挂机助手，依赖于直播的一个库，作者的处理方式是将依赖的库打包为 exe，然后使用 python 进行调用。

然后因为依赖的 live 库版本太旧有 bug，作者也没有更新打包的 exe 文件，只能自己动手，进行替换。

## 准备工作

首先当然是安装 pyinstaller

```shell
pip install pyinstaller
```

> PyInstaller 是一个十分有用的第三方库，可以用来打包 python 应用程序，打包完的程序就可以在没有安装 Python 解释器的机器上运行了。
>
> 它能够在 Windows、Linux、 Mac OS X 等操作系统下将 Python 源文件打包，通过对源文件打包， Python 程序可以在没有安装 Python 的环境中运行，也可以作为一个 独立文件方便传递和管理。

## 使用

一般的用法网上能搜出一大把，常规流程：

```
pyinstaller -F helloworld.py
```

其中，-F 表示打包成单独的 .exe 文件，这时生成的 .exe 文件会比较大，而且运行速度回较慢。

另外，
-i 还可以指定可执行文件的图标； 
-w 表示去掉控制台窗口，这在 GUI 界面时非常有用。不过如果是命令行程序的话那就把这个选项删除吧！

执行步骤：

> 1、在脚本目录生成 helloworld.spec 文件； 
> 2、创建一个 build 目录； 
> 3、写入一些日志文件和中间流程文件到 build 目录； 
> 4、创建 dist 目录； 
> 5、生成可执行文件到 dist 目录；

## 打包静态文件

当你的代码需要调用一些图片和资源文件的，这是不会自动导入的，需要你自己手动复制进去才行。不然 exe 文件运行时命令窗口会报错找不到这个文件。

还是先看常规做法；
使用 pyi-makespec 生成预处理文件：

```
pyi-makespec -F helloworld.py
```

此时会生成一个 .spec 文件，这个文件会告诉 pyinstaller 如何处理你的脚本，pyinstaller 创建一个 exe 的文件就是依靠它里面的内容进行执行的。

修改预处理文件，将需要的静态资源加入进去：

```
// 修改前
datas=[]
// 修改后
datas=[('test.txt','.')]
```

最后，执行生成 exe 文件：

```
pyinstaller helloworld.spec
```

以上内容主要摘录自 [https://zhuanlan.zhihu.com/p/...](https://zhuanlan.zhihu.com/p/45288707)

------

当然，我没那么幸运，按照一顿操作并没有成功，对于预处理文件，上面写的也不是很清楚，于是研究了下，最终也算是成功了。

配置文件是不需要打包的，程序运行时也会自动读取，根据错误信息，我这个需要依赖的有两个 data.db 文件，经过搜索分别在当前目录的 dyn 和 substance 文件夹下，所以你需要这么写：

```
datas=[('dyn\\data.db', 'dyn'), ('substance\\data.db', 'substance')]
```

解释下，datas 是一个数组，每一个文件用 `(a,b)` 进行描述，其中 a 是源文件，也就是你要打包的文件，记得转义符；
b 是目标路径，也就是最终打包 exe 执行时，它需要把这些静态资源解压出来，那么解压到哪里呢就需要 b 来指定，它是一个文件夹，保险起见我设置跟原来一样的名字（路径）。

如果静态资源比较少的话，可以使用命令行来快速打包：

```
pyinstaller -F showlist.py --add-data poetry;poetry --add-data list.txt;.
```

使用的是 --add-data 命令，后面跟源与目标路径，中间用分号分割，这种方式倒是不用转义了。
