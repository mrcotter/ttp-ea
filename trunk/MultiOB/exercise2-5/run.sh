#!/bin/bash

#
# Run script by adding a argument indicating metaheuristic algorithms.
# e.g., ./run.sh nsgaii      ./run.sh spea2        ./run.sh ibea
# If no argument supplied, NSGA-II will be used as deafult algorithm.
#
# The porgram can receive 1 argument that indicates the ttp file name
# arg0  ttp filename
#

if [ $# -eq 0 ]
  then
    echo "No algorithm supplied"
    echo "NSGA-II will be used as default algorithm"

    #ant nsgaii -Darg0="a280_n279_bounded-strongly-corr_01.ttp"
	#ant nsgaii -Darg0="a280_n1395_uncorr-similar-weights_05.ttp"
	#ant nsgaii -Darg0="a280_n2790_uncorr_10.ttp"
	ant nsgaii -Darg0="fnl4461_n4460_bounded-strongly-corr_01.ttp"
	#ant nsgaii -Darg0="pla33810_n33809_bounded-strongly-corr_01.ttp"

  else
  	#ant $1 -Darg0="a280_n279_bounded-strongly-corr_01.ttp"
	#ant $1 -Darg0="a280_n1395_uncorr-similar-weights_05.ttp"
	#ant $1 -Darg0="a280_n2790_uncorr_10.ttp"
	#ant $1 -Darg0="fnl4461_n4460_bounded-strongly-corr_01.ttp"
	ant $1 -Darg0="pla33810_n33809_bounded-strongly-corr_01.ttp"
fi

