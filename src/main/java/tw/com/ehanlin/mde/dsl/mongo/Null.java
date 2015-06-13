package tw.com.ehanlin.mde.dsl.mongo;

import java.util.Objects;

public class Null {

    public static final Null instance = new Null();

    private static final String _toString = "null";
    private static final int _hashCode = Objects.hashCode(instance);

    private Null(){

    }

    @Override
    public int hashCode() {
        return _hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == instance;
    }

    @Override
    public String toString() {
        return _toString;
    }

}
