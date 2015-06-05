package com.ehanlin.mde;

import com.mongodb.*;
import com.mongodb.util.JSON;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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


    public DBObject embed(String dbId, String collectionName, Object resource , Map embedDesc, Map includeDesc, Map<String, BasicDBObject> findOneCache, Map<String, BasicDBList> findCache) {
        try{
            if(collectionName == null){
                return embedWithColl(dbId, null, resource, embedDesc, includeDesc, findOneCache, findCache);
            }
            return embedWithColl(dbId, dbMap.get(dbId).getCollection(collectionName), resource, embedDesc, includeDesc, findOneCache, findCache);
        }catch(Throwable ex){
            return null;
        }
    }


    public DBObject embed(String collectionName, Object resource , Map embedDesc, Map includeDesc, Map<String, BasicDBObject> findOneCache, Map<String, BasicDBList> findCache) {
        return embed(defaultMongoDatabaseId, collectionName, resource, embedDesc, includeDesc, findOneCache, findCache);
    }

    public DBObject embed(String collectionName, Object resource , Map embedDesc, Map includeDesc) {
        return embed(defaultMongoDatabaseId, collectionName, resource, embedDesc, includeDesc, new ConcurrentHashMap(), new ConcurrentHashMap());
    }

    public DBObject embed(String collectionName, Object resource , String embedDesc) {
        return embed(collectionName, resource, (Map)JSON.parse(embedDesc), emptyDBObject);
    }

    public DBObject embed(String collectionName, Object resource , String embedDesc, String includeDesc) {
        return embed(collectionName, resource, (Map)JSON.parse(embedDesc), (Map)JSON.parse(includeDesc));
    }


    public DBObject embed(Object resource , Map embedDesc, Map includeDesc, Map<String, BasicDBObject> findOneCache, Map<String, BasicDBList> findCache) {
        return embed(defaultMongoDatabaseId, null, resource, embedDesc, includeDesc, findOneCache, findCache);
    }

    public DBObject embed(Object resource , Map embedDesc, Map includeDesc) {
        return embed(defaultMongoDatabaseId, null, resource, embedDesc, includeDesc, new ConcurrentHashMap(), new ConcurrentHashMap());
    }

    public DBObject embed(Object resource , String embedDesc) {
        return embed(null, resource, (Map)JSON.parse(embedDesc), emptyDBObject);
    }

    public DBObject embed(Object resource , String embedDesc, String includeDesc) {
        return embed(null, resource, (Map)JSON.parse(embedDesc), (Map)JSON.parse(includeDesc));
    }




    static private final String fieldDbCode = "_db";
    static private final String fieldCollectionCode = "_coll";
    static private final String fieldQueryCode = "_query";
    static private final String magicDelimiterCode = "__";

    static private final String defaultMongoDatabaseId = "_default";
    static private final BasicDBObject emptyDBObject = new BasicDBObject();

    private final Map<String, DB> dbMap = new ConcurrentHashMap();

    private BasicDBObject findOneWithCache(String dbId, DBCollection coll, BasicDBObject query, BasicDBObject fieldKeys, Map<String, BasicDBObject> cache) {
        try{
            String key = dbId+magicDelimiterCode+coll.getFullName()+magicDelimiterCode+query.toString()+magicDelimiterCode+fieldKeys.toString();
            if(!cache.containsKey(key)){
                BasicDBObject result = (BasicDBObject)coll.findOne(query, fieldKeys);
                cache.put(key, result);
            }
            if(isDuplicateCache)
                return (BasicDBObject)cache.get(key).copy();
            else
                return cache.get(key);
        }catch(Exception ex){
            return null;
        }
    }

    private BasicDBList findWithCache(String dbId, DBCollection coll, BasicDBObject query, BasicDBObject fieldKeys, Map<String, BasicDBList> cache) {
        try{
            String key = dbId+magicDelimiterCode+coll.getFullName()+magicDelimiterCode+query.toString()+magicDelimiterCode+fieldKeys.toString();
            if(!cache.containsKey(key)){
                BasicDBList list = new BasicDBList();
                list.addAll(coll.find(query, fieldKeys).toArray());
                cache.put(key, list);
            }
            if(isDuplicateCache)
                return (BasicDBList)cache.get(key).copy();
            else
                return cache.get(key);
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

    private DBObject embedWithColl(String dbId, DBCollection coll, Object resource, Map embedDesc, Map includeDesc, Map<String, BasicDBObject> findOneCache, Map<String, BasicDBList> findCache) {
        try {
            if (resource == null) {
                return null;
            } else if (resource instanceof Map) {
                return embedMapType(dbId, (Map) resource, embedDesc, includeDesc, findOneCache, findCache);
            } else if (resource instanceof Iterable) {
                return embedIterableType(dbId, coll, (Iterable) resource, embedDesc, includeDesc, findOneCache, findCache);
            } else {
                return embedObjectType(dbId, coll, resource, embedDesc, includeDesc, findOneCache, findCache);
            }
        } catch (Throwable ex){
            return null;
        }
    }

    private DBObject embedObjectType(String dbId, DBCollection coll, Object resource, Map embedDesc, Map includeDesc, Map<String, BasicDBObject> findOneCache, Map<String, BasicDBList> findCache) {
        DBObject item = findOneWithCache(dbId, coll, new BasicDBObject("_id", resource), buildDesc(includeDesc), findOneCache);
        return embedWithColl(dbId, coll, item, embedDesc, includeDesc, findOneCache, findCache);
    }

    private DBObject embedMapType(String dbId, Map resource, Map embedDesc, Map includeDesc, Map<String, BasicDBObject> findOneCache, Map<String, BasicDBList> findCache) {
        Map tmp = createTmpMap();
        StreamSupport.stream(embedDesc.keySet().spliterator(), isParallel())
            .filter(key -> resource.containsKey(key))
            .forEach(key -> {
                try {
                    tmp.put(key, embedWithColl(dbId, dbMap.get(dbId).getCollection(key.toString()), resource.get(key), cutDesc(embedDesc, key), cutDesc(includeDesc, key), findOneCache, findCache));
                } catch (Throwable ex) { }
            });
        BasicDBObject result = new BasicDBObject(resource);
        result.putAll(tmp);
        return result;
    }

    private DBObject embedIterableType(String dbId, DBCollection coll, Iterable resource, Map embedDesc, Map includeDesc, Map<String, BasicDBObject> findOneCache, Map<String, BasicDBList> findCache) {
        BasicDBList list = new BasicDBList();
        for(Object item : resource){
            list.add(embedWithColl(dbId, coll, item, embedDesc, includeDesc, findOneCache, findCache));
        }
        return list;
    }


}