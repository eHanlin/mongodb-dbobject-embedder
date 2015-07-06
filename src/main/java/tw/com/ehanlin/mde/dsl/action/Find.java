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

public class Find extends FindOne {

    public static final String KEY_SORT = "sort";
    public static final String KEY_SKIP = "skip";
    public static final String KEY_LIMIT = "limit";

    public Find(Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public DBObject projection(DataStack data) {
        Object _projection = evalInfo(KEY_PROJECTION, data);
        return (DBObject)((_projection != null) ? _projection : EmptyObject.BasicDBObject);
    }

    public DBObject sort(DataStack data) {
        Object _sort = evalInfo(KEY_SORT, data);
        return (DBObject)((_sort != null) ? _sort : EmptyObject.BasicDBObject);
    }

    public Integer skip(DataStack data) {
        return (Integer) evalInfo(KEY_SKIP, data);
    }

    public Integer limit(DataStack data) {
        return (Integer) evalInfo(KEY_LIMIT, data);
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

        if(infos().get(KEY_SORT) != null){
            list.add(KEY_SORT); list.add(infos().get(KEY_SORT));
        }

        if(infos().get(KEY_SKIP) != null){
            list.add(KEY_SKIP); list.add(infos().get(KEY_SKIP));
        }

        if(infos().get(KEY_LIMIT) != null){
            list.add(KEY_LIMIT); list.add(infos().get(KEY_LIMIT));
        }

        return toString("find", list.toArray());
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return super.cacheKey(data, prefix)+"_"+
            sort(data)+"_"+
            skip(data)+"_"+
            limit(data);
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        BasicDBList result = new BasicDBList();
        DBCursor cursor = coll.find(query(data), projection(data));
        if(infos().get(KEY_SORT) != null){
            cursor.sort(sort(data));
        }
        if(infos().get(KEY_SKIP) != null){
            cursor.skip(skip(data));
        }
        if(infos().get(KEY_LIMIT) != null){
            cursor.limit(limit(data));
        }
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        cursor.close();
        return result;
    }

}
