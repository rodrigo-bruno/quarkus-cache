#!/bin/bash

export JAVA_HOME=/home/rbruno/software/openjdk1.8.0_222-jvmci-19.3-b01-fastdebug
export GRAALVM_HOME=/home/rbruno/graalvm_builds/inlined/graalvm-unknown-19.3.0-dev
#export GRAALVM_HOME=/home/rbruno/graalvm_builds/vanilla/graalvm-unknown-19.3.0-dev

#export GRAALVM_HOME=/home/rbruno/software/graalvm-ce-java8-19.3.0
#export GRAALVM_HOME=/home/rbruno/software/graalvm-ee-java8-19.3.0

#./mvnw package quarkus:dev  2>&1 | tee build.log
./mvnw package -Pnative 2>&1 | tee build.log
