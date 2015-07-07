package tw.com.ehanlin.mde.dsl.action.db;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.action.db.DbAction;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

import java.util.ArrayList;
import java.util.List;

public class Aggregate extends DbAction {

    public static final String KEY_PIPELINES = "pipelines";

    public Aggregate(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public BasicDBList pipelines(DataStack data) {
        return (BasicDBList)evalInfo(KEY_PIPELINES, data);
    }

    @Override
    public String toString() {
        return toString("aggregate",
                KEY_DB, infos().get(KEY_DB),
                KEY_COLL, infos().get(KEY_COLL),
                KEY_PIPELINES, infos().get(KEY_PIPELINES));
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+pipelines(data);
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        BasicDBList pipes = pipelines(data);
        List<DBObject> pipeList = new ArrayList();
        pipes.forEach(item -> pipeList.add((DBObject)item));
        BasicDBList result = new BasicDBList();
        coll.aggregate(pipeList).results().forEach(item -> result.add(item));
        return result;
    }

}
