package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

public class FindOne extends Find {

    public FindOne(Scope scope, String db, String coll, MdeDBObject query, MdeDBObject projection) {
        super(scope, db, coll, query, projection);
    }

    @Override
    public String toString() {
        return toString("findOne", "db", db(), "coll", coll(), "query", query(), "projection", projection());
    }

    @Override
    protected String cacheKey(DataStack data, DBCollection coll) {
        return "findOne_"+coll.getFullName()+"_"+ AtEvaluator.eval(data, query()).toString()+"_"+AtEvaluator.eval(data, projection()).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        return coll.findOne(AtEvaluator.eval(data, query()), AtEvaluator.eval(data, projection()));
    }

}
