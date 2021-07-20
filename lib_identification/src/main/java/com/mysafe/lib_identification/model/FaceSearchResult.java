package com.mysafe.lib_identification.model;

/**
 * 搜索人脸结果
 */
public class FaceSearchResult {
    /**
     * 是否成功
     */
    private boolean Success;
    /**
     * 识别结果
     */
    private CompareResult result;


    public FaceSearchResult(CompareResult result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public CompareResult getResult() {
        return result;
    }

    public void setResult(CompareResult result) {
        this.result = result;
    }
}
