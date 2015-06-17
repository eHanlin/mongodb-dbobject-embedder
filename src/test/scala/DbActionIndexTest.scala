import scala.collection.JavaConversions._

import tw.com.ehanlin.mde.MongoEmbedder

class DbActionIndexTest extends ReadmeExampleTest {def is = s2"""
  test  $test
"""

  def test = {
    val dsl =
      """
        |@find <db=game coll=game query={ team : "zebra" } projection={ _id : 0 } index=[ { team : 1 } , { _id : 0 , box_score : 1 } ]>
        |[
        |]
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)

    val keyList = mongo.getDB("game").getCollection("game").getIndexInfo().map(_.get("key").toString).toList
    val teamIndex = MObj("team" -> NumInt(1)).toString
    val box_scoreIndex = MObj("_id" -> NumInt(0), "box_score" -> NumInt(1)).toString

    (keyList.contains(teamIndex) must_== true) and (keyList.contains(box_scoreIndex) must_== true)
  }

}
