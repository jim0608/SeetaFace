package com.mysafe.lib_identification.camera_view

interface CameraCallback {
    /**
     * 返回nv21预览数据
     * @param analyzeBean 预览帧画面相关数据实体
     */
    fun cameraPreviewFrame(analyzeBean:YUV420spAnalyzeBean)
}