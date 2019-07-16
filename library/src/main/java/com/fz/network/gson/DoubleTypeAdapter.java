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
public class DoubleTypeAdapter implements JsonSerializer<Double>, JsonDeserializer<Double> {


    @Override
    public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }

    @Override
    public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json == null){
            return 0.0;
        } else {
            try {//直接解析
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {
                    //定义为int类型,如果后台返回""或者null,则返回0
                    return 0.0;
                }
                return  Double.parseDouble(json.getAsString());
            } catch (Exception e) {
                return 0.0;
            }
        }
    }
}
