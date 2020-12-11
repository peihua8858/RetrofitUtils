package com.fz.network.utils;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.fz.common.network.NetworkType;


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
        return isNetAvailable(context);
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
        return com.fz.common.network.NetworkUtil.isConnectedWifi(mContext);
    }

    /**
     * 判断是不是使用手机移动网络链接
     *
     * @author dingpeihua
     * @date 2016/10/19 16:38
     * @version 1.0
     */
    public static boolean isConnectedMobile() {
        return com.fz.common.network.NetworkUtil.isConnectedMobile(mContext);
    }

    /**
     * 检测网络是否连接
     *
     * @author dingpeihua
     * @date 2016/10/19 16:42
     * @version 1.0
     */
    public static boolean isConnected() {
        return isNetAvailable(mContext);
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
            return isNetAvailable(fragment.getContext());
        }
        return false;
    }


    /**
     * 检测网络是否连接
     * @param context
     * @return
     */
    public static boolean isNetAvailable(Context context) {
        return com.fz.common.network.NetworkUtil.isConnected(context);
    }


    public static boolean isConnectedFast() {
        return com.fz.common.network.NetworkUtil.isConnectionFast(mContext);
    }


    public static NetworkType getNetworkType(Context context) {
        return com.fz.common.network.NetworkUtil.getNetworkType(context);
    }
}