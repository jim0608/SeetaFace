buildscript {
    repositories {
        // 需要添加 jcenter 否则会提示找不到 gradlePlugin
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20")
    }
}

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}


repositories {
    mavenCentral()
    google()
    jcenter()
}

dependencies {
    implementation(gradleApi())
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.20")
//    implementation("com.android.tools.build:gradle:4.1.3")
}

kotlinDslPluginOptions {
    experimentalWarning.set(false)
}

gradlePlugin {
    plugins {
        create("version") {
            // 在 app 模块需要通过 id 引用这个插件
            id = "com.plugin.version"
            // 实现这个插件的类的路径
            implementationClass = "com.plugin.versionplugin.CommonVersionPlugin"
        }
    }
}
