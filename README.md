tacc-scoobi
===========

Download Cloudera's Hadoop distribution CDH3 from

    http://www.cloudera.com/hadoop/

Or link to my existing version:

    ~$ ln -s /scratch/01899/dhg1/bin/hadoop-0.20.2-cdh3u5 hadoop

Add the following to `~/.profile_user` and `job.hadoop.new`:

    export HADOOP_HOME=${HOME}/hadoop

Run this project:

    ~/tacc-scoobi$ sh bin/sbt "run-main dhg.tacc.WordCount"

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

