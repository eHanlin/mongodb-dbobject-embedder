import my.action.EmbedPostalCode
import tw.com.ehanlin.mde.MongoEmbedder

class Example8 extends ReadmeExampleTest {def is = s2"""
  Example8  $example8
"""

  def example8 = {

    MongoEmbedder.dslParser.registerAction("embedPostalCode", classOf[EmbedPostalCode])

    val dsl =
      """
        |@embedPostalCode []
        |[
        |  @findOneById [db=user coll=user projection={ _id : 0 , name : 1 }]
        |  friends
        |]
      """.stripMargin

    val result = MongoEmbedder.instance.embed(MList("557e58727a8ea2a9dfe2ef76","557e58727a8ea2a9dfe2ef7a"), dsl)
    mustString(result,
      """
        |[
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef76"},
        |    "name":"Kirk",
        |    "postal_code":{
        |      "country":"TW",
        |      "city":{"country":{"name":"臺灣"},"name":"臺北市"},
        |      "code":"100",
        |      "name":"中正區"},
        |    "height":220,
        |    "friends":[
        |      {"name":"Bill"},
        |      {"name":"Mick"},
        |      {"name":"Rick"},
        |      {"name":"Andy"}]
        |  },
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef7a"},
        |    "name":"Rick",
        |    "postal_code":{
        |      "country":"TW",
        |      "city":{"country":{"name":"臺灣"},"name":"臺北市"},
        |      "code":"110",
        |      "name":"信義區"},
        |    "height":218,
        |    "friends":[
        |      {"name":"Kirk"},
        |      {"name":"Toby"},
        |      {"name":"Andy"}]
        |  }
        |]
      """.stripMargin)
  }

}
