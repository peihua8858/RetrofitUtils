package com.fz.network.demo;

import com.fz.network.cache.ICacheResponse;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * {"statusCode":200,"result":[],"msg":""}
 * {@link #result}可能为空，使用需要做非空判断
 * 如果{@link #result}类型不是list，而接口返回result是JSONArray,此时数据为空
 *
 * @param <RESULT>
 */
public final class HttpResponse<RESULT> extends ICacheResponse {
    private int statusCode;
    private String msg;
    private RESULT result;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public RESULT getResult() {
        return result;
    }

    public void setResult(RESULT result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                '}';
    }

    @Override
    protected boolean checkResult() {
        if (result instanceof Collection) {
            return ((Collection) result).size() > 0;
        }
        if (result instanceof Map) {
            return ((Map) result).size() > 0;
        }
        return result != null;
    }
}
