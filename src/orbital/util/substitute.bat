@ECHO Off
REM @todo reenable gjc that knows assert
call myjavac  *.java -d %JAVA_HOME%\classes %1 %2 %3 %4 %5
goto :EOF
call mklib
setlocal
    set dst=%tmp%\substitute%RANDOM%
    if exist %dst% goto Warn
    mkdir %dst%
    for %%f in (Pair.java, Setops.java) do sed -f %home%\Java\InstantiateTemplates.sed %%f > %dst%\%%f
    pushd %dst%
    call gjc -d %JAVA_HOME%\classes -unchecked *.java
    popd
    rd %dst% /S /Q
goto :EOF

:Warn
    echo WARNING: temporary directory %dst% does alread exist
goto :EOF