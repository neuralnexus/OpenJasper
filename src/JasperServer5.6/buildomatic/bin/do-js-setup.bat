@ECHO OFF

rem
rem JasperReports Server common installation and upgrade script.
rem
rem Usage: do-js-setup.bat (install|upgrade){setup mode} (ce|pro){edition} {script option} {Ant target} [Ant options]
rem

rem -----------------------------------------------------------------------------

rem
rem Determining argument count.
rem
SET ARGUMENT_COUNT=0
FOR %%X IN (%*) DO SET /A ARGUMENT_COUNT+=1

rem
rem Validating arguments and setting internal variables.
rem
IF %ARGUMENT_COUNT% LSS 4 (
  CALL :fail "Invalid argument count"
  EXIT /b 1
)
IF %ARGUMENT_COUNT% GTR 7 (
  CALL :fail "Invalid argument count"
  EXIT /b 1
)

SET JS_SETUP_MODE=%1
IF NOT "%JS_SETUP_MODE%"=="install" IF NOT "%JS_SETUP_MODE%"=="upgrade" (
  CALL :fail "Setup mode expected as input"
  EXIT /b 1
)

SET JS_EDITION=%2
IF NOT "%JS_EDITION%"=="ce" IF NOT "%JS_EDITION%"=="pro" (
  CALL :fail "JasperReports Server edition expected as input"
  EXIT /b 1
)

SET JS_OPTION=%3
SET JS_ANT_TARGET=%4
SET JS_ANT_UPGRADE_STRATEGY_OPTION=-Dstrategy=%5
IF "%JS_SETUP_MODE%"=="upgrade" (
  SET JS_ANT_OPTIONS=%JS_ANT_UPGRADE_STRATEGY_OPTION%
  IF "%5"=="standard" IF NOT ""%6""=="""" (
    SET JS_ANT_OPTIONS=%JS_ANT_UPGRADE_STRATEGY_OPTION% -DimportFile=%6
	IF NOT ""%7""=="""" (
		SET JS_ANT_OPTIONS=%JS_ANT_UPGRADE_STRATEGY_OPTION% -DimportFile=%6 -DincludeAccessEvents=%7
	)
  )
)
SET JS_ANT_OPTIONS=%JS_ANT_OPTIONS% -Djs.setup.mode=%JS_SETUP_MODE%

rem
rem Initializing time variable.
rem
CALL "%~dp0/date.bat"
CALL "%~dp0/time.bat"
SET JS_CURRENT_TIME=%NOW_YYYY_MM_DD%_%NOW_HH_MM%

rem
rem Defining log file name, creating log directory if it doesn't exist.
rem
SET /a JS_LOG_FILE_PREFIX=%RANDOM%+10000
SET JS_LOG_FILE=logs/js-%JS_SETUP_MODE%-%JS_EDITION%_%JS_CURRENT_TIME%_%JS_LOG_FILE_PREFIX%.log
IF NOT EXIST logs (
  md logs
)
ECHO Writing to log file: %JS_LOG_FILE%

rem
rem Printing entry information.
rem
CALL :log
CALL :log "Running JasperReports Server %JS_SETUP_MODE% script at %JS_CURRENT_TIME%"
CALL :log

rem
rem Checking JAVA_HOME.
rem
if "%JAVA_HOME%"=="" (
  CALL :log "WARNING: JAVA_HOME environment variable not found"
)

rem
rem Setting up Ant.
rem

CALL :log "[%JS_OPTION%]"

:initializeAntEnvironment
IF EXIST "..\apache-ant" ( GOTO :useBundledAnt )
CALL :log "Bundled Ant not found. Using existing Ant."
SET ANT_RUN=ant
GOTO :endAntSetup

:useBundledAnt
SET ANT_HOME=..\apache-ant
SET ANT_RUN=%ANT_HOME%\bin\ant
SET PATH=%PATH%;%ANT_HOME%\bin

:endAntSetup

rem
rem Running Ant.
rem

:runAnt
IF "%JS_ANT_TARGET%" == "" ( GOTO :antTargetNotSpecified )
CALL :log "Running %JS_ANT_TARGET% Ant task"
CALL :log
CALL %ANT_RUN% -nouserlib -f build.xml %JS_ANT_TARGET% %JS_ANT_OPTIONS% 2>&1 | "%~dp0/wtee" -a %JS_LOG_FILE%

IF ERRORLEVEL 1 ( GOTO :runAntFailed )
CALL :log "Checking Ant return code: OK"
CALL :log
GOTO :end

rem -----------------------------------------------------------------------------

rem
rem Console + file logging subroutine.
rem
:log
SET JS_LOG_MESSAGE=
IF "%~1" == "" SET JS_LOG_MESSAGE=----------------------------------------------------------------------
IF NOT "%~1" == "" SET JS_LOG_MESSAGE=%~1
ECHO %JS_LOG_MESSAGE% | "%~dp0/wtee" -a %JS_LOG_FILE%
GOTO:EOF

:showUsage
ECHO Usage: ./do-js-setup.bat {setup mode} {edition} {script option} {Ant target} {Ant options}
GOTO:EOF

:fail
IF NOT "%~1" == "" ( ECHO %~1 )
CALL :showUsage
EXIT /b 1

:runAntFailed
CALL :log "Checking Ant return code: BAD (1)"
EXIT /b 1

:end
EXIT /b 0
