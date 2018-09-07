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
import android.os.Bundle
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceLikelihood
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.GsonBuilder
import java.util.*
import kotlin.collections.HashMap

/*
    Sample data for PlaceWrapper object converted to JSON.

    {
        "likelihood": 0.95,
        "id": "ChIJoxJh_Ue7j4AR2256AbvEuKQ",
        "placeTypes": [
            69,
            1013,
            34
        ],
        "address": "268 Waverley St, Palo Alto, CA 94301, USA",
        "locale": "en_US",
        "name": "Johnson Park",
        "latLng": {
            "latitude": 37.4493795,
            "longitude": -122.16315629999998
        },
        "viewport": {
            "northeast": {
                "latitude": 37.4507458302915,
                "longitude": -122.16179314999997
            },
            "southwest": {
                "latitude": 37.4480478697085,
                "longitude": -122.16483615000001
            }
        },
        "websiteUri": {},
        "phoneNumber": "+1 650-329-2100",
        "rating": 4.4,
        "priceLevel": -1
    }
 */
data class PlaceWrapper(val place: Place, val confidence: Float = 1f) {
    constructor(placeLikelihood: PlaceLikelihood) :
            this(placeLikelihood.place, placeLikelihood.likelihood)

    val map: HashMap<String, Any?> = importFrom(place, confidence, this)
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
    lateinit var placeObject: Place

    companion object {
        /**
         * 1. Freeze the given [PlaceLikelihood] object's [Place]. This ensures that the
         *    this object will be available after the [PlaceLikelihoodBufferResponse]
         *    is released.
         * 2. Wrap the [Place] object using a [Map] that is easy via [PlaceWrapper].
         */
        private fun importFrom(place: Place,
                               confidence: Float,
                               placeWrapper: PlaceWrapper): HashMap<String, Any?> {
            return HashMap<String, Any?>().also { map ->
                map["likelihood"] = confidence
                // Make sure to get an instance from freeze() that will be available
                // after the buffer is released.
                place.freeze().apply {
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
                    placeWrapper.placeObject = this
                }
            }
        }
    }

    fun getBundle(key: String): Bundle {
        return Bundle().apply {
            putSerializable(key, map)
        }
    }

    override fun toString(): String {
        return GsonBuilder().setPrettyPrinting().create().toJson(map)
    }

}

data class AutocompletePredictionData(val fullText: CharSequence,
                                      val primaryText: CharSequence,
                                      val secondaryText: CharSequence,
                                      val placeId: String?,
                                      val placeTypes: List<Int>?)