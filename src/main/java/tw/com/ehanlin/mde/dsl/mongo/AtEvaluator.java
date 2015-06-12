package tw.com.ehanlin.mde.dsl.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class AtEvaluator {

    public static DBObject eval(Object resource, DBObject structure) {

        if(structure instanceof BasicDBObject) {
            BasicDBObject clone = (BasicDBObject)((BasicDBObject) structure).copy();
            replace(resource, clone);
            return (DBObject) JSON.parse(clone.toString());
        }

        if(structure instanceof BasicDBList) {
            BasicDBList clone = (BasicDBList)((BasicDBList) structure).copy();
            replace(resource, clone);
            return (DBObject) JSON.parse(clone.toString());
        }

        return structure;
    }

    private static void replace(Object resource, BasicDBObject structure) {
        structure.forEach((k, v) -> {
            if(v instanceof BasicDBObject) {
                replace(resource, (BasicDBObject)v);
            }else if(v instanceof BasicDBList) {
                replace(resource, (BasicDBList)v);
            }else if(v instanceof At) {
                structure.put(k, ((At) v).eval(resource));
            }
        });
    }

    private static void replace(Object resource, BasicDBList structure) {
        for(int i=0 ; i<structure.size() ; i++){
            Object v = structure.get(i);
            if(v instanceof BasicDBObject) {
                replace(resource, (BasicDBObject)v);
            }else if(v instanceof BasicDBList) {
                replace(resource, (BasicDBList)v);
            }else if(v instanceof At) {
                structure.put(i, ((At)v).eval(resource));
            }
        }
    }

}
