import com.mongodb.{BasicDBList, BasicDBObject, Mongo}
import de.flapdoodle.embed.mongo.config.{MongodConfigBuilder, Net}
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.mongo.{MongodExecutable, MongodStarter}
import de.flapdoodle.embed.process.runtime.Network
import org.bson.types.ObjectId
import org.specs2.Specification
import org.specs2.specification.BeforeAfterAll
import tw.com.ehanlin.mde.MongoEmbedder

import scala.collection.JavaConversions._

class ReadmeExampleTest extends Specification with BeforeAfterAll {def is = s2"""
  check Example 1               $example1
"""

  val port = 12346
  var mongodExecutable : MongodExecutable = null
  var mongo : Mongo = null

  def mustString(obj : Object, str : String) = {
    obj.toString.replaceAll("\\s+", "") must_== str.replaceAll("\\s+", "")
  }

  def example1 = {
    println("<  Example  1  >")
    val dsl =
      """
        |@find <db=user coll=user query={ height : { $gte : 210 } }>
        |[
        |  @count (db=user coll=user query={ height : { $gte : 200 } , postal_code : @.postal_code })
        |  num
        |
        |  @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
        |  postal_code
        |
        |  @findOneById [db=user coll=user, projection={ _id : 0 , postal_code : 1, height : 1}]
        |  friends
        |  [
        |    @findOneById <db=info coll=postal_code projection={ _id : 0 , name : 1 }>
        |    postal_code
        |  ]
        |]
      """.stripMargin
    val result = MongoEmbedder.instance.embed(null, dsl)
    mustString(result, """[ { "_id" : { "$oid" : "557e58727a8ea2a9dfe2ef76"} , "name" : "Kirk" , "postal_code" : { "name" : "中正區"} , "height" : 220 , "friends" : [ { "postal_code" : { "name" : "中正區"} , "height" : 201} , { "postal_code" : { "name" : "中正區"} , "height" : 211} , { "postal_code" : { "name" : "信義區"} , "height" : 218} , { "postal_code" : { "name" : "內湖區"} , "height" : 208}] , "num" : 3} , { "_id" : { "$oid" : "557e58727a8ea2a9dfe2ef77"} , "name" : "Mick" , "postal_code" : { "name" : "中正區"} , "height" : 211 , "friends" : [ { "postal_code" : { "name" : "中正區"} , "height" : 220}] , "num" : 3} , { "_id" : { "$oid" : "557e58727a8ea2a9dfe2ef7a"} , "name" : "Rick" , "postal_code" : { "name" : "信義區"} , "height" : 218 , "friends" : [ { "postal_code" : { "name" : "中正區"} , "height" : 220} , { "postal_code" : { "name" : "信義區"} , "height" : 214} , { "postal_code" : { "name" : "內湖區"} , "height" : 208}] , "num" : 4} , { "_id" : { "$oid" : "557e58727a8ea2a9dfe2ef7c"} , "name" : "Toby" , "postal_code" : { "name" : "信義區"} , "height" : 214 , "friends" : [ { "postal_code" : { "name" : "信義區"} , "height" : 218}] , "num" : 4}]""")
  }


  object MObj {
    def apply(elems: (String, Object)*) : BasicDBObject = {
      val result = new BasicDBObject()
      elems.foreach{case((k, v)) =>
        result.append(k, v)
      }
      result
    }
  }

  object MList {
    def apply(elems: Object*) : BasicDBList = {
      val result = new BasicDBList()
      result.addAll(elems)
      result
    }
  }

  object Oid {
    def apply(id: String) : ObjectId = {
      new ObjectId(id)
    }
  }

  object NumInt {
    def apply(i: Int) : java.lang.Integer = {
      i
    }
  }

  def beforeAll(): Unit = {
    mongodExecutable = MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
      .version(Version.Main.PRODUCTION)
      .net(new Net(port, Network.localhostIsIPv6()))
      .build())
    val mongod = mongodExecutable.start()

    mongo = new Mongo("localhost", port)

    def insert(db : String, coll : String, elems : BasicDBObject*) {
      val dbColl = mongo.getDB(db).getCollection(coll);
      elems.foreach(item => dbColl.insert(item))
    }

    insert("info", "country", MObj("_id"->"TW","name"->"臺灣"))

    insert("info", "city", MObj("_id"->Oid("557e55af7a8ea2a9dfe2ef70"),"country"->"TW","name"->"臺北市"))

    insert("info", "postal_code",
      MObj("_id"->Oid("557e56287a8ea2a9dfe2ef71"),"country"->"TW","city"->"557e55af7a8ea2a9dfe2ef70","code"->"100","name"->"中正區"),
      MObj("_id"->Oid("557e56287a8ea2a9dfe2ef72"),"country"->"TW","city"->"557e55af7a8ea2a9dfe2ef70","code"->"110","name"->"信義區"),
      MObj("_id"->Oid("557e56287a8ea2a9dfe2ef73"),"country"->"TW","city"->"557e55af7a8ea2a9dfe2ef70","code"->"114","name"->"內湖區"))

    insert("user", "user",
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef74"),"name"->"Bill","postal_code"->Oid("557e56287a8ea2a9dfe2ef71"),"height"->NumInt(201),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef76"),Oid("557e58727a8ea2a9dfe2ef78"),Oid("557e58727a8ea2a9dfe2ef7e"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef75"),"name"->"Hugo","postal_code"->Oid("557e56287a8ea2a9dfe2ef71"),"height"->NumInt(178),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef78"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef76"),"name"->"Kirk","postal_code"->Oid("557e56287a8ea2a9dfe2ef71"),"height"->NumInt(220),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef74"),Oid("557e58727a8ea2a9dfe2ef77"),Oid("557e58727a8ea2a9dfe2ef7a"),Oid("557e58727a8ea2a9dfe2ef7e"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef77"),"name"->"Mick","postal_code"->Oid("557e56287a8ea2a9dfe2ef71"),"height"->NumInt(211),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef76"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef78"),"name"->"Noah","postal_code"->Oid("557e56287a8ea2a9dfe2ef71"),"height"->NumInt(183),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef74"),Oid("557e58727a8ea2a9dfe2ef75"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef79"),"name"->"Phil","postal_code"->Oid("557e56287a8ea2a9dfe2ef72"),"height"->NumInt(197),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef7d"),Oid("557e58727a8ea2a9dfe2ef7f"),Oid("557e58727a8ea2a9dfe2ef81"),Oid("557e58727a8ea2a9dfe2ef82"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef7a"),"name"->"Rick","postal_code"->Oid("557e56287a8ea2a9dfe2ef72"),"height"->NumInt(218),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef76"),Oid("557e58727a8ea2a9dfe2ef7c"),Oid("557e58727a8ea2a9dfe2ef7e"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef7b"),"name"->"Sean","postal_code"->Oid("557e56287a8ea2a9dfe2ef72"),"height"->NumInt(203),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef7d"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef7c"),"name"->"Toby","postal_code"->Oid("557e56287a8ea2a9dfe2ef72"),"height"->NumInt(214),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef7a"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef7d"),"name"->"Abel","postal_code"->Oid("557e56287a8ea2a9dfe2ef72"),"height"->NumInt(200),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef79"),Oid("557e58727a8ea2a9dfe2ef7b"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef7e"),"name"->"Andy","postal_code"->Oid("557e56287a8ea2a9dfe2ef73"),"height"->NumInt(208),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef74"),Oid("557e58727a8ea2a9dfe2ef76"),Oid("557e58727a8ea2a9dfe2ef7a"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef7f"),"name"->"Cary","postal_code"->Oid("557e56287a8ea2a9dfe2ef73"),"height"->NumInt(193),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef79"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef80"),"name"->"Dean","postal_code"->Oid("557e56287a8ea2a9dfe2ef73"),"height"->NumInt(186),"friends"->MList()),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef81"),"name"->"Eric","postal_code"->Oid("557e56287a8ea2a9dfe2ef73"),"height"->NumInt(194),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef79"),Oid("557e58727a8ea2a9dfe2ef81"))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef82"),"name"->"Glen","postal_code"->Oid("557e56287a8ea2a9dfe2ef73"),"height"->NumInt(194),"friends"->MList(Oid("557e58727a8ea2a9dfe2ef79"),Oid("557e58727a8ea2a9dfe2ef82"))))

    MongoEmbedder.registerDB(mongo.getDB("info"))
    MongoEmbedder.registerDB("info", mongo.getDB("info"))
    MongoEmbedder.registerDB("user", mongo.getDB("user"))
  }

  def afterAll(): Unit ={
    mongodExecutable.stop()
  }

}
