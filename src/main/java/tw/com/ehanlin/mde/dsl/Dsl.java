package tw.com.ehanlin.mde.dsl;

import com.mongodb.BasicDBList;
import com.mongodb.DB;
import tw.com.ehanlin.mde.util.ConcurrentCache;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.*;
import java.util.function.BiFunction;
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

        for(Action action : _actions) {
            action.execute(data, dbMap, cache, parallel);
        }

        if(_dsls.isEmpty() && !isNest())
            return data.getSelf();

        if(_iterate == Iterate.LIST)
            return executeIterateList(data, dbMap, cache, parallel);
        else
            return executeIterateMap(data, dbMap, cache, parallel);
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

    public void nestedDsl(Dsl dsl) {
        _nest = dsl;
    }

    public Boolean isNest() {
        return _nest != null;
    }

    public Boolean isEmpty() {
        if(this == EmptyObject.Dsl) {
            return true;
        }
        return _actions.isEmpty() && _dsls.isEmpty() && !isNest();
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

    public Dsl nest() {
        return _nest;
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
    private Dsl _nest;
    private Iterate _iterate = Iterate.MAP;
    private Dsl _parent;
    private String _name;


    private void excuteMapData(DataStack data, Boolean parallel, BiFunction<Object, DataStack, Object> action) {
        excuteMapDataWithSpliterator(data, parallel, ((Map)data.getSelf()).keySet().spliterator(), action);
    }

    private void excuteMapDataWithSpliterator(DataStack data, Boolean parallel, Spliterator spliterator, BiFunction<Object, DataStack, Object> action) {
        Map map = (Map) data.getSelf();
        ConcurrentCache<Object, Object> tmp = new ConcurrentCache();
        StreamSupport.stream(spliterator, parallel).forEach(key -> {
            tmp.put(key, action.apply(key, new DataStack(data, map.get(key))));
        });
        tmp.forEach((k, v) -> map.put(k, v));
    }

    private void excuteListData(DataStack data, Boolean parallel, BiFunction<Integer, DataStack, Object> action) {
        excuteListDataWithSpliterator(data, parallel, IntStream.range(0, ((List)data.getSelf()).size()).spliterator(), action);
    }

    private void excuteListDataWithSpliterator(DataStack data, Boolean parallel, Spliterator<Integer> spliterator, BiFunction<Integer, DataStack, Object> action) {
        List list = (List) data.getSelf();
        ConcurrentCache<Integer, Object> tmp = new ConcurrentCache();
        StreamSupport.stream(spliterator, parallel).forEach(index -> {
            tmp.put(index, action.apply(index, new DataStack(data, list.get(index))));
        });
        tmp.forEach((k, v) -> list.set(k, v));
    }

    private Object executeIterateMap(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        Object source = data.getSelf();
        if(isNest()){
            source = _nest.execute(data, dbMap, cache, parallel);
            data.setSelf(source);
        }else{
            if(source instanceof Map){
                excuteMapDataWithSpliterator(data, parallel, _dsls.keySet().spliterator(), (key, childData) -> _dsls.get(key).execute(childData, dbMap, cache, parallel));
            }else if(source instanceof List){
                excuteListDataWithSpliterator(data, parallel, _dsls.keySet().stream().map(item -> Integer.parseInt(item)).spliterator(), (key, childData) -> _dsls.get(key.toString()).execute(childData, dbMap, cache, parallel));
            }
        }
        return source;
    }

    private Object executeIterateList(DataStack data, Map<String, DB> dbMap, Map<String, Object> cache, Boolean parallel) {
        Object source = data.getSelf();
        if(source instanceof Map){
            excuteMapData(data, parallel, (key, childData) -> executeIterateMap(childData, dbMap, cache, parallel));
        }else if(source instanceof List){
            excuteListData(data, parallel, (index, childData) -> executeIterateMap(childData, dbMap, cache, parallel));
        }else if(source instanceof Iterable){
            BasicDBList list = new BasicDBList();
            ((Iterable)source).forEach(item -> list.add(item));
            excuteListData(new DataStack(data.getParent(), list), parallel, (index, childData) -> executeIterateMap(childData, dbMap, cache, parallel));
            return list;
        }else{
            BasicDBList list = new BasicDBList();
            list.add(source);
            excuteListData(new DataStack(data.getParent(), list), parallel, (index, childData) -> executeIterateMap(childData, dbMap, cache, parallel));
            return list;
        }
        return source;
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
        if(key.length() > 0){
            result.append(" ");
        }

        if(!_dsls.isEmpty() || isNest()){
            if(_iterate == Iterate.MAP)
                result.append("<");
            else
                result.append("[");
            result.append(lineSeparator);
            String nextPadding = padding + layerPadding;

            if(isNest()){
                result.append(_nest.toString("", nextPadding));
                result.append(lineSeparator);
            }else{
                _dsls.forEach((k, dsl) -> {
                    result.append(dsl.toString(k, nextPadding));
                    result.append(lineSeparator);
                });
            }

            result.append(padding);
            if(_iterate == Iterate.MAP)
                result.append(">");
            else
                result.append("]");
        }

        return result.toString();
    }

}
