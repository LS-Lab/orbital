@ECHO Off
pushd %1
if exist exclude goto Fin
if exist *.java dir %1\*.java /B >> %HOME%\java\orbital\class-list-new
if exist substitute.bat goto substitute
if not exist *.java goto Fin

cd >> %HOME%\java\orbital\package-list-new
cd
call mklib %2 %3 %4 %5
goto Fin

 :substitute
   rem substitue and discontinue further execution then since NO call is used
    call substitute.bat
 :Fin
popd
