import scala.collection.JavaConversions._

import com.mongodb._
import de.flapdoodle.embed.mongo._
import de.flapdoodle.embed.mongo.config._
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.bson.types.ObjectId
import org.specs2.specification.BeforeAfterAll
import org.specs2._

import com.ehanlin.mde.MongoEmbedder

class MongoEmbedderTest extends Specification with BeforeAfterAll {def is = s2"""
  check mock data                             $checkMockData
  test basic embed                            $testBasicEmbed
"""

  val port = 12345
  var mongodExecutable : MongodExecutable = null
  var mongo : Mongo = null

  def checkMockData = {
    val mongo = new Mongo("localhost", port)
    val infoDB = mongo.getDB("info")
    val subjectColl = infoDB.getCollection("subject")
    val subjectEN = subjectColl.findOne(new BasicDBObject("code", "EN"))
    (subjectEN must_!= null) and
    (subjectEN.get("_id").toString must_== "55711d2ad6a23e26b37be431") and
    (subjectEN.get("name") must_== "英語")
  }

  def testBasicEmbed = {
    val list : java.util.List[String] = List("video_PC_1_1", "video_PC_2_1", "video_PC_2_2", "video_PC_3_1")
    val embedList = MongoEmbedder.instance.embed("video", list, """{"subject":true,unit:true}""").asInstanceOf[BasicDBList]
    embedList.toString must_== """[ { "_id" : "video_PC_1_1" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "code" : "PC" , "name" : "國文"} , "name" : "video_PC_1_1" , "order" : 1 , "unit" : [ { "_id" : "unit_PC_1" , "subject" : "PC" , "name" : "第一課" , "order" : 1}]} , { "_id" : "video_PC_2_1" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "code" : "PC" , "name" : "國文"} , "name" : "video_PC_2_1" , "order" : 2 , "unit" : [ { "_id" : "unit_PC_2" , "subject" : "PC" , "name" : "第二課" , "order" : 2}]} , { "_id" : "video_PC_2_2" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "code" : "PC" , "name" : "國文"} , "name" : "video_PC_2_2" , "order" : 3 , "unit" : [ { "_id" : "unit_PC_2" , "subject" : "PC" , "name" : "第二課" , "order" : 2}]} , { "_id" : "video_PC_3_1" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "code" : "PC" , "name" : "國文"} , "name" : "video_PC_3_1" , "order" : 4 , "unit" : [ { "_id" : "unit_PC_1" , "subject" : "PC" , "name" : "第一課" , "order" : 1} , { "_id" : "unit_PC_2" , "subject" : "PC" , "name" : "第二課" , "order" : 2} , { "_id" : "unit_PC_3" , "subject" : "PC" , "name" : "第三課" , "order" : 3}]}]"""
  }



  def beforeAll(): Unit = {
    mongodExecutable = MongodStarter.getDefaultInstance().prepare(new MongodConfigBuilder()
      .version(Version.V3_0_1)
      .net(new Net(port, Network.localhostIsIPv6()))
      .build())
    val mongod = mongodExecutable.start()

    mongo = new Mongo("localhost", port)


    def buildMockSubject(id : String, code : String, name : String)(implicit coll : DBCollection) =
      coll.insert(new BasicDBObject("_id", new ObjectId(id)).append("code", code).append("name", name), WriteConcern.FSYNCED)
    def mockSubject(implicit coll : DBCollection) = {
      buildMockSubject("55711d2ad6a23e26b37be430", "PC", "國文")
      buildMockSubject("55711d2ad6a23e26b37be431", "EN", "英語")
      buildMockSubject("55711d2ad6a23e26b37be432", "MA", "數學")
      buildMockSubject("55711d2ad6a23e26b37be433", "NA", "自然")
      buildMockSubject("55711d2ad6a23e26b37be434", "SO", "社會")
    }
    mockSubject(mongo.getDB("info").getCollection("subject"))


    def buildMockUnit(id : String, subject : String, name : String, order : Integer)(implicit coll : DBCollection) =
      coll.insert(new BasicDBObject("_id", id).append("subject", subject).append("name", name).append("order", order), WriteConcern.FSYNCED)
    def mockUnit(implicit coll : DBCollection) = {
      buildMockUnit("unit_PC_1", "PC", "第一課", 1)
      buildMockUnit("unit_PC_2", "PC", "第二課", 2)
      buildMockUnit("unit_PC_3", "PC", "第三課", 3)
      buildMockUnit("unit_PC_4", "PC", "第四課", 4)
      buildMockUnit("unit_PC_5", "PC", "第五課", 5)
      buildMockUnit("unit_EN_1", "EN", "Lesson 1", 1)
      buildMockUnit("unit_EN_2", "EN", "Lesson 2", 2)
      buildMockUnit("unit_EN_3", "EN", "Lesson 3", 3)
      buildMockUnit("unit_EN_4", "EN", "Lesson 4", 4)
      buildMockUnit("unit_EN_5", "EN", "Lesson 5", 5)
      buildMockUnit("unit_MA_1", "MA", "加法", 1)
      buildMockUnit("unit_MA_2", "MA", "減法", 2)
      buildMockUnit("unit_MA_3", "MA", "乘法", 3)
      buildMockUnit("unit_MA_4", "MA", "除法", 4)
      buildMockUnit("unit_MA_5", "MA", "二元一次方程式", 5)
      buildMockUnit("unit_NA_1", "NA", "動物", 1)
      buildMockUnit("unit_NA_2", "NA", "植物", 2)
      buildMockUnit("unit_SO_1", "SO", "台灣", 1)
      buildMockUnit("unit_SO_2", "SO", "世界", 2)
    }
    mockUnit(mongo.getDB("info").getCollection("unit"))


    def buildMockVideo(id : String, subject : String, name : String, order : Integer, units : List[String])(implicit coll : DBCollection) = {
      val unitList = new BasicDBList()
      unitList.addAll(units)
      coll.insert(new BasicDBObject("_id", id).append("subject", new ObjectId(subject)).append("name", name).append("order", order).append("unit", unitList), WriteConcern.FSYNCED)
    }
    def mockVideo(implicit coll : DBCollection) = {
      buildMockVideo("video_PC_1_1", "55711d2ad6a23e26b37be430", "video_PC_1_1", 1, List("unit_PC_1"))
      buildMockVideo("video_PC_2_1", "55711d2ad6a23e26b37be430", "video_PC_2_1", 2, List("unit_PC_2"))
      buildMockVideo("video_PC_2_2", "55711d2ad6a23e26b37be430", "video_PC_2_2", 3, List("unit_PC_2"))
      buildMockVideo("video_PC_3_1", "55711d2ad6a23e26b37be430", "video_PC_3_1", 4, List("unit_PC_1", "unit_PC_2", "unit_PC_3"))
    }
    mockVideo(mongo.getDB("info").getCollection("video"))


    def buildMockUser(id : String, name : String, age : Integer)(implicit coll : DBCollection) =
      coll.insert(new BasicDBObject("_id", new ObjectId(id)).append("name", name).append("age", age), WriteConcern.FSYNCED)
    def mockUser(implicit coll : DBCollection) = {
      buildMockUser("55712d61d6a23e26b37be440", "user1", 30)
      buildMockUser("55712d61d6a23e26b37be441", "user2", 10)
      buildMockUser("55712d61d6a23e26b37be442", "user3", 25)
    }
    mockUser(mongo.getDB("user").getCollection("user"))


    def buildMockTask(id : String, subject : String, video : String, user : List[String])(implicit coll : DBCollection) = {
      val userList = new BasicDBList()
      userList.addAll(user.map(new ObjectId(_)))
      coll.insert(new BasicDBObject("_id", id).append("subject", subject).append("video", video).append("user", userList), WriteConcern.FSYNCED)
    }
    def mockTask(implicit coll : DBCollection) = {
      buildMockTask("task_1", "PC", "video_PC_1_1", List("55712a3bd6a23e26b37be430"))
      buildMockTask("task_2", "PC", "video_PC_3_1", List("55712a3bd6a23e26b37be430", "55712a3bd6a23e26b37be431", "55712a3bd6a23e26b37be432"))
    }
    mockTask(mongo.getDB("user").getCollection("task"))


    MongoEmbedder.registerDB(mongo.getDB("info"))
    MongoEmbedder.registerDB("user", mongo.getDB("user"))
  }

  def afterAll(): Unit ={
    mongodExecutable.stop()
  }

}