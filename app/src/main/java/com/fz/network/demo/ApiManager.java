package com.fz.network.demo;

import com.fz.network.VpHttpClient;
import com.fz.network.remote.BasicDataManager;

import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * @author tp
 * @date 2018/07/31
 * 管理product所有的接口列表
 */
public class ApiManager extends BasicDataManager {

    private ApiManager() {
        init(null);
    }

    static class InnerHelper {
        static ApiManager dataManager = new ApiManager();
    }

    public synchronized static ApiManager newInstance() {
        return InnerHelper.dataManager;
    }

    @Override
    public void init(String baseUrl) {
        vpNewtWork = new VpHttpClient.Builder()
                .addOkHttpClient(new OkHttpClient())
                .addBaseUrl(baseUrl)
                .build();
    }

    /**
     * 获取接口清单
     *
     * @param tClass
     * @param <T>
     * @return
     */
    @Override
    public <T> T getInterIml(Class<T> tClass) {
        return vpNewtWork.createRetrofit(tClass);
    }

    public <T> T createApi(String host, Class<T> tClass, Type type, Object typeAdapter) {
        return vpNewtWork.createRetrofit(host, tClass, type, typeAdapter);
    }

    /**
     * 商品模块、用户中心模块及社区模块由于数据不规范需要自定义处理
     *
     * @author dingpeihua
     * @date 2019/1/11 15:52
     * @version 1.0
     */
    public <T> T createApi(String host, Class<T> tClass) {
        return VpHttpClient.createService(vpNewtWork)
                .setHost(host)
                .setHttpClient(new OkHttpClient())
                .setMediaType(MediaType.parse("application/json; charset=utf-8"))
                .setService(tClass)
                .setTypeAdapter(HttpResponse.class, new HttpResponseAdapter())
                .build();
    }

    public <T> T createApi(Class<T> tClass) {
        return createApi(URLConfigs.URL_CMS_HOST, tClass);
    }

    public static CmsServiceApi cmsServiceApi() {
        return newInstance().createApi(CmsServiceApi.class);
    }

    public static AddressApi addressApi() {
        return newInstance().createApi(URLConfigs.API_HOST_URL,AddressApi.class);
    }
}