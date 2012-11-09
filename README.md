# Tacc-Hadoop

This will get you started using a Hadoop cluster on [TACC](http://www.tacc.utexas.edu/) with [scoobi](https://github.com/NICTA/scoobi), a Scala wrapper of the Hadoop interface. Using Scoobi is not required, and this repository still has some useful tools even if you're working with Ruby: [Wukong](https://github.com/infochimps/wukong), Python: [Dumbo](https://github.com/klbostee/dumbo), or plain old Java: [Hadoop-MapReduce](https://github.com/apache/hadoop-mapreduce).

## Setting up the environment

You'll need to set up a few things to get yourself access to Longhorn.

1. Get an [account on TACC](https://portal.tacc.utexas.edu/). I'll assume you use the username `taccname`.
2. Ask [Weijia Xu](http://www.tacc.utexas.edu/staff/weijia-xu) to add you to the `hadoop` group on Longhorn.
3. On your local machine, add a shortcut to the longhorn login node, which I will name `tacc`.
    `dig a longhorn.tacc.utexas.edu` will show you the IP address in case the one below does not work.

```sh
echo '129.114.50.211 tacc' >> /etc/hosts
ssh taccname@tacc
```

4. Great, now you're on TACC, on the login node. Clone this repository to your home folder:

```sh
cd ~
module load git
git clone --recursive https://github.com/chbrown/tacc-hadoop.git
echo '. ~/tacc-hadoop/hadoop-conf/hadoop-env.sh' >> ~/.bash_profile
```

5. Now you can either log out and back in, or run this manually, just this once:

    source ~/tacc-hadoop/hadoop-conf/hadoop-env.sh

## Starting a cluster

Reserve a 5-machine cluster for 10 hours. JobName can be anything without spaces and is not required:

    start 10 5 JobName

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

## Running hadoop jobs with scoobi

To run the project locally (for testing with a small amount of data, for example), you need to make sure your configuration is not expecting a cluster:

    stop-cluster.sh

Now put in some sample data:

    cd $TACC_HADOOP
    echo "this is a test . this test is short ." > example.txt
    rm -rf example.wc

And run!

    ./sbt "run-main dhg.tacc.WordCount example.txt example.wc"

Look at the output:

    cat example.wc/ch0out0-r-00000
    > (a,1)
    > (is,2)
    > (short,1)
    > (test,2)
    > (this,2)


---

To run on the cluster, once your cluster is up and running and `hadoop dfsadmin -report` looked promising:

    cd $TACC_HADOOP

Package up a jar of the SBT build. This can take about 2 minutes.

    ./sbt assembly

Make up some data:

    echo "this is a test . this test is short ." > example.txt
    hadoop fs -put example.txt example.txt

There is a helper command `put` that is a simple shortcut for `hadoop -fs put`. The above could be replicated with:

    put example.txt

Run the `materialize` example:

    run cluster dhg.tacc.WordCountMaterialize example.txt

This will produce:

    List((a,1), (is,2), (short,1), (test,2), (this,2))

Run the file-output example:

    hadoop jar target/tacc-hadoop-assembly.jar dhg.tacc.WordCount example.txt example.wc
    rm -rf example.wc
    hadoop fs -getmerge example.wc example.wc
    cat example.wc

This will produce

    (a,1)
    (is,2)
    (short,1)
    (test,2)
    (this,2)

## Other useful stuff

Scoobi allows you to pass command-line options to change its behavior.  One useful
option is `inmemory` which will run your job locally through a pipeline backed by
scala collections.  This is a very fast way to test your job that doesn't require
you to actually change the code.  It works on any way that you run your job:

    cd $TACC_HADOOP
    ./sbt "run-main dhg.tacc.WordCountMaterialize example.txt -- scoobi inmemory"
    run local dhg.tacc.WordCountMaterialize example.txt -- scoobi inmemory
    run cluster dhg.tacc.WordCountMaterialize example.txt -- scoobi inmemory

---

By default, this repository uses another user's Hadoop package, which is at:

    /scratch/01813/roller/software/lib/hadoop/hadoop-0.20.2-cdh3u2/

You can use your own Hadoop, for example, the newer `hadoop-0.20.2-cdh3u5`, which you can get from [http://archive.cloudera.com/cdh/3/]. Simply download, unpackage, and link:

    cd $TACC_HADOOP
    rm hadoop
    wget http://archive.cloudera.com/cdh/3/hadoop-0.20.2-cdh3u5.tar.gz
    tar xzf hadoop-0.20.2-cdh3u5.tar.gz
    ln -s hadoop-0.20.2-cdh3u5 hadoop

## Miscellaneous cluster stuff

While you can't `sudo` on TACC to install system packages, there are some other modules you can load from the TACC system. A recent Python is one:

    module load python/2.7.1-epd

Oddly, you can't use some of them from your cluster nodes. `module load git` doesn't work, for example. I've built `git` and `tmux` packages and put them in a `~/local` folder with `~/local/bin` on my `PATH`

---

Some UT graduate students have compiled a useful collection of TACC-related notes, geared towards Windows users who prefer Java and graphical user interfaces. [https://sites.google.com/site/tacchadoop/]
