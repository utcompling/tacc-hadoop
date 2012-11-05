Tacc-Hadoop
===========

This project exists for two purposes.  First, it is a repository of useful tools for working with TACC.  
And second, it is template for building a project using hadoop and the scala-based hadoop-wrapper scoobi.


Setting up the environment
--------------------------

Change your default shell to bash (if it's not bash already):

    ~$ chsh -s /bin/bash

Put Git on your path:

    ~$ module load git

Link to the existing version of Cloudera's Hadoop distribution CDH3:

    ~$ ln -s /scratch/01813/roller/software/lib/hadoop/hadoop-0.20.2-cdh3u2/ hadoop

Make the `.hadoop2` directory:

    ~$ mkdir .hadoop2

Download the following file onto your computer: (TODO: `wget` isn't working for me for this file)

    https://sites.google.com/site/tacchadoop/home/-fatal-error-core-site-xml-1-1-premature-end-of-file/hadoop2conf.tar.gz

Upload the file from your computer to your tacc account and extract it:

    <your computer>$ scp <filepath>/hadoop2conf.tar.gz <username>@longhorn.tacc.utexas.edu:.hadoop2

Extract the hadoop configuration.  (Create a `.hadoop2` directory if it does not already exist).

    ~$ cd .hadoop2
    ~$ tar zxvf hadoop2conf.tar.gz

Clone this project:

    ~$ git clone https://github.com/dhgarrette/tacc-hadoop.git
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
    export PATH=$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$JAVA_HOME/bin:$TACC_HADOOP/bin:$PATH

Also add an environment variable for your project name.  For example, if you are part of
the TeXIT project, then you would add the following to `~/.profile_user`:

    export TACC_PROJECT_NAME=TeXIT

Clone the lastest version of scoobi and checkout the `chd3` branch. (TODO: fix this to a particular version at some point.)

    ~$ git clone https://github.com/NICTA/scoobi.git
    ~$ cd tacc-hadoop
    ~/tacc-hadoop$ ln -s ../scoobi scoobi
    ~/tacc-hadoop$ cd ../scoobi
    ~/scoobi$ git checkout cdh3

If you haven't done it before, then you will also have to run the command `vncpasswd` before you can schedule jobs.

    ~$ vncpasswd


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


Troubleshooting for the first time you use Hadoop on TACC
---------------------------------------------------------

1. When you run "check", you may get an error like:
 
        $ check
        [Fatal Error] core-site.xml:1:1: Premature end of file.
        <more error output>

  If this happens, follow the instructions on this page:

    https://sites.google.com/site/tacchadoop/home/-fatal-error-core-site-xml-1-1-premature-end-of-file


2. If you request just one machine, you won't be able to run Hadoop jobs. (Though this is of course 
still useful for running jobs that just require a single machine.)


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


Other useful stuff
------------------

Scoobi allows you to pass command-line options to change its behavior.  One useful
option is `inmemory` which will run your job locally through a pipeline backed by
scala collections.  This is a very fast way to test your job that doesn't require
you to actually change the code.  It works on any way that you run your job:

    ~/tacc-hadoop$ ./sbt "run-main dhg.tacc.WordCountMaterialize example.txt -- scoobi inmemory"
    ~/tacc-hadoop$ run local dhg.tacc.WordCountMaterialize example.txt -- scoobi inmemory
    ~/tacc-hadoop$ run cluster dhg.tacc.WordCountMaterialize example.txt -- scoobi inmemory
