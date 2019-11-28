package com.fz.network.demo;

import com.fz.network.interceptor.TimeoutInterceptor;
import com.fz.network.remote.BasicDataManager;
import com.fz.networklog.NetLoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @author tp
 * @date 2018/07/31
 * 管理product所有的接口列表
 */
public class ApiManager extends BasicDataManager {
    protected ApiManager() {
        super(new OkHttpClient.Builder()
                .connectTimeout(30000, TimeUnit.MILLISECONDS)
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000, TimeUnit.MILLISECONDS)
                .addInterceptor(new TimeoutInterceptor())
                .addInterceptor(new NetLoggingInterceptor(new NetLoggingInterceptor.OnDynamicParamCallback() {
                    @Override
                    public String getVersionName() {
                        return "1.0.0";
                    }

                    @Override
                    public String getLogTag() {
                        return "log";
                    }

                    @Override
                    public String getAppName() {
                        return "Demo";
                    }
                }))
                .build(), URLConfigs.API_HOST_URL);
    }

    static class InnerHelper {
        static ApiManager dataManager = new ApiManager();
    }

    public synchronized static ApiManager newInstance() {
        return InnerHelper.dataManager;
    }

    public static CmsServiceApi cmsServiceApi() {
        return newInstance().createApi(CmsServiceApi.class);
    }

    public static AddressApi addressApi() {
        return newInstance().createApi(URLConfigs.API_HOST_URL, AddressApi.class);
    }
}
