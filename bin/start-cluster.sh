#!/bin/sh

# render mapred-site.xml.template -> mapred-site.xml
NAMENODE=`hostname -s`
MAP_TASKS_MAX=8
REDUCE_TASKS_MAX=8
TASK_XMX='3g'
cat $HADOOP_CONF_DIR/mapred-site.xml.template | sed \
    -e "s/JOBNODE/$NAMENODE/" \
    -e "s/MAP_TASKS_MAX/$MAP_TASKS_MAX/" \
    -e "s/REDUCE_TASKS_MAX/$REDUCE_TASKS_MAX/" \
    -e "s/USER/$USER/" \
    -e "s/TASK_XMX/$TASK_XMX/" > $HADOOP_CONF_DIR/mapred-site.xml

# render core-site.xml.template -> core-site.xml
cat $HADOOP_CONF_DIR/core-site.xml.template | sed \
  -e "s/NAMENODE/$NAMENODE/g" > $HADOOP_CONF_DIR/core-site.xml

# render hdfs-site.xml.template -> hdfs-site.xml
cat $HADOOP_CONF_DIR/hdfs-site.xml.template | sed \
  -e "s/USER/$USER/g" > $HADOOP_CONF_DIR/hdfs-site.xml

# render hadoop-env.sh
HADOOP_HEAPSIZE=2000
perl -pi -e "s/HADOOP_HEAPSIZE=\d+/HADOOP_HEAPSIZE=$HADOOP_HEAPSIZE/" $HADOOP_CONF_DIR/hadoop-env.sh

# set masters
echo $NAMENODE > $HADOOP_CONF_DIR/masters
# set slaves
HOSTFILE=${PE_HOSTFILE-/tmp/pe_hostfile}
cat $HOSTFILE | tail -n +2 | perl -pe 's/([^.]+).*/\1/' > $HADOOP_CONF_DIR/slaves

$HADOOP_HOME/bin/hadoop namenode -format

$HADOOP_HOME/bin/start-all.sh
