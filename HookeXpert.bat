@echo off
set JLINK_VM_OPTIONS=
set DIR=%~dp0bin
"%DIR%\java" %JLINK_VM_OPTIONS% -m com.app.ancea/com.app.ancea.Main %*
