package tw.com.ehanlin.mde.dsl.action;

public abstract class Action {

    public enum Scope {
        PARENT,
        SALF,
        CHILD
    }

    public Action(Scope scope, String db, String coll) {
        _db = db;
        _coll = coll;
        _scope = scope;
    }

    public Scope scope() {
        return _scope;
    }

    public String db() {
        return _db;
    }

    public String coll() {
        return _coll;
    }


    protected String toString(String name, Object... args) {
        StringBuilder result = new StringBuilder("@");
        result.append(name);
        switch(_scope){
            case PARENT :
                result.append(" (");
                appendInfo(result, args);
                result.append(" )");
                break;
            case SALF :
                result.append(" <");
                appendInfo(result, args);
                result.append(" >");
                break;
            case CHILD :
                result.append(" [");
                appendInfo(result, args);
                result.append(" ]");
                break;
        }
        return result.toString();
    }

    private void appendInfo(StringBuilder result, Object[] args) {
        Boolean more = false;
        for(int i=0 ; i<args.length ; i += 2) {
            if(args[i+1] != null){
                if(more){
                    result.append(",");
                }
                result.append(" ");
                result.append(args[i]);
                result.append("=");
                result.append(args[i+1]);
                more = true;
            }
        }
    }

    private Scope _scope;
    private String _db;
    private String _coll;

}
