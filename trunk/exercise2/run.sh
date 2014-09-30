#!/bin/bash

# 
# arg0  folder with TTP files
# arg1  pattern to identify the TTP problems that should be solved
# arg2  optimisation approach chosen: 1 - RLS, 2 - 1+1 EA, 3 - Local Search
# arg3  stopping criterion: number of evaluations without improvement
# arg4  stopping criterion: time in milliseconds (e.g., 60000 equals 1 minute)
# 
 
for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=a280_n1395_uncorr-similar-weights_05.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=a280_n2790_uncorr_10.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=fnl4461_n4460_bounded-strongly-corr_01.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=fnl4461_n22300_uncorr-similar-weights_05.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=fnl4461_n44600_uncorr_10.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=pla33810_n33809_bounded-strongly-corr_01.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=pla33810_n169045_uncorr-similar-weights_05.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

for i in `seq 1 $1`; do
    ant run -Darg0=instances -Darg1=pla33810_n338090_uncorr_10.ttp -Darg2=1 -Darg3=10000 -Darg4=600000
done

ant clean