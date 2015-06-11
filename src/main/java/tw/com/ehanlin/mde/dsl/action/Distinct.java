package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBObject;

public class Distinct extends Count {

    public Distinct(Scope scope, String db, String coll, String key, BasicDBObject query) {
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

    private String _key;

}
