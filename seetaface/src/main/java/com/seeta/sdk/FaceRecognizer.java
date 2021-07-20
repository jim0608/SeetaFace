package com.seeta.sdk;


/**
 * @describe 人脸识别器。
 */
public class FaceRecognizer {

    static {
        System.loadLibrary("SeetaFaceRecognizer600_java");
    }


    public long impl = 0;

    private native void construct(SeetaModelSetting setting);

    public FaceRecognizer(SeetaModelSetting setting) {
        this.construct(setting);
    }

    public native void dispose();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.dispose();
    }

    /**
     * 获取裁剪人脸的宽度。
     * @return 返回的人脸宽度
     */
    public native int GetCropFaceWidth();

    /**
     * 获取裁剪的人脸高度。
     * @return 返回的人脸高度
     */
    public native int GetCropFaceHeight();

    /**
     * 获取裁剪的人脸数据通道数。
     * @return 返回的人脸数据通道数
     */
    public native int GetCropFaceChannels();

    /**
     * 获取特征值数组的长度。
     * @return 特征值数组的长度
     */
    public native int GetExtractFeatureSize();

    /**
     * 裁剪人脸。
     * @param image 原始图像数据
     * @param points 人脸特征点数组
     * @param face 返回的裁剪人脸
     * @return true表示人脸裁剪成功
     */
    public native boolean CropFace(SeetaImageData image, SeetaPointF[] points, SeetaImageData face);

    /**
     * 输入裁剪后的人脸图像，提取人脸的特征值数组。
     * @param face 裁剪后的人脸图像数据
     * @param features 返回的人脸特征值数组
     * @return true表示提取特征成功
     */
    public native boolean ExtractCroppedFace(SeetaImageData face, float[] features);

    /**
     * 输入原始图像数据和人脸特征点数组，提取人脸的特征值数组。
     * @param image 原始的人脸图像数据
     * @param points 人脸的特征点数组
     * @param features 返回的人脸特征值数组
     * @return true表示提取特征成功
     */
    public native boolean Extract(SeetaImageData image, SeetaPointF[] points, float[] features);

    /**
     * 比较两人脸的特征值数据，获取人脸的相似度值。
     * @param features1 特征数组一
     * @param features2 特征数组二
     * @return 相似度值
     */
    public native float CalculateSimilarity(float[] features1, float[] features2);

    /**
     * 设置人脸检测器相关属性值
     * PROPERTY_NUMBER_THREADS: 表示计算线程数，默认为 4.
     * @param property 人脸检测器属性类别
     * @param value 设置的属性值
     */
    public native void set(Property property, double value);
}
