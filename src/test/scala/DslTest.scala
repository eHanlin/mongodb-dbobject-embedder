import org.specs2.Specification
import tw.com.ehanlin.mde.dsl.DslParser

class DslTest extends Specification {def is = s2"""
  check dsl basic                       $checkBasic
"""

  def checkBasic = {
    val dsl = DslParser.instance.parse(
      """@findOneById [db=user, coll=Task, projection={unit:true}]
        |<
        |
        |  @findOneById [db=info, coll=Unit, projection={createDate:0}]
        |  unit <
        |
        |    @findOne <query={_id:@}>
        |    subject
        |
        |    @distinct (coll=Video, key=subject, query={subject:'PC',knowledge:{$in:@.knowledge}})
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
        |    @findOne [query={_id:@}]
        |    child [
        |
        |      @findOne <query={_id:@}>
        |      subject
        |
        |    ]
        |
        |  ]
        |
        |>""".stripMargin)
    dsl.toString must_== """@findOneById [ db=user, coll=Task, projection={ "unit" : true} ]
                           |<
                           |  @findOneById [ db=info, coll=Unit, projection={ "createDate" : 0} ]
                           |  unit  <
                           |    @findOne < query={ "_id" : @}, projection={ } >
                           |    subject
                           |    @distinct ( coll=Video, key=subject, query={ "subject" : "PC" , "knowledge" : { "$in" : @.knowledge}} )
                           |    @findOneById [ coll=subject, projection={ } ]
                           |    videoSubject
                           |    @findOneById [ coll=Knowledge, projection={ } ]
                           |    knowledge    [
                           |      @findOneById < projection={ } >
                           |      subject
                           |    ]
                           |  >
                           |  video  <
                           |    @findOne < query={ "_id" : @}, projection={ } >
                           |    subject
                           |  >
                           |  knowledge  [
                           |    @findOne < query={ "_id" : @}, projection={ } >
                           |    subject
                           |    @findOne [ query={ "_id" : @}, projection={ } ]
                           |    child    [
                           |      @findOne < query={ "_id" : @}, projection={ } >
                           |      subject
                           |    ]
                           |  ]
                           |>""".stripMargin
  }

}
