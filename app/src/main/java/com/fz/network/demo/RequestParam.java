package com.fz.network.demo;


import com.fz.network.params.VpRequestParams;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 公共基本请求参数
 */
public class RequestParam extends VpRequestParams {
    private static final String TAG = "RequestParam";
    /**
     * 是否需要拼接data字段
     */
    private boolean hasData = true;
    final ConcurrentHashMap<String, Object> publicParams = new ConcurrentHashMap<>();
    public ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();

    public RequestParam() {
//       {
//	"data": {
//		"coupon": "",
//		"ab_cart_price": 0,
//		"auto_coupon": 1,
//		"no_login_select": 0,
//		"source": "0",
//		"appsFlyerUID": "1560152354316-3505768219074339794"
//	},
//	"device_id": "e076fff9c03bea2a",
//	"version": "4.7.0",
//	"user_country_code": "HK",
//	"token": "fe4fc9d8e340463169ed1ca4b3942899",
//	"country_code": "HK",
//	"user_country_id": "239",
//	"lang": "zh-tw",
//	"country_id": "239"
//}
        hasData = true;
        publicParams.put("version", "4.7.0");
        publicParams.put("user_country_id", "239");
        publicParams.put("user_country_code", "HK");
        publicParams.put("lang", "zh-tw");
        publicParams.put("token", "fe4fc9d8e340463169ed1ca4b3942899");
        publicParams.put("country_code", "HK");
        publicParams.put("country_id", "239");
        //设备
        publicParams.put("device_id", "e076fff9c03bea2a");
        //Data 中的公共字段
        //购物车优惠券与价格明细同步其他接口
        data.put("appsFlyerUID", "1560152354316-3505768219074339794");
        String userAgent = System.getProperty("http.agent");
        addHeader("User-Agent", userAgent + "RequestFlag/Push");
    }

    public RequestParam(String key, Object value) {
        this();
        publicParams.put(key, value);
    }

    public RequestParam(HashMap<String, String> publicParams) {
        this(true, publicParams);
    }

    public RequestParam(boolean hasData, HashMap<String, String> publicParams) {
        this();
        this.hasData = hasData;
        publicParams.putAll(publicParams);
    }

    public RequestParam(boolean hasData, boolean isReadCache) {
        this();
        setOpenCache(isReadCache);
        this.hasData = hasData;
    }

    /**
     * 创建一个是否支持读写缓存的构造函数；
     *
     * @param isReadCache 是否可读写缓存
     * @author dingpeihua
     * @date 2019/9/2 15:33
     * @version 1.0
     */
    public RequestParam(boolean isReadCache) {
        this(true, isReadCache);
    }


    public void put(String key, double value) {
        if (key != null) {
//            this.urlParams.put(key, String.valueOf(value));
        }
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }
//
//    @Override
//    public RequestParam put(String key, Object value) {
//        checkValue(value);
//        super.put(key, value);
//        return this;
//    }
//
//    boolean checkValue(Object value) {
//        if (value instanceof org.json.JSONArray || value instanceof org.json.JSONObject) {
//            throw new IllegalArgumentException("Value can not be org.json.JSONArray or org.json.JSONObject");
//        }
//        return true;
//    }

    /**
     * put data 数据
     *
     * @param key
     * @param value
     */
    public void putData(String key, Object value) {
        checkValue(value);
        data.put(key, value);
    }

    @Override
    public RequestBody createRequestBody() {
        /**
         * 针对 zaful 的 请求 数据 data 的封装
//         */
//        clearNull();
//        combineMap(urlParams, data);
//        clearNull();
//        //如果请求参数包括data字段，则需要将data集合中的数据放到data字段下
//        if (hasData) {
//            HashMap<String, Object> params = new HashMap<>();
//            params.put("data", data);
//            urlParams.clear();
//            urlParams.putAll(params);
//        }
//        urlParams.putAll(publicParams);
        return super.createRequestBody();
    }

    private void clearNull() {
//        Iterator<String> it = urlParams.keySet().iterator();
//        while (it.hasNext()) {
//            String key = it.next();
//            Object value = urlParams.get(key);
//            if (value == null) {
//                urlParams.remove(key);
//            }
//        }
    }

    /**
     * 合并 Map
     *
     * @param source
     * @param target
     * @return
     */
    public static Map<String, Object> combineMap(Map<String, Object> source, Map<String, Object> target) {
        Iterator<String> it = source.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object value = source.get(key);
            if (value != null) {
                target.put(key, value);
            }
        }
        return target;
    }

    @Override
    public MultipartBody createFileRequestBody() {
//        urlParams.putAll(publicParams);
        return super.createFileRequestBody();
    }

    public static final RequestBody buildRequestBody(Map<String, Object> map) {
        return buildRequestBody(MediaType.parse("application/json; charset=utf-8"), map);
    }

    public static final RequestBody buildRequestBody(MediaType mediaType, Map<String, Object> map) {
        //补全请求地址
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //设置类型
        builder.setType(MultipartBody.FORM);
        Set<String> keys = map.keySet();
        //追加参数
        for (String key : keys) {
            final Object object = map.get(key);
            if (object instanceof File) {
                final File file = (File) object;
                builder.addFormDataPart(key, file.getName(), RequestBody.create(mediaType, file));
            } else {
                builder.addFormDataPart(key, object.toString());
            }
        }
        return builder.build();
    }
}