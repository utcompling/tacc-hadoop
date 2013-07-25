package com.utcompling.tacc.scoobi.example

import com.utcompling.tacc.scoobi.ScoobiJob
import com.nicta.scoobi.Scoobi._

object WordCount extends ScoobiJob {
  def run() {

    val (inputFile, outputFile) =
      args match {
        case Seq(inputFile, outputFile) => (inputFile, outputFile)
        case _ => sys.error("WordCount requires two arguments: inputFile outputFile.  Found: " + args.mkString(" "))
      }

    val counts: DList[(String, Int)] =
      fromTextFile(inputFile)
        .flatMap(_.toLowerCase.split("\\W+"))
        .map(word => (word, 1))
        .groupByKey  // same as .groupBy{ case (word, count) => word }
        .combine(_ + _)  // same as .combine((a: Int, b: Int) => a + b)

    persist(toTextFile(counts, outputFile))

  }
}
