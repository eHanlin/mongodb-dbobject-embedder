package tw.com.ehanlin.mde.dsl.action.data;

import com.mongodb.DB;
import tw.com.ehanlin.mde.dsl.action.data.DataAction;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

import java.util.Map;

public class Fill extends DataAction {

    public static final String KEY_VALUE = "value";

    public Fill(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public Object value(DataStack data) {
        return evalInfo(KEY_VALUE, data);
    }

    @Override
    public String toString() {
        return toString("fill", KEY_VALUE, infos().get(KEY_VALUE));
    }


    @Override
    protected Object executeObjectWithCache(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        return value(data);
    }

}
