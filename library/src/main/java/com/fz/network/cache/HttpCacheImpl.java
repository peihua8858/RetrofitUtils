package com.fz.network.cache;

import android.net.Uri;
import android.text.TextUtils;

import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Iterator;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;

/**
 * 缓存内部实现
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/10/26 9:30
 */
class HttpCacheImpl implements IHttpCache {
    final static String POST = "POST";
    final static String GET = "GET";
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private static final String KEY_DATA = "data";
    private static final String KEY_OPEN_CACHE = "isOpenCache";
    private static final String KEY_TIMESTAMP = "timestamp";
    private static final String KEY_SAVE_CACHE = "isSaveCache";
    private static final String KEY_LIFE_TIME = "lifeTime";
    private long cacheLifeTime = -1;

    public HttpCacheImpl(long cacheLifeTime) {
        this.cacheLifeTime = cacheLifeTime;
    }

    @Override
    public <T> boolean saveCache(Call<T> call, String response) {
        if (call != null && response != null) {
            try {
                Request request = call.clone().request();
                String method = request.method();
                if (GET.equalsIgnoreCase(method)) {
                    return saveGetMethodCache(request, response);
                } else if (POST.equalsIgnoreCase(method)) {
                    return savePostMethodCache(request, response);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 缓存get方法响应数据
     *
     * @param request
     * @param response
     * @author dingpeihua
     * @date 2019/3/28 10:45
     * @version 1.0
     */
    private boolean saveGetMethodCache(Request request, String response) throws IOException, JSONException {
        final String tUrl = request.url().toString();
        Uri uri = Uri.parse(tUrl);
        boolean isOpenCache = CacheUtil.toBoolean(CacheUtil.getUriParameter(uri, KEY_OPEN_CACHE));
        long lifeTime = CacheUtil.toLong(CacheUtil.getUriParameter(uri, KEY_LIFE_TIME), getLifeTime());
        if (isOpenCache) {
            return saveCache(response, tUrl, "", lifeTime);
        }
        return false;
    }

    /**
     * 缓存post方法响应数据
     *
     * @param request
     * @param response
     * @author dingpeihua
     * @date 2019/3/28 10:44
     * @version 1.0
     */
    private boolean savePostMethodCache(Request request, String response) throws IOException, JSONException {
        final RequestBody requestBody = request.body();
        if (requestBody instanceof MultipartBody || requestBody == null) {
            return false;
        }
        final String tUrl = request.url().toString();
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        buffer.close();
        String json = buffer.readString(UTF8);
        JSONObject jsonObject = new JSONObject(json);
        boolean isSaveCache = isOpenCache(jsonObject) || isSaveCache(jsonObject);
        long lifeTime = findLifeTime(jsonObject);
        JSONObject dataJson = jsonObject.optJSONObject(KEY_DATA);
        if (dataJson != null) {
            remove(dataJson);
        }
        remove(jsonObject);
        json = jsonObject.toString();
        if (isSaveCache) {
            KLog.d("LockCacheManage写入>>>tUrl:" + tUrl + ",json:" + json);
            return saveCache(response, tUrl, json, lifeTime);
        }
        return false;
    }

    private void remove(JSONObject object) {
        if (object != null) {
            object.remove(KEY_OPEN_CACHE);
            object.remove(KEY_SAVE_CACHE);
            object.remove(KEY_TIMESTAMP);
            object.remove(KEY_LIFE_TIME);
        }
    }

    /**
     * 写入缓存
     *
     * @param response
     * @param tUrl
     * @param json
     * @author dingpeihua
     * @date 2019/3/28 10:50
     * @version 1.0
     */
    private boolean saveCache(String response, String tUrl, String json, long lifeTime) throws IOException {
        String cacheKey = buildCacheKey(tUrl, json);
        KLog.d("LockCacheManage写入>>>cacheKey:" + cacheKey);
        KLog.d("LockCacheManage写入>>>content:" + response);
        if (!TextUtils.isEmpty(response) && CacheManager.isInitCache()) {
            CacheManager.getInstance().putCache(cacheKey, response, lifeTime);
            return true;
        }
        return false;
    }

    private String buildCacheKey(String httpUrl, String requestContent) throws IOException {
        //创建缓存key
        final StringBuilder sb = new StringBuilder(URLEncoder.encode(httpUrl, "UTF-8"));
        sb.append(requestContent);
        final String cacheKey = sb.toString();
        return cacheKey;
    }

    @Override
    public String readCache(Request request) {
        if (request == null) {
            return null;
        }
        try {
            String method = request.method();
            if (GET.equalsIgnoreCase(method)) {
                return readGetMethodCache(request);
            } else if (POST.equalsIgnoreCase(method)) {
                return readPostMethodCache(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        KLog.d("LockCacheManage读取>>>没有数据！");
        return null;
    }

    private String readGetMethodCache(Request request) throws IOException {
        final String tUrl = request.url().toString();
        Uri uri = Uri.parse(tUrl);
        boolean isOpenCache = CacheUtil.toBoolean(CacheUtil.getUriParameter(uri, KEY_OPEN_CACHE));
        if (isOpenCache) {
            return readCache(tUrl, "");
        }
        return null;
    }

    String readPostMethodCache(Request request) throws IOException, JSONException {
        final RequestBody requestBody = request.body();
        if (requestBody instanceof MultipartBody || requestBody == null) {
            return null;
        }
        final String tUrl = request.url().toString();
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        buffer.close();
        String json = buffer.readString(UTF8);
        JSONObject jsonObject = new JSONObject(json);
        boolean isSaveCache = isOpenCache(jsonObject);
        JSONObject dataJson = jsonObject.optJSONObject(KEY_DATA);
        if (dataJson != null) {
            remove(dataJson);
        }
        remove(jsonObject);
        json = jsonObject.toString();
        if (isSaveCache) {
            KLog.d("LockCacheManage读取>>>tUrl:" + tUrl);
            KLog.d("LockCacheManage读取>>>json:" + json);
            return readCache(tUrl, json);
        }
        return null;
    }

    private String readCache(String tUrl, String json) throws IOException {
        if (CacheManager.isInitCache()) {
            String cacheKey = buildCacheKey(tUrl, json);
            String content = CacheManager.getInstance().getCache(cacheKey);
            KLog.d("LockCacheManage读取>>>cacheKey:" + cacheKey);
            return content;
        }
        return null;
    }

    long findLifeTime(JSONObject jsonObject) {
        Object value = findValue(jsonObject, KEY_LIFE_TIME);
        return CacheUtil.toLong(value, getLifeTime());
    }

    boolean isOpenCache(JSONObject jsonObject) {
        Object value = findValue(jsonObject, KEY_OPEN_CACHE);
        return CacheUtil.toBoolean(value);
    }

    boolean isSaveCache(JSONObject jsonObject) {
        Object value = findValue(jsonObject, KEY_SAVE_CACHE);
        return CacheUtil.toBoolean(value);
    }

    Object findValue(JSONObject jsonObject, String findKey) {
        if (jsonObject == null) {
            return null;
        }
        if (jsonObject.has(findKey)) {
            return removeKey(jsonObject, findKey);
        }
        Iterator<String> sIterator = jsonObject.keys();
        Object value;
        while (sIterator.hasNext()) {
            final String key = sIterator.next();
            value = jsonObject.opt(key);
            if (value instanceof JSONObject) {
                value = findValue((JSONObject) value, findKey);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    Object removeKey(Object value, String key) {
        if (value instanceof JSONObject) {
            return ((JSONObject) value).remove(key);
        }
        return null;
    }

    @Override
    public long getLifeTime() {
        return cacheLifeTime;
    }
}
