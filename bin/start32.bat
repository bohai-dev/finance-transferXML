
setlocal
cd ..
set "CURRENT_DIR=%cd%"
echo %CURRENT_DIR%


setlocal enableextensions enabledelayedexpansion

set classpath=.
for %%c in (%CURRENT_DIR%\lib\*.jar) do set classpath=!classpath!;%%c

start jre32\bin\javaw -Dfile.encoding=UTF-8 -classpath %classpath% com.bohai.finance.main.Demo

