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

package com.google.api.places.places_api_poc.misc

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.api.places.places_api_poc.daggger.MyApplication
import com.google.api.places.places_api_poc.ui.BaseTabFragment
import com.google.api.places.places_api_poc.ui.DriverActivity
import java.io.ByteArrayOutputStream

//
// Bitmap helper.
//

fun bitmapToBundle(bitmap: Bitmap): Bundle {
    return Bundle().apply {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        stream.close()
        val byteArray = stream.toByteArray()
        putByteArray("image", byteArray)
    }
}

fun bundleToBitmap(bundle: Bundle, key: String): Bitmap {
    val byteArray = bundle.getByteArray(key)
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}


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

fun getUrl(lat: Double, lon: Double): String {
    return "https://maps.google.com/maps?q=$lat,$lon"
}

//
// Extension functions.
//

fun String.log() = Log.i("places-api-poc", this)

inline fun snack(view: View,
                 text: String = "",
                 duration: Int = Snackbar.LENGTH_SHORT,
                 functor: Snackbar.() -> Unit) {
    val snackbar: Snackbar = Snackbar.make(view, text, duration)
    snackbar.functor()
    snackbar.show()
}

inline fun toast(context: Context,
                 text: String = "",
                 duration: Int = Toast.LENGTH_SHORT,
                 functor: Toast.() -> Unit) {
    val toast: Toast = Toast.makeText(context, text, duration)
    toast.functor()
    toast.show()
}

fun BottomSheetDialogFragment.getMyApplication(): MyApplication =
        this.requireActivity().application as MyApplication

fun DriverActivity.getMyApplication(): MyApplication = this.application as MyApplication

fun Application.getMyApplication(): MyApplication = this as MyApplication

fun BaseTabFragment.getMyApplication(): MyApplication =
        this.requireActivity().application as MyApplication