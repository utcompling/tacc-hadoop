package com.utcompling.tacc.scalding.example

import com.utcompling.tacc.scalding.ScaldingJob
import com.twitter.scalding._

object WordCount extends ScaldingJob {
  def jobClass = classOf[WordCount]
}

class WordCount(args: Args) extends Job(args) {
  val (inputFile, outputFile) =
    args.positional match {
      case Seq(inputFile, outputFile) => (inputFile, outputFile)
      case _ => sys.error("WordCount requires two positional arguments: inputFile outputFile.  Found: " + args.m)
    }

  TextLine(inputFile)
    .flatMap('line -> 'word) { line: String => line.split("\\W+") }
    .groupBy('word) { group => group.size }
    .write(Tsv(outputFile))
}
