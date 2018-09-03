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

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

open class BaseTabFragment : Fragment() {

    // Access shared ViewModel
    lateinit var placesAPIViewModel: PlacesAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {
        // Load ViewModel.
        // 🛑 Note - You **must** pass activity scope, in order to get this ViewModel,
        // and if you pass the fragment instance, then you won't get the ViewModel that
        // was attached w/ the parent activity (DriverActivity).
        placesAPIViewModel = ViewModelProviders.of(requireActivity()).get(PlacesAPI::class.java)
    }

    // Access parent activity (DriverActivity).
    fun getParentActivity(): DriverActivity {
        return requireActivity() as DriverActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        attachToUI()
    }

    open fun attachToUI() {}

}