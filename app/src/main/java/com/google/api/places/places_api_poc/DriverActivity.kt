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
import android.arch.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_driver.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class DriverActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        setupFragments()
        setupViewModel()
    }

    // Manage runtime permissions for ACCESS_FINE_LOCATION
    // Constant required when dealing with asking user for permission grant
    val PERMISSION_ID = 1234

    fun requestPermissionAndGetCurrentPlace() {
        if (isPermissionDenied(this, ACCESS_FINE_LOCATION)) {
            // Permission is not granted ☹. Ask the user for the run time permission 🙏
            info { "PlacesAPI ⇢ ACCESS_FINE_LOCATION permission not granted 🛑, make request 🙏️" }
            requestPermission(this, ACCESS_FINE_LOCATION, PERMISSION_ID)
        } else {
            // Permission is granted 🙌
            actuallyGetCurrentPlace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ID -> {
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
            else          -> {
                // Ignore all other requests.
            }
        }
    }

    private fun actuallyGetCurrentPlace() {
        info { "PlacesAPI ⇢ Permission granted 🙌, PlaceDetectionClient.getCurrentPlace() ✅" }
        placesAPIViewModel.getCurrentPlace()
    }

    // Manage ViewModels (which encapsulate Places API clients)
    private lateinit var placesAPIViewModel: PlacesAPI

    private fun setupViewModel() {
        // Create the ViewModel which acts as my proxy to the Places API client(s)
        placesAPIViewModel = ViewModelProviders.of(this).get(PlacesAPI::class.java)
        // Connect to the Places API
        lifecycle.addObserver(placesAPIViewModel)
    }

    // Manage creating and switching Fragments
    fun setupFragments() {
        // Enable bottom bar navigation to respond to user input
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Pre-select the first fragment
        switchFragment(R.id.navigation_tab1)
    }

    private val fragmentMap = mutableMapOf<Int, Fragment>().apply {
        put(R.id.navigation_tab1, Tab1Fragment())
        put(R.id.navigation_tab2, Tab2Fragment())
        put(R.id.navigation_tab3, Tab3Fragment())
    }

    private fun switchFragment(id: Int) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_fragmentholder,
                         fragmentMap[id])
                .addToBackStack(null)
                .commit()
    }

    // Handle user input on bottom bar navigation
    private val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                val id = item.itemId
                if (id in fragmentMap.keys) {
                    switchFragment(id)
                    return@OnNavigationItemSelectedListener true
                } else return@OnNavigationItemSelectedListener false
            }

}
