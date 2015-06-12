package tw.com.ehanlin.mde.dsl.mongo;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class At {

    private String _at;
    private String[] _keys;

    public At(String at) {
        _at = at;
        _keys = _at.split(".");
    }

    @Override
    public String toString() {
        return _at;
    }

    public Object eval(Object resource) {
        if(_keys.length <= 1)
            return resource;

        Object source = resource;
        for(int i=1 ; i<_keys.length ; i++) {
            if(source instanceof Map) {
                source = ((Map) source).get(_keys[i]);
            }else if(source instanceof List) {
                source = ((List) source).get(Integer.parseInt(_keys[i]));
            }
        }

        return source;
    }
}
