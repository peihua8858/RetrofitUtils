package com.fz.network.demo;

import com.fz.network.BuildConfig;

/**
 * app域名配置
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2018/8/15 09:30
 */
public class URLConfigs {
    /**
     * 正式环境
     * https://testapp.zaful.com/
     * https://app.zaful.com/
     */
    static final String R_HOST = "https://app.zaful.com/";
    public static final String HOST;


    /**
     * 服务端接口根路径，develop表示测试环境,其他表示正式环境
     */
    public final static String API_HOST_URL;


    public static final String URL_CMS_R_HOST = "http://www.cms.com.release.php7.egomsl.com/api/";//"https://cms.glosop.com/api/";
    public static final String URL_CMS_HOST;

    /**
     * api版本字符
     * https://app.zaful.com/api_android/4.7.0/address/get_address_list
     */
    public static final String API_VERSION = "api_android/4.7.0//";

    static {
        HOST = R_HOST;
        API_HOST_URL = HOST + API_VERSION;
        URL_CMS_HOST = URL_CMS_R_HOST;
    }
}
