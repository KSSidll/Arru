// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.jvm) apply false
    alias(libs.plugins.spotless) apply false
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**/*.kt")
            ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
        }

        kotlinGradle {
            target("*.gradle.kts")
            ktfmt(libs.versions.ktfmt.get()).kotlinlangStyle()
        }

        format("misc") {
            target("*.gradle", "*.md", ".gitignore")
            trimTrailingWhitespace()
            leadingTabsToSpaces(2)
            endWithNewline()
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
