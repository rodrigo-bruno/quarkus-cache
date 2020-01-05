#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

function start_runner {
    runner=builds/$build/rest-json-quickstart-1.0-SNAPSHOT-runner
    echo "########## testing $build..."
    $runner -Xmx8g &> runner-$build.log &
    echo $! > runner.pid
}

function stop_runner {
    kill `cat runner.pid`
    rm runner.pid
}

function initialize {
    curl -s -i -X GET http://localhost:8080/movies/init
        echo ""
}

for build in vanilla inlined
do
    start_runner
    initialize
    for query in Query1 Query2 Query3 Query4
    do
        tag=$build-$query
        echo "$tag"
        jmeter -n -l jmeter.log  -t "$query.jmx" | grep "summary =" | tee runner-$tag.tput
    done
    stop_runner
done
