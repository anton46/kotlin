import org.jetbrains.kotlin.gradle.dsl.Coroutinesapply {    plugin("kotlin")}dependencies {    compile(project(":examples:scripting-jvm-simple-script:script"))    compile(project(":kotlin-scripting-jvm-host"))    compile(project(":kotlin-script-util"))    runtime(projectDist(":kotlin-compiler"))    testCompile(commonDep("junit"))}sourceSets {    "main" { projectDefault() }    "test" { projectDefault() }}kotlin {    experimental.coroutines = Coroutines.ENABLE}