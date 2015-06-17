import tw.com.ehanlin.mde.MongoEmbedder

class Example7 extends ReadmeExampleTest {def is = s2"""
  Example7  $example7
"""

  def example7 = {
    val dsl =
      """
        |@findOne [db=user coll=user query={ _id : { $oid : "557e58727a8ea2a9dfe2ef74" } } projection={ _id : 0 , name : 1 , friends : 1 }]
        |<
        |  0
        |  <
        |    @findOneById [db=user coll=user projection={ _id : 0 , name : 1 }]
        |    friends
        |  >
        |>
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result,
      """
        |[
        |  {
        |    "name":"Bill",
        |    "friends":[
        |      {"name":"Kirk"},
        |      {"name":"Noah"},
        |      {"name":"Andy"}]
        |  }
        |]
      """.stripMargin)
  }

}
