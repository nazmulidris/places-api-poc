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

package com.google.api.places.places_api_poc.model

import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.PlaceLikelihood
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.api.places.places_api_poc.misc.log
import com.importre.crayon.red
import java.util.*

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
class PlaceWrapper(frozenPlace: Place, confidence: Float = 1f) {
    constructor(placeLikelihood: PlaceLikelihood) :
            this(placeLikelihood.place.freeze(), placeLikelihood.likelihood)

    val likelihood: Float = confidence
    val id: String = frozenPlace.id
    val placeTypes: List<Int> = frozenPlace.placeTypes
    val address: String? = frozenPlace.address?.toString()
    val locale: Locale = frozenPlace.locale
    val name: String = frozenPlace.name.toString()
    val latLng: LatLng = frozenPlace.latLng
    val viewport: LatLngBounds? = frozenPlace.viewport
    val websiteUri: Uri? = frozenPlace.websiteUri
    val phoneNumber: String? = frozenPlace.phoneNumber?.toString()
    val rating: Float = frozenPlace.rating
    val priceLevel: Int = frozenPlace.priceLevel
    val attributions: String? = frozenPlace.attributions?.toString()

    override fun toString(): String {
        return "PlaceWrapper(likelihood=$likelihood, id='$id', placeTypes=$placeTypes, " +
                "address=$address, locale=$locale, name='$name', latLng=$latLng, " +
                "viewport=$viewport, websiteUri=$websiteUri, phoneNumber=$phoneNumber, " +
                "rating=$rating, priceLevel=$priceLevel, attributions=$attributions)"
    }

    fun toHtmlString(): String {
        val map: MutableMap<String, String> = mutableMapOf()

        map["likelihood"] = "%.2f".format(likelihood)
        map["id"] = id
        map["placeTypes"] = placeTypes.joinToString(",", "{", "}")
        map["address"] = address ?: "n/a"
        map["locale"] = locale.toString()
        map["name"] = name
        map["latLng"] = latLng.toString()
        map["viewport"] = viewport.toString()
        map["websiteUri"] = websiteUri.toString()
        map["phoneNumber"] = phoneNumber ?: "n/a"
        map["rating"] = "%.2f".format(rating)
        map["priceLevel"] = priceLevel.toString()
        map["attributions"] = attributions ?: "n/a"

        fun generateValueString(value: Any?) =
                when {
                    value == null -> "n/a"
                    value.toString().trim().isEmpty() -> "n/a"
                    else -> value.toString()
                }

        return StringBuilder().apply {
            map.forEach { prop ->
                val name = prop.key
                val value = prop.value
                val valueDisplayString = generateValueString(value)
                append("<br/><b>$name</b><br/>")
                append("<code>$valueDisplayString<code><br/>")
            }
        }.toString()
    }
}

fun AutocompletePrediction.parse(): AutocompletePredictionData {
    if (getFullText(null).isNullOrEmpty() || getPrimaryText(null).isNullOrEmpty() ||
            getSecondaryText(null).isNullOrEmpty() || placeId.isNullOrEmpty() ||
            placeTypes == null) {
        (StringBuilder()).apply {
            append("⚠️ AutocompletePrediction - some fields are null or empty".red())
            append("\nfullText: '${getFullText(null)}'")
            append("\nprimaryText: '${getPrimaryText(null)}'")
            append("\nsecondaryText: '${getSecondaryText(null)}'")
            append("\nplaceId: '$placeId'")
            append("\nplaceTypes: '$placeTypes'")
        }.toString().log()
    }

    return AutocompletePredictionData(
            fullText = getFullText(null) ?: "",
            primaryText = getPrimaryText(null) ?: "",
            secondaryText = getSecondaryText(null) ?: "",
            placeId = placeId ?: "",
            placeTypes = placeTypes ?: listOf()
    )
}

data class AutocompletePredictionData(val fullText: CharSequence,
                                      val primaryText: CharSequence,
                                      val secondaryText: CharSequence,
                                      val placeId: String,
                                      val placeTypes: List<Int>)

data class BitmapWrapper(val bitmap: Bitmap? = null, val attribution: String? = null)