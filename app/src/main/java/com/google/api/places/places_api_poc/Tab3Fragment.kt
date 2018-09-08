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
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer

class Tab3Fragment : BaseTabFragment() {

    private lateinit var fragmentContainer: CoordinatorLayout
    private lateinit var textDebugCurrentPlace: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment.
        val layout = inflater.inflate(R.layout.fragment_tab3, container, false)
        with(layout) {
            fragmentContainer = findViewById(R.id.layout_tab3_root)
            textDebugCurrentPlace = findViewById(R.id.text_debug_current_place)
        }
        return layout
    }

    override fun onFragmentCreate() {
        placesAPIViewModel.currentPlaceLiveData.observe(
            this,
            Observer { listOfPlaceWrappers ->
                val maxsize = listOfPlaceWrappers.size
                val output = StringBuilder()
                output.append("<h4>Current Places</h4>")
                output.append("<ol>")
                for ((index, placeWrapper) in listOfPlaceWrappers.withIndex()) {
                    output.append("<li>")
                    output.append("<i>${index + 1}/$maxsize - </i>")
                    output.append("<b>name:</b> ${placeWrapper.name} <br/>")
                    output.append("<b>id:</b> ${placeWrapper.id} <br/>")
                    output.append("</li>")
                }
                output.append("</ol>")

                textDebugCurrentPlace.text = Html.fromHtml(output.toString())
            }
        )
    }

}
