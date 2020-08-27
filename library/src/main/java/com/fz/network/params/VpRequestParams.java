package com.fz.network.params;

import com.fz.okhttp.params.OkRequestParams;

import java.util.Map;

/**
 * 处理缓存参数
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2020/7/1 16:53
 */
public class VpRequestParams extends OkRequestParams {
    public VpRequestParams() {
    }

    public VpRequestParams(Map<String, Object> source) {
        super(source);
    }

    public VpRequestParams(String key, Object value) {
        super(key, value);
    }

    public VpRequestParams(Object... keysAndValues) {
        super(keysAndValues);
    }

    /**
     * 创建一个是否支持读写缓存的构造函数；
     *
     * @param isReadCache 是否可读写缓存
     * @author dingpeihua
     * @date 2019/9/2 15:33
     * @version 1.0
     */
    public VpRequestParams(boolean isReadCache) {
        this("isOpenCache", isReadCache);
    }
    /**
     * 数据写入缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    public final VpRequestParams saveCache() {
        put("isSaveCache", true);
        return this;
    }
    /**
     * 开启读写缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    public final VpRequestParams openCache() {
        put("isOpenCache", true);
        return this;
    }

    /**
     * 设置缓存有效时间
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    public final VpRequestParams setLifeTime(long lifeTime) {
        put("lifeTime", lifeTime);
        return this;
    }

    /**
     * 设置是否开启缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 16:35
     * @version 1.0
     */
    public final void setOpenCache(boolean isReadCache) {
        put("isOpenCache", isReadCache ? "true" : "false");
    }
    /**
     * 设置是否将数据写入缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 16:35
     * @version 1.0
     */
    public final void setSaveCache(boolean isSaveCache) {
        put("isSaveCache", isSaveCache ? "true" : "false");
    }
}
