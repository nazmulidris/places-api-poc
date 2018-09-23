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

package com.google.api.places.places_api_poc.daggger

import com.google.api.places.places_api_poc.PlacesAPI
import dagger.Component
import dagger.Module
import dagger.Provides
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@ActivityScope
@Component(dependencies = [ApplicationComponent::class],
           modules = [ExecutorModule::class])
interface ActivityComponent {
    fun inject(placesAPI: PlacesAPI)
}

@Module
class ExecutorModule {
    @Provides
    @ActivityScope
    fun provideExecutor(): ExecutorWrapper {
        return ExecutorWrapper()
    }
}

class ExecutorWrapper {
    lateinit var executor: ExecutorService

    fun create() {
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
    }

    fun destroy() {
        executor.shutdown()
    }
}