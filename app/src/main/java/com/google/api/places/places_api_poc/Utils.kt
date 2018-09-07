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

import android.content.Context
import android.location.Location
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.snackbar.Snackbar

//
// Location helper classes.
//

class LatLngRange {
    /** [More info on Wikipedia](https://en.wikipedia.org/wiki/Decimal_degrees) */
    enum class Range(val decimalDegrees: Double) {
        Country(1.0),
        LargeCityOoDistrict(0.1),
        TownOrVillage(0.01),
        NeighborhoodOrStreet(0.001),
        IndvidualStreetOrLandParcel(0.0001),
        DoorEntranceOrIndvidualTree(0.00001),
        IndividualHumans(0.000001)
    }

    companion object {
        /** [More Info](https://stackoverflow.com/a/32368196/2085356) */
        fun getBounds(location: Location, range: Range = Range.NeighborhoodOrStreet): LatLngBounds {
            val radiusDegrees = range.decimalDegrees
            val center = LatLng(location.latitude, location.longitude)
            val northEast = LatLng(center.latitude + radiusDegrees,
                                   center.longitude + radiusDegrees)
            val southWest = LatLng(center.latitude - radiusDegrees,
                                   center.longitude - radiusDegrees)
            return LatLngBounds.builder()
                    .include(northEast)
                    .include(southWest)
                    .build()
        }
    }

}


//
// Extension functions.
//

fun String.log() = Log.i("places-api-poc", this)

fun String.snack(view: View) = Snackbar.make(view, this, Snackbar.LENGTH_SHORT).show()

fun String.toast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_SHORT).show()