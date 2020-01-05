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
    for i in 1M 2M 4M 8M 12M 16M
    do
        echo "########## testing $build with $i entries..."
        rm jmeter_db.csv &> /dev/null
        ln -s jmeter_db_$i.csv jmeter_db.csv
        $runner -Xmx8g &> runner-$build-$i.log &
        echo $! > runner.pid
        initialize | tee runner-$build-$i.mem
        kill `cat runner.pid`
        rm runner.pid
        echo "########## testing $build with $i entries... done"
        sleep 1
    done
done
