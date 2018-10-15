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
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.api.places.places_api_poc.daggger.LocationLiveData
import com.google.api.places.places_api_poc.misc.ExecutorWrapper
import com.google.api.places.places_api_poc.misc.isPermissionGranted
import com.google.api.places.places_api_poc.misc.log
import com.google.api.places.places_api_poc.misc.safelyProcess

class GetLastLocationService
constructor(private val executorWrapper: ExecutorWrapper,
            private val currentLocationClient: FusedLocationProviderClient,
            private val context: Context,
            private val liveData: LocationLiveData) {

    /**
     * This function won't execute if FINE_ACCESS_LOCATION permission is not granted.
     */
    @SuppressLint("MissingPermission")
    fun execute() {
        if (isPermissionGranted(context,
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
            "PlacesAPI ⇢ FusedLocationProviderClient.lastLocation() ✅".log()
            currentLocationClient.lastLocation.let { requestTask ->
                // Run this in background thread.
                requestTask.addOnCompleteListener(
                        executorWrapper.executor,
                        OnCompleteListener { responseTask ->
                            responseTask.safelyProcess(
                                    {
                                        processCurrentLocation(this)
                                    },
                                    {
                                        "⚠️ Task failed with exception $exception".log()
                                    }
                            )
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