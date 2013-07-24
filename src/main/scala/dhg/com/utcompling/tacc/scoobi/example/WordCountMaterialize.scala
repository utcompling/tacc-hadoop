package com.utcompling.tacc.scoobi.example

import com.nicta.scoobi.Scoobi._

object WordCountMaterialize extends ScoobiApp {
  def run() {

    val inputFile =
      args.toList match {
        case Seq(inputFile) => inputFile
        case _ => sys.error("WordCount requires one argument: inputFile")
      }

    val counts: DList[(String, Int)] =
      fromTextFile(inputFile)
        .flatMap(_.toLowerCase.split("\\W+"))
        .map(word => (word, 1))
        .groupByKey
        .combine((a: Int, b: Int) => a + b)

    val materialized: Iterable[(String, Int)] = persist(counts.materialize)
    println(materialized.toList)

  }
}
