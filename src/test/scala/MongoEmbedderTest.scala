import scala.collection.JavaConversions._

import com.mongodb._
import de.flapdoodle.embed.mongo._
import de.flapdoodle.embed.mongo.config._
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.bson.types.ObjectId
import org.specs2.specification.BeforeAfterAll
import org.specs2._

import tw.com.ehanlin.mde.MongoEmbedder

class MongoEmbedderTest extends Specification with BeforeAfterAll {def is = s2"""
  check findOneById                           $checkFindOneById
"""

  val port = 12345
  var mongodExecutable : MongodExecutable = null
  var mongo : Mongo = null


  def checkFindOneById = {
    val dsl = """
                |@findOneById [db=info, coll=video]
                |[
                |   @findOneById <db=info, coll=subject, projection={name:1}>
                |   subject
                |]
              """.stripMargin
    val list = new BasicDBList()
    list.addAll(List("video_PC_1_1", "video_PC_2_1", "video_PC_2_2", "video_PC_3_1"))
    val result = MongoEmbedder.instance.embed(list, dsl)
    result.toString must_== """[ { "_id" : "video_PC_1_1" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "name" : "國文"} , "name" : "video_PC_1_1" , "order" : 1 , "unit" : [ "unit_PC_1"]} , { "_id" : "video_PC_2_1" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "name" : "國文"} , "name" : "video_PC_2_1" , "order" : 2 , "unit" : [ "unit_PC_2"]} , { "_id" : "video_PC_2_2" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "name" : "國文"} , "name" : "video_PC_2_2" , "order" : 3 , "unit" : [ "unit_PC_2"]} , { "_id" : "video_PC_3_1" , "subject" : { "_id" : { "$oid" : "55711d2ad6a23e26b37be430"} , "name" : "國文"} , "name" : "video_PC_3_1" , "order" : 4 , "unit" : [ "unit_PC_1" , "unit_PC_2" , "unit_PC_3"]}]"""
  }

  def checkFindOne = {
    val dsl = """
                |@findOne [db=info, coll=video, query={_id:@}]
                |[
                |   @findOne [db=info, coll=unit, query={_id:@}]
                |   unit [
                |     @findOne <db=info, coll=subject, query={code:@}>
                |     subject
                |   ]
                |]
              """.stripMargin
    val list = new BasicDBList()
    list.addAll(List("video_PC_1_1", "video_PC_2_1", "video_PC_2_2", "video_PC_3_1"))
    val result = MongoEmbedder.instance.embed(list, dsl)
    println(result)
    ok
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
      coll.insert(new BasicDBObject("_id", id).append("subject", new ObjectId(subject)).append("resource", video).append("student", userList), WriteConcern.FSYNCED)
    }
    def mockTask(implicit coll : DBCollection) = {
      buildMockTask("task_1", "55711d2ad6a23e26b37be430", "video_PC_1_1", List("55712d61d6a23e26b37be440"))
      buildMockTask("task_2", "55711d2ad6a23e26b37be430", "video_PC_3_1", List("55712d61d6a23e26b37be440", "55712d61d6a23e26b37be441", "55712d61d6a23e26b37be442"))
    }
    mockTask(mongo.getDB("user").getCollection("task"))

    MongoEmbedder.registerDB(mongo.getDB("info"))
    MongoEmbedder.registerDB("info", mongo.getDB("info"))
    MongoEmbedder.registerDB("user", mongo.getDB("user"))
  }

  def afterAll(): Unit ={
    mongodExecutable.stop()
  }

}
