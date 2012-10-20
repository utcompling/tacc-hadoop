Tacc-Hadoop
===========

This project exists for two purposes.  First, it is a repository of useful tools for working with TACC.  
And second, it is template for building a project using hadoop and the scala-based hadoop-wrapper scoobi.


Setting up the environment
--------------------------

Link to the existing version of Cloudera's Hadoop distribution CDH3:

    ~$ ln -s /scratch/01813/roller/software/lib/hadoop/hadoop-0.20.2-cdh3u2/ hadoop
        
Checkout this project:

    ~$ git clone git@github.com:dhgarrette/tacc-hadoop.git
    ~$ chmod u+x tacc-hadoop/sbt
    ~$ chmod u+x tacc-hadoop/bin/*

Add the following to `~/.profile_user` (and run them on the command line):

    export JAVA_HOME=/share/apps/teragrid/jdk1.6.0_19-64bit/
    export HADOOP_HOME=${HOME}/hadoop
    export HADOOP_CONF_DIR=${HOME}/.hadoop2/conf/
    export HADOOP_LOG_DIR=${HOME}/.hadoop2/logs/
    export HADOOP_SLAVES=${HADOOP_CONF_DIR}/slaves
    export HADOOP_PID_DIR=/hadoop/pids
    export TACC_HADOOP=${HOME}/tacc-hadoop
    export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$TACC_HADOOP/bin:$PATH

Clone the lastest version of scoobi and checkout the `chd3` branch. (TODO: fix this to a particular version at some point.)

    ~$ git clone https://github.com/NICTA/scoobi.git
    ~$ cd tacc-hadoop
    ~/tacc-hadoop$ ln -s ../scoobi scoobi
    ~/tacc-hadoop$ cd ../scoobi
    ~/scoobi$ git checkout cdh3


Starting a cluster
------------------

Start a cluster:

    $ start NUM_HOURS NUM_MACHINES    # start a cluster
    $ showq                           # show the queue
    $ qstat                           # show the queue information

    $ nn                              # ssh to the namenode
    $ check                           # check the cluster
    $ pi                              # run the pi test job

Other commands:

    vnc_cmd                         # print the command needed to set up a vnc connection
    fix                             # fix the cluster when it is screwed up
    init                            # used to control memory settings across the entire cluster


Running a hadoop job locally
----------------------------

Run this project locally:

    ~$ cd $TACC_HADOOP
    ~/tacc-hadoop$ echo "this is a test . this test is short ." > example.txt
    ~/tacc-hadoop$ rm -rf example.wc
    ~/tacc-hadoop$ ./sbt "run-main dhg.tacc.WordCount example.txt example.wc"
    ~/tacc-hadoop$ cat example.wc/ch0out0-r-00000
    (a,1)
    (is,2)
    (short,1)
    (test,2)
    (this,2)


Running a hadoop job on the cluster
-----------------------------------

    ~$ cd $TACC_HADOOP

Package a jar:

    ~/tacc-hadoop$ ./sbt assembly
    
Make some data:
    
    ~/tacc-hadoop$ echo "this is a test . this test is short ." > example.txt
    ~/tacc-hadoop$ hadoop fs -put example.txt example.txt

Run the `materialize` example:

    ~/tacc-hadoop$ hadoop jar target/tacc-hadoop-assembly.jar dhg.tacc.WordCountMaterialize example.txt
    
This will produce

    List((a,1), (is,2), (short,1), (test,2), (this,2))

Run the file-output example:

    ~/tacc-hadoop$ hadoop jar target/tacc-hadoop-assembly.jar dhg.tacc.WordCount example.txt example.wc
    ~/tacc-hadoop$ rm -rf example.wc
    ~/tacc-hadoop$ hadoop fs -getmerge example.wc example.wc
    ~/tacc-hadoop$ cat example.wc

This will produce

    (a,1)
    (is,2)
    (short,1)
    (test,2)
    (this,2)


With `run` script
-----------------

    ~/tacc-hadoop$ run compile                                                # compile code for local use
    ~/tacc-hadoop$ run local dhg.tacc.WordCountMaterialize example.txt        # run a scala script locally

    ~/tacc-hadoop$ run jar                                                    # jar up the project for use by hadoop on the cluster
    ~/tacc-hadoop$ run cluster dhg.tacc.WordCountMaterialize example.txt      # run a scala script in distributed mode
