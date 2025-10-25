plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradleup.patrouille)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.jetbrains.compose)
}

compatPatrouille {
    java(libs.versions.java.get().toInt())
    kotlin(libs.versions.kotlin.get())
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
        freeCompilerArgs.add("-Xnested-type-aliases")
        optIn.add("kotlin.time.ExperimentalTime")
        optIn.add("kotlin.uuid.ExperimentalUuidApi")
    }
    sourceSets.commonMain.dependencies {
        // ##### Official Dependencies #####
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.datetime)

        implementation(libs.okio)

        // ##### Builtin Dependencies #####
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.material3AdaptiveNavigationSuite)
        implementation(compose.materialIconsExtended)
        implementation(compose.ui)
        implementation(compose.components.resources)
        implementation(compose.components.uiToolingPreview)

        // ##### Internal Dependencies #####

        implementation(libs.optionkt)
        implementation(libs.extkt.json)

        implementation(libs.lsafer.compose.simplenav)

        // ##### Community Dependencies #####

        implementation(libs.touchlab.kermit)

        // ##### ANDROID Dependencies #####

        implementation(libs.androidx.lifecycle.runtime.compose)

        implementation(libs.androidx.lifecycle.viewmodel)
        implementation(libs.androidx.lifecycle.viewmodel.compose)
    }
}
