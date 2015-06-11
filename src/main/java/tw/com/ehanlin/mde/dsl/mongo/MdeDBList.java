package tw.com.ehanlin.mde.dsl.mongo;

import com.mongodb.BasicDBList;
import com.mongodb.util.MdeJSON;

public class MdeDBList extends BasicDBList {

    public MdeDBList() {

    }

    @Override
    public String toString() {
        return MdeJSON.serialize(this);
    }

    @Override
    public Object copy() {
        MdeDBList result = new MdeDBList();
        result.addAll((BasicDBList)super.copy());
        return result;
    }
}
