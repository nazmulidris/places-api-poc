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

object Versions {
    val gradle = "gradle:3.3.0-alpha13"
    val kotlin = "1.2.71"

    val compile_sdk = 28
    val target_sdk = 28
    val min_sdk = 16

    val arch_comp = "2.0.0"

    val design = "1.0.0"

    val gms = "16.0.0"

    val gson = "2.8.5"

    val dagger2 = "2.17"
}

object Deps {
    val gradle = "com.android.tools.build:${Versions.gradle}"

    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"

    val arch_comp = "androidx.lifecycle:lifecycle-extensions:${Versions.arch_comp}"
    val arch_comp_annotation = "androidx.lifecycle:lifecycle-compiler:${Versions.arch_comp}"

    val material_design = "com.google.android.material:material:${Versions.design}"
    val vector_drawable = "androidx.vectordrawable:vectordrawable:${Versions.design}"
    val recycler_view = "androidx.recyclerview:recyclerview:${Versions.design}"

    val gms_places = "com.google.android.gms:play-services-places:${Versions.gms}"
    val gms_location = "com.google.android.gms:play-services-location:${Versions.gms}"

    val gson = "com.google.code.gson:gson:${Versions.gson}"

    val dagger2 = "com.google.dagger:dagger:${Versions.dagger2}"
    val dagger2_annotation = "com.google.dagger:dagger-compiler:${Versions.dagger2}"
}