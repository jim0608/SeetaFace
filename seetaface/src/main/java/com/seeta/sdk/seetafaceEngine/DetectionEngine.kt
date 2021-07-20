package com.jimz.seetaface.seetafaceEngine

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.seeta.sdk.*
import com.seeta.sdk.seetafaceEngine.EngineHelper
import com.seeta.sdk.util.FileUtils
import java.io.File

/**
 * @author Create By 张晋铭
 * @Date on 2021/6/25
 * @Describe:初始化识别引擎文件
 */
class DetectionEngine {
    private val TAG = "TAG_DetectionEngin"

    //人脸检测模型
    private var fdModel = "face_detector.csta"

    //人脸特征标记模型
    private var pdModel = "face_landmarker_pts5.csta"

    //人脸识别模型
    private var frModel = "face_recognizer.csta"

    //局部模型缓存
    private var fasModel1 = "fas_first.csta"

    //全局模型缓存
    private var fasModel2 = "fas_second.csta"

    private var faceDetector: FaceDetector? = null

    private var faceLandmarker: FaceLandmarker? = null

    private var faceRecognizer: FaceRecognizer? = null

    fun initPath(context: Context) {
        val cacheDir: File = getExternalCacheDirectory(context, null)
        val modelPath = cacheDir.absolutePath
        Log.d("cacheDir", "" + modelPath)

        if (!isExists(modelPath, fdModel)) {
            val fdFile = File("$cacheDir/$fdModel")
            FileUtils.copyFromAsset(context, fdModel, fdFile, false)
        }
        if (!isExists(modelPath, pdModel)) {
            val pdFile = File("$cacheDir/$pdModel")
            FileUtils.copyFromAsset(context, pdModel, pdFile, false)
        }
        if (!isExists(modelPath, frModel)) {
            val frFile = File("$cacheDir/$frModel")
            FileUtils.copyFromAsset(context, frModel, frFile, false)
        }
        initEngine(cacheDir)
    }

    private fun initEngine(cacheDir: File) {

        val rootPath = "$cacheDir/"
        try {
            if (faceDetector == null || faceLandmarker == null || faceRecognizer == null) {
                faceDetector = FaceDetector(
                    SeetaModelSetting(
                        0,
                        arrayOf(rootPath + fdModel),
                        SeetaDevice.SEETA_DEVICE_AUTO
                    )
                )
                faceLandmarker = FaceLandmarker(
                    SeetaModelSetting(
                        0,
                        arrayOf(rootPath + pdModel),
                        SeetaDevice.SEETA_DEVICE_AUTO
                    )
                )
                faceRecognizer = FaceRecognizer(
                    SeetaModelSetting(
                        0,
                        arrayOf(rootPath + frModel),
                        SeetaDevice.SEETA_DEVICE_AUTO
                    )
                )
            }
            faceDetector?.set(
                Property.PROPERTY_MIN_FACE_SIZE,
                100.0
            )
//            faceRecognizer?.set(Property.PROPERTY_NUMBER_THREADS,8.0)
            EngineHelper.faceDetector = faceDetector
            EngineHelper.faceLandmarker = faceLandmarker
            EngineHelper.faceRecognizer = faceRecognizer

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "init exception:$e")
        }
    }


    private fun getExternalCacheDirectory(context: Context, type: String?): File {
        var appCacheDir: File? = if (TextUtils.isEmpty(type)) {
            context.externalCacheDir // /sdcard/data/data/app_package_name/cache
        } else {
            File(context.filesDir, type) // /data/data/app_package_name/files/type
        }
        if (!appCacheDir!!.exists() && !appCacheDir.mkdirs()) {
            Log.e(
                "getInternalDirectory",
                "getInternalDirectory fail ,the reason is make directory fail !"
            )
        }
        return appCacheDir
    }

    private fun isExists(path: String, modelName: String): Boolean {
        val file = File("$path/$modelName")
        return file.exists()
    }

    companion object {
        init {
            System.loadLibrary("opencv_java3")
        }
    }
}