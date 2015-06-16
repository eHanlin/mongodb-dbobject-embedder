package tw.com.ehanlin.mde.dsl;

import com.mongodb.*;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.ConcurrentCache;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public abstract class Action {

    public enum Scope {
        PARENT,
        SELF,
        CHILD
    }

    public Action(Scope scope, MdeDBObject infos) {
        _scope = scope;
    }

    public Scope scope() {
        return _scope;
    }

    public Dsl dsl() {
        return _dsl;
    }

    public void execute(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        switch(scope()) {
            case CHILD :
                data.setSelf(executeCollectionWithCache(data, dbMap, cache, parallel));
                break;
            case PARENT :
                if(data.hasParent()){
                    data.setSelf(executeObjectWithCache(data.getParent(), dbMap, cache));
                }else{
                    data.setSelf(executeObjectWithCache(data, dbMap, cache));
                }
                break;
            case SELF:
                data.setSelf(executeObjectWithCache(data, dbMap, cache));
                break;
        }
    }



    protected abstract String cacheKey(DataStack data);

    protected abstract Object executeObject(DataStack data, Map<String, DB> dbMap);

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



    private void excuteListData(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        List list = (List) data.getSelf();
        ConcurrentCache<Integer, Object> tmp = new ConcurrentCache();
        StreamSupport.stream(IntStream.range(0, list.size()).spliterator(), parallel).forEach(index -> {
            tmp.put(index, executeObjectWithCache(new DataStack(data, list.get(index)), dbMap, cache));
        });
        tmp.forEach((k, v) -> list.set(k, v));
    }

    private Object executeCollectionWithCache(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        Object source = data.getSelf();
        if(source instanceof Map){
            Map map = (Map) source;
            ConcurrentCache<Object, Object> tmp = new ConcurrentCache();
            StreamSupport.stream(map.keySet().spliterator(), parallel).forEach(key -> {
                tmp.put(key, executeObjectWithCache(new DataStack(data, map.get(key)), dbMap, cache));
            });
            tmp.forEach((k, v) -> map.put(k, v));
        }else if(source instanceof List){
            excuteListData(data, dbMap, cache, parallel);
        }else if(source instanceof Iterable){
            BasicDBList list = new BasicDBList();
            ((Iterable)source).forEach(item -> list.add(item));
            excuteListData(new DataStack(data.getParent(), list), dbMap, cache, parallel);
            return list;
        }else{
            BasicDBList list = new BasicDBList();
            list.add(source);
            excuteListData(new DataStack(data.getParent(), list), dbMap, cache, parallel);
            return list;
        }
        return source;
    }

    private Object executeObjectWithCache(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache) {
        String key = cacheKey(data);
        if(!cache.containsKey(key)){
            Object result = executeObject(data, dbMap);
            cache.put(key, (result != null) ? result : EmptyObject.Null);
        }
        Object result = cache.get(key);
        return (result != EmptyObject.Null) ? result : null;
    }

    private void appendInfo(StringBuilder result, Object[] args) {
        for(int i=0 ; i<args.length ; i += 2) {
            if(args[i+1] != null){
                result.append(" ");
                result.append(args[i]);
                result.append("=");
                result.append(args[i+1]);
            }
        }
    }

    private Scope _scope;

    Dsl _dsl;

}
