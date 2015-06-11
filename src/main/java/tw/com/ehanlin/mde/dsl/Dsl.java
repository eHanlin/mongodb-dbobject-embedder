package tw.com.ehanlin.mde.dsl;

import tw.com.ehanlin.mde.dsl.action.Action;
import tw.com.ehanlin.mde.util.EmptyObject;

import java.util.*;

public class Dsl {

    private static String lineSeparator = System.getProperty("line.separator");
    private static String layerPadding = "  ";

    private List<Action> _actions = new ArrayList();
    private Map<String, Dsl> _dsls = new LinkedHashMap();
    private Dsl _parent;

    public Dsl() {

    }

    public Dsl(Collection<Action> actions) {
        appendAction(actions);
    }

    public void appendAction(Action action) {
        _actions.add(action);
    }

    public void appendAction(Collection<Action> actions) {
        for(Action action : actions){
            appendAction(action);
        }
    }

    public void appendDsl(String key, Dsl dsl) {
        _dsls.put(key, dsl);
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

    public Map<String, Dsl> dsls() {
        return _dsls;
    }

    public Dsl parent() {
        return _parent;
    }

    @Override
    public String toString() {
        return toString("", "");
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
            result.append("{");
            result.append(lineSeparator);
            String nextPadding = padding + layerPadding;
            _dsls.forEach((k, dsl) -> {
                result.append(dsl.toString(k, nextPadding));
                result.append(lineSeparator);
            });
            result.append(padding);
            result.append("}");
        }

        return result.toString();
    }
}
