@ECHO off
echo starting rmi daemon
start /B rmid
pause
echo starting rmi registry
start /B rmiregistry
pause
echo starting transient CORBA nameserver 
start /B tnameserv

