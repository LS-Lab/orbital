@ECHO Off
echo Usage: MkDoc [{-assertions,-standard}] ...
echo    -assertions   generates assertion comments of pre/postconditions
echo    -standard     only use the standard Doclet

rem once Emacs knows Unicode we could switch Javadoc to -charset "utf-8"

    setlocal
    set base=%HOME%\Java\Orbital
    set src=%base%\src\orbital
    set s=
    if "%1"=="-assertions" set s=-doclet iContract.doclet.Standard -docletpath %jdk_home%\lib\iDoclet.jar 
    if "%1"=="-assertions" shift
    if NOT "%1"=="-standard" set s=-tag param -tag pre:mc:"Preconditions:" -tag post:mc:"Postconditions:" -tag return -tag throws -tag stereotype:a:"Stereotype:" -tag structure:t:"Structure:" -tag invariant:t:"Invariants:" -tag attribute:a:"Attributes:" -tag version -tag author -tag see
       rem -tag todo:Xa -tag xxx:Xa -tag fixme:Xa
    if "%1"=="-standard" shift
  echo make jjdoc
    rem todo let jjdoc include brief TOKEN declarations
    pushd %src%\moon\logic
    if not exist doc-files mkdir doc-files
    for %%U in (*.jj) do call jjdoc -OUTPUT_FILE:doc-files\%%~nU_grammar.html %%U
    popd
    if errorlevel 1 goto Errored

    if exist moon\io\cryptix\Stegano*.java ren moon\io\cryptix\Stegano*.java *.restricted
    pushd %JDK_HOME%\docs\orbitaldoc
    copy %base%\recommendations.*html
    copy %base%\features.*html
    copy %base%\recommendations.*html %HOME%\www\orbital\
    copy %base%\features.*html %HOME%\www\orbital\
  echo make javadoc
    javadoc %s% -d . -sourcepath %base%\src;%CLASSPATH% -link ../api -overview %base%\overview.html @%src%\options @%src%\package-grouping @%src%\package-list %1 %2 %3 %4 %5 %6 %7 %8 %9 > %temp%\doc
    if errorlevel 1 set wasError=true
    copy %HOME%\www\stylist.css stylist.css
    copy /A stylesheet.css +stylist.css
    echo "@media screen { a:link, a:visited {color:blue} }" >> stylesheet.css
    if "%wasError%" == "true" goto Errored
    start /B xcopy *.* %base%\api\ /S /Q /Y
    copy %src%\META-INF\Manifest-all.mf %HOME%\www\orbital\compack\*.*
goto :Fin

 :Errored
popd
type %temp%\doc
pause
rem fall-through

 :Fin
  echo clean up
    popd
    if exist moon\io\cryptix\*.restricted ren moon\io\cryptix\Stegano*.restricted *.java
    set src=
    set s=
  echo finished