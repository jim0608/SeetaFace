package com.seeta.sdk;

/**
 * @describe 人脸检测器
 */
public class FaceDetector {
    static {

        System.loadLibrary("SeetaFaceDetector600_java");
    }

    //如果报错：no "J" field "impl" in class
    //这个地方必须得有 long型的impl，native中有使用
    public long impl = 0;

    private native void construct(SeetaModelSetting setting) throws Exception;
    public FaceDetector(SeetaModelSetting setting) throws Exception {
        this.construct(setting);
    }

    public native void dispose();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.dispose();
    }

    /**
     * 输入彩色图像，检测其中的人脸。
     *
     * @param image 输入的图像数据
     * @return 人脸信息数组
     */
    public native SeetaRect[] Detect(SeetaImageData image);

    /**
     * 设置人脸检测器相关属性值
     * @param property 人脸检测器属性类别
     * @param value 设置的属性值
     */
    public native void set(Property property, double value);

    /**
     * 获取人脸检测器相关属性值。
     * @param property 人脸检测器属性类别
     * @return 对应的人脸属性值
     */
    public native double get(Property property);
}
