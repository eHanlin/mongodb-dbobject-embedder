package tw.com.ehanlin.mde.dsl.action.data;

import com.mongodb.DB;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.Dsl;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

import java.util.Map;

public abstract class DslAction extends Action {

    protected abstract Dsl actionDsl();

    public DslAction(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    @Override
    protected Object executeObjectWithCache(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        return actionDsl().execute(data, dbMap, cache, parallel);
    }
}
