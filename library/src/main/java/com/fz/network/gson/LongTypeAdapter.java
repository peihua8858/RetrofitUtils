package com.fz.network.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 *
 */
public class LongTypeAdapter implements JsonSerializer<Long>, JsonDeserializer<Long> {


    @Override
    public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }

    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json == null){
            return 0L;
        } else {
            try {//直接解析
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {
                    //定义为int类型,如果后台返回""或者null,则返回0
                    return 0L;
                }
                Double dd = Double.parseDouble(json.getAsString());
                return dd.longValue();
            } catch (Exception e) {
                return 0L;
            }
        }
    }
}
