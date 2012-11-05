export JAVA_HOME=/share/apps/teragrid/jdk1.6.0_19-64bit/
export TACC_HADOOP=$HOME/tacc-hadoop
export HADOOP_HOME=$TACC_HADOOP/hadoop
export HADOOP_CONF_DIR=$TACC_HADOOP/hadoop-conf
export HADOOP_LOG_DIR=$TACC_HADOOP/hadoop-logs
export HADOOP_SLAVES=$HADOOP_CONF_DIR/slaves
export HADOOP_PID_DIR=/hadoop/$USER/pids
# export HADOOP_CLASSPATH=
# The maximum amount of heap to use, in MB. Default is 1000.
# export HADOOP_HEAPSIZE=2000
export HADOOP_HEAPSIZE=6000
export HADOOP_NAMENODE_OPTS="-XX:+UseParallelGC -Dcom.sun.management.jmxremote $HADOOP_NAMENODE_OPTS"
export HADOOP_SECONDARYNAMENODE_OPTS="-Dcom.sun.management.jmxremote $HADOOP_SECONDARYNAMENODE_OPTS"
export HADOOP_DATANODE_OPTS="-Dcom.sun.management.jmxremote $HADOOP_DATANODE_OPTS"
export HADOOP_BALANCER_OPTS="-Dcom.sun.management.jmxremote $HADOOP_BALANCER_OPTS"
export HADOOP_JOBTRACKER_OPTS="-Dcom.sun.management.jmxremote $HADOOP_JOBTRACKER_OPTS"
export TMP=/hadoop/tmp/
export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$JAVA_HOME/bin:$TACC_HADOOP/bin:$PATH

# `/usr/local/etc/taccinfo` is what you see after /etc/motd when you login
# cat /usr/local/etc/taccinfo to see why the two lines below work
export TACC_PROJECT_ID=`grep $USER /share/sge6.2/default/acct/map/projectuser.map | awk '{print $2}'`
export TACC_PROJECT_NAME=`grep $TACC_PROJECT_ID /share/sge6.2/default/acct/map/project.map | awk '{print $1}'`
