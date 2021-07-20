package com.plugin.versionplugin

/**
 * @author jimz
 * @description 编译配置信息
 */
object BuildConfig {
    const val compileSdkVersion = 28
    const val buildToolsVersion ="28.0.3"
    const val minSdkVersion = 22
    const val targetSdkVersion = 28

    const val applicationId ="com.phz.composingbuilddemo"
    const val testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
    const val testApplicationId="org.eclipse.paho.android.service.test"
    const val consumerProguardFiles="consumer-rules.pro"

    var versionName = "1.2"
    var versionCode = 3

    var gradle_version = "4.1.1"
    var kotlin_version = "1.4.20"
    val kt_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"

    object Kotlin {
        var stdlib ="org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
        val stdlibJdk7 = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
        val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"
        val test = "org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version"
        val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}