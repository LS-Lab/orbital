@ECHO Off
echo NOTE: this feature has been excluded from the Orbital library and is now
echo available as an Add-on, only
pause
echo remember calling "startenv.bat" to run invocation daemons!
echo You must also make sure the resources are accessible, refer to readme.html
%JAVA_HOME%\bin\java.exe -classpath ..\lib\orbital-ext.jar;%CLASSPATH% -Djava.security.policy=policy.all orbital.moon.orbiter.tool.OrbitalControl %1 %2 %3 %4 %5