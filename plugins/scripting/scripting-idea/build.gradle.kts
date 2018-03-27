
description = "Kotlin Scripting IDEA Plugin"

plugins {
    kotlin("jvm")
}

dependencies {
    compile(project(":kotlin-scripting-compiler"))
}

sourceSets {
    "main" { projectDefault() }
    "test" {}
}

runtimeJar()

ideaPlugin()

