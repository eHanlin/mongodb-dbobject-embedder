import com.mongodb.BasicDBObject
import tw.com.ehanlin.mde.MongoEmbedder

class FindTest extends ReadmeExampleTest {def is = s2"""
  test  $test
"""

  def test = {
    val dsl =
      """
        |@find <db=user coll=user projection={ name : 1 , height : 1} sort={ height : -1 } skip=2 limit=@.limit>
        |[
        |]
      """.stripMargin
    val result = MongoEmbedder.instance.embed(new BasicDBObject("limit",2), dsl)
    mustString(result,
      """
        |[
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef7c"},
        |    "name":"Toby",
        |    "height":214
        |  },
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef77"},
        |    "name":"Mick",
        |    "height":211
        |  }
        |]
      """.stripMargin)
  }

}
