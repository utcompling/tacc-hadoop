#!/bin/bash
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

$HADOOP_HOME/bin/hadoop namenode -format
$HADOOP_HOME/bin/start-all.sh
