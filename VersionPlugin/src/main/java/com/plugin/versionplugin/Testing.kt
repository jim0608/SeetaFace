package com.plugin.versionplugin

import org.gradle.api.artifacts.dsl.DependencyHandler

/**
 * @author jimz
 */
object Testing {
    private const val testImplementation = "testImplementation"
    private const val androidTestImplementation = "androidTestImplementation"

    const val junit = "junit:junit:4.+"
    const val androidJunit = "androidx.test.ext:junit:1.1.2"
    const val androidRunner = "androidx.test:runner:1.2.0"
    const val espresso = "androidx.test.espresso:espresso-core:3.3.0"

    val androidTestLibraries = arrayListOf<String>().apply {
        add(androidJunit)
        add(androidRunner)
        add(espresso)
    }

    val testLibraries = arrayListOf<String>().apply {
        add(junit)
    }

    fun DependencyHandler.androidTestImplementation(list: List<String>) {
        list.forEach { dependency ->
            add(androidTestImplementation, dependency)
        }
    }

    fun DependencyHandler.testImplementation(list: List<String>) {
        list.forEach { dependency ->
            add(testImplementation, dependency)
        }
    }
}