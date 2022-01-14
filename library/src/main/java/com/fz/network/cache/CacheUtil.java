package com.fz.network.cache;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.socks.library.KLog;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Junk drawer of utility methods.
 */
final class CacheUtil {
    static final Charset US_ASCII = Charset.forName("US-ASCII");
    static final Charset UTF_8 = Charset.forName("UTF-8");

    private CacheUtil() {
        throw new UnsupportedOperationException();
    }

    static String readFully(Reader reader) throws IOException {
        try {
            StringWriter writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        } finally {
            reader.close();
        }
    }

    /**
     * Deletes the contents of {@code dir}. Throws an IOException if any file
     * could not be deleted, or if {@code dir} is not a readable directory.
     */
    static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("not a readable directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (!file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }

    static void closeQuietly(/*Auto*/Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (RuntimeException rethrown) {
                throw rethrown;
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 进行MD5加密
     *
     * @param info 要加密的信息
     * @return String 加密后的字符串
     */
    public static String encryptToMD5(String info) {
        byte[] digesta = null;
        try {
            // 得到一个md5的消息摘要
            MessageDigest alga = MessageDigest.getInstance("MD5");
            // 添加要进行计算摘要的信息
            alga.update(info.getBytes(UTF_8));
            // 得到该摘要
            digesta = alga.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将摘要转为字符串
        String rs = byte2hex(digesta);
        KLog.d("未加密:" + info + ",加密后:" + rs);
        return rs;
    }

    /**
     * 将二进制转化为16进制字符串
     *
     * @param b 二进制字节数组
     * @return String
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (byte aB : b) {
            stmp = (Integer.toHexString(aB & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toLowerCase();
    }

    /**
     * 获取Uri参数的异常处理
     *
     * @param uri 需要获取指定参数的uri
     * @param key 参数key值
     * @return
     * @author dingpeihua
     * @date 2016/5/28 17:46
     * @version 1.0
     */
    public static String getUriParameter(Uri uri, String key) {
        String parameter;
        try {
            parameter = uri.getQueryParameter(key);
        } catch (Exception e) {
            parameter = "";
        }
        return TextUtils.isEmpty(parameter) ? "" : parameter;
    }

    /**
     * 将Object对象转成boolean类型
     *
     * @param value
     * @return 如果value不能转成boolean，则默认false
     */
    public static Boolean toBoolean(Object value) {
        return toBoolean(value, false);
    }

    /**
     * 将Object对象转成boolean类型
     *
     * @param value
     * @return 如果value不能转成boolean，则默认defaultValue
     */
    public static Boolean toBoolean(Object value, boolean defaultValue) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return "true".equalsIgnoreCase((String) value);
        }
        return defaultValue;
    }

    /**
     * 将Object对象转成boolean类型
     *
     * @param value
     * @return 如果value不能转成boolean，则默认false
     */
    public static long toLong(Object value) {
        return toLong(value, 0L);
    }

    /**
     * 将Object对象转成boolean类型
     *
     * @param value
     * @return 如果value不能转成boolean，则默认defaultValue
     */
    public static long toLong(Object value, long defaultValue) {
        if (value instanceof Long) {
            return (Long) value;
        } else if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.valueOf((String) value);
            } catch (NumberFormatException ignored) {
            }
        }
        return defaultValue;
    }

    /**
     * 获取app 缓存目录
     *
     * @param uniqueName
     * @return 获取到的缓存目录
     * @author dingpeihua
     * @date 2017/3/24 10:11
     * @version 1.0
     */
    public static String getDiskCacheDir(Context context, String folderName, String uniqueName) {
        String cachePathFile = getDiskCacheRootDir(context, folderName);
        File outFilePath = new File(cachePathFile, uniqueName);
        if (!outFilePath.exists()) {
            outFilePath.mkdirs();
        }
        return outFilePath.getAbsolutePath();
    }

    /**
     * 获取app缓存目录
     *
     * @param context
     * @param folderName
     * @author dingpeihua
     * @date 2019/9/2 15:52
     * @version 1.0
     */
    public static String getDiskCacheRootDir(Context context, String folderName) {
        //如果SD卡存在通过getExternalCacheDir()获取路径，
        //放在路径 /sdcard/Android/data/<application package>/cache/uniqueName
        File file = context.getExternalCacheDir();
        //如果SD卡不存在通过getCacheDir()获取路径，
        //放在路径 /data/data/<application package>/cache/uniqueName
        if (file == null) {
            file = context.getCacheDir();
        }
        File outFilePath = new File(file, folderName);
        return outFilePath.getAbsolutePath();
    }
}
