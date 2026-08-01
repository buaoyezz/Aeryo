@echo off
echo 清理历史残留锁文件与后台进程...
taskkill /F /IM qemu-system-x86_64.exe >nul 2>&1
taskkill /F /IM emulator.exe >nul 2>&1
timeout /t 1 /nobreak >nul

del /f /q /s "%TEMP%\avd\running\*" >nul 2>&1
rmdir /s /q "%TEMP%\avd\running" >nul 2>&1
del /f /q "%USERPROFILE%\.android\avd\Aeryo_Test.avd\*.lock" >nul 2>&1

set ANDROID_HOME=C:\android_sdk
set PATH=%ANDROID_HOME%\emulator;%PATH%

echo 正在启动全新的 Aeryo 浏览器 Android 虚拟机...
echo 注意：正在捕获详细运行日志...
"C:\android_sdk\emulator\emulator.exe" -avd Aeryo_Test -gpu swiftshader_indirect -no-snapshot-load -verbose > "e:\EZPUA\emulator_user_crash.log" 2>&1

echo.
echo ===================================================
echo 虚拟机已意外退出！
echo 详细错误日志已保存至：e:\EZPUA\emulator_user_crash.log
echo 请不要关闭此窗口，并在聊天窗口中告诉我，我将分析该日志。
echo ===================================================
pause
