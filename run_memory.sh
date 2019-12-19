#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/


function print_memory {
    curl -s -i -X GET http://localhost:8080/fruits/memory | grep memory
}

for build in vanilla inlined
do
    runner=builds/$build/rest-json-quickstart-1.0-SNAPSHOT-runner
    for i in 100000
    #for i in 1000 10000 1000000 10000000
    do
        echo "testing $build with $i entries..."
        $runner &> runner.log &
        echo $! > runner.pid
        print_memory
        jmeter -n -l jmeter.log  -t memory/Write$i.jmx | grep "summary ="
        print_memory
        kill `cat runner.pid`
        rm runner.pid
        echo "testing $build with $i entries... done"
    done
done
