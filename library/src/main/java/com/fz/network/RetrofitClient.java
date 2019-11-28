package com.fz.network;


import android.text.TextUtils;

import com.fz.network.gson.GsonBuilderFactory;
import com.fz.network.remote.GsonConverterFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * https retrofit 构建，解决多域名需要创建多个实例的问题
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/22 16:31
 */
public class RetrofitClient {
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient;
    private final HashMap<Class<?>, Object> services;
    private final HashMap<String, Retrofit> retrofits;
    private String mBaseUrl;

    private RetrofitClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        services = new HashMap<>();
        retrofits = new HashMap<>();
    }

    public RetrofitClient(Builder builder) {
        this(builder.httpClient);
        this.mBaseUrl = builder.baseUrl;
    }

    public void removeRetrofit(String url) {
        retrofits.remove(url);
    }

    public void removeService(Class<?> clazz) {
        services.remove(clazz);
    }

    public void removeAllRetrofit() {
        retrofits.clear();
    }

    public void removeAllService() {
        services.clear();
    }

    public CookieJar getCookieJar() {
        return okHttpClient != null ? okHttpClient.cookieJar() : null;
    }

    public RetrofitClient setBaseUrl(String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
        return this;
    }

    /**
     * 创建服务端请求对象
     *
     * @param host
     * @param clazz
     * @author dingpeihua
     * @date 2016/12/23 09:40
     * @version 1.0
     */
    public <T> T createRetrofit(String host, Class<T> clazz, MediaType mediaType, final Type type, final Object typeAdapter) {
        return createRetrofit(host, okHttpClient, clazz, mediaType, new HashMap<Type, Object>() {{
            if (type != null && typeAdapter != null) {
                put(type, typeAdapter);
            }
        }});
    }

    /**
     * 创建服务端请求对象
     *
     * @param host
     * @param clazz
     * @author dingpeihua
     * @date 2016/12/23 09:40
     * @version 1.0
     */
    public <T> T createRetrofit(String host, OkHttpClient okHttpClient, Class<T> clazz, MediaType mediaType, Map<Type, Object> typeAdapters) {
        if (services.containsKey(clazz)) {
            return clazz.cast(services.get(clazz));
        }
        Retrofit retrofit;
        if (retrofits.containsKey(host)) {
            retrofit = retrofits.get(host);
        } else {
            retrofit = createRetrofit(host, okHttpClient, mediaType, typeAdapters);
            retrofits.put(host, retrofit);
        }
        T service = retrofit.create(clazz);
        services.put(clazz, service);
        return service;
    }

    /**
     * 创建一个Retrofit
     *
     * @param host
     * @author dingpeihua
     * @date 2019/1/11 15:26
     * @version 1.0
     */
    private Retrofit createRetrofit(String host, MediaType mediaType, final Type type, final Object typeAdapter) {
        return createRetrofit(host, okHttpClient, mediaType, new HashMap<Type, Object>() {{
            if (type != null && typeAdapter != null) {
                put(type, typeAdapter);
            }
        }});
    }

    /**
     * 创建一个Retrofit
     *
     * @param host
     * @author dingpeihua
     * @date 2019/1/11 15:26
     * @version 1.0
     */
    private Retrofit createRetrofit(String host, OkHttpClient okHttpClient, MediaType mediaType, Map<Type, Object> typeAdapters) {
        if (TextUtils.isEmpty(host)) {
            host = mBaseUrl;
        }
        return new Retrofit.Builder()
                .baseUrl(host)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilderFactory.createBuild(typeAdapters), mediaType))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createRetrofit(Class<T> clazz) {
        return createRetrofit(mBaseUrl, clazz);
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createRetrofit(String host, Class<T> clazz, MediaType mediaType) {
        return createRetrofit(host, clazz, mediaType, null, null);
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createRetrofit(String host, Class<T> clazz, Type type, Object typeAdapter) {
        return createRetrofit(host, clazz, MEDIA_TYPE, type, typeAdapter);
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createRetrofit(String host, Class<T> clazz) {
        return createRetrofit(host, clazz, MEDIA_TYPE);
    }

    /**
     * 创建API 服务代理对象
     *
     * @param client
     * @author dingpeihua
     * @date 2019/6/13 11:22
     * @version 1.0
     */
    public static <T> ServiceBuilder<T> createService(RetrofitClient client, Class<T> val) {
        return new ServiceBuilder<>(client, val);
    }

    /**
     * Api 服务代理创建者
     *
     * @author dingpeihua
     * @version 1.0
     * @date 2019/6/13 11:22
     */
    public static final class ServiceBuilder<T> {
        private final RetrofitClient httpClient;
        private OkHttpClient okHttpClient;
        private String host;
        private MediaType mediaType;
        private Class<T> service;
        private HashMap<Type, Object> typeAdapters = new HashMap<>();

        public ServiceBuilder(RetrofitClient vpHttpClient, Class<T> val) {
            this.httpClient = vpHttpClient;
            service = val;
        }

        public ServiceBuilder<T> setHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public ServiceBuilder<T> setHttpClient(HttpClient val) {
            okHttpClient = val.build();
            return this;
        }

        public ServiceBuilder<T> setHost(String val) {
            host = val;
            return this;
        }

        public ServiceBuilder<T> setMediaType(MediaType val) {
            mediaType = val;
            return this;
        }

        public ServiceBuilder<T> setService(Class<T> val) {
            service = val;
            return this;
        }

        public ServiceBuilder<T> setTypeAdapter(Type val1, Object val2) {
            typeAdapters.put(val1, val2);
            return this;
        }

        public T build() {
            return service.cast(httpClient.createRetrofit(host, okHttpClient, service, mediaType, typeAdapters));
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        OkHttpClient httpClient;
        private String baseUrl;

        public Builder() {
        }

        public Builder setHttpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder setHttpClient(HttpClient httpClient) {
            this.httpClient = httpClient.build();
            return this;
        }

        public Builder setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public RetrofitClient build() {
            return new RetrofitClient(this);
        }
    }
}
