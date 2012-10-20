package dhg.tacc

import com.nicta.scoobi.Scoobi._

object WordCountMaterialize extends ScoobiApp {
  def run() {

    val List(inputFile) = args.toList

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

