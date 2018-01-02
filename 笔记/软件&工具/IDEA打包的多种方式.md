# IDEA打包的多种方式

打包算是必备的一个技能吧，只会写程序怎么行，打包又分为打 jar 包和打 war 包，这里使用的工具是 IDEA，真是用了 IDEA 后再也回不去了

## 打Jar

常用的几种方式：

1. 用IDEA自带的打包形式
2. 用Maven插件`maven-shade-plugin`打包
3. 用Maven插件`maven-assembly-plugin`打包

### 用IDEA自带的打包方式

打开IDEA的`file -> Project Structure`，进入项目配置页面。

点击`Artifacts`，进入`Artifacts`配置页面，点击 + ，选择如下图的选项。

![img](http://ww1.sinaimg.cn/mw690/876975d1gy1fd44sbclg4j20r203q751)

进入`Create JAR from Modules`页面，按照如下图配置。

![img](http://ww1.sinaimg.cn/mw690/876975d1gy1fd44u9q66lj20ry0h2q5h)

1. 第一步选择Main函数执行的类。
2. 第二步选择如图的选项，目的是对第三方Jar包打包时做额外的配置，如果不做额外的配置可不选这个选项（但不保证打包成功），如果选择第一个项目所依赖的 jar 包及代码会被打成一个 jar，第二个可以做自由配置。
3. 第三步需要在`src/main`目录下，新建一个`resources`目录，将`MANIFEST.MF`文件保存在这里面，因为如果用默认缺省值的话，据说在IDEA12版本下会有bug。

点击`OK`之后，出现如下图界面，右键点击`<output root>`，点击`Create Directory`,创建一个`libs`，将所有的第三方JAR放进libs目录下。

放入之后，点击我们要打成的jar的名字，这里面是`kafka-cps.jar`,选择classpath进行配置，前面加上你创建的 `libs/`

同时还注意在配置页面，勾选`build on make` (make 项目的时候会自动输出jar)

回到IDEA,点击`Build->Build Artifacts`，选择`build` 即可

### 用maven-shade-plugin打包

**Tips：测试可以直接使用 IDEA 自带的 Maven 面板里的 install 打包**

上面的打包过程实在是过于的繁琐，而且也没有利用到maven管理项目的特色。为此，我们这里利用maven中的`maven-shade-plugin`插件。在`pom.xml`中，我们加入如下的信息来加入插件。

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>1.4</version>
  <configuration>
    <createDependencyReducedPom>true</createDependencyReducedPom>
  </configuration>
  <executions>
    <execution>
      <phase>package</phase>
      <goals>
        <goal>shade</goal>
      </goals>
      <configuration>
        <transformers>
          <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer" >
            <mainClass>Main.Main</mainClass>
          </transformer>
        </transformers>
      </configuration>
    </execution>
  </executions>
</plugin>
```

这里面配置了一个 configuration 标签内容，在此标签下面 有一个`transformer`标签，用来配置Main函数的入口( `<mainClass>Main.Main</mainClass>`)，当然此标签内容很复杂，不是上面写的那么简单，上面之所以如此简单，是因为在所有类中(包括第三方Jar)只有一个Main方法。

如果第三方jar中有Main方法，就要进行额外的配置，上面这么配置，不一定能执行成功。

然后就可以使用 IDEA 右边的侧边栏或使用命令进行打包了

PS：我感觉不配置插件也可以打包，待尝试

### 用maven-assembly-plugin打包

上面的方法，我们还需要点击很多命令去打包。这次利用一个新的插件，可以打包更简单。同样，在pom.xml中加入如下代码。上文的`maven-shade-plugin`插件代码可以删除。最好不要写2个插件代码。

```xml
<plugin>
  <artifactId>maven-assembly-plugin</artifactId>
  <version>2.4</version>
  <configuration>
    <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
    </descriptorRefs>
    <archive>
      <manifest>
        <mainClass>Main.Main</mainClass>
      </manifest>
    </archive>
  </configuration>
  <executions>
    <execution>
      <id>make-assembly</id>
      <phase>package</phase>
      <goals>
        <goal>single</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

这里同样配置了一个manifest标签来配置Main函数的入口。然后通过如下指令来实现打包。

```sh
mvn assembly:assembly
```

如果使用IDEA的话，可以通过自带的maven管理工具代替执行上面的命令。如下图所示，点击蓝色的部分。

![img](http://images2015.cnblogs.com/blog/859903/201611/859903-20161106231602674-150734166.jpg)

然后通过执行`java -jar cps-1.0-SNAPSHOT-jar-with-dependencies.jar`运行。

## 打War包

最简单的方式还是用 IDEA 自带的 Maven 面板工具，单击右侧 **maven project** 选项，选择 其中的 **Lifecycle** 菜单项展开，执行里面的 package 命令即可

---

使用自带的打包工具，File->Project Structure->Artifacts

选择 Type：(Web Application:Archive)，设置好 Output directory 以及 Output Layout 就可以了

exploded 的意思可以理解为 war 包解压后的文件（自动解压 war），在配置 Tomcat 调试的时候使用它就免去了解压 war 包的步骤，一般开发都配这个

## 参考

https://www.cnblogs.com/qifengshi/p/6036870.html