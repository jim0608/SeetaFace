package com.seeta.sdk.seetafaceEngine.helper

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.mysafe.lib_base.base.identify.FaceRectInfo
import com.seeta.sdk.SeetaImageData
import com.seeta.sdk.SeetaPointF
import com.seeta.sdk.SeetaRect
import com.seeta.sdk.seetafaceEngine.EngineHelper
import com.seeta.sdk.sqlite.FaceDatabase
import com.seeta.sdk.sqlite.entity.FaceEntity
import kotlinx.coroutines.*
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * @author Create By 张晋铭
 * @date on 2021/6/28
 * @describe
 */
class IdentifyFaceHelper(private val width: Int, private val height: Int) {
    private lateinit var dataBase: FaceDatabase
    private val TAG = "TAG_DertectionActivityHelper"
    private var faceInfo: MutableList<FaceRectInfo> = mutableListOf()
    private var faceRectInfo = FaceRectInfo()
    private val matNv21 = Mat(height * 3 / 2, width, CvType.CV_8UC1)
    private var matBgr = Mat(height, width, CvType.CV_8UC3)
    private var matRegister = Mat(height, width, CvType.CV_8UC3)
    private var imageData: SeetaImageData? = null
    private var faceBmp: Bitmap? = null


    /**
     * 特征提取线程池
     */
    private var frExecutor: ExecutorService? = null

    /**
     * 特征提取线程队列
     */
    private var frThreadQueue: LinkedBlockingQueue<Runnable>? = null
    val liveData: MutableLiveData<String> = MutableLiveData()
    val liveRegister: MutableLiveData<Boolean> = MutableLiveData()
    private var successTime: Long = 0
    var isSuccess = false

    fun setDataBase(dataBase: FaceDatabase) {
        this.dataBase = dataBase
        frThreadQueue = LinkedBlockingQueue<Runnable>(1)
        frExecutor = ThreadPoolExecutor(
            1, 1, 0, TimeUnit.MILLISECONDS, frThreadQueue
        ) { r: Runnable? ->
            val t = Thread(r)
            t.name = "frThread-" + t.id
            t
        }
    }

    private fun getRegisterBit(): Bitmap? {
        return showBitmap(matRegister)
    }

    //人脸注册
    fun registerFace(fileUri: String, param: Register) {
        matRegister = Imgcodecs.imread(fileUri, Imgcodecs.IMREAD_COLOR)
        Util.resize(matRegister, height, width)


        Core.transpose(matRegister, matRegister)
        Core.flip(matRegister, matRegister, 0)
        Core.flip(matRegister, matRegister, 1)

        matRegister.get(0, 0, imageData?.data)
        val faces = EngineHelper.faceDetector?.Detect(imageData)


        Log.i(TAG, "detection: ${faces?.size}")
        if (faces?.isNotEmpty() == true) {
            for (i in faces.indices) {
                //特征点检测
                Log.d(TAG, "registerFace线程1：${Thread.currentThread().name}")
                Log.d(TAG, "registerFace线程2：${Thread.currentThread().name}")
                GlobalScope.launch (Dispatchers.IO){
                    synchronized(EngineHelper.faceRecognizer!!) {
                        Log.i(TAG, "detection1: ${System.currentTimeMillis()}")
                        val feats = getFeatures(faces[i])
                        if (feats != null) {
                            val bytes = Util.floatArrayToByteArray(feats)
                            val face = FaceEntity(
                                "name_",
                                bytes, 123
                            )
                            dataBase.faceDao().insert(face)
                            Log.i(TAG, "registerFace: 注册地成功")
                            param.isSuccess()
                        }
                    }
                }
            }
        } else {
            param.onFailed()
        }
    }

    fun checkMat(data: ByteArray, mat: Mat): Mat {
        matNv21.put(0, 0, data)
        Imgproc.cvtColor(matNv21, mat, Imgproc.COLOR_YUV2BGR_NV21)
        return mat
    }

    //人脸识别
    fun detection(data: ByteArray): MutableList<FaceRectInfo>? {
        //width == 1024 height == 768
        matBgr = checkMat(data, matBgr)
        Core.transpose(matBgr, matBgr)
        Core.flip(matBgr, matBgr, 0)
        Core.flip(matBgr, matBgr, 1)

        //width == 768 height == 1024
        if (imageData == null) {
            imageData = SeetaImageData(matBgr.width(), matBgr.height(), 3)
        }

        matBgr.get(0, 0, imageData?.data)
        val faces = EngineHelper.faceDetector?.Detect(imageData)

//        Log.i(TAG, "detection: ${faces?.size}")
        if (faces?.isNotEmpty() == true) {
            faceInfo.clear()
            val maxFace = keepMaxFace(faces)
            val faceRectInfo = FaceRectInfo()

            faceRectInfo.width = matBgr.width()
            faceRectInfo.height = matBgr.height()
            faceRectInfo.rect?.left = maxFace.x
            faceRectInfo.rect?.top = maxFace.y
            faceRectInfo.rect?.right = maxFace.x + maxFace.width
            faceRectInfo.rect?.bottom = maxFace.y + maxFace.height

            if (isSuccess) {
                faceRectInfo.name = "userName"
                faceRectInfo.color = Color.GREEN
                //识别成功20s后，重置状态
                val time = System.currentTimeMillis() - successTime
                if (time > 5000) {
                    isSuccess = false
                }
            } else {
                if (getFaces()!!.size > 0 && frThreadQueue!!.remainingCapacity() > 0) {
                    frExecutor!!.execute(FaceRecognizeRunnable(maxFace))
                }
                faceRectInfo.color = Color.RED
            }
            faceInfo.add(faceRectInfo)
            return faceInfo
        }
        isSuccess = false
        return null
    }

    //获取特征值
    private fun getFeatures(maxFace: SeetaRect): FloatArray? {
        Log.i(TAG, "detection___1: ${System.currentTimeMillis()}")

        val image = EngineHelper.faceRecognizer?.let {
            SeetaImageData(
                it.GetCropFaceWidth(),
                it.GetCropFaceHeight(),
                it.GetCropFaceChannels()
            )
        }

        val feats = EngineHelper.faceRecognizer?.GetExtractFeatureSize()?.let { FloatArray(it) }
        //特征点检测
        val points = arrayOfNulls<SeetaPointF>(5)
        EngineHelper.faceLandmarker?.mark(imageData, maxFace, points)
        Log.i(TAG, "detection___2: ${System.currentTimeMillis()}")
        EngineHelper.faceRecognizer?.CropFace(imageData, points, image)
        Log.i(TAG, "detection___3: ${System.currentTimeMillis()}")
        //特征提取
//        EngineHelper.faceRecognizer?.Extract(imageData, points, feats)
        val isExtract = EngineHelper.faceRecognizer?.ExtractCroppedFace(image, feats)
        Log.i(TAG, "detection___4: ${System.currentTimeMillis()}")

        if (feats != null && isExtract == true) {
            return feats
        }
        return null
    }

    private fun keepMaxFace(faces: Array<SeetaRect>): SeetaRect {
        var maxFaceInfo = faces[0]
        for (face in faces) {
            if (face.width > maxFaceInfo.width) {
                maxFaceInfo = face
            }
        }
        return maxFaceInfo
    }

    private fun faceMatch(floatMatcher: FloatArray): Boolean {
        for (face in getFaces()!!) {
            val fedata = Util.byteArrayToFloatArray(face.featureData)
            val sim = EngineHelper.faceRecognizer?.CalculateSimilarity(
                floatMatcher,
                fedata
            )
            if (sim != null) {
                Log.i(TAG, "faceMatch: $sim")
                if (sim > 0.7) {
                    return true
                }
            }
        }
        return false
    }

    private fun getFaces() = dataBase.faceDao().allFaces


    private fun showBitmap(mat: Mat): Bitmap? {
        if (faceBmp == null) {
            faceBmp = Bitmap.createBitmap(
                mat.width(),
                mat.height(),
                Bitmap.Config.ARGB_8888
            )
        }

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


    inner class FaceRecognizeRunnable(val maxFace: SeetaRect) : Runnable {
        override fun run() {
            synchronized(EngineHelper.faceRecognizer!!) {
                Log.i(TAG, "detection1: ${System.currentTimeMillis()}")
                val fea = getFeatures(maxFace)
                isSuccess = faceMatch(fea!!)
                Log.i(TAG, "detection2: ${System.currentTimeMillis()}")
                if (isSuccess) {
                    liveData.postValue("识别成功")
                    successTime = System.currentTimeMillis()
                } else {
                    liveData.postValue("识别失败")
                }
            }
        }
    }
}