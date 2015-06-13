package tw.com.ehanlin.mde.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;

public class ConcurrentCache<K, V> {

    public ConcurrentCache() {
        _cache = new ConcurrentHashMap();
    }

    public ConcurrentCache(ConcurrentMap concurrentMap) {
        _cache = concurrentMap;
    }

    public void put(K key, V value) {
        Object k = (key != null) ? key : EmptyObject.Null;
        Object v = (value != null) ? value : EmptyObject.Null;
        _cache.put(k, v);
    }

    public V get(K key) {
        Object result = _cache.get(key);
        return (V)((result == EmptyObject.Null) ? null : result);
    }

    public boolean containsKey(K key) {
        Object k = (key != null) ? key : EmptyObject.Null;
        return _cache.containsKey(key);
    }

    public void forEach(BiConsumer<? super K, ? super V> action) {
        _cache.forEach((key, value) -> {
            Object k = (key != EmptyObject.Null) ? key : null;
            Object v = (value != EmptyObject.Null) ? value : null;
            action.accept((K)k, (V)v);
        });
    }

    private ConcurrentMap _cache;

}
