@ECHO Off
REM @todo reenable a gjc that knows assert
call myjavac *.java -d %JAVA_HOME%\classes %1 %2 %3 %4 %5
goto :EOF
setlocal
    set dst=%tmp%\substitute%RANDOM%
    if exist %dst% goto Warn
    mkdir %dst%
    pushd %home%\Java\orbital\util
    for %%f in (Pair.java, Setops.java) do sed -f %home%\Java\InstantiateTemplates.sed %%f > %dst%\%%f
    popd
    for %%f in (*.java) do sed -f %home%\Java\InstantiateTemplates.sed %%f > %dst%\%%f
    pushd %dst%
    call gjc -d %JAVA_HOME%\classes -unchecked %1 %2 %3 %4 %5 *.java
    popd
    rd %dst% /S /Q
goto :EOF

:Warn
    echo WARNING: temporary directory %dst% does alread exist
goto :EOF
