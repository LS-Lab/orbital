#!/bin/sh
#
echo "NOTE: this feature has been excluded from the Orbital library and is available as an Add-on, only"
echo remember calling "startenv" to run invocation daemons!
echo You must also make sure the resources are accessible, refer to readme.html
java -classpath ..\lib\orbital-ext.jar;%CLASSPATH% -Djava.security.policy=policy.all orbital.moon.orbiter.tool.OrbitalControl $1 $2 $3 $4 $5