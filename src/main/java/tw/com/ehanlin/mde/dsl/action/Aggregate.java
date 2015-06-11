package tw.com.ehanlin.mde.dsl.action;

import tw.com.ehanlin.mde.dsl.Action;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBList;

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

    private MdeDBList _pipelines;
}
