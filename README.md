tacc-scoobi
===========

Link to my existing version of Cloudera's Hadoop distribution CDH4:

    ~$ ln -s /scratch/01899/dhg1/bin/hadoop-2.0.0-cdh4.0.1 hadoop
    
Or download your own copy:

    ~$ wget http://archive.cloudera.com/cdh4/cdh/4/hadoop-2.0.0-cdh4.0.1.tar.gz
    ~$ tar zxvf hadoop-2.0.0-cdh4.0.1.tar.gz
    
Checkout this project:

    ~$ git clone git@github.com:dhgarrette/tacc-scoobi.git
    ~$ chmod u+x tacc-scoobi/sbt

Checkout the lastest version of scoobi (TODO: fix this to a particular version at some point):

    ~$ git clone https://github.com/NICTA/scoobi.git
    ~$ cd tacc-scoobi
    ~$ ln -s ../scoobi scoobi

Add the following to `~/.profile_user`:

    export JAVA_HOME=/share/apps/teragrid/jdk1.6.0_19-64bit/
    export HADOOP_HOME=${HOME}/hadoop
    export HADOOP_CONF_DIR=${HOME}/.hadoop2/conf/
    export HADOOP_LOG_DIR=${HOME}/.hadoop2/logs/
    export HADOOP_SLAVES=${HADOOP_CONF_DIR}/slaves
    export HADOOP_PID_DIR=/hadoop/pids
    export TACC_SCOOBI=${HOME}/tacc-scoobi
    export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$TACC_SCOOBI/bin:$PATH

Run this project:

    ~/tacc-scoobi$ ./sbt "run-main dhg.tacc.WordCount"


Setting up the cluster
----------------------

Set up access to helper TACC/Hadoop functions:

    chmod u+x $TACC_SCOOBI/bin/*

Start a cluster:

    start NUM_HOURS NUM_MACHINES    # start a cluster
    showq                           # show the queue
    qstat                           # show the queue information

    nn                              # ssh to the namenode
    check                           # check the cluster
    pi                              # run the pi test job

Other commands:

    init                            # used to control memory settings
    vnc_cmd                         # print the command needed to set up a vnc connection
    fix                             # fix the cluster when it is screwed up


On the cluster
--------------

    cd $TACC_SCOOBI

Package a jar:

    ./sbt assembly
    
Make some data:
    
    echo "this is a test . this test is short ." > example.txt
    hadoop fs -put example.txt example.txt

Run the `materialize` example:

    hadoop jar target/tacc-scoobi-assembly.jar dhg.tacc.WordCountMaterialize example.txt
    
This will produce

    List((a,1), (is,2), (short,1), (test,2), (this,2))

Run the file-output example:

    hadoop jar target/tacc-scoobi-assembly.jar dhg.tacc.WordCount example.txt example.wc
    hadoop fs -getmerge example.wc example.wc
    cat example.wc

This will produce

	a	1
	is	2
	short	1
	test	2
	this	2

