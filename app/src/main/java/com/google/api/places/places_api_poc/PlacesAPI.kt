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
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.OnCompleteListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class PlacesAPI(val context: Application) : AndroidViewModel(context), LifecycleObserver {

    //
    // Places API clients - Current Place.
    //

    private lateinit var currentPlaceClient: PlaceDetectionClient
    val currentPlaceLiveData = MutableLiveData<List<PlaceWrapper>>()

    //
    // Places API clients - Current Place.
    //

    private lateinit var geoDataClient: GeoDataClient
    val autocompletePredictionLiveData = MutableLiveData<List<AutocompletePredictionData>>()

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
    // Place IDs and Details.
    //

    fun getPlaceById(placeId: String) {
        "PlacesAPI ⇢ GeoDataClient.getPlaceById() ✅".log()
        geoDataClient.getPlaceById(placeId).let { requestTask ->
            requestTask.addOnCompleteListener(
                executor,
                OnCompleteListener { responseTask ->
                    if (responseTask.isSuccessful) {
                        processPlace(responseTask.result)
                        responseTask.result.release()
                    } else {
                        "⚠️ Task failed with exception ${responseTask.exception}".log()
                    }
                }
            )
        }
    }

    enum class GET_PLACE_BY_ID { KEY, ACTION }

    // This runs in a background thread.
    private fun processPlace(placeBufferResponse: PlaceBufferResponse) {
        val place = placeBufferResponse.get(0)
        LocalBroadcastManager.getInstance(context).sendBroadcast(
            Intent(GET_PLACE_BY_ID.ACTION.name).apply {
                putExtra(GET_PLACE_BY_ID.KEY.name, PlaceWrapper(place).map)
            }
        )
    }

    //
    // Place Photos.
    //

    enum class GET_PHOTO { KEY, ACTION }

    // todo 1. Make PlaceDetailsSheetFragment call this function
    // todo 2. Make the call to the GeoDataClient to get the bitmap
    // todo 3. Fire the LocalBroadcast message
    // todo 4. Make PlaceDetailsSheetFragment respond to this message
    fun getPhoto(placeId: String) {
        "PlacesAPI ⇢ GeoDataClient.getPlacePhotos() ✅".log()

    }

    fun sendBitmap(bitmap: Bitmap) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(
            Intent(GET_PHOTO.ACTION.name).apply {
                putExtra(GET_PHOTO.KEY.name, bitmapToBundle(bitmap))
            }
        )
    }

    //
    // Place Autocomplete.
    //

    fun getAutocompletePredictions(queryString: String,
                                   bounds: LatLngBounds,
                                   filter: AutocompleteFilter = AutocompleteFilter.Builder()
                                           .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
                                           .build()) {
        "PlacesAPI ⇢ GeoDataClient.getAutocompletePredictions() ✅".log()
        geoDataClient.getAutocompletePredictions(queryString, bounds, filter).let { requestTask ->
            requestTask.addOnCompleteListener(
                executor,
                OnCompleteListener { responseTask ->
                    if (responseTask.isSuccessful) {
                        processAutocompletePrediction(responseTask.result)
                    } else {
                        "⚠️ Task failed with exception ${responseTask.exception}".log()
                    }
                }
            )
        }
    }

    // This runs in a background thread.
    private fun processAutocompletePrediction(buffer: AutocompletePredictionBufferResponse) {
        val count = buffer.count

        if (count == 0) {
            "⚠️ No autocomplete predictions found".log()
            return
        }

        val outputList: MutableList<AutocompletePredictionData> = mutableListOf()

        for (index in 0 until count) {
            val item = buffer.get(index)
            outputList.add(AutocompletePredictionData(
                placeId = item.placeId,
                placeTypes = item.placeTypes,
                fullText = item.getFullText(null),
                primaryText = item.getPrimaryText(null),
                secondaryText = item.getSecondaryText(null)
            ))
        }

        // Dump the list of AutocompletePrediction objects to logcat.
        outputList.joinToString("\n").log()

        // Update the LiveData, so observables can react to this change.
        autocompletePredictionLiveData.postValue(outputList)

    }

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        if (isPermissionGranted(context, ACCESS_FINE_LOCATION)) {
            "PlacesAPI ⇢ FusedLocationProviderClient.lastLocation() ✅".log()
            currentLocationClient.lastLocation.let { requestTask ->
                // Run this in background thread
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

    // This runs in a background thread.
    private fun processCurrentLocation(value: Location) {
        currentLocationLiveData.postValue(value)
    }

}