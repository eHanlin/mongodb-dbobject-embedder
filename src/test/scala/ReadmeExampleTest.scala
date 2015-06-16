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

abstract class ReadmeExampleTest extends Specification with BeforeAfterAll {

  val port = 12345
  var mongodExecutable : MongodExecutable = null
  var mongo : Mongo = null

  def mustString(obj : Object, str : String) = {
    obj.toString.replaceAll("\\s+", "") must_== str.replaceAll("\\s+", "")
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

    insert("user", "team",
      MObj("_id"->"zebra","name"->"zebra","player"->MObj("c"->Oid("557e58727a8ea2a9dfe2ef76"),"f"->Oid("557e58727a8ea2a9dfe2ef77"),"g"->Oid("557e58727a8ea2a9dfe2ef7b"))),
      MObj("_id"->"snake","name"->"snake","player"->MObj("c"->Oid("557e58727a8ea2a9dfe2ef7c"),"f"->Oid("557e58727a8ea2a9dfe2ef81"),"g"->Oid("557e58727a8ea2a9dfe2ef82"))),
      MObj("_id"->"koala","name"->"koala","player"->MObj("c"->Oid("557e58727a8ea2a9dfe2ef7a"),"f"->Oid("557e58727a8ea2a9dfe2ef7e"),"g"->Oid("557e58727a8ea2a9dfe2ef74"))))

    insert("game", "game",
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef83"),"team"->MList("zebra","snake"),"box_score"->MList(MList(Oid("557e58727a8ea2a9dfe2ef86"),Oid("557e58727a8ea2a9dfe2ef87"),Oid("557e58727a8ea2a9dfe2ef88")),MList(Oid("557e58727a8ea2a9dfe2ef89"),Oid("557e58727a8ea2a9dfe2ef8a"),Oid("557e58727a8ea2a9dfe2ef8b")))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef84"),"team"->MList("snake","koala"),"box_score"->MList(MList(Oid("557e58727a8ea2a9dfe2ef8c"),Oid("557e58727a8ea2a9dfe2ef8d"),Oid("557e58727a8ea2a9dfe2ef8e")),MList(Oid("557e58727a8ea2a9dfe2ef8f"),Oid("557e58727a8ea2a9dfe2ef90"),Oid("557e58727a8ea2a9dfe2ef91")))),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef85"),"team"->MList("koala","zebra"),"box_score"->MList(MList(Oid("557e58727a8ea2a9dfe2ef92"),Oid("557e58727a8ea2a9dfe2ef93"),Oid("557e58727a8ea2a9dfe2ef94")),MList(Oid("557e58727a8ea2a9dfe2ef95"),Oid("557e58727a8ea2a9dfe2ef96"),Oid("557e58727a8ea2a9dfe2ef97")))))

    insert("game", "box_score",
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef86"),"game"->Oid("557e58727a8ea2a9dfe2ef83"),"user"->Oid("557e58727a8ea2a9dfe2ef76"),"pts"->NumInt(11),"ast"->NumInt(2)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef87"),"game"->Oid("557e58727a8ea2a9dfe2ef83"),"user"->Oid("557e58727a8ea2a9dfe2ef77"),"pts"->NumInt(5),"ast"->NumInt(4)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef88"),"game"->Oid("557e58727a8ea2a9dfe2ef83"),"user"->Oid("557e58727a8ea2a9dfe2ef7b"),"pts"->NumInt(5),"ast"->NumInt(2)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef89"),"game"->Oid("557e58727a8ea2a9dfe2ef83"),"user"->Oid("557e58727a8ea2a9dfe2ef7c"),"pts"->NumInt(3),"ast"->NumInt(1)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef8a"),"game"->Oid("557e58727a8ea2a9dfe2ef83"),"user"->Oid("557e58727a8ea2a9dfe2ef81"),"pts"->NumInt(6),"ast"->NumInt(2)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef8b"),"game"->Oid("557e58727a8ea2a9dfe2ef83"),"user"->Oid("557e58727a8ea2a9dfe2ef82"),"pts"->NumInt(6),"ast"->NumInt(5)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef8c"),"game"->Oid("557e58727a8ea2a9dfe2ef84"),"user"->Oid("557e58727a8ea2a9dfe2ef7c"),"pts"->NumInt(3),"ast"->NumInt(2)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef8d"),"game"->Oid("557e58727a8ea2a9dfe2ef84"),"user"->Oid("557e58727a8ea2a9dfe2ef81"),"pts"->NumInt(3),"ast"->NumInt(7)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef8e"),"game"->Oid("557e58727a8ea2a9dfe2ef84"),"user"->Oid("557e58727a8ea2a9dfe2ef82"),"pts"->NumInt(15),"ast"->NumInt(2)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef8f"),"game"->Oid("557e58727a8ea2a9dfe2ef84"),"user"->Oid("557e58727a8ea2a9dfe2ef7a"),"pts"->NumInt(5),"ast"->NumInt(2)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef90"),"game"->Oid("557e58727a8ea2a9dfe2ef84"),"user"->Oid("557e58727a8ea2a9dfe2ef7e"),"pts"->NumInt(7),"ast"->NumInt(3)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef91"),"game"->Oid("557e58727a8ea2a9dfe2ef84"),"user"->Oid("557e58727a8ea2a9dfe2ef74"),"pts"->NumInt(7),"ast"->NumInt(3)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef92"),"game"->Oid("557e58727a8ea2a9dfe2ef85"),"user"->Oid("557e58727a8ea2a9dfe2ef7a"),"pts"->NumInt(7),"ast"->NumInt(4)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef93"),"game"->Oid("557e58727a8ea2a9dfe2ef85"),"user"->Oid("557e58727a8ea2a9dfe2ef7e"),"pts"->NumInt(7),"ast"->NumInt(5)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef94"),"game"->Oid("557e58727a8ea2a9dfe2ef85"),"user"->Oid("557e58727a8ea2a9dfe2ef74"),"pts"->NumInt(7),"ast"->NumInt(5)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef95"),"game"->Oid("557e58727a8ea2a9dfe2ef85"),"user"->Oid("557e58727a8ea2a9dfe2ef76"),"pts"->NumInt(9),"ast"->NumInt(2)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef96"),"game"->Oid("557e58727a8ea2a9dfe2ef85"),"user"->Oid("557e58727a8ea2a9dfe2ef77"),"pts"->NumInt(1),"ast"->NumInt(3)),
      MObj("_id"->Oid("557e58727a8ea2a9dfe2ef97"),"game"->Oid("557e58727a8ea2a9dfe2ef85"),"user"->Oid("557e58727a8ea2a9dfe2ef7b"),"pts"->NumInt(6),"ast"->NumInt(3)))

    MongoEmbedder.registerDB(mongo.getDB("info"))
    MongoEmbedder.registerDB("info", mongo.getDB("info"))
    MongoEmbedder.registerDB("user", mongo.getDB("user"))
    MongoEmbedder.registerDB("game", mongo.getDB("game"))
  }

  def afterAll(): Unit ={
    mongodExecutable.stop()
  }

}
