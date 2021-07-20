package com.jimz.seetaface

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.jimz.seetaface.databinding.ActivityIdentifyBinding
import com.seeta.sdk.sqlite.FaceDatabase
import com.mysafe.lib_identification.camera_view.CameraCallback
import com.mysafe.lib_identification.camera_view.YUV420spAnalyzeBean
import com.mysafe.msmealorder_public.toasts
import com.seeta.sdk.seetafaceEngine.helper.IdentifyFaceHelper
import com.seeta.sdk.seetafaceEngine.helper.Register
import com.seeta.sdk.util.NV21ToBitmap
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy


/**
 * @author Create By 张晋铭
 * @Date on 2021/6/22
 * @Describe:
 */
class IdentifyFacesActivity : AppCompatActivity(), CameraCallback {
    private val TAG = "TAG_DetectionActivity"
    private lateinit var dataBinding: ActivityIdentifyBinding
    private var helper: IdentifyFaceHelper? = null
    private var isRegister = false
    private lateinit var dataBase: FaceDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_identify)
        initView()
        initLiveData()
    }

    private fun initView() {
        startFaceCamera()
        initDataBase()
        dataBinding.btnRegister.setOnClickListener {
            openCamera()
        }
    }

    private fun initLiveData() {
        helper?.liveData?.observe(this) {
            dataBinding.surfaceView.text = it
        }
        helper?.liveRegister?.observe(this) {
            isRegister = false
            Log.i(TAG, "registerFace: true")
        }
    }

    private fun initDataBase() {
        dataBase = FaceDatabase.getDatabase(this)
    }

    @SuppressLint("MissingPermission")
    private fun startFaceCamera() {
        val nv212bit = NV21ToBitmap(this)
        dataBinding.cameraView
            .setCameraSize(Size(480, 640))
            .setDestRotationDegrees(1)
            .isClipPicture(false)
            .setMirror(true)
            .setCameraId(1)
            .bindToLifecycle(this)
        dataBinding.cameraView.setCameraCallBack(this)
    }

    override fun cameraPreviewFrame(analyzeBean: YUV420spAnalyzeBean) {
        if (helper == null) {
            helper = IdentifyFaceHelper(
                analyzeBean.imgWidth,
                analyzeBean.imgHeight
            )
            helper?.setDataBase(dataBase)
        }
        if (isRegister) {

        } else {
            val list = helper?.detection(analyzeBean.nv21!!)
            runOnUiThread {
                if (list?.isNotEmpty() == true) {
                    dataBinding.cameraView.setFaceRectList(list)
                    dataBinding.cameraView.setAnalyzeWidth(list[0].width)
                    dataBinding.cameraView.setAnalyzeHeight(list[0].height)
                } else {
                    dataBinding.cameraView.clearFaceRectList()
                }
            }
        }
    }


    private val REQUEST_CODE_CHOOSE_PHOTO_ALBUM = 1
    private fun openCamera() {
        Matisse.from(this)
            .choose(MimeType.ofImage(), false)
            .capture(true)  // 使用相机，和 captureStrategy 一起使用
            .captureStrategy(CaptureStrategy(true, "com.jsf.piccompresstest"))
            .theme(R.style.Matisse_Dracula)
            .countable(true)
            .maxSelectable(1)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            .thumbnailScale(0.87f)
            .imageEngine(GlideLoadEngine())
            .forResult(REQUEST_CODE_CHOOSE_PHOTO_ALBUM)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode === REQUEST_CODE_CHOOSE_PHOTO_ALBUM && resultCode === RESULT_OK) {
            //图片路径 同样视频地址也是这个 根据requestCode
            val pathList: List<String> = Matisse.obtainPathResult(data)
            for (uri in pathList) {
                println(pathList)
                val view = dataBinding.cameraView
                helper?.registerFace(
                    uri,
                    object : Register {
                        override fun isSuccess() {
                            runOnUiThread { toasts("注册成功") }
                            Log.i(TAG, "registerFace: true")
                        }

                        override fun onFailed() {
                            runOnUiThread { toasts("注册失败") }
                        }
                    })
            }
        }
    }

}