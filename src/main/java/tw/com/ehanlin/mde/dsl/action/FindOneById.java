package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBObject;

public class FindOneById extends Find {

    private static BasicDBObject query = new BasicDBObject("_id", "@");

    public FindOneById(Scope scope, String db, String coll, BasicDBObject projection) {
        super(scope, db, coll, query, projection);
    }

    @Override
    public String toString() {
        return toString("findOneById", "db", db(), "coll", coll(), "projection", projection());
    }

}
