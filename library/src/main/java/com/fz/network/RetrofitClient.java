package com.fz.network;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.fz.gson.GsonFactory;
import com.fz.network.remote.GsonConverterFactory;
import com.fz.okhttp.OkHttpWrapper;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;

/**
 * https retrofit 构建，解决多域名需要创建多个实例的问题
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/22 16:31
 */
public class RetrofitClient {
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient mOkHttpClient;
    /**
     * key: service Class
     * value:service instance object
     */
    private final HashMap<Class<?>, Object> services;
    /**
     * key: host  url
     * value retrofit instance object
     */
    private final HashMap<String, Retrofit> retrofits;
    private Converter.Factory mFactory;
    private CallAdapter.Factory mAdapterFactory;
    /**
     * 服务域名地址
     */
    private String mBaseUrl;

    private RetrofitClient(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
        services = new HashMap<>();
        retrofits = new HashMap<>();
    }

    private RetrofitClient(Builder builder) {
        this(builder.httpClient);
        this.mBaseUrl = builder.baseUrl;
        this.mFactory = builder.factory;
        this.mAdapterFactory = builder.adapterFactory;
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
        return mOkHttpClient != null ? mOkHttpClient.cookieJar() : null;
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
        return createRetrofit(host, mOkHttpClient, clazz, mediaType, new HashMap<Type, Object>() {{
            if (type != null && typeAdapter != null) {
                put(type, typeAdapter);
            }
        }});
    }

    private <T> T createRetrofit(String host, OkHttpClient okHttpClient, Class<T> clazz, MediaType mediaType,
                                 Map<Type, Object> typeAdapters,
                                 Converter.Factory factory,
                                 CallAdapter.Factory adapterFactory) {
        if (services.containsKey(clazz)) {
            return clazz.cast(services.get(clazz));
        }
        Retrofit retrofit;
        if (retrofits.containsKey(host)) {
            retrofit = retrofits.get(host);
        } else {
            retrofit = createRetrofit(host, okHttpClient, mediaType, typeAdapters, factory, adapterFactory);
            retrofits.put(host, retrofit);
        }
        T service = retrofit.create(clazz);
        services.put(clazz, service);
        return service;
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
    public <T> T createRetrofit(String host, OkHttpClient okHttpClient, Class<T> clazz, MediaType mediaType,
                                Map<Type, Object> typeAdapters) {
        if (services.containsKey(clazz)) {
            return clazz.cast(services.get(clazz));
        }
        Retrofit retrofit;
        if (retrofits.containsKey(host)) {
            retrofit = retrofits.get(host);
        } else {
            retrofit = createRetrofit(host, okHttpClient, mediaType, typeAdapters, null, null);
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
    private Retrofit createRetrofit(String host, MediaType mediaType, final Type type, final Object typeAdapter,
                                    Converter.Factory factory,
                                    CallAdapter.Factory adapterFactory) {
        return createRetrofit(host, mOkHttpClient, mediaType, new HashMap<Type, Object>() {{
            if (type != null && typeAdapter != null) {
                put(type, typeAdapter);
            }
        }}, factory, adapterFactory);
    }

    /**
     * 创建一个Retrofit
     *
     * @param host
     * @author dingpeihua
     * @date 2019/1/11 15:26
     * @version 1.0
     */
    private Retrofit createRetrofit(String host, OkHttpClient okHttpClient, MediaType mediaType, Map<Type, Object> typeAdapters,
                                    Converter.Factory factory,
                                    CallAdapter.Factory adapterFactory) {
        if (TextUtils.isEmpty(host)) {
            host = mBaseUrl;
        }
        if (factory == null) {
            factory = this.mFactory != null ? this.mFactory : GsonConverterFactory.create(GsonFactory.createBuild(typeAdapters), mediaType);
        }
        if (adapterFactory == null) {
            adapterFactory = this.mAdapterFactory != null ? mAdapterFactory : RxJava3CallAdapterFactory.create();
        }
        return createRetrofit(host, okHttpClient, factory, adapterFactory);
    }

    /**
     * 创建一个Retrofit
     *
     * @param host
     * @author dingpeihua
     * @date 2019/1/11 15:26
     * @version 1.0
     */
    private Retrofit createRetrofit(String host, OkHttpClient okHttpClient,
                                    @NonNull Converter.Factory factory,
                                    @NonNull CallAdapter.Factory adapterFactory) {
        if (TextUtils.isEmpty(host)) {
            host = mBaseUrl;
        }
        return new Retrofit.Builder()
                .baseUrl(host)
                .client(okHttpClient == null ? this.mOkHttpClient : okHttpClient)
                .addConverterFactory(factory)
                .addCallAdapterFactory(adapterFactory)
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
        private Converter.Factory converterFactory;
        private CallAdapter.Factory adapterFactory;
        private HashMap<Type, Object> typeAdapters = new HashMap<>();

        public ServiceBuilder(RetrofitClient vpHttpClient, Class<T> val) {
            this.httpClient = vpHttpClient;
            service = val;
        }

        public ServiceBuilder<T> setHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public ServiceBuilder<T> converter(Converter.Factory factory) {
            converterFactory = factory;
            return this;
        }

        public ServiceBuilder<T> adapter(CallAdapter.Factory factory) {
            adapterFactory = factory;
            return this;
        }

        public ServiceBuilder<T> setHttpClient(OkHttpWrapper val) {
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
            return service.cast(httpClient.createRetrofit(host, okHttpClient, service, mediaType, typeAdapters,
                    converterFactory, adapterFactory));
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private OkHttpClient httpClient;
        private String baseUrl;
        private Converter.Factory factory;
        private CallAdapter.Factory adapterFactory;

        public Builder() {
        }

        public Builder converter(Converter.Factory factory) {
            this.factory = factory;
            return this;
        }

        public Builder adapter(CallAdapter.Factory factory) {
            this.adapterFactory = factory;
            return this;
        }

        public Builder setHttpClient(OkHttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        public Builder setHttpClient(OkHttpWrapper httpClient) {
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
