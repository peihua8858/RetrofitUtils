package com.fz.network.interceptor;

import com.fz.network.params.TimeoutRequestBody;
import com.socks.library.KLog;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 从header读取超时时间并设置
 *
 * @author dingpeihua
 * @version 1.0
 * @date 2019/6/14 14:22
 */
public class TimeoutInterceptor implements Interceptor {
    public static final String CONNECT_TIMEOUT = "connect_timeout";
    public static final String READ_TIMEOUT = "read_timeout";
    public static final String WRITE_TIMEOUT = "write_timeout";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        int connectTimeout = chain.connectTimeoutMillis();
        int readTimeout = chain.readTimeoutMillis();
        int writeTimeout = chain.writeTimeoutMillis();
        KLog.d("TimeoutInterceptor>>>requestBody:" + (requestBody != null ? requestBody.getClass() : null));
        if (requestBody instanceof TimeoutRequestBody) {
            TimeoutRequestBody body = (TimeoutRequestBody) requestBody;
            connectTimeout = (int) body.getConnectTimeout(chain.connectTimeoutMillis());
            readTimeout = (int) body.getReadTimeout(chain.readTimeoutMillis());
            writeTimeout = (int) body.getWriteTimeout(chain.writeTimeoutMillis());
        } else if (requestBody instanceof FormBody) {
            FormBody body = (FormBody) requestBody;
            int size = body.size();
            FormBody.Builder builder = new FormBody.Builder();
            for (int i = 0; i < size; i++) {
                final String name = body.name(i);
                if (READ_TIMEOUT.equals(name)) {
                    readTimeout = toInteger(body.value(i), chain.readTimeoutMillis());
                } else if (WRITE_TIMEOUT.equals(name)) {
                    writeTimeout = toInteger(body.value(i), chain.writeTimeoutMillis());
                } else if (CONNECT_TIMEOUT.equals(name)) {
                    connectTimeout = toInteger(body.value(i), chain.connectTimeoutMillis());
                } else {
                    builder.add(name, body.value(i));
                }
            }
            request = request.newBuilder().post(builder.build()).build();
        } else {
            String connectNew = request.header(CONNECT_TIMEOUT);
            String readNew = request.header(READ_TIMEOUT);
            String writeNew = request.header(WRITE_TIMEOUT);
            connectTimeout = toTimeout(connectNew, chain.connectTimeoutMillis());
            readTimeout = toTimeout(readNew, chain.readTimeoutMillis());
            writeTimeout = toTimeout(writeNew, chain.writeTimeoutMillis());
        }
        KLog.d("TimeoutInterceptor>>>url:" + request.url() + "\nconnectTimeout:"
                + connectTimeout + ",readTimeout:" + readTimeout + ",writeTimeout:" + writeTimeout);
        return chain
                .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .proceed(request);
    }

    int toTimeout(String value, int defaultValue) {
        int result = toInteger(value, defaultValue);
        return result > 0 ? result : defaultValue;
    }

    /**
     * 将Object对象转成Integer类型
     *
     * @param value
     * @return 如果value不能转成Integer，则默认defaultValue
     */
    int toInteger(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ignored) {
        }
        return defaultValue;
    }
}
