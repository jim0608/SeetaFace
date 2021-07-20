package com.mysafe.msmealorder_customer.activitys.camera.camera_view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import androidx.camera.core.*
import androidx.camera.core.impl.utils.CameraOrientationUtil
import androidx.camera.core.impl.utils.executor.CameraXExecutors
import androidx.camera.core.impl.utils.futures.FutureCallback
import androidx.camera.core.impl.utils.futures.Futures
import androidx.camera.view.PreviewView
import androidx.camera.view.PreviewView.StreamState
import androidx.lifecycle.*
import com.mysafe.lib_base.base.identify.FaceRectInfo
import com.mysafe.lib_identification.R
import com.mysafe.lib_identification.camera_view.CameraCallback
import com.mysafe.lib_identification.camera_view.FaceRectHelper
import com.mysafe.lib_identification.camera_view.FaceRectView


/**
 * @author Create By 张晋铭
 * @Date on 2021/2/3
 * @Describe:
 */
class FaceCameraView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val TAG = "TAG_FaceCameraView"
    private var mZoom: Float = 0f
    private lateinit var mCameraHelper: FaceCameraHelper
    private val ASPECT_RATIO_16_9 = Rational(16, 9)
    private val ASPECT_RATIO_4_3 = Rational(4, 3)
    private val ASPECT_RATIO_9_16 = Rational(9, 16)
    private val ASPECT_RATIO_3_4 = Rational(3, 4)

    /**
     * 相机待机时的图标显示
     */
    private var mIconImageView = ImageView(context)

    /**
     * 相机预览View
     */
    private var mPreviewView = PreviewView(context)

    /**
     * 人脸框View
     */
    private var mFaceRectView = FaceRectView(context)

    /**
     * 相机预览帧
     */
    private var cameAnalysis: CameraImageAnalyze? = null

    /**
     * 人脸框绘制坐标算法
     */
    private var rectHelper: FaceRectHelper? = null

    /**
     * lifecycle
     */
    private var mCurrentLifecycle: LifecycleOwner? = null

    /**
     * 相机是否需要重启
     */
    private var isStreaming = false


    private var isClip = true

    // For tap-to-focus
    private var mDownEventTimestamp: Long = 0

    //预览界面是否左右翻转
    private var isXMirror = false
    private var isMirrorHorizontal = false
    private var isYMirror = false

    // For accessibility event
    private var mUpEvent: MotionEvent? = null

    //是否自动分配camera分辨率
    private var isAutoSize = false

    private var cameraSize: Size? = null

    /**
     * lifecycle监听
     */
    private val mCurrentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        fun onStop(owner: LifecycleOwner) {
            if (owner === mCurrentLifecycle) {
                isStreaming = false
            }
        }
    }

    init {
        addView(mPreviewView, 0)
        addView(mFaceRectView, 1)

        initIconCamera()
        initCamera()
    }

    private fun initIconCamera() {
        val layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.gravity = Gravity.CENTER
        mIconImageView.layoutParams = layoutParams
    }

    private fun initCamera() {
        mCameraHelper = FaceCameraHelper(context, mPreviewView, this)
        mCameraHelper.startCamera()
    }

    private fun delta(): Long {
        return System.currentTimeMillis() - mDownEventTimestamp
    }

    @SuppressLint("MissingPermission")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Since bindToLifecycle will depend on the measured dimension, only call it when measured
        // dimension is not 0x0
        if (measuredWidth > 0 && measuredHeight > 0) {
            if (isAutoSize) {
                setCameraResolution()
            }
            mCameraHelper.bindToLifecycleAfterViewMeasured()
            if (isClip && cameAnalysis != null && width > 0 && height > 0) {
                cameAnalysis!!.setViewLayout(width, height)
                isClip = false
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    // TODO(b/124269166): Rethink how we can handle permissions here.
    @SuppressLint("MissingPermission")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        // In case that the CameraView size is always set as 0x0, we still need to trigger to force
        // binding to lifecycle
        mCameraHelper.bindToLifecycleAfterViewMeasured()
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mDownEventTimestamp = System.currentTimeMillis()
            MotionEvent.ACTION_UP -> if (delta() < ViewConfiguration.getLongPressTimeout()
                    && mCameraHelper.isBoundToLifecycle()) {
                mUpEvent = event
                performClick()
            }
            else ->                 // Unhandled event.
                return false
        }
        return true
    }


    /**
     * Focus the position of the touch event, or focus the center of the preview for
     * accessibility events
     */
    @SuppressLint("RestrictedApi")
    override fun performClick(): Boolean {
        super.performClick()
        val x = mUpEvent?.x ?: x + width / 2f
        val y = mUpEvent?.y ?: y + height / 2f
        mUpEvent = null

        val pointFactory = SurfaceOrientedMeteringPointFactory(width.toFloat(), height.toFloat())
        val afPointWidth = 1.0f / 6.0f // 1/6 total area
        val aePointWidth = afPointWidth * 1.5f
        val afPoint = pointFactory.createPoint(x, y, afPointWidth)
        val aePoint = pointFactory.createPoint(x, y, aePointWidth)
        val camera = mCameraHelper.getCamera()
        if (camera != null) {
            val future = camera.cameraControl.startFocusAndMetering(
                    FocusMeteringAction.Builder(afPoint,
                            FocusMeteringAction.FLAG_AF).addPoint(aePoint,
                            FocusMeteringAction.FLAG_AE).build())
            Futures.addCallback(future, object : FutureCallback<FocusMeteringResult?> {
                override fun onSuccess(result: FocusMeteringResult?) {}
                override fun onFailure(t: Throwable) {
                    // Throw the unexpected error.
//                    throw RuntimeException(t)
                }
            }, CameraXExecutors.directExecutor())
        } else {
            Log.d(TAG, "cannot access camera")
        }
        return true
    }


    /**
     * 初始化camera分辨率
     */
    private fun setCameraResolution() {
        val isDisplayPortrait = (getDisplayRotationDegrees() == 0 || getDisplayRotationDegrees() == 180)
//        val isDisplayPortrait = measuredWidth>measuredHeight
        val targetAspectRatio = if (isDisplayPortrait) ASPECT_RATIO_16_9 else ASPECT_RATIO_9_16

        val height = (measuredWidth / targetAspectRatio.toFloat()).toInt()
        cameraSize = Size(measuredWidth, height)
        mCameraHelper.setRotateSize(cameraSize!!)
    }

    /**
     * 设置Camera分辨率
     */
    fun setCameraSize(size: Size): FaceCameraView {
        mCameraHelper.setRotateSize(size)
        return this
    }

    /**
     * 设置Camera焦距
     */
    fun setZoom(mZoom: Float): FaceCameraView {
        this.mZoom = mZoom
        return this
    }

    /**
     * 设置相机旋转
     * @param mDestRotationDegrees 旋转角度范围：0，1，2，3
     */
    fun setDestRotationDegrees(mDestRotationDegrees:Int): FaceCameraView {
        mCameraHelper.setDestRotationDegrees(mDestRotationDegrees)
        return this
    }

    /**
     * 是否根据预览界面大小自动分配分辨率
     */
    fun isAutoCameraSize(isAutoSize: Boolean = true): FaceCameraView {
        this.isAutoSize = isAutoSize
        return this
    }

    /**
     * 生命周期关联
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        mCameraHelper.bindToLifecycle(lifecycleOwner)
        if (mCurrentLifecycle == null) {
            setPreviewStreamState(lifecycleOwner)
        }
    }

    /**
     * 取消生命周期关联
     */
    fun unbindToLifecycle() {
        isStreaming = false
        mCameraHelper.clearCurrentLifecycle()
    }

    /**
     * camera每一帧画面 转换未nv21格式 返回数据
     * 初始化相机预览画面
     */
    fun setCameraCallBack(cameraCallback: CameraCallback) {
        if (cameAnalysis == null) {
            cameAnalysis = CameraImageAnalyze(cameraCallback)
        }
        mCameraHelper.setImageAnalysis(cameAnalysis!!)
    }

    /**
     * 手动切换摄像头
     */
    fun setCameraLensFacing(lensFacing: Int?) {
        mCameraHelper.setCameraSelectByLensFacing(lensFacing)
    }

    /**
     * 设置CameraId
     */
    fun setCameraId(cameraId: Int): FaceCameraView {
        mCameraHelper.setCameraSelectByCameraId(cameraId)
        return this
    }

    /**
     * 是否根据预览画面大小剪切
     */
    fun isClipPicture(isClip: Boolean = false): FaceCameraView {
        this.isClip = isClip
        return this
    }

    /**
     * 获取拍照实例
     */
    fun takePicture(): ImageCapture? {
        return mCameraHelper.getImageCapture()
    }

    /**
     * @param isXMirror 水平方向翻转
     * @param isYMirror 垂直方向翻转
     */
    fun setMirror(isXMirror: Boolean = false, isYMirror: Boolean = false): FaceCameraView {
        this.isXMirror = isXMirror
        this.isYMirror = isYMirror
        rectHelper?.isMirror = isXMirror
        rectHelper?.isMirrorVertical = isYMirror

        if (isXMirror) {
            scaleX = -1f
        }
        if (isYMirror) {
            scaleY = -1f
        }
        return this
    }

    fun setMirrorHorizontal(isMirrorHorizontal: Boolean = false): FaceCameraView {
        this.isMirrorHorizontal = isMirrorHorizontal
        rectHelper?.isMirrorHorizontal = isXMirror
        return this
    }

    /**
     * 获取设备是否旋转
     */
    fun getDisplaySurfaceRotation(): Int {
        val display = display ?: return 0
        return display.rotation
    }

    @SuppressLint("RestrictedApi")
    fun getDisplayRotationDegrees(): Int {
        return CameraOrientationUtil.surfaceRotationToDegrees(getDisplaySurfaceRotation())
    }

    /**
     * 相机是否启动状态监听
     */
    @SuppressLint("RestrictedApi")
    fun setPreviewStreamState(lifecycleOwner: LifecycleOwner) {
        this.mCurrentLifecycle = lifecycleOwner
        this.mCurrentLifecycle?.lifecycle?.addObserver(mCurrentLifecycleObserver)
        mIconImageView.setImageResource(R.drawable.ic_baseline_photo_camera)
        mPreviewView.previewStreamState.observe(lifecycleOwner) {
            when (it) {
                StreamState.IDLE -> {
                    Log.i(TAG, "setPreviewStreamState: StreamState.IDLE $childCount")
                    if (childCount < 3) {
                        addView(mIconImageView, 2)
                    }

                    Log.i(TAG, "setPreviewStreamState: StreamState.IDLE$isStreaming")
                    if (isStreaming) {
                        isStreaming = false
                        mCameraHelper.bindToLifecycle(lifecycleOwner)
                    }
                }
                StreamState.STREAMING -> {
                    Log.i(TAG, "setPreviewStreamState: StreamState.STREAMING$childCount")
                    isStreaming = true
                    removeViewAt(2)
                    mCameraHelper.setZoom(mZoom)
                    if (rectHelper == null) {
                        val previewWidth = mCameraHelper.getPreview()!!.attachedSurfaceResolution!!.width
                        val previewHeight = mCameraHelper.getPreview()!!.attachedSurfaceResolution!!.height
                        rectHelper = FaceRectHelper(
                                previewWidth, previewHeight,
                                measuredWidth, measuredHeight,
                                getDisplayRotationDegrees(), mCameraHelper.getLensFacing(),
                                isXMirror, isMirrorHorizontal, isYMirror)
                    }
                }
            }
        }
    }


    /**
     * previewStreamState实例获取，监听相机是否启动
     */
    fun getPreviewStreamState(): LiveData<StreamState?> {
        return mPreviewView.previewStreamState
    }

    /**
     * 设置人来能识别人脸框
     */
    fun setFaceRectList(list: MutableList<FaceRectInfo>?) {
//        Log.i(TAG, "setFaceRectList: 人脸数${list?.size}")
        if (rectHelper != null && list != null)
            mFaceRectView.setFaceRectList(list, rectHelper)
    }

    /**
     * 设置预览帧旋转角度
     */
    fun setAnalyzeRotate(rotate: Int) {
        rectHelper?.cameraDisplayOrientation = rotate
    }

    /**
     * 设置预览帧旋转角度
     */
    fun setAnalyzeWidth(width: Int) {
        rectHelper?.previewWidth = width
    }

    /**
     * 设置预览帧旋转角度
     */
    fun setAnalyzeHeight(height: Int) {
        rectHelper?.previewHeight = height
    }

    /**
     * 清除人脸框
     */
    fun clearFaceRectList() {
        mFaceRectView.clearFaceRectList()
    }


}