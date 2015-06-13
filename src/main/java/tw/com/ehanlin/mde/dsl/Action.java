package tw.com.ehanlin.mde.dsl;

import com.mongodb.*;
import tw.com.ehanlin.mde.util.ConcurrentCache;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public abstract class Action {

    public enum Scope {
        PARENT,
        SELF,
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

    public void execute(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        DBCollection coll = dbMap.get(db()).getCollection(coll());
        switch(scope()) {
            case CHILD :
                data.setSelf(executeCollectionWithCache(data, coll, cache, parallel));
                break;
            case PARENT :
                if(data.hasParent()){
                    data.setSelf(executeObjectWithCache(data.getParent(), coll, cache));
                }else{
                    data.setSelf(executeObjectWithCache(data, coll, cache));
                }
                break;
            case SELF:
                data.setSelf(executeObjectWithCache(data, coll, cache));
                break;
        }
    }



    protected abstract String cacheKey(DataStack data, DBCollection coll);

    protected abstract Object executeObject(DataStack data, DBCollection coll);

    protected String toString(String name, Object... args) {
        StringBuilder result = new StringBuilder("@");
        result.append(name);
        switch(_scope){
            case PARENT :
                result.append(" (");
                appendInfo(result, args);
                result.append(" )");
                break;
            case SELF:
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



    private void excuteListData(DataStack data, DBCollection coll, Map<String, Object> cache, Boolean parallel) {
        List list = (List) data.getSelf();
        ConcurrentCache<Integer, Object> tmp = new ConcurrentCache();
        StreamSupport.stream(IntStream.range(0, list.size()).spliterator(), parallel).forEach(index -> {
            tmp.put(index, executeObjectWithCache(new DataStack(data, list.get(index)), coll, cache));
        });
        tmp.forEach((k, v) -> list.set(k, v));
    }

    private Object executeCollectionWithCache(DataStack data, DBCollection coll, Map<String, Object> cache, Boolean parallel) {
        Object source = data.getSelf();
        if(source instanceof Map){
            Map map = (Map) source;
            ConcurrentCache<Object, Object> tmp = new ConcurrentCache();
            StreamSupport.stream(map.keySet().spliterator(), parallel).forEach(key -> {
                tmp.put(key, executeObjectWithCache(new DataStack(data, map.get(key)), coll, cache));
            });
            tmp.forEach((k, v) -> map.put(k, v));
        }else if(source instanceof List){
            excuteListData(data, coll, cache, parallel);
        }else if(source instanceof Iterable){
            BasicDBList list = new BasicDBList();
            ((Iterable)source).forEach(item -> list.add(item));
            excuteListData(new DataStack(data.getParent(), list), coll, cache, parallel);
            return list;
        }else{
            BasicDBList list = new BasicDBList();
            list.add(source);
            excuteListData(new DataStack(data.getParent(), list), coll, cache, parallel);
            return list;
        }
        return source;
    }

    private Object executeObjectWithCache(DataStack data, DBCollection coll, Map<String, Object> cache) {
        String key = cacheKey(data, coll);
        if(!cache.containsKey(key)){
            Object result = executeObject(data, coll);
            cache.put(key, (result != null) ? result : EmptyObject.Null);
        }
        Object result = cache.get(key);
        return (result != EmptyObject.Null) ? result : null;
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
