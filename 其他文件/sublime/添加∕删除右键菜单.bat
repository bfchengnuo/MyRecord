@ECHO OFF
PUSHD %~DP0
TITLE Sublime Text (���/ɾ���Ҽ��˵�)
echo.&echo 1.��� Sublime Text �Ҽ��˵�
echo.&echo 2.ɾ�� Sublime Text �Ҽ��˵�
echo.&echo.
set /p a= �������ֻس���
if "%a%"=="1" Goto Add
if "%a%"=="2" Goto Remove
:Add
reg add "HKEY_CLASSES_ROOT\*\shell\SublimeText3" /ve /t REG_SZ /d "�� &SublimeText ��" /f
reg add "HKEY_CLASSES_ROOT\*\shell\SublimeText3\command" /ve /t REG_SZ /d "\"%~dp0sublime_text.exe\" ""%%1""" /f
pause
exit
:Remove
reg delete "HKEY_CLASSES_ROOT\*\shell\SublimeText3" /f
pause
exit