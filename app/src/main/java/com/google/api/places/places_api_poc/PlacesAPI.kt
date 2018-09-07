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
import android.location.Location
import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.OnCompleteListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlacesAPI(val context: Application) : AndroidViewModel(context), LifecycleObserver {

    //
    // Places API clients.
    //

    // Client for geo data.
    private lateinit var geoDataClient: GeoDataClient
    // Client for place detection.
    private lateinit var currentPlaceClient: PlaceDetectionClient
    // LiveData for current place API responses.
    val currentPlaceLiveData = MutableLiveData<List<PlaceWrapper>>()

    //
    // Fused Location Provider.
    //

    private lateinit var currentLocationClient: FusedLocationProviderClient
    val currentLocationLiveData = MutableLiveData<Location>()

    //
    // Activity lifecycle.
    //

    // Lifecycle hooks.
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connect() {
        "ON_CREATE ⇢ PlacesAPIClients.connect() ✅".log()

        geoDataClient = Places.getGeoDataClient(context)
        currentPlaceClient = Places.getPlaceDetectionClient(context)

        "💥 connect() - got GetDataClient and PlaceDetectionClient".log()

        currentLocationClient = LocationServices.getFusedLocationProviderClient(context)
        "💥 connect() - got FusedLocationProviderClient".log()

        createExecutor()
        "💥 connect() - complete!".log()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        "🚿 PlacesAPIClients.cleanup()".log()
        destroyExecutor()
    }

    //
    // Manage background executor.
    //

    // Manage background execution.
    lateinit var executor: ExecutorService

    fun createExecutor() {
        executor = Executors.newCachedThreadPool()
    }

    fun destroyExecutor() {
        executor.shutdown()
    }

    //
    // Current Place.
    //

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun getCurrentPlace() {
        if (isPermissionGranted(context, ACCESS_FINE_LOCATION)) {
            // Permission is granted 🙌.
            "PlacesAPI ⇢ PlaceDetectionClient.getCurrentPlace() ✅".log()
            currentPlaceClient.getCurrentPlace(null).let { requestTask ->
                // Run this in background thread
                requestTask.addOnCompleteListener(
                    executor,
                    OnCompleteListener { responseTask ->
                        if (responseTask.isSuccessful) {
                            processPlacelikelihoodBuffer(responseTask.result)
                            responseTask.result.release()
                        } else {
                            "⚠️ Task failed with exception ${responseTask.exception}".log()
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
        currentPlaceLiveData.postValue(outputList)
    }

    //
    // Place Autocomplete.
    //

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (isPermissionGranted(context, ACCESS_FINE_LOCATION)) {
            "PlacesAPI ⇢ FusedLocationProviderClient.lastLocation() ✅".log()
            currentLocationClient.lastLocation.let { requestTask ->
                // Run this in background thread
//                requestTask.addOnSuccessListener(
//                    executor,
//                    OnSuccessListener { location ->
//                        processCurrentLocation(location)
//                    })
                requestTask.addOnCompleteListener(
                    executor,
                    OnCompleteListener { responseTask ->
                        if (responseTask.isSuccessful && responseTask.result != null) {
                            processCurrentLocation(responseTask.result)
                        } else {
                            "⚠️ Task failed with exception ${responseTask.exception}".log()
                        }
                    }
                )
            }
        }
    }

    private fun processCurrentLocation(value: Location) {
        currentLocationLiveData.postValue(value)
    }

}