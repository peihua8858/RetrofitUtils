package com.fz.network.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import androidx.core.content.pm.PackageInfoCompat;

import com.fz.common.network.NetworkUtil;
import com.socks.library.KLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 缓存管理
 * 增加过期时间处理
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/11/4 9:52
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
    /**
     * max cache size 10mb
     */
    private static final long DISK_CACHE_SIZE = 1024 * 1024 * 10;
    /**
     * 数据缓存位置
     */
    private static final int DISK_CACHE_INDEX = 0;
    /**
     * 超时时间缓存位置
     */
    private static final int LIFT_TIME_INDEX = 1;

    private static final int CACHE_VALUE_COUNT = 2;
    /**
     * 默认的过期时间
     */
    private static final long DEFAULT_LIFE_TIME = -1L;
    private DiskLruCache mDiskLruCache;
    private static CacheManager cacheManager;
    private Context context;
    private String cachePath;

    private CacheManager(Context context, String cachePath) {
        this.context = context.getApplicationContext();
        this.cachePath = cachePath;
    }

    private synchronized void initCache(Context context, String cachePath) {
        if (TextUtils.isEmpty(cachePath)) {
            cachePath = CacheUtil.getDiskCacheDir(context, APP_DISK_CACHE_CONFIG, DISK_CACHE_CONFIG);
            this.cachePath = cachePath;
        }
        File diskCacheDir = new File(cachePath);
        if (!diskCacheDir.exists()) {
            boolean b = diskCacheDir.mkdirs();
            KLog.d(TAG, "!diskCacheDir.exists() --- diskCacheDir.mkdirs()=" + b);
        }
        if (diskCacheDir.getUsableSpace() > DISK_CACHE_SIZE) {
            try {
                mDiskLruCache = DiskLruCache.open(diskCacheDir,
                        /*一个key对应多少个文件*/
                        getAppVersion(context), CACHE_VALUE_COUNT, DISK_CACHE_SIZE);
                KLog.d(TAG, "mDiskLruCache created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static CacheManager initCacheManager(Context context) {
        return initCacheManager(context, null);
    }

    public static boolean isInitCache() {
        if (cacheManager == null) {
            KLog.e(TAG, "retrofit缓存器还没有初始化，不能读取和存储缓存数据。请先调用" +
                    "CacheManager.initCacheManager(Context, String)" +
                    "或者CacheManager.initCacheManager(Context)方法初始化缓存管理器");
        }
        return cacheManager != null;
    }

    public static CacheManager initCacheManager(Context context, String cachePath) {
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

    /**
     * 同步设置缓存
     */
    public synchronized void putCache(String key, String value, long lifeTime) {
        if (mDiskLruCache == null) {
            initCache(context, cachePath);
        }
        OutputStream os = null;
        try {
            DiskLruCache.Editor editor = mDiskLruCache.edit(encryptMD5(key));
            os = editor.newOutputStream(DISK_CACHE_INDEX);
            os.write(value.getBytes());
            os.flush();
            //缓存有效截止时间戳是保存数据的当前时间戳+数据有效时间
            editor.set(LIFT_TIME_INDEX, String.valueOf(lifeTime > 0 ? System.currentTimeMillis() + lifeTime : DEFAULT_LIFE_TIME));
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
     * 同步设置缓存
     */
    public synchronized void putCache(String key, String value) {
        putCache(key, value, DEFAULT_LIFE_TIME);
    }

    /**
     * 同步获取缓存
     */
    public String getCache(String key) {
        if (mDiskLruCache == null) {
            KLog.e(TAG, "DiskLruCache not initialized!");
            initCache(context, cachePath);
        }
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            String md5Key = encryptMD5(key);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(md5Key);
            if (snapshot != null) {
                fis = (FileInputStream) snapshot.getInputStream(DISK_CACHE_INDEX);
                //lifeTime=保存数据的当前时间戳+数据有效时间，所以读取数据时，当前时间戳小于lifeTime即为缓存过期
                long lifeTime = CacheUtil.toLong(snapshot.getString(LIFT_TIME_INDEX));
                if (lifeTime == DEFAULT_LIFE_TIME || System.currentTimeMillis() < lifeTime) {
                    bos = new ByteArrayOutputStream();
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) != -1) {
                        bos.write(buf, 0, len);
                    }
                    byte[] data = bos.toByteArray();
                    return new String(data, CacheUtil.UTF_8);
                } else {
                    //缓存过期删除key
                    mDiskLruCache.remove(md5Key);
                    return null;
                }
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
            return getAppVersion(pi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取APP版本号
     */
    private int getAppVersion(PackageInfo pi) {
        if (pi == null) {
            return 0;
        }
        return (int) PackageInfoCompat.getLongVersionCode(pi);
    }
}
