@echo off
if "%OS%" == "Windows_NT" setlocal

setlocal enabledelayedexpansion

rem %~dp0 is expanded pathname of the current script under NT
set LIQUIBASE_DIR=%~dp0

%LIQUIBASE_DIR%\liquibase.bat --contexts=expansion rollbackToDate %1
