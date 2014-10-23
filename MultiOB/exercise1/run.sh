#!/bin/bash

#
# Run script. e.g., ./run.sh
# 
# The porgram can receive 1 para that indicates the problem name,
# which must fit with those in the package jmetal.problems
#
# arg0  problem to solve
#

ant run -Darg0=ZDT3

#ant run -Darg0=ZDT2

