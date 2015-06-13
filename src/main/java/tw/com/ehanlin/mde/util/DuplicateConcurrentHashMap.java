package tw.com.ehanlin.mde.util;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import java.util.concurrent.ConcurrentHashMap;

public class DuplicateConcurrentHashMap<K,V> extends ConcurrentHashMap<K,V> {

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public V get(Object key) {
        V result = super.get(key);
        if(result instanceof BasicDBObject){
            return (V)((BasicDBObject) result).copy();
        }
        if(result instanceof BasicDBList){
            return (V)((BasicDBList) result).copy();
        }
        return result;
    }

}
