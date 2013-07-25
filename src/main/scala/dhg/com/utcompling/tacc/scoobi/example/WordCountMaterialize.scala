package com.utcompling.tacc.scoobi.example

import com.utcompling.tacc.scoobi.ScoobiJob
import com.nicta.scoobi.Scoobi._

object WordCountMaterialize extends ScoobiJob {
  def run() {

    val inputFile =
      args match {
        case Seq(inputFile) => inputFile
        case _ => sys.error("WordCountMaterialize requires one argument: inputFile.  Found: " + args.mkString(" "))
      }

    val counts: DList[(String, Int)] =
      fromTextFile(inputFile)
        .flatMap(_.toLowerCase.split("\\W+"))
        .map(word => (word, 1))
        .groupByKey  // same as .groupBy{ case (word, count) => word }.map{ case (word, wordCountPairs) => (word, wordCountPairs.map(_._2)) }
        .combine(_ + _)  // same as .combine((a: Int, b: Int) => a + b)

    val materialized: Iterable[(String, Int)] = persist(counts.materialize)
    println(materialized.toList)

  }
}
