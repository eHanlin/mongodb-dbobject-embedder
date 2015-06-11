package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBObject;
import tw.com.ehanlin.mde.util.EmptyObject;

public class Count extends Action {

    public Count(Scope scope, String db, String coll, BasicDBObject query) {
        super(scope, db, coll);
        _query = (query != null) ? query : EmptyObject.BasicDBObject;
    }

    public BasicDBObject query() {
        return (_query.isEmpty()) ? _query : (BasicDBObject)_query.copy();
    }

    @Override
    public String toString() {
        return toString("count", "db", db(), "coll", coll(), "query", query());
    }

    private BasicDBObject _query;
}
