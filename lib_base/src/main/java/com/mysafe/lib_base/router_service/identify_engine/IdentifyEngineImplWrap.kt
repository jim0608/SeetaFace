package com.mysafe.lib_base.router_service.identify_engine

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.launcher.ARouter
import com.mysafe.lib_base.router_service.ConstancePath
import com.mysafe.lib_base.sqlite.entity.FaceEntity

/**
 * @author Create By 张晋铭
 * @Date on 2021/6/1
 * @Describe:
 */
object IdentifyEngineImplWrap {
    @Autowired(name = ConstancePath.IdentifyEnginePath)
    lateinit var service: IdentifyEngineService

    init {
        ARouter.getInstance().inject(this)
    }

    fun setFaceList(context: Context, list: MutableList<FaceEntity>?) {
        service.setFaceList(context,list)
    }

    fun addFaceInfo(faceInfo:FaceEntity) {
        service.addFaceInfo(faceInfo)
    }
}