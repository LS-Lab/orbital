@ECHO Off
if "%JAVA_HOME%"=="" java.exe -jar ..\lib\orbital-ext.jar %1 %2 %3 %4 %5
if not "%JAVA_HOME%"=="" %JAVA_HOME%\bin\java.exe -jar ..\lib\orbital-ext.jar %1 %2 %3 %4 %5