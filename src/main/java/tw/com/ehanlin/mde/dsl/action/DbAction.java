package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

import java.util.Map;

public abstract class DbAction extends Action {

    public DbAction (Scope scope, MdeDBObject infos) {
        super(scope, infos);
        _db = (String)infos.get("db");
        _coll = (String)infos.get("coll");
    }

    protected Object executeObject(DataStack data, Map<String, DB> dbMap) {
        return executeObject(data, dbMap.get(db()).getCollection(coll()));
    }

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

}
