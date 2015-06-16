import tw.com.ehanlin.mde.MongoEmbedder

class Example2 extends ReadmeExampleTest {def is = s2"""
  Example2  $example2
"""

  def example2 = {
    val dsl =
      """
        |@findOne <db=user coll=user query={ _id : { $oid : "557e58727a8ea2a9dfe2ef7a" } }>
        |<
        |  @findOneById <db=info coll=postal_code projection={ _id : 0 }>
        |  postal_code
        |  <
        |    @findOne <db=info coll=city query={ _id : { $oid : @ } } projection={ _id : 0 }>
        |    city
        |    <
        |      @findOneById <db=info coll=country projection={ _id : 0 }>
        |      country
        |    >
        |  >
        |>
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result,
      """
        |{
        |  "_id":{"$oid":"557e58727a8ea2a9dfe2ef7a"},
        |  "name":"Rick",
        |  "postal_code":{
        |    "country":"TW",
        |    "city":{
        |      "country":{
        |        "name":"臺灣"},
        |      "name":"臺北市"},
        |    "code":"110",
        |    "name":"信義區"},
        |  "height":218,
        |  "friends":[
        |    {"$oid":"557e58727a8ea2a9dfe2ef76"},
        |    {"$oid":"557e58727a8ea2a9dfe2ef7c"},
        |    {"$oid":"557e58727a8ea2a9dfe2ef7e"}]
        |}
      """.stripMargin)
  }

}
