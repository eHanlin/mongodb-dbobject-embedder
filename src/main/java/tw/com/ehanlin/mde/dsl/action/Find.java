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

public class Find extends Count {

    public Find(Scope scope, MdeDBObject infos) {
        super(scope, infos);

        MdeDBObject projection = (MdeDBObject)infos.get("projection");
        _projection = (projection != null) ? projection : EmptyObject.MdeDBObject;

        _sort = (MdeDBObject)infos.get("sort");

        Object skip = infos.get("skip");
        if(skip != null){
            _skip = Integer.parseInt(skip.toString());
        }

        Object limit = infos.get("limit");
        if(limit != null){
            _limit = Integer.parseInt(limit.toString());
        }
    }

    public MdeDBObject projection() {
        return (_projection.isEmpty()) ? _projection : (MdeDBObject)_projection.copy();
    }

    public MdeDBObject sort() {
        return _sort;
    }

    public Integer skip() {
        return _skip;
    }

    public Integer limit() {
        return _limit;
    }

    @Override
    public String toString() {
        ArrayList list = new ArrayList();
        list.add("db");
        list.add(db());
        list.add("coll");
        list.add(coll());
        list.add("query");
        list.add(query());
        list.add("projection");
        list.add(projection());
        if(sort() != null){
            list.add("sort");
            list.add(sort());
        }
        if(skip() != null){
            list.add("skip");
            list.add(skip());
        }
        if(limit() != null){
            list.add("limit");
            list.add(limit());
        }
        return toString("find", list.toArray());
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        return prefix+AtEvaluator.eval(data, query()).toString()+"_"+AtEvaluator.eval(data, projection()).toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        BasicDBList result = new BasicDBList();
        DBCursor cursor = coll.find(AtEvaluator.eval(data, query()), AtEvaluator.eval(data, projection()));
        if(sort() != null){
            cursor.sort(sort());
        }
        if(skip() != null){
            cursor.skip(skip());
        }
        if(limit() != null){
            cursor.limit(limit());
        }
        while(cursor.hasNext()){
            result.add(cursor.next());
        }
        cursor.close();
        return result;
    }

    private MdeDBObject _projection;
    private MdeDBObject _sort;
    private Integer _skip;
    private Integer _limit;

}
