package com.seeta.sdk;

/**
 * @describe 人脸特征点检测器。
 */
public class FaceLandmarker {
    static {
        System.loadLibrary("SeetaFaceLandmarker600_java");
    }

    public long impl = 0;

    private native void construct(SeetaModelSetting seeting);

    public FaceLandmarker(SeetaModelSetting setting) {
        this.construct(setting);
    }

    public native void dispose();

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.dispose();
    }

    /**
     * 获取模型对应的特征点数组长度。
     * @return 模型特征点数组长度
     */
    public native int number();

    /**
     * 获取人脸特征点。
     * @param imageData 图像原始数据
     * @param seetaRect 人脸位置
     * @param pointFS 获取的人脸特征点数组(需预分配好数组长度，长度为number()返回的值)
     */
    public native void mark(SeetaImageData imageData, SeetaRect seetaRect, SeetaPointF[] pointFS);

    /**
     * 获取人脸特征点和遮挡信息。
     * @param imageData 图像原始数据
     * @param seetaRect 人脸位置
     * @param pointFS 获取的人脸特征点数组(需预分配好数组长度，长度为number()返回的值)
     * @param masks 获取人脸特征点位置对应的遮挡信息数组(需预分配好数组长度，长度为number()返回的值), 其中值为1表示被遮挡，0表示未被遮挡
     */
    public native void mark(SeetaImageData imageData, SeetaRect seetaRect, SeetaPointF[] pointFS, int[] masks);
}
