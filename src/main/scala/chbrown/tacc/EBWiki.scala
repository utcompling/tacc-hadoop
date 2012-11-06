package chbrown.tacc

import com.nicta.scoobi.Scoobi._
import com.nicta.scoobi.io.sequence.SequenceInput
import edu.umd.cloud9.collection.wikipedia._
import org.apache.hadoop.io.SequenceFile.{Reader => SequenceFileReader}
import org.apache.hadoop.io.{Writable, IntWritable}

// Objective:
//  cycle through the 40607 lines in titles-1911.txt, and find the closest wikipedia article for each
//  pull over the article text, and well as the title

object WikipediaTitles extends ScoobiApp {
  def run() {
    // args is a global, a Vector or something
    // val (inputFile, outputFile) = args.toList match {
    //   case Seq(inputFile, outputFile) => (inputFile, outputFile)
    //   case _ => sys.error("WordCount requires two arguments: inputFile outputFile")
    // }

    val eb1911Titles = persist(fromTextFile("titles-1911.txt").materialize)
      .map(_.replaceAll("\\W+", "").toLowerCase).toList

    // : DList[(IntWritable, WikipediaPage)]
    val input = SequenceInput.fromSequenceFile[IntWritable, WikipediaPage]("enwiki-latest-blockcompressed")

    val pages = input.filter { case (intwritable, page) =>
      val wikititle = page.getTitle()
      val alphatitle = wikititle.replaceAll("\\W+", "").toLowerCase

      eb1911Titles.contains(alphatitle)
    } map { case (intwritable, page) =>
      val wikititle = page.getTitle()
      val alphatitle = wikititle.replaceAll("\\W+", "").toLowerCase

      alphatitle + "\t" + wikititle + "\t" + page.getContent().replaceAll("[\\r\\n\\t]+", " ")
    }

    // articleContent.set(p.getContent().replaceAll("[\\r\\n]+", " "));

    // 3151953 27725984  Book:Scandium
    // 3940723 36877085  Christopher F. Dixon, Jr., House
    // 1648060 12021711  Genki o Dashite
    // 3715988 34349413  Liugezhuang
    // 3955759 37035340  2012–13 Eredivisie (ice hockey) season
    persist(toTextFile(pages, "title-overlap-1", overwrite=true))
    // val key = reader.getKeyClass().newInstance().asInstanceOf[WritableComparable[IntWritable]]
    // val value = reader.getValueClass().newInstance().asInstanceOf[Writable]

    // val f = new WikipediaForwardIndex();
    // f.loadIndex(new Path("enwiki-latest-index.dat"),
    //   new Path("enwiki-latest-docno.dat"),
    //   fs);

    // fetch docno (always an int, local)
    // val pageA = f.getDocument(1000);
    // println(pageA.getDocid() + ": " + pageA.getTitle());

    // fetch docid (always a string, references Wikipedia-internal ID)
    // val pageB = f.getDocument("1875");
    // println(pageB.getDocid() + ": " + pageB.getTitle());

    // 12/11/04 14:39:22 INFO wikipedia.WikipediaPage: fetching docno 1000: seeking to 11590293 at /user/chbrown/enwiki-latest-blockcompressed/part-00000
    // 12/11/04 14:39:22 INFO wikipedia.WikipediaPage:  docno 1000 fetched in 138ms
    // 2581: Apache HTTP Server
    // 12/11/04 14:39:22 INFO wikipedia.WikipediaPage: fetching docno 662: seeking to 7530024 at /user/chbrown/enwiki-latest-blockcompressed/part-00000
    // 12/11/04 14:39:22 INFO wikipedia.WikipediaPage:  docno 662 fetched in 18ms
    // 1875: Adhemar of Le Puy

    // val counts: DList[(String, Int)] =
    //   fromTextFile(inputFile)
    //     .flatMap(_.toLowerCase.split("\\W+"))
    //     .map(word => (word, 1))
    //     .groupByKey
    //     .combine((a: Int, b: Int) => a + b)

    // toTextFile() is a global, and takes (object_to_write, output_file_or_directory)
    // args(1)

  }
}
