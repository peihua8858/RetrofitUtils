package com.fz.network.cache;

import android.net.Uri;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.socks.library.KLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okio.Buffer;
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
    final static String POST = "POST";
    final static String GET = "GET";
    /**
     * http 请求媒体类型
     */
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    static class CacheManagerHelper {
        final static HttpCacheManager CACHE_MANAGER = new HttpCacheManager();
    }

    private HttpCacheManager() {
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
    private boolean saveGetMethodCache(Request request, Response response) throws IOException, JSONException {
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
    private boolean savePostMethodCache(Request request, Response response) throws IOException, JSONException {
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
        boolean isSaveCache = jsonObject.optBoolean("isOpenCache");
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
     * 写入缓存
     *
     * @param response
     * @param tUrl
     * @param json
     * @author dingpeihua
     * @date 2019/3/28 10:50
     * @version 1.0
     */
    private boolean saveCache(Response response, String tUrl, String json) throws IOException, JSONException {
        String cacheKey = buildCacheKey(tUrl, json);
        Object body = checkResponse(response);
        if (body != null) {
            Gson gson = new Gson();
            String content = gson.toJson(body);
            KLog.d("LockCacheManage写入>>>cacheKey:" + cacheKey);
            KLog.d("LockCacheManage写入>>>content:" + content);
            if (!TextUtils.isEmpty(content)) {
                JSONObject jsonObject1 = new JSONObject(content);
                Object object = jsonObject1.opt("result");
                if (object instanceof JSONObject) {
                    JSONObject result = (JSONObject) object;
                    result.put("isCacheData", "true");
                }
                jsonObject1.put("isCacheData", "true");
                KLog.d("LockCacheManage写入>>>content:" + content);
                content = jsonObject1.toString();
                CacheManager.getInstance().putCache(cacheKey, content);
                return true;
            }
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
        try {
            Request request = call.clone().request();
            String method = request.method();
            if (GET.equalsIgnoreCase(method)) {
                return readGetMethodCache(request, call);
            } else if (POST.equalsIgnoreCase(method)) {
                return readPostMethodCache(request, call);
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

    private <T> Response<T> readGetMethodCache(Request request, Call<T> call) throws IOException {
        final String tUrl = request.url().toString();
        Uri uri = Uri.parse(tUrl);
        boolean isOpenCache = CacheUtil.toBoolean(CacheUtil.getUriParameter(uri, "isOpenCache"));
        if (isOpenCache) {
            return readCache(request, call, tUrl, "", null);
        }
        return null;
    }

    <T> Response<T> readPostMethodCache(Request request, Call<T> call) throws IOException, JSONException {
        final RequestBody requestBody = request.body();
        if (requestBody instanceof MultipartBody || requestBody == null) {
            return null;
        }
        final String tUrl = request.url().toString();
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        buffer.close();
        String json = buffer.readString(Charset.forName("UTF-8"));
        MediaType contentType = requestBody.contentType();
        JSONObject jsonObject = new JSONObject(json);
        boolean isSaveCache = jsonObject.optBoolean("isOpenCache");
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
            return readCache(request, call, tUrl, json, contentType);
        }
        return null;
    }

    private <T> Response<T> readCache(Request request, Call<T> call, String tUrl, String json, MediaType contentType) throws IOException {
        String cacheKey = buildCacheKey(tUrl, json);
        String content = CacheManager.getInstance().getCache(cacheKey);
        KLog.d("LockCacheManage读取>>>cacheKey:" + cacheKey);
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
            try {
                Class<?> clazz = Class.forName("retrofit2.OkHttpCall");
                Method method = clazz.getDeclaredMethod("parseResponse", okhttp3.Response.class);
                method.setAccessible(true);
                return (Response<T>) method.invoke(call, response);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        KLog.d("LockCacheManage读取>>>没有数据！");
        return null;
    }
}
