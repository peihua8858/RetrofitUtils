package com.fz.network.params;


import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;

public class VpUrlUtil {


    public static final String TAG = VpUrlUtil.class.getName();

    /**
     * 添加公共参数
     *
     * @param url
     * @param params url后面的参数
     * @param body   post的内容
     * @return
     */
    private String addPublicParams(String url, VpRequestParams params, String body, boolean isEncrypt) {

        if (url == null) {
            return "";
        }
        return url;
    }

    /**
     * Will encode url, if not disabled, and adds params on the end of it
     *
     * @param url             String with URL, should be valid URL without params
     * @param params          RequestParams to be appended on the end of URL
     * @param shouldEncodeUrl whether url should be encoded (replaces spaces with %20)
     * @return encoded url if requested with params appended if any available
     * @throws UnsupportedEncodingException
     */
    public static String getUrlWithQueryString(boolean shouldEncodeUrl, String url, VpRequestParams params) throws UnsupportedEncodingException {
        if (url == null)
            return null;

        if (shouldEncodeUrl) {
            try {
                String decodedURL = URLDecoder.decode(url, "UTF-8");
                URL _url = new URL(decodedURL);
                URI _uri = new URI(_url.getProtocol(), _url.getUserInfo(), _url.getHost(), _url.getPort(), _url.getPath(), _url.getQuery(), _url.getRef());
                url = _uri.toASCIIString();
            } catch (Exception ex) {
                // Should not really happen, added just for sake of validity
                Log.e(TAG, "getUrlWithQueryString encoding URL", ex);
            }
        }

        if (params != null) {

            String paramString = "";
            //add 参数
            for (ConcurrentHashMap.Entry<String, Object> entry : params.getUrlParams().entrySet()) {
                //增加url params
                paramString += entry.getKey() + "=" + URLEncoder.encode(VpUrlUtil.toString(entry.getValue()), params.getContentEncoding()) + "&";
            }
            paramString = paramString.substring(0, paramString.length() - 1);

            // Only add the query string if it isn't empty and it
            // isn't equal to '?'.
            if (!paramString.equals("") && !paramString.equals("?")) {
                url += url.contains("?") ? "&" : "?";
                url += paramString;
            }
        }
        return url;
    }

    /**
     * 将Object对象转成String类型
     *
     * @param value
     * @return 如果value不能转成String，则默认""
     */
    public static String toString(Object value) {
        return toString(value, "");
    }

    /**
     * 将Object对象转成String类型
     *
     * @param value
     * @return 如果value不能转成String，则默认defaultValue
     */
    public static String toString(Object value, String defaultValue) {
        if (value instanceof String) {
            return (String) value;
        } else if (value != null) {
            return String.valueOf(value);
        }
        return defaultValue;
    }
}
