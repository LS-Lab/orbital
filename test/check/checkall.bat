@ECHO Off
rem perform class consistency checks
call mklib
echo Run consistency checks on all classes
setlocal
pushd ..\..\classes
echo @echo off > %temp%\t.bak
for /R %%U in (*.class) do call :Use %%~dnpU
sed -x -e s/D:\\Sprachen\\java\\jre\\classes\\// -e s/\\/\//g < %temp%\t.bak > %temp%\t.bat
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