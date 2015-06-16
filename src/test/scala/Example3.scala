import tw.com.ehanlin.mde.MongoEmbedder

class Example3 extends ReadmeExampleTest {def is = s2"""
  Example3  $example3
"""

  def example3 = {
    val dsl =
      """
        |@findOne <db=user coll=team query={ _id : "zebra" } projection={ _id : 0 }>
        |<
        |  @findOneById [db=user coll=user projection={ _id : 0 , name : 1 , postal_code : 1 }]
        |  player
        |  [
        |    @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
        |    postal_code
        |  ]
        |>
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result,
      """
        |{
        |  "name":"zebra",
        |  "player":{
        |    "c":{
        |      "name":"Kirk",
        |      "postal_code":{"name":"中正區"}},
        |    "f":{
        |      "name":"Mick",
        |      "postal_code":{"name":"中正區"}},
        |    "g":{
        |      "name":"Sean",
        |      "postal_code":{"name":"信義區"}}}
        |}
      """.stripMargin)
  }

}
