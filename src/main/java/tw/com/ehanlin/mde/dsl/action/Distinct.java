package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

public class Distinct extends Count {

    public Distinct(Scope scope, MdeDBObject infos) {
        super(scope, infos);
        _key = (String)infos.get("key");
    }

    public String key() {
        return _key;
    }

    @Override
    public String toString() {
        return toString("distinct", "db", db(), "coll", coll(), "key", key(), "query", query());
    }

    @Override
    protected String cacheKey(DataStack data, DBCollection coll) {
        return "distinct_"+coll.getFullName()+"_"+key()+"_"+AtEvaluator.eval(data, query()).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        return coll.distinct(key(), AtEvaluator.eval(data, query()));
    }

    private String _key;

}
