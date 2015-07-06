import org.specs2.Specification
import tw.com.ehanlin.mde.dsl.DslParser

class DslTest extends Specification {def is = s2"""
  check dsl basic                       $checkBasic
"""

  def checkBasic = {
    val dsl = new DslParser().parse(
      """@findOneById [db=user coll=Task projection={unit:true}]
        |<
        |
        |  @findOneById [db=info coll=Unit projection={createDate:0}]
        |  unit <
        |
        |    @findOne <query={_id:@}>
        |    subject
        |
        |    @distinct (coll=Video key=subject query={subject:'PC',knowledge:{$in:@.knowledge}})
        |    @findOneById [coll=subject]
        |    videoSubject
        |
        |    @findOneById [coll=Knowledge]
        |    knowledge [
        |
        |      @findOneById <>
        |      subject
        |
        |    ]
        |
        |  >
        |
        |  video <
        |
        |    @findOne <query={_id:@}>
        |    subject
        |
        |  >
        |
        |  knowledge [
        |
        |    @findOne <query={_id:@}>
        |    subject
        |
        |    @find [query={_id:@} skip=5 limit=10]
        |    child [[
        |
        |      @findOne <query={_id:@}>
        |      subject
        |
        |    ]]
        |
        |  ]
        |
        |>""".stripMargin)

    val musetBe = """@findOneById [ db=user coll=Task projection={ "unit" : true} ]
               |<
               |  @findOneById [ db=info coll=Unit projection={ "createDate" : 0} ]
               |  unit <
               |    @findOne < query={ "_id" : @} >
               |    subject
               |    @distinct ( coll=Video key=subject query={ "subject" : "PC" , "knowledge" : { "$in" : @.knowledge}} )
               |    @findOneById [ coll=subject ]
               |    videoSubject
               |    @findOneById [ coll=Knowledge ]
               |    knowledge [
               |      @findOneById <  >
               |      subject
               |    ]
               |  >
               |  video <
               |    @findOne < query={ "_id" : @} >
               |    subject
               |  >
               |  knowledge [
               |    @findOne < query={ "_id" : @} >
               |    subject
               |    @find [ query={ "_id" : @} skip=5 limit=10 ]
               |    child [
               |      [
               |        @findOne < query={ "_id" : @} >
               |        subject
               |      ]
               |    ]
               |  ]
               |>""".stripMargin

    dsl.toString.replaceAll("\\s+", "") must_== musetBe.replaceAll("\\s+", "")
  }

}
