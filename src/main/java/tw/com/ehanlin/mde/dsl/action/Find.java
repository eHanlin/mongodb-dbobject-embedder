package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
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

    @Override
    protected String cacheKey(Object resource, DBCollection coll) {
        return "find_"+coll.getFullName()+"_"+AtEvaluator.eval(resource, query()).toString()+"_"+AtEvaluator.eval(resource, projection()).toString();
    }

    @Override
    protected Object executeObject(Object resource, DBCollection coll) {
        BasicDBList result = new BasicDBList();
        DBCursor cursor = coll.find(AtEvaluator.eval(resource, query()), AtEvaluator.eval(resource, projection()));
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        return result;
    }

    private MdeDBObject _projection;

}
