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
import android.util.Log
import androidx.lifecycle.*
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.OnCompleteListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlacesAPI(val context: Application) : AndroidViewModel(context),
        LifecycleObserver {
    // Client for geo data
    lateinit var geoDataClient: GeoDataClient
    // Client for place detection
    lateinit var placeDetectionClient: PlaceDetectionClient
    // LiveData for place picker API responses
    val placePickerData = MutableLiveData<String>()
    // LiveData for current place API responses
    val currentPlaceData = MutableLiveData<String>()

    // Lifecycle hooks.
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connect() {
        Log.i(javaClass.name,
              "ON_CREATE ⇢ PlacesAPIClients.connect() ✅")
        geoDataClient = Places.getGeoDataClient(context)
        placeDetectionClient = Places.getPlaceDetectionClient(context)

        // Debug stuff
        placePickerData.value = "connect!"
        currentPlaceData.value = "connect!"
        Log.i(javaClass.name,
              "💥 connect() - got GetDataClient and PlaceDetectionClient"
              )

        // Create executor
        createExecutor()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        Log.i(javaClass.name,
              "🛀 PlacesAPIClients.cleanup()")
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
            Log.i(javaClass.name,
                  "PlacesAPI ⇢ PlaceDetectionClient.getCurrentPlace() ✅")
            placeDetectionClient.getCurrentPlace(null).let { task ->
                // Run this in background thread
                task.addOnCompleteListener(
                    executor,
                    OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            processPlacelikelihoodBuffer(task.result)
                            task.result.release()
                        } else {
                            Log.i(javaClass.name,
                                  "⚠️ Task failed with exception ${task.exception}")
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
        val outputList = mutableListOf<String>()
        val count = likeyPlaces.count
        for (index in 0 until count) {
            val placeLikelihood = likeyPlaces.get(index)
            val confidence = placeLikelihood.likelihood * 100
            with(placeLikelihood.place) {
                outputList.add("📌 ${name}, ${confidence}％, 🗺${address}")
            }
        }
        with(outputList.joinToString("\n")) {
            Log.i(javaClass.name,
                  this)
            currentPlaceData.postValue(this)
        }
    }

}