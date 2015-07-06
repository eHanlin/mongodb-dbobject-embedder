package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

public class Distinct extends Count {

    public static final String KEY_KEY = "key";

    public Distinct(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public String key(DataStack data) {
        return (String) evalInfo(KEY_KEY, data);
    }

    @Override
    public String toString() {
        return toString("distinct",
                KEY_DB, infos().get(KEY_DB),
                KEY_COLL, infos().get(KEY_COLL),
                KEY_KEY, infos().get(KEY_KEY),
                KEY_QUERY, infos().get(KEY_QUERY));
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+key(data)+"_"+query(data).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        return coll.distinct(key(data), query(data));
    }

}
