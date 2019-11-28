package com.fz.network.remote;


import com.fz.network.HttpClient;
import com.fz.network.RetrofitClient;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 基础的网络请求接口
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/22 16:35
 */
public class BasicDataManager {

    private RetrofitClient mClient;

    protected BasicDataManager(OkHttpClient httpClient, String baseUrl) {
        mClient = RetrofitClient.newBuilder()
                .setHttpClient(httpClient)
                .setBaseUrl(baseUrl)
                .build();
    }

    protected BasicDataManager(OkHttpClient httpClient) {
        this(httpClient, "");
    }

    protected BasicDataManager(HttpClient httpClient) {
        this(httpClient, "");
    }

    protected BasicDataManager(HttpClient httpClient, String baseUrl) {
        this(httpClient.build(), baseUrl);
    }

    public void setBaseUrl(String baseUrl) {
        mClient.setBaseUrl(baseUrl);
    }

    public void newRetrofit(OkHttpClient httpClient, String baseUrl) {
        mClient = RetrofitClient.newBuilder()
                .setHttpClient(httpClient)
                .setBaseUrl(baseUrl)
                .build();
    }

    public void newRetrofit(HttpClient httpClient, String baseUrl) {
        mClient = RetrofitClient.newBuilder()
                .setHttpClient(httpClient)
                .setBaseUrl(baseUrl)
                .build();
    }

    /**
     * 返回当前OkHttp cookieJar
     *
     * @return
     * @author dingpeihua
     * @date 2019/11/22 17:18
     * @version 1.0
     */
    public CookieJar getCookieJar() {
        return mClient != null ? mClient.getCookieJar() : null;
    }

    /**
     * 删除Api对象
     *
     * @param clazz api类
     * @author dingpeihua
     * @date 2019/11/22 17:16
     * @version 1.0
     */
    public void removeService(Class<?> clazz) {
        mClient.removeService(clazz);
    }

    /**
     * 删除retrofit对象
     *
     * @param url retrofit对应的url
     */
    public void removeRetrofit(String url) {
        mClient.removeRetrofit(url);
    }

    public void removeAllRetrofit() {
        mClient.removeAllRetrofit();
    }

    public void removeAllService() {
        mClient.removeAllService();
    }

    public <T> T createApi(Class<T> clazz) {
        return mClient.createRetrofit(clazz);
    }

    public <T> T createApi(String host, Class<T> clazz, Type type, Object typeAdapter) {
        return mClient.createRetrofit(host, clazz, type, typeAdapter);
    }

    public <T> T createApi(String host, Class<T> clazz) {
        return mClient.createRetrofit(host, clazz);
    }

    public <T> T createRetrofit(String host, OkHttpClient okHttpClient, Class<T> clazz, MediaType mediaType, Map<Type, Object> typeAdapters) {
        return mClient.createRetrofit(host, okHttpClient, clazz, mediaType, typeAdapters);
    }

    public <T> T createRetrofit(String host, Class<T> clazz, MediaType mediaType, final Type type, final Object typeAdapter) {
        return mClient.createRetrofit(host, clazz, mediaType, type, typeAdapter);
    }

    public <T> RetrofitClient.ServiceBuilder<T> createService(Class<T> val) {
        return createService(mClient, val);
    }

    public <T> RetrofitClient.ServiceBuilder<T> createService(RetrofitClient client, Class<T> val) {
        return RetrofitClient.createService(client, val);
    }
}
