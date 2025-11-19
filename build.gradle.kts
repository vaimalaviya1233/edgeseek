//import org.jetbrains.kotlin.gradle.plugin.extraProperties

group = "net.lsafer.edgeseek"
version = "0.3-pre.7"
project.ext["version_code"] = 21
project.ext["application_id"] = "lsafer.edgeseek"

tasks.wrapper {
    gradleVersion = "8.14.3"
}

subprojects {
    version = rootProject.version
    group = buildString {
        append(rootProject.group)
        generateSequence(project.parent) { it.parent }
            .forEach {
                append(".")
                append(it.name)
            }
    }
}
