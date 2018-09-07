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

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

class Tab2Fragment : BaseTabFragment() {

    private lateinit var fragmentContainer: CoordinatorLayout
    private lateinit var textInputQuery: EditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_tab2, container, false)
        with(layout) {
            fragmentContainer = findViewById(R.id.layout_tab2_root)
            textInputQuery = findViewById(R.id.text_input_query)
        }
        return layout
    }


    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(inputString: Editable) {
            respondToTextChange(inputString)
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }

    override fun onFragmentCreate() {
        observeLocationLiveData()
    }

    fun observeLocationLiveData() {
        placesAPIViewModel.currentLocationLiveData.observe(
            this@Tab2Fragment,
            Observer { location ->
                "📌 Current Location = $location".log()

                val bounds = getBounds(location)
                "$bounds".log()

                "Current: ${getUrl(location.latitude, location.longitude)}".log()
                "NorthEast: ${getUrl(bounds.northeast.latitude, bounds.northeast.longitude)}".log()
                "SouthWest: ${getUrl(bounds.southwest.latitude, bounds.southwest.longitude)}".log()
            })


    }

    fun getUrl(lat: Double, lon: Double): String {
        return "https://maps.google.com/maps?q=$lat,$lon"
        //return "https://www.latlong.net/c/?lat=$lat&long=$lon\n"
    }

    fun getLocation() {
        getParentActivity().executeTaskOnPermissionGranted(
            object : DriverActivity.PermissionDependentTask {
                override fun getRequiredPermission() =
                        Manifest.permission.ACCESS_FINE_LOCATION

                override fun onPermissionGranted() {
                    placesAPIViewModel.getLastLocation()
                    "🚀️ Calling FusedLocationProvider lastLocation()".snack(
                        fragmentContainer)
                }

                override fun onPermissionRevoked() {
                    "🛑 This app will not function without ${getRequiredPermission()}"
                            .snack(fragmentContainer)
                }
            })
    }

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

    /** [More Info](https://stackoverflow.com/a/32368196/2085356) */
    fun getBounds(location: Location, range: Range = Range.NeighborhoodOrStreet): LatLngBounds {
        val radiusDegrees = range.decimalDegrees
        val center = LatLng(location.latitude, location.longitude)
        val northEast = LatLng(center.latitude + radiusDegrees, center.longitude + radiusDegrees)
        val southWest = LatLng(center.latitude - radiusDegrees, center.longitude - radiusDegrees)
        return LatLngBounds.builder()
                .include(northEast)
                .include(southWest)
                .build()
    }

    override fun onStart() {
        super.onStart()
        getLocation()
        textInputQuery.addTextChangedListener(textChangeListener)
    }

    override fun onStop() {
        super.onStop()
        textInputQuery.removeTextChangedListener(textChangeListener)
    }

    private fun respondToTextChange(inputString: Editable) {
        "🔤 $inputString".toast(getParentActivity())
    }

}
