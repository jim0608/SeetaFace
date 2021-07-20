package com.mysafe.lib_identification.camera_view

/**
 * @author Create By 张晋铭
 * @Date on 2021/3/4
 * @Describe:
 */
/**
 * 返回nv21预览数据
 * @param nv21 每一帧图片的nv21格式;
 * @param rotate 帧画面旋转角度
 * @param widthSize 图片宽size
 * @param heightSize 图片高size
 */
data class YUV420spAnalyzeBean(

    var nv21: ByteArray? = null,
    var rotateDegree: Int = 0,
    var imgWidth: Int = 0,
    var imgHeight: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YUV420spAnalyzeBean

        if (nv21 != null) {
            if (other.nv21 == null) return false
            if (!nv21.contentEquals(other.nv21)) return false
        } else if (other.nv21 != null) return false
        if (rotateDegree != other.rotateDegree) return false
        if (imgWidth != other.imgWidth) return false
        if (imgHeight != other.imgHeight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nv21?.contentHashCode() ?: 0
        result = 31 * result + rotateDegree
        result = 31 * result + imgWidth
        result = 31 * result + imgHeight
        return result
    }
}