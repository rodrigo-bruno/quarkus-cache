#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

dataset=jmeter_db_10K.csv

rm jmeter_db.csv
ln -s $dataset jmeter_db.csv

function start_runner {
    runner=builds/$build/rest-json-quickstart-1.0-SNAPSHOT-runner
    echo "########## testing $build..."
    $runner &> runner-$build.log &
    echo $! > runner.pid
}

function stop_runner {
    kill `cat runner.pid`
    rm runner.pid
}

for build in vanilla inlined
do
    start_runner
    for i in 1 2 4 8 16 32 64 128
    do
        for j in Write Read
        do
            tag=$build-$j-$i
            echo "$tag"
            jmeter -n -l jmeter.log  -t "throughput_resptime/$j$i.jmx" | grep "summary =" | tee runner-$tag.tput
        done
    done
    stop_runner
done
