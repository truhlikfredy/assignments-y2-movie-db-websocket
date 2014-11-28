#!/bin/sh

#copyright Anton Krug

while true
do
    java -Dfile.encoding=UTF-8 -classpath ./bin:./src/trove-141123034115.jar:./src/colt-1.2.0.jar:./src/xstream-1.4.3.jar:./src/mws.jar:./src/json-simple-1.1.1.jar:./src/commons-collections4-4.0.jar eu.antonkrug.Server
done