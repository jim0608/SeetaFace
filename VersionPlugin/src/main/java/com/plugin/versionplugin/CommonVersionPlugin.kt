package com.plugin.versionplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import com.plugin.versionplugin.Testing.androidTestImplementation
import com.plugin.versionplugin.Testing.testImplementation

import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.plugins.PluginContainer
import org.gradle.kotlin.dsl.defaultTasks
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
//import com.android.build.gradle.*

/**
 * @author Create By 张晋铭
 * @Date on 2021/5/20
 * @Describe:
 */
const val api = "api"
const val implementation = "implementation"

class CommonVersionPlugin: Plugin<Project> {
    private val TAG = "TAG_CommonVersionPlugin"
    override fun apply(target: Project) {

    }

//    private fun PluginContainer.config(project: Project) {
//        whenPluginAdded {
//            when (this) {
//                //com.android.application
//                is AppPlugin -> {
//                    //公共插件
//                    project.configCommonPlugin()
//                    //公共 android 配置项
//                    project.extensions.getByType<AppExtension>().applyAppCommons(project)
//                    //公共依赖
//                    project.configAppDependencies()
//                }
//                //com.android.library
//                is LibraryPlugin -> {
//                    //公共插件
//                    project.configCommonPlugin()
//                    //公共 android 配置项
//                    project.extensions.getByType<LibraryExtension>().applyLibraryCommons(project)
//                    //公共依赖
//                    project.configLibraryDependencies()
//                }
//                is KotlinAndroidPluginWrapper -> {
//                    //根据 project build.gradle.kts 中的配置动态设置 kotlinVersion
//                    project.getKotlinPluginVersion()?.also { kotlinVersion ->
//                        BuildConfig.kotlin_version = kotlinVersion
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * library Module 公共依赖
//     */
//    private fun Project.configLibraryDependencies() {
//        dependencies.apply {
//            add(api, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//            add(implementation, BuildConfig.Kotlin.stdlib)
//            configTestDependencies()
//        }
//    }
//
//    /**
//     * app Module 公共依赖
//     */
//    private fun Project.configAppDependencies() {
//        dependencies.apply {
//            add(implementation, fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
//            add(implementation, BuildConfig.Kotlin.stdlib)
//            // 统一引入 baselib,当有baselib作为library时
//            /*add(implementation, (project(":baselib")))*/
//            configTestDependencies()
//        }
//    }
//
//    /**
//     * test 依赖配置
//     */
//    private fun DependencyHandler.configTestDependencies() {
//        testImplementation(Testing.testLibraries)
//        androidTestImplementation(Testing.androidTestLibraries)
//    }
//
//    /**
//     * 公共plugin插件依赖
//     */
//    private fun Project.configCommonPlugin() {
//        plugins.apply("kotlin-android")
//        plugins.apply("kapt")
//        plugins.apply("kotlin-android-extensions")
//    }
//
//    /**
//     * app Module 配置项，此处固定了 applicationId
//     */
//    private fun AppExtension.applyAppCommons(project: Project) {
//        defaultConfig { applicationId = BuildConfig.applicationId }
//        applyBaseCommons(project)
//    }
//
//    /**
//     * library Module 配置项
//     */
//    private fun LibraryExtension.applyLibraryCommons(project: Project) {
//        applyBaseCommons(project)
//
//        /*onVariants.withBuildType("debug") {
//            androidTest {
//                enabled = false
//            }
//        }*/
//    }
//
//    /**
//     * 公共需要添加的设置，如sdk目标版本，jdk版本，jvm目标版本等
//     */
//    private fun BaseExtension.applyBaseCommons(project: Project) {
//        compileSdkVersion(BuildConfig.compileSdkVersion)
//
//        defaultConfig {
//            minSdkVersion(BuildConfig.minSdkVersion)
//            targetSdkVersion(BuildConfig.targetSdkVersion)
//            versionCode = BuildConfig.versionCode
//            versionName = BuildConfig.versionName
//            testInstrumentationRunner = BuildConfig.testInstrumentationRunner
//        }
//        compileOptions {
//            sourceCompatibility = JavaVersion.VERSION_1_8
//            targetCompatibility = JavaVersion.VERSION_1_8
//        }
//
////        project.tasks.withType<KotlinCompile> {
////            kotlinOptions{
////                jvmTarget = "1.8"
////            }
////        }

}
