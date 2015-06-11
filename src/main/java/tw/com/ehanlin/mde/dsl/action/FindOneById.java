package tw.com.ehanlin.mde.dsl.action;

import tw.com.ehanlin.mde.dsl.mongo.At;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;

public class FindOneById extends Find {

    private static MdeDBObject query = new MdeDBObject("_id", new At("@"));

    public FindOneById(Scope scope, String db, String coll, MdeDBObject projection) {
        super(scope, db, coll, query, projection);
    }

    @Override
    public String toString() {
        return toString("findOneById", "db", db(), "coll", coll(), "projection", projection());
    }

}
