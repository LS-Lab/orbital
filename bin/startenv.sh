#!/bin/sh
echo starting rmi daemon
rmid &
echo starting rmi registry
rmiregistry &
echo starting transient CORBA nameserver 
tnameserv &