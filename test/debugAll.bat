@ECHO Off
rem @todo at least break when exceptions occur, and make a report file
echo NOTE: you should have compiled with -source 1.4
echo Debug all Classes that provide an inner class Debug with a main method
pushd %JAVA_HOME%\classes
echo @echo off > %temp%\t.bak
for /R %%U in (*$Debug.class) do call :Use %%~dnpU
sed -x -e s/D:\\Sprachen\\java\\jre\\classes\\// -e s/\\/\//g < %temp%\t.bak > %temp%\t.bat
del %temp%\t.bak
call %temp%\t.bat
@echo off
del %temp%\t.bat
popd
echo Remember calling all classes in %HOME%\Java\new\test
goto :EOF
:Use
    echo echo %1 >>%temp%\t.bak
    echo java -ea %1 >>%temp%\t.bak
    echo if ERRORLEVEL 1 echo FAIL: %1 >>%temp%\t.bak