package com.plugin.versionplugin

/**
 * @author Create By 张晋铭
 * @Date on 2021/5/20
 * @Describe:
 */
object ThirdDepend {
    //顶部SnackBar
    const val topSnackBar = "com.github.PengHaiZhuo:TSnackBar:1.1.1"
    const val mmkv = "com.tencent:mmkv-static:1.2.4"
    const val BaseQuickAdapter = "com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.4"
    const val xxPermission = "com.hjq:xxpermissions:9.0"
    const val XCrash = "com.iqiyi.xcrash:xcrash-android-lib:3.0.0"

    //网路请求库retrofit
    object Retrofit {
        private const val retrofit_version = "2.9.0"
        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofit_version"
        const val convertGson = "com.squareup.retrofit2:converter-gson:$retrofit_version"
        const val adapterRxjava = "com.squareup.retrofit2:adapter-rxjava2:$retrofit_version"
    }

    //okhttp
    object OkHttp {
        private const val okhttp_version = "4.10.0-RC1"
        const val okhttp = "com.squareup.okhttp3:okhttp:$okhttp_version"
        const val urlConnection = "com.squareup.okhttp3:okhttp-urlconnection:$okhttp_version"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$okhttp_version"
    }

    //rxjava
    const val rxjava2 = "io.reactivex.rxjava2:rxjava:2.2.14"
    const val rxandroid = "io.reactivex.rxjava2:rxandroid:2.1.1"

    //图片加载框架
    object Glide {
        private const val glide_version = "4.11.0"
        const val glide = "com.github.bumptech.glide:glide:$glide_version"
        const val compiler = "com.github.bumptech.glide:compiler:$glide_version"
    }

    //mqtt
    object MQTT {
        const val mqtt_client = "org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.4"
        const val mqtt_service = "org.eclipse.paho:org.eclipse.paho.android.service:1.1.1"
    }

    //协程coroutines
    object Coroutines {
        private val coroutines_version = "1.4.3"
        val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version"
        val coroutines_android =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version"
    }

    //ARouter
    val arouter_api = "com.alibaba:arouter-api:1.5.0"
    val arouter_compiler = "com.alibaba:arouter-compiler:1.2.2"

}