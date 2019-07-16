package com.fz.network.demo;


import io.reactivex.Flowable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * 地址
 * @author linjinyan
 */
public interface AddressApi {

    String ADDRESS_LIST_URL = "address/get_address_list";

    String DELETE_ADDRESS_URL = "address/del_address";

    String DEFAULT_ADDRESS_URL = "address/default_address";

    String GOOGLE_ADDRESS_URL = "address/get_google_state";

    String URL_COUNTRY_LIST = "common/get_country_list";

    String URL_ADDRESS_STATE_LIST = "common/get_city_list";

    String URL_ADDRESS_CITY_LIST = "common/get_city_list_by_province";

    /**
     * 设置中更改选择的国家
     */
    String URL_MEMBER_COUNTRY_LIST = "common/get_member_country_list";

    String URL_SMART_CITY_SEARCH = "address/get_city_hint";

    String URL_COUNTRY_CURRENT = "address/get_cur_country_info";
    /**
     * 获取google智能地址详情
     */
    String URL_GOOGLE_ADDRESS_DETAIL = "address/get_state_detail";

    String URL_ADDRESS_EDIT = "address/edit_address";

    /**
     * 智能纠正地址接口
     */
    String URL_CORRECT_ADDRESS = "address/check_shipping_address";

    /**
     * 查询国家城市ZipCode
     */
    String ADDRESS_QUERY_ZIP_URL = "address/by_city_query_zip_code";

    /**
     * 地址四合一：获取国家州城市乡镇数据
     */
    String COUNTRY_REGION_URL = "address/get_area_linkage";

    /**
     * 获取新兴国家信息
     */
    String URL_EMERGING_COUNTRY = "common/get_emerging_country";

    /**
     * 获取地址列表
     */
    @POST(ADDRESS_LIST_URL)
    Flowable<String> getAddressLits(@Body RequestBody params);

    /**
     * 删除地址
     */
    @POST(DELETE_ADDRESS_URL)
    Flowable<String> reqDeleteAddress(@Body RequestBody params);

    /**
     * 设置默认地址
     */
    @POST(DEFAULT_ADDRESS_URL)
    Flowable<String> setDefaultAddress(@Body RequestBody params);


    /**
     * 智能地址
     */
    @POST(GOOGLE_ADDRESS_URL)
    Flowable<String> getGoogleSmartAddress(@Body RequestBody params);

    /**
     * 获取国家列表数据
     */
    @POST(URL_COUNTRY_LIST)
    Flowable<String> getCountryDatas(@Body RequestBody params);

    /**
     * 获取州列表数据
     */
    @POST(URL_ADDRESS_STATE_LIST)
    Flowable<String> getStateDatas(@Body RequestBody params);

    /**
     * 获取城市列表数据
     */
    @POST(URL_ADDRESS_CITY_LIST)
    Flowable<String> getCityDatas(@Body RequestBody params);

    /**
     * 设置中更改选择的国家
     */
    @POST(URL_MEMBER_COUNTRY_LIST)
    Flowable<String> getSettingCountryDatas(@Body RequestBody params);

    /**
     * 获取智能搜索城市列表数据
     */
    @POST(URL_SMART_CITY_SEARCH)
    Flowable<String> getSmartCityDatas(@Body RequestBody params);


    /**
     * 获取当前国家列表
     */
    @POST(URL_COUNTRY_CURRENT)
    Flowable<String> getCurrentCountry(@Body RequestBody params);

    /**
     * 获取google智能地址详情
     */
    @POST(URL_GOOGLE_ADDRESS_DETAIL)
    Flowable<String> getGoogleAddressDetail(@Body RequestBody params);

    /**
     * 添加与修改地址
     * @param params params
     * @return 回调string
     */
    @POST(URL_ADDRESS_EDIT)
    Flowable<String> addEditAddress(@Body RequestBody params);

    /**
     * 智能地址纠正
     * @param params params
     * @return 回调string
     */
    @POST(URL_CORRECT_ADDRESS)
    Flowable<String> smartCorrectAddress(@Body RequestBody params);

    /**
     * 查询国家城市ZipCode
     * @param params params
     * @return string
     */
    @POST(ADDRESS_QUERY_ZIP_URL)
    Flowable<String> reqZipCodeData(@Body RequestBody params);

    /**
     * 获取国家州城市乡镇数据
     * @param params params
     * @return string
     */
    @POST(COUNTRY_REGION_URL)
    Flowable<String> reqCountryRegionData(@Body RequestBody params);

    /**
     * 获取新兴国家数据列表
     * @param params params
     * @return string
     */
    @POST(URL_EMERGING_COUNTRY)
    Flowable<String> getEmergingCountry(@Body RequestBody params);

}
