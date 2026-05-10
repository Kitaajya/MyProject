@echo off
chcp 65001 >nul
set JBR_HOME=E:\IDEA\jbr
set CP=E:\2026IDEAJava\WakeUpSchedule\build
set MAIN=wakeup.ScheduleApp
set LOG=%TEMP%\wakeup_error.log

start "" "%JBR_HOME%\bin\javaw" "-Dfile.encoding=UTF-8" -cp "%CP%" %MAIN%
if errorlevel 1 (
    echo Launch failed > "%LOG%"
    pause
)
