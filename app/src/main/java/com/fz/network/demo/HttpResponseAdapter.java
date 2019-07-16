package com.fz.network.demo;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.fz.network.gson.GsonBuilderFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.socks.library.KLog;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * 适配数据响应解析
 * {"statusCode":200,"result":[],"msg":""}
 * 将不符合此格式的进行转换，
 * 并处理Expected BEGIN_OBJECT but was BEGIN_ARRAY at path $.result.goods_list形式的异常
 *
 * @author dingpeihua
 */
public class HttpResponseAdapter implements JsonDeserializer<HttpResponse> {
    private static final String KEY_RESULT = "result";
    private static final String KEY_RETURN_CODE = "returnCode";
    private static final String KEY_RETURN_INFO = "returnInfo";
    private static final String KEY_MESSAGE = "msg";
    private static final String KEY_STATUS_CODE = "statusCode";
    private static final String KEY_COMMUNITY_CODE = "code";
    private static final String KEY_DATA = "data";
    @NonNull
    final Gson gson;

    public HttpResponseAdapter(Gson gson) {
        this.gson = gson;
    }

    public HttpResponseAdapter() {
        this.gson = GsonBuilderFactory.createDefaultBuild().create();
    }

    @Override
    public HttpResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject obj = json.getAsJsonObject();
            if (obj.has(KEY_RESULT)) {
                JsonElement result = obj.get(KEY_RESULT);
                //如果接口返回类型和接收类型不一致时，不处理这个结果,比如：接口返回JSONArray，
                // 而客户端接收类型是非List<?>
                Class<?> clazz = ReflectUtil.getClass(typeOfT, 0);
                if (result.isJsonArray() && !(List.class.isAssignableFrom(clazz))) {
                    obj.remove(KEY_RESULT);
                }
                if (result.isJsonObject()) {
                    JsonObject resultObj = result.getAsJsonObject();
                    if (resultObj.has(KEY_RETURN_CODE)) {
                        //处理商品详情及登陆、注册接口错误时，returninfo返回提示信息的问题
                        JsonElement returnCodeObject = resultObj.get(KEY_RETURN_CODE);
                        String code = returnCodeObject.getAsString();
                        if (!"0".equals(code)) {
                            //code 不为0则，表示业务层出错
                            JsonElement returnInfo = resultObj.get(KEY_RETURN_INFO);
                            resultObj.remove(KEY_RETURN_INFO);
                            resultObj.add(KEY_MESSAGE, returnInfo);
                        }
                    }
                }
            }
            if (obj.has(KEY_COMMUNITY_CODE)) {
                JsonElement codeObject = obj.get(KEY_COMMUNITY_CODE);
                String code = codeObject.getAsString();
                if ("10006".equals(code)) {
                    //10006是社区接口无数据，需要转换成200
                    obj.addProperty(KEY_STATUS_CODE, 200);
                    obj.addProperty(KEY_COMMUNITY_CODE, 0);
                }
            }
            //替换data为result
            if (obj.has(KEY_DATA)) {
                JsonElement data = obj.get(KEY_DATA);
                obj.remove(KEY_DATA);
                obj.add(KEY_RESULT, data);
            }

            //这里要自己负责解析了
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            try {
                return parseException(json, typeOfT, e);
            } catch (Exception e1) {
                KLog.e(e1.getMessage());
                e1.printStackTrace();
                throw new JsonParseException(e1);
            }
        }
    }

    HttpResponse parseException(JsonElement json, Type typeOfT, Exception e) throws Exception {
        try {
            String message = e.getMessage();
            if (!TextUtils.isEmpty(message)) {
                int index = message.indexOf("$");
                KLog.d("LockHttpResponse>>>index:" + index);
                String resultJson = message.substring(index + 2);
                KLog.d("LockHttpResponse>>>resultJson:" + resultJson);
                String[] result = resultJson.split("\\.");
                KLog.d("LockHttpResponse>>>result:" + Arrays.toString(result));
                int length = result != null ? result.length : 0;
                if (length > 0) {
                    if (json.isJsonObject()) {
                        parseJsonObjectException(json.getAsJsonObject(), result, length);
                    } else if (json.isJsonArray()) {
                        parseJsonArrayException(json.getAsJsonArray(), result, length);
                    }
                    return gson.fromJson(json, typeOfT);
                }
            }
        } catch (Exception e1) {
            throw e1;
        }
        throw e;
    }

    void parseJsonArrayException(JsonArray tempJson, String[] result, int resultLength) {
        for (JsonElement jsonElement : tempJson) {
            if (jsonElement.isJsonObject()) {
                parseJsonObjectException(jsonElement.getAsJsonObject(), result, resultLength);
            } else if (jsonElement.isJsonArray()) {
                parseJsonArrayException(tempJson.getAsJsonArray(), result, resultLength);
            }
        }
    }

    void parseJsonObjectException(JsonObject tempJson, String[] result, int resultLength) {
        int i = 0;
        JsonElement jsonElement;
        while (i < resultLength) {
            jsonElement = tempJson.get(result[i]);
            if (jsonElement != null) {
                if (jsonElement.isJsonObject()) {
                    jsonElement = jsonElement.getAsJsonObject();
                } else if (jsonElement.isJsonArray()) {
                    parseJsonArrayException(jsonElement.getAsJsonArray(), result, resultLength);
                }
                //因为此次处理值针对有异常数据处理，如果遍历到倒数第二层时
                //例如：java.lang.IllegalStateException: Expected BEGIN_OBJECT but was BEGIN_ARRAY at path $.result.goods_list
                //倒数第二层为result时，移除有异常的层次goods_list数据
                if (i >= resultLength - 1) {
                    if (tempJson.isJsonObject()) {
                        tempJson.remove(result[i]);
                    }
                    break;
                }
                if (jsonElement.isJsonObject()) {
                    tempJson = jsonElement.getAsJsonObject();
                }
            }
            ++i;
        }
    }
}
