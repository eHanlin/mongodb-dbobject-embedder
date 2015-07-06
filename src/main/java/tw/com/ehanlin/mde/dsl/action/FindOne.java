package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.ArrayList;

public class FindOne extends Count {

    public static final String KEY_PROJECTION = "projection";

    public FindOne(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public DBObject projection(DataStack data) {
        Object _projection = evalInfo(KEY_PROJECTION, data);
        return (DBObject)((_projection != null) ? _projection : EmptyObject.BasicDBObject);
    }

    @Override
    public String toString() {
        ArrayList list = new ArrayList();
        list.add(KEY_DB); list.add(infos().get(KEY_DB));
        list.add(KEY_COLL); list.add(infos().get(KEY_COLL));
        list.add(KEY_QUERY); list.add(infos().get(KEY_QUERY));

        if(infos().get(KEY_PROJECTION) != null){
            list.add(KEY_PROJECTION); list.add(infos().get(KEY_PROJECTION));
        }

        return toString("findOne", list.toArray());
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return super.cacheKey(data, prefix)+"_"+projection(data);
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        return coll.findOne(query(data), projection(data));
    }

}
