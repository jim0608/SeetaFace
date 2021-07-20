package com.mysafe.lib_base.base

import android.content.Context
import com.mysafe.lib_base.base.identify.FaceRectInfo
import com.mysafe.lib_base.base.identify.IRecogniseResultCallback


/**
 * @author Create By 张晋铭
 * @Date on 2021/2/6
 * @Describe:设计模式：其他所有人脸识别引擎都继承这个方法
 */
abstract class CameraFaceEngine {
    private val TAG = "TAG_CameraFaceEngin"

    /**
     * 初始化引擎
     */
    abstract fun initEngine(mContext: Context)

    /**
     * 帧图片识别
     */
    abstract fun setPreviewFrame(nv21: ByteArray, widthSize: Int, heightSize: Int):MutableList<FaceRectInfo>?

    /**
     * 识别结果返回
     */
    abstract fun setIdentifyResult(recogniseCallback: IRecogniseResultCallback)

    /**
     * 引擎回收
     */
    abstract fun releaseEngine()
}