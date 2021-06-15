package com.fz.network.params

import com.fz.okhttp.params.OkRequestParams

/**
 * 处理缓存参数
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/7/1 16:53
 */
open class VpRequestParams : OkRequestParams {
    constructor()
    constructor(source: Map<String, Any?>?) : super(source)
    constructor(key: String?, value: Any?) : super(key, value)
    constructor(vararg keysAndValues: Any?) : super(*keysAndValues)

    /**
     * 创建一个是否支持读写缓存的构造函数；
     *
     * @param isReadCache 是否可读写缓存
     * @author dingpeihua
     * @date 2019/9/2 15:33
     * @version 1.0
     */
    constructor(isReadCache: Boolean) : this("isOpenCache", isReadCache)

    /**
     * 数据写入缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    fun saveCache(): VpRequestParams {
        put("isSaveCache", true)
        return this
    }

    /**
     * 开启读写缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    fun openCache(): VpRequestParams {
        put("isOpenCache", true)
        return this
    }

    /**
     * 设置缓存有效时间
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    fun setLifeTime(lifeTime: Long): VpRequestParams {
        put("lifeTime", lifeTime)
        return this
    }

    /**
     * 设置是否开启缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 16:35
     * @version 1.0
     */
    fun setOpenCache(isReadCache: Boolean) {
        put("isOpenCache", if (isReadCache) "true" else "false")
    }

    /**
     * 设置是否将数据写入缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 16:35
     * @version 1.0
     */
    fun setSaveCache(isSaveCache: Boolean) {
        put("isSaveCache", if (isSaveCache) "true" else "false")
    }
}