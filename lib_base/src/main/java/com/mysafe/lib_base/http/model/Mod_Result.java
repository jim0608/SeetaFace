package com.mysafe.lib_base.http.model;

/**
 * 网络请求标准结果类
 *
 * @param <Gene>
 */
public class Mod_Result<Gene> {
    /**
     * 是否成功
     */
    public boolean isSuccess;
    /**
     * 错误码
     */
    public int code;
    /**
     * 信息
     */
    public String message;
    /**
     * 部分接口会携带的返回数据,数据类型根据对应的接口来定
     */
    public Gene data;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Gene getData() {
        return data;
    }

    public void setData(Gene data) {
        this.data = data;
    }
}
