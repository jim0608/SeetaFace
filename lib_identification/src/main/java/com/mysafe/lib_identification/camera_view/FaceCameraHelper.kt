package com.mysafe.msmealorder_customer.activitys.camera.camera_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.core.CameraSelector.LensFacing
import androidx.camera.core.impl.LensFacingConverter
import androidx.camera.core.impl.utils.executor.CameraXExecutors
import androidx.camera.core.impl.utils.futures.FutureCallback
import androidx.camera.core.impl.utils.futures.Futures
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.util.Preconditions
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.mysafe.lib_identification.camera_view.CameraIdFilter
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * @author Create By 张晋铭
 * @Date on 2021/2/3
 * @Describe:
 */
class FaceCameraHelper(
        private val mContext: Context,
        private val mPreviewView: PreviewView,
        private val mCameraView: FaceCameraView,
) {
    private val TAG = "TAG_FaceCameraHelper"


    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()


    private var mPreviewBuilder: Preview.Builder? = null
    private var mImageCaptureBuilder: ImageCapture.Builder? = null
    private var mImageAnalyzerBuilder: ImageAnalysis.Builder? = null

    private var mCurrentLifecycle: LifecycleOwner? = null
    private var mNewLifecycle: LifecycleOwner? = null
    private var mCameraLensFacing: Int? = CameraSelector.LENS_FACING_BACK
    private var mCameraId: Int? = null
    private var mCameraSelector: CameraSelector? = null
    private var mCameraProvider: ProcessCameraProvider? = null

    private var mCamera: Camera? = null
    private var mPreview: Preview? = null
    private var mImageCapture: ImageCapture? = null
    private var mImageAnalyzer: ImageAnalysis? = null
    private var cameraAnalysis: ImageAnalysis.Analyzer? = null

    private var mSize: Size? = null
    private var mZoom: Float = 0f
    //mDestRotationDegrees 范围0，1，2，3
    private var mDestRotationDegrees = 0

    private val mCurrentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun onDestroy(owner: LifecycleOwner) {
            if (owner === mCurrentLifecycle) {
                clearCurrentLifecycle()
            }
        }
    }


    @SuppressLint("RestrictedApi")
    fun startCamera() {
        Futures.addCallback(ProcessCameraProvider.getInstance(mContext),
                object : FutureCallback<ProcessCameraProvider?> {
                    // TODO(b/124269166): Rethink how we can handle permissions here.
                    @SuppressLint("MissingPermission")
                    override fun onSuccess(provider: ProcessCameraProvider?) {
                        Preconditions.checkNotNull(provider)
                        mCameraProvider = provider
                        if (mCurrentLifecycle != null) {
                            Log.i(TAG, "onSuccess: ")
                            bindToLifecycle(mCurrentLifecycle!!)
                        }
                    }

                    override fun onFailure(t: Throwable) {
                        throw RuntimeException("CameraX failed to initialize.", t)
                    }
                }, CameraXExecutors.mainThreadExecutor())

        mPreviewBuilder = Preview.Builder().setTargetName("Preview")
        mImageCaptureBuilder = ImageCapture.Builder().setTargetName("ImageCapture")
        mImageAnalyzerBuilder = ImageAnalysis.Builder().setTargetName("ImageAnalyzer")
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner) {
        mNewLifecycle = lifecycleOwner
        if (getMeasuredWidth() > 0 && getMeasuredHeight() > 0) {
            bindToLifecycleAfterViewMeasured()
        }
    }

    @SuppressLint("UnsafeExperimentalUsageError", "WrongConstant", "UnsafeOptInUsageError")
    fun bindToLifecycleAfterViewMeasured() {
        if (mNewLifecycle == null) {
            return
        }
        clearCurrentLifecycle()
        if (mNewLifecycle!!.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            mNewLifecycle = null
            return
        }
        mCurrentLifecycle = mNewLifecycle
        mNewLifecycle = null

        if (mCameraProvider == null) {
            return
        }

        if (mCameraId == null) {
            getCameraSelectByLensFacing()
        } else {
            getCameraSelectById()
        }
        if (mCameraSelector == null) {
            Log.e(TAG, "Please input mCameraSelector")
            return
        }
        // Set the preferred aspect ratio as 4:3 if it is IMAGE only mode. Set the preferred aspect
        // ratio as 16:9 if it is VIDEO or MIXED mode. Then, it will be WYSIWYG when the view finder
        // is in CENTER_INSIDE mode.

        mImageCaptureBuilder!!.setTargetRotation(getDisplayRotationDegrees())
        mSize?.let { it1 -> mImageCaptureBuilder!!.setTargetResolution(it1) }
        if (mImageCapture == null) {
            mImageCapture = mImageCaptureBuilder!!.build()
        }


        mImageAnalyzerBuilder!!.let {
            mSize?.let { it1 -> it.setTargetResolution(it1) }
            it.setTargetRotation(getDisplayRotationDegrees())
            it.setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }
        if (mImageAnalyzer == null) {
            mImageAnalyzer = mImageAnalyzerBuilder!!.build()
            cameraAnalysis?.let { mImageAnalyzer!!.setAnalyzer(cameraExecutor, it) }
        }


        // Adjusts the preview resolution according to the view size and the target aspect ratio.
        mSize?.let { mPreviewBuilder!!.setTargetResolution(it) }
        if (mPreview == null) {
            mPreview = mPreviewBuilder!!.build()
        }
        mPreview?.setSurfaceProvider(mPreviewView.surfaceProvider)
        mPreview?.targetRotation = getDisplayRotationDegrees()
        mPreviewView.scaleType = PreviewView.ScaleType.FILL_CENTER


        mCamera = mCameraProvider!!.bindToLifecycle(mCurrentLifecycle!!, mCameraSelector!!,
                mImageCapture,
                mImageAnalyzer,
                mPreview)
        mCamera?.cameraControl?.setLinearZoom(mZoom)
        mCurrentLifecycle!!.lifecycle.addObserver(mCurrentLifecycleObserver)
    }

    fun clearCurrentLifecycle() {
        if (mCurrentLifecycle != null && mCameraProvider != null) {
            // Remove previous use cases
            val toUnbind: MutableList<UseCase> = ArrayList()
//            if (mImageCapture != null && mCameraProvider!!.isBound(mImageCapture!!)) {
//                toUnbind.add(mImageCapture!!)
//            }
            if (mImageAnalyzer != null && mCameraProvider!!.isBound(mImageAnalyzer!!)) {
                toUnbind.add(mImageAnalyzer!!)
            }
            if (mPreview != null && mCameraProvider!!.isBound(mPreview!!)) {
                toUnbind.add(mPreview!!)
            }
            for (bind in toUnbind) {
                mCameraProvider!!.unbind(bind)
            }

            // Remove surface provider once unbound.
            if (mPreview != null) {
                mPreview!!.setSurfaceProvider(null)
            }
        }
        mCamera = null
        mCurrentLifecycle = null
    }

    @SuppressLint("MissingPermission")
    private fun getCameraSelectByLensFacing() {
        val available: Set<Int> = getAvailableCameraLensFacing()
        if (available.isEmpty()) {
            Log.w(TAG, "Unable to bindToLifeCycle since no cameras available")
            mCameraLensFacing = null
        }

        // Ensure the current camera exists, or default to another camera
        if (mCameraSelector != null && !available.contains(mCameraLensFacing)) {
            Log.w(TAG, "Camera does not exist with direction $mCameraLensFacing")

            // Default to the first available camera direction
            mCameraLensFacing = available.iterator().next()
            Log.w(TAG, "Defaulting to primary camera with direction $mCameraLensFacing")
        }
        mCameraSelector = CameraSelector.Builder().requireLensFacing(mCameraLensFacing!!).build()
    }

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun getCameraSelectById() {
        mCameraSelector = CameraSelector.Builder().addCameraFilter(CameraIdFilter(mCameraId!!)).build()
    }

    @SuppressLint("RestrictedApi")

    private fun getAvailableCameraLensFacing(): Set<Int> {
        // Start with all camera directions
        val available: MutableSet<Int> = LinkedHashSet(listOf(*LensFacingConverter.values()))

        // If we're bound to a lifecycle, remove unavailable cameras
        if (mCurrentLifecycle != null) {
            if (!hasCameraWithLensFacing(CameraSelector.LENS_FACING_BACK)) {
                available.remove(CameraSelector.LENS_FACING_BACK)
            }
            if (!hasCameraWithLensFacing(CameraSelector.LENS_FACING_FRONT)) {
                available.remove(CameraSelector.LENS_FACING_FRONT)
            }
        }
        return available
    }


    fun hasCameraWithLensFacing(@LensFacing lensFacing: Int): Boolean {
        return if (mCameraProvider == null) {
            false
        } else try {
            mCameraProvider!!.hasCamera(CameraSelector.Builder().requireLensFacing(lensFacing).build())
        } catch (e: CameraInfoUnavailableException) {
            false
        }
    }

    /**
     * 在当前摄像头基础上切换到另一个摄像头
     */
    fun toggleCamera() {
        // TODO(b/124269166): Rethink how we can handle permissions here.
        @SuppressLint("MissingPermission") val availableCameraLensFacing = getAvailableCameraLensFacing()
        if (availableCameraLensFacing.isEmpty()) {
            return
        }
        if (mCameraLensFacing == null) {
            setCameraSelectByLensFacing(availableCameraLensFacing.iterator().next())
            return
        }
        if (mCameraLensFacing == CameraSelector.LENS_FACING_BACK
                && availableCameraLensFacing.contains(CameraSelector.LENS_FACING_FRONT)) {
            setCameraSelectByLensFacing(CameraSelector.LENS_FACING_FRONT)
            return
        }
        if (mCameraLensFacing == CameraSelector.LENS_FACING_FRONT
                && availableCameraLensFacing.contains(CameraSelector.LENS_FACING_BACK)) {
            setCameraSelectByLensFacing(CameraSelector.LENS_FACING_BACK)
            return
        }
    }

    /**
     * 切换到指定摄像头
     */
    @SuppressLint("MissingPermission")
    fun setCameraSelectByLensFacing(lensFacing: Int?) {
        // Setting same lens facing is a no-op, so check for that first
        if (mCameraLensFacing != lensFacing) {
            // If we're not bound to a lifecycle, just update the camera that will be opened when we
            // attach to a lifecycle.
            mCameraLensFacing = lensFacing
            if (mCurrentLifecycle != null) {
                // Re-bind to lifecycle with new camera
                bindToLifecycle(mCurrentLifecycle!!)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun setCameraSelectByCameraId(mCameraId: Int) {
        if (this.mCameraId != mCameraId) {
            this.mCameraId = mCameraId
            if (mCurrentLifecycle != null) {
                // Re-bind to lifecycle with new camera
                bindToLifecycle(mCurrentLifecycle!!)
            }
        }
    }

    @SuppressLint("RestrictedApi", "WrongConstant")
    fun getDisplayRotationDegrees(): Int {
        var destRotationDegrees = getDisplaySurfaceRotation()
        if (destRotationDegrees > 2) {
            destRotationDegrees = 0
        } else {
            destRotationDegrees += mDestRotationDegrees
        }
        return destRotationDegrees
    }

    fun isBoundToLifecycle(): Boolean {
        return mCamera != null
    }

    fun getLensFacing(): Int {
        return mCameraLensFacing ?: 0
    }

    fun setRotateSize(mSize: Size) {
        if (this.mSize == null) {
            this.mSize = mSize
        }
    }

    fun setZoom(mZoom: Float) {
        this.mZoom = mZoom
        mCamera?.cameraControl?.setLinearZoom(mZoom)
    }

    fun getCamera(): Camera? {
        return mCamera
    }

    fun getPreview(): Preview? {
        return mPreview
    }

    fun getImageCapture(): ImageCapture? {
        return mImageCapture
    }

    private fun getDisplaySurfaceRotation(): Int {
        return mCameraView.getDisplaySurfaceRotation()
    }

    private fun getMeasuredWidth(): Int {
        return mCameraView.measuredWidth
    }

    private fun getMeasuredHeight(): Int {
        return mCameraView.measuredHeight
    }

    fun setImageAnalysis(cameraAnalysis: CameraImageAnalyze) {
        this.cameraAnalysis = cameraAnalysis
    }

    fun setDestRotationDegrees(mDestRotationDegrees:Int){
        this.mDestRotationDegrees = mDestRotationDegrees
    }

}