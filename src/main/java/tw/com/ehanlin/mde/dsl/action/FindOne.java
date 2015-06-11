package tw.com.ehanlin.mde.dsl.action;

import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;

public class FindOne extends Find {

    public FindOne(Scope scope, String db, String coll, MdeDBObject query, MdeDBObject projection) {
        super(scope, db, coll, query, projection);
    }

    @Override
    public String toString() {
        return toString("findOne", "db", db(), "coll", coll(), "query", query(), "projection", projection());
    }

}
