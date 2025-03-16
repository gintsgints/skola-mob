buildscript {
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.25")
    }
}

allprojects {
    repositories {
        google()
        maven { url = uri("https://jitpack.io") }
        mavenCentral()
    }
}

tasks.register("clean").configure {
    delete("build")
}

