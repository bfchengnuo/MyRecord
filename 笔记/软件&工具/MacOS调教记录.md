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
  腾讯出品的一个免费截图工具，大部分情况下自带的就够用了，遇到滚动截取长图就只能靠第三方了，地址：https://snip.qq.com/
  PS：想念 FSCapture，需要注意下，开启滚动截图需要在隐私设置里给它权限。
- QQ（没有 TIM）
- WeChat
- 网易云音乐
- Typora
- Chrome、Firefox
- SS-NG-R
- brew
- FishShell
- Parallels Desktop
- Office
- Keka
  类似 7-z 压缩解压工具，官网：https://www.keka.io/zh-cn/
- iMazing
- ScrollReverser
- Paragon NTFS

### 推荐软件

我偶尔会用到的一些，在某些场景还是非常有存在感的。

- OBS
- TV
- Anki
- 迅雷、Folx 5
- 有道云笔记
- Kap
  Gif 录制工具，地址：https://getkap.co/
- ScreenFlow
  高级屏幕录制，一般来说自带的 Quicktime 就可以实现简单的屏幕录制需求了。
- Wine
- iText
- VBOX
- Telegram
- Steam
- Bartender 3
- iterm2
- Alfred 3
- KeyCue
- IINA
- HandBrake（小巧格式转换）

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
- NavicatPremium
- Sublime、VSC
- Git、SVN
- 远程桌面
- WinSCP
- termius 或者 FinalShell
  Xshell 替代品，或者 iterm2
- Charles

### 其他

- iStatistica、iStat Menus
- Dash

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

效果如果不好，可以再试试中科大的源：

> https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/brew.git
> https://mirrors.tuna.tsinghua.edu.cn/git/homebrew/homebrew-core.git

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
#151.101.76.249 global-ssl.fastly.Net
```

第一行对应的 ip 是你 ping GitHub 后解析出的地址，一般这个 ip 你ping 不通。

第二行对应的 IP 你可以去 [这个](http://github.global.ssl.fastly.net.ipaddress.com/) 网站测试得出。

具体哪一个快，你可以去某些在线解析域名的看看。

## 启动项

除了系统偏好设置里的，在下面几个路径中的也会随系统启动：

```
~/Library/LaunchAgents
/Library/LaunchAgents
/Library/LaunchDaemons
/System/Library/LaunchAgents
/System/Library/LaunchDaemons
```

