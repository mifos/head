@echo off
if "%OS%" == "Windows_NT" setlocal

setlocal enabledelayedexpansion

rem %~dp0 is expanded pathname of the current script under NT
set LIQUIBASE_HOME="%~dp0"
set LIQUIBASE_HOME_WITHOUT_QUOTES=%~dp0

set CP=.
for /R %LIQUIBASE_HOME%\.. %%f in (liquibase*.jar) do set CP=!CP!;%%f
for /R %LIQUIBASE_HOME%\..\lib %%f in (*.jar) do set CP=!CP!;%%f

rem get command line args into a variable
set CMD_LINE_ARGS=%1
if ""%1""=="""" goto done
shift
:setup
if ""%1""=="""" goto done
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setup
:done

set JAVA_OPTS=

set DB_PROPS_FILE=%LIQUIBASE_HOME_WITHOUT_QUOTES%\..\mifos-db.properties
set CHANGE_LOG_FILE=%LIQUIBASE_HOME_WITHOUT_QUOTES%\..\changesets\changelog_relG_special.xml
set DRIVER=com.mysql.jdbc.Driver
FOR /F "tokens=1,2 delims==" %%A IN (%DB_PROPS_FILE%) DO IF "%%A"=="defaultSchemaName" set DEFAULT_SCHEMA_NAME=%%B
FOR /F "tokens=1,2 delims==" %%A IN (%DB_PROPS_FILE%) DO IF "%%A"=="username" set USER_NAME=%%B
FOR /F "tokens=1,2 delims==" %%A IN (%DB_PROPS_FILE%) DO IF "%%A"=="password" set PASSWORD=%%B
FOR /F "tokens=1,2 delims==" %%A IN (%DB_PROPS_FILE%) DO IF "%%A"=="databaseHost" set HOST=%%B
FOR /F "tokens=1,2 delims==" %%A IN (%DB_PROPS_FILE%) DO IF "%%A"=="databasePort" set PORT=%%B
set URL=jdbc:mysql://%HOST%:%PORT%/%DEFAULT_SCHEMA_NAME%

java -cp "%CP%" %JAVA_OPTS% liquibase.integration.commandline.Main --changeLogFile=%CHANGE_LOG_FILE% --driver=%DRIVER% --url=%URL% --username=%USER_NAME% --password=%PASSWORD% --defaultSchemaName=%DEFAULT_SCHEMA_NAME% %CMD_LINE_ARGS%
