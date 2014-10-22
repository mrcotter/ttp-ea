#!/bin/bash

#
# Run script. e.g., ./run.sh
# 
# The porgram can receive 1 para that indicates the ttp file name,
#
# arg0  ttp filename
#

script_path=`echo $0 | rev | cut -d "/" -f2-  | rev`
JMETALHOME="$script_path/src/jmetal"

export CLASSPATH=$CLASSPATH:$JMETALHOME

ant run -Darg0="a280_n279_bounded-strongly-corr_01.ttp"

#ant run -Darg0="a280_n1395_uncorr-similar-weights_05.ttp"

#ant run -Darg0="a280_n2790_uncorr_10.ttp"