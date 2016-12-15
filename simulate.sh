#!/bin/bash
# This script will be used to launch simulations

if [ ! -d "./Statistic" ]
then
    mkdir ./Statistic
fi

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
    exit
else
    if [ -f $1 ]
    then
        java -cp "./Simulate/bin:/lib/*" Simulate.Simulate $*
    else
        echo "The file $1 is no exist"
    fi
    # visualisation
    java -cp "./Simulate/bin:/lib/*" Visualisation.Visualisation "./Statistic/s1"
    
    # generate the visualisation of statistic
    cd ./Statistic
    num=$(wc -l ./v1 | awk '{print $1}')
    if [ ${num} -gt 1 ]
    then
        gnuplot ./v1_plot
    fi
    num=$(wc -l ./v2 | awk '{print $1}')
    if [ ${num} -gt 1 ]
    then
        gnuplot ./v2_plot
    fi
    num=$(wc -l ./v3 | awk '{print $1}')
    if [ ${num} -gt 1 ]
    then
        gnuplot ./v3_plot
    fi
fi

