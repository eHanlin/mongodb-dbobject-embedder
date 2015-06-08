package com.ehanlin.mde;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

abstract public class MongoEmbedder {

    static public class CommonMongoEmbedder extends MongoEmbedder {
        public CommonMongoEmbedder(Boolean isDuplicateCache) {
            super(isDuplicateCache);
        }
        @Override
        protected Boolean isParallel() {
            return false;
        }
        @Override
        protected Map createTmpMap() {
            return new HashMap();
        }
    }

    static public class ParallelMongoEmbedder extends MongoEmbedder {
        public ParallelMongoEmbedder(Boolean isDuplicateCache) {
            super(isDuplicateCache);
        }
        @Override
        protected Boolean isParallel() {
            return true;
        }
        @Override
        protected Map createTmpMap() {
            return new ConcurrentHashMap();
        }
    }

    static public final MongoEmbedder instance = new CommonMongoEmbedder(false);
    static public final MongoEmbedder duplicate = new CommonMongoEmbedder(true);
    static public final MongoEmbedder parallel = new ParallelMongoEmbedder(false);
    static public final MongoEmbedder duplicateParallel = new ParallelMongoEmbedder(true);

    static public DB registerDB(String id, DB db) {
        instance.register(id, db);
        duplicate.register(id, db);
        parallel.register(id, db);
        duplicateParallel.register(id, db);
        return db;
    }

    static public DB registerDB(DB db) {
        instance.register(db);
        duplicate.register(db);
        parallel.register(db);
        duplicateParallel.register(db);
        return db;
    }

    public MongoEmbedder(Boolean isDuplicateCache){
        this.isDuplicateCache = isDuplicateCache;
    }

    abstract protected Map createTmpMap();
    abstract protected Boolean isParallel();

    protected Boolean isDuplicateCache;

    public DB register(String id, DB db) {
        dbMap.put(id, db);
        return db;
    }

    public DB register(DB db) {
        return register(defaultMongoDatabaseId, db);
    }


    public DBObject embed(String dbId, String collectionName, Object resource , Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        try{
            if(collectionName == null){
                return embedWithColl(dbId, null, resource, embedDesc, includeDesc, cache);
            }
            return embedWithColl(dbId, dbMap.get(dbId).getCollection(collectionName), resource, embedDesc, includeDesc, cache);
        }catch(Throwable ex){
            return null;
        }
    }

    public DBObject embed(String dbId, String collectionName, Object resource , Map embedDesc, Map includeDesc) {
        return embed(dbId, collectionName, resource, embedDesc, includeDesc, new ConcurrentHashMap());
    }

    public DBObject embed(String dbId, String collectionName, Object resource , String embedDesc) {
        return embed(dbId, collectionName, resource, (Map)JSON.parse(embedDesc), emptyDBObject);
    }

    public DBObject embed(String dbId, String collectionName, Object resource , String embedDesc, String includeDesc) {
        return embed(dbId, collectionName, resource, (Map)JSON.parse(embedDesc), (Map)JSON.parse(includeDesc));
    }


    public DBObject embed(Object resource , Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        return embed(defaultMongoDatabaseId, null, resource, embedDesc, includeDesc, cache);
    }

    public DBObject embed(Object resource , Map embedDesc, Map includeDesc) {
        return embed(defaultMongoDatabaseId, null, resource, embedDesc, includeDesc, new ConcurrentHashMap());
    }

    public DBObject embed(Object resource , String embedDesc) {
        return embed(defaultMongoDatabaseId, null, resource, (Map)JSON.parse(embedDesc), emptyDBObject);
    }

    public DBObject embed(Object resource , String embedDesc, String includeDesc) {
        return embed(defaultMongoDatabaseId, null, resource, (Map)JSON.parse(embedDesc), (Map)JSON.parse(includeDesc));
    }




    static private final String fieldDbCode = "_db";
    static private final String fieldCollectionCode = "_coll";
    static private final String fieldFindCode = "_find";
    static private final String fieldFindOneCode = "_findOne";
    static private final String fieldDistinctCode = "_distinct";
    static private final String fieldCountCode = "_count";
    static private final String fieldAggregatCode = "_aggregate";
    static private final List<String> reservedFields = Arrays.asList(fieldFindCode, fieldFindOneCode, fieldDistinctCode, fieldCountCode, fieldAggregatCode);
    static private final Pattern fieldQuerySelfCodePattern = Pattern.compile("^@(\\[.+\\])+$");
    static private final Pattern fieldQueryKeyCodePattern = Pattern.compile("\\[(.+?)\\]");
    static private final String fieldQueryParentKeyCode = "..";
    static private final String magicDelimiterCode = "__";

    static private final String defaultMongoDatabaseId = "_default";
    static private final BasicDBObject emptyDBObject = new BasicDBObject();

    private final Map<String, DB> dbMap = new ConcurrentHashMap();

    private BasicDBObject findOneWithCache(String dbId, DBCollection coll, BasicDBObject query, BasicDBObject fieldKeys, Map<String, Object> cache) {
        try{
            String key = fieldFindOneCode+magicDelimiterCode+dbId+magicDelimiterCode+coll.getFullName()+magicDelimiterCode+query.toString()+magicDelimiterCode+fieldKeys.toString();
            if(!cache.containsKey(key)){
                BasicDBObject result = (BasicDBObject)coll.findOne(query, fieldKeys);
                cache.put(key, result);
            }
            if(isDuplicateCache)
                return (BasicDBObject)((BasicDBObject)cache.get(key)).copy();
            else
                return (BasicDBObject)cache.get(key);
        }catch(Exception ex){
            return null;
        }
    }

    private BasicDBList findWithCache(String dbId, DBCollection coll, BasicDBObject query, BasicDBObject fieldKeys, Map<String, Object> cache) {
        try{
            String key = fieldFindCode+magicDelimiterCode+dbId+magicDelimiterCode+coll.getFullName()+magicDelimiterCode+query.toString()+magicDelimiterCode+fieldKeys.toString();
            if(!cache.containsKey(key)){
                BasicDBList list = new BasicDBList();
                list.addAll(coll.find(query, fieldKeys).toArray());
                cache.put(key, list);
            }
            if(isDuplicateCache)
                return (BasicDBList)((BasicDBList)cache.get(key)).copy();
            else
                return (BasicDBList)cache.get(key);
        }catch(Exception ex){
            return null;
        }
    }

    private Long countWithCache(String dbId, DBCollection coll, BasicDBObject query, Map<String, Object> cache) {
        try{
            String key = fieldCountCode+magicDelimiterCode+dbId+magicDelimiterCode+coll.getFullName()+magicDelimiterCode+query.toString();
            if(!cache.containsKey(key)){
                Long count = coll.count(query);
                cache.put(key, count);
            }
            return (Long)cache.get(key);
        }catch(Exception ex){
            return null;
        }
    }

    private BasicDBObject buildDesc(Object desc) {
        if(desc instanceof Map){
            BasicDBObject result = new BasicDBObject();
            ((Map)desc).keySet().forEach(key -> result.append((String)key, true));
            return result;
        }
        return emptyDBObject;
    }

    private Map cutDesc(Map desc, Object key) {
        if(desc.get(key) instanceof Map){
            return (Map)desc.get(key);
        }
        return emptyDBObject;
    }

    private DBCollection getEmbedColl(String dbId, Object collectionName,  Map embed) {
        String itemDbId = (embed.containsKey(fieldDbCode)) ? embed.get(fieldDbCode).toString() : dbId;
        String itemCollectionName = (embed.containsKey(fieldCollectionCode)) ? embed.get(fieldCollectionCode).toString() : collectionName.toString();
        return dbMap.get(itemDbId).getCollection(itemCollectionName);
    }

    private Object getQueryValue(Object resource, Matcher keyMatcher) {
        if(keyMatcher.find()){
            String key = keyMatcher.group(1);
            if(resource instanceof Map){
                return getQueryValue(((Map)resource).get(key), keyMatcher);
            }
            if(resource instanceof List){
                return getQueryValue(((List) resource).get(Integer.parseInt(key)), keyMatcher);
            }
        }
        return resource;
    }

    private Object evalQueryValue(Map resource, Object value) {
        if(value instanceof String) {
            Matcher matcher = fieldQuerySelfCodePattern.matcher((String)value);
            if(matcher.matches()){
                return getQueryValue(resource, fieldQueryKeyCodePattern.matcher(matcher.group(1)));
            }
            return value;
        }
        if(value instanceof Map) {
            BasicDBObject result = new BasicDBObject((Map)value);
            result.forEach((k, v) -> result.append(k, evalQueryValue(resource, v)));
            return result;
        }
        if(value instanceof Iterable) {
            BasicDBList result = new BasicDBList();
            ((Iterable) value).forEach(v -> result.add(evalQueryValue(resource, v)));
            return result;
        }
        return value;
    }

    private DBObject embedWithColl(String dbId, DBCollection coll, Object resource, Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        try {
            if (resource == null) {
                return null;
            }
            if (resource instanceof Map) {
                return embedMapType(dbId, (Map) resource, embedDesc, includeDesc, cache);
            }
            if (resource instanceof Iterable) {
                return embedIterableType(dbId, coll, (Iterable) resource, embedDesc, includeDesc, cache);
            }
            return embedObjectType(dbId, coll, resource, embedDesc, includeDesc, cache);
        } catch (Throwable ex){
            return null;
        }
    }

    private DBObject embedObjectType(String dbId, DBCollection coll, Object resource, Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        DBObject item = findOneWithCache(dbId, coll, new BasicDBObject("_id", resource), buildDesc(includeDesc), cache);
        return embedWithColl(dbId, coll, item, embedDesc, includeDesc, cache);
    }

    private String findReservedField(Map embed) {
        if(embed.isEmpty()) {
            return null;
        }
        for(String reservedField : reservedFields) {
            if(embed.containsKey(reservedField)) {
                return reservedField;
            }
        }
        return null;
    }

    private DBObject _find_task(String dbId, DBCollection coll, Object resource, Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        BasicDBObject embedQuery = (BasicDBObject)evalQueryValue((Map)resource, embedDesc.get(fieldFindCode));
        BasicDBList list = findWithCache(dbId, coll, embedQuery, buildDesc(includeDesc), cache);
        return embedWithColl(dbId, coll, list, embedDesc, includeDesc, cache);
    }

    private DBObject _findOne_task(String dbId, DBCollection coll, Object resource, Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        BasicDBObject embedQuery = (BasicDBObject)evalQueryValue((Map)resource, embedDesc.get(fieldFindOneCode));
        BasicDBObject item = findOneWithCache(dbId, coll, embedQuery, buildDesc(includeDesc), cache);
        return embedWithColl(dbId, coll, item, embedDesc, includeDesc, cache);
    }

    private Long _count_task(String dbId, DBCollection coll, Object resource, Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        BasicDBObject embedQuery = (BasicDBObject)evalQueryValue((Map)resource, embedDesc.get(fieldCountCode));
        return countWithCache(dbId, coll, embedQuery, cache);
    }

    private DBObject embedMapType(String dbId, Map resource, Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        Map tmp = createTmpMap();
        StreamSupport.stream(embedDesc.keySet().spliterator(), isParallel())
            .filter(key -> {
                if (resource.containsKey(key)) {
                    return true;
                }
                return findReservedField(cutDesc(embedDesc, key)) != null;
            })
            .forEach(key -> {
                try {
                    Map itemEmbedDesc = cutDesc(embedDesc, key);
                    Map itemIncludeDesc = cutDesc(includeDesc, key);
                    DBCollection itemEmbedColl = getEmbedColl(dbId, key, itemEmbedDesc);
                    String eservedField = findReservedField(itemEmbedDesc);
                    if(eservedField != null) {
                        Method task = MongoEmbedder.class.getDeclaredMethod(eservedField+"_task", String.class, DBCollection.class, Object.class, Map.class, Map.class, Map.class);
                        tmp.put(key, task.invoke(this, dbId, itemEmbedColl, resource, itemEmbedDesc, itemIncludeDesc, cache));
                    } else {
                        tmp.put(key, embedWithColl(dbId, itemEmbedColl, resource.get(key), itemEmbedDesc, itemIncludeDesc, cache));
                    }
                } catch (Throwable ex) {
                }
            });
        BasicDBObject result = new BasicDBObject(resource);
        result.putAll(tmp);
        return result;
    }

    private DBObject embedIterableType(String dbId, DBCollection coll, Iterable resource, Map embedDesc, Map includeDesc, Map<String, Object> cache) {
        BasicDBList list = new BasicDBList();
        for(Object item : resource){
            list.add(embedWithColl(dbId, coll, item, embedDesc, includeDesc, cache));
        }
        return list;
    }


}