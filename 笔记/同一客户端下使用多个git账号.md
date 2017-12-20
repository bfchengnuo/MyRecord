# 同一客户端下使用多个git账号

在日常使用git作为仓库使用的时候，有时可能会遇到这样的一些情况：

> 1. 有两个github账号，一台电脑怎么同时连接这两个账号进行维护呢？
> 2. 自己用一个github账号，平时用来更新自己的一些资料；公司使用的gitlab（也是git的衍生产品）

总结来说，就是同一客户端（即同一台电脑）可能需要连接多个git衍生产品账号（以下简称git账号）。下面就记录一下我配置的方法，也是网上都可以搜到的。

首先来说，配置多个 git 账号分为两种情况：

1. 已经配置过git账号，想在添加一个账号。
2. 没有配置过任何git账号，直接就像配置两个账号


[TOC]

## 已配置过git账号

 一般开发用户应该都配置过一个git的账号，让我们先来回顾一下，当时我们是怎样来配置git账号的

### 回顾配置git账号的过程

当本地git仓库和github上的仓库进行通信的时候，必须得双方建立连接才行，而SSH keys 就是两者建立连接的一把钥匙，只要钥匙对了就完成了其中一步验证了。

其中我们可以在生成的SSH keys中看到，里面包含了我们的账号信息和邮箱信息（后面会用到，先提一下）。

还有一步验证就是用户名和邮箱的验证，这个貌似可有可无（具体我也没搞清楚，反正不设置的时候也没有问题），有些连接本地git仓库和github仓库的教程中会有下面这样的设置：

```shell
git config --golbal user.name 'bfchengnuo'

git config --golbal user.emil 'bfchengnuo@gmail.com'
```

这其实就是在设置全局范围的用户名和邮箱的设置，有时不设置这一步，两者之间进行连接会有问题。

### 设置多账号

简单回顾玩设置git账号的步骤和一些注意点，进入今天的主题，设置多账号。

#### 清除git的全局设置

如果你之前在设置本地仓库和github连接的时候设置过**user.name**和**user.email**,那么你必须首先清楚掉该设置，因为不清楚掉该设置，两个账号在提交资料的时候，验证肯定冲突（只能设置一个全局的**user.name**和**user.email**，而你现在有两个账号就对应两个不同的）。

```powershell
git config --global user.name "your_name"
git config --global user.email  "your_email"
```

就是重新设置覆盖了而已。。。

如果没有设置，可以直接跳转第二部。如果你忘了，最好还是覆盖一下。

PS：个人认为，保留一个全局的配置也未尝不可

#### 生成新站好的SSH keys

前面提到过生成的SSH keys里面包含了账号和邮箱信息，所以新账号必须另外在生成一份SSH keys，当然生成的方式和以前一样。

1. **用ssh-keygen命令生成一组新的 id_rsa_new和id_rsa_new.pub**

   `ssh-keygen -t rsa -C "new email"`

   平时我们都是直接回车，默认生成 id_rsa 和 id_rsa.pub。这里特别需要注意，出现提示输入文件名的时候(`Enter file in which to save the key (~/.ssh/id_rsa): id_rsa_new`)要输入与默认配置不一样的文件名

   比如：我这里填的是 id_rsa_new

   > 注：windows 用户和 mac 用户的区别就是，mac中`.ssh`文件夹在根目录下，所以表示成 
   >
   > `~/.ssh/`,而windwos用户是`C:\Users\Administrator\.ssh`。
   >

   下面同理，不在赘述。


2. **执行ssh-agent让ssh识别新的私钥**

   因为默认只读取id_rsa，为了让SSH识别新的私钥，需将其添加到SSH agent中：

   `ssh-add ~/.ssh/id_rsa_work`

   如果出现`Could not open a connection to your authentication agent`的错误，就试着用以下命令：

   `ssh-agent bash`

   `ssh-add ~/.ssh/id_rsa_work`

3. **配置~/.ssh/config文件**

   前面我们在 `~/.ssh` 目录下面，使用 `ssh-keygen -C “your_email” -t rsa` 生成公私秘钥，当有多个 github 账号的时候，可以生成多组 rsa 的公司密钥。然后配置 `~/.ssh/config` 文件（如果没有的话请重新创建一个）。

   然后修改如下：

   我的 config 配置如下：

   ``` shell
   # 该文件用于配置私钥对应的服务器
   # Default github user(chping_2125@163.com)
   Host git@github.com
    HostName https://github.com
    User git(loli@bfchengnuo.com)
    IdentityFile ~/.ssh/id_rsa

    # second user(second@mail.com)
    # 建一个github别名，新建的帐号使用这个别名做克隆和更新
   Host git@code.xxxxxxx.com
    HostName https://code.xxxxxxx.com    #公司的gitlab
    User git
    IdentityFile ~/.ssh/id_rsa_new
   ```

   如果存在 config 文件的话，其实就是往这个config中添加一个Host：

   其规则就是：从上至下读取 **config** 的内容，在每个Host下寻找对应的私钥。你可以设置 Host 别名：

   ``` shell
   # 该文件用于配置私钥对应的服务器
   # Default github user(chping_2125@163.com)
   Host git1    #############不同在这里
    HostName https://github.com
    User git
    IdentityFile ~/.ssh/id_rsa

    # second user(second@mail.com)
    # 建一个github别名，新建的帐号使用这个别名做克隆和更新
   Host git2  #############不同在这里
    HostName https://code.xxxxxxx.com    #公司的gitlab
    User git
    IdentityFile ~/.ssh/id_rsa_new
   ```

   那么你原本想在新账号克隆的命令是

   `git clone git@xxxxxx.com:chping/test.git`

   就要相应的变成

   `git clone git2:chping/test.git`

4. **添加新的SSH keys到新账号的SSH设置中**

   可不要忘了将新生成的SSH keys添加到你的另一个github帐号(或者公司的gitlab)下的SSH Key中。

**测试一下**

```shell
$ ssh -T git@github.com
Hi BeginMan! You've successfully authenticated, but GitHub does not provide shell access.

# 上面是github的成功返回语句，下面是gitlab的成功返回语句。

$ ssh -T git@xxxxxx.com
Welcome to GitLab, chping!
```

## 一次性配置两个账号

其实同理上面，先配置一个，在配置两一个。

## 参考

http://chping.website/2016/12/17/gitMore/