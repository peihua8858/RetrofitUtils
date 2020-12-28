package com.fz.network.demo;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * cms 接口
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/2/16 14:18
 */
public interface CmsServiceApi {
    /**
     * 获取cms广告数据
     */
    String URL_GET_CMS_AD = "app-adverts/adverts";
    /**
     * 获取cms菜单数据
     */
    String URL_GET_CMS_MENU = "cms-api/get-menu";
    /**
     * 获取cms页面数据
     */
    String URL_GET_CMS_PAGE_DATA = "cms-api/get-page";

    /**
     * 获取cms首页菜单数据
     *
     * @param request
     * @return
     */
    @POST(URL_GET_CMS_MENU)
    Flowable<Response<HttpResponse<List<MenuBean>>>> getMenuList(@Body RequestBody request);

    /**
     * 获取cms页面数据
     *
     * @param request
     * @return
     */
    @POST(URL_GET_CMS_PAGE_DATA)
    Flowable<Response<HttpResponse<List<MenuData>>>> getCmsPageData(@Body RequestBody request);

    /**
     * 获取cms备份数据
     *
     * @param url
     * @return
     */
    @GET
    Flowable<Response<HttpResponse<List<MenuBean>>>> getCmsZafulData(@Url String url);
}
