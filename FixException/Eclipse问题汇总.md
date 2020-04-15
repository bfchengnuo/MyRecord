# Eclipse问题汇总

虽然现在 Eclipse 基本就是吃灰状态，不过偶尔也会缅怀一下，哈哈

Mac 运行 Eclipse 提示：

> Failed to create the Java Virtual Machine

这个的原因是我本地有多个 JDK，Eclipse 启动的时候不知道用那个。

解决方案：

进入 Eclipse 的安装目录，找到这个文件：`Eclipse Installer.app/Contents/Eclipse/eclipse-inst.ini`

然后手动指定 JVM：

``` 
-vm
/Users/bparks/jdk/jdk1.8.0_162_x64/bin/java
```

上面的路径需要你自己找下。

PS：上面的配置必须放在 `-vmargs` 之前才有效。

---

