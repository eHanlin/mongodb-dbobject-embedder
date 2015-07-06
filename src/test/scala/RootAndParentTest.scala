import tw.com.ehanlin.mde.MongoEmbedder

class RootAndParentTest extends ReadmeExampleTest { def is = s2"""
  RootAndParentTest  $test
"""

  def test = {
    val dsl =
      """
        |<
        |  @find (db=user coll=user query={ postal_code : @.code } projection={ _id : 0 , friends : 1, height : 1 })
        |  players
        |  [
        |    @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @../../.code })
        |    higher
        |
        |    @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @.../.code })
        |    higherFriend
        |
        |    @findOneById [db=user coll=user, projection={ _id : 0 , height : 1 }]
        |    friends
        |    [
        |      @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @../../../../.code })
        |      higher
        |
        |      @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @.../.code })
        |      higherFriend
        |    ]
        |  ]
        |>
      """.stripMargin
    val result = MongoEmbedder.instance.embed(MObj("code" -> Oid("557e56287a8ea2a9dfe2ef71")), dsl)
    mustString(result,
      """{
        |  "code":{
        |    "$oid":"557e56287a8ea2a9dfe2ef71"
        |  },
        |  "players":[
        |    {
        |      "height":201,
        |      "friends":[
        |        {"height":220,"higherFriend":1,"higher":1},
        |        {"height":183,"higherFriend":4,"higher":4},
        |        {"height":208,"higherFriend":2,"higher":2}],
        |      "higherFriend":3,
        |      "higher":3
        |    },
        |    {
        |      "height":178,
        |      "friends":[
        |        {"height":183,"higherFriend":4,"higher":4}],
        |      "higherFriend":5,
        |      "higher":5
        |    },
        |    {
        |      "height":220,
        |      "friends":[
        |        {"height":201,"higherFriend":3,"higher":3},
        |        {"height":211,"higherFriend":2,"higher":2},
        |        {"height":218,"higherFriend":1,"higher":1},
        |        {"height":208,"higherFriend":2,"higher":2}],
        |      "higherFriend":1,
        |      "higher":1
        |    },
        |    {
        |      "height":211,
        |      "friends":[
        |        {"height":220,"higherFriend":1,"higher":1}],
        |      "higherFriend":2,
        |      "higher":2
        |    },
        |    {
        |      "height":183,
        |      "friends":[
        |        {"height":201,"higherFriend":3,"higher":3},
        |        {"height":178,"higherFriend":5,"higher":5}],
        |      "higherFriend":4,
        |      "higher":4
        |    }
        |  ]
        |}""".stripMargin)
  }
}
