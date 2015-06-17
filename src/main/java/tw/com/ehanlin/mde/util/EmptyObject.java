package tw.com.ehanlin.mde.util;

import com.mongodb.*;
import tw.com.ehanlin.mde.dsl.mongo.*;
import tw.com.ehanlin.mde.dsl.Dsl;

public class EmptyObject {

    public static BasicDBObject BasicDBObject = new BasicDBObject();
    public static BasicDBList BasicDBList = new BasicDBList();
    public static MdeDBObject MdeDBObject = new MdeDBObject();
    public static MdeDBList MdeDBList = new MdeDBList();
    public static Dsl Dsl = new Dsl(null);
    public static Null Null = tw.com.ehanlin.mde.dsl.mongo.Null.instance;

    private EmptyObject() {

    }
}
