#!/bin/bash

#
# Run script by adding a parameter indicating the problem name. e.g., ./run.sh ZDT3
# 
# The porgram can receive 1 para that indicates the problem name,
# which must fit with those in the package jmetal.problems
#
# arg0  problem to solve
#

ant run -Darg0=$1

