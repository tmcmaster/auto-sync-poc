#!/bin/bash
#
# complete -W "`./build.sh --module-list`" build.sh

if [ $1 == "--module-list" ];
then
    ls */pom.xml |awk -F"/" '{print $1}' |tr '\n' ' '
fi
