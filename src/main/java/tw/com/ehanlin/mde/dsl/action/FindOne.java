package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBObject;

public class FindOne extends Find {

    public FindOne(Scope scope, String db, String coll, BasicDBObject query, BasicDBObject projection) {
        super(scope, db, coll, query, projection);
    }

    @Override
    public String toString() {
        return toString("findOne", "db", db(), "coll", coll(), "query", query(), "projection", projection());
    }

}
