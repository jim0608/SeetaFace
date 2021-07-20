package com.mysafe.msmealorder_customer.activitys.camera.camera_view

import android.annotation.SuppressLint
import android.util.Log
import android.util.Rational
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.internal.utils.ImageUtil
import com.mysafe.lib_identification.camera_view.CameraCallback
import com.mysafe.lib_identification.camera_view.NV21Utils
import com.mysafe.lib_identification.camera_view.YUV420spAnalyzeBean
import java.util.concurrent.locks.ReentrantLock

/**
 * @author Create By 张晋铭
 * @Date on 2021/2/3
 * @Describe:
 */
class CameraImageAnalyze(private var cameraCallback: CameraCallback) : ImageAnalysis.Analyzer {
    private val TAG = "TAG_StrockImageAnalyze"
    private val lock: ReentrantLock = ReentrantLock()
    private val analyzeDefault: YUV420spAnalyzeBean = YUV420spAnalyzeBean()
    private lateinit var analyzeBean: YUV420spAnalyzeBean
    private var width: Int = 0
    private var height: Int = 0
    private var clipW: Int = 0
    private var clipH: Int = 0
    private var imgRational: Rational? = null
    private var viewRect: Rational? = null
    override fun analyze(image: ImageProxy) {
        lock.lock()
        try {
            analyzeDefault.imgWidth = image.width
            analyzeDefault.imgHeight = image.height
            analyzeDefault.rotateDegree = image.imageInfo.rotationDegrees
            analyzeDefault.nv21 = yuv420_888ToNv21(image)
            image.close()

            if (width != 0 && height != 0 && analyzeDefault.nv21 != null) {
                try {
                    analyzeDefault.nv21 =
                        clipNv21(analyzeDefault.nv21!!, analyzeDefault.imgWidth, analyzeDefault.imgHeight)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i(TAG, "analyze: ${e.message}")
                }
            }
            analyzeBean = analyzeDefault.copy()
            cameraCallback.cameraPreviewFrame(analyzeBean)
        } finally {
            lock.unlock()
        }

    }

    fun setViewLayout(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    /**
     * YUV_420_888转NV21
     *
     * @param imageProxy CameraX ImageProxy
     * @return byte array
     */

    @SuppressLint("UnsafeExperimentalUsageError", "RestrictedApi")
    private fun yuv420_888ToNv21(imageProxy: ImageProxy): ByteArray {
        return ImageUtil.yuv_420_888toNv21(imageProxy)
    }


    fun rotateNV21(input: ByteArray, width: Int, height: Int, rotation: Int): ByteArray {
        val output = ByteArray(input.size)
        val swap = rotation == 90 || rotation == 270
        // **EDIT:** in portrait mode & front cam this needs to be set to true:
        val yflip = rotation == 90 || rotation == 180
        val xflip = rotation == 270 || rotation == 180
        for (x in 0 until width) {
            for (y in 0 until height) {
                var xo = x
                var yo = y
                var w = width
                var h = height
                var xi = xo
                var yi = yo
                if (swap) {
                    xi = w * yo / h
                    yi = h * xo / w
                }
                if (yflip) {
                    yi = h - yi - 1
                }
                if (xflip) {
                    xi = w - xi - 1
                }
                output[w * yo + xo] = input[w * yi + xi]
                val fs = w * h
                val qs = fs shr 2
                xi = xi shr 1
                yi = yi shr 1
                xo = xo shr 1
                yo = yo shr 1
                w = w shr 1
                h = h shr 1
                // adjust for interleave here
                val ui = fs + (w * yi + xi) * 2
                val uo = fs + (w * yo + xo) * 2
                // and here
                val vi = ui + 1
                val vo = uo + 1
                output[uo] = input[ui]
                output[vo] = input[vi]
            }
        }
        return output
    }

    @SuppressLint("UnsafeExperimentalUsageError")


    private fun clipNv21(nv21: ByteArray, widthSize: Int, heightSize: Int): ByteArray {
        var nv212: ByteArray? = null
        //图片分辨率
        if (imgRational == null) {
            imgRational = Rational(heightSize, widthSize)
        }

        if (width < height) {
            viewRect = Rational(width, height * imgRational!!.denominator / imgRational!!.numerator)
            clipW = widthSize * viewRect!!.numerator / viewRect!!.denominator
            if (clipW % 4 != 0) {
                clipW = clipW + 4 - clipW % 4
            }
            analyzeDefault.imgHeight = heightSize
            analyzeDefault.imgWidth = clipW
            nv212 = NV21Utils.clipNV21(
                nv21,
                widthSize,
                heightSize,
                (widthSize - clipW) / 2,
                0,
                clipW,
                heightSize
            )
        } else {
            viewRect = Rational(height, width * imgRational!!.numerator / imgRational!!.denominator)
            clipH = heightSize * viewRect!!.numerator / viewRect!!.denominator
            if (clipH % 4 != 0) {
                clipH = clipH + 4 - clipH % 4
            }
            analyzeDefault.imgWidth = widthSize
            analyzeDefault.imgHeight = clipH
            nv212 = NV21Utils.clipNV21(
                nv21,
                widthSize,
                heightSize,
                0,
                (heightSize - clipH) / 2,
                widthSize,
                clipH
            )

        }

        if (nv212 == null) {
            analyzeDefault.imgWidth = widthSize
            analyzeDefault.imgHeight = heightSize
            return nv21
        }
        return nv212
    }


}