package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DB;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

import java.util.Map;

public class DataAction extends Action {

    public DataAction (Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    @Override
    protected Object executeObjectWithCache(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        return null;
    }

}