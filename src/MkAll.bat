@ECHO Off
echo Usage: MkAll [-generate] [{-jini,-jar}] [-optimize] [-nodeprecated]
echo    -generate     only generates preprocessed java/class files
echo    -jini         only generates the jini downloadable archive
echo    -jar          only generates the jar archive
echo    -optimize     optimize jar archive, remove superfluous debug information
echo    -nodeprecated optimize jar archive, remove deprecated classes

set O=
set O2=
if "%1"=="-optimize" set O=optJar
if "%2"=="-optimize" set O=optJar
if "%3"=="-optimize" set O=optJar
if "%1"=="-nodeprecated" set O2=optNodeprecated
if "%2"=="-nodeprecated" set O2=optNodeprecated
if "%3"=="-nodeprecated" set O2=optNodeprecated
if "%4"=="-nodeprecated" set O2=optNodeprecated

echo using library
call unlib d

if "%1"=="-jini" goto Jini
if "%1"=="-jar" goto JAR
if "%O%"=="extJAR" goto %O%

call ReBuild -generate
if "%1"=="-generate" goto Generate

  echo compiling
    echo . > %HOME%\Java\Orbital\src\orbital\package-list-new
    echo . > %HOME%\Java\Orbital\src\orbital\class-list-new
    REM call mklib
    REM C:\bin\forEach -v -dir _mklib.bat "%"
    pushd
    if "%1"=="-optimize" for /R /D %%d in (*) do call %home%\Java\Orbital\src\orbital\_mklib.bat %%d -options -g:lines
    if not "%1"=="-optimize" for /R /D %%d in (*) do call %home%\Java\Orbital\src\orbital\_mklib.bat %%d
    popd
    call mklib %HOME%\java\Orbital\src\orbital\math\functional
  if not "%O2%"=="optNodeprecated" goto Generate
  echo compiling deprecated classes
    echo remember removing exclude files!
    pushd moon\deprecated
    for /R /D %%d in (*) do call %home%\Java\Orbital\src\orbital\_mklib.bat %%d
    popd
:Generate
goto :JAR
REM Skip this Section
:Jini
  echo packing downloadable jini jar file
    pushd %JAVA_HOME%\classes
    jar cf %HOME%\www\orbital\orbital.rt\orbital\lib\orbital-dl.jar -C . orbital\moon\orbiter\jini\Oracle.class orbital\moon\orbiter\jini\OracleJini_Stub.class orbital\moon\orbiter\jini\JiniOrbitalImpl_Stub.class orbital\moon\orbiter\remote\*.class orbital\moon\orbiter\ServiceImpl_Stub.class
    pushd %HOME%\www\orbital\orbital.rt\orbital\lib
    echo Enter Passphrase for keystore: (confirm with enter and Ctrl-Z)
    copy con %tmp%\xy
    jarsigner orbital-dl.jar Andre < %tmp%\xy
    touch -t01:00:01 orbital-dl.jar
    copy orbital-dl.jar %HOME%\www\orbital
    move orbital-dl.jar %HOME%\www\orbital\orbital.rt\orbital\lib\remote
    popd
    popd
    if "%1"=="-jini" goto ENDE
    if "%2"=="-jini" goto ENDE
:JAR
  if "%1"=="-jar" call ReBuild -prepare
  echo packing
    pushd %JAVA_HOME%\classes
    if "%O%"=="optJar" call :%O%
    del orbital\moon\io\cryptix\Steg*.class >NUL
    del orbital\moon\io\cryptix\provider\HandshakeRand*.class >NUL
    jar cfm %HOME%\www\orbital\orbital.rt\orbital\lib\orbital-core.jar meta-inf/manifest-core.mf -C . orbital/*.class orbital/logic/*.class orbital/logic/functor/*.class orbital/logic/trs/*.class orbital/logic/imp/*.class orbital/math/*.class orbital/math/functional/*.class orbital/util/*.class orbital/util/graph/*.class
    jar cfm %HOME%\www\orbital\orbital.rt\orbital\lib\orbital-ext.jar meta-inf/manifest-ext.mf -C . orbital/awt/*.gif orbital/awt/*.class orbital/game/*.class orbital/algorithm/*.class orbital/algorithm/evolutionary/*.class orbital/algorithm/template/*.class orbital/robotic/*.class orbital/robotic/strategy/*.class orbital/io/*.class meta-inf/services orbital/resources orbital/moon orbital/awt/virtual/*.class orbital/text/*.class orbital/io/*.class orbital/io/encoding/*.class orbital/io/parsing/*.class
    jar cfm %SystemDrive%\pub\ftp\orbital.jar meta-inf/manifest-core.mf -C . orbital/*.class orbital/logic/*.class orbital/logic/functor/*.class orbital/logic/trs/*.class orbital/logic/imp/*.class orbital/math/*.class orbital/math/functional/*.class orbital/util/*.class orbital/util/graph/*.class orbital/awt/*.gif orbital/awt/*.class orbital/game/*.class orbital/algorithm/*.class orbital/algorithm/evolutionary/*.class orbital/algorithm/template/*.class orbital/robotic/*.class orbital/robotic/strategy/*.class orbital/io/*.class meta-inf/services orbital/resources orbital/moon orbital/awt/virtual/*.class orbital/text/*.class orbital/io/*.class orbital/io/encoding/*.class orbital/io/parsing/*.class
    rem excluded orbital/io/cryptix/*.class orbital/net/secure/*.class
    pushd %HOME%\www\orbital\orbital.rt\orbital\lib\
    rem ascending order might be important
    rem jar -i orbital-core.jar
    rem jar -i orbital-ext.jar
    echo Enter Passphrase for keystore: (confirm with enter and Ctrl-Z)
    if not exist %tmp%\xy copy con %tmp%\xy
    jarsigner orbital-core.jar Andre <%tmp%\xy
    jarsigner orbital-ext.jar Andre <%tmp%\xy
    copy nul %tmp%\xy
    del %tmp%\xy
    C:\bin\touch -t01:00:07 orbital*.jar
    copy orbital*.jar %HOME%\www\orbital
    if exist orbital-dl.jar move orbital-dl.jar remote\orbital-dl.jar
    popd
    popd
    if "%O%"=="optJar" call :%O%Clean
    if "%O%"=="extJAR" call :%O%
goto ENDE
:optJAR
  echo optimizing
    xcopy *.* %tmp%\opt\ /S /Y >NUL
    del %tmp%\opt\*$Debug*.class /S
    popd
    pushd %tmp%\opt
goto :EOF
:optJARClean
    rd %tmp%\opt /S /Q
goto :EOF
:ENDE
copy nul %tmp%\xy
del %tmp%\xy
set O=
set O2=