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

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView

class DriverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        setupFragments()
        setupViewModel()
        setupModalPlaceDetailSheetHandler()
    }

    // Manage runtime permissions for ACCESS_FINE_LOCATION.
    // Constant required when dealing with asking user for permission grant.
    val PERMISSION_ID = 1234

    // Holds one pending task that will be run if permission is granted.
    private var pendingTask: PermissionDependentTask? = null

    fun executeTaskOnPermissionGranted(task: PermissionDependentTask) {
        if (isPermissionDenied(this, task.getRequiredPermission())) {
            // Permission is not granted ☹. Ask the user for the run time permission 🙏.
            "🔒 ${task.getRequiredPermission()} not granted 🛑, request it 🙏️".log()
            requestPermission(this, task.getRequiredPermission(), PERMISSION_ID)
            if (pendingTask == null) pendingTask = task
        } else {
            // Permission is granted 🙌. Run the task function.
            "🔒 ${task.getRequiredPermission()} permission granted 🙌, Execute pendingTask ".log()
            task.onPermissionGranted()
        }
    }

    // Simple interface to perform a task that requires a permission.
    interface PermissionDependentTask {
        fun getRequiredPermission(): String
        fun onPermissionGranted()
        fun onPermissionRevoked()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ID -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED)) {
                    // Permission was granted, 🎉. Run the pending task function.
                    if (pendingTask != null) {
                        "🔒 Permission is granted 🙌, Execute pendingTask".log()
                        pendingTask?.onPermissionGranted()
                        pendingTask = null
                    }
                } else {
                    // Permission denied, ☹.
                    pendingTask?.onPermissionRevoked()
                }
                return
            }
            // Add other 'when' lines to check for other permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private lateinit var placesAPIViewModel: PlacesAPI

    private fun setupViewModel() {
        // Create the ViewModel which acts as a proxy to the Places API client(s).
        placesAPIViewModel = ViewModelProviders.of(this).get(PlacesAPI::class.java)
        // Connect to the Places API.
        lifecycle.addObserver(placesAPIViewModel)
    }

    // Manage creating and switching Fragments.
    private fun setupFragments() {
        // Enable bottom bar navigation to respond to user input.
        findViewById<BottomNavigationView>(R.id.layout_app_navigation)
                .setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        // Pre-select the first fragment.
        switchFragment(R.id.navigation_tab1)
    }

    private val fragmentMap: Map<Int, Fragment> =
            mutableMapOf<Int, Fragment>().apply {
                put(R.id.navigation_tab1, Tab1Fragment())
                put(R.id.navigation_tab2, Tab2Fragment())
                put(R.id.navigation_tab3, Tab3Fragment())
            }.toMap()

    private fun switchFragment(id: Int) {
        fragmentMap[id]?.let { newFragment ->
            supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.layout_app_fragment, newFragment)
                    // To allow user to use back button to undo fragment switch uncomment this.
                    //.addToBackStack(null)
                    .commit()
        }
    }

    // Handle user input on bottom bar navigation.
    private val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                val id = item.itemId
                if (id in fragmentMap.keys) {
                    switchFragment(id)
                    return@OnNavigationItemSelectedListener true
                } else return@OnNavigationItemSelectedListener false
            }

    // Handle showing the PlaceDetailsSheetFragment (modal/dialog).
    private fun setupModalPlaceDetailSheetHandler() {

        placesAPIViewModel.modalPlaceDetailsSheetLiveData.sheetVisibleObservable().observe(
            this,
            Observer { showFlag ->
                if (showFlag) {
                    PlaceDetailsSheetFragment()
                            .show(this.supportFragmentManager,
                                  PlaceDetailsSheetFragment::javaClass.name)

                } else {
                    // Don't do anything.
                }
            }
        )

    }

}