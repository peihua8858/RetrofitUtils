package com.fz.network.remote;


import com.fz.network.VpHttpClient;

import java.util.HashMap;

/**
 * 基础的网络请求接口
 */
public class BasicDataManager {

    protected VpHttpClient vpNewtWork;
    protected HashMap<String, Object> interIml = new HashMap<>();

    protected BasicDataManager() {
    }

    /**
     * 重写可以自己配置更多
     * init data DataManager
     *
     * @param baseUrl
     */
    public void init(String baseUrl) {
        vpNewtWork = new VpHttpClient.Builder().addBaseUrl(baseUrl).build();
    }

    /**
     * 获取接口清单
     *
     * @param tClass
     * @param <T>
     * @return
     */
    public <T> T getInterIml(Class<T> tClass) {
        String key = tClass.getSimpleName();
        if (interIml.get(key) == null) {
            interIml.put(key, vpNewtWork.createRetrofit(tClass));
        }

        return (T) interIml.get(key);
    }
}
