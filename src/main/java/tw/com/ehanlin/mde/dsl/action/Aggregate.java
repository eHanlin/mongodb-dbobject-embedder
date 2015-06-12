package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Aggregate extends Action {

    public Aggregate(Scope scope, String db, String coll, MdeDBList pipelines) {
        super(scope, db, coll);
        _pipelines = pipelines;
    }

    public MdeDBList pipelines() {
        return _pipelines;
    }

    @Override
    public String toString() {
        return toString("aggregate", "db", db(), "coll", coll(), "pipelines", pipelines());
    }

    @Override
    protected String cacheKey(Object resource, DBCollection coll) {
        BasicDBList pipes = (BasicDBList)AtEvaluator.eval(resource, pipelines());
        return "aggreate_"+coll.getFullName()+"_"+pipes.toString();
    }

    @Override
    protected Object executeObject(Object resource, DBCollection coll) {
        BasicDBList pipes = (BasicDBList)AtEvaluator.eval(resource, pipelines());
        List<DBObject> pipeList = new ArrayList();
        pipes.forEach(item -> pipeList.add((DBObject)item));
        BasicDBList result = new BasicDBList();
        coll.aggregate(pipeList).results().forEach(item -> result.add(item));
        return result;
    }

    private MdeDBList _pipelines;
}
