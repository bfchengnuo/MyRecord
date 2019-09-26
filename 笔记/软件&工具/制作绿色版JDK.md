官方下载的 JDK 都是 exe 安装程序，当然是在 win 下，但根本没必要嘛，绿色版多好，自己配环境变量，还省的出各种奇葩问题。

重要的是可以多个版本的 JDK 并存！

## 制作步骤

1. 下载官方 exe 安装程序
2. 使用 7Z 提取文件到某个文件夹
3. 进入 `.rsrc\1033\JAVA_CAB10` 目录，命令行下执行 `extrac32 111` (在地址栏输入 cmd 可快捷在当前目录打开)

   然后即可获得 tools.zip，该压缩文件内部就是我们所需的 JDK，但是还需要转换下。
   
   JDK 10 中提取完就直接是 tools.zip 了，就不需要这一步的处理了。
4. 解压文件，然后进入，在这个目录下执行命令：`for /r %x in (*.pack) do .\bin\unpack200 -r "%x" "%~dx%~px%~nx.jar"`

然后即可获得绿色版的 JDK 了！

`java -version` 测试下吧！

go it！

> 原文：https://bgasparotto.com/convert-jdk-exe-zip/
