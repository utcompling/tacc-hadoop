# Tacc-Hadoop

This will get you started using a Hadoop cluster on [TACC](http://www.tacc.utexas.edu/) with [scoobi](https://github.com/NICTA/scoobi), a Scala wrapper of the Hadoop interface. Using Scoobi is not required, and this repository still has some useful tools even if you're working with Ruby: [Wukong](https://github.com/infochimps/wukong), Python: [Dumbo](https://github.com/klbostee/dumbo), or plain old Java: [Hadoop-MapReduce](https://github.com/apache/hadoop-mapreduce).

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

To shutdown your cluster, use the `stop` command FROM THE LOGIN NODE

    login1$ stop   # SHORTHAND FOR: qdel NNNNNN

---

If you have an iThing and want to be notified when your job starts, you can add an environmental variable to `~/tacc-hadoop/hadoop-conf/hadoop-env.sh` or `~/.bash_profile`:

    export PROWL_API_KEY=215f5a87c6e95c5c43dcc8ca74994ce67c6e95c5

This is used in `jobs/hadoop.template`, which `start` renders to `jobs/hadoop` and then submits to the Longhorn queue manager with `qsub`. So if you want to change the message it sends you, look for the PROWL_API_KEY string in `jobs/hadoop.template` and change the fields sent to `curl` however you like.

To get an API key, [register for a free Prowl account](http://www.prowlapp.com/), log in, then go to the [API tab](https://www.prowlapp.com/api_settings.php) to view / create a new API key. Paste that key into one of the files above, instead of the "215f5a8..." example.

The iPhone app is $2.99. You just install, log in with the same username and password you used for the Prowl website, and then everything *just works*. The notifications have no delay, as far as I can tell.

The job template does allow specifying an `-M me@gmail.com` flag, which presumably emails you when the job starts/aborts/ends, but in my experience, it takes about three days for these emails to get all the way from JJ. Pickle to my computer. Not awfully useful, since the maximum reservation duration on TACC is much shorter than 72 hours.

## Running hadoop jobs with scoobi

To run the project locally (for testing with a small amount of data, for example), you need to make sure your configuration is not expecting a cluster:

    stop-cluster.sh

Now put in some sample data:

    cd $TACC_HADOOP
    echo "this is a test . this test is short ." > example.txt
    rm -rf example.wc

And run!

    run compile 
    run local com.utcompling.tacc.scoobi.WordCount example.txt example.wc  
    # ALTERNATIVES
    #   run compile local com.utcompling.tacc.scoobi.WordCount example.txt example.wc
    #   ./sbt "run-main com.utcompling.tacc.scoobi.WordCount example.txt example.wc"

Look at the output:

    cat example.wc/ch*out*-r-*
    > (a,1)
    > (is,2)
    > (short,1)
    > (test,2)
    > (this,2)

---

To run on the cluster, once your cluster is up and running and `hadoop dfsadmin -report` looked promising:

    cd $TACC_HADOOP

Package up a jar of the SBT build. This can take about 2 minutes.

    run jar  # SHORTHAND FOR: ./sbt assembly

Make up some data:

    echo "this is a test . this test is short ." > example.txt
    put example.txt   # SHORTHAND FOR: hadoop fs -put example.txt example.txt

Run the `materialize` example:

    run cluster com.utcompling.tacc.scoobi.WordCountMaterialize example.txt
    # SHORTHAND FOR: hadoop jar target/scala-2.9.2/tacc-hadoop-assembly.jar com.utcompling.tacc.scoobi.WordCountMaterialize example.txt

This will produce:

    List((a,1), (is,2), (short,1), (test,2), (this,2))

Run the file-output example:

    run cluster com.utcompling.tacc.scoobi.WordCount example.txt example.wc
    # SHORTHAND FOR: hadoop jar target/tacc-hadoop-assembly.jar com.utcompling.tacc.scoobi.WordCount example.txt example.wc

And retrieve the results:

    rm -rf example.wc
    get example.wc   # SHORTHAND FOR: hadoop fs -getmerge example.wc example.wc
    cat example.wc

This will produce

    (a,1)
    (is,2)
    (short,1)
    (test,2)
    (this,2)


## Running a hadoop job, start to finish: start cluster, run job, get results, shutdown

    start 10 3
    nn
    cd tacc-hadoop
    run jar
    echo "this is a test . this test is short ." > example.txt
    put example.txt
    run cluster com.utcompling.tacc.scoobi.WordCount example.txt example.wc
    get example.wc
    exit
    stop


## Other useful stuff

Scoobi allows you to pass command-line options to change its behavior.  One useful
option is `inmemory` which will run your job locally through a pipeline backed by
scala collections.  This is a very fast way to test your job that doesn't require
you to actually change the code.  It works on any way that you run your job:

    cd $TACC_HADOOP
    run local com.utcompling.tacc.scoobi.WordCountMaterialize example.txt -- scoobi inmemory
    run cluster com.utcompling.tacc.scoobi.WordCountMaterialize example.txt -- scoobi inmemory

---

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

Some UT graduate students have compiled a useful collection of TACC-related notes, geared towards Windows users who prefer Java and graphical user interfaces. https://sites.google.com/site/tacchadoop/
