package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.*;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class DbAction extends Action {

    private static final ConcurrentHashMap<String, Boolean> _indexMap = new ConcurrentHashMap();

    public DbAction (Scope scope, MdeDBObject infos) {
        super(scope, infos);
        _db = (String)infos.get("db");
        _coll = (String)infos.get("coll");
        _index = infos.get("index");
    }

    private void checkIndex(DBCollection dbColl, BasicDBObject index, List<String> indexStrs) {
        if(!indexStrs.contains(index.toString())){
            dbColl.createIndex(index);
        }
    }

    private void checkIndex(DBCollection dbColl, BasicDBList indexs, List<String> indexStrs) {
        indexs.forEach(index -> checkIndex(dbColl, (BasicDBObject)index, indexStrs));
    }

    @Override
    protected Object executeObject(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        if(_index != null){
            DBCollection dbColl = dbMap.get(db()).getCollection(coll());
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

        return executeObject(data, dbMap.get(db()).getCollection(coll()));
    }

    @Override
    protected String cacheKey(DataStack data) {
        return cacheKey(data, this.getClass().getName()+"_"+db()+"_"+coll()+"_");
    }

    protected abstract String cacheKey(DataStack data, String prefix);

    protected abstract Object executeObject(DataStack data, DBCollection coll);

    public String db() {
        return _db;
    }

    public String coll() {
        return _coll;
    }

    private String _db;
    private String _coll;
    private Object _index;

}
