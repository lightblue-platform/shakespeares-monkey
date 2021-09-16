#!/usr/bin/env sh

echo "DEPLOY MASTER BUILD"
echo "Current directory is $(pwd)"
'[[ $BRANCH == "master" ]] && [[ $JDK_VERSION == "openjdk7" ]] && { mvn clean deploy -DskipTests; };'
