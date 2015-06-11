package tw.com.ehanlin.mde.dsl.action;

import com.mongodb.BasicDBList;

public class Aggregate extends Action {

    public Aggregate(Scope scope, String db, String coll, BasicDBList pipelines) {
        super(scope, db, coll);
        _pipelines = pipelines;
    }

    public BasicDBList pipelines() {
        return _pipelines;
    }

    @Override
    public String toString() {
        return toString("aggregate", "db", db(), "coll", coll(), "pipelines", pipelines());
    }

    private BasicDBList _pipelines;
}
