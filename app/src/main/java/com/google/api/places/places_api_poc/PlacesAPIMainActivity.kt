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

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class PlacesAPIMainActivity : AppCompatActivity(), AnkoLogger {

    private lateinit var placesAPIViewModel: PlacesAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the ViewModel which acts as my proxy to the Places API client(s)
        placesAPIViewModel = ViewModelProviders.of(this).get(PlacesAPI::class.java)

        // Connect to the Places API
        lifecycle.addObserver(placesAPIViewModel)

        // Attach LiveData observers for place_picker_text
        placesAPIViewModel.placePickerData.observe(this, Observer {
            this.place_picker_text.text = it ?: "n/a"
        })

        // Attach LiveData observers for place_picker_text
        placesAPIViewModel.currentPlaceData.observe(this, Observer {
            this.current_place_text.text = it ?: "n/a"
        })

        // Connect the buttons
        button_get_current_place.onClick {
            requestPermissionAndGetCurrentPlace(placesAPIViewModel)
        }

    }

    // Constant required when dealing with asking user for permission grant
    val REQUEST_ACCESS_FINE_LOCATION_FOR_GET_CURRENT_PLACE = 1234

    private fun requestPermissionAndGetCurrentPlace(placesAPIViewModel: PlacesAPI) {
        if (isPermissionDenied(this, ACCESS_FINE_LOCATION)) {
            // Permission is not granted ☹. Ask the user for the run time permission 🙏
            info { "PlacesAPI ⇢ ACCESS_FINE_LOCATION permission not granted 🛑, make request 🙏️" }
            requestPermission(this,
                              ACCESS_FINE_LOCATION,
                              REQUEST_ACCESS_FINE_LOCATION_FOR_GET_CURRENT_PLACE)
        } else {
            // Permission is granted 🙌
            actuallyGetCurrentPlace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION_FOR_GET_CURRENT_PLACE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, 🎉
                    actuallyGetCurrentPlace()
                } else {
                    // permission denied, ☹
                    toast("☠ This app will not function without this permission")
                }
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun actuallyGetCurrentPlace() {
        info { "PlacesAPI ⇢ Permission granted 🙌, PlaceDetectionClient.getCurrentPlace() ✅" }
        placesAPIViewModel.getCurrentPlace()
    }

}