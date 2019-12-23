#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/


function print_memory {
    curl -s -i -X GET http://localhost:8080/fruits/memory
        echo ""
}

function initialize {
    curl -s -i -X GET http://localhost:8080/fruits/initialize
        echo ""
}

for build in vanilla inlined
do
    runner=builds/$build/rest-json-quickstart-1.0-SNAPSHOT-runner
    for i in 1000 10000 100000 1000000
    do
        echo "########## testing $build with $i entries..."
        rm jmeter_db.csv &> /dev/null
        ln -s jmeter_db_$i.csv jmeter_db.csv
        $runner &> runner.log &
        echo $! > runner.pid
        initialize
        kill `cat runner.pid`
        rm runner.pid
        echo "########## testing $build with $i entries... done"
    done
done
