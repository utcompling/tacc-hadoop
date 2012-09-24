package dhg.tacc

import com.nicta.scoobi.Scoobi._

object WordCount extends ScoobiApp {
  def run() {

    val List(inputFile, outputFile) = args.toList

    val counts = 
      fromTextFile(inputFile)
        .flatMap(_.toLowerCase.split("\\W+"))
        .map(word => (word, 1))
        .groupByKey
        .combine((a: Int, b: Int) => a + b)
    
    persist(toTextFile(counts, outputFile))

  }
}

