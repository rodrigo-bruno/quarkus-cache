#!/bin/bash

export JAVA_HOME=/home/rbruno/software/openjdk1.8.0_222-jvmci-19.3-b01-fastdebug

function build_inlined {
    export GRAALVM_HOME=/home/rbruno/graalvm_builds/inlined/graalvm-unknown-19.3.0-dev
    cp src/main/resources/application.properties.inlined src/main/resources/application.properties
    ./mvnw package -Pnative 2>&1 | tee build.log
    cp target/rest-json-quickstart-1.0-SNAPSHOT-runner build.log   builds/inlined/
}

function build_vanilla {
    export GRAALVM_HOME=/home/rbruno/graalvm_builds/vanilla/graalvm-unknown-19.3.0-dev
    cp src/main/resources/application.properties.vanilla src/main/resources/application.properties
    ./mvnw package -Pnative 2>&1 | tee build.log
    cp target/rest-json-quickstart-1.0-SNAPSHOT-runner build.log   builds/vanilla/
}

build_inlined
build_vanilla
#./mvnw package quarkus:dev  2>&1 | tee build.log
