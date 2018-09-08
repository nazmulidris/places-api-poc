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

import android.graphics.Bitmap
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PlaceDetailsSheetFragment : BottomSheetDialogFragment() {

    private lateinit var textBody: TextView
    private lateinit var textHeader: TextView
    private lateinit var imagePlacePhoto: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment.
        val layout = inflater.inflate(R.layout.fragment_place_details_sheet,
                                      container,
                                      false)

        with(layout) {
            textBody = findViewById(R.id.text_place_details_body)
            textHeader = findViewById(R.id.text_place_details_header)
            imagePlacePhoto = findViewById(R.id.image_place_photo)
        }

        return layout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        setupPlaceWrapperLiveDataObserver()
        setupBitmapWrapperLiveDataObserver()
    }

    //
    // Observe changes in LiveData (placeWrapperLiveData, bitmapWrapperLiveData).
    //

    private lateinit var placesAPIViewModel: PlacesAPI
    private fun setupViewModel() {
        // Load ViewModel.
        // 🛑 Note - You **must** pass activity scope, in order to get this ViewModel,
        // and if you pass the fragment instance, then you won't get the ViewModel that
        // was attached w/ the parent activity (DriverActivity).
        placesAPIViewModel = ViewModelProviders.of(requireActivity()).get(PlacesAPI::class.java)
    }

    private fun setupBitmapWrapperLiveDataObserver() {
        placesAPIViewModel.bitmapWrapperLiveData.observe(
            this,
            Observer { bitmapWrapper ->
                renderPhoto(bitmapWrapper.bitmap)
            }
        )
    }

    private fun setupPlaceWrapperLiveDataObserver() {
        placesAPIViewModel.placeWrapperLiveData.observe(
            this,
            Observer { placeWrapper ->
                renderPlace(placeWrapper)
                lookupPhoto(placeId = placeWrapper.id)
            }
        )
    }

    //
    // Make Places API call to get photos.
    //

    private fun lookupPhoto(placeId: String) {
        placesAPIViewModel.getPlacePhotos(placeId)
    }

    //
    // Render this View.
    //

    private fun renderPhoto(bitmap: Bitmap) {
        imagePlacePhoto.setImageBitmap(bitmap)
    }

    private fun renderPlace(placeWrapper: PlaceWrapper) {
        textHeader.text = placeWrapper.name
        StringBuffer().apply {
            for (entry in placeWrapper.map) {
                append("<br/><b>${entry.key}</b><br/>")
                append("<code>${generateValueString(entry.value)}<code><br/>")
            }
        }.apply {
            textBody.text = Html.fromHtml(this.toString())
        }
    }

    private fun generateValueString(value: Any?) =
            if (value == null) "n/a"
            else if (value.toString().trim().isEmpty()) "n/a"
            else value.toString()

}