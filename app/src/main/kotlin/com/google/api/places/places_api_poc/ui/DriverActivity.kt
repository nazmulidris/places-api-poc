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

package com.google.api.places.places_api_poc.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.api.places.places_api_poc.R
import com.google.api.places.places_api_poc.daggger.PlaceDetailsSheetLiveData
import com.google.api.places.places_api_poc.misc.PermissionDependentTask
import com.google.api.places.places_api_poc.misc.PermissionsHandler
import com.google.api.places.places_api_poc.misc.getMyApplication
import com.google.api.places.places_api_poc.model.PlacesAPI
import javax.inject.Inject

class DriverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        setupFragments()
        setupViewModel()
        setupModalPlaceDetailSheetHandler()
    }

    fun executeTaskOnPermissionGranted(task: PermissionDependentTask) {
        PermissionsHandler.executeTaskOnPermissionGranted(this, task)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        PermissionsHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                put(R.id.navigation_tab1,
                    Tab1Fragment())
                put(R.id.navigation_tab2,
                    Tab2Fragment())
                put(R.id.navigation_tab3,
                    Tab3Fragment())
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

    @Inject
    lateinit var modalPlaceDetailsSheetLiveData: PlaceDetailsSheetLiveData

    // Handle showing the PlaceDetailsSheetFragment (modal/dialog).
    private fun setupModalPlaceDetailSheetHandler() {

        // Inject objects into fields.
        getMyApplication().activityComponent?.inject(this)

        modalPlaceDetailsSheetLiveData.sheetVisibleObservable().observe(
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