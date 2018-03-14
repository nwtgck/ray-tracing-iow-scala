#! /bin/sh

cd `dirname $0`
# (from: https://stackoverflow.com/a/31737303/2885946)
sbt 'set test in assembly := {}' clean assembly