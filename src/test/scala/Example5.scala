import tw.com.ehanlin.mde.MongoEmbedder

class Example5 extends ReadmeExampleTest {def is = s2"""
  Example5  $example5
"""

  def example5 = {
    val dsl =
      """
        |@find <db=info coll=postal_code projection={ name : 1 }>
        |[
        |  @count (db=user coll=user query={ height : { $gte : 200 } , postal_code : @._id })
        |  num
        |]
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result,
      """
        |[
        |  {
        |    "_id":{"$oid":"557e56287a8ea2a9dfe2ef71"},
        |    "name":"中正區",
        |    "num":3
        |  },
        |  {
        |    "_id":{"$oid":"557e56287a8ea2a9dfe2ef72"},
        |    "name":"信義區",
        |    "num":4
        |  },
        |  {
        |    "_id":{"$oid":"557e56287a8ea2a9dfe2ef73"},
        |    "name":"內湖區",
        |    "num":1
        |  }
        |]
      """.stripMargin)
  }

}
