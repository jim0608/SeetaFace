package com.seeta.sdk.seetafaceEngine.helper

import android.graphics.*
import android.util.Log
import com.seeta.sdk.SeetaImageData
import com.seeta.sdk.SeetaPointF
import com.seeta.sdk.SeetaRect
import com.seeta.sdk.seetafaceEngine.EngineHelper
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc


/**
 * @author Create By 张晋铭
 * @date on 2021/6/28
 * @describe
 */
class MatchFaceHelper {
    private val TAG = "TAG_DertectionActivityHelper"

    private var faceMatched = arrayOf<SeetaRect>()
    private var matMatched = Mat()

    private var bitMat = Mat()

    fun checkMat(fileUri: String, measuredWidth: Int, measuredHeight: Int): Bitmap? {
        val mat = Imgcodecs.imread(fileUri, Imgcodecs.IMREAD_COLOR)
//        resize(mat, measuredHeight, measuredWidth)
        bitMat = mat
        Core.transpose(bitMat, bitMat)
        Core.flip(bitMat, bitMat, 0)
        Core.flip(bitMat, bitMat, 1)
        return showBitmap(bitMat)
    }

    fun transpose(): Bitmap? {
        Core.transpose(bitMat, bitMat)
        return showBitmap(bitMat)
    }

    fun x(): Bitmap? {
        Core.flip(bitMat, bitMat, 0)
        return showBitmap(bitMat)

    }

    fun y(): Bitmap? {
        Core.flip(bitMat, bitMat, 1)
        return showBitmap(bitMat)

    }

    fun detection(
        fileUri: String,
        measuredWidth: Int,
        measuredHeight: Int,
        isMatched: Boolean = false
    ): MutableList<FloatArray>? {
        checkMat(fileUri, measuredWidth, measuredHeight)
        val imageData = SeetaImageData(
            bitMat.width(),
            bitMat.height(),
            3
        )
        bitMat.get(0, 0, imageData.data)
        val faces = EngineHelper.faceDetector?.Detect(imageData)

        Log.i(TAG, "detection: ${faces?.size}")
        if (faces?.isNotEmpty() == true) {
            var floats: MutableList<FloatArray> = mutableListOf()
            //利用Bitmap创建Canvas，为了在图像上绘制人脸区域
            if (isMatched) {
                this.faceMatched = faces
                matMatched = bitMat.clone()
                Imgproc.cvtColor(matMatched, matMatched, Imgproc.COLOR_BGR2RGBA)
            }
            for (i in faces.indices) {
                val feats =
                    EngineHelper.faceRecognizer?.GetExtractFeatureSize()?.let { FloatArray(it) }
                //特征点检测
                val points = arrayOfNulls<SeetaPointF>(5)
                EngineHelper.faceLandmarker?.mark(imageData, faces[i], points)

                //特征提取
                EngineHelper.faceRecognizer?.Extract(
                    imageData,
                    points,
                    feats
                )
                if (feats != null) {
                    floats.add(feats)
                }
            }

            return floats
        }
        return null
    }

    fun faceMatch(floatMatcher: MutableList<FloatArray>, floatMatched: MutableList<FloatArray>): Bitmap? {
        for (matched in floatMatched.indices) {
            val sim = EngineHelper.faceRecognizer?.CalculateSimilarity(
                floatMatcher[0],
                floatMatched[matched]
            )
            if (sim != null) {
                if (sim > 0.7) {
                    return getFace(matched)
                }
            }
        }
        return null
    }

    fun bYJson(floatMatcher: FloatArray, floatMatched: MutableList<FloatArray>):Bitmap?{
        for (matched in floatMatched.indices) {
            val sim = EngineHelper.faceRecognizer?.CalculateSimilarity(
                floatMatcher,
                floatMatched[matched]
            )
            if (sim != null) {
                if (sim > 0.7) {
                    return getFace(matched)
                }
            }
        }
        return null
    }

    fun getFace(matched: Int): Bitmap? {
        val face = faceMatched[matched]
        val bitmap = showBitmap(matMatched)
        //利用Bitmap创建Canvas，为了在图像上绘制人脸区域
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.RED
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f

        //将匹配出来的人脸区域绘制出来
        val rect =
            Rect(face.x, face.y, face.x + face.width, face.y + face.height)
        canvas.drawRect(rect, paint)
        return bitmap
    }

    private fun resize(mat: Mat, measuredHeight: Int, measuredWidth: Int) {
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


    private fun showBitmap(mat: Mat): Bitmap? {
        val faceBmp = Bitmap.createBitmap(
            mat.width(), mat.height(),
            Bitmap.Config.ARGB_8888
        )

        try {
            Utils.matToBitmap(mat, faceBmp)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return faceBmp
    }

    /**
     * 函数的作用是将一个图像从一个颜色空间转换到另一个颜色空间，但是从RGB向其他类型转换时，
     * 必须明确指出图像的颜色通道，前面我们也提到过，在opencv中，其默认的颜色制式排列是BGR而非RGB。
     * 所以对于24位颜色图像来说，前8-bit是蓝色，中间8-bit是绿色，最后8-bit是红色。常见的R,G,B通道的取值范围为：
     * 0-255 :CV_8U类型图片
     * 0-65535: CV_16U类型图片
     * 0-1: CV_32F类型图片
     *
     * @param matNv21 输入图像即要进行颜色空间变换的原图像，可以是Mat类
     * @param matBgr 输出图像即进行颜色空间变换后存储图像，也可以Mat类
     * @param colorYuv2bgrNv21 转换的代码或标识，即在此确定将什么制式的图片转换成什么制式的图片，后面会详细将
     */
    fun cvtColor(matNv21: Mat, matBgr: Mat, colorYuv2bgrNv21: Int) {
        Imgproc.cvtColor(matNv21, matBgr, colorYuv2bgrNv21)
    }


}