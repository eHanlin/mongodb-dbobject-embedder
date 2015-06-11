package tw.com.ehanlin.mde.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class SpliceStringReader {

    private StringReader reader;

    public SpliceStringReader(String str) {
        reader = new StringReader(str);
    }

    public Matcher splice(List<String> symbols) {
        StringBuilder prefix = new StringBuilder();
        try{
            int ch;
            while ((ch = reader.read()) > -1) {
                String current = String.valueOf((char)ch);
                if(symbols.contains(current)){
                    return new Matcher(prefix.toString(), current, this);
                }
                prefix.append(current);
            }
            return new Matcher(prefix.toString(), null, this);
        }catch(IOException ex){
            throw new IllegalStateException(ex.getMessage());
        }
    }

    public class Matcher {

        private String _prefix;
        private String _match;
        private SpliceStringReader _reader;
        private Boolean _finish;

        private Matcher(String prefix, String match, SpliceStringReader reader) {
            _prefix = prefix;
            _match = match;
            _reader = reader;
            _finish = match == null;
        }

        public String prefix() {
            return _prefix;
        }

        public String match() {
            return _match;
        }

        public SpliceStringReader reader() {
            return _reader;
        }

        public Boolean finish() {
            return _finish;
        }

    }

}
