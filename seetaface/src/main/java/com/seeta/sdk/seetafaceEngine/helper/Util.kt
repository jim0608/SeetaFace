package com.seeta.sdk.seetafaceEngine.helper

import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import java.io.*

/**
 * @author Create By 张晋铭
 * @date on 2021/7/13
 * @describe
 */
object Util {
    fun resize(mat: Mat, measuredHeight: Int, measuredWidth: Int) {
        if (mat.width() >= mat.height()) {
            val dsi = measuredWidth.toFloat() / mat.width().toFloat()
            val height = mat.height() * dsi
            Imgproc.resize(mat, mat, Size(measuredWidth.toDouble(), height.toDouble()))

            val h = (measuredHeight - height) / 2
            Core.copyMakeBorder(
                mat,
                mat,
                h.toInt(), h.toInt(), 0, 0,
                Core.BORDER_CONSTANT,
                Scalar(0.0, 0.0, 0.0)
            )
        } else {
            val dsi = measuredHeight.toFloat() / mat.height().toFloat()
            val width = mat.width() * dsi
            Imgproc.resize(mat, mat, Size(width.toDouble(), measuredHeight.toDouble()))

            val w = (measuredWidth - width) / 2
            Core.copyMakeBorder(
                mat,
                mat,
                0, 0, w.toInt(), w.toInt(),
                Core.BORDER_CONSTANT,
                Scalar(0.0, 0.0, 0.0)
            )
        }
    }


    fun byteArrayToFloatArray(data: ByteArray): FloatArray? {
        val bas = ByteArrayInputStream(data)
        val ds = DataInputStream(bas)
        val fArr = FloatArray(data.size / 4)
        try {
            for (i in fArr.indices) {
                fArr[i] = ds.readFloat()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fArr
    }

    fun floatArrayToByteArray(data: FloatArray): ByteArray? {
        val out = ByteArrayOutputStream()
        val dataOutputStream = DataOutputStream(out)
        for (i in data.indices) {
            try {
                dataOutputStream.writeFloat(data[i])
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return out.toByteArray()
    }
}