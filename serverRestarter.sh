#!/bin/bash

#Watch if something changes in ./bin folder and then restart the server
#if automatic build is enabled this will restart server each time I 
#save a source file.

#copyright Anton Krug 2014
#requires inotify tools

while true
do
    pid=`ps -eo pid,comm,args | grep java | grep antonkrug | cut -dj -f1`
    kill -9 $pid
    echo killing $pid
#    inotifywait -e modify -e create -e delete -e attrib ./bin
    inotifywait -e modify -e create -e delete -r -q ./bin
done



