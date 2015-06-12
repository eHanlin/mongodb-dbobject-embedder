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

    public MongoEmbedder() {

    }


    public Object embed(Object resource, String dslStr) {
        return embed(resource, DslParser.instance.parse(dslStr));
    }

    public Object embed(Object resource, Dsl dsl) {
        return dsl.execute(resource, dbMap, new ConcurrentHashMap(), false);
    }


    private final Map<String, DB> dbMap = new ConcurrentHashMap();

}
