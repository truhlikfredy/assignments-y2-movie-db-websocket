#!/bin/bash

#copyright Anton Krug 2014

cp java_api_begin.txt java_api.java
cp main.js bundle.js
for val in `cat common_values.py | tr '\n' ' '`
do
 name=`echo $val | cut -d '=' -f 1` 
 namelow=`echo ${name,,}`
 val=`echo $val | cut -d '=' -f 2` 
 sed -i "s/%$name%/$val/g" bundle.js
 echo -e "  $name($val)," >>java_api.java
# echo $namelow
done
cat java_api_tail.txt >> java_api.java

cp java_api.java ../src/eu/antonkrug/API.java
cp bundle.js ../web-client/js/bundle.js

