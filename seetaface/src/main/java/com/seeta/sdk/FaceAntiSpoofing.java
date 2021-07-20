package com.seeta.sdk;

/**
 * @describe 静默活体识别根据输入的图像数据、人脸位置和人脸特征点，对输入人脸进行活体的判断，并返回人脸活体的状态。
 */
public class FaceAntiSpoofing {
    static {
        System.loadLibrary("SeetaFaceAntiSpoofingX600_java");
    }

    public enum Status {
        //REAL(真人)
        REAL,
        //SPOOF(假体)
        SPOOF,
        //FUZZY（由于图像质量问题造成的无法判断）
        FUZZY,
        //DETECTING（正在检测）,DETECTING 状态针对于 PredictVideo 模式。
        DETECTING,
    }

    public long impl = 0;

    private native void construct(SeetaModelSetting setting);

    public native void dispose();

    public FaceAntiSpoofing(SeetaModelSetting setting) {
        this.construct(setting);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.dispose();
    }

    /**
     * 基于单帧图像对人脸是否为活体进行判断。
     * @param image 原始图像数据
     * @param face 人脸位置
     * @param landmarks 人脸特征点数组
     * @return 人脸活体的状态
     */
    public Status Predict(SeetaImageData image, SeetaRect face, SeetaPointF[] landmarks) {
        int status_num = this.PredictCore(image, face, landmarks);
        return Status.values()[status_num];
    }

    /**
     * 基于连续视频序列对人脸是否为活体进行判断。
     * @param image 原始图像数据
     * @param face 人脸位置
     * @param landmarks 人脸特征点数组
     * @return 人脸活体的状态
     */
    public Status PredictVideo(SeetaImageData image, SeetaRect face, SeetaPointF[] landmarks) {
        int status_num = this.PredictVideoCore(image, face, landmarks);
        return Status.values()[status_num];
    }

    //此函数不支持多线程调用，在多线程环境下需要建立对应的 FaceAntiSpoofing 的对象分别调用检测函数
    private native int PredictCore(SeetaImageData image, SeetaRect face, SeetaPointF[] landmarks);

    private native int PredictVideoCore(SeetaImageData image, SeetaRect face, SeetaPointF[] landmarks);

    /**
     * 重置活体识别结果，开始下一次 PredictVideo 识别过程。
     */
    public native void ResetVideo();

    /**
     * 获取活体检测内部分数。
     * @param clarity 人脸清晰度分数
     * @param reality 人脸活体分数
     */
    public native void GetPreFrameScore(float[] clarity, float[] reality);

    /**
     * 设置 Video 模式中识别视频帧数，当输入帧数为该值以后才会有活体的 真假结果。
     * @param number video模式下活体需求帧数
     */
    public native void SetVideoFrameCount(int number);

    /**
     * 获取video模式下活体需求帧数。
     * @return 获取video模式下活体需求帧数。
     */
    public native int GetVideoFrameCount();

    /**
     * 设置阈值。
     * @param clarity 人脸清晰度阈值
     * @param reality 人脸活体阈值
     */
    public native void SetThreshold(float clarity, float reality);

    /**
     * 获取阈值。
     * @param clarity 人脸清晰度阈值
     * @param reality 人脸活体阈值
     */
    public native void GetThreshold(float[] clarity, float[] reality);
}