tacc-scoobi
===========

Download Cloudera's Hadoop distribution CDH3 from

    http://www.cloudera.com/hadoop/

Or link to my existing version:

    ~$ ln -s /scratch/01899/dhg1/bin/hadoop-0.20.2-cdh3u5 hadoop

Add the following to `~/.profile_user` and `job.hadoop.new`:

    export HADOOP_HOME=${HOME}/hadoop

Run this project:

    ~/tacc-scoobi$ sbt "run-main dhg.tacc.WordCount"



Setting up the cluster
----------------------

Set up access to helper TACC/Hadoop functions:

    chmod u+x ~/tacc-scoobi/bin/*
    export PATH="~/tacc-scoobi/bin:$PATH"

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

Package a jar:

    sbt assembly
    
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

