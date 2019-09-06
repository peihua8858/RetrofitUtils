package com.fz.network.interceptor;

import android.os.Build;
import android.text.Html;
import android.text.TextUtils;

import com.socks.library.KLog;

import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 网络日志打印
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/6/17 19:12
 */
public class NetLoggingInterceptor implements Interceptor {
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    final OkHttpClient okHttpClient;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private final OnDynamicParamCallback mCallback;

    public NetLoggingInterceptor(OnDynamicParamCallback callback) {
        if (callback == null) {
            throw new NullPointerException("callback is null.");
        }
        mCallback = callback;
        okHttpClient = new OkHttpClient();
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false;
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !"identity".equalsIgnoreCase(contentEncoding);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        StringBuilder responseHeaderTag = new StringBuilder();
        long startNs = System.nanoTime();
        long tookMs = 0;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            responseHeaderTag.append("<-- HTTP FAILED: ").append(e).append("<br/>");
            throw e;
        }
        tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        StringBuilder requestHeaderTag = new StringBuilder();
        StringBuilder requestHeader = new StringBuilder();
        StringBuilder responseHeader = new StringBuilder();
        HttpUrl url = request.url();
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String requestStartMessage = "<font   face=\"arial\" color=\"#6897bb\">" + url.toString() + " - 耗时：" + tookMs + " ms </font>" + protocol.toString();
        requestHeaderTag.append(requestStartMessage).append("<br/>");
        if (hasRequestBody) {
            if (requestBody.contentType() != null) {
                requestHeader.append("Content-Type: ").append(requestBody.contentType()).append("<br/>");
            }
            requestHeader.append("Content-Length: ").append(requestBody.contentLength()).append("<br/>");
        }
        String requestBodyStr = "";
        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            final String name = headers.name(i);
            requestHeader.append(name).append(": ").append(headers.value(i)).append("<br/>");
        }
        requestHeaderTag.append(requestHeader);
        if (bodyEncoded(request.headers())) {
            requestHeaderTag.append("method: ").append(request.method()).append(" (encoded body omitted)<br/>");
        } else {
            requestHeaderTag.append("method: ").append(request.method()).append("<br/>");
            if (hasRequestBody) {
                Charset charset = UTF8;
                MediaType contentType = requestBody.contentType();
                if (contentType != null) {
                    charset = contentType.charset(UTF8);
                }
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                if (isPlaintext(buffer)) {
                    requestHeaderTag.append("<br/><font color='#AE8ABE'>请求参数 </font>(").append(requestBody.contentLength()).append("-byte body)<br/>");
                    requestBodyStr = buffer.readString(charset == null ? UTF8 : charset);
                    requestHeaderTag
                            .append("<pre style='color: #AAAAAA'>")
                            .append(Html.escapeHtml(requestBodyStr))
                            .append("</pre>");
                    requestHeaderTag.append("<br/><br/>")
                    ;
                } else {
                    requestHeaderTag.append("--> END ").append(request.method()).append(" (binary ")
                            .append(requestBody.contentLength()).append("-byte body omitted)<br/><br/>");
                }
            }
        }
        String responseBodyStr = "";
        ResponseBody responseBody = response.body();
        Headers responseHeaders = response.headers();
        for (int i = 0, count = responseHeaders.size(); i < count; i++) {
            responseHeader.append(responseHeaders.name(i)).append(": ").append(responseHeaders.value(i)).append("<br/>");
        }
        responseHeaderTag.append(responseHeader);
        responseHeaderTag.append("<br/>");
        if (!HttpHeaders.hasBody(response)) {
            responseHeaderTag.append("<-- END HTTP<br/>");
        } else if (bodyEncoded(response.headers())) {
            responseHeaderTag.append("<-- END HTTP (encoded body omitted)<br/>");
        } else if (responseBody != null) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();
            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    responseHeaderTag.append("<br/>");
                    responseHeaderTag.append("Couldn't decode the response body; charset is likely malformed.<br/>");
                    responseHeaderTag.append("<-- END HTTP<br/>");
                }
            }
            if (!isPlaintext(buffer)) {
                responseHeaderTag.append("<br/>");
                responseHeaderTag.append("<-- END HTTP (binary ").append(buffer.size()).append("-byte body omitted)<br/>");
            } else {
                responseHeaderTag.append("<font color='#AE8ABE'>返回结果 </font>(")
                        .append(response.code()).append(' ').append(response.message()).append("--")
                        .append(buffer.size()).append("-byte body)<br/>");
                responseBodyStr = buffer.clone().readString(charset == null ? UTF8 : charset);
                responseHeaderTag.append("<pre style='color: #AAAAAA'>")
                        .append(Html.escapeHtml(responseBodyStr))
                        .append("</pre>");
            }
        }
        sendPost(url.toString(), requestHeader, requestHeaderTag, requestBodyStr, responseHeader, responseHeaderTag, responseBodyStr, tookMs);
        return response;
    }

    public void sendPost(String url, StringBuilder requestHeader, StringBuilder requestHeaderTag, String requestBody, StringBuilder responseHeader, StringBuilder responseHeaderTag, String responseBody, long timing) {
        String ip = mCallback.getServiceIp();
        if (TextUtils.isEmpty(ip)) {
            ip = "10.36.5.100";
        }
        String HOST = "http://" + ip + ":8090/pullLogcat";
        try {
            String tag = mCallback.getLogTag();
            if (TextUtils.isEmpty(tag)) {
                tag = Build.MODEL;
            }
            final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = DEFAULT_DATE_FORMAT.format(new Date(System.currentTimeMillis()));
            StringBuilder log = new StringBuilder();
            log.append(requestHeaderTag).append(responseHeaderTag);
            String bodyLog = tag + ": " + log;

            JSONObject object = new JSONObject();
            object.put("body", bodyLog);
            object.put("timestamp", date);
            object.put("requestHeader", requestHeader);
            object.put("responseHeader", responseHeader);
            String phoneInfo = "android-" + Build.MODEL + "-" + Build.VERSION.RELEASE + "-" + Locale.getDefault().getLanguage();
            object.put("level", phoneInfo);
            object.put("userId", mCallback.getAppName());
            object.put("appName", mCallback.getAppName());
            object.put("platform", getPlatform(tag));

            object.put("url", url);
            object.put("request", requestBody);
            object.put("response", responseBody);
            object.put("device", Build.MODEL + "-" + Build.VERSION.RELEASE);
            object.put("domain", mCallback.getPlatform());
            object.put("version", mCallback.getVersionName());
            object.put("feeTime", timing);
            KLog.d("NetLoggingInterceptor>>>" + object);
            RequestBody body = RequestBody.create(JSON_TYPE, object.toString());
            Request request = new Request.Builder()
                    .url(HOST)//请求接口。如果需要传参拼接到接口后面。
                    .post(body)
                    .build();//创建Request 对象
            okHttpClient.newCall(request).enqueue(new ResponseCallback());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String getPlatform(String logTag) {
        if (TextUtils.isEmpty(logTag)) {
            return mCallback.getPlatform();
        }
        return mCallback.getPlatform() + "-" + logTag;
    }

    static class ResponseCallback implements Callback {

        @Override
        public void onFailure(Call call, IOException e) {
            KLog.d("NetLoggingInterceptor>>>error:" + e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) {
            KLog.d("NetLoggingInterceptor>>>log:" + response.toString());
        }
    }

    /**
     * 日志系统动态获取参数
     *
     * @author dingpeihua
     * @version 1.0
     * @date 2019/9/2 18:03
     */
    public interface OnDynamicParamCallback {
        /**
         * APP当前版本号，如：1.0.0
         *
         * @return
         */
        String getVersionName();

        /**
         * 日志筛选标签
         *
         * @author dingpeihua
         * @date 2019/9/2 18:27
         * @version 1.0
         */
        String getLogTag();

        /**
         * 日志服务器ip地址
         *
         * @author dingpeihua
         * @date 2019/9/2 18:27
         * @version 1.0
         */
        default String getServiceIp() {
            return "10.36.5.100";
        }

        /**
         * 项目名称
         *
         * @return
         */
        String getAppName();

        /**
         * 域名,默认项目名称
         *
         * @return
         */
        default String getPlatform() {
            return getAppName() + "-Android";
        }
    }
}
