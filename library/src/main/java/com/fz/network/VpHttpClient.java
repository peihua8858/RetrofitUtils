package com.fz.network;


import android.text.TextUtils;

import com.fz.network.gson.GsonBuilderFactory;
import com.fz.network.remote.GsonConverterFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CookieJar;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * https retrofit 构建，解决多域名需要创建多个实例的问题
 */
public class VpHttpClient {
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_TYPE = MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8");
    public static final String BASE_URL = "https://httpbin.org/";
    private OkHttpClient okHttpClient;
    private Retrofit mRetrofit;
    private String mBaseUrl;
    private List<Interceptor> interceptors;
    private HashMap<Class<?>, Object> services;
    private HashMap<String, Retrofit> retrofits;

    private VpHttpClient() {
        services = new HashMap<>();
        retrofits = new HashMap<>();
    }

    public void removeRetrofit(String url) {
        retrofits.remove(url);
    }

    public CookieJar getCookieJar() {
        return okHttpClient != null ? okHttpClient.cookieJar() : null;
    }

    public void removeService(Class<?> clazz) {
        services.remove(clazz);
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
            return (T) services.get(clazz);
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
    Retrofit createRetrofit(String host, OkHttpClient okHttpClient, MediaType mediaType, Map<Type, Object> typeAdapters) {
        if (TextUtils.isEmpty(host)) {
            host = BASE_URL;
        }
        return new Retrofit.Builder()
                .baseUrl(host)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilderFactory.createBuild(typeAdapters), mediaType))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private Retrofit getRetrofit() {
        return createRetrofit(mBaseUrl, JSON_TYPE, null, null);
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
        return createRetrofit(host, clazz, JSON_TYPE, type, typeAdapter);
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T createRetrofit(String host, Class<T> clazz) {
        return createRetrofit(host, clazz, JSON_TYPE);
    }

    /**
     * okhttp clicent
     *
     * @return
     */
    public synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (interceptors != null) {
                for (Interceptor interceptor : interceptors) {
                    builder.addInterceptor(interceptor);
                }
            }
            okHttpClient = builder
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    /**
     * 创建API 服务代理对象
     *
     * @param vpHttpClient
     * @author dingpeihua
     * @date 2019/6/13 11:22
     * @version 1.0
     */
    public static ServiceBuilder createService(VpHttpClient vpHttpClient) {
        return new ServiceBuilder(vpHttpClient);
    }

    /**
     * Api 服务代理创建者
     *
     * @author dingpeihua
     * @version 1.0
     * @date 2019/6/13 11:22
     */
    public static final class ServiceBuilder {
        private final VpHttpClient httpClient;
        private OkHttpClient okHttpClient;
        private String host;
        private MediaType mediaType;
        private Class<?> service;
        private HashMap<Type, Object> typeAdapters = new HashMap<>();

        public ServiceBuilder(VpHttpClient vpHttpClient) {
            this.httpClient = vpHttpClient;
        }

        public ServiceBuilder setHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public ServiceBuilder setHost(String val) {
            host = val;
            return this;
        }

        public ServiceBuilder setMediaType(MediaType val) {
            mediaType = val;
            return this;
        }

        public <T> ServiceBuilder setService(Class<T> val) {
            service = val;
            return this;
        }

        public ServiceBuilder setTypeAdapter(Type val1, Object val2) {
            typeAdapters.put(val1, val2);
            return this;
        }

        public <T> T build() {
            return (T) httpClient.createRetrofit(host, okHttpClient, service, mediaType, typeAdapters);
        }
    }

    /**
     * 构建builder
     */
    public static class Builder {
        VpHttpClient netWorkFactory;

        public Builder() {
            netWorkFactory = new VpHttpClient();
        }

        public Builder addBaseUrl(String url) {
            netWorkFactory.mBaseUrl = url;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            if (netWorkFactory.interceptors == null) {
                netWorkFactory.interceptors = new LinkedList<>();
            }
            netWorkFactory.interceptors.add(interceptor);
            return this;
        }

        public Builder addOkHttpClient(OkHttpClient okHttpClient) {
            if (okHttpClient == null) {
                throw new IllegalArgumentException("okHttpClient is null !");
            }
            netWorkFactory.okHttpClient = okHttpClient;
            return this;
        }

        public Builder addRetrofit(Retrofit retrofit) {
            netWorkFactory.mRetrofit = retrofit;
            return this;
        }


        public VpHttpClient build() {
            if (netWorkFactory.okHttpClient == null) {
                netWorkFactory.okHttpClient = netWorkFactory.getOkHttpClient();
            }
            if (netWorkFactory.mRetrofit == null) {
                Retrofit retrofit = netWorkFactory.getRetrofit();
                netWorkFactory.mRetrofit = retrofit;
            }
            return netWorkFactory;
        }
    }
}
