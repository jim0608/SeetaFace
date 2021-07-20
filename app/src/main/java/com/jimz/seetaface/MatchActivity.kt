package com.jimz.seetaface

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.jimz.seetaface.databinding.ActivityMatchBinding
import com.seeta.sdk.seetafaceEngine.helper.MatchFaceHelper
import com.seeta.sdk.seetafaceEngine.helper.Util
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.internal.entity.CaptureStrategy


/**
 * @author Create By 张晋铭
 * @Date on 2021/6/22
 * @Describe:
 */
class MatchActivity : AppCompatActivity() {
    private val TAG = "TAG_DetectionActivity"
    private val REQUEST_CODE_CHOOSE_PHOTO_ALBUM = 1
    private var imgUrl: String? = null
    private lateinit var dataBinding: ActivityMatchBinding
    private var helper: MatchFaceHelper? = null
    private var floatMatched:MutableList<FloatArray>? = arrayListOf()
    private var floatMatcher:MutableList<FloatArray>? = arrayListOf()
    private var isMatcher = false
    private var isMatched = false

    //    private val rectView = FaceRectView(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_match)
        initView()
    }

    private fun initView() {
        if (helper == null) {
            helper = MatchFaceHelper()
        }
        dataBinding.imgMatcher.setOnClickListener {
            isMatcher = true
            isMatched = false
            openCamera()
        }
        dataBinding.imgMatched.setOnClickListener {
            isMatched = true
            isMatcher = false
            openCamera()
        }

        dataBinding.btnDetection.setOnClickListener {
            detection()
        }
    }

    private fun detection() {


        val asd= getjsonByte()
//        val bit = helper?.faceMatch(floatMatcher!!,floatMatched!!)
        val bit = helper?.bYJson(asd!!,floatMatched!!)
        Glide.with(this).load(bit).into(dataBinding.imgMatched)
    }

    fun getjson(): FloatArray? {
        try {
            val iStream = assets.open("tzz.json")
            val size: Int = iStream.available()
            val buffer = ByteArray(size)
            iStream.read(buffer)
            iStream.close()
            val json = String(buffer)
            return Gson().fromJson(json,FloatArray::class.java)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }

    fun getjsonByte(): FloatArray? {
        try {
            val iStream = assets.open("byteStr.json")
            val size: Int = iStream.available()
            val buffer = ByteArray(size)
            iStream.read(buffer)
            iStream.close()
            val json = String(buffer)

            val asd = Util.byteArrayToFloatArray( json.toByteArray())

            return asd
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
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
                if (isMatched){
                    isMatched = false
                    floatMatched?.clear()
                    dataBinding.imgMatched.let{
                        Glide.with(this).load(_Uri).into(it)
                        floatMatched = helper?.detection(_Uri,it.measuredWidth,it.measuredHeight,true)!!
                    }

                }else if (isMatcher){
                    floatMatcher?.clear()
                    isMatcher = false
                    dataBinding.imgMatcher.let{
                        Glide.with(this).load(_Uri).into(it)
                        floatMatcher = helper?.detection(_Uri,it.measuredWidth,it.measuredHeight)!!
                    }
                }
            }
        }
    }

    private fun showBit(bitmap:Bitmap) {
        Glide.with(this).load(bitmap).into(dataBinding.imgMatched)
    }
}