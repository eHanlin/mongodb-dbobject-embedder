package tw.com.ehanlin.mde;

import com.mongodb.DB;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tw.com.ehanlin.mde.dsl.Dsl;
import tw.com.ehanlin.mde.dsl.DslParser;
import tw.com.ehanlin.mde.util.DuplicateConcurrentHashMap;

public class MongoEmbedder {

    public static final MongoEmbedder instance = new MongoEmbedder();
    public static final MongoEmbedder duplicate = new DuplicateMongoEmbedder();

    public static void registerDB(DB db) {
        instance.register(_defaultKey, db);
        duplicate.register(_defaultKey, db);
    }

    public static void registerDB(String key, DB db) {
        instance.register(key, db);
        duplicate.register(key, db);
    }

    private static String _defaultKey = "_default";


    public MongoEmbedder() {

    }

    public Object embed(Object resource, String dslStr) {
        return embed(resource, DslParser.instance.parse(dslStr));
    }

    public Object embed(Object resource, Dsl dsl) {
        System.out.println("\r\n< embed >\r\n[ resource ]\r\n"+resource+"\r\n[ dsl ]\r\n"+dsl);
        return dsl.execute(resource, dbMap, new ConcurrentHashMap(), false);
    }

    public void register(String key, DB db){
        dbMap.put(key, db);
    }

    protected final Map<String, DB> dbMap = new ConcurrentHashMap();


    public static class DuplicateMongoEmbedder extends MongoEmbedder {
        @Override
        public Object embed(Object resource, Dsl dsl) {
            return dsl.execute(resource, dbMap, new DuplicateConcurrentHashMap(), false);
        }
    }

}
