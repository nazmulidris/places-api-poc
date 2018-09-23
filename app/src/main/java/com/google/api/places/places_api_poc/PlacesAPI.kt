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
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.OnCompleteListener
import com.google.api.places.places_api_poc.daggger.ExecutorWrapper
import com.google.api.places.places_api_poc.daggger.MyApplication
import java.util.concurrent.ExecutorService
import javax.inject.Inject


class PlacesAPI(val app: Application) : AndroidViewModel(app), LifecycleObserver {

    // Places API Clients.
    @Inject
    lateinit var currentPlaceClient: PlaceDetectionClient
    @Inject
    lateinit var geoDataClient: GeoDataClient

    // Fused Location Provider Client.
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // Background Executor.
    @Inject
    lateinit var executorWrapper: ExecutorWrapper

    // Find Last Location.
    lateinit var getLastLocation: GetLastLocation

    // Find Current Place.
    lateinit var getCurrentPlace: GetCurrentPlace

    // Fetch Place by ID.
    lateinit var getPlaceByID: GetPlaceByID

    // Fetch Autocomplete Predictions.
    lateinit var autoCompletePredictions: AutoCompletePredictions

    // Get Place Photos
    lateinit var getPlacePhotos: GetPlacePhotos

    // Get Photo.
    lateinit var getPhoto: GetPhoto

    // Modal "Place Details Sheet" Data.
    val modalPlaceDetailsSheetLiveData = ModalPlaceDetailsSheetLiveData()

    //
    // Activity lifecycle.
    //

    // Lifecycle hooks.
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun connect() {
        "ON_CREATE ⇢ PlacesAPI.connect() ✅".log()

        // Dagger 2 component creation.
        with((app as MyApplication)) {
            createActivityComponent()?.inject(this@PlacesAPI)
        }

        "💥 connect() - got GetDataClient, PlaceDetectionClient, FusedLocationProviderClient".log()

        "ON_CREATE ⇢ Create Executor ✅".log()
        executorWrapper.create()

        "ON_CREATE ⇢ Create API wrappers ✅".log()
        getCurrentPlace = GetCurrentPlace(executorWrapper.executor,
                                          app,
                                          currentPlaceClient)
        getPlaceByID = GetPlaceByID(executorWrapper.executor,
                                    geoDataClient,
                                    modalPlaceDetailsSheetLiveData)
        autoCompletePredictions = AutoCompletePredictions(executorWrapper.executor,
                                                          geoDataClient)
        getLastLocation = GetLastLocation(executorWrapper.executor,
                                          fusedLocationProviderClient,
                                          app)
        getPhoto = GetPhoto(executorWrapper.executor,
                            geoDataClient,
                            modalPlaceDetailsSheetLiveData)
        getPlacePhotos = GetPlacePhotos(executorWrapper.executor,
                                        geoDataClient,
                                        getPhoto)
        "💥 connect() - complete!".log()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun cleanup() {
        "ON_DESTROY ⇢ PlacesAPI cleanup ✅".log()
        executorWrapper.destroy()
        "🚿 cleanup() - complete!".log()
        with((app as MyApplication)) {
            destroyActivityComponent()
        }
    }

}

//
// Get Photo.
//

class GetPhoto(private val executor: ExecutorService,
               private val geoDataClient: GeoDataClient,
               private val modalPlaceDetailsSheetLiveData: ModalPlaceDetailsSheetLiveData) {

    fun execute(photoMetadata: PlacePhotoMetadata, attribution: CharSequence) {
        // Get a full-size bitmap for the photo.
        "PlacesAPI ⇢ GeoDataClient.getPhoto() ✅".log()
        geoDataClient.getPhoto(photoMetadata).let { requestTask ->
            requestTask.addOnCompleteListener(
                    executor,
                    OnCompleteListener { responseTask ->
                        if (responseTask.isSuccessful) {
                            processPhoto(responseTask.result.bitmap, attribution)
                        } else {
                            "⚠️ Task failed with exception ${responseTask.exception}".log()
                        }
                    }
            )
        }
    }

    private fun processPhoto(bitmap: Bitmap, attribution: CharSequence) {
        modalPlaceDetailsSheetLiveData.bitmap.postValue(
                BitmapWrapper(bitmap, attribution.toString())
        )
    }

}

//
// Get Place Photos.
//

class GetPlacePhotos(private val executor: ExecutorService,
                     private val geoDataClient: GeoDataClient,
                     private val getPhoto: GetPhoto) {

    fun execute(placeId: String) {
        "PlacesAPI ⇢ GeoDataClient.getPlacePhotos() ✅".log()
        // Run this in background thread.
        geoDataClient.getPlacePhotos(placeId).let { requestTask ->
            requestTask.addOnCompleteListener(
                    executor,
                    OnCompleteListener { responseTask ->
                        if (responseTask.isSuccessful) {
                            processPhotosMetadata(responseTask.result)
                        } else {
                            "⚠️ Task failed with exception ${responseTask.exception}".log()
                        }
                    }
            )
        }

    }

    // This runs in a background thread.
    private fun processPhotosMetadata(photos: PlacePhotoMetadataResponse) {

        // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
        val photoMetadataBuffer = photos.photoMetadata

        val count = photoMetadataBuffer.count

        if (count > 0) {
            // Get the first photo in the list.
            val photoMetadata = photoMetadataBuffer.get(0)

            // Get the attribution text.
            val attribution = photoMetadata.attributions

            // Actually get the photo.
            getPhoto.execute(photoMetadata, attribution)
        }

    }

}

//
// Get Last Location.
//

class GetLastLocation(private val executor: ExecutorService,
                      private val currentLocationClient: FusedLocationProviderClient,
                      private val context: Application) {

    val liveData = MutableLiveData<Location>()

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun execute() {
        if (isPermissionGranted(context, ACCESS_FINE_LOCATION)) {
            "PlacesAPI ⇢ FusedLocationProviderClient.lastLocation() ✅".log()
            currentLocationClient.lastLocation.let { requestTask ->
                // Run this in background thread.
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
        liveData.postValue(value)
    }

}

//
// Place Autocomplete.
//

class AutoCompletePredictions(private val executor: ExecutorService,
                              private val geoDataClient: GeoDataClient) {

    val liveData = MutableLiveData<List<AutocompletePredictionData>>()

    private val defaultFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
            .build()

    fun execute(queryString: String,
                bounds: LatLngBounds,
                filter: AutocompleteFilter = defaultFilter) {
        "PlacesAPI ⇢ GeoDataClient.getAutocompletePredictions() ✅".log()
        geoDataClient.getAutocompletePredictions(queryString, bounds, filter)
                .let { requestTask ->
                    // Run this in background thread.
                    requestTask.addOnCompleteListener(
                            executor,
                            OnCompleteListener { responseTask ->
                                if (responseTask.isSuccessful) {
                                    processAutocompletePrediction(responseTask.result)
                                    responseTask.result.release()
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
        liveData.postValue(outputList)

    }

}

//
// Place IDs and Details.
//

class GetPlaceByID(private val executor: ExecutorService,
                   private val geoDataClient: GeoDataClient,
                   private val modalPlaceDetailsSheetData: ModalPlaceDetailsSheetLiveData) {

    fun execute(placeId: String) {
        "PlacesAPI ⇢ GeoDataClient.getPlaceById() ✅".log()
        geoDataClient.getPlaceById(placeId).let { requestTask ->
            // Run this in background thread.
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

    // This runs in a background thread.
    private fun processPlace(placeBufferResponse: PlaceBufferResponse) {
        val place = placeBufferResponse.get(0)
        modalPlaceDetailsSheetData.postPlace(PlaceWrapper(place))
    }

}

//
// Modal "Place Details Sheet" Data.
//

data class ModalPlaceDetailsSheetLiveData(
        val bitmap: MutableLiveData<BitmapWrapper> = MutableLiveData(),
        /** [place] is private, because it's changes [bitmap] & [sheetVisible]. */
        private val place: MutableLiveData<PlaceWrapper> = MutableLiveData(),
        /** [sheetVisible] is private, because it depends on [place]. */
        private val sheetVisible: MutableLiveData<Boolean> = MutableLiveData()) {
    /** This is called from the main thread. When a new place is set, then bitmap
     * is cleared, and the visibility is set to true, so that the place details sheet
     * will appear. This is driven by [DriverActivity].*/
    fun setPlace(value: PlaceWrapper) {
        place.value = value
        bitmap.value = BitmapWrapper()
        sheetVisible.value = true
    }

    /** This is called from a background thread. When a new place is set, then bitmap
     * is cleared, and the visibility is set to true, so that the place details sheet
     * will appear. This is driven by [DriverActivity] */
    fun postPlace(value: PlaceWrapper) {
        place.postValue(value)
        bitmap.postValue(BitmapWrapper())
        sheetVisible.postValue(true)
    }

    fun placeObservable(): MutableLiveData<PlaceWrapper> {
        return place
    }

    fun sheetVisibleObservable(): MutableLiveData<Boolean> {
        return sheetVisible
    }
}

//
// Current Place.
//

class GetCurrentPlace(private val executor: ExecutorService,
                      private val context: Context,
                      private val currentPlaceClient: PlaceDetectionClient) {

    val liveData = MutableLiveData<List<PlaceWrapper>>()

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun execute() {
        if (isPermissionGranted(context, ACCESS_FINE_LOCATION)) {
            // Permission is granted 🙌.
            "PlacesAPI ⇢ PlaceDetectionClient.getCurrentPlace() ✅".log()
            currentPlaceClient.getCurrentPlace(null).let { requestTask ->
                // Run this in background thread.
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
        liveData.postValue(outputList)
    }

}