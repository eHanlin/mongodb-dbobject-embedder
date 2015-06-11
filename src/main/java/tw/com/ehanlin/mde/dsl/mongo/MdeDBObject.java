package tw.com.ehanlin.mde.dsl.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.util.MdeJSON;

import java.util.Map;

public class MdeDBObject extends BasicDBObject {

    public MdeDBObject() {
    }

    public MdeDBObject(int size) {
        super(size);
    }

    public MdeDBObject(String key, Object value) {
        super(key, value);
    }

    public MdeDBObject(Map map) {
        super(map);
    }

    @Override
    public String toString() {
        return MdeJSON.serialize(this);
    }

    @Override
    public Object copy() {
        return new MdeDBObject(((BasicDBObject)super.copy()));
    }
}
