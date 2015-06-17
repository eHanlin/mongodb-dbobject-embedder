package my.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import tw.com.ehanlin.mde.dsl.action.DbAction;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

public class FindLimit2 extends DbAction {

    public FindLimit2(Scope scope, MdeDBObject infos) {
        super(scope, infos);

        MdeDBObject query = (MdeDBObject)infos.get("query");
        _query = (query != null) ? query : EmptyObject.MdeDBObject;

    }

    public MdeDBObject query() {
        return (_query.isEmpty()) ? _query : (MdeDBObject)_query.copy();
    }

    @Override
    public String toString() {
        return toString("findLimit2", "db", db(), "coll", coll(), "query", query());
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+ AtEvaluator.eval(data, query()).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        BasicDBList result = new BasicDBList();
        DBCursor cursor = coll.find(AtEvaluator.eval(data, query())).limit(2);
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        cursor.close();
        return result;
    }

    private MdeDBObject _query;
}
