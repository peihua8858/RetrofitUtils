package com.fz.network.demo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.fz.common.network.NetworkUtil;
import com.fz.network.cache.CacheManager;
import com.fz.network.demo.databinding.ActivityMainBinding;
import com.socks.library.KLog;

import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

public class MainActivity extends DataBindingBaseActivity<ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBinding.btnAddressList.setOnClickListener(this::onClick);
        mBinding.unbind();
        String networkDiskCacheDir = new File(getExternalCacheDir(), "netWork").getAbsolutePath();
        CacheManager.initCacheManager(this, networkDiskCacheDir);
        NetworkUtil.init(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.btn_cms_data) {
            requestCmsData();
        } else if (view.getId() == R.id.btn_address_list) {

        }
    }

    private void requestAddressList() {
//        ApiManager.addressApi().getAddressLits(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "{\"data\":{\"coupon\":\"\",\"ab_cart_price\":0,\"auto_coupon\":1,\"no_login_select\":0,\"source\":\"0\",\"appsFlyerUID\":\"1560152354316-3505768219074339794\"},\"device_id\":\"e076fff9c03bea2a\",\"version\":\"4.7.0\",\"user_country_code\":\"HK\",\"token\":\"fe4fc9d8e340463169ed1ca4b3942899\",\"country_code\":\"HK\",\"user_country_id\":\"239\",\"lang\":\"zh-tw\",\"country_id\":\"239\"}"))
        final RequestParam request = new RequestParam(false, true);
        request.connectTimeoutMillis(1000);
        request.readTimeoutMillis(1000);
        request.writeTimeoutMillis(1000);
        request.setJsonParams(false);
        request.setLifeTime(5000000);
        ApiManager.addressApi().getAddressLits(request.createRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RSubscriber<String>(this) {
                    @Override
                    protected void success(String response) {
                        KLog.d("MainActivity", "response:" + response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        KLog.e("MainActivity", "response:" + e.getMessage());
                    }
                });
    }

    void requestCmsData() {
        final RequestParam request = new RequestParam(false, true);
        request.setJsonParams(true);
        request.connectTimeoutMillis(1000);
        request.readTimeoutMillis(1000);
        request.writeTimeoutMillis(1000);
        request.setLifeTime(5000000);
        buildParams(request);
        ApiManager.cmsServiceApi().getMenuList(request.createRequestBody())
                .map(MainActivity.<HttpResponse<List<MenuBean>>>handleFunction())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RSubscriber<HttpResponse<List<MenuBean>>>(this) {
                    @Override
                    protected void success(HttpResponse<List<MenuBean>> response) {
                        KLog.d("MainActivity", "response:" + response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        super.onError(e);
                        KLog.e("MainActivity", "response:" + e.getMessage());
                    }
                });
    }

    /**
     * 处理 {@link Response} 错误信息
     *
     * @author dingpeihua
     * @date 2019/6/6 13:49
     * @version 1.0
     */
    public static <T> Function<Response<T>, T> handleFunction() {
        return new Function<Response<T>, T>() {
            @Override
            public T apply(Response<T> response) throws Exception {
                if (response.isSuccessful()) {
                    return response.body();
                } else {
                    throw new HttpException(response);
                }
            }
        };
    }

    public static void buildParams(RequestParam param) {
        if (param == null) {
            return;
        }
        buildCommon(param);
        param.put("platform", "Android");
        param.put("page_code", "Homepage");
        //release 不加
        param.put("is_new_customer", "1");
    }

    static void buildCommon(RequestParam param) {
        param.put("website", "ZF");
        param.put("api_version", "2");
        param.put("mid", "e076fff9c03bea2a");
        param.put("app_version", "4.7.0");
        param.put("language_code", "zh-tw");
        param.put("country_acronym", "HK");
        param.put("language", "zh-tw");
    }
}
