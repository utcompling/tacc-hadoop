package com.utcompling.tacc.scoobi.example

import com.nicta.scoobi.Scoobi._

object WordCount extends ScoobiApp {
  def run() {

    val (inputFile, outputFile) =
      args.toList match {
        case Seq(inputFile, outputFile) => (inputFile, outputFile)
        case _ => sys.error("WordCount requires two arguments: inputFile outputFile")
      }

    val counts: DList[(String, Int)] =
      fromTextFile(inputFile)
        .flatMap(_.toLowerCase.split("\\W+"))
        .map(word => (word, 1))
        .groupByKey
        .combine((a: Int, b: Int) => a + b)

    persist(toTextFile(counts, outputFile))

  }
}
