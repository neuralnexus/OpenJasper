@echo off

REM Collect the command line args

set JS_CMD_NAME=%0

set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto setArgs
:doneSetArgs

REM Set the config folder to use
REM if Pro config exists, then set to Pro config
REM otherwise, assume ce config

set JS_EDITION="ce"
if exist "conf_source\iePro" goto setConfPro
set CONFIG_DIR=conf_source\ieCe
goto doneSetConf
:setConfPro
set CONFIG_DIR=conf_source\iePro
set JS_EDITION="pro"
:doneSetConf

REM additional config dir to find the js.jdbc.properties
set ADDITIONAL_CONFIG_DIR=build_conf\default

REM Add all jars the export classpath variable
set EXP_CLASSPATH=%CONFIG_DIR%\lib\*

rem
rem Initializing time variable.
rem


CALL "%~dp0/bin/date.bat"
CALL "%~dp0/bin/time.bat"
SET JS_CURRENT_TIME=%NOW_YYYY_MM_DD%_%NOW_HH_MM%
SET JS_SETUP_MODE="import"

rem
rem Defining log file name, creating log directory if it doesn't exist.
rem
SET /a JS_LOG_FILE_PREFIX=%RANDOM%+10000
SET JS_LOG_FILE=logs/js-%JS_SETUP_MODE%-%JS_EDITION%_%JS_CURRENT_TIME%_%JS_LOG_FILE_PREFIX%.log
IF NOT EXIST logs (
  md logs
)
ECHO Writing to log file: %JS_LOG_FILE%

REM Set the java command
REM
REM If "..\java\bin\java.exe" exists then use it
REM

if exist "..\java\bin\java.exe" goto setLocalJava

goto setStandardJava
:setLocalJava

set JAVA_HOME="..\java"
set PATH=..\java\bin;%PATH%

goto doneJava
:setStandardJava

if "%JAVA_HOME%"=="" goto warnJava

goto doneJava
:warnJava

echo "WARNING: Did not find a JAVA_HOME environment variable setting. Script will continue."

:doneJava

:initializeAntEnvironment
IF EXIST "..\apache-ant" ( GOTO :useBundledAnt )
CALL :log "Bundled Ant not found. Using system Ant."
SET ANT_RUN=ant
GOTO :endAntSetup

:useBundledAnt
SET ANT_HOME=..\apache-ant
SET ANT_RUN=%ANT_HOME%\bin\ant
SET PATH=%PATH%;%ANT_HOME%\bin

:endAntSetup

:runAnt
IF "%BUILDOMATIC_MODE%"=="" set BUILDOMATIC_MODE=interactive
SET JS_ANT_TARGET="validate-keystore"

CALL :log "Running %JS_ANT_TARGET% Ant task"
CALL :log
CALL %ANT_RUN% -nouserlib -f build.xml %JS_ANT_TARGET% %JS_ANT_OPTIONS% && set temp= 2>&1 | "%~dp0/bin/wtee" -a %JS_LOG_FILE%

IF %ERRORLEVEL% == 0 ( GOTO :runImport )
IF not %ERRORLEVEL% == 0 ( GOTO :runAntFailed )

CALL :log "Checking Ant return code: OK"
CALL :log
GOTO :end

rem
rem Console + file logging subroutine. TODO
rem
:log

SET JS_LOG_MESSAGE=
IF "%~1" == "" SET JS_LOG_MESSAGE=----------------------------------------------------------------------
IF NOT "%~1" == "" SET JS_LOG_MESSAGE=%~1
ECHO %JS_LOG_MESSAGE% | "%~dp0/bin/wtee" -a %JS_LOG_FILE%
GOTO:EOF

:runImport

REM Set the java memory options

set JAVA_OPTS=%JAVA_OPTS% -Xms128m -Xmx512m -noverify

REM Add config dirs to EXP_CLASSPATH

set EXP_CLASSPATH=%CONFIG_DIR%;%ADDITIONAL_CONFIG_DIR%;%EXP_CLASSPATH%;.

java -classpath "%EXP_CLASSPATH%" %JAVA_OPTS% com.jaspersoft.jasperserver.export.ImportCommand %JS_CMD_NAME% %CMD_LINE_ARGS%

GOTO:EOF

:runAntFailed
CALL :log "Checking Ant return code: BAD (1)"
EXIT /b 1
