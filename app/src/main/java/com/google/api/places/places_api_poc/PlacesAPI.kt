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

import android.Manifest
import android.app.Application
import android.arch.lifecycle.*
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.location.places.Places
import com.google.android.gms.tasks.OnCompleteListener
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlacesAPI(val context: Application) : AndroidViewModel(context),
        LifecycleObserver, AnkoLogger {
    // Client for geo data
    lateinit var geoDataClient: GeoDataClient
    // Client for place detection
    lateinit var placeDetectionClient: PlaceDetectionClient
    // LiveData for place picker API responses
    val placePickerData = MutableLiveData<String>()
    // LiveData for current place API responses
    val currentPlaceData = MutableLiveData<String>()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connect() {
        info {
            "ON_CREATE ⇢ PlacesAPIClients.connect() ✅"
        }
        geoDataClient = Places.getGeoDataClient(context)
        placeDetectionClient = Places.getPlaceDetectionClient(context)

        // Debug stuff
        placePickerData.value = "connect!"
        currentPlaceData.value = "connect!"
        context.toast("connect() - got GetDataClient and PlaceDetectionClient")

        // Create executor
        createExecutor()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        context.toast("cleanup()")
        info {
            "PlacesAPIClients.cleanup()"
        }
        destroyExecutor()
    }

    lateinit var executor: ExecutorService

    fun createExecutor() {
        executor = Executors.newCachedThreadPool()
    }

    fun destroyExecutor() {
        executor.shutdown()
    }

    fun getCurrentPlace() {
        if (ContextCompat.checkSelfPermission(context,
                                              Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted 🙌
            placeDetectionClient.getCurrentPlace(null).apply {
                addOnCompleteListener(executor, OnCompleteListener { task ->
                    if (task.getResult() != null)
                        processPlacelikelihoodBuffer(task.getResult()!!)
                })
            }

        }

    }

    // This runs in background thread
    private fun processPlacelikelihoodBuffer(likeyPlaces: PlaceLikelihoodBufferResponse) {
        val outputString = StringBuilder()
        likeyPlaces.forEach { placeLikelihood ->
            outputString.append("📌 ${placeLikelihood.place.name} 🤷 ${placeLikelihood.likelihood}️")
        }
        likeyPlaces.release()
        currentPlaceData.postValue(outputString.toString())
    }

}