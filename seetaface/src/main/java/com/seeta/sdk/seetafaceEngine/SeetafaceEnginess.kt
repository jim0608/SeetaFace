package com.seeta.sdk.seetafaceEngine

import android.content.Context
import com.mysafe.lib_base.base.CameraFaceEngine
import com.mysafe.lib_base.base.identify.FaceRectInfo
import com.mysafe.lib_base.base.identify.IRecogniseResultCallback

/**
 * @author Create By 张晋铭
 * @date on 2021/6/28
 * @describe
 */
class SeetafaceEnginess: CameraFaceEngine() {
    private val TAG = "TAG_SeetafaceEngine"

    private var helper:SeetafaceEngineHelper? = null

    override fun initEngine(mContext: Context) {
        helper = SeetafaceEngineHelper()
        helper?.initEngine(mContext)
        helper?.initFaceHelper()
    }

    override fun setPreviewFrame(
        nv21: ByteArray,
        widthSize: Int,
        heightSize: Int
    ): MutableList<FaceRectInfo>? {
       return null
    }

    override fun setIdentifyResult(recogniseCallback: IRecogniseResultCallback) {
    }

    override fun releaseEngine() {
    }
}