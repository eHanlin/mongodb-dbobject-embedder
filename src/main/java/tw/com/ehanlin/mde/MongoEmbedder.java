package tw.com.ehanlin.mde;


import com.mongodb.DB;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.Dsl;
import tw.com.ehanlin.mde.dsl.DslParser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

public class MongoEmbedder {

    public static MongoEmbedder instance = new MongoEmbedder();

    public static void registerDB(DB db) {
        instance.register(_defaultKey, db);
    }

    public static void registerDB(String key, DB db) {
        instance.register(key, db);
    }

    private static String _defaultKey = "_default";


    public MongoEmbedder() {

    }

    public Object embed(Object resource, String dslStr) {
        return embed(resource, DslParser.instance.parse(dslStr));
    }

    public Object embed(Object resource, Dsl dsl) {
        System.out.println("< embed >\r\n[ resource ]\r\n"+resource+"\r\n[ dsl ]\r\n"+dsl);
        return dsl.execute(resource, dbMap, new ConcurrentHashMap(), false);
    }

    public void register(String key, DB db){
        dbMap.put(key, db);
    }

    private final Map<String, DB> dbMap = new ConcurrentHashMap();

}
