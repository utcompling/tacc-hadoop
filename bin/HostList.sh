#!/bin/bash
#
#-----------------------------------------------------------------------------
# generate the host file
#-----------------------------------------------------------------------------

pe_hostfile=$PE_HOSTFILE
pe_ppn=`echo $PE | sed -e 's/way//g;'`

# Don't be concerned, the following two lines are correct!
IFS='
'
for l in `cat $pe_hostfile`; do
    h=`echo $l| sed -e 's/^\([ci][0-9]\+-[0-9]\+.longhorn.tacc.utexas.edu\) \+\([0-9]\+\).*/\1/g' | sort -n`
    N=`expr $N + 1`
    h=$(echo $h | awk 'BEGIN{FS="."}{ print $1}')
    echo "$h" 
done

unset IFS

