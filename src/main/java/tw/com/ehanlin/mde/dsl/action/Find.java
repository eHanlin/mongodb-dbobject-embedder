package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
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
    protected String cacheKey(DataStack data, DBCollection coll) {
        return "find_"+coll.getFullName()+"_"+AtEvaluator.eval(data, query()).toString()+"_"+AtEvaluator.eval(data, projection()).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        BasicDBList result = new BasicDBList();
        DBCursor cursor = coll.find(AtEvaluator.eval(data, query()), AtEvaluator.eval(data, projection()));
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        return result;
    }

    private MdeDBObject _projection;

}
