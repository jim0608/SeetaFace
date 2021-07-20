package com.mysafe.lib_base.base.identify

import android.graphics.Rect

/**
 * @author Create By 张晋铭
 * @Date on 2021/2/18
 * @Describe:人脸图像数据信息实体
 */
data class FaceRectInfo(
    var rect: Rect? = Rect(),
    var width: Int = 0,
    var height: Int = 0,
    var sex: Int = 0,
    var age: Int = 0,
    var liveness: Int = 0,
    var color: Int = 0,
    var name: String? = null
) {
    private val TAG = "TAG_FaceRectInfo"
}