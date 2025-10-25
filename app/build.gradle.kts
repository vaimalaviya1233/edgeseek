import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.kotlin.gradle.plugin.extraProperties

plugins {
    `edgeseek-app-conventions`
    alias(libs.plugins.gmazzo.buildConfig)
}

val pkg = "net.lsafer.edgeseek.app"
val application_id = rootProject.extraProperties["application_id"].toString()
val version_code = rootProject.extraProperties["version_code"].toString().toInt()

val configFile = file("$rootDir/app-config.local.json")
val configString = runCatching { configFile.readText() }
    .onFailure { System.err.println("app config: ${it.message}") }
    .mapCatching { Json.parseToJsonElement(it) }
    .onFailure { System.err.println("app config: ${it.message}") }
    .fold({ Json.encodeToString(it) }, { "{}" })

buildConfig {
    className = "BuildConfig"
    packageName = pkg
    useKotlinOutput { internalVisibility = false }
    buildConfigField("VERSION", version.toString())
    buildConfigField("VERSION_CODE", version_code)
    buildConfigField("CONFIG_STRING", configString)
}

android {
    namespace = pkg

    defaultConfig {
        applicationId = application_id
        versionCode = version_code
        versionName = version.toString()
    }
}
