/*
 * Copyright (C) Globalegrow E-Commerce Co. , Ltd. 2007-2018.
 * All rights reserved.
 * This software is the confidential and proprietary information
 * of Globalegrow E-Commerce Co. , Ltd. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement
 * you entered into with Globalegrow.
 */

package com.fz.network.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.fz.network.utils.NetworkUtil;
import com.socks.library.KLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 作者：Create on 2017/1/4 10:12 by longxl
 * 邮箱：214980423@qq.com
 * 描述：HTTP 缓存管理
 * 最近修改：2017/1/4 10:12 modify by longxl
 */
public class CacheManager {
    public static final String TAG = "CacheManager";
    /**
     * app缓存目录
     */
    public static final String APP_DISK_CACHE_CONFIG = "diskCache";
    /**
     * 数据缓存目录
     */
    static final String DISK_CACHE_CONFIG = "dataCache";
    //max cache size 10mb
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10;

    private static final int DISK_CACHE_INDEX = 0;
    private DiskLruCache mDiskLruCache;
    private static CacheManager cacheManager;

    public static CacheManager initCacheManager(Context context) {
        return initCacheManager(context, null);
    }

    public static CacheManager initCacheManager(Context context, String cachePath) {
        if (TextUtils.isEmpty(cachePath)) {
            cachePath = CacheUtil.getDiskCacheDir(context, APP_DISK_CACHE_CONFIG, DISK_CACHE_CONFIG);
        }
        NetworkUtil.initNetwork(context);
        if (cacheManager == null) {
            synchronized (CacheManager.class) {
                if (cacheManager == null) {
                    cacheManager = new CacheManager(context, cachePath);
                }
            }
        }
        return cacheManager;
    }

    public static CacheManager getInstance() {
        return cacheManager;
    }

    private CacheManager(Context context, String cachePath) {
        File diskCacheDir = new File(cachePath);
        if (!diskCacheDir.exists()) {
            boolean b = diskCacheDir.mkdirs();
            KLog.d(TAG, "!diskCacheDir.exists() --- diskCacheDir.mkdirs()=" + b);
        }
        if (diskCacheDir.getUsableSpace() > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir,
                        /*一个key对应多少个文件*/
                        getAppVersion(context), 1, DISK_CACHE_SIZE);
                KLog.d(TAG, "mDiskLruCache created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 同步设置缓存
     */
    public synchronized void putCache(String key, String value) {
        if (mDiskLruCache == null) {
            return;
        }
        OutputStream os = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(encryptMD5(key));
            os = editor.newOutputStream(DISK_CACHE_INDEX);
            os.write(value.getBytes());
            os.flush();
            editor.commit();
            mDiskLruCache.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 异步设置缓存
     */
    public void setCache(final String key, final String value) {
        new Thread() {
            @Override
            public void run() {
                putCache(key, value);
            }
        }.start();
    }

    /**
     * 同步获取缓存
     */
    public String getCache(String key) {
        if (mDiskLruCache == null) {
            return null;
        }
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(encryptMD5(key));
            if (snapshot != null) {
                fis = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                byte[] data = bos.toByteArray();
                return new String(data, "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 移除缓存
     */
    public boolean removeCache(String key) {
        if (mDiskLruCache != null) {
            try {
                return mDiskLruCache.remove(encryptMD5(key));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 清除缓存
     */
    public boolean clearCache() {
        if (mDiskLruCache != null) {
            try {
                mDiskLruCache.deleteContents();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 对字符串进行MD5编码
     */
    public String encryptMD5(String string) {
        return CacheUtil.encryptToMD5(string);
    }

    /**
     * 获取APP版本号
     */
    private int getAppVersion(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            return pi == null ? 0 : pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
