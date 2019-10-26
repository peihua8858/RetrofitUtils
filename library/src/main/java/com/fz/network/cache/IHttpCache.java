package com.fz.network.cache;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;

/**
 * http 缓存
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/10/26 9:25
 */
public interface IHttpCache {
    /**
     * 由子类自定义缓存读取方法，默认空实现
     * 注意：请与{@link #save(Call, Response)}方法配合使用
     *
     * @param call
     * @return 读取成功返回不会null，否则返回null
     * @author dingpeihua
     * @date 2019/10/26 18:42
     * @version 1.0
     */
    default <T> Response<T> get(Call<T> call) {
        return null;
    }

    /**
     * 由子类自定义缓存保存方法，默认空实现
     * 注意：请与{@link #get(Call)} 方法配合使用
     *
     * @param call
     * @param response
     * @return true保存成功, 否则保存失败
     * @author dingpeihua
     * @date 2019/10/26 18:43
     * @version 1.0
     */
    default <T> boolean save(Call<T> call, Response response) {
        return false;
    }

    /**
     * 保存网络数据到缓存，默认空实现
     * 注意：请与{@link #readCache(Request)} 方法配合使用
     *
     * @param call     缓存键名
     * @param response 缓存内容
     * @return true保存成功, 否则保存失败
     * @author dingpeihua
     * @date 2019/10/26 9:26
     * @version 1.0
     */
    default <T> boolean saveCache(Call<T> call, String response) {
        return false;
    }

    /**
     * 读取缓存数据，默认空实现
     * 注意：请与{@link #saveCache(Call, String)} 方法配合使用
     *
     * @param request 请求体
     * @return 读取成功返回不会null，否则返回null
     * @author dingpeihua
     * @date 2019/10/26 9:26
     * @version 1.0
     */
    default String readCache(Request request) {
        return null;
    }
}
