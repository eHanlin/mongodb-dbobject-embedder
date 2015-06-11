package tw.com.ehanlin.mde.dsl.action;

import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.EmptyObject;

public class Count extends Action {

    public Count(Scope scope, String db, String coll, MdeDBObject query) {
        super(scope, db, coll);
        _query = (query != null) ? query : EmptyObject.MdeDBObject;
    }

    public MdeDBObject query() {
        return (_query.isEmpty()) ? _query : (MdeDBObject)_query.copy();
    }

    @Override
    public String toString() {
        return toString("count", "db", db(), "coll", coll(), "query", query());
    }

    private MdeDBObject _query;
}
