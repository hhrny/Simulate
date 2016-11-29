#!/bin/bash
# This script will be used to launch simulations

if [ $# -eq 0 ]  # There is no arguments
then
    echo 
    echo 'Usage: ./simulate.sh <input_file_name> [OPTIONS]'
    echo 
    echo '    <input_file_name> : the input file contains a set of global parameters'
    echo
    echo '    [OPTION]'
    echo '        -able-logging or -disable-logging to open or close the logging'
    echo '        -able-detailed or disable-detailed to open or close display the details'
    echo
else
    if [ -f $1 ]
    then
        java -cp "./Simulate/bin:/lib/*" Simulate.Simulate $*
    else
        echo "The file $1 is no exist"
    fi
fi

