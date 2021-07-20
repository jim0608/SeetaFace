package com.mysafe.lib_identification.camera_view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.mysafe.lib_identification.camera_view.FaceRectHelper
import com.mysafe.lib_base.base.identify.FaceRectInfo

/**
 * @author Create By 张晋铭
 * @Date on 2021/2/3
 * @Describe:
 */
class FaceRectView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = "TAG_FaceRectView"
    private var paint: Paint
    private val mPath = Path()
    private var faceRectList: MutableList<FaceRectInfo> = ArrayList()
    private var rectHelper: FaceRectHelper? = null

    init {
        paint = Paint()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (faceRect in faceRectList) {
            drawFaceRect(canvas, faceRect)
        }
        if (faceRectList.size == 0){
            canvas?.drawPath(mPath, paint)
        }
    }

    fun setFaceRectList(faceRectInfo: MutableList<FaceRectInfo>?, rectHelper: FaceRectHelper?) {
        this.rectHelper = rectHelper
        faceRectList.clear()
        mPath.reset()
        if (faceRectInfo != null && faceRectInfo.size > 0) {
            faceRectList.addAll(faceRectInfo)
        }

        postInvalidate()
    }

    fun clearFaceRectList(){
        faceRectList.clear()
        mPath.reset()
        postInvalidate()
    }


    private fun drawFaceRect(canvas: Canvas?, faceRect: FaceRectInfo) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5.toFloat()
        paint.color = faceRect.color
        paint.isAntiAlias = true
        if (rectHelper==null){
            Log.i(TAG, "drawFaceRect: rectHelper is null")
            return
        }
        val rect = rectHelper!!.adjustRect(faceRect.rect)
//        val rect = faceRect.rect!!

//        Log.i(TAG, "drawFaceRect:left: ${rect.left} right: ${rect.right} top: ${rect.top} bottom$${rect.bottom}")
        // 左上
        mPath.moveTo(rect.left.toFloat(), (rect.top + rect.height() / 4).toFloat())
        mPath.lineTo(rect.left.toFloat(), rect.top.toFloat())
        mPath.lineTo((rect.left + rect.width() / 4).toFloat(), rect.top.toFloat())
        // 右上
        mPath.moveTo((rect.right - rect.width() / 4).toFloat(), rect.top.toFloat())
        mPath.lineTo(rect.right.toFloat(), rect.top.toFloat())
        mPath.lineTo(rect.right.toFloat(), (rect.top + rect.height() / 4).toFloat())
        // 右下
        mPath.moveTo(rect.right.toFloat(), (rect.bottom - rect.height() / 4).toFloat())
        mPath.lineTo(rect.right.toFloat(), rect.bottom.toFloat())
        mPath.lineTo((rect.right - rect.width() / 4).toFloat(), rect.bottom.toFloat())
        // 左下
        mPath.moveTo((rect.left + rect.width() / 4).toFloat(), rect.bottom.toFloat())
        mPath.lineTo(rect.left.toFloat(), rect.bottom.toFloat())
        mPath.lineTo(rect.left.toFloat(), (rect.bottom - rect.height() / 4).toFloat())
        canvas?.drawPath(mPath, paint)
    }
}