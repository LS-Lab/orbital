@ECHO Off
xcopy nothing* ..\convert\ /S /E >NUL
for /R %%U in (*.java) do call :Convert %%~dnpU
goto :EOF

:Convert
  c:\bin\sed\sed.exe -f convert.sed < %1 > ..\convert\%1
goto :EOF