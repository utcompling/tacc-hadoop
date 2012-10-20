tacc-scoobi
===========

Link to the existing version of Cloudera's Hadoop distribution CDH3:

    ~$ ln -s /scratch/01813/roller/software/lib/hadoop/hadoop-0.20.2-cdh3u2/ hadoop
        
Checkout this project:

    ~$ git clone git@github.com:dhgarrette/tacc-scoobi.git
    ~$ chmod u+x tacc-scoobi/sbt

Clone the lastest version of scoobi and checkout the `chd3` branch. (TODO: fix this to a particular version at some point.)

    ~$ git clone https://github.com/NICTA/scoobi.git
    ~$ cd tacc-scoobi
    ~$ ln -s ../scoobi scoobi
    ~/scoobi$ cd scoobi
    ~/scoobi$ git checkout cdh3

Add the following to `~/.profile_user`:

    export JAVA_HOME=/share/apps/teragrid/jdk1.6.0_19-64bit/
    export HADOOP_HOME=${HOME}/hadoop
    export HADOOP_CONF_DIR=${HOME}/.hadoop2/conf/
    export HADOOP_LOG_DIR=${HOME}/.hadoop2/logs/
    export HADOOP_SLAVES=${HADOOP_CONF_DIR}/slaves
    export HADOOP_PID_DIR=/hadoop/pids
    export TACC_SCOOBI=${HOME}/tacc-scoobi
    export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$TACC_SCOOBI/bin:$PATH

Run this project locally:

    ~$ cd ~/tacc-scoobi
    ~/tacc-scoobi$ echo "this is a test . this test is short ." > example.txt
    ~/tacc-scoobi$ ./sbt "run-main dhg.tacc.WordCount example.txt example.wc"
    ~/tacc-scoobi$ cat example.wc/ch0out0-r-00000
    (a,1)
    (is,2)
    (short,1)
    (test,2)
    (this,2)


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

    vnc_cmd                         # print the command needed to set up a vnc connection
    fix                             # fix the cluster when it is screwed up
    init                            # used to control memory settings across the entire cluster


On the cluster
--------------

    ~$ cd $TACC_SCOOBI

Package a jar:

    ~/tacc-scoobi$ ./sbt assembly
    
Make some data:
    
    ~/tacc-scoobi$ echo "this is a test . this test is short ." > example.txt
    ~/tacc-scoobi$ hadoop fs -put example.txt example.txt

Run the `materialize` example:

    ~/tacc-scoobi$ hadoop jar target/tacc-scoobi-assembly.jar dhg.tacc.WordCountMaterialize example.txt
    
This will produce

    List((a,1), (is,2), (short,1), (test,2), (this,2))

Run the file-output example:

    ~/tacc-scoobi$ hadoop jar target/tacc-scoobi-assembly.jar dhg.tacc.WordCount example.txt example.wc
    ~/tacc-scoobi$ hadoop fs -getmerge example.wc example.wc
    ~/tacc-scoobi$ cat example.wc

This will produce

    (a,1)
    (is,2)
    (short,1)
    (test,2)
    (this,2)
