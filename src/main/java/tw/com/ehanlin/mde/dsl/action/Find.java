package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBObject;
import tw.com.ehanlin.mde.util.EmptyObject;

public class Find extends Count {

    public Find(Scope scope, String db, String coll, BasicDBObject query, BasicDBObject projection) {
        super(scope, db, coll, query);
        _projection = (projection != null) ? projection : EmptyObject.BasicDBObject;
    }

    public BasicDBObject projection() {
        return (_projection.isEmpty()) ? _projection : (BasicDBObject)_projection.copy();
    }

    @Override
    public String toString() {
        return toString("find", "db", db(), "coll", coll(), "query", query(), "projection", projection());
    }

    private BasicDBObject _projection;
}
