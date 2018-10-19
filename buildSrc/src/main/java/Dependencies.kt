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

object GradlePlugins {
    data class Versions(val gradle: String = "gradle:3.3.0-alpha13",
                        val kotlin: String = "1.2.71",
                        val junit5: String = "1.2.0.0")

    val versions = Versions()

    val gradle = "com.android.tools.build:${versions.gradle}"

    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.kotlin}"

    val junit5 = "de.mannodermaus.gradle.plugins:android-junit5:${versions.junit5}"
}

object Versions {
    val compile_sdk = 28
    val target_sdk = 28
    val min_sdk = 16
}

object Deps {
    data class Versions(val arch_comp: String = "2.0.0",
                        val design: String = "1.0.0",
                        val gson: String = "2.8.5",
                        val gms: String = "16.0.0",
                        val dagger2: String = "2.17",
                        val junit5: String = "5.2.0")

    val versions = Versions()

    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${GradlePlugins.versions.kotlin}"

    val arch_comp = "androidx.lifecycle:lifecycle-extensions:${versions.arch_comp}"
    val arch_comp_annotation = "androidx.lifecycle:lifecycle-compiler:${versions.arch_comp}"

    val material_design = "com.google.android.material:material:${versions.design}"
    val vector_drawable = "androidx.vectordrawable:vectordrawable:${versions.design}"
    val recycler_view = "androidx.recyclerview:recyclerview:${versions.design}"

    val gms_places = "com.google.android.gms:play-services-places:${versions.gms}"
    val gms_location = "com.google.android.gms:play-services-location:${versions.gms}"

    val gson = "com.google.code.gson:gson:${versions.gson}"

    val dagger2 = "com.google.dagger:dagger:${versions.dagger2}"
    val dagger2_annotation = "com.google.dagger:dagger-compiler:${versions.dagger2}"

    val junit5_jupiter = "org.junit.jupiter:junit-jupiter-api:${versions.junit5}"
    val junit5_jupiter_runtime = "org.junit.jupiter:junit-jupiter-engine:${versions.junit5}"
    val junit5_jupiter_params = "org.junit.jupiter:junit-jupiter-params:${versions.junit5}"
}
