package tw.com.ehanlin.mde.dsl;

import com.mongodb.*;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.List;
import java.util.Map;

public abstract class Action {

    public enum Scope {
        PARENT,
        SALF,
        CHILD
    }

    public Action(Scope scope, String db, String coll) {
        _db = db;
        _coll = coll;
        _scope = scope;
    }

    public Scope scope() {
        return _scope;
    }

    public String db() {
        return _db;
    }

    public String coll() {
        return _coll;
    }

    public Dsl dsl() {
        return _dsl;
    }

    public Object execute(Object resource, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        DBCollection coll = dbMap.get(db()).getCollection(coll());
        switch(scope()) {
            case PARENT :
                return executeObjectWithCache(resource, coll, cache);
            case SALF : {
                if(resource instanceof Map && _dsl.name() != null) {
                    return executeObjectWithCache(((Map) resource).get(_dsl.name()), coll, cache);
                }
                if(resource instanceof List && _dsl.name() != null){
                    return executeObjectWithCache(((List) resource).get(Integer.parseInt(_dsl.name())), coll, cache);
                }
                return executeObjectWithCache(resource, coll, cache);
            }
            case CHILD : {
                if(resource instanceof Map && _dsl.name() != null) {
                    return executeCollection(((Map)resource).get(_dsl.name()), coll, cache);
                }
                if(resource instanceof List && _dsl.name() != null){
                    return executeCollection(((List) resource).get(Integer.parseInt(_dsl.name())), coll, cache);
                }
                return executeCollection(resource, coll, cache);
            }
            default :
                return executeObjectWithCache(resource, coll, cache);
        }
    }

    private Object executeCollection(Object resource, DBCollection coll, Map<String, Object> cache) {
        if(resource instanceof Map) {
            BasicDBObject result = new BasicDBObject();
            ((Map) resource).forEach((k, v) -> result.append(k.toString(), executeObjectWithCache(v, coll, cache)));
            return result;
        }
        if(resource instanceof Iterable) {
            BasicDBList result = new BasicDBList();
            ((Iterable)resource).forEach(item -> result.add(executeObjectWithCache(item, coll, cache)));
            return result;
        }
        return executeObjectWithCache(resource, coll, cache);
    }

    private Object executeObjectWithCache(Object resource, DBCollection coll, Map<String, Object> cache) {
        String key = cacheKey(resource, coll);
        if(!cache.containsKey(key)){
            Object result = executeObject(resource, coll);
            cache.put(key, (result != null) ? result : EmptyObject.Null);
        }
        Object result = cache.get(key);
        return (result != EmptyObject.Null) ? result : null;
    }

    protected abstract String cacheKey(Object resource, DBCollection coll);

    protected abstract Object executeObject(Object resource, DBCollection coll);

    protected String toString(String name, Object... args) {
        StringBuilder result = new StringBuilder("@");
        result.append(name);
        switch(_scope){
            case PARENT :
                result.append(" (");
                appendInfo(result, args);
                result.append(" )");
                break;
            case SALF :
                result.append(" <");
                appendInfo(result, args);
                result.append(" >");
                break;
            case CHILD :
                result.append(" [");
                appendInfo(result, args);
                result.append(" ]");
                break;
        }
        return result.toString();
    }

    private void appendInfo(StringBuilder result, Object[] args) {
        Boolean more = false;
        for(int i=0 ; i<args.length ; i += 2) {
            if(args[i+1] != null){
                if(more){
                    result.append(",");
                }
                result.append(" ");
                result.append(args[i]);
                result.append("=");
                result.append(args[i+1]);
                more = true;
            }
        }
    }

    private Scope _scope;
    private String _db;
    private String _coll;

    Dsl _dsl;

}
