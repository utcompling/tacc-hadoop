package com.utcompling.tacc.scalding

import org.apache.hadoop
import com.twitter.scalding._

trait ScaldingJob {
  def main(args: Array[String]) {
    val newArgs = 
      if (args.contains("--TACC-LOCAL")) {
        args.filterNot("--TACC-LOCAL" ==) ++ Array("--local")
      }
      else {
        args ++ Array("--hdfs")
      }   
   
    //hadoop.util.ToolRunner.run(new hadoop.conf.Configuration, new Tool, newArgs)

    Tool.main(Array(jobClass.getName) ++ newArgs)
  }

  def jobClass: Class[_ <: Job]
}
