package com.fz.network.cache

import com.fz.common.text.isNonEmpty
import com.fz.common.utils.eLog
import com.fz.gson.GsonFactory
import com.fz.network.params.VpRequestParams
import com.fz.okhttp.params.OkRequestParams.Companion.asRequestParams
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.socks.library.KLog
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.*
import java.net.URLEncoder

/**
 * 缓存操作
 * @author dingpeihua
 * @date 2021/4/6 14:29
 * @version 1.0
 */
class KotlinCacheEx {
    @Throws(IOException::class)
    private fun buildCacheKey(httpUrl: String, requestContent: String): String {
        //创建缓存key
        val sb = StringBuilder(URLEncoder.encode(httpUrl, "UTF-8"))
        sb.append(requestContent)
        return sb.toString()
    }

    /**
     * 保存网络数据到缓存，默认空实现
     * 注意：请与[.readCache] 方法配合使用
     *
     * @param call     缓存键名
     * @param response 缓存内容
     * @return true保存成功, 否则保存失败
     * @author dingpeihua
     * @date 2019/10/26 9:26
     * @version 1.0
     */
    fun <T> saveCache(url: String, request: VpRequestParams?, response: T?, lifeTime: Long): Boolean {
        if (request != null && response != null) {
            try {
                val gson = Gson()
                val requestStr = request.asRequestParams()
                val responseStr = gson.toJson(response)
                if (responseStr.isNonEmpty() && requestStr.isNonEmpty()) {
                    val cacheKey = buildCacheKey(url, requestStr)
                    CacheManager.getInstance().putCache(cacheKey, responseStr, lifeTime)
                    return true
                }
            } catch (e: Exception) {
                eLog { e.stackTraceToString() }
            }
        }
        return false
    }

    /**
     * 读取缓存数据，默认空实现
     * 注意：请与[.saveCache] 方法配合使用
     *
     * @param request 请求体
     * @return 读取成功返回不会null，否则返回null
     * @author dingpeihua
     * @date 2019/10/26 9:26
     * @version 1.0
     */
    fun <T> readCache(url: String, request: VpRequestParams?, clazz: Class<T>): T? {
        if (CacheManager.isInitCache() && request != null) {
            try {
                val requestStr = request.asRequestParams()
                val cacheKey = buildCacheKey(url, requestStr)
                val content = CacheManager.getInstance().getCache(cacheKey)
                KLog.d("LockCacheManage读取>>>cacheKey:$cacheKey")
                if (content.isNonEmpty()) {
                    return content.toResponseBody().use {
                        ByteArrayInputStream(it.bytes()).use { bis ->
                            BufferedReader(InputStreamReader(bis)).use { reader ->
                                val gson = GsonFactory.create()
                                val jsonReader = gson!!.newJsonReader(reader)
                                val adapter: TypeAdapter<T> = gson.getAdapter(TypeToken.get(clazz))
                                adapter.read(jsonReader)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                eLog { e.stackTraceToString() }
            }
        }
        return null
    }
}