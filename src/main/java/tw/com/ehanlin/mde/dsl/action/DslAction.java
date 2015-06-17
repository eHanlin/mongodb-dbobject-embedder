package tw.com.ehanlin.mde.dsl.action;

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
    protected String cacheKey(DataStack data) {
        if(data.getSelf() != null){
            return actionDsl().toString().replaceAll("\\s+", "")+"_"+data.getSelf().toString();
        }
        return actionDsl().toString().replaceAll("\\s+", "")+"_null";
    }

    @Override
    protected Object executeObject(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        return actionDsl().execute(data, dbMap, cache, parallel);
    }

}
