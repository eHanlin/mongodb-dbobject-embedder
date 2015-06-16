package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.AtEvaluator;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBList;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.DataStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Aggregate extends DbAction {

    public Aggregate(Scope scope, MdeDBObject infos) {
        super(scope, infos);
        _pipelines = (MdeDBList)infos.get("pipelines");
    }

    public MdeDBList pipelines() {
        return _pipelines;
    }

    @Override
    public String toString() {
        return toString("aggregate", "db", db(), "coll", coll(), "pipelines", pipelines());
    }

    @Override
    protected String cacheKey(DataStack data, String prefix) {
        BasicDBList pipes = (BasicDBList)AtEvaluator.eval(data, pipelines());
        return prefix+pipes.toString();
    }

    @Override
    protected Object executeObject(DataStack data, DBCollection coll) {
        BasicDBList pipes = (BasicDBList)AtEvaluator.eval(data, pipelines());
        List<DBObject> pipeList = new ArrayList();
        pipes.forEach(item -> pipeList.add((DBObject)item));
        BasicDBList result = new BasicDBList();
        coll.aggregate(pipeList).results().forEach(item -> result.add(item));
        return result;
    }

    private MdeDBList _pipelines;
}
