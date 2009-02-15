@ECHO Off
%JAVA_HOME%\bin\java.exe -mx400M -classpath ..\classes:..\dist\lib\orbital-ext.jar;%CLASSPATH% orbital.moon.logic.ClassicalLogic %1 %2 %3 %4 %5