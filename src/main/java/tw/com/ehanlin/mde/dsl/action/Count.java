package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

public class Count extends DbAction {

    public Count(Scope scope, MdeDBObject infos) {
        super(scope, infos);
        MdeDBObject query = (MdeDBObject)infos.get("query");
        _query = (query != null) ? query : EmptyObject.MdeDBObject;
    }

    public MdeDBObject query() {
        return (_query.isEmpty()) ? _query : (MdeDBObject)_query.copy();
    }

    @Override
    public String toString() {
        return toString("count", "db", db(), "coll", coll(), "query", query());
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+AtEvaluator.eval(data, query()).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        return coll.count(AtEvaluator.eval(data, query()));
    }

    private MdeDBObject _query;
}
