package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.mongo.At;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.ArrayList;

public class FindOneById extends FindOne {

    private static MdeDBObject query = new MdeDBObject("_id", new At("@"));

    public FindOneById(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    @Override
    public DBObject query(DataStack data) {
        return (DBObject)AtEvaluator.eval(data, query);
    }

    @Override
    public String toString() {
        ArrayList list = new ArrayList();
        list.add(KEY_DB); list.add(infos().get(KEY_DB));
        list.add(KEY_COLL); list.add(infos().get(KEY_COLL));

        if(infos().get(KEY_PROJECTION) != null){
            list.add(KEY_PROJECTION); list.add(infos().get(KEY_PROJECTION));
        }

        return toString("findOneById", list.toArray());
    }

}
