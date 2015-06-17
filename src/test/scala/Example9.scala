import my.action.FindLimit2
import tw.com.ehanlin.mde.MongoEmbedder

class Example9 extends ReadmeExampleTest {def is = s2"""
  Example9  $example9
"""

  def example9 = {

    MongoEmbedder.dslParser.registerAction("findLimit2", classOf[FindLimit2])

    val dsl =
      """
        |@findLimit2 <db=user coll=user query={ height : { $gte : 210 } }>
        |[
        |  @findOneById [db=info coll=postal_code projection={ _id : 0 , name : 1 }]
        |  postal_code
        |]
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result,
      """
        |[
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef76"},
        |    "name":"Kirk",
        |    "postal_code":[{"name":"中正區"}],
        |    "height":220,
        |    "friends":[
        |      {"$oid":"557e58727a8ea2a9dfe2ef74"},
        |      {"$oid":"557e58727a8ea2a9dfe2ef77"},
        |      {"$oid":"557e58727a8ea2a9dfe2ef7a"},
        |      {"$oid":"557e58727a8ea2a9dfe2ef7e"}]
        |  },
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef77"},
        |    "name":"Mick",
        |    "postal_code":[{"name":"中正區"}],
        |    "height":211,
        |    "friends":[
        |      {"$oid":"557e58727a8ea2a9dfe2ef76"}]
        |  }
        |]
      """.stripMargin)
  }

}
