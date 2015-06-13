package tw.com.ehanlin.mde.dsl;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.util.ConcurrentCache;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class Dsl {

    public enum Iterate {
        MAP,
        LIST
    }

    public Dsl() {

    }

    public Dsl(Iterate iterate) {
        _iterate = iterate;
    }

    public Dsl(Collection<Action> actions) {
        appendAction(actions);
    }

    public Dsl(Iterate iterate, Collection<Action> actions) {
        _iterate = iterate;
        appendAction(actions);
    }

    public Object execute(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        executeActions(data, dbMap, cache, parallel);
        if(_iterate == Iterate.LIST)
            return executeIterateList(data, dbMap, cache, parallel);
        else
            return executeIterateMap(data, dbMap, cache, parallel);
    }

    private Object executeIterateMap(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        Object source = data.getSelf();
        if(source instanceof Map){
            ConcurrentCache<String, Object> tmp = new ConcurrentCache();
            StreamSupport.stream(_dsls.keySet().spliterator(), parallel).forEach(key -> {
                Object result = _dsls.get(key).execute(new DataStack(data, ((Map) source).get(key)), dbMap, cache, parallel);
                tmp.put(key, result);
            });
            tmp.forEach((k, v) -> ((Map)source).put(k, v));
        }else if(source instanceof List){
            ConcurrentCache<Integer, Object> tmp = new ConcurrentCache();
            StreamSupport.stream(_dsls.keySet().spliterator(), parallel).forEach(key -> {
                Integer index = Integer.parseInt(key);
                Object result = _dsls.get(key).execute(new DataStack(data, ((List) source).get(index)), dbMap, cache, parallel);
                tmp.put(index, result);
            });
            tmp.forEach((k, v) -> ((List)source).set(k, v));
        }
        return source;
    }

    private Object executeIterateList(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        Object source = data.getSelf();
        if(source instanceof Map){
            Map map = (Map) source;
            ConcurrentCache<Object, Object> tmp = new ConcurrentCache();
            StreamSupport.stream(map.keySet().spliterator(), parallel).forEach(key -> {
                tmp.put(key, executeIterateMap(new DataStack(data, map.get(key)), dbMap, cache, parallel));
            });
            tmp.forEach((k, v) -> map.put(k, v));
        }else if(source instanceof List){
            List list = (List) source;
            ConcurrentCache<Integer, Object> tmp = new ConcurrentCache();
            StreamSupport.stream(IntStream.range(0, list.size()).spliterator(), parallel).forEach(index -> {
                tmp.put(index, executeIterateMap(new DataStack(data, list.get(index)), dbMap, cache, parallel));
            });
            tmp.forEach((k, v) -> list.set(k, v));
        }else if(source instanceof Iterable){
            BasicDBList list = new BasicDBList();
            ((Iterable)source).forEach(item -> list.add(item));
            ConcurrentCache<Integer, Object> tmp = new ConcurrentCache();
            StreamSupport.stream(IntStream.range(0, list.size()).spliterator(), parallel).forEach(index -> {
                tmp.put(index, executeIterateMap(new DataStack(data, list.get(index)), dbMap, cache, parallel));
            });
            tmp.forEach((k, v) -> list.set(k, v));
            return list;
        }else{
            BasicDBList list = new BasicDBList();
            list.add(source);
            DataStack listData = new DataStack(data.getParent(), list);
            list.set(0, executeIterateMap(new DataStack(listData, source), dbMap, cache, parallel));
            return list;
        }
        return source;
    }

    public void appendAction(Action action) {
        _actions.add(action);
        action._dsl = this;
    }

    public void appendAction(Collection<Action> actions) {
        for(Action action : actions){
            appendAction(action);
        }
    }

    public void appendDsl(String key, Dsl dsl) {
        _dsls.put(key, dsl);
        dsl._name = key;
        dsl._parent = this;
    }

    public Boolean isEmpty() {
        if(this == EmptyObject.Dsl) {
            return true;
        }
        return _actions.isEmpty() && _dsls.isEmpty();
    }

    public List<Action> actions() {
        return _actions;
    }

    public Iterate iterate() {
        return _iterate;
    }

    public Map<String, Dsl> dsls() {
        return _dsls;
    }

    public Dsl parent() {
        return _parent;
    }

    public String name() {
        return _name;
    }

    @Override
    public String toString() {
        return toString("", "");
    }

    void changeIterate(Iterate iterate) {
        _iterate = iterate;
    }




    private static String lineSeparator = System.getProperty("line.separator");
    private static String layerPadding = "  ";

    private List<Action> _actions = new ArrayList();
    private Map<String, Dsl> _dsls = new LinkedHashMap();
    private Iterate _iterate = Iterate.MAP;
    private Dsl _parent;
    private String _name;



    private String toString(String key, String padding) {
        StringBuilder result = new StringBuilder();
        for(Action action : _actions){
            result.append(padding);
            result.append(action.toString());
            result.append(lineSeparator);
        }

        result.append(padding);
        result.append(key);

        if(!_dsls.isEmpty()){
            result.append(padding);
            if(_iterate == Iterate.MAP)
                result.append("{");
            else
                result.append("[");
            result.append(lineSeparator);
            String nextPadding = padding + layerPadding;
            _dsls.forEach((k, dsl) -> {
                result.append(dsl.toString(k, nextPadding));
                result.append(lineSeparator);
            });
            result.append(padding);
            if(_iterate == Iterate.MAP)
                result.append("}");
            else
                result.append("]");
        }

        return result.toString();
    }

    private void executeActions(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        for(Action action : _actions) {
            action.execute(data, dbMap, cache, parallel);
        }
    }
}
