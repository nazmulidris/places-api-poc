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

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tab1.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.sdk25.coroutines.onClick

class Tab1Fragment : Fragment(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    // Access shared ViewModel
    private lateinit var placesAPIViewModel: PlacesAPI

    private fun setupViewModel() {
        // Load ViewModel
        // 🛑 Note - You **must** pass activity scope, in order to get this ViewModel, and if you
        // pass the fragment instance, then you won't get the ViewModel that was attached w/ the
        // parent activity (DriverActivity)
        placesAPIViewModel = ViewModelProviders.of(activity).get(PlacesAPI::class.java)
    }

    // Inflate the layout
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab1, container, false)

    }

    // Access parent activity (DriverActivity)
    private var parentActivity: DriverActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as DriverActivity
    }

    override fun onDetach() {
        super.onDetach()
        parentActivity = null
    }

    // Bind things to the UI
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachToUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        placesAPIViewModel.currentPlaceData.removeObservers(this)
        info { "🛑 removing observers" }
    }

    private fun attachToUI() {
        // Attach a behavior to the button
        button_current_place_fragment.onClick {
            parentActivity?.requestPermissionAndGetCurrentPlace()
        }
        // Attach LiveData observers for current_place_text
        placesAPIViewModel.currentPlaceData.observe(this, Observer {
            info { "🎉observable reacting -> $it" }
            current_place_text_fragment.text = it ?: "n/a"
        })
    }

}
