import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlinx.kover) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

/** Align JVM target versions between Java and Kotlin compilation tasks. */
val jvm = JvmTarget.fromTarget(libs.versions.jvmVersion.get())
subprojects {
    tasks.withType(KotlinCompile::class.java).configureEach {
        compilerOptions.jvmTarget = jvm
    }
    tasks.withType(JavaCompile::class.java).configureEach {
        sourceCompatibility = jvm.target
        targetCompatibility = jvm.target
    }
    afterEvaluate {
        if (plugins.hasPlugin("com.android.base")) {
            pluginManager.withPlugin("com.android.base") {
                extensions.configure("android", Action<BaseExtension> {
                    compileOptions.sourceCompatibility = JavaVersion.toVersion(jvm.target)
                    compileOptions.targetCompatibility = JavaVersion.toVersion(jvm.target)
                })
            }
        }
        // Apply JVM toolchain to Kotlin projects
        if (plugins.hasPlugin("org.jetbrains.kotlin.jvm") || plugins.hasPlugin("org.jetbrains.kotlin.android")) {
            extensions.findByType(KotlinProjectExtension::class.java)?.apply {
                    jvmToolchain(jvm.target.toInt())
                }
        }

        // Apply JVM toolchain to Java projects
        if (plugins.hasPlugin("java")) {
            extensions.findByType(JavaPluginExtension::class.java)?.apply {
                toolchain.languageVersion.set(JavaLanguageVersion.of(jvm.target.toInt()))
                sourceCompatibility = JavaVersion.toVersion(jvm.target)
                targetCompatibility = JavaVersion.toVersion(jvm.target)
            }
        }
    }
}

buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
    }
}
