@ECHO OFF
PUSHD %~DP0
TITLE Sublime Text (添加/删除右键菜单)
echo.&echo 1.添加 Sublime Text 右键菜单
echo.&echo 2.删除 Sublime Text 右键菜单
echo.&echo.
set /p a= 输入数字回车：
if "%a%"=="1" Goto Add
if "%a%"=="2" Goto Remove
:Add
reg add "HKEY_CLASSES_ROOT\*\shell\SublimeText3" /ve /t REG_SZ /d "用 &SublimeText 打开" /f
reg add "HKEY_CLASSES_ROOT\*\shell\SublimeText3\command" /ve /t REG_SZ /d "\"%~dp0sublime_text.exe\" ""%%1""" /f
pause
exit
:Remove
reg delete "HKEY_CLASSES_ROOT\*\shell\SublimeText3" /f
pause
exit