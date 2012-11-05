stop-all.sh

#the following will delete everthing user created in /hadoop /tmp during the job.
for line in `cat $HADOOP_CONF_DIR/slaves`;
  do
  #echo $line
  ssh -Y $line 'rm -rf /hadoop/$USER'
done
namenode=`HostList.sh | head -1`
ssh -Y $namenode 'rm -rf /hadoop/$USER'

#revert the slaves file to default
cd $HADOOP_CONF_DIR
cp slaves.default slaves

