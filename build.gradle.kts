import com.android.build.gradle.BaseExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlinx.kover) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// Add JVM setup on all gradle subprojects from Gradle Version Catalog
val jvm = JvmTarget.fromTarget(libs.versions.jvmVersion.get())
allprojects {
    tasks.withType(KotlinCompile::class.java).configureEach {
        compilerOptions.jvmTarget = jvm
    }
    tasks.withType(JavaCompile::class.java).configureEach {
        sourceCompatibility = jvm.target
        targetCompatibility = jvm.target
    }
    if (pluginManager.hasPlugin("com.android.base")) {
        pluginManager.withPlugin("com.android.base") {
            extensions.configure("android", Action<BaseExtension> {
                compileOptions.sourceCompatibility = JavaVersion.toVersion(jvm.target)
                compileOptions.targetCompatibility = JavaVersion.toVersion(jvm.target)
            })
        }
    }
}

buildscript {
    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin.gradle.plugin)
    }
}
