/*
 * Copyright (C) Globalegrow E-Commerce Co. , Ltd. 2007-2018.
 * All rights reserved.
 * This software is the confidential and proprietary information
 * of Globalegrow E-Commerce Co. , Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with Globalegrow.
 */

package com.fz.network.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.fragment.app.Fragment;

import com.socks.library.KLog;


/**
 * 网络工具类
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2017/11/8 10:18
 */
public final class NetworkUtil {
    static Context mContext;

    public static void initNetwork(Context context) {
        if (mContext == null) {
            mContext = context.getApplicationContext();
        }
    }

    private NetworkUtil() {
        throw new UnsupportedOperationException("Not support!");
    }

    /**
     * 检测网络是否连接
     *
     * @param context
     * @return 是返回true，否返回false
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            context = mContext;
        }
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 检测网络是否连接
     *
     * @return 是返回true，否返回false
     */
    public static boolean isNetworkConnected() {
        return isNetworkConnected(mContext);
    }

    /**
     * 检测网络是否连接
     *
     * @param fragment
     * @return 是返回true，否返回false
     */
    public static boolean isNetworkConnected(Fragment fragment) {
        if (fragment != null) {
            Activity activity = fragment.getActivity();
            if (activity != null) {
                return isNetworkConnected(fragment.getActivity());
            }
        }
        return isNetworkConnected(mContext);
    }


    /**
     * 判断是不是使用WiFi链接
     *
     * @author dingpeihua
     * @date 2016/10/19 16:37
     * @version 1.0
     */
    public static boolean isConnectedWifi() {
        return Connectivity.isConnectedWifi(mContext);
    }

    /**
     * 判断是不是使用手机移动网络链接
     *
     * @author dingpeihua
     * @date 2016/10/19 16:38
     * @version 1.0
     */
    public static boolean isConnectedMobile() {
        return Connectivity.isConnectedMobile(mContext);
    }

    /**
     * 检测网络是否连接
     *
     * @author dingpeihua
     * @date 2016/10/19 16:42
     * @version 1.0
     */
    public static boolean isConnected() {
        return Connectivity.isConnected(mContext);
    }


    /**
     * 检测网络是否连接
     *
     * @author dingpeihua
     * @date 2016/10/19 16:42
     * @version 1.0
     */
    public static boolean isConnected(Fragment fragment) {
        if (fragment != null) {
            boolean isConnect = Connectivity.isConnected(fragment.getActivity());
            KLog.d("LockNetwork>>>>>isConnect:" + isConnect);
            return isConnect;
        }
        return false;
    }

    /**
     * 检测网络是否连接
     *
     * @author dingpeihua
     * @date 2016/10/19 16:42
     * @version 1.0
     */
    public static boolean isConnected(Context context) {
        if (context != null) {
            boolean isConnect = Connectivity.isConnected(context.getApplicationContext());
            KLog.d("LockNetwork>>>>>isConnect:" + isConnect);
            return isConnect;
        }
        return false;
    }

    public static boolean isConnectedFast() {
        return Connectivity.isConnectedFast(mContext);
    }

    public static NetworkType getNetworkType(Context context) {
        return Connectivity.getNetworkType(context);
    }

    public static NetworkType getNetworkType(NetworkInfo info) {
        return Connectivity.getNetworkType(info);
    }
}