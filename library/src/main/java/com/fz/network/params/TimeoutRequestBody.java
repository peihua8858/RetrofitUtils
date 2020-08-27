//package com.fz.network.params;
//
//import androidx.annotation.Nullable;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Map;
//
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import okio.BufferedSink;
//import okio.ByteString;
//
///**
// * 请求体处理超时时间
// *
// * @author dingpeihua
// * @version 1.0
// * @date 2019/8/30 17:02
// */
//public final class TimeoutRequestBody extends RequestBody {
//    private final RequestBody other;
//    private long connectTimeout;
//    private long readTimeout;
//    private long writeTimeout;
//    private Map<String, String> headers;
//
//    public TimeoutRequestBody connectTimeoutMillis(long timeMillis) {
//        this.connectTimeout = timeMillis;
//        return this;
//    }
//
//    public TimeoutRequestBody readTimeoutMillis(long timeMillis) {
//        this.readTimeout = timeMillis;
//        return this;
//    }
//
//    public TimeoutRequestBody writeTimeoutMillis(long timeMillis) {
//        writeTimeout = timeMillis;
//        return this;
//    }
//
//    public long getConnectTimeout(long defaultTimeout) {
//        return connectTimeout > 0 ? connectTimeout : defaultTimeout;
//    }
//
//    public TimeoutRequestBody setHeaders(Map<String, String> headers) {
//        this.headers = headers;
//        return this;
//    }
//
//    public Map<String, String> getHeaders() {
//        return headers;
//    }
//
//    public long getReadTimeout(long defaultTimeout) {
//        return readTimeout > 0 ? readTimeout : defaultTimeout;
//    }
//
//    public long getWriteTimeout(long defaultTimeout) {
//        return writeTimeout > 0 ? writeTimeout : defaultTimeout;
//    }
//
//    public TimeoutRequestBody(RequestBody other) {
//        this.other = other;
//    }
//
//    @Override
//    public long contentLength() throws IOException {
//        return other.contentLength();
//    }
//
//    @Override
//    public MediaType contentType() {
//        return other.contentType();
//    }
//
//    @Override
//    public void writeTo(BufferedSink sink) throws IOException {
//        other.writeTo(sink);
//    }
//
//    public TimeoutRequestBody copyTimeout(TimeoutRequestBody body) {
//        if (body == null) {
//            return this;
//        }
//        writeTimeoutMillis(body.writeTimeout);
//        connectTimeoutMillis(body.connectTimeout);
//        readTimeoutMillis(body.readTimeout);
//        return this;
//    }
//
//    /**
//     * @see RequestBody#create(MediaType, String)
//     */
//    public static TimeoutRequestBody create(@Nullable MediaType contentType, String content) {
//        return new TimeoutRequestBody(RequestBody.create(contentType, content));
//    }
//
//    /**
//     * @see RequestBody#create(MediaType, ByteString)
//     */
//    public static TimeoutRequestBody create(final @Nullable MediaType contentType, final ByteString content) {
//        return new TimeoutRequestBody(RequestBody.create(contentType, content));
//    }
//
//    /**
//     * @see RequestBody#create(MediaType, byte[], int, int)
//     */
//    public static TimeoutRequestBody create(final @Nullable MediaType contentType, final byte[] content) {
//        return create(contentType, content, 0, content.length);
//    }
//
//    /**
//     * @see RequestBody#create(MediaType, byte[], int, int)
//     */
//    public static TimeoutRequestBody create(final @Nullable MediaType contentType, final byte[] content,
//                                            final int offset, final int byteCount) {
//        return new TimeoutRequestBody(RequestBody.create(contentType, content, offset, byteCount));
//    }
//
//    /**
//     * @see RequestBody#create(MediaType, File)
//     */
//    public static TimeoutRequestBody create(final @Nullable MediaType contentType, final File file) {
//        return new TimeoutRequestBody(RequestBody.create(contentType, file));
//    }
//}
