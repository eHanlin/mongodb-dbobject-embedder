package tw.com.ehanlin.mde.util;

import com.mongodb.*;
import tw.com.ehanlin.mde.dsl.Dsl;

public class EmptyObject {

    public static BasicDBObject BasicDBObject = new BasicDBObject();
    public static BasicDBList BasicDBList = new BasicDBList();
    public static Dsl Dsl = new Dsl();

    private EmptyObject() {

    }
}
