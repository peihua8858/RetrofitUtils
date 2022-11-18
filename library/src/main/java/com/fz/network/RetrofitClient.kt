package com.fz.network

import com.fz.gson.GsonFactory
import com.fz.network.remote.GsonConverterFactory.Companion.create
import com.fz.okhttp.OkHttpWrapper
import okhttp3.CookieJar
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.NullPointerException
import java.lang.reflect.Type
import java.util.*

/**
 * https retrofit 构建，解决多域名需要创建多个实例的问题
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/22 16:31
 */
class RetrofitClient private constructor(
    private val mOkHttpClient: OkHttpClient,
    private var mAdapterFactory: CallAdapter.Factory? = null,
    private var mFactory: Converter.Factory? = null
) {
    /**
     * key: service Class
     * value:service instance object
     */
    private val services: HashMap<Class<*>, Any> = HashMap()

    /**
     * key: host  url
     * value retrofit instance object
     */
    private val retrofits: HashMap<String, Retrofit> = HashMap()


    /**
     * 服务域名地址
     */
    private var mBaseUrl: String = ""

    private constructor(builder: Builder) : this(builder.httpClient!!) {
        mBaseUrl = builder.baseUrl
        mFactory = builder.factory
        mAdapterFactory = builder.adapterFactory
    }

    fun removeRetrofit(url: String?) {
        retrofits.remove(url)
    }

    fun setAdapterFactory(adapterFactory: CallAdapter.Factory) {
        this.mAdapterFactory = adapterFactory
    }

    fun removeService(clazz: Class<*>) {
        services.remove(clazz)
    }

    fun removeAllRetrofit() {
        retrofits.clear()
    }

    fun removeAllService() {
        services.clear()
    }

    val cookieJar: CookieJar
        get() = mOkHttpClient.cookieJar

    fun setBaseUrl(mBaseUrl: String): RetrofitClient {
        this.mBaseUrl = mBaseUrl
        return this
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
    fun <T> createRetrofit(
        host: String,
        clazz: Class<T>,
        mediaType: MediaType?,
        type: Type?,
        typeAdapter: Any?
    ): T {
        return createRetrofit(host, mOkHttpClient, clazz, mediaType, object : HashMap<Type, Any>() {
            init {
                if (type != null && typeAdapter != null) {
                    put(type, typeAdapter)
                }
            }
        })
    }

    private fun <T> createRetrofit(
        host: String, okHttpClient: OkHttpClient?, clazz: Class<T>, mediaType: MediaType?,
        typeAdapters: Map<Type, Any>,
        factory: Converter.Factory?,
        adapterFactory: CallAdapter.Factory?,
    ): T {
        if (services.containsKey(clazz)) {
            return clazz.cast(services[clazz])
        }
        val retrofit: Retrofit = if (retrofits.containsKey(host)) {
            retrofits[host]!!
        } else {
            createRetrofit(host, okHttpClient, mediaType, typeAdapters, factory, adapterFactory)
        }
        retrofits[host] = retrofit
        val service: T = retrofit.create(clazz)
        services[clazz] = service!!
        return service
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
    fun <T> createRetrofit(
        host: String, okHttpClient: OkHttpClient?, clazz: Class<T>, mediaType: MediaType?,
        typeAdapters: Map<Type, Any>?,
    ): T {
        if (services.containsKey(clazz)) {
            return clazz.cast(services[clazz])
        }
        val retrofit: Retrofit = if (retrofits.containsKey(host)) {
            retrofits[host]!!
        } else {
            createRetrofit(host, okHttpClient, mediaType, typeAdapters, null, null)
        }
        retrofits[host] = retrofit
        val service = retrofit.create(clazz)
        services[clazz] = service!!
        return service
    }

    /**
     * 创建一个Retrofit
     *
     * @param host
     * @author dingpeihua
     * @date 2019/1/11 15:26
     * @version 1.0
     */
    private fun createRetrofit(
        host: String, mediaType: MediaType, type: Type?, typeAdapter: Any?,
        factory: Converter.Factory,
        adapterFactory: CallAdapter.Factory,
    ): Retrofit {
        return createRetrofit(host, mOkHttpClient, mediaType, object : HashMap<Type, Any>() {
            init {
                if (type != null && typeAdapter != null) {
                    put(type, typeAdapter)
                }
            }
        }, factory, adapterFactory)
    }

    /**
     * 创建一个Retrofit
     *
     * @param host
     * @author dingpeihua
     * @date 2019/1/11 15:26
     * @version 1.0
     */
    private fun createRetrofit(
        host: String?,
        okHttpClient: OkHttpClient?,
        mediaType: MediaType?,
        typeAdapters: Map<Type, Any>?,
        factory: Converter.Factory?,
        adapterFactory: CallAdapter.Factory?,
    ): Retrofit {
        val factory1 = factory ?: mFactory
        ?: create(GsonFactory.createBuild(typeAdapters), mediaType ?: MEDIA_TYPE)
        val adapterFactory1 = adapterFactory ?: mAdapterFactory
        return createRetrofit(host, okHttpClient, factory1, adapterFactory1)
    }

    /**
     * 创建一个Retrofit
     *
     * @param host
     * @author dingpeihua
     * @date 2019/1/11 15:26
     * @version 1.0
     */
    private fun createRetrofit(
        host: String?, okHttpClient: OkHttpClient?,
        factory: Converter.Factory?,
        adapterFactory: CallAdapter.Factory?,
    ): Retrofit {
        val builder = Retrofit.Builder()
            .baseUrl(host ?: mBaseUrl)
            .client(okHttpClient ?: mOkHttpClient)
        if (factory != null) {
            builder.addConverterFactory(factory)
        }
        if (adapterFactory != null) {
            builder.addCallAdapterFactory(adapterFactory)
        }
        return builder.build()
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
    </T> */
    fun <T> createRetrofit(clazz: Class<T>): T {
        return createRetrofit(mBaseUrl, clazz)
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
    </T> */
    fun <T> createRetrofit(host: String, clazz: Class<T>, mediaType: MediaType?): T {
        return createRetrofit(host, clazz, mediaType, null, null)
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
    </T> */
    fun <T> createRetrofit(host: String, clazz: Class<T>, type: Type?, typeAdapter: Any?): T {
        return createRetrofit(host, clazz, MEDIA_TYPE, type, typeAdapter)
    }

    /**
     * 创建 retorfit
     *
     * @param clazz
     * @param <T>
     * @return
    </T> */
    fun <T> createRetrofit(host: String, clazz: Class<T>): T {
        return createRetrofit(host, clazz, MEDIA_TYPE)
    }

    /**
     * Api 服务代理创建者
     *
     * @author dingpeihua
     * @version 1.0
     * @date 2019/6/13 11:22
     */
    class ServiceBuilder<T>(private val httpClient: RetrofitClient, private var service: Class<T>) {
        private var okHttpClient: OkHttpClient? = null
        private var host: String = ""
        private var mediaType: MediaType? = null
        private var converterFactory: Converter.Factory? = null
        private var adapterFactory: CallAdapter.Factory? = null
        private val typeAdapters = HashMap<Type, Any>()
        fun setHttpClient(`val`: OkHttpClient?): ServiceBuilder<T> {
            okHttpClient = `val`
            return this
        }

        fun converter(factory: Converter.Factory?): ServiceBuilder<T> {
            converterFactory = factory
            return this
        }

        fun adapter(factory: CallAdapter.Factory?): ServiceBuilder<T> {
            adapterFactory = factory
            return this
        }

        fun setHttpClient(`val`: OkHttpWrapper): ServiceBuilder<T> {
            okHttpClient = `val`.build()
            return this
        }

        fun setHost(`val`: String): ServiceBuilder<T> {
            host = `val`
            return this
        }

        fun setMediaType(`val`: MediaType?): ServiceBuilder<T> {
            mediaType = `val`
            return this
        }

        fun setService(`val`: Class<T>): ServiceBuilder<T> {
            service = `val`
            return this
        }

        fun setTypeAdapter(val1: Type, val2: Any): ServiceBuilder<T> {
            typeAdapters[val1] = val2
            return this
        }

        fun build(): T {
            return httpClient.createRetrofit(
                host, okHttpClient, service, mediaType, typeAdapters,
                converterFactory, adapterFactory
            )
        }
    }

    class Builder {
        var httpClient: OkHttpClient? = null
        var baseUrl: String = ""
        var factory: Converter.Factory? = null
        var adapterFactory: CallAdapter.Factory? = null
        fun converter(factory: Converter.Factory?): Builder {
            this.factory = factory
            return this
        }

        fun adapter(factory: CallAdapter.Factory?): Builder {
            adapterFactory = factory
            return this
        }

        fun setHttpClient(httpClient: OkHttpClient): Builder {
            this.httpClient = httpClient
            return this
        }

        fun setHttpClient(httpClient: OkHttpWrapper): Builder {
            this.httpClient = httpClient.build()
            return this
        }

        fun setBaseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun build(): RetrofitClient {
            return RetrofitClient(this)
        }
    }

    companion object {
        val MEDIA_TYPE: MediaType = "application/json; charset=utf-8".toMediaType()

        /**
         * 创建API 服务代理对象
         *
         * @param client
         * @author dingpeihua
         * @date 2019/6/13 11:22
         * @version 1.0
         */
        fun <T> createService(client: RetrofitClient, `val`: Class<T>): ServiceBuilder<T> {
            return ServiceBuilder(client, `val`)
        }

        fun newBuilder(): Builder {
            return Builder()
        }
    }

}