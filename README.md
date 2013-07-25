# TACC-Hadoop

This will get you started using a Hadoop cluster on [TACC](http://www.tacc.utexas.edu/).  This
project contains numerous scripts that make interacting with the TACC cluster much more managable.
The source for these scripts can be found [here](https://github.com/utcompling/tacc-hadoop/tree/master/bin).

The project also contains everything necessary to use the [Scoobi](https://github.com/NICTA/scoobi)
and [Scalding](https://github.com/twitter/scalding) Hadoop wrappers.  However, use of these wrappers 
is not required, and this repository still has some useful tools even if you're working with Ruby: 
[Wukong](https://github.com/infochimps/wukong), Python: [Dumbo](https://github.com/klbostee/dumbo), 
or plain old Java: [Hadoop-MapReduce](https://github.com/apache/hadoop-mapreduce).

## Setting up the environment

You'll need to set up a few things to get yourself access to Longhorn.

1. Get an [account on TACC](https://portal.tacc.utexas.edu/). I'll assume you use the username `taccname`.
2. Ask [Weijia Xu](http://www.tacc.utexas.edu/staff/weijia-xu) to add you to the `hadoop` group on Longhorn.
3. On your local machine, add a shortcut to the longhorn login node, which I will name `tacc`.
    `dig a longhorn.tacc.utexas.edu` will show you the IP address in case the one below does not work.

        echo '129.114.50.211 tacc' >> /etc/hosts
        ssh taccname@tacc

4. Great, now you're on TACC, on the login node. Change your default shell to bash (if it's not bash already):

        chsh -s /bin/bash

5. Clone this repository to your home folder:

        cd ~
        module load git
        git clone --recursive https://github.com/utcompling/tacc-hadoop.git
        echo '. ~/tacc-hadoop/hadoop-conf/hadoop-env.sh' >> ~/.bash_profile

5. Now you can either log out and back in, or run this manually, just this once:

        source ~/tacc-hadoop/hadoop-conf/hadoop-env.sh


## Starting a cluster

Reserve a **3-machine cluster** for **2 hours**. JobName can be anything without spaces and is *not required*:

    start 3 2 JobName

It may take a while to fit that in. To check on how many other people are using the cluster and see who else is waiting in the queue:

    showq

Your job has your username in it, as well as the first 10 characters of the `JobName` you used above.
You can also check on the status of just *your* request:

    qstat

The output of that program will show you a hostname under the `queue` column when your cluster is ready. That hostname is the `namenode` of your cluster, and is where you will do most of your work from.
You can ssh to that node, or simply call

    nn

Once your cluster is running and you've ssh'ed over, you can run the `pi` test job, which simply calculates Pi.

    # Calculate Pi with 10 maps and 1000 samples
    hadoop jar $HADOOP_HOME/hadoop-*examples*.jar pi 10 1000

Check on the general health of your cluster, to make sure HDFS is up and running:

    hadoop dfsadmin -report

If you have any trouble, or see anything weird in the output, you can stop and re-start the cluster:

    stop-cluster.sh
    start-cluster.sh

When you are finished work, please always shut down your cluster to free up the nodes for others
to use.  The `stop` command can be run *FROM THE LOGIN NODE* to do this (you may need need to 
`exit` the namenode to get back to the login node).

    stop


## Running jobs

[Scoobi](https://github.com/NICTA/scoobi) and [Scalding](https://github.com/twitter/scalding)
are Scala-based frameworks that provides a very nice interfaces to Hadoop.  Both are already 
configured within this project and there are example jobs for each.  Below is a demonstration 
of how to run Hadoop jobs using examples featuring each of these frameworks.

These instructions make heavy use of a convenient `run` script for building and running jobs. 
If you are curious about the underlying commands, feel free to inspect the script's 
[source](https://github.com/utcompling/tacc-hadoop/blob/master/run).


### Running jobs on the cluster

Start a cluster and log into the name node:

    start 3 2
    nn
    cd $TACC_HADOOP
    
Build the full project jar so that it can be (automatically) uploaded to the cluster.

    run jar
    
Create some data and put it into the Hadoop filesystem

    echo "this is a test . this test is short ." > example.txt
    put example.txt

Run the [Scoobi word count](https://github.com/utcompling/tacc-hadoop/blob/master/src/main/scala/dhg/com/utcompling/tacc/scoobi/example/WordCount.scala), 
[Scalding word count](https://github.com/utcompling/tacc-hadoop/blob/master/src/main/scala/dhg/com/utcompling/tacc/scalding/example/WordCount.scala),
or [Scalding word count fields](https://github.com/utcompling/tacc-hadoop/blob/master/src/main/scala/dhg/com/utcompling/tacc/scalding/example/WordCountFields.scala) 
example jobs.  There are two Scalding example jobs because Scalding has two different APIs: the 
[Type Safe](https://github.com/twitter/scalding/wiki/Type-safe-api-reference) API, which looks
like normal Scala (similar to Scoobi), and the original [Fields](https://github.com/twitter/scalding/wiki/Fields-based-API-Reference) 
API.

    run cluster com.utcompling.tacc.scoobi.example.WordCount example.txt example.wc
    run cluster com.utcompling.tacc.scalding.example.WordCount example.txt example.wc
    run cluster com.utcompling.tacc.scalding.example.WordCountFields example.txt example.wc

And retrieve the results:

    get example.wc            # SHORTHAND FOR: hadoop fs -getmerge example.wc example.wc
    cat example.wc
    > (a,1)
    > (is,2)
    > (short,1)
    > (test,2)
    > (this,2)

There is a second Scoobi example, [word count materialize](https://github.com/utcompling/tacc-hadoop/blob/master/src/main/scala/dhg/com/utcompling/tacc/scoobi/example/WordCountMaterialize.scala), 
demonstrating the ability to pull the result of a Hadoop job into memory.

    run cluster com.utcompling.tacc.scoobi.example.WordCountMaterialize example.txt
    > List((a,1), (is,2), (short,1), (test,2), (this,2))

Finally, please stop the cluster when you are done working.

    stop
    
NOTE: If you run multiple tests you will have to manually delete output files from the local
and remote filesystems:

    rm -rf example.wc
    hadoop fs -rmr example.wc


### Running jobs in local mode

The `cluster` keyword used above directs the script to run distributed on the cluster. 
Replacing this with `local` will run the job very quickly in memory, which is useful 
for testing on small amounts of data.

    run compile
    run local com.utcompling.tacc.scoobi.example.WordCount example.txt example.wc
    run local com.utcompling.tacc.scalding.example.WordCount example.txt example.wc
    run local com.utcompling.tacc.scoobi.example.WordCountMaterialize example.txt
    run local com.utcompling.tacc.scalding.example.WordCountFields example.txt example.wc


## Other useful stuff

By default, this repository uses another user's Hadoop package, which is at:

    /scratch/01813/roller/software/lib/hadoop/hadoop-0.20.2-cdh3u2/

You can use your own Hadoop, for example, the newer `hadoop-0.20.2-cdh3u5`, which you can get from http://archive.cloudera.com/cdh/3/. Simply download, unpackage, and link:

    cd $TACC_HADOOP
    rm hadoop
    wget http://archive.cloudera.com/cdh/3/hadoop-0.20.2-cdh3u5.tar.gz
    tar xzf hadoop-0.20.2-cdh3u5.tar.gz
    ln -s hadoop-0.20.2-cdh3u5 hadoop


## Miscellaneous cluster stuff

While you can't `sudo` on TACC to install system packages, there are some other modules you can load from the TACC system. A recent Python is one:

    module load python/2.7.1-epd

Oddly, you can't use some of them from your cluster nodes. `module load git` doesn't work, for example. I've built `git` and `tmux` packages and put them in a `~/local` folder with `~/local/bin` on my `PATH`, which is working out well.

---

If you have an iThing and want to be notified when your job starts, you can add an environmental variable to `~/tacc-hadoop/hadoop-conf/hadoop-env.sh` or `~/.bash_profile`:

    export PROWL_API_KEY=215f5a87c6e95c5c43dcc8ca74994ce67c6e95c5

This is used in `jobs/hadoop.template`, which `start` renders to `jobs/hadoop` and then submits to the Longhorn queue manager with `qsub`. So if you want to change the message it sends you, look for the PROWL_API_KEY string in `jobs/hadoop.template` and change the fields sent to `curl` however you like.

To get an API key, [register for a free Prowl account](http://www.prowlapp.com/), log in, then go to the [API tab](https://www.prowlapp.com/api_settings.php) to view / create a new API key. Paste that key into one of the files above, instead of the "215f5a8..." example.

The iPhone app is $2.99. You just install, log in with the same username and password you used for the Prowl website, and then everything *just works*. The notifications have no delay, as far as I can tell.

The job template does allow specifying an `-M me@gmail.com` flag, which presumably emails you when the job starts/aborts/ends, but in my experience, it takes about three days for these emails to get all the way from JJ. Pickle to my computer. Not awfully useful, since the maximum reservation duration on TACC is much shorter than 72 hours.

---

Some UT graduate students have compiled a useful collection of TACC-related notes, geared towards Windows users who prefer Java and graphical user interfaces. https://sites.google.com/site/tacchadoop/


## Test Commands to verify examples are working:

    run compile

    rm -rf example.wc; run local com.utcompling.tacc.scoobi.example.WordCount example.txt example.wc
    cat example.wc/ch*-r-*

    rm -rf example.wc; run local com.utcompling.tacc.scalding.example.WordCount example.txt example.wc
    cat example.wc

    run local com.utcompling.tacc.scoobi.example.WordCountMaterialize example.txt

    run jar

    hdfs -rmr example.wc; run cluster com.utcompling.tacc.scoobi.example.WordCount example.txt example.wc
    rm -rf example.wc; get example.wc; cat example.wc

    hdfs -rmr example.wc; run cluster com.utcompling.tacc.scalding.example.WordCount example.txt example.wc
    rm -rf example.wc; get example.wc; cat example.wc

    run cluster com.utcompling.tacc.scoobi.example.WordCountMaterialize example.txt

