package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

public class Count extends DbAction {

    public static final String KEY_QUERY = "query";

    public Count(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public DBObject query(DataStack data) {
        Object _query = evalInfo(KEY_QUERY, data);
        return (DBObject)((_query != null) ? _query : EmptyObject.BasicDBObject);
    }

    @Override
    public String toString() {
        return toString("count",
            KEY_DB, infos().get(KEY_DB),
            KEY_COLL, infos().get(KEY_COLL),
            KEY_QUERY, infos().get(KEY_QUERY));
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+query(data);
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        return coll.count(query(data));
    }

}
