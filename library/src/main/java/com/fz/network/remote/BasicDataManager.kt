package com.fz.network.remote

import com.fz.network.RetrofitClient
import com.fz.network.RetrofitClient.ServiceBuilder
import com.fz.okhttp.OkHttpWrapper
import okhttp3.CookieJar
import okhttp3.MediaType
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import java.lang.reflect.Type

/**
 * 基础的网络请求接口
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/22 16:35
 */
open class BasicDataManager protected constructor(client: RetrofitClient) {
    private var mClient: RetrofitClient = client

    protected constructor(
        httpClient: OkHttpClient, baseUrl: String = "",
        adapterFactory: CallAdapter.Factory? = null,
        factory: Converter.Factory? = null
    ) : this(
        RetrofitClient.newBuilder()
            .setHttpClient(httpClient)
            .adapter(adapterFactory)
            .converter(factory)
            .setBaseUrl(baseUrl)
            .build()
    ) {
    }

    protected constructor(
        httpClient: OkHttpWrapper,
        baseUrl: String = "",
        adapterFactory: CallAdapter.Factory? = null,
        factory: Converter.Factory? = null
    ) : this(httpClient.build(), baseUrl, adapterFactory, factory)

    fun setBaseUrl(baseUrl: String) {
        mClient.setBaseUrl(baseUrl)
    }

    fun newRetrofit(
        httpClient: OkHttpClient, baseUrl: String,
        adapterFactory: CallAdapter.Factory? = null,
        factory: Converter.Factory? = null
    ) {
        setRetrofit(
            RetrofitClient.newBuilder()
                .setHttpClient(httpClient)
                .setBaseUrl(baseUrl)
                .adapter(adapterFactory)
                .converter(factory)
                .build()
        )
    }

    fun newRetrofit(
        httpClient: OkHttpWrapper, baseUrl: String,
        adapterFactory: CallAdapter.Factory? = null,
        factory: Converter.Factory? = null
    ) {
        setRetrofit(
            RetrofitClient.newBuilder()
                .setHttpClient(httpClient)
                .setBaseUrl(baseUrl)
                .adapter(adapterFactory)
                .converter(factory)
                .build()
        )
    }

    fun setRetrofit(client: RetrofitClient) {
        mClient = client
    }

    /**
     * 返回当前OkHttp cookieJar
     *
     * @return
     * @author dingpeihua
     * @date 2019/11/22 17:18
     * @version 1.0
     */
    val cookieJar: CookieJar
        get() = mClient.cookieJar

    /**
     * 删除Api对象
     *
     * @param clazz api类
     * @author dingpeihua
     * @date 2019/11/22 17:16
     * @version 1.0
     */
    fun removeService(clazz: Class<*>) {
        mClient.removeService(clazz)
    }

    /**
     * 删除retrofit对象
     *
     * @param url retrofit对应的url
     */
    fun removeRetrofit(url: String?) {
        mClient.removeRetrofit(url)
    }

    fun removeAllRetrofit() {
        mClient.removeAllRetrofit()
    }

    fun removeAllService() {
        mClient.removeAllService()
    }

    open fun <T> createApi(clazz: Class<T>): T {
        return mClient.createRetrofit(clazz)
    }

    open fun <T> createApi(host: String, clazz: Class<T>, type: Type?, typeAdapter: Any?): T {
        return mClient.createRetrofit(host, clazz, type, typeAdapter)
    }

    open fun <T> createApi(host: String, clazz: Class<T>): T {
        return mClient.createRetrofit(host, clazz)
    }

    open fun <T> createRetrofit(
        host: String,
        okHttpClient: OkHttpClient,
        clazz: Class<T>,
        mediaType: MediaType?,
        typeAdapters: Map<Type, Any>?
    ): T {
        return mClient.createRetrofit(host, okHttpClient, clazz, mediaType, typeAdapters)
    }

    open fun <T> createRetrofit(
        host: String,
        clazz: Class<T>,
        mediaType: MediaType?,
        type: Type?,
        typeAdapter: Any?
    ): T {
        return mClient.createRetrofit(host, clazz, mediaType, type, typeAdapter)
    }

    open fun <T> createService(clazz: Class<T>): ServiceBuilder<T> {
        return createService(mClient, clazz)
    }

    open fun <T> createService(client: RetrofitClient, clazz: Class<T>): ServiceBuilder<T> {
        return RetrofitClient.createService(client, clazz)
    }

    init {
        setRetrofit(client)
    }
}