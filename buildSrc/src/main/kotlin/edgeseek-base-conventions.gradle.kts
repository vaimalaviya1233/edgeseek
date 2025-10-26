plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.gradleup.patrouille)
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
}

dependencies {
    val implementation = "implementation"
    val coreLibraryDesugaring = "coreLibraryDesugaring"

    // ##### Official Dependencies #####
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)

    implementation(libs.okio)

    // ##### Builtin Dependencies #####
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    // ##### Internal Dependencies #####

    implementation(libs.optionkt)
    implementation(libs.extkt.json)

    implementation(libs.lsafer.compose.simplenav)

    // ##### Community Dependencies #####

    implementation(libs.touchlab.kermit)

    implementation(libs.godaddy.colorpickerCompose)

    // ##### ANDROID Dependencies #####

    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.cardview)

    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")
}
