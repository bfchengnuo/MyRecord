# Git技巧

<br>

### **查看历史提交日志**


>git log --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit --

这样是可以查看毕竟漂亮的日志，但是太长了。。我们可以

>git config --global alias.lg "log --color --graph --pretty=format:'%Cred%h%Creset -%C(yellow)%d%Creset %s %Cgreen(%cr) %C(bold blue)<%an>%Creset' --abbrev-commit --"

然后，我们就可以使用这样的短命令了：

>git lg