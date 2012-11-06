# Tacc-Hadoop

This project exists for two purposes.  First, it is a repository of useful tools for working with TACC.
And second, it is template for building a project using hadoop and the scala-based hadoop-wrapper scoobi.

## Setting up the environment

On your local machine, add a shortcut to the longhorn login node, which will be named `tacc`. `dig a longhorn.tacc.utexas.edu` will show you the IP address.

    echo '129.114.50.211 tacc' >> /etc/hosts
    ssh tacc
    # if your local username and your TACC username differ:
    # ssh whoamiontacc@tacc

Get this repository:

    module load git
    git clone --recursive https://github.com/chbrown/tacc-hadoop.git
    echo '. ~/tacc-hadoop/hadoop-conf/hadoop-env.sh' >> ~/.bashrc

Now log out and back in, or simply `source ~/tacc-hadoop/hadoop-conf/hadoop-env.sh`.

Other useful modules: `module load python/2.7.1-epd`

## Cluster commands

Start a cluster:

    start 10 5   # reserve a 5-machine cluster for 10 hours
    showq        # show the queue
    qstat        # show the queue information
    nn           # ssh to the namenode

    # run the pi test job
    hadoop jar $HADOOP_HOME/hadoop-*examples*.jar pi 10 1000

    # check the cluster
    hadoop dfsadmin -report

If you request just one machine, you won't be able to run Hadoop jobs. (Though this is of course still useful for running jobs that just require a single machine.)

Other commands:

    vnc_cmd                         # print the command needed to set up a vnc connection

    # fix the cluster when it is screwed up
    stop-cluster.sh
    start-cluster.sh

    init                            # used to control memory settings across the entire cluster

## Troubleshooting

### .Xauthority

If you get errors like the following in your HadoopJob.out

    Warning: untrusted X11 forwarding setup failed: xauth key data not generated
    c201-116: Warning: No xauth data; using fake authentication data for X11 forwarding.
    c201-116: /usr/bin/xauth:  error in locking authority file /home/01613/chbrown/.Xauthority

Simply `rm ~/.Xauthority`

### VNC

For the Mac, Chicken is a free VNC client. Here's a link to the latest installer from Sourceforge, relinked to a github repository: [Chicken-2.2b2.dmg](https://github.com/downloads/chbrown/chicken/Chicken-2.2b2.dmg).

## Running a hadoop job locally

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


If you haven't used Longhorn/TACC before, you'll need to set up a VNC password, even if you aren't going to use VNC. Passwords are truncated to 8 characters.

    vncpasswd


# /home/01683/benwing/qsub
