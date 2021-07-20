package com.mysafe.lib_identification.enums;

/**
 * 人脸识别中可能出现的状态
 */
public class RequestFeatureStatus {
    /**
     * 处理中
     */
    public static final int SEARCHING = 0;
    /**
     * 识别成功
     */
    public static final int SUCCEED = 1;
    /**
     * 识别失败
     */
    public static final int FAILED = 2;
    /**
     * 待重试
     */
    public static final int TO_RETRY = 3;
}
