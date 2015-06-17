package my.action;

import tw.com.ehanlin.mde.dsl.Dsl;
import tw.com.ehanlin.mde.dsl.action.DslAction;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;

public class EmbedPostalCode extends DslAction {

    private static Dsl _actionDsl;

    public EmbedPostalCode(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    @Override
    protected Dsl actionDsl() {
        if(_actionDsl == null) {
            _actionDsl = this.dsl().parser().parse("" +
                    "@findOne <db=user coll=user query={ _id : { $oid : @ } }>\n" +
                    "<\n" +
                    "  @findOneById <db=info coll=postal_code projection={ _id : 0 }>\n" +
                    "  postal_code\n" +
                    "  <\n" +
                    "    @findOne <db=info coll=city query={ _id : { $oid : @ } } projection={ _id : 0 }>\n" +
                    "    city\n" +
                    "    <\n" +
                    "      @findOneById <db=info coll=country projection={ _id : 0 }>\n" +
                    "      country\n" +
                    "    >\n" +
                    "  >\n" +
                    ">");
        }
        return _actionDsl;
    }

    @Override
    public String toString() {
        return toString("embedPostalCode");
    }
}
