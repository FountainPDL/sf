#!/bin/sh
#
# Copyright © 2015-2021 the original authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
die () {
    echo
    echo "ERROR: $*"
    echo
    exit 1
}
warn () {
    echo "$*"
}
set -e
DIRNAME=`dirname "$0"`
cd "$DIRNAME"
APP_HOME=`pwd -P`
exec "$JAVACMD" "$@" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"
