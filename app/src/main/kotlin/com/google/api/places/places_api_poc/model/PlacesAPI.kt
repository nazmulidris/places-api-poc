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

package com.google.api.places.places_api_poc.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.api.places.places_api_poc.misc.ExecutorWrapper
import com.google.api.places.places_api_poc.misc.getMyApplication
import com.google.api.places.places_api_poc.misc.log
import javax.inject.Inject


class PlacesAPI(val app: Application) : AndroidViewModel(app), LifecycleObserver {

    // Background Executor.
    @Inject
    lateinit var executorWrapper: ExecutorWrapper

    /**
     * When this object is constructed, setup the Dagger 2 subcomponent (@ActivityScope).
     * This can't be done in connect(), as the DriverActivity needs this to be setup as
     * soon as the ViewModel object is created (which eliminates any race conditions w/
     * Activity lifecycle, and lifecycle observer lifecycle (that's observing an Activity).
     */
    init {
        // Dagger 2 subcomponent creation.
        app.getMyApplication()
                .createActivityComponent()
                .inject(this)
    }

    //
    // Activity lifecycle.
    //

    // Lifecycle hooks.
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connect() {
        with("ON_CREATE" +
                     "\n ⇢ PlacesAPI.connect() ✅" +
                     "\n ⇢ Create GeoDataClient, PlaceDetectionClient, FusedLocationProviderClient ✅" +
                     "\n ⇢ Create API wrappers ✅" +
                     "\n ⇢ Create Executor ✅") { this.log() }
        executorWrapper.create()
        "💥 connect() - complete!".log()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        "ON_DESTROY ⇢ PlacesAPI cleanup ✅".log()
        executorWrapper.destroy()
        "🚿 cleanup() - complete!".log()
        app.getMyApplication().destroyActivityComponent()
    }

}