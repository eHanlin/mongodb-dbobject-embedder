import org.specs2.Specification
import tw.com.ehanlin.mde.dsl.DslParser

class DslTest extends Specification {def is = s2"""
  check basic                       $checkBasic



"""

  def checkBasic = {
    val dsl = DslParser.instance.parse(
      """
        @findOneById [db=user, coll=Task, projection={unit:true}]
        {

          @findOneById (coll=Knowledge, projection={subject:1})
          knowledge {

            @findOneById <>
            subject

          }

        }
      """)
    dsl.toString must_== """@FindOneById [ db=user, coll=Task, projection={ "unit" : true} ]
                           |{
                           |  @FindOneById ( coll=Knowledge, projection={ "subject" : 1} )
                           |  knowledge  {
                           |    @FindOneById < projection={ } >
                           |    subject
                           |  }
                           |}""".stripMargin
  }

}
