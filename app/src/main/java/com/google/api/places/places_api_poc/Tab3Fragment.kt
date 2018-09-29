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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import javax.inject.Inject

class Tab3Fragment : BaseTabFragment() {

    private lateinit var fragmentContainer: CoordinatorLayout
    private lateinit var textDebugCurrentPlace: TextView
    private lateinit var textDebugCurrentLocation: TextView
    private lateinit var textDebugModalData: TextView
    private lateinit var textDebugAutocompletePrediction: TextView
    @Inject
    lateinit var getCurrentPlaceLiveData: MutableLiveData<List<PlaceWrapper>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment.
        val layout = inflater.inflate(R.layout.fragment_tab3, container, false)
        with(layout) {
            fragmentContainer = findViewById(R.id.layout_tab3_root)
            textDebugCurrentPlace = findViewById(R.id.text_debug_current_place)
            textDebugCurrentLocation = findViewById(R.id.text_debug_current_location)
            textDebugModalData = findViewById(R.id.text_debug_modal_data)
            textDebugAutocompletePrediction = findViewById(R.id.text_debug_autocomplete_prediction)
        }
        return layout
    }

    override fun onFragmentCreate() {
        // This injects an object into getCurrentPlaceLiveData
        getMyApplication().activityComponent?.inject(this@Tab3Fragment)

        getCurrentPlace()
        getCurrentLocation()
        getAutocompletePredictions()
        getModalData()
    }

    private fun getModalData() {
        fun render() {
            textDebugModalData.text = Html.fromHtml(StringBuilder().apply {
                append("<h3>Modal Place Detail Sheet Data</h3>")
                with(placesViewModel.modalPlaceDetailsSheetLiveData) {
                    placeObservable().value?.apply {
                        append("<b>place:</b> ${name}<br/>")
                    }
                    sheetVisibleObservable().value?.apply {
                        append("<b>sheetVisible:</b> ${this}<br/>")
                    }
                    bitmap.value?.apply {
                        val size = this.bitmap?.byteCount?.div(1024) ?: "0"
                        append("<b>bitmap:</b> $size KB")
                    }
                }
            }.toString())
        }

        placesViewModel.modalPlaceDetailsSheetLiveData.placeObservable().observe(
                this,
                Observer { place ->
                    render()
                }
        )

        placesViewModel.modalPlaceDetailsSheetLiveData.sheetVisibleObservable().observe(
                this,
                Observer { visibility ->
                    render()
                }
        )

        placesViewModel.modalPlaceDetailsSheetLiveData.bitmap.observe(
                this,
                Observer { bitmap ->
                    render()
                }
        )

    }


    private fun getAutocompletePredictions() {
        placesViewModel.autoCompletePredictions.liveData.observe(
                this,
                Observer { listOfAutocompletePreductions ->
                    val count = listOfAutocompletePreductions.size
                    if (count > 0)
                        textDebugAutocompletePrediction.text = Html.fromHtml(StringBuilder().apply {
                            append("<h3>Autocomplete Predictions</h3>")
                            for ((index, prediction) in listOfAutocompletePreductions.withIndex()) {
                                append("<i>${index + 1}/$count</i>▶")
                                append("<b>name:</b> ${prediction.primaryText}")
                                //append(", <b>id:</b> ${prediction.placeId}")
                                append("<br/>")
                            }
                        }.toString())
                }
        )
    }

    private fun getCurrentLocation() {
        placesViewModel.getLastLocation.liveData.observe(
                this,
                Observer { location ->
                    textDebugCurrentLocation.text = Html.fromHtml(StringBuilder().apply {
                        append("<h3>Current Location</h3>")
                        val url = getUrl(location.latitude, location.longitude)
                        append("$url")
                        //append("${location}")
                    }.toString())
                }
        )
    }

    private fun getCurrentPlace() {
        getCurrentPlaceLiveData.observe(
                this,
                Observer { listOfPlaceWrappers ->
                    val count = listOfPlaceWrappers.size
                    if (count > 0)
                        textDebugCurrentPlace.text = Html.fromHtml(StringBuilder().apply {
                            append("<h3>Current Places</h3>")
                            for ((index, placeWrapper) in listOfPlaceWrappers.withIndex()) {
                                append("<i>${index + 1}/$count</i>▶")
                                append("<b>name:</b> ${placeWrapper.name}")
                                //append(", <b>id:</b> ${placeWrapper.id}")
                                append("<br/>")
                            }
                        }.toString())
                }
        )
    }

}
