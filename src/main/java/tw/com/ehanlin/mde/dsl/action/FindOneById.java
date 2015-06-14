package tw.com.ehanlin.mde.dsl.action;

import tw.com.ehanlin.mde.dsl.mongo.At;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;

public class FindOneById extends FindOne {

    private static MdeDBObject query = new MdeDBObject("_id", new At("@"));

    public FindOneById(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    @Override
    public MdeDBObject query() {
        return (MdeDBObject)query.copy();
    }

    @Override
    public String toString() {
        return toString("findOneById", "db", db(), "coll", coll(), "projection", projection());
    }

}
