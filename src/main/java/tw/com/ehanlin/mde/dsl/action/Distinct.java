package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;

public class Distinct extends Count {

    public Distinct(Scope scope, String db, String coll, String key, MdeDBObject query) {
        super(scope, db, coll, query);
        _key = key;
    }

    public String key() {
        return _key;
    }

    @Override
    public String toString() {
        return toString("distinct", "db", db(), "coll", coll(), "key", key(), "query", query());
    }

    @Override
    protected String cacheKey(Object resource, DBCollection coll) {
        return "distinct_"+coll.getFullName()+"_"+key()+"_"+AtEvaluator.eval(resource, query()).toString();
    }

    @Override
    protected Object executeObject(Object resource, DBCollection coll) {
        return coll.distinct(key(), AtEvaluator.eval(resource, query()));
    }

    private String _key;

}
