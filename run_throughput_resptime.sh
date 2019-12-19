#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

dataset=jmeter_db_10000.csv

rm jmeter_db.csv
ln -s $dataset jmeter_db.csv

for i in 1 2 4 8 16 32 64 128
do
    for j in Write Read
    do
        echo "$j $i"
        jmeter -n -l jmeter.log  -t "throughput_resptime/$j$i.jmx" | grep "summary ="
    done
done
