package com.fz.network.cache;

/**
 * app缓存处理，判断是否是缓存数据{@link #isCacheData}，
 * 如果为true表示当前返回为缓存数据，否则是网络接口数据
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/1/24 13:42
 */
public abstract class ICacheResponse {
    /**
     * 当前数据是否为缓存数据，true是缓存，否则不是缓存数据
     */
    protected boolean isCacheData;

    /**
     * 判断是否是缓存数据
     *
     * @return 如果为true表示当前返回为缓存数据，否则是网络接口数据
     * @author dingpeihua
     * @date 2019/1/24 13:46
     * @version 1.0
     */
    public final boolean isCacheData() {
        return isCacheData;
    }

    /**
     * 检查http返回数据是否为空；
     * 如返回{"statusCode":200,"msg":"","result":{...}}应检查result是否为空
     *
     * @author dingpeihua
     * @date 2019/9/2 11:38
     * @version 1.0
     */
    protected abstract boolean checkResult();
}
