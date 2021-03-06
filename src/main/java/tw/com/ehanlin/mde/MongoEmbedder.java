package tw.com.ehanlin.mde;

import com.mongodb.DB;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tw.com.ehanlin.mde.dsl.Dsl;
import tw.com.ehanlin.mde.dsl.DslParser;
import tw.com.ehanlin.mde.util.DataStack;
import tw.com.ehanlin.mde.util.DuplicateConcurrentHashMap;

public class MongoEmbedder {

    public static final DslParser dslParser = new DslParser();
    public static final MongoEmbedder instance = new DuplicateMongoEmbedder();
    public static final MongoEmbedder noDuplicate = new MongoEmbedder();

    public static void registerDB(DB db) {
        instance.register(_defaultKey, db);
        noDuplicate.register(_defaultKey, db);
    }

    public static void registerDB(String key, DB db) {
        instance.register(key, db);
        noDuplicate.register(key, db);
    }

    private static String _defaultKey = "_default";


    public MongoEmbedder() {

    }

    public Object embed(String dslStr) {
        return embed(null, dslStr);
    }

    public Object embed(String dslStr, Boolean parallel) {
        return embed(null, dslStr, parallel);
    }

    public Object embed(Dsl dsl) {
        return embed(null, dsl);
    }

    public Object embed(Dsl dsl, Boolean parallel) {
        return embed(null, dsl, parallel);
    }

    public Object embed(Object resource, String dslStr) {
        return embed(resource, dslParser.parse(dslStr));
    }

    public Object embed(Object resource, String dslStr, Boolean parallel) {
        return embed(resource, dslParser.parse(dslStr), parallel);
    }

    public Object embed(Object resource, Dsl dsl) {
        return dsl.execute(new DataStack(null, resource), dbMap, new ConcurrentHashMap(), false);
    }

    public Object embed(Object resource, Dsl dsl, Boolean parallel) {
        return dsl.execute(new DataStack(null, resource), dbMap, new ConcurrentHashMap(), parallel);
    }

    public void register(String key, DB db){
        dbMap.put(key, db);
    }

    protected final Map<String, DB> dbMap = new ConcurrentHashMap();


    public static class DuplicateMongoEmbedder extends MongoEmbedder {
        @Override
        public Object embed(Object resource, Dsl dsl) {
            return dsl.execute(new DataStack(null, resource), dbMap, new DuplicateConcurrentHashMap(), false);
        }
    }

}
