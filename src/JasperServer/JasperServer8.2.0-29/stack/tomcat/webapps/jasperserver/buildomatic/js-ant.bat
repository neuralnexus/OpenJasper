echo off

REM  
REM chk to see if bundled ant should be used (..\apache-ant or ..\..\apache-ant) 
REM  

set bundledAntPath=..\apache-ant
if exist "..\apache-ant" goto useBundledAnt
set bundledAntPath=..\..\apache-ant
if exist "..\..\apache-ant" goto useBundledAnt 

REM try to use existing ant

echo WARNING: No bundled Ant found (..\apache-ant or ..\..\apache-ant). Using system Ant

set ANT_RUN=ant

goto :doneAntSetup

:useBundledAnt

set ANT_HOME=%bundledAntPath%
set PATH=%ANT_HOME%\bin;%PATH%
set ANT_RUN=%ANT_HOME%\bin\ant.bat

:doneAntSetup

REM Collect the command line args

set DROP_FIRST=%0

set CMD_LINE_ARGS=
:setArgs
if ""%1""=="""" goto doneSetArgs
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto :setArgs
:doneSetArgs

IF "%BUILDOMATIC_MODE%"=="" set BUILDOMATIC_MODE=interactive

rem Directory paths are relative to JRS_HOME\buildomatic.
for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do @set "jver=%%j"
echo %jver%
IF %jver% GEQ 17 (
  echo "Copying additional jar file(s) that are needed very specific to jdk 17+ runtime."
  copy /Y .\install_resources\extra-jars-jdk17\asm*.jar .\lib
  copy /Y .\install_resources\extra-jars-jdk17\nashorn*.jar .\lib
) ELSE (
  echo "Deleting any existing jar file(s) that are needed very specific to jdk 17+ runtime."
  del /F /Q .\lib\asm*.jar
  del /F /Q .\lib\nashorn*.jar
)

%ANT_RUN% -nouserlib -lib . -lib lib  -f build.xml %CMD_LINE_ARGS%
