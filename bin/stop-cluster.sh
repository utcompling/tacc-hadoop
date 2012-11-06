#!/bin/sh
# execute this on the namenode only

$HADOOP_HOME/bin/stop-all.sh

# the following will delete everthing user created in /hadoop /tmp during the job.
for slavehost in `cat $HADOOP_CONF_DIR/slaves`; do
  echo "ssh $slavehost 'rm -rf /hadoop/$USER'"
  ssh -Y $slavehost "rm -rf /hadoop/$USER"
done
rm -rf /hadoop/$USER

# revert the masters and slaves files to default
echo 'localhost' > $HADOOP_CONF_DIR/masters
echo 'localhost' > $HADOOP_CONF_DIR/slaves
