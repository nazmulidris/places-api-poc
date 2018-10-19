/*
 * Copyright 2018 Nazmul Idris. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("de.mannodermaus.android-junit5")
}


android {
    compileSdkVersion(Versions.compile_sdk)
    defaultConfig {
        applicationId = "com.google.api.places.places_api_poc"
        minSdkVersion(Versions.compile_sdk)
        targetSdkVersion(Versions.target_sdk)
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
    }
    // More info : https://stackoverflow.com/a/46440810/2085356
    // More info : https://github.com/gradle/kotlin-dsl/issues/443
    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
    }
}

// Kotlin.
dependencies {
    implementation(Deps.kotlin_stdlib_jdk8)
}

// Add Architecture Components (ViewModel, LiveData).
dependencies {
    implementation(Deps.arch_comp)
    kapt(Deps.arch_comp_annotation)
}

// Design.
dependencies {
    implementation(Deps.material_design)
    implementation(Deps.vector_drawable)
    implementation(Deps.recycler_view)
}

// GMS APIs (Places API, Fused Location Provider).
dependencies {
    implementation(Deps.gms_places)
    implementation(Deps.gms_location)
}

// GSON.
dependencies {
    implementation(Deps.gson)
}

// Dagger 2.
run {
    // Dagger 2 and Kotlin docs - https://kotlinlang.org/docs/tutorials/android-frameworks.html
    // Dagger 2 and Android (Java) - https://kotlinlang.org/docs/tutorials/android-frameworks.html
    dependencies {
        // Basic Dagger 2 (required).
        implementation(Deps.dagger2)
        kapt(Deps.dagger2_annotation)
    }
}

// Testing w/ JUnit5 & AssertJ.
run {

    dependencies {
        // Add JUnit5 dependencies.
        testImplementation(TestingDeps.junit5_jupiter)
        testRuntimeOnly(TestingDeps.junit5_jupiter_runtime)
        testImplementation(TestingDeps.junit5_jupiter_params)
        // Add AssertJ dependencies.
        testImplementation(TestingDeps.assertj)
    }

    // Need this to use Java8 in order to use certain features of JUnit5 (such as calling static
    // methods on interfaces).

    // More info : https://github.com/mannodermaus/android-junit5/wiki/Getting-Started
    // More info : https://stackoverflow.com/a/45994990/2085356

    // For Kotlin sources.
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    // For Java sources.
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
