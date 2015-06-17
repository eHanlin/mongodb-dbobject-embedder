package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

public class FindOne extends Count {

    public FindOne(Scope scope, MdeDBObject infos) {
        super(scope, infos);

        MdeDBObject projection = (MdeDBObject)infos.get("projection");
        _projection = (projection != null) ? projection : EmptyObject.MdeDBObject;
    }

    public MdeDBObject projection() {
        return (_projection.isEmpty()) ? _projection : (MdeDBObject)_projection.copy();
    }

    @Override
    public String toString() {
        return toString("findOne", "db", db(), "coll", coll(), "query", query(), "projection", projection());
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+AtEvaluator.eval(data, query()).toString()+"_"+AtEvaluator.eval(data, projection()).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        return coll.findOne(AtEvaluator.eval(data, query()), AtEvaluator.eval(data, projection()));
    }

    private MdeDBObject _projection;
}
