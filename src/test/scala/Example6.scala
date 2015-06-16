import tw.com.ehanlin.mde.MongoEmbedder

class Example6 extends ReadmeExampleTest {def is = s2"""
  Example6  $example6
"""

  def example6 = {
    val dsl =
      """
        |@find <db=info coll=postal_code projection={ _id : 0 , code : 0 }>
        |[
        |  @findOne (db=info coll=country query={ _id : @.country } projection={ _id : 0 })
        |  country
        |
        |  @findOne (db=info coll=city query={ _id : { $oid : @.city } } projection={ _id : 0 , country : 0 })
        |  city
        |]
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result,
      """
        |[
        |  {
        |    "country":{"name":"臺灣"},
        |    "city":{"name":"臺北市"},
        |    "name":"中正區"
        |  },
        |  {
        |    "country":{"name":"臺灣"},
        |    "city":{"name":"臺北市"},
        |    "name":"信義區"
        |  },
        |  {
        |    "country":{"name":"臺灣"},
        |    "city":{"name":"臺北市"},
        |    "name":"內湖區"
        |  }
        |]
      """.stripMargin)
  }

}
