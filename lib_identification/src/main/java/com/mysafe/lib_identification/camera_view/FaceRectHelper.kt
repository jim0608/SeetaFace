package com.mysafe.lib_identification.camera_view

import android.graphics.Rect
import androidx.camera.core.CameraSelector

/**
 * @author Create By 张晋铭
 * @Date on 2021/3/4
 * @Describe:将检测回传的人脸框（基于NV21数据）转换为View绘制（基于View）所需的人脸框
 * 创建一个绘制辅助类对象，并且设置绘制相关的参数
 *
 * @param previewWidth             预览宽度(当前您截取nv21后的宽度)
 * @param previewHeight            预览高度(当前您截取nv21后的高度)
 * @param canvasWidth              绘制控件的宽度（空间的实际宽高）
 * @param canvasHeight             绘制控件的高度
 * @param cameraDisplayOrientation 旋转角度
 * @param cameraId                 相机ID
 * @param isMirror                 是否水平镜像显示（若相机是镜像显示的，设为true，用于纠正）
 * @param isMirrorHorizontal         为兼容部分设备使用，水平再次镜像
 * @param isMirrorVertical           为兼容部分设备使用，垂直再次镜像
 */
class FaceRectHelper(var previewWidth: Int = 0,
                     var previewHeight: Int = 0,
                     var canvasWidth: Int = 0,
                     var canvasHeight: Int = 0,
                     var cameraDisplayOrientation: Int = 0,
                     var cameraId: Int = 0,
                     var isMirror: Boolean = false,
                     var isMirrorHorizontal: Boolean = false,
                     var isMirrorVertical: Boolean = false) {
    private val TAG = "TAG_FaceRectHelper"
    /**

     */

    private val newRect = Rect()

    /**
     * 调整人脸框用来绘制
     *
     * @param ftRect FT人脸框
     * @return 调整后的需要被绘制到View上的rect
     */
    fun adjustRect(ftRect: Rect?): Rect {
        newRect.setEmpty()
        if (ftRect == null) {
            return newRect
        }
        val rect = Rect(ftRect)
        val horizontalRatio: Float
        val verticalRatio: Float
        if (cameraDisplayOrientation % 180 == 0) {
            horizontalRatio = canvasWidth.toFloat() / previewWidth.toFloat()
            verticalRatio = canvasHeight.toFloat() / previewHeight.toFloat()
        } else {
            horizontalRatio = canvasHeight.toFloat() / previewWidth.toFloat()
            verticalRatio = canvasWidth.toFloat() / previewHeight.toFloat()
        }
        val left = rect.left*horizontalRatio
        val right = rect.right*horizontalRatio
        val top = rect.top*verticalRatio
        val bottom = rect.bottom*verticalRatio
        rect.left = left.toInt()
        rect.right = right.toInt()
        rect.top = top.toInt()
        rect.bottom = bottom.toInt()
//        Log.i(TAG, "adjustRect: $canvasWidth $canvasHeight  $previewWidth $previewHeight")

        when (cameraDisplayOrientation) {
            0 -> {
                if (cameraId == CameraSelector.LENS_FACING_FRONT) {
                    newRect.left = canvasWidth - rect.right
                    newRect.right = canvasWidth - rect.left
                } else {
                    newRect.left = rect.left
                    newRect.right = rect.right
                }
                newRect.top = rect.top
                newRect.bottom = rect.bottom
            }
            90 -> {
                newRect.right = canvasWidth - rect.top
                newRect.left = canvasWidth - rect.bottom
                if (cameraId == CameraSelector.LENS_FACING_FRONT) {
                    newRect.top = canvasHeight - rect.right
                    newRect.bottom = canvasHeight - rect.left
                } else {
                    newRect.top = rect.left
                    newRect.bottom = rect.right
                }
            }
            180 -> {
                newRect.top = canvasHeight - rect.bottom
                newRect.bottom = canvasHeight - rect.top
                if (cameraId == CameraSelector.LENS_FACING_FRONT) {
                    newRect.left = rect.left
                    newRect.right = rect.right
                } else {
                    newRect.left = canvasWidth - rect.right
                    newRect.right = canvasWidth - rect.left
                }
            }
            270 -> {
                newRect.left = rect.top
                newRect.right = rect.bottom
                if (cameraId == CameraSelector.LENS_FACING_FRONT) {
                    newRect.top = rect.left
                    newRect.bottom = rect.right
                } else {
                    newRect.top = canvasHeight - rect.right
                    newRect.bottom = canvasHeight - rect.left
                }
            }
            else -> {
            }
        }
        /**
         * isMirror mirrorHorizontal finalIsMirrorHorizontal
         * true         true                false
         * false        false               false
         * true         false               true
         * false        true                true
         *
         * XOR
         */
        if (isMirror xor isMirrorHorizontal) {
            val left = newRect.left
            val right = newRect.right
            newRect.left = canvasWidth - right
            newRect.right = canvasWidth - left
        }
        if (isMirrorVertical) {
            val top = newRect.top
            val bottom = newRect.bottom
            newRect.top = canvasHeight - bottom
            newRect.bottom = canvasHeight - top
        }
        return newRect
    }
}