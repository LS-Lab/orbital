@ECHO Off
echo Will recompile all example files provided a correct path and class path
echo Use with care!
pause
for /R %%i in (*.java) do call :Use %%~dpi %%~nxi
pause
goto :EOF

:Use
    echo %1%2
    cd %1
    javac %2
goto :EOF

REM @todo reformat all example source codes