@ECHO Off
echo . > %home%\java\orbital\class-list
for /R %%f in (*.java) do call :listClass %%f
goto :EOF

:listClass
	echo %~pn1 >> %home%\java\orbital\class-list
goto :EOF