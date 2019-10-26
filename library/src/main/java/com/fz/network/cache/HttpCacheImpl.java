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
import retrofit2.Response;

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
    private static final String KEY_OPEN_CACHE = "isOpenCache";

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
        boolean isOpenCache = CacheUtil.toBoolean(CacheUtil.getUriParameter(uri, "isOpenCache"));
        if (isOpenCache) {
            return saveCache(response, tUrl, "");
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
        String json = buffer.readString(Charset.forName("UTF-8"));
        JSONObject jsonObject = new JSONObject(json);
        boolean isSaveCache = isOpenCache(jsonObject);
        JSONObject dataJson = jsonObject.optJSONObject("data");
        if (dataJson != null) {
            dataJson.remove("timestamp");
        }
        jsonObject.remove("timestamp");
        jsonObject.remove("isOpenCache");
        json = jsonObject.toString();
        if (isSaveCache) {
            KLog.d("LockCacheManage写入>>>tUrl:" + tUrl + ",json:" + json);
            return saveCache(response, tUrl, json);
        }
        return false;
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
    private boolean saveCache(String response, String tUrl, String json) throws IOException {
        String cacheKey = buildCacheKey(tUrl, json);
        KLog.d("LockCacheManage写入>>>cacheKey:" + cacheKey);
        KLog.d("LockCacheManage写入>>>content:" + response);
        if (!TextUtils.isEmpty(response)) {
            CacheManager.getInstance().putCache(cacheKey, response);
            return true;
        }
        return false;
    }

    public Object checkResponse(Response response) {
        Object body = response.body();
        if (body instanceof ICacheResponse) {
            if (((ICacheResponse) body).checkResult()) {
                return body;
            }
        }
        return null;
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
        boolean isOpenCache = CacheUtil.toBoolean(CacheUtil.getUriParameter(uri, "isOpenCache"));
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
        String json = buffer.readString(Charset.forName("UTF-8"));
        JSONObject jsonObject = new JSONObject(json);
        boolean isSaveCache = isOpenCache(jsonObject);
        JSONObject dataJson = jsonObject.optJSONObject("data");
        if (dataJson != null) {
            dataJson.remove("timestamp");
        }
        jsonObject.remove("timestamp");
        jsonObject.remove("isOpenCache");
        json = jsonObject.toString();
        if (isSaveCache) {
            KLog.d("LockCacheManage读取>>>tUrl:" + tUrl);
            KLog.d("LockCacheManage读取>>>json:" + json);
            return readCache(tUrl, json);
        }
        return null;
    }

    private String readCache(String tUrl, String json) throws IOException {
        String cacheKey = buildCacheKey(tUrl, json);
        String content = CacheManager.getInstance().getCache(cacheKey);
        KLog.d("LockCacheManage读取>>>cacheKey:" + cacheKey);
        return content;
    }

    boolean isOpenCache(JSONObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        if (jsonObject.has(KEY_OPEN_CACHE)) {
            return jsonObject.optBoolean(KEY_OPEN_CACHE);
        }
        Iterator<String> sIterator = jsonObject.keys();
        while (sIterator.hasNext()) {
            final String key = sIterator.next();
            final Object value = jsonObject.opt(key);
            final boolean isOpenCache;
            if (value instanceof JSONObject) {
                isOpenCache = isOpenCache((JSONObject) value);
            } else {
                isOpenCache = false;
            }
            if (isOpenCache) {
                return true;
            }
        }
        return false;
    }
}
