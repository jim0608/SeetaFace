package com.seeta.sdk;

/**
 * @describe 人脸检测器相关属性值。
 */
public enum Property {
    /**
     * 表示人脸检测器可以检测到的最小人脸，该值越小，支持检测到的人脸尺寸越小，检测速度越慢，默认值为20；
     */
    PROPERTY_MIN_FACE_SIZE(0),

    /**
     * 表示人脸检测器过滤阈值，默认为 0.90；
     */
    PROPERTY_THRESHOLD(1),

    /**
     * 分别表示支持输入的图像的最大宽度和高度；
     */
    PROPERTY_MAX_IMAGE_WIDTH(2),
    PROPERTY_MAX_IMAGE_HEIGHT(3),

    /**
     * 表示人脸检测器计算线程数，默认为 4.
     */
    PROPERTY_NUMBER_THREADS(4);

    private int value;

    private Property(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}