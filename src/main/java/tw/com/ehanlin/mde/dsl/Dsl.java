package tw.com.ehanlin.mde.dsl;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.*;
import java.util.stream.StreamSupport;

public class Dsl {

    public enum Iterate {
        MAP,
        LIST
    }

    private static String lineSeparator = System.getProperty("line.separator");
    private static String layerPadding = "  ";

    private List<Action> _actions = new ArrayList();
    private Map<String, Dsl> _dsls = new LinkedHashMap();
    private Iterate _iterate = Iterate.MAP;
    private Dsl _parent;
    private String _name;

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

    public Object execute(Object resource, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        Object source = executeActions(resource, dbMap, cache, parallel);
        if(_iterate == Iterate.LIST)
            return executeIterateList(source, dbMap, cache, parallel);
        else
            return executeIterateMap(source, dbMap, cache, parallel);
    }

    private Object executeIterateMap(Object source, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        if(source instanceof Map){
            StreamSupport.stream(_dsls.keySet().spliterator(), parallel).forEach(key -> {
                ((Map)source).put(key, _dsls.get(key).execute(((Map) source).get(key), dbMap, cache, parallel));
            });
        }else if(source instanceof List){
            StreamSupport.stream(_dsls.keySet().spliterator(), parallel).forEach(key -> {
                ((List)source).set(Integer.parseInt(key), _dsls.get(key).execute(((List) source).get(Integer.parseInt(key)), dbMap, cache, parallel));
            });
        }
        return source;
    }

    private Object executeIterateList(Object source, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        if(source instanceof Map){
            Map map = (Map) source;
            StreamSupport.stream(map.keySet().spliterator(), parallel).forEach(key -> {
                map.put(key, executeIterateMap(map.get(key), dbMap, cache, parallel));
            });
        }else if(source instanceof List){
            List list = (List) source;
            Map<Integer, Object> map = new HashMap();
            for(Integer i = 0 ; i < list.size() ; i++){
                map.put(i, list.get(i));
            }
            StreamSupport.stream(map.keySet().spliterator(), parallel).forEach(key -> {
                list.set(key, executeIterateMap(map.get(key), dbMap, cache, parallel));
            });
        }else if(source instanceof Iterable){
            BasicDBList list = new BasicDBList();
            Map<Integer, Object> map = new HashMap();
            Iterator iterator = ((Iterable) source).iterator();
            Integer index = 0;
            while (iterator.hasNext()){
                map.put(index, iterator.next());
                index++;
            }
            StreamSupport.stream(map.keySet().spliterator(), parallel).forEach(key -> {
                list.set(key, executeIterateMap(map.get(key), dbMap, cache, parallel));
            });
            return list;
        }else{
            BasicDBList list = new BasicDBList();
            list.add(executeIterateMap(source, dbMap, cache, parallel));
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

    private Object executeActions(Object resource, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        Object source = resource;
        for(Action action : _actions) {
            source = action.execute(resource, dbMap, cache, parallel);
        }
        return source;
    }
}
