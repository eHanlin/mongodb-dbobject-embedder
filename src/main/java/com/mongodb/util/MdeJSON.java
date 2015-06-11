package com.mongodb.util;

import com.mongodb.util.JSON;
import com.mongodb.util.JSONSerializers;
import com.mongodb.util.ObjectSerializer;
import tw.com.ehanlin.mde.dsl.mongo.At;

public class MdeJSON extends JSON {

    public static String serialize(Object object) {
        StringBuilder buf = new StringBuilder();
        serialize(object, buf);
        return buf.toString();
    }

    public static void serialize(Object object, StringBuilder buf) {
        ClassMapBasedObjectSerializer serializer = (ClassMapBasedObjectSerializer) JSONSerializers.getLegacy();
        serializer.addObjectSerializer(At.class, new AtSerializer());
        serializer.serialize(object, buf);
    }

    private static class AtSerializer extends AbstractObjectSerializer {

        @Override
        public void serialize(Object o, StringBuilder stringBuilder) {
            stringBuilder.append(((At)o).toString());
        }
    }
}
