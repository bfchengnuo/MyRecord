# MacOS调教记录

之前随便写了写，现在来重新组织一下，快捷键不要自定义，键盘和触摸板（一定要开三指拖移，尽量使用按压的方式触发单击）要慢慢适应，最终形成肌肉记忆就好了，和 Windows 之前无缝切换。

## Mac键盘符号

| 图标 | 介绍               |
| ---- | ------------------ |
| ⌘    | Command            |
| ⇧    | Shift              |
| ⇪    | Caps Lock          |
| ⌥    | Option = Alt       |
| ⌃    | Control            |
| ↩    | Enter              |
| ⌫    | Delete             |
| ⌦    | Fn + Delete        |
| ⇞    | Fn + ↑ = Page Up   |
| ⇟    | Fn + ↓ = Page Down |
| Home | Fn + ←             |
| End  | Fn + →             |

## 个性化配置

系统偏好设置看着自己喜欢来就行了。。。

说实话，也没怎么配置，基本默认的就够了，除了加一个触发角，屏幕保护我习惯设置立即需要密码了。

修改计算机名是这共享里面；打开非官方商店的应用可以试试按住 cmd，否则可能提示损坏。

## 小技巧

对于 TauchBar，快速调节亮度和音量，可以直接在对应的按钮上快速左右滑动，每次会增加或者减少一格，当然按住不动也可以。

在访达中，可以使用快捷键 `cmd + shift + .` 快速查看隐藏文件或者目录。

## 软件应用

首先列一下准备安装的软件，非常简单的傻瓜式安装的软件就不多介绍

### 必备软件

无需太多解释，终究是逃不过的。。。排名不分先后，反正都得装。

某些收费软件有钱当然正版的好，没钱只能去 Xclient 这种网站看看了。

- ~~Snip~~ 、Xnip
  
  Snip 是腾讯出品的一个免费截图工具，不过我现在更推荐 Xnip 了，商店里可直接搜。
  
  或者试试开源的 Snipaste？
  
  PS：想念 FSCapture，需要注意下，开启滚动截图需要在隐私设置里给它权限。
  
- QQ（没有 TIM）

- WeChat

- 网易云音乐

- CotEditor

- Typora

  最喜欢的 MD 写作软件

- Chrome、Firefox

- SS-NG-R、V2ray

- brew

  这个难道有不装的么？神器！

- Parallels Desktop

- Office

- Keka
  
  类似 7-z 压缩解压工具，官网：https://www.keka.io/zh-cn/
  
  国人写的免费的 ezip 也不错
  
- iMazing

- Mos（推荐）/ScrollReverser

  从此鼠标非常顺滑

- Paragon NTFS/NTFS For Mac

- Karabiner - 改键神器

- iStat Menus

- tree、lsd、 Annie、autojump、frp、qrgo、ncmdump、exiftool、lrzsz、tmux

  一些命令行实用工具，可以在 Github 上搜到

### 推荐软件

我偶尔会用到的一些，在某些场景还是非常有存在感的。

- OBS

- TV

- Anki

- Magnet

- 迅雷、Motrix、~~Folx 5、Downie3~~

- 有道云笔记

- ~~Kap~~、Capture
  
  Gif 录制工具，感觉 Capture 更好用一点
  
- ScreenFlow
  
  高级屏幕录制，一般来说自带的 Quicktime 就可以实现简单的屏幕录制需求了。
  
- Wine（Windows 环境模拟）

- Captuocr

  OCR 识别，配套的有 alfredworkflow 版，见 v2ex

- VBOX

- Telegram

- Steam

- ~~Bartender 3~~

- iterm2

- Alfred 3

- IINA（视频播放器）

- XLD（音乐格式必备，brew 记得用 cask）

- HandBrake（小巧格式转换、压缩）

- Proxifier

- Paintbrush（代替 win 的画图）

- BetterTouchTool（Touch Bar 增强，要配合规则）

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

- Dash
- ~~PopClip~~
- Todoist
- ~~OhMyStar~~
- Permute（格式转换）
- Airserver
- BestTrace（追踪解析，玩具）
- KeyCue（快捷键查询）
- bear（笔记软件）
- dupeGuru（文件查重）
- AppCleaner（App卸载残留检测）
- HyperSwitch（窗口切换显示预览，我没用）

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

## 开发相关

这里列举我目前用到的一些软件，例如 Nginx、Redis、Docker

### Redis

安装参考官网，下载后 mark 一下，不用担心，挺快的，然后执行  `./redis-server` 即可，暂时也不需要配置，用默认的就够了。

### Nginx

反代还是很有必要的，这里我直接使用 brew 安装的，安装完成后会提示你一些关键信息：

``` 
Docroot is: /usr/local/var/www

The default port has been set in /usr/local/etc/nginx/nginx.conf to 8080 so that
nginx can run without sudo.

nginx will load all files in /usr/local/etc/nginx/servers/.

To have launchd start nginx now and restart at login:
  brew services start nginx
Or, if you don't want/need a background service you can just run:
  nginx
==> Summary
🍺  /usr/local/Cellar/nginx/1.17.2: 25 files, 2MB
==> Caveats
==> nginx
Docroot is: /usr/local/var/www

The default port has been set in /usr/local/etc/nginx/nginx.conf to 8080 so that
nginx can run without sudo.

nginx will load all files in /usr/local/etc/nginx/servers/.

To have launchd start nginx now and restart at login:
  brew services start nginx
Or, if you don't want/need a background service you can just run:
  nginx

```

我的话就习惯将配置文件改一下，换成 80，然后配置文件 include 一个方便的文件夹位置，便于以后添加配置。

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

## Tmux速查

启动新会话：

```shell
tmux [new -s 会话名 -n 窗口名]
```

恢复会话：

```shell
tmux at [-t 会话名]
```

列出所有会话：

```shell
tmux ls
```

关闭会话：

```shell
tmux kill-session -t 会话名
```

关闭所有会话：

```shell
tmux ls | grep : | cut -d. -f1 | awk '{print substr($1, 0, length($1)-1)}' | xargs kill
```

### 快捷键

Tmux 中快捷键操作的前缀是 `ctrl+b`

#### 会话

```
:new<回车>  启动新会话
s           列出所有会话
$           重命名当前会话
d						分离会话
```

#### 窗口 (标签页)

```
c  创建新窗口
w  列出所有窗口
n  后一个窗口
p  前一个窗口
f  查找窗口
,  重命名当前窗口
&  关闭当前窗口
```

#### 调整窗口排序

```
swap-window -s 3 -t 1  交换 3 号和 1 号窗口
swap-window -t 1       交换当前和 1 号窗口
move-window -t 1       移动当前窗口到 1 号
```

#### 窗格

```
%  垂直分割
"  水平分割
o  交换窗格
x  关闭窗格
⍽  左边这个符号代表空格键 - 切换布局
q 显示每个窗格是第几个，当数字出现的时候按数字几就选中第几个窗格
{ 与上一个窗格交换位置
} 与下一个窗格交换位置
z 切换窗格最大化/最小化

Ctrl+o：当前窗格上移。
Alt+o：当前窗格下移。
!：将当前窗格拆分为一个独立窗口。
Ctrl+<arrow key>：按箭头方向调整窗格大小。
<arrow key>：光标切换到其他窗格。<arrow key>是指向要切换到的窗格的方向键，比如切换到下方窗格，就按方向键↓。
```

### 配置选项：

```
# 鼠标支持 - 设置为 on 来启用鼠标(与 2.1 之前的版本有区别，请自行查阅 man page)
* set -g mouse on

# 设置默认终端模式为 256color
set -g default-terminal "screen-256color"

# 启用活动警告
setw -g monitor-activity on
set -g visual-activity on

# 居中窗口列表
set -g status-justify centre

# 最大化/恢复窗格
unbind Up bind Up new-window -d -n tmp \; swap-pane -s tmp.1 \; select-window -t tmp
unbind Down
bind Down last-window \; swap-pane -s tmp.1 \; kill-window -t tmp
```

### 参考配置文件（~/.tmux.conf）：

下面这份配置是我使用 Tmux 几年来逐渐精简后的配置，请自取。

```
# -----------------------------------------------------------------------------
# Tmux 基本配置 - 要求 Tmux >= 2.3
# 如果不想使用插件，只需要将此节的内容写入 ~/.tmux.conf 即可
# -----------------------------------------------------------------------------

# C-b 和 VIM 冲突，修改 Prefix 组合键为 Control-Z，按键距离近
set -g prefix C-z

set -g base-index         1     # 窗口编号从 1 开始计数
set -g display-panes-time 10000 # PREFIX-Q 显示编号的驻留时长，单位 ms
set -g mouse              on    # 开启鼠标
set -g pane-base-index    1     # 窗格编号从 1 开始计数
set -g renumber-windows   on    # 关掉某个窗口后，编号重排

setw -g allow-rename      off   # 禁止活动进程修改窗口名
setw -g automatic-rename  off   # 禁止自动命名新窗口
setw -g mode-keys         vi    # 进入复制模式的时候使用 vi 键位（默认是 EMACS）

# -----------------------------------------------------------------------------
# 使用插件 - via tpm
#   1. 执行 git clone https://github.com/tmux-plugins/tpm ~/.tmux/plugins/tpm
#   2. 执行 bash ~/.tmux/plugins/tpm/bin/install_plugins
# -----------------------------------------------------------------------------

setenv -g TMUX_PLUGIN_MANAGER_PATH '~/.tmux/plugins'

# 推荐的插件（请去每个插件的仓库下读一读使用教程）
set -g @plugin 'seebi/tmux-colors-solarized'
set -g @plugin 'tmux-plugins/tmux-pain-control'
set -g @plugin 'tmux-plugins/tmux-prefix-highlight'
set -g @plugin 'tmux-plugins/tmux-resurrect'
set -g @plugin 'tmux-plugins/tmux-sensible'
set -g @plugin 'tmux-plugins/tmux-yank'
set -g @plugin 'tmux-plugins/tpm'

# tmux-resurrect
set -g @resurrect-dir '~/.tmux/resurrect'

# tmux-prefix-highlight
set -g status-right '#{prefix_highlight} #H | %a %Y-%m-%d %H:%M'
set -g @prefix_highlight_show_copy_mode 'on'
set -g @prefix_highlight_copy_mode_attr 'fg=white,bg=blue'

# 初始化 TPM 插件管理器 (放在配置文件的最后)
run '~/.tmux/plugins/tpm/tpm'

# -----------------------------------------------------------------------------
# 结束
# -----------------------------------------------------------------------------
```

参考：https://gist.github.com/ryerh/14b7c24dfd623ef8edc7

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
        Proxycommand    ncat --proxy 127.0.0.1:1081 --proxy-type socks5 %h %p

# 如果是给某同性社交网站用的（走 ssh 协议），可以直接使用该配置。
# 其它类似网站的话，替换掉域名（ Host/Hostname）即可。
# 可以看出，ssh 协议的 git 客户端，配置与 ssh 一模一样。
# 需要注意的是这里的 User 应该是 git，而不是你在该网站上注册的用户名。
# （虽然有些提供 git 仓库托管的网站会用其它用户名，这种情况根据网站配置。）
Host github.com
  User    git
  Hostname github.com
  ProxyCommand    ncat --proxy 127.0.0.1:1080 --proxy-type socks5 %h %p
  # Proxycommand    nc -X 5 -x 127.0.0.1:1080 %h %p
```

该方式的配置中，如果 Host 设置为 `*`，那么 `Host *` 对应的配置会被应用到所有没有独立配置 的 ssh 连接中，包括使用了 ssh 协议的 git 操作。

> 尴尬的是，需要使用 netcat 这个软件，这软件有两个分支，GNU 和 OPEN BSD 版本，然而我的 Mac 只能通过 brew 安装 GNU 版的，查了一个小时资料，真的用不了，哎。
>
> 直到，我翻到了[这个帖子](https://apple.stackovernet.com/cn/q/5470)，知道了 ncat 其实是 Nmap，可以理解为是 netcat 的升级版，果断测试使用 brew 安装，完美！（怪我没看原文的前置条件，自己傻了）

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