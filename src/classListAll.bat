@ECHO Off
echo . > %home%\Java\Orbital\src\class-list
for /R %%f in (*.java) do call :listClass %%f
goto :EOF

:listClass
	echo %~pn1 >> %home%\Java\Orbital\src\class-list
goto :EOF