package tw.com.ehanlin.mde.dsl.action.db;

import com.mongodb.*;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class DbAction extends Action {

    private static final ConcurrentHashMap<String, Boolean> _indexMap = new ConcurrentHashMap();

    public static final String KEY_DB = "db";
    public static final String KEY_COLL = "coll";
    public static final String KEY_INDEX = "index";

    public DbAction (Scope scope, MdeDBObject infos) {
        super(scope, infos);
    }

    public String db(DataStack data) {
        return (String) evalInfo(KEY_DB, data);
    }

    public String coll(DataStack data) {
        return (String) evalInfo(KEY_COLL, data);
    }

    public Object index(DataStack data) {
        return evalInfo(KEY_INDEX, data);
    }


    protected Object executeObjectWithCache(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        String key = cacheKey(data);
        if(!cache.containsKey(key)){
            Object result = executeObject(data, dbMap, cache, parallel);
            cache.put(key, (result != null) ? result : EmptyObject.Null);
        }
        Object result = cache.get(key);
        return (result != EmptyObject.Null) ? result : null;
    }

    protected abstract String cacheKey(DataStack data, String prefix);

    protected abstract Object executeObject(DataStack data, DBCollection coll);


    private Object executeObject(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        String _db = db(data);
        String _coll = coll(data);
        Object _index = index(data);

        if(_index != null){
            DBCollection dbColl = dbMap.get(_db).getCollection(_coll);
            String indexMapKey = dbColl.getFullName()+"_"+this.getClass().getName();
            if(!_indexMap.containsKey(indexMapKey)){
                List<DBObject> indexInfo = dbColl.getIndexInfo();
                List<String> indexStrs = indexInfo.stream().map(info -> info.get("key").toString()).collect(Collectors.toList());

                if(_index instanceof BasicDBObject) {
                    checkIndex(dbColl, (BasicDBObject)_index, indexStrs);
                }else if(_index instanceof BasicDBList) {
                    checkIndex(dbColl, (BasicDBList)_index, indexStrs);
                }

                _indexMap.put(indexMapKey, Boolean.TRUE);
            }
        }

        return executeObject(data, dbMap.get(_db).getCollection(_coll));
    }

    private void checkIndex(DBCollection dbColl, BasicDBObject index, List<String> indexStrs) {
        if(!indexStrs.contains(index.toString())){
            dbColl.createIndex(index);
        }
    }

    private void checkIndex(DBCollection dbColl, BasicDBList indexs, List<String> indexStrs) {
        indexs.forEach(index -> checkIndex(dbColl, (BasicDBObject)index, indexStrs));
    }

    private String cacheKey(DataStack data) {
        return cacheKey(data, this.getClass().getName() + "_" + db(data) + "_" + coll(data) + "_");
    }
}
