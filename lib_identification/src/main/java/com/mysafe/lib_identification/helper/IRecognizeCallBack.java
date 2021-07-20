package com.mysafe.lib_identification.helper;

import com.mysafe.lib_identification.model.CompareResult;

public interface IRecognizeCallBack {
    /**
     * 识别结果回调
     *
     * @param isHavePerson 是否有结果
     * @param compareResult          比对结果
     * @param liveness               活体值
     * @param similarPass            是否通过（依据设置的阈值）
     */
    void onRecognized(boolean isHavePerson, CompareResult compareResult, Integer liveness, boolean similarPass);

    void onHaveNotFace();
}
