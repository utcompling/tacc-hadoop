package com.utcompling.tacc.scoobi

import com.nicta.scoobi.Scoobi._

trait ScoobiJob extends ScoobiApp {
  override def main(args: Array[String]) {
    if (args.contains("--TACC-LOCAL")) {
      super.main(args.filterNot("--TACC-LOCAL" ==) ++ Array("--", "scoobi", "inmemory"))
    }
    else {
      super.main(args)
    }
  }

  final def run() {
    runJob(args.toList)
  }

  def runJob(args: List[String])

}
