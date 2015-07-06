import tw.com.ehanlin.mde.MongoEmbedder

class RootAndParentTest extends ReadmeExampleTest { def is = s2"""
  RootAndParentTest  $test
"""

  def test = {
    val dsl =
      """
        |<
        |  @find (db=user coll=user query={ postal_code : @.postal_code } projection={ _id : 0 , friends : 1, height : 1 })
        |  players
        |  [
        |    @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @../.postal_code })
        |    higher
        |
        |    @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @.../.postal_code , _id : { $in : @.friends } })
        |    higherFriend
        |
        |    @findOneById [db=user coll=user, projection={ _id : 0 , friends : 1, height : 1 }]
        |    friends
        |    [
        |      @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @../../.postal_code })
        |      higher
        |
        |      @count (db=user coll=user query={ height : { $gte : @.height } , postal_code : @.../.postal_code , _id : { $in : @.friends } })
        |      higherFriend
        |    ]
        |  ]
        |>
      """.stripMargin
    val result = MongoEmbedder.instance.embed(MObj("postal_code" -> Oid("557e56287a8ea2a9dfe2ef71")), dsl)
    mustString(result,
      """{
        |  "postal_code":{
        |    "$oid":"557e56287a8ea2a9dfe2ef71"
        |  },
        |  "players":[
        |    {
        |      "height":201,
        |      "friends":[
        |        {
        |          "height":220,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef74"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef77"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7a"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7e"}],
        |          "higherFriend":0,
        |          "higher":1
        |        },
        |        {
        |          "height":183,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef74"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef75"}],
        |          "higherFriend":1,
        |          "higher":4
        |        },
        |        {
        |          "height":208,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef74"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef76"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7a"}],
        |          "higherFriend":1,
        |          "higher":2
        |        }
        |      ],
        |      "higherFriend":1,
        |      "higher":3
        |    },
        |    {
        |      "height":178,
        |      "friends":[
        |        {
        |          "height":183,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef74"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef75"}],
        |          "higherFriend":1,
        |          "higher":4
        |        }
        |      ],
        |      "higherFriend":1,
        |      "higher":5
        |    },
        |    {
        |      "height":220,
        |      "friends":[
        |        {
        |          "height":201,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef76"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef78"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7e"}],
        |          "higherFriend":1,
        |          "higher":3
        |        },
        |        {
        |          "height":211,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef76"}],
        |          "higherFriend":1,
        |          "higher":2
        |        },
        |        {
        |          "height":218,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef76"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7c"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7e"}],
        |          "higherFriend":1,
        |          "higher":1
        |        },
        |        {
        |          "height":208,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef74"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef76"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7a"}],
        |          "higherFriend":1,
        |          "higher":2
        |        }
        |      ],
        |      "higherFriend":0,
        |      "higher":1
        |    },
        |    {
        |      "height":211,
        |      "friends":[
        |        {
        |          "height":220,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef74"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef77"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7a"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7e"}],
        |          "higherFriend":0,
        |          "higher":1
        |        }
        |      ],
        |      "higherFriend":1,
        |      "higher":2
        |    },
        |    {
        |      "height":183,
        |      "friends":[
        |        {
        |          "height":201,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef76"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef78"},
        |            {"$oid":"557e58727a8ea2a9dfe2ef7e"}],
        |          "higherFriend":1,
        |          "higher":3
        |        },
        |        {
        |          "height":178,
        |          "friends":[
        |            {"$oid":"557e58727a8ea2a9dfe2ef78"}
        |          ],
        |          "higherFriend":1,
        |          "higher":5
        |        }
        |      ],
        |      "higherFriend":1,
        |      "higher":4
        |    }
        |  ]
        |}""".stripMargin)
  }
}
