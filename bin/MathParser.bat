@ECHO Off
%JAVA_HOME%\bin\java.exe -classpath ..\dist\lib\orbital-ext.jar;%CLASSPATH% orbital.moon.logic.MathExpressionSyntax %1 %2 %3 %4 %5