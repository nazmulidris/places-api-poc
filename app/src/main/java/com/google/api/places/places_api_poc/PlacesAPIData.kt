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

import android.net.Uri
import com.google.android.gms.location.places.PlaceLikelihood
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.util.*

data class PlaceWrapper(val placeLikelihood: PlaceLikelihood) {
    private val map: Map<String, Any?> = importFrom(placeLikelihood)
    val likelihood: Float by map
    val id: String by map
    val placeTypes: List<Int> by map
    val address: String? by map
    val locale: Locale by map
    val name: String by map
    val latLng: LatLng by map
    val viewport: LatLngBounds? by map
    val websiteUri: Uri? by map
    val phoneNumber: String? by map
    val rating: Float by map
    val priceLevel: Int by map
    val attributions: String? by map

    companion object {
        /**
         * 1. Freeze the given [PlaceLikelihood] object's [Place]. This ensures that the
         *    this object will be available after the [PlaceLikelihoodBufferResponse]
         *    is released.
         * 2. Wrap the [Place] object using a [Map] that is easy via [PlaceWrapper].
         */
        fun importFrom(placeLikelihood: PlaceLikelihood): Map<String, Any?> {
            return LinkedHashMap<String, Any?>().also { map ->
                map["likelihood"] = placeLikelihood.likelihood
                // Make sure to get an instance from freeze() that will be available
                // after the buffer is released.
                placeLikelihood.place.freeze().apply {
                    map["id"] = id
                    map["placeTypes"] = placeTypes
                    map["address"] = address
                    map["locale"] = locale
                    map["name"] = name
                    map["latLng"] = latLng
                    map["viewport"] = viewport
                    map["websiteUri"] = websiteUri
                    map["phoneNumber"] = phoneNumber
                    map["rating"] = rating
                    map["priceLevel"] = priceLevel
                    map["attributions"] = attributions
                }
            }
        }
    }

    override fun toString(): String {
        return map.values.joinToString(",", "📌 {", "}")
    }

}