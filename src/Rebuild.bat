@ECHO Off
echo Usage: ReBuild [-generate] [-prepare] [-jar] [-pause]
echo    -generate  only generates preprocessed java/class files
echo    -prepare   only prepares external resources and entries
echo    -jar       only generates the jar archive
echo    -pause     pause after each step
  if exist %JAVA_HOME%\classes\*.class echo WARNING: default package level class files exist!
  echo adding resources
    if not exist %JAVA_HOME%\classes\orbital md %JAVA_HOME%\classes\orbital
    if not exist %JAVA_HOME%\classes\orbital\resources md %JAVA_HOME%\classes\orbital\resources
    copy ..\resources %JAVA_HOME%\classes\orbital\resources
    copy ..\COPYRIGHT.txt %JAVA_HOME%\classes\orbital\resources
    copy ..\license.txt %JAVA_HOME%\classes\orbital\resources
    copy ..\orbital.gif %JAVA_HOME%\classes\orbital\resources
    copy ..\orbital.ico %JAVA_HOME%\classes\orbital\resources
    if not exist %JAVA_HOME%\classes\orbital\awt md %JAVA_HOME%\classes\orbital\awt
    copy orbital\awt\*.gif %JAVA_HOME%\classes\orbital\awt
  echo adding manifest
    if not exist %JAVA_HOME%\classes\META-INF md %JAVA_HOME%\classes\META-INF
    xcopy orbital\META-INF %JAVA_HOME%\classes\META-INF /S /Y
    rd %JAVA_HOME%\classes\META-INF\CVS /S /Q
    rd %JAVA_HOME%\classes\META-INF\services\CVS /S /Q
if "%1"=="-prepare" goto :Fin
if "%1"=="-generate" goto Generate
if "%1"=="-pause" set intermediate=pause
if "%1"=="-pause" shift

    set src=%HOME%\Java\Orbital\src

  pushd %src%
  rem Change package grouping in package-grouping, rebuild.bat and mkall.bat
  echo rebuilding core
    call myjavac -d %JAVA_HOME%\classes %1 orbital/*.java orbital/io/*.java orbital/logic/*.java orbital/logic/functor/*.java orbital/logic/trs/*.java orbital/math/*.java orbital/math/functional/*.java orbital/util/*.java orbital/util/graph/*.java
      rem orbital.moon.spec.SP_Impl
  if not "%intermediate%"=="" call %intermediate%
  echo rebuilding extension
    call myjavac -d %JAVA_HOME%\classes %1 orbital/awt/*.java orbital/io/encoding/*.java orbital/text/*.java orbital/logic/imp/*.java
      rem orbital.moon.awt.*
      rem orbital.moon.io.cryptix.spec.*
  if not "%intermediate%"=="" call %intermediate%
  echo rebuilding services
    call myjavac -d %JAVA_HOME%\classes %1 orbital/awt/virtual/*.java orbital/game/*.java orbital/net/*.java orbital/net/secure/*.java orbital/io/parsing/*.java orbital/algorithm/*.java orbital/algorithm/evolutionary/*.java orbital/algorithm/template/*.java orbital/robotic/*.java orbital/robotic/strategy/*.java
      rem orbital.moon.evolutionary.*
  if not "%intermediate%"=="" call %intermediate%
  call :Generate
  if not "%intermediate%"=="" call %intermediate%
  echo rebuilding implementation
    cd %src%
    cd orbital\moon
    for /R /D %%d in (*) do call %src%\_mklib.bat %%d
  popd
goto :Fin

:Generate
  goto :Fin
  pushd %src%
  echo generating rmi skeletons and stubs
    pushd orbital\moon\orbiter
    call substitute
    popd
  popd
goto :Fin

 :Fin
set intermediate=
