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

function build_ee {
    export GRAALVM_HOME=/home/rbruno/Downloads/graalvm-ee-complete-19.2.0
    cp src/main/resources/application.properties.vanilla src/main/resources/application.properties
    ./mvnw package -Pnative 2>&1 | tee build.log
    cp target/rest-json-quickstart-1.0-SNAPSHOT-runner build.log   builds/ee/
}

build_inlined
build_vanilla
#build_ee
#./mvnw package quarkus:dev  2>&1 | tee build.log
