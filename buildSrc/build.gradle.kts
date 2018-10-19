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
  `kotlin-dsl`
}

// More info: https://docs.gradle.org/4.10.1/userguide/kotlin_dsl.html#sec:kotlin-dsl_plugin
repositories {
  jcenter()
}

// More info : https://stackoverflow.com/a/46440810/2085356
// More info : https://github.com/gradle/kotlin-dsl/issues/443
sourceSets {
  getByName("main").java.srcDir("src/main/kotlin")
}