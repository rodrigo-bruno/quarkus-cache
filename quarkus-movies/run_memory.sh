#!/bin/bash

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/


function initialize {
    curl -s -i -X GET http://localhost:8080/movies/init
        echo ""
}

for build in vanilla inlined
do
    runner=builds/$build/rest-json-quickstart-1.0-SNAPSHOT-runner
    echo "########## testing $build..."
    $runner -Xmx8g &> runner-$build.log &
    echo $! > runner.pid
    initialize | tee runner-$build.mem
    kill `cat runner.pid`
    rm runner.pid
    echo "########## testing $build... done"
done
