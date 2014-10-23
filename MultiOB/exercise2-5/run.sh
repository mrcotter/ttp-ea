#!/bin/bash

#
# Run script by adding a parameter indicating metaheuristic algorithms.
# e.g., ./run.sh nsgaii      ./run.sh spea2        ./run.sh ibea
# 
# The porgram can receive 1 argument that indicates the ttp file name
# arg0  ttp filename
#


ant $1 -Darg0="a280_n279_bounded-strongly-corr_01.ttp"

#ant $1 -Darg0="a280_n1395_uncorr-similar-weights_05.ttp"

#ant $1 -Darg0="a280_n2790_uncorr_10.ttp"

#ant $1 -Darg0="fnl4461_n4460_bounded-strongly-corr_01.ttp"

#ant $1 -Darg0="pla33810_n33809_bounded-strongly-corr_01.ttp"