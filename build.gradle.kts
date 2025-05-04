// 기본적인 Kotlin DSL 구문으로 간소화
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath("com.android.tools.build:gradle:8.9.1")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51")
        classpath ("androidx.navigation:navigation-safe-args-gradle-plugin:2.7.7")
    }
}

// 모든 프로젝트에 대한 공통 설정
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
