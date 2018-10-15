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

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.api.places.places_api_poc.daggger.PlacesLiveData
import com.google.api.places.places_api_poc.misc.ExecutorWrapper
import com.google.api.places.places_api_poc.misc.isPermissionGranted
import com.google.api.places.places_api_poc.misc.log
import com.google.api.places.places_api_poc.misc.safelyProcess
import com.google.api.places.places_api_poc.model.PlaceWrapper

class GetCurrentPlaceService
constructor(private val executorWrapper: ExecutorWrapper,
            private val context: Context,
            private val currentPlaceClient: PlaceDetectionClient,
            private val liveData: PlacesLiveData) {

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun execute() {
        if (isPermissionGranted(context,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Permission is granted 🙌.
            "PlacesAPI ⇢ PlaceDetectionClient.getCurrentPlace() ✅".log()
            currentPlaceClient.getCurrentPlace(null).let { requestTask ->
                // Run this in background thread.
                requestTask.addOnCompleteListener(
                        executorWrapper.executor,
                        OnCompleteListener { responseTask ->
                            responseTask.safelyProcess(
                                    {
                                        processPlacelikelihoodBuffer(this)
                                        release()
                                    },
                                    {
                                        "⚠️ Task failed with exception $exception".log()
                                    }
                            )

/*
                            if (responseTask.isSuccessful) {
                                if (responseTask.result != null){
                                    processPlacelikelihoodBuffer(responseTask.result!!)
                                    responseTask.result!!.release()
                                }
                            } else {
                                "⚠️ Task failed with exception ${responseTask.exception}".log()
                            }
*/
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
            outputList.add(PlaceWrapper(likeyPlaces.get(
                    index)))
        }

        // Dump the list of PlaceWrapper objects to logcat.
        outputList.joinToString("\n").log()

        // Update the LiveData, so observables can react to this change.
        liveData.postValue(outputList)
    }

}