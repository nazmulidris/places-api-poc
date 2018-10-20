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

import androidx.annotation.WorkerThread
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.maps.model.LatLngBounds
import com.google.api.places.places_api_poc.daggger.AutocompletePredictionsLiveData
import com.google.api.places.places_api_poc.misc.ExecutorWrapper
import com.google.api.places.places_api_poc.misc.log
import com.google.api.places.places_api_poc.model.AutocompletePredictionData
import com.google.api.places.places_api_poc.model.parse

class GetAutocompletePredictionsService
constructor(private val executorWrapper: ExecutorWrapper,
            private val geoDataClient: GeoDataClient,
            private val liveData: AutocompletePredictionsLiveData) {

    private val defaultFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
            .build()

    fun execute(queryString: String,
                bounds: LatLngBounds,
                filter: AutocompleteFilter = defaultFilter) {
        "PlacesAPI ⇢ GeoDataClient.getAutocompletePredictions() ✅".log()
        geoDataClient.getAutocompletePredictions(queryString, bounds, filter)
                .handleResponse(executorWrapper.executor) { response ->
                    when (response) {
                        is ServiceResponse.Success -> {
                            processAutocompletePrediction(response.value)
                            response.value.release()
                        }
                        is ServiceResponse.Error -> {
                            "⚠️ Task failed with exception ${response.exception}".log()
                        }
                    }
                }
    }

    @WorkerThread
    private fun processAutocompletePrediction(buffer: AutocompletePredictionBufferResponse) {
        val count = buffer.count

        if (count == 0) {
            "⚠️ No autocomplete predictions found".log()
            return
        }

        val outputList: MutableList<AutocompletePredictionData> = mutableListOf()

        for (index in 0 until count) {
            val item: AutocompletePrediction = buffer.get(index)
            outputList.add(item.parse())
        }

        // Dump the list of AutocompletePrediction objects to logcat.
        outputList.joinToString(prefix = "{\n📌", separator = "\n📌", postfix = "\n}").log()

        // Update the LiveData, so observables can react to this change.
        liveData.postValue(outputList)

    }

}