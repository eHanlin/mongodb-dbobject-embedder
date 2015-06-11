package tw.com.ehanlin.mde.dsl.action;

import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.EmptyObject;

public class Find extends Count {

    public Find(Scope scope, String db, String coll, MdeDBObject query, MdeDBObject projection) {
        super(scope, db, coll, query);
        _projection = (projection != null) ? projection : EmptyObject.MdeDBObject;
    }

    public MdeDBObject projection() {
        return (_projection.isEmpty()) ? _projection : (MdeDBObject)_projection.copy();
    }

    @Override
    public String toString() {
        return toString("find", "db", db(), "coll", coll(), "query", query(), "projection", projection());
    }

    private MdeDBObject _projection;
}
