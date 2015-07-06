package tw.com.ehanlin.mde.dsl.mongo;

import tw.com.ehanlin.mde.util.DataStack;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class At {

    private static final String _root = "(<[{root}]>)";
    private static final String _rootReplace = "."+_root;
    private static final String _rootRegex = "\\.\\.\\./";
    private static final String _parent = "(<[{parent}]>)";
    private static final String _parentReplace = "."+_root;
    private static final String _parentRegex = "\\.\\./";
    private static final String _childRegex = "\\.";

    private String _at;
    private String[] _keys;

    public At(String at) {
        _at = at;
        _keys = at.replaceAll(_rootRegex, _rootReplace).replaceAll(_parentRegex, _parentReplace).split(_childRegex);
    }

    @Override
    public String toString() {
        return _at;
    }

    public Object eval(DataStack data) {
        if(_keys.length <= 1)
            return data.getSelf();

        DataStack currentData = data;
        for(int i=1 ; i<_keys.length ; i++) {
            String key = _keys[i];
            if(key.equals(_root)){
                currentData = findRoot(currentData);
            }else if(key.equals(_parent)){
                currentData = currentData.getParent();
            }else{
                Object self = currentData.getSelf();
                if(self instanceof Map) {
                    currentData = new DataStack(currentData, ((Map)self).get(key));
                }else if(self instanceof List) {
                    currentData = new DataStack(currentData, ((List)self).get(Integer.parseInt(key)));
                }
            }
        }

        return currentData.getSelf();
    }

    private DataStack findRoot(DataStack data) {
        DataStack current = data;
        while(current.hasParent()){
            current = current.getParent();
        }
        return current;
    }
}
