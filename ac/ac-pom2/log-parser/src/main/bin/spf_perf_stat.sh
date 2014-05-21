#!/bin/bash

#######
# Produce request/wsrp(request,profile)/duration statistics on specified time window
# Usage: spf_perf_stat.sh 2012-07-05 a.txt b.txt
#######

function produce(){
echo "Produce result ${DATE} ${1}:00:00 - ${DATE} ${2}:00:00 to spf_perf_stat_${DATE}_${1}_${2}.result"
java -classpath ac-log-parser.jar -DstartTime="${DATE} ${1}:00:00" -DendTime="${DATE} ${2}:00:00" com.hp.it.perf.ac.load.hpsc.statistics.HpscStatisticMain SPFPerformance ${FILES} > spf_perf_stat_${DATE}_${1}_${2}.result || exit 1
}

DATE=$1
if [ -z ${DATE} ]; then
echo "ERROR: Need date parameter, like 2012-07-05" 1>&2
exit 1
fi

shift
FILES="$@"
if [ -z "${FILES}" ]; then
echo "Using stdin as input..." 1>&2
fi

produce "06" "10"
produce "06" "08"
produce "08" "10"
