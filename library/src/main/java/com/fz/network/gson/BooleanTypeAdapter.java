package com.fz.network.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 *  add by tanping
 */
public class BooleanTypeAdapter implements JsonSerializer<Boolean>, JsonDeserializer<Boolean> {


    @Override
    public JsonElement serialize(Boolean src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }

    @Override
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json == null){
            return false;
        } else {
            try {//直接解析
                return json.getAsString().toLowerCase().equals("true");
            } catch (Exception e) {
                return false;
            }
        }
    }
}
