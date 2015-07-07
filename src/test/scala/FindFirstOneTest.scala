import tw.com.ehanlin.mde.MongoEmbedder

class FindFirstOneTest extends ReadmeExampleTest { def is = s2"""
  FindFirstOneTest  $test
"""

  def test = {
    val dsl =
      """
        |<
        |  @findFirstOne (db=user coll=user query={ height : { $gte : 215 } } sort={ height : -1 } projection={name:1})
        |  kirk
        |
        |  @findFirstOne (db=user coll=user query={ height : { $gte : 215 } } sort={ height : -1 } projection={name:1} skip=1)
        |  rick
        |
        |  @findFirstOne (db=user coll=user query={ height : { $gte : 215 } } sort={ height : -1 } projection={name:1} skip=2)
        |  no
        |>
      """.stripMargin
    val result = MongoEmbedder.instance.embed(MObj(), dsl)
    println(result)
    mustString(result,
      """""".stripMargin)
    ok
  }
}
