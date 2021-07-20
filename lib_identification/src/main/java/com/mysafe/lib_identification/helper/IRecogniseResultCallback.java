package com.mysafe.lib_identification.helper;

public interface IRecogniseResultCallback {
    /**
     * 人脸搜索完成
     *
     * @param faceId        人脸ID
     * @param isLiveness    活物检测是否通过
     * @param userNum       人脸对应的编号
     * @param userName       人脸对应的名称
     * @param isSimilarPass 识别相似度阈值是否通过
     */
    void onFaceSuccess(int faceId, boolean isLiveness, String userNum,String userName, boolean isSimilarPass);

    /**
     * 人脸识别界面是否有人脸
     * @param isHaveFace true 识别到有人脸 ，false 相机未查找到人脸
     */
    void onFaceSearch(boolean isHaveFace);
    /**
     * 搜索人脸失败,没有找到对应的人脸或是人脸库为空
     */
    void onFaceFailed();

    /**
     * 无人脸进行识别
     */
    void onNotFindFace();
}