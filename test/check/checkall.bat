@ECHO Off
rem perform class consistency checks
REM call mklib
echo Run consistency checks on all classes
setlocal
set CLASSPATH=%CLASSPATH%;..\test
pushd ..\..\classes
echo @echo off > %temp%\t.bak
for /R %%U in (*.class) do call :Use %%~dnpU
sed -x -e s-C:\\Personal\\Java\\Orbital\\classes\\--g -e s/\\/\//g < %temp%\t.bak > %temp%\t.bat
del %temp%\t.bak
call %temp%\t.bat
@echo off
del %temp%\t.bat
popd
echo done
pause
goto :EOF

:Use
  echo java -ea check.ClassConsistencyCheck %1 >>%temp%\t.bak