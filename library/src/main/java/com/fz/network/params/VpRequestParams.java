/*
    Android Asynchronous Http Client
    Copyright (c) 2011 James Smith <james@loopj.com>
    https://loopj.com

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        https://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package com.fz.network.params;

import android.text.TextUtils;

import com.fz.network.interceptor.TimeoutInterceptor;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author tp
 * <p>
 * A collection of string request parameters or files to send along with requests made from an
 * <pre>
 * RequestParams params = new RequestParams();
 * params.put("username", "james");
 * params.put("password", "123456");
 * params.put("email", "my&#064;email.com");
 * params.put("profile_picture", new File("pic.jpg")); // Upload a File
 * params.put("profile_picture2", someInputStream); // Upload an InputStream
 * params.put("profile_picture3", new ByteArrayInputStream(someBytes)); // Upload some bytes
 *
 * Map&lt;String, String&gt; map = new HashMap&lt;String, String&gt;();
 * map.put("first_name", "James");
 * map.put("last_name", "Smith");
 * params.put("user", map); // url params: "user[first_name]=James&amp;user[last_name]=Smith"
 *
 * Set&lt;String&gt; set = new HashSet&lt;String&gt;(); // unordered collection
 * set.add("music");
 * set.add("art");
 * params.put("like", set); // url params: "like=music&amp;like=art"
 *
 * List&lt;String&gt; list = new ArrayList&lt;String&gt;(); // Ordered collection
 * list.add("Java");
 * list.add("C");
 * params.put("languages", list); // url params: "languages[0]=Java&amp;languages[1]=C"
 *
 * </pre>
 */
public class VpRequestParams implements Serializable {


    protected final static String LOG_TAG = "RequestParams";
    protected final ConcurrentHashMap<String, Object> urlParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, FileWrapper> fileParams = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, String> headers = new ConcurrentHashMap<>();
    protected final Gson mGson = new Gson();
    protected String jsonParams;
    protected boolean isJsonParams = true;
    /**
     * 是否 重复尝试
     */
    protected boolean isRepeatable = true;
    protected String contentEncoding = "utf-8";
    /**
     * 是否显示对话框
     */
    protected boolean isShowDialog = true;

    /**
     * Constructs a new empty {@code RequestParams} instance.
     */
    public VpRequestParams() {
        this((Map<String, Object>) null);
    }

    /**
     * 创建一个是否支持读写缓存的构造函数；
     *
     * @param isReadCache 是否可读写缓存
     * @author dingpeihua
     * @date 2019/9/2 15:33
     * @version 1.0
     */
    public VpRequestParams(boolean isReadCache) {
        this("isOpenCache", isReadCache);
    }

    /**
     * 开启读写缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 15:36
     * @version 1.0
     */
    public final VpRequestParams openCache() {
        put("isOpenCache", true);
        return this;
    }

    /**
     * Adds a key/value string pair to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value string for the new param.
     */
    public VpRequestParams addHeader(String key, String value) {
        if (key != null && value != null) {
            headers.put(key, value);
        }
        return this;
    }

    /**
     * 设置是否开启缓存
     *
     * @author dingpeihua
     * @date 2019/9/2 16:35
     * @version 1.0
     */
    public final void setOpenCache(boolean isReadCache) {
        put("isOpenCache", isReadCache ? "true" : "false");
    }

    /**
     * Constructs a new RequestParams instance containing the key/value string params from the
     * specified map.
     *
     * @param source the source key/value string map to add.
     */
    public VpRequestParams(Map<String, Object> source) {
        if (source != null) {
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Constructs a new RequestParams instance and populate it with a single initial key/value
     * string param.
     *
     * @param key   the key name for the intial param.
     * @param value the value string for the initial param.
     */
    public VpRequestParams(final String key, final Object value) {
        this(new HashMap<String, Object>() {{
            put(key, value);
        }});
    }


    /**
     * Constructs a new RequestParams instance and populate it with multiple initial key/value
     * string param.
     *
     * @param keysAndValues a sequence of keys and values. Objects are automatically converted to
     *                      Strings (including the value {@code null}).
     * @throws IllegalArgumentException if the number of arguments isn't even.
     */
    public VpRequestParams(Object... keysAndValues) {
        int len = keysAndValues.length;
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        }
        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    private long connectTimeout;
    private long readTimeout;
    private long writeTimeout;

    /**
     * 设置连接超时时间
     * 注意：使用该参数时，如果有自定义{@link RequestBody} ，需要使用{@link TimeoutRequestBody}，并调用
     * {@link TimeoutRequestBody#copyTimeout(TimeoutRequestBody)}
     *
     * @author dingpeihua
     * @date 2019/8/30 16:35
     * @version 1.0
     */
    public VpRequestParams connectTimeoutMillis(long timeMillis) {
        connectTimeout = timeMillis;
        return this;
    }

    /**
     * 设置读取超时时间
     * 注意：使用该参数时，如果有自定义{@link RequestBody} ，需要使用{@link TimeoutRequestBody}，并调用
     * {@link TimeoutRequestBody#copyTimeout(TimeoutRequestBody)}
     *
     * @author dingpeihua
     * @date 2019/8/30 16:35
     * @version 1.0
     */
    public VpRequestParams readTimeoutMillis(long timeMillis) {
        readTimeout = timeMillis;
        return this;
    }

    /**
     * 设置写超时时间
     * 注意：使用该参数时，如果有自定义{@link RequestBody} ，需要使用{@link TimeoutRequestBody}，并调用
     * {@link TimeoutRequestBody#copyTimeout(TimeoutRequestBody)}
     *
     * @author dingpeihua
     * @date 2019/8/30 16:35
     * @version 1.0
     */
    public VpRequestParams writeTimeoutMillis(long timeMillis) {
        writeTimeout = timeMillis;
        return this;
    }

    /**
     * Adds a key/value string pair to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value string for the new param.
     */
    public VpRequestParams put(String key, Object value) {
        if (key != null && value != null) {
            urlParams.put(key, value);
        }
        return this;
    }

    public VpRequestParams putJsonParams(String params) {
        jsonParams = params;
        return this;
    }


    /**
     * Adds a file to the request.
     *
     * @param key  the key name for the new param.
     * @param file the file to add.
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public VpRequestParams put(String key, File file) throws FileNotFoundException {
        put(key, file, null, null);
        return this;
    }

    /**
     * Adds a file to the request with custom provided file name
     *
     * @param key            the key name for the new param.
     * @param file           the file to add.
     * @param customFileName file name to use instead of real file name
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public VpRequestParams put(String key, String customFileName, File file) throws FileNotFoundException {
        put(key, file, null, customFileName);
        return this;
    }

    /**
     * Adds a file to the request with custom provided file content-type
     *
     * @param key         the key name for the new param.
     * @param file        the file to add.
     * @param contentType the content type of the file, eg. application/json
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public VpRequestParams put(String key, File file, String contentType) throws FileNotFoundException {
        put(key, file, contentType, null);
        return this;
    }

    /**
     * Adds a file to the request with both custom provided file content-type and file name
     *
     * @param key            the key name for the new param.
     * @param file           the file to add.
     * @param contentType    the content type of the file, eg. application/json
     * @param customFileName file name to use instead of real file name
     * @throws FileNotFoundException throws if wrong File argument was passed
     */
    public VpRequestParams put(String key, File file, String contentType, String customFileName) throws FileNotFoundException {
        if (file == null || !file.exists()) {
            throw new FileNotFoundException();
        }
        if (key != null) {
            fileParams.put(key, new FileWrapper(file, contentType, customFileName));
        }
        return this;
    }


    /**
     * Adds a int value to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value int for the new param.
     */
    public VpRequestParams put(String key, int value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
        return this;
    }

    public void clearUrlParams() {
        urlParams.clear();
    }

    /**
     * Adds a long value to the request.
     *
     * @param key   the key name for the new param.
     * @param value the value long for the new param.
     */
    public VpRequestParams put(String key, long value) {
        if (key != null) {
            urlParams.put(key, String.valueOf(value));
        }
        return this;
    }


    /**
     * Removes a parameter from the request.
     *
     * @param key the key name for the parameter to remove.
     */
    public void remove(String key) {
        urlParams.remove(key);
        fileParams.remove(key);
    }

    /**
     * Check if a parameter is defined.
     *
     * @param key the key name for the parameter to check existence.
     * @return Boolean
     */
    public boolean has(String key) {
        return urlParams.get(key) != null ||
                fileParams.get(key) != null;
    }


    public String getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public ConcurrentHashMap<String, Object> getUrlParams() {
        return urlParams;
    }

    public ConcurrentHashMap<String, FileWrapper> getFileParams() {
        return fileParams;
    }

    public ConcurrentHashMap<String, String> getHeaders() {
        return headers;
    }


    public String getJsonParams() {
        return jsonParams;
    }

    public void setJsonParams(String jsonParams) {
        this.jsonParams = jsonParams;
    }

    public void setJsonParams(boolean b) {
        this.isJsonParams = b;
    }

    public boolean isRepeatable() {
        return isRepeatable;
    }

    public void setRepeatable(boolean isRepeatable) {
        this.isRepeatable = isRepeatable;
    }

    public boolean isShowDialog() {
        return isShowDialog;
    }

    public void setShowDialog(boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, Object> entry : urlParams.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        return result.toString();
    }


    public void setIsRepeatable(boolean flag) {
        this.isRepeatable = flag;
    }


    public static class FileWrapper implements Serializable {
        public final File file;
        public String contentType;
        public String customFileName;

        public FileWrapper(File file, String contentType, String customFileName) {
            this.file = file;
            this.contentType = contentType;
            this.customFileName = customFileName;
            if (TextUtils.isEmpty(contentType)) {
                this.contentType = "image/*";
            }
            if (TextUtils.isEmpty(customFileName)) {
                this.customFileName = file.getName();
            }
        }
    }

    public RequestBody createRequestBody() {
        return createRequestBody(this);
    }

    public MultipartBody createFileRequestBody() {
        return createFileRequestBody(this);
    }


    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType FORM_TYPE = MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8");

    /**
     * 请求参数
     *
     * @param params
     * @return
     */
    public static RequestBody createRequestBody(VpRequestParams params) {
        Map<String, Object> paramsMap = params.urlParams;
        if (params.isJsonParams) {
            if (TextUtils.isEmpty(params.jsonParams)) {
                //json 参数
                Gson mGson = params.mGson;
                params.jsonParams = mGson.toJson(paramsMap);
            }
            return TimeoutRequestBody.create(JSON_TYPE, params.jsonParams)
                    .setHeaders(params.headers)
                    .connectTimeoutMillis(params.connectTimeout)
                    .readTimeoutMillis(params.readTimeout)
                    .writeTimeoutMillis(params.writeTimeout);
        } else {
            // Form表单
            FormBody.Builder builder = new FormBody.Builder();
            Iterator<String> it = paramsMap.keySet().iterator();
            // add 参数
            while (it.hasNext()) {
                String key = it.next();
                Object value = paramsMap.get(key);
                if (value != null) {
                    builder.add(key, VpUrlUtil.toString(value));
                }
            }
            builder.add(TimeoutInterceptor.CONNECT_TIMEOUT, String.valueOf(params.connectTimeout))
                    .add(TimeoutInterceptor.READ_TIMEOUT, String.valueOf(params.readTimeout))
                    .add(TimeoutInterceptor.WRITE_TIMEOUT, String.valueOf(params.writeTimeout));
            if (params.headers.size() > 0) {
                Map<String, String> headers = params.headers;
                Iterator<String> keys = headers.keySet().iterator();
                while (keys.hasNext()) {
                    final String key = keys.next();
                    final String value = headers.get(key);
                    if (value != null) {
                        builder.add(TimeoutInterceptor.HEAD_KEY + key, value);
                    }
                }
            }
            return builder.build();
        }
    }

    /**
     * 文件上传
     *
     * @param params
     * @return
     */
    public static MultipartBody createFileRequestBody(VpRequestParams params) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        //add 参数
        for (ConcurrentHashMap.Entry<String, Object> entry : params.urlParams.entrySet()) {
            builder.addFormDataPart(entry.getKey(), VpUrlUtil.toString(entry.getValue()));
        }
        for (ConcurrentHashMap.Entry<String, FileWrapper> entry : params.fileParams.entrySet()) {
            builder.addFormDataPart(entry.getKey(), entry.getValue().customFileName,
                    RequestBody.create(MediaType.parse(entry.getValue().contentType), entry.getValue().file));
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }
}
