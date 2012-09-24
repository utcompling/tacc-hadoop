#!/bin/bash
export HADOOP_CONF_DIR=${HOME}/.hadoop2/conf/

if [ ! -d ${HOME}/.hadoop2 ]; then 
mkdir -p ${HOME}/.hadoop2
fi

if [ ! -d ${HADOOP_CONF_DIR} ]; then 
mkdir -p ${HADOOP_CONF_DIR}
#cp ${HADOOP_HOME}/conf/* ${HADOOP_CONF_DIR}
cp ${HADOOP_HOME}/share/hadoop/common/templates/conf/* ${HADOOP_CONF_DIR}
sed -ie "s|USERNAME|$USER|g" ${HADOOP_CONF_DIR}mapred-site.xml.default 
sed -ie "s|USERNAME|$USER|g" ${HADOOP_CONF_DIR}hdfs-site.xml.default 
cp ${HADOOP_CONF_DIR}hdfs-site.xml.default ${HADOOP_CONF_DIR}hdfs-site.xml
fi


if [ -n "${PE_HOSTFILE+x}" ]; then 
HostList.sh | tail -n+2 > ${HADOOP_CONF_DIR}slaves
namenode=`HostList.sh | head -1`
jobnode=$namenode
sed -e "s|localhost|$jobnode|g" ${HADOOP_CONF_DIR}mapred-site.xml.default > ${HADOOP_CONF_DIR}mapred-site.xml
sed -e "s|localhost|$namenode|g" ${HADOOP_CONF_DIR}core-site.xml.default > ${HADOOP_CONF_DIR}core-site.xml
else
cp ${HADOOP_CONF_DIR}mapred-site.xml.default ${HADOOP_CONF_DIR}mapred-site.xml
cp ${HADOOP_CONF_DIR}core-site.xml.default ${HADOOP_CONF_DIR}core-site.xml
fi

#${HADOOP_HOME}/bin/hadoop namenode -format
hadoop namenode -format
#${HADOOP_HOME}/bin/start-all.sh
start-all.sh

