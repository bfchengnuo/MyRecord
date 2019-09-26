# MacOS调教记录

最近终于下了决心，斥资买了 MBP，怀着激动的心情来把玩一下向往已久的 MacOS。
不知道到熟练使用的程度需要多久，甚至我还买了一本书 o(￣▽￣*)ゞ))￣▽￣*)o，windows 感觉已经厌倦了，应该也可以说是熟练各种骚操作，接下来就是 MacOS，主要应该就是快捷键需要适应，能培养出肌肉记忆就更好了。

## Mac键盘符号

| 图标 | 介绍                   |
| ---- | ---------------------- |
| ⌘    | Command                |
| ⇧    | Shift                  |
| ⇪    | Caps Lock              |
| ⌥    | Option = Alt           |
| ⌃    | Control                |
| ↩    | Enter                  |
| ⌫    | Delete                 |
| ⌦    | Fn + Delete            |
| ↑    | 上箭头                 |
| ↓    | 下箭头                 |
| ←    | 左箭头                 |
| →    | 右箭头                 |
| ⇞    | Fn + ↑ = Page Up       |
| ⇟    | Fn + ↓ = Page Down     |
| Home | Fn + ←                 |
| End  | Fn + →                 |
| ⇥    | Tab = 右制表符         |
| ⇤    | Shift + Tab = 左制表符 |
| ⎋    | Esc = Escape           |
| ⏏    | 电源开关键             |

## 个性化配置

记录一下我自己的系统偏好设置，因为是刚开始用，后期没准会再修改，最终趋于稳定，会同时更新这里的记录（如果记得的话）
说实话，也没怎么配置，基本默认的就够了，除了加一个触发角，屏幕保护我习惯设置立即需要密码了。
修改计算机名是这共享里面，打开非官方商店的应用可以试试按住 cmd，否则可能提示损坏。

## 软件预备

首先列一下准备安装的软件，非常简单的傻瓜式安装的软件就不多介绍，有些复杂点的会展开来记录一下，个性化设置相关了。

### 必备软件

无需太多解释，终究是逃不过的。。。
排名不分先后，反正都得装。

- Snip
  腾讯出品的一个免费截图工具，不过我现在更推荐 Xnip 了，商店里可直接搜。
  PS：想念 FSCapture，需要注意下，开启滚动截图需要在隐私设置里给它权限。
- QQ（没有 TIM）
- WeChat
- 网易云音乐
- Typora
- Chrome、Firefox
- SS-NG-R
- brew
- FishShell/tree/lsd
- Parallels Desktop
- Office
- Keka
  类似 7-z 压缩解压工具，官网：https://www.keka.io/zh-cn/
  国人写的免费的 ezip 也不错
- iMazing
- Mos（推荐）/ScrollReverser
- Paragon NTFS/NTFS For Mac
- Karabiner - 改键神器

### 推荐软件

我偶尔会用到的一些，在某些场景还是非常有存在感的。

- OBS
- TV
- Anki
- 迅雷、Folx 5、Downie3
- 有道云笔记
- Kap
  Gif 录制工具，地址：https://getkap.co/
  或者考虑下 Capture
- ScreenFlow
  高级屏幕录制，一般来说自带的 Quicktime 就可以实现简单的屏幕录制需求了。
- Wine
- iText
- VBOX
- Telegram
- Steam
- ~~Bartender 3~~
- iterm2
- Alfred 3
- KeyCue
- IINA
- HandBrake（小巧格式转换）
- dupeGuru（文件查重）
- AppCleaner（App卸载残留检测）
- HyperSwitch
- bear（笔记）
- Proxifier
- Paintbrush（代替 win 的画图）

### 开发工具

由于主力 Java，必要的 JB 全家桶不能少，前端也是必修，相关软件一个都不能少，偶尔还要搞搞 py 之类的。

- Jetbrains 全家桶
- Python
- Node
- Mysql
  这里为选择的是使用 brew 来进行安装：
  运行 `brew service start mysql`  可以后台启动 mysql
  运行 `mysql.server start` 前台启动 mysql
  运行 `mysql_secure_installation`  可以设置密码
- Docker
- Eclipse
- NavicatPremium/Sequelpro
- Sublime、VSC
- Git、SVN
- 远程桌面连接
- WinSCP/Commander One
- termius 或者 FinalShell
  Xshell 替代品，或者 iterm2
- Charles

### 其他

- iStatistica、iStat Menus
- Dash
- PopClip
- Todoist
- OhMyStar
- Permute
- Downie3
- Airserver
- BestTrace

## Fish配置

最终为选择了轻量一些的 fish 而不是 zsh，默认的 bash 其实也够用了。

与 bash 不同，fish 的配置文件主要是 `~/.config/fish/config.fish` ，bash 就上传统的 `~/.bashrc`

在设置环境变量上也不太一样，例如：

``` shell
## bash
export PATH="$PATH:/usr/matlab/bin:/home/aborn/aborn/scripts"

## fish
alias python="/usr/local/Cellar/python/3.7.4/bin/python3"
set -x PATH /usr/matlab/bin /home/aborn/aborn/scripts

## 如果你想在命令行用 sublime 之类打开指定文件（默认可以使用 open）
alias sublime='open -a /Applications/Sublime\ Text.app'
```

最后不要忘记执行 `source ~/.config/fish/config.fish` 让修改生效。

输入 `fish_config` 会自动打开一个网站，可以进行个性化配置。

PS：另外，如果有需要切换到 bash 来执行某些脚本或者命令的，可以直接输入 bash。

### 环境变量

说一下环境变量的设置，与 bash 不太一样，它不能使用 export 语法，用的是 set：

```shell
# 当前 shell 生效
set -x VISUAL vim

# 全局生效
set -Ux VISUAL vim

# 设置变量
set name val
echo $name

# 设置 path
set PATH /new/path $PATH
```

fish 里面的 path 是一个路径数组（fish 独有的数组类型），而不是 : 分隔的路径字符串，你可以在 `fish.config` 里面设置；

推荐使用 `set -Ux` 保存常用环境变量， (而不是写到 `config.fish` 文件) 这样你的 app 就算不是从 shell 启动，也会获得这些变量。

## brew优化

Brew 肯定是必装的吧，不过呢，因为仓库是 Github 所以速度啊.......我们可以换用阿里的源来优化一下速度：

平时我们执行 brew 命令安装软件的时候，跟以下 3 个仓库地址有关：

1. brew.git
2. homebrew-core.git
3. homebrew-bottles

通过以下操作将这 3 个仓库地址全部替换为 Alibaba 提供的地址。

``` shell
# 替换 brew.git 成阿里巴巴的 brew.git 仓库地址:
cd "$(brew --repo)"
git remote set-url origin https://mirrors.aliyun.com/homebrew/brew.git

#=======================================================

# 还原为官方提供的 brew.git 仓库地址
cd "$(brew --repo)"
git remote set-url origin https://github.com/Homebrew/brew.git




# 替换成阿里巴巴的 homebrew-core.git 仓库地址:
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://mirrors.aliyun.com/homebrew/homebrew-core.git

#=======================================================

# 还原为官方提供的 homebrew-core.git 仓库地址
cd "$(brew --repo)/Library/Taps/homebrew/homebrew-core"
git remote set-url origin https://github.com/Homebrew/homebrew-core.git




# 替换 homebrew-bottles 访问 URL，以 bash 为例
echo 'export HOMEBREW_BOTTLE_DOMAIN=https://mirrors.aliyun.com/homebrew/homebrew-bottles' >> ~/.bash_profile
source ~/.bash_profile
```

另外你可以选择禁用掉每次安装前更新（其实 ctrl+c 结束会跳过更新执行下面的）：

`export HOMEBREW_NO_AUTO_UPDATE=true`

效果如果不好，可以再试试中科大的源或者清华大学的源：

> https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git
> https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-core.git

如果你有代理，你可以试试挂个代理：

``` shell
# bash
export ALL_PROXY=socks5://127.0.0.1:1080

# fish
set -x ALL_PROXY socks5://127.0.0.1:1080
```

如果效果还不好，那。。。。。

## brew常用命令

brew 在 Mac 上的重要性就不必说了，下面来整理下经常用到的一些命令。

``` shell
# 安装软件
brew install [name]

# 卸载软件，别名还有 rm/remove
brew uninstall [name]

# 查看已安装软件列表，别名有 ls
brew list

# 搜索软件
brew search [name]

# 更新软件
brew upgrade [name]

# 查看软件信息
brew info [name]

# 打开 brew 主页
brew home

# 查看依赖关系/以树形菜单查看依赖关系
brew deps [name]
brew deps --installed --tree [name]

# 删除（单个软件）老版本
brew cleanup git 
brew cleanup

# 查看是否有软件需要更新
brew outdated
```

服务管理相关：

``` shell
# 查看使用 brew 安装的服务列表
brew services list

# 启动服务（仅启动不注册）
brew services run [formula|--all]

# 启动服务，并注册
brew services start [formula|--all]

# 停止/重启服务
brew services stop [formula|--all]
brew services restart [formula|--all]

# 清除已卸载应用的无用的配置
brew services cleanup

# 帮助
brew services --help
```

cask相关：

cask 可以认为是 Homebrew 的一个模块，`brew cask` 开头的命令一般管理的是带有 GUI 的软件；使用上也和 brew 基本一致：

``` shell
brew cask install software-name
brew cask list
brew cask outdated
brew cask upgrade
brew cask uninstall software-name
```

## iTerm2终端

常用命令一览：

```
1. 垂直分屏：command + d
2. 水平分屏：command + shift + d
3. 切换屏幕：command + option + 方向键 command + [ 或 command + ]
4. 查看历史命令：command + ;
5. 查看剪贴板历史：command + shift + h

7. 新建标签：command + t
8. 关闭标签：command + w
9. 切换标签：command + 数字 command + 左右方向键
10. 切换全屏：command + enter
11. 查找：command + f

12. ----------------分屏--------------------
13. 清除当前行：ctrl + u
14. 到行首：ctrl + a
15. 到行尾：ctrl + e
16. 前进后退：ctrl + f/b (相当于左右方向键)
17. 上一条命令：ctrl + p
18. 搜索命令历史：ctrl + r
19. 删除当前光标的字符：ctrl + d
20. 删除光标之前的字符：ctrl + h
21. 删除光标之前的单词：ctrl + w
22. 删除到文本末尾：ctrl + k
23. 交换光标处文本：ctrl + t
24. 清屏1：command + r
25. 清屏2：ctrl + l
26. ⌘ + f 所查找的内容会被自动复制
27. ⌘ + r = clear，而且只是换到新一屏，不会想 clear 一样创建一个空屏
28. ctrl + u 清空当前行，无论光标在什么位置
29. 输入开头命令后 按 ⌘ + ; 会自动列出输入过的命令
30. ⌘ + shift + h 会列出剪切板历史
```

## Git克隆速度慢

这个是通病，我虽然设置了 git 的代理，但是效果不好，也就 http 协议有效，而主流已经是 ssh 了，根本原因就是那个的问题，所以找一个好的 ip 很关键，我们使用 host 大法：

```
13.229.188.59 github.global.ssl.fastly.net
13.250.177.223 github.com
# 151.101.76.249 global-ssl.fastly.Net
```

对应的 IP 你可以去 [这个](http://github.global.ssl.fastly.net.ipaddress.com/) 网站测试得出。

具体哪一个快，你可以去某些在线解析域名的看看。

---

更新：

已知问题，ip 可能会失效，失效后会出现 ssl 错误导致直接无法访问。

> 原因：git clone 特别慢是因为 `github.global.ssl.fastly.net` 域名被限制了。
> 只要找到这个域名对应的 ip 地址，然后在 hosts 文件中加上 ip –> 域名的映射，刷新 DNS 缓存便可。

快捷使用命令查看：

``` shell
nslookup github.global.ssl.fastly.Net
nslookup github.com

sudo vim /etc/hosts

# 刷新 DNS 缓存
sudo killall -HUP mDNSResponder
sudo dscacheutil -flushcache

# Linux
sudo /etc/init.d/networking restart
```

这个还是上一种方法补充说明，个人不是很推荐。

---

其实我最想的是挂代理，毕竟我有 socket 代理，配置 http 协议的代理网上有很多，这里补充一下使用 ssh 协议时如何走代理：

在你用户目录下，建立 `.ssh/config`，在里面添加如下配置：

```
# 将这里的 User、Hostname、Port 替换成你需要用 ssh 登录的服务器的配置。
# Host 可以认为像是书签一样的东西，当你用 Host 指明的字符串代替你服务器的 IP/域名 时，
# 便会应用该节点下的配置。当然你也可以将 Host 和 Hostname 设置成一样。
Host yourserver.com
        User    someone
        Hostname        yourserver.com
        Port    22
        Proxycommand    /usr/bin/ncat --proxy 127.0.0.1:1081 --proxy-type socks5 %h %p

# 如果是给某同性社交网站用的（走 ssh 协议），可以直接使用该配置。
# 其它类似网站的话，替换掉域名（ Host/Hostname）即可。
# 可以看出，ssh 协议的 git 客户端，配置与 ssh 一模一样。
# 需要注意的是这里的 User 应该是 git，而不是你在该网站上注册的用户名。
# （虽然有些提供 git 仓库托管的网站会用其它用户名，这种情况根据网站配置。）
Host github.com
        User    git
        Hostname        github.com
        Port    22
        Proxycommand    /usr/bin/ncat --proxy 127.0.0.1:1080 --proxy-type socks5 %h %p
```

该方式的配置中，如果 Host 设置为 `*`，那么 `Host *` 对应的配置会被应用到所有没有独立配置 的 ssh 连接中，包括使用了 ssh 协议的 git 操作。

终极：为使用 git 协议的 git 配置代理

建立 `/opt/bin/socks5proxywrapper` 文件，并将该文件设置为可执行权限，文件内容如下：

```shell
#!/bin/sh
/usr/bin/ncat --proxy 127.0.0.1:1081 --proxy-type socks5 "$@"
```

配置 git，使其全局使用该代理：

``` shell
git config --global core.gitProxy '/opt/bin/socks5proxywrapper'

# 也可针对特定域名启用代理，如：
git config --global core.gitProxy '/opt/bin/socks5proxywrapper for git.kernel.org'

# 临时启用代理而不想将配置保存下来的话，可以使用设置环境变量的方法：
export GIT_PROXY_COMMAND=/opt/bin/socks5proxywrapper
```

参考：https://blog.systemctl.top/2017/2017-09-28_set-proxy-for-git-and-ssh-with-socks5/

## 设置快速预览扩展

空格快速预览非常的爽，但是有些文件不能查看，但是不要忘了，它是支持插件的！

``` shell
# 代码高亮
brew cask install qlcolorcode

# md 预览
brew cask install qlmarkdown

# 图片信息
brew cask install qlimagesize

# zip 压缩包（betterzip）
brew cask install betterzip

# 更全的视频预览
brew cask install qlvideo
```

更全的插件可以去 GitHub 看看：https://github.com/sindresorhus/quick-look-plugins

## 启动项

除了系统偏好设置里的，在下面几个路径中的也会随系统启动：

```
~/Library/LaunchAgents
/Library/LaunchAgents
/Library/LaunchDaemons
/System/Library/LaunchAgents
/System/Library/LaunchDaemons
```

## Tips

临时保持屏幕常亮：

` caffeinate -u -t 100`

单位是秒，cmd + c 结束后恢复默认。