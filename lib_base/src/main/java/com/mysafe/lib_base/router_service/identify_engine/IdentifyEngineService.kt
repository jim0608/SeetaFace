package com.mysafe.lib_base.router_service.identify_engine

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.mysafe.lib_base.sqlite.entity.FaceEntity

/**
 * @author Create By 张晋铭
 * @Date on 2021/6/1
 * @Describe:
 */
interface IdentifyEngineService:IProvider {
    fun setFaceList(context: Context, list:MutableList<FaceEntity>?)
    fun addFaceInfo(faceInfo:FaceEntity)
}