import tw.com.ehanlin.mde.MongoEmbedder

class Example4 extends ReadmeExampleTest {def is = s2"""
  Example4  $example4
"""

  def example4 = {
    val dsl =
      """
        |@findOne <db=game coll=game query={ _id : { $oid : "557e58727a8ea2a9dfe2ef83" } } projection={ _id : 0 }>
        |<
        |  box_score
        |  [
        |  	@findOneById [db=game coll=box_score projection={ _id : 0 , user : 1 , pts : 1 }]
        |  	[
        |  		@findOneById <db=user coll=user projection={ _id : 0 , name : 1}>
        |  		user
        |  	]
        |  ]
        |>
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    println(result)
    mustString(result,
      """
        |{
        |  "team":["zebra","snake"],
        |  "box_score":[
        |    [
        |      {"user":{"name":"Kirk"},"pts":11},
        |      {"user":{"name":"Mick"},"pts":5},
        |      {"user":{"name":"Sean"},"pts":5}
        |    ],
        |    [
        |      {"user":{"name":"Toby"},"pts":3},
        |      {"user":{"name":"Eric"},"pts":6},
        |      {"user":{"name":"Glen"},"pts":6}
        |    ]
        |  ]
        |}
      """.stripMargin)
  }

}
