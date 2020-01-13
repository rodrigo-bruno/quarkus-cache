#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/

function initialize {
    curl -s -i -X GET http://localhost:8080/movies/dryinit?dbpath=/home/rbruno/Downloads/imdb-lite
    #curl -s -i -X GET http://localhost:8080/movies/dryinit?dbpath=/home/rbruno/Downloads/imdb
    echo ""
}

#for build in vanilla inlined
for build in vanilla
do
    runner=builds/$build/rest-json-quickstart-1.0-SNAPSHOT-runner
    echo "########## testing $build..."
    $runner -Xmx20g &> runner-$build.log &
    echo $! > runner.pid
    sleep 1
    initialize | tee runner-$build.mem
    kill `cat runner.pid`
    rm runner.pid
    echo "########## testing $build... done"
done
