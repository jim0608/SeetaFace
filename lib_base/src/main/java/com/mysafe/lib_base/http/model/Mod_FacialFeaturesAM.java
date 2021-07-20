package com.mysafe.lib_base.http.model;

/**
 * 人脸识别存储模型
 */
public class Mod_FacialFeaturesAM {
    /**
     * 人员学号
     */
    public String studentNo;
    /**
     * 人脸识别的特征值
     */
    public byte[] facialFeatures;

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public byte[] getFacialFeatures() {
        return facialFeatures;
    }

    public void setFacialFeatures(byte[] facialFeatures) {
        this.facialFeatures = facialFeatures;
    }
}
