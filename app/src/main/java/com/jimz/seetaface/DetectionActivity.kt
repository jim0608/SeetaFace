package com.jimz.seetaface

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.jimz.seetaface.databinding.ActivityDetectionBinding
import com.mysafe.lib_base.base.identify.FaceRectInfo
import com.mysafe.lib_identification.camera_view.FaceRectHelper
import com.mysafe.msmealorder_public.toasts
import com.seeta.sdk.seetafaceEngine.helper.DetectionFaceHelper
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy


/**
 * @author Create By 张晋铭
 * @Date on 2021/6/22
 * @Describe:
 */
class DetectionActivity : AppCompatActivity() {
    private val TAG = "TAG_DetectionActivity"
    private val REQUEST_CODE_CHOOSE_PHOTO_ALBUM = 1
    private var imgUrl: String? = null
    private lateinit var btn: Button
    private lateinit var dataBinding: ActivityDetectionBinding
    private var helper: DetectionFaceHelper? = null

    //    private val rectView = FaceRectView(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_detection)
        btn = findViewById(R.id.btn_detection)
        initView()
    }

    private fun initView() {
        if (helper == null) {
            helper = DetectionFaceHelper()
        }
        dataBinding.imageView.setOnClickListener {
            dataBinding.rectView.clearFaceRectList()
            openCamera()
        }

        btn.setOnClickListener {
            detection()
        }

        dataBinding.btnImage.setOnClickListener {
            val bitmap = helper?.checkMat(imgUrl!! ,
                dataBinding.imageView.measuredWidth,
                dataBinding.imageView.measuredHeight)
            if (bitmap != null) {
                showBit(bitmap)
            }
        }
        dataBinding.btnDetection.setOnClickListener {
            detection()
        }

        dataBinding.btnTranspose.setOnClickListener {
            val bitmap = helper?.transpose()
            if (bitmap != null) {
                showBit(bitmap)
            }
        }
        dataBinding.btnX.setOnClickListener {
            val bitmap = helper?.x()
            if (bitmap != null) {
                showBit(bitmap)
            }
        }
        dataBinding.btnY.setOnClickListener {
            val bitmap = helper?.y()
            if (bitmap != null) {
                showBit(bitmap)
            }
        }

    }

    private fun detection() {
        val list = helper?.detection()
        if (list?.isNotEmpty() == true){
            val rectHelper = getRectHelper(list[0])
            dataBinding.rectView.setFaceRectList(list, rectHelper)
        }else{
            toasts("人脸识别失败请调整人脸方向！")
        }
    }

    private fun getRectHelper(info: FaceRectInfo): FaceRectHelper {
        return FaceRectHelper(
            info.width,
            info.height,
            dataBinding.imageView.measuredWidth,
            dataBinding.imageView.measuredHeight,
            cameraId = 1
        )
    }

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
            for (_Uri in pathList) {

                println(pathList)
                imgUrl = _Uri
                Glide.with(this).load(imgUrl).into(dataBinding.imageView)
            }
        }
    }

    private fun showBit(bitmap:Bitmap) {
        Glide.with(this).load(bitmap).into(dataBinding.imgShow)
    }
}