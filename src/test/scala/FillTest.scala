import tw.com.ehanlin.mde.MongoEmbedder

class FillTest extends ReadmeExampleTest { def is = s2"""
  FillTest  $test
"""

  def test = {
    val dsl =
      """
        |@find <db=user coll=user query={ height : { $gte : 215 } }>
        |[
        |  @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
        |  @fill <value=@.name>
        |  postal_code
        |
        |  @findOneById [db=user coll=user, projection={ _id : 0 , postal_code : 1, height : 1}]
        |  friends
        |  [
        |    @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
        |    @fill <value=@.name>
        |    postal_code
        |  ]
        |]
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result,
      """[
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef76"},
        |    "name":"Kirk",
        |    "postal_code":"中正區",
        |    "height":220,
        |    "friends":[
        |      {"postal_code":"中正區","height":201},
        |      {"postal_code":"中正區","height":211},
        |      {"postal_code":"信義區","height":218},
        |      {"postal_code":"內湖區","height":208}]
        |  },
        |  {
        |    "_id":{"$oid":"557e58727a8ea2a9dfe2ef7a"},
        |    "name":"Rick",
        |    "postal_code":"信義區",
        |    "height":218,
        |    "friends":[
        |      {"postal_code":"中正區","height":220},
        |      {"postal_code":"信義區","height":214},
        |      {"postal_code":"內湖區","height":208}]
        |  }
        |]""".stripMargin)
  }
}
