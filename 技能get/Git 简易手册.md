# Git 简易手册 #


###  前言  ###

　　这里就不介绍 git 了，表一看到命令行就怕怕，常用的命令也就这些，基本满足 99% 以上的项目需求，非常实用。

## 一、常用 git 命令  ##

 

`git clone <url> [<directory>]`

	从远程库 clone 代码到本地，directory 用于指定一个新目录名

`git status`

	检查本地状态，可以查看当前所在分支、新增或被修改的文件

`git checkout <file>`

	还原某文件所作的修改，也可以 "git checkout ." 还原所有修改

`git add <file>`

	添加文件到缓存，并追踪新的文件。也可以 "git add ." 添加所有文件到缓存

`git commit -m "commit message"`

	提交代码到本地代码库，非常推荐做完一个小功能就 commit 一次，多次 commit 替代一次大 commit ，也便于后续代码合并和 review 。

`git push`

	同步本地库代码到远程库。（push 失败后面会专门提到代码合并）

`git branch`

	列出可用分支，git branch -a 列出本地和远程库所有分支

`git checkout <branch>`

	切换分支

`git log [-<number>]`

	查看本地 commit 记录，number 可用限制 log 显示条数

`git diff [<file>]`

	比较当前文件和暂存区文件差异

`git pull`

	从远程库提取更新代码到本地

 

## 二、代码撤销 ##

撤销本地未 add、未 commit 的代码

	　`　git checkout <file>` 撤销单个文件，或者 "git checkout ." 撤销本地所有修改。

撤销本地已经add、但未commit的代码

	`git reset <file>` 撤销单个文件，git reset 撤销所有的

撤销本地已经commit、但未push的代码

	`git reset --hard HEAD~1 `撤销最后一次 commit 并回退代码到上次 commit 的代码，注意代码就都丢弃了。（不加 --hard 可以只撤销 commit，不回退代码）

撤销已经 commit 并且已经 push 的代码

	`git revert <commit id>`

 修改已经commit的注释内容

	git commit --amend，一般会用 vim 打开，修改完 :wq 退出即可。

 

## 三、同分支代码合并 ##

适用场景：git pull 失败或者已知远程库有更新的情况下，注意本地先 commit，然后再 rebase 操作。

 `git pull --rebase`

	从远程库提取更新代码到本地，并尝试合并代码。与 merge 不同，不会产生新的 commit 记录，合并代码的时候需要非常小心，不要把别的小伙伴的代码弄丢了！

 `git add <file>`

	遇到冲突解决冲突，解决完成以后git add

 `git rebase --continue`

	所有冲突都解决完并 add 完以后继续 rebase，不要 commit，可能需要多次 continue 和 add 操作，直至全部合并完。

 `git push`

	将本地代码库代码同步到远程库。

 

## 四、不同分支代码合并 ##

适用场景：把代码从 develop 分支（开发）合并到 master 分支（生产）

 `git cherry-pick <commit id>`

	把另一个分支的 commit 修改应用到当前分支。commit id支持跨分支、短 commit id（前 8 位，例如 247d27c6），当然也支持用于同一分支。

 `git add <file>`

	遇到冲突解决冲突，解决完成以后git add

 `git rebase --continue`

	所有冲突都解决完并 add 完以后继续 rebase，不要 commit ，可能需要多次 continue 和 add 操作，直至全部合并完。

 git push

	将本地代码库代码同步到远程库。

 

## 五、代码暂存 ##

适用场景：功能未完成又不想 commit，但是又急需还原代码到上次 commit（例如紧急bug修复）

git stash

	暂存未 commit 的代码并还原所有修改

git stash pop

	还原最后一次 stash 的代码

 

## 六、其他 ##

Android

	Android Studio 自带的版本管理非常好用，非常方便切换分支、对比修改、合并提交代码。

iOS

	可以用 XCode 自带的版本管理检测对比，commit 到本地，然后通过命令行来合并。

	也有不少同学用 SourceTree 

Git 官方中文版

[https://git-scm.com/book/zh](官方)



#

原文链接：[http://www.cnblogs.com/over140/p/4548410.html](http://www.cnblogs.com/over140/p/4548410.html)