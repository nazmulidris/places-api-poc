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

package com.google.api.places.places_api_poc

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.OnCompleteListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlacesAPI(val context: Application) : AndroidViewModel(context), LifecycleObserver {

    // Client for geo data
    lateinit var geoDataClient: GeoDataClient

    // Client for place detection
    lateinit var placeDetectionClient: PlaceDetectionClient

    // LiveData for current place API responses
    val currentPlaceData = MutableLiveData<List<PlaceWrapper>>()

    // Lifecycle hooks.
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connect() {
        "ON_CREATE ⇢ PlacesAPIClients.connect() ✅".log()
        geoDataClient = Places.getGeoDataClient(context)
        placeDetectionClient = Places.getPlaceDetectionClient(context)

        // Debug stuff
        "💥 connect() - got GetDataClient and PlaceDetectionClient".log()

        // Create executor
        createExecutor()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        "🚿 PlacesAPIClients.cleanup()".log()
        destroyExecutor()
    }

    // Manage background execution.
    lateinit var executor: ExecutorService

    fun createExecutor() {
        executor = Executors.newCachedThreadPool()
    }

    fun destroyExecutor() {
        executor.shutdown()
    }

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentPlace() {
        if (isPermissionGranted(context, ACCESS_FINE_LOCATION)) {
            // Permission is granted 🙌.
            "PlacesAPI ⇢ PlaceDetectionClient.getCurrentPlace() ✅".log()
            placeDetectionClient.getCurrentPlace(null).let { task ->
                // Run this in background thread
                task.addOnCompleteListener(
                    executor,
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            processPlacelikelihoodBuffer(task.result)
                            task.result.release()
                        } else {
                            "⚠️ Task failed with exception ${task.exception}".log()
                        }
                    })
            }
        }
    }

    /**
     * This runs in the background thread.
     * [PlaceLikelihoodBufferResponse docs](http://tinyurl.com/y9y9jl3d).
     */
    private fun processPlacelikelihoodBuffer(likeyPlaces: PlaceLikelihoodBufferResponse) {
        val outputList = mutableListOf<PlaceWrapper>()
        val count = likeyPlaces.count
        for (index in 0 until count) {
            outputList.add(PlaceWrapper(likeyPlaces.get(index)))
        }

        // Dump the list of PlaceWrapper objects to logcat.
        outputList.joinToString("\n").log()

        // Update the LiveData, so observables can react to this change.
        currentPlaceData.postValue(outputList)
    }

}