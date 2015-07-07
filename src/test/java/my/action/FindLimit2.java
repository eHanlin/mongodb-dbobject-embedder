package my.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.action.db.DbAction;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

public class FindLimit2 extends DbAction {

    public static final String KEY_QUERY = "query";

    public FindLimit2(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public DBObject query(DataStack data) {
        Object _query = evalInfo(KEY_QUERY, data);
        return (DBObject)((_query != null) ? _query : EmptyObject.BasicDBObject);
    }

    @Override
    public String toString() {
        return toString("findLimit2",
                KEY_DB, infos().get(KEY_DB),
                KEY_COLL, infos().get(KEY_COLL),
                KEY_QUERY, infos().get(KEY_QUERY));
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+query(data);
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        BasicDBList result = new BasicDBList();
        DBCursor cursor = coll.find(query(data)).limit(2);
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        cursor.close();
        return result;
    }

}
