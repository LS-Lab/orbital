@ECHO Off
%JAVA_HOME%\bin\java.exe -classpath ..\lib\orbital-ext.jar;%CLASSPATH% orbital.moon.evolutionary.BreederControl %1 %2 %3 %4 %5