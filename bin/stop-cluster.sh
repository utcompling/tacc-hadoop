stop-all.sh
export HADOOP_CONF_DIR=${HOME}/.hadoop2/conf/
#the following will delete everthing user created in /hadoop /tmp during the job. 
for line in `cat ${HADOOP_CONF_DIR}/slaves`;
do
#echo $line
ssh -Y $line 'rm -rf /hadoop/$USER; rm -rf /tmp/*'
ssh -Y $line 'rm -rf /hadoop/foo/$USER; rm -rf /tmp/*'
done
namenode=`HostList.sh | head -1`
ssh -Y $namenode 'rm -rf /hadoop/$USER; rm -rf /tmp/*'
ssh -Y $namenode 'rm -rf /hadoop/foo/$USER; rm -rf /tmp/*'

#revert the slaves file to default 
cp ${HADOOP_CONF_DIR}slaves.default ${HADOOP_CONF_DIR}slaves

