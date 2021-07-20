/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mysafe.lib_identification.camera_view

import android.annotation.SuppressLint
import androidx.annotation.OptIn
import androidx.camera.core.CameraFilter
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector.LensFacing
import androidx.camera.core.ExperimentalCameraFilter
import androidx.camera.core.impl.CameraInfoInternal
import androidx.core.util.Preconditions
import java.util.*

/**
 * A filter that filters camera based on camera Id.
 */
@OptIn(markerClass = [ExperimentalCameraFilter::class])
class CameraIdFilter(@field:LensFacing
                     /** Returns the lens facing associated with this lens facing camera id filter.  */
                     @get:LensFacing
                     @param:LensFacing val cameraId: Int) : CameraFilter {
    @SuppressLint("RestrictedApi")
    override fun filter(cameraInfos: List<CameraInfo>): List<CameraInfo> {
        val result: MutableList<CameraInfo> = ArrayList()
        for (cameraInfo in cameraInfos) {
            Preconditions.checkArgument(cameraInfo is CameraInfoInternal,
                    "The camera info doesn't contain internal implementation.")
            val cameraId = (cameraInfo as CameraInfoInternal).cameraId.toInt()
            if (cameraId != null && cameraId == this.cameraId) {
                result.add(cameraInfo)
            }
        }
        return result
    }
}