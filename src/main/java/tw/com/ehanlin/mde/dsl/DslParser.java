package tw.com.ehanlin.mde.dsl;

import tw.com.ehanlin.mde.dsl.action.*;
import tw.com.ehanlin.mde.dsl.mongo.At;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBList;
import tw.com.ehanlin.mde.dsl.mongo.MdeDBObject;
import tw.com.ehanlin.mde.util.EmptyObject;
import tw.com.ehanlin.mde.util.SpliceStringReader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DslParser {

    public static DslParser instance = new DslParser();

    private ConcurrentHashMap<String, Class<? extends Action>> actionMap = new ConcurrentHashMap();

    public DslParser() {
        registerAction("find", Find.class);
        registerAction("findOne", FindOne.class);
        registerAction("findOneById", FindOneById.class);
        registerAction("distinct", Distinct.class);
        registerAction("count", Count.class);
        registerAction("aggregate", Aggregate.class);
    }

    public void registerAction(String key, Class<? extends Action> type) {
        actionMap.put(key, type);
    }

    public Dsl parse(String dsl) {

        if(cache.containsKey(dsl)){
            return cache.get(dsl);
        }

        SpliceStringReader reader = new SpliceStringReader(dsl);
        SpliceStringReader.Matcher matcher = reader.splice(rootSymbols);
        if(matcher.finish()){
            cache.put(dsl, EmptyObject.Dsl);
            return EmptyObject.Dsl;
        }

        List<Action> actions = new ArrayList();
        do{
            switch(matcher.match()){
                case "@" :
                    Action action = parseAction(reader);
                    if(action != null){
                        actions.add(action);
                    }
                    break;
                case "<" :
                case "[" :
                    Dsl result = (matcher.match().equals("<")) ? new Dsl(Dsl.Iterate.MAP, actions) : new Dsl(Dsl.Iterate.LIST, actions);
                    parseContent(result, reader);
                    cache.put(dsl, result);
                    return result;
            }
        }while(!((matcher = reader.splice(rootSymbols)).finish()));

        cache.put(dsl, EmptyObject.Dsl);
        return EmptyObject.Dsl;
    }



    private List<String> rootSymbols = Arrays.asList("@", "<", "[");
    private List<String> readActionOrPropertySymbols = Arrays.asList("@", "<", "[", ">", "]");
    private List<String> actionScopeSymbols = Arrays.asList("(", "<", "[");
    private List<String> actionInfoSymbols = Arrays.asList("=", ",", " ", "\b", "\f", "\t", "\r", "\n", "[", "{", ")", ">", "]");
    private List<String> mongoSymbols = Arrays.asList(":", ",", "{", "[", "]", "}");
    private Pattern propertyPattern = Pattern.compile("\\S+");
    private Pattern stringPattern = Pattern.compile("^(?:'(.*)'|\"(.*)\")$");
    private Pattern atPattern = Pattern.compile("^@.*");
    private Pattern longPattern = Pattern.compile("^[+-]?\\d+$");
    private Pattern doublePattern = Pattern.compile("^[+-]?\\d*\\.\\d+$");
    private Pattern booleanPattern = Pattern.compile("^(true|false)$", Pattern.CASE_INSENSITIVE);


    private void parseContent(Dsl current, SpliceStringReader reader){
        List<Action> actions = new ArrayList();
        SpliceStringReader.Matcher matcher;
        loop: while(!((matcher = reader.splice(readActionOrPropertySymbols)).finish())){
            Dsl lastDsl = null;
            for(String parseProperty : parseProperties(matcher.prefix().trim())){
                lastDsl = new Dsl(actions);
                current.appendDsl(parseProperty, lastDsl);
                actions = new ArrayList();
            }
            switch(matcher.match()){
                case "@" :
                    actions.add(parseAction(reader));
                    break;
                case "<" :
                case "[" :
                    if(lastDsl == null){
                        lastDsl = new Dsl(((matcher.match().equals("<")) ? Dsl.Iterate.MAP : Dsl.Iterate.LIST), actions);
                        actions = new ArrayList();
                        current.nestedDsl(lastDsl);
                    }else{
                        lastDsl.changeIterate((matcher.match().equals("<")) ? Dsl.Iterate.MAP : Dsl.Iterate.LIST);
                    }
                    parseContent(lastDsl, reader);
                    break;
                case ">" :
                case "]" :
                    break loop;
            }
        }
    }


    private List<String> parseProperties(String properties){
        List<String> result = new ArrayList();
        Matcher matcher = propertyPattern.matcher(properties);
        while (matcher.find()){
            result.add(matcher.group());
        }
        return result;
    }


    private Object parseMongoContent(String content) {

        Matcher stringMatcher = stringPattern.matcher(content);
        if(stringMatcher.matches()){
            String result = stringMatcher.group(1);
            return (result != null) ? result : stringMatcher.group(2);
        }

        if(atPattern.matcher(content).matches()){
            return new At(content);
        }

        if(longPattern.matcher(content).matches()){
            return Long.parseLong(content);
        }

        if(doublePattern.matcher(content).matches()){
            return Double.parseDouble(content);
        }

        Matcher booleanMatcher = booleanPattern.matcher(content);
        if(booleanMatcher.matches()){
            return booleanMatcher.group(0).toLowerCase().equals("true");
        }

        return content;
    }

    private String parsePairSymbolsContent(String start, String end, SpliceStringReader reader){
        List<String> symbols = Arrays.asList(start, end);
        StringBuilder result = new StringBuilder(start);
        Integer count = 1;
        SpliceStringReader.Matcher matcher;
        while(!((matcher = reader.splice(symbols)).finish())){
            result.append(matcher.prefix());
            result.append(matcher.match());
            if(matcher.match().equals(start)){
                count += 1;
            }else{
                count -= 1;
            }
            if(count <= 0){
                return result.toString();
            }
        }
        return result.toString();
    }

    private MdeDBList parseMongoList(SpliceStringReader reader) {
        SpliceStringReader.Matcher matcher;
        MdeDBList result = new MdeDBList();
        while (!((matcher = reader.splice(mongoSymbols)).finish())) {
            switch (matcher.match()) {
                case "," : {
                    String value = matcher.prefix().trim();
                    if (value.length() > 0) {
                        result.add(parseMongoContent(value));
                    }
                    break;
                }
                case "{" :
                    result.add(parseMongoMap(reader));
                    break;
                case "[" :
                    result.add(parseMongoList(reader));
                    break;
                case "}" : {
                    String value = matcher.prefix().trim();
                    if (value.length() > 0) {
                        result.add(parseMongoContent(value));
                    }
                    return result;
                }
            }
        }
        return result;
    }

    private MdeDBObject parseMongoMap(SpliceStringReader reader) {
        SpliceStringReader.Matcher matcher;
        MdeDBObject result = new MdeDBObject();
        String currentKey = null;
        while (!((matcher = reader.splice(mongoSymbols)).finish())) {
            switch (matcher.match()) {
                case ":" :
                    currentKey = matcher.prefix().trim();
                    break;
                case "," : {
                    String value = matcher.prefix().trim();
                    if (currentKey != null && value.length() > 0) {
                        result.append(currentKey, parseMongoContent(value));
                        currentKey = null;
                    }
                    break;
                }
                case "{" :
                    if(currentKey != null){
                        result.append(currentKey, parseMongoMap(reader));
                        currentKey = null;
                    }
                    break;
                case "[" :
                    if(currentKey != null){
                        result.append(currentKey, parseMongoList(reader));
                        currentKey = null;
                    }
                    break;
                case "}" : {
                    String value = matcher.prefix().trim();
                    if (currentKey != null && value.length() > 0) {
                        result.append(currentKey, parseMongoContent(value));
                    }
                    return result;
                }
            }
        }
        return result;
    }

    private MdeDBObject parseActionInfo(SpliceStringReader reader) {
        SpliceStringReader.Matcher matcher;
        MdeDBObject result = new MdeDBObject();
        String currentKey = null;
        while(!((matcher = reader.splice(actionInfoSymbols)).finish())){
            switch(matcher.match()){
                case "=" :
                    currentKey = matcher.prefix().trim();
                    break;
                case " " :
                case "\b" :
                case "\f" :
                case "\t" :
                case "\r" :
                case "\n" :
                case "," : {
                    String value = matcher.prefix().trim();
                    if (currentKey != null && value.length() > 0) {
                        result.append(currentKey, value);
                        currentKey = null;
                    }
                    break;
                }
                case "[" :
                    if(currentKey != null){
                        result.append(currentKey, parseMongoList(reader));
                        currentKey = null;
                    }
                    break;
                case "{" :
                    if(currentKey != null){
                        result.append(currentKey, parseMongoMap(reader));
                        currentKey = null;
                    }
                    break;
                case ")" :
                case ">" :
                case "]" : {
                    String value = matcher.prefix().trim();
                    if (currentKey != null && value.length() > 0) {
                        result.append(currentKey, value);
                    }
                    return result;
                }
            }
        }
        return result;
    }

    private Action parseAction(SpliceStringReader reader) {
        SpliceStringReader.Matcher matcher = reader.splice(actionScopeSymbols);
        if(matcher.finish()) {
            return null;
        }

        String actionName = matcher.prefix().trim();
        Action.Scope scope = null;
        switch(matcher.match()){
            case "(" :
                scope = Action.Scope.PARENT;
                break;
            case "<" :
                scope = Action.Scope.SELF;
                break;
            case "[" :
                scope = Action.Scope.CHILD;
                break;
        }

        MdeDBObject infos = parseActionInfo(reader);

        try{
            return actionMap.get(actionName).getDeclaredConstructor(Action.Scope.class, MdeDBObject.class).newInstance(scope, infos);
        }catch(Exception ex){
            throw new IllegalArgumentException(actionName+" is not support action.");
        }
    }

    private Map<String, Dsl> cache = new ConcurrentHashMap();

}
