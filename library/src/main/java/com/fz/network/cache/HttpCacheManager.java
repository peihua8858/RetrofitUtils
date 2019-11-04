package com.fz.network.cache;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.socks.library.KLog;

import org.json.JSONObject;

import java.lang.reflect.Method;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Retrofit2 OkHttp 缓存管理
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2018/7/6 21:05
 */
public final class HttpCacheManager {
    /**
     * http 请求媒体类型
     */
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private IHttpCache iHttpCache;
    private long cacheLifeTime;
    static class CacheManagerHelper {
        final static HttpCacheManager CACHE_MANAGER = new HttpCacheManager();
    }

    private HttpCacheManager() {
    }

    public void setHttpCache(IHttpCache iHttpCache) {
        this.iHttpCache = iHttpCache;
    }

    public void setCacheLifeTime(long cacheLifeTime) {
        this.cacheLifeTime = cacheLifeTime;
    }

    /**
     * 获取缓存管理对象
     *
     * @author dingpeihua
     * @date 2018/7/6 21:06
     * @version 1.0
     */
    public static HttpCacheManager instance() {
        return CacheManagerHelper.CACHE_MANAGER;
    }

    /**
     * 保存http缓存数据
     *
     * @param call     http请求对象
     * @param response http响应对象
     * @return 如果缓存成功返回true，否则返回false
     * @author dingpeihua
     * @date 2018/7/6 21:07
     * @version 1.0
     */
    public synchronized <T> boolean put(Call<T> call, Response response) {
        if (getHttpCache().save(call, response)) {
            return true;
        }
        Object body = checkResponse(response);
        try {
            if (body != null) {
                Gson gson = new Gson();
                String content = gson.toJson(body);
                KLog.d("LockCacheManage写入>>>content:" + content);
                if (!TextUtils.isEmpty(content)) {
                    JSONObject jsonObject = new JSONObject(content);
                    jsonObject.put("isCacheData", "true");
                    Object value = null;
                    if (jsonObject.has("result")) {
                        value = jsonObject.opt("result");
                    } else if (jsonObject.has("data")) {
                        value = jsonObject.opt("data");
                    }
                    if (value instanceof JSONObject) {
                        JSONObject result = (JSONObject) value;
                        result.put("isCacheData", "true");
                    }
                    KLog.d("LockCacheManage写入>>>content:" + content);
                    content = jsonObject.toString();
                    return getHttpCache().saveCache(call, content);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * 根据请求对象获取缓存数据
     *
     * @param call http请求对象
     * @return 如果获取成功返回响应实体对象，否则返回null
     * @author dingpeihua
     * @date 2018/7/6 21:08
     * @version 1.0
     */
    public <T> Response<T> get(Call<T> call) {
        if (call == null) {
            return null;
        }
        Response caCheResponse;
        if ((caCheResponse = getHttpCache().get(call)) != null) {
            return caCheResponse;
        }
        try {
            Request request = call.clone().request();
            KLog.d("LockCacheManage读取>>>没有数据！");
            String content = getHttpCache().readCache(request);
            final RequestBody requestBody = request.body();
            MediaType contentType = requestBody != null ? requestBody.contentType() : MEDIA_TYPE;
            KLog.d("LockCacheManage读取>>>content:" + content);
            if (!TextUtils.isEmpty(content)) {
                okhttp3.Response response = new okhttp3.Response.Builder()
                        .code(200)
                        .message("Success")
                        .body(ResponseBody.create(contentType == null ? MEDIA_TYPE : contentType, content))
                        .protocol(Protocol.HTTP_1_1)
                        .headers(request.headers())
                        .request(request)
                        .build();
                Class<?> clazz = Class.forName("retrofit2.OkHttpCall");
                Method method = clazz.getDeclaredMethod("parseResponse", okhttp3.Response.class);
                method.setAccessible(true);
                return (Response<T>) method.invoke(call, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    IHttpCache getHttpCache() {
        return iHttpCache != null ? iHttpCache : (iHttpCache = new HttpCacheImpl(cacheLifeTime));
    }

}
