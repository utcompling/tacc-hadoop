package chbrown.tacc

import com.nicta.scoobi.Scoobi._
import com.nicta.scoobi.io.sequence.SequenceInput
import edu.umd.cloud9.collection.wikipedia._
import org.apache.hadoop.io.SequenceFile.{Reader => SequenceFileReader}
import org.apache.hadoop.io.{Writable, IntWritable}

object EBWiki extends ScoobiApp {
  def run() {

    val enwiki_selection = persist(fromTextFile("enwiki-12k.tsv").materialize).toList
    val enwiki_selection_titles: List[String] = enwiki_selection.map(_.split("\t")(0))
    // val eb_selection = persist(fromTextFile("eb-all.tsv").materialize).toList
    // each file looks like:
    // lowercasetitle TAB Full title (hopefully no tabs) TAB article text (only normal whitespace)

    // goal:
    //   find the articles in eb that are also in enwiki (should = 12902)

    var eb_selection_dlist = fromTextFile("eb-all.tsv").filter { case line =>
      val title = line.split("\t")(0)
      enwiki_selection_titles.contains(title)
    }

    persist(toTextFile(eb_selection_dlist, "eb-12k-maybe.tsv"))

    // : DList[(IntWritable, WikipediaPage)]
    // val input = SequenceInput.fromSequenceFile[IntWritable, WikipediaPage]("enwiki-latest-blockcompressed")
    // val pages = input.filter { case (intwritable, page) =>
    //   val wikititle = page.getTitle()
    //   val alphatitle = wikititle.replaceAll("\\W+", "").toLowerCase

    //   eb1911Titles.contains(alphatitle)
    // } map { case (intwritable, page) =>
    //   val wikititle = page.getTitle()
    //   val alphatitle = wikititle.replaceAll("\\W+", "").toLowerCase

    //   alphatitle + "\t" + wikititle + "\t" + page.getContent().replaceAll("[\\r\\n\\t]+", " ")
    // }

    // articleContent.set(p.getContent().replaceAll("[\\r\\n]+", " "));

    // 3151953 27725984  Book:Scandium
    // 3940723 36877085  Christopher F. Dixon, Jr., House
    // 1648060 12021711  Genki o Dashite
    // 3715988 34349413  Liugezhuang
    // 3955759 37035340  2012–13 Eredivisie (ice hockey) season
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
