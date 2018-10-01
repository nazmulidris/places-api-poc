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

package com.google.api.places.places_api_poc.service

import android.graphics.Bitmap
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlacePhotoMetadata
import com.google.android.gms.location.places.PlacePhotoMetadataResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.api.places.places_api_poc.daggger.PlaceDetailsSheetLiveData
import com.google.api.places.places_api_poc.misc.ExecutorWrapper
import com.google.api.places.places_api_poc.misc.log
import com.google.api.places.places_api_poc.model.BitmapWrapper

class GetPhotoService
constructor(private val executorWrapper: ExecutorWrapper,
            private val geoDataClient: GeoDataClient,
            private val modalPlaceDetailsSheetLiveData: PlaceDetailsSheetLiveData) {

    fun execute(photoMetadata: PlacePhotoMetadata, attribution: CharSequence) {
        // Get a full-size bitmap for the photo.
        "PlacesAPI ⇢ GeoDataClient.getPhoto() ✅".log()
        geoDataClient.getPhoto(photoMetadata).let { requestTask ->
            requestTask.addOnCompleteListener(
                    executorWrapper.executor,
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
                BitmapWrapper(bitmap,
                              attribution.toString())
        )
    }

}

class GetPlacePhotosService
constructor(private val executorWrapper: ExecutorWrapper,
            private val geoDataClient: GeoDataClient,
            private val getPhoto: GetPhotoService) {

    fun execute(placeId: String) {
        "PlacesAPI ⇢ GeoDataClient.getPlacePhotos() ✅".log()
        // Run this in background thread.
        geoDataClient.getPlacePhotos(placeId).let { requestTask ->
            requestTask.addOnCompleteListener(
                    executorWrapper.executor,
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