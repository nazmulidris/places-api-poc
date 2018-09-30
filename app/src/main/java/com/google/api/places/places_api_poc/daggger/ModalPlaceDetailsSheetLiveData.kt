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

package com.google.api.places.places_api_poc.daggger

import androidx.lifecycle.MutableLiveData
import com.google.api.places.places_api_poc.model.BitmapWrapper
import com.google.api.places.places_api_poc.model.PlaceWrapper

//
// Modal "Place Details Sheet" Data.
//

data class ModalPlaceDetailsSheetLiveData(
        val bitmap: MutableLiveData<BitmapWrapper> = MutableLiveData(),
        /** [place] is private, because it's changes [bitmap] & [sheetVisible]. */
        private val place: MutableLiveData<PlaceWrapper> = MutableLiveData(),
        /** [sheetVisible] is private, because it depends on [place]. */
        private val sheetVisible: MutableLiveData<Boolean> = MutableLiveData()
) {
    /**
     * This is called from the main thread. When a new place is set, then bitmap
     * is cleared, and the visibility is set to true, so that the place details sheet
     * will appear. This is driven by [DriverActivity].
     */
    fun setPlace(value: PlaceWrapper) {
        place.value = value
        bitmap.value = BitmapWrapper()
        sheetVisible.value = true
    }

    /**
     * This is called from a background thread. When a new place is set, then bitmap
     * is cleared, and the visibility is set to true, so that the place details sheet
     * will appear. This is driven by [DriverActivity]
     */
    fun postPlace(value: PlaceWrapper) {
        place.postValue(value)
        bitmap.postValue(BitmapWrapper())
        sheetVisible.postValue(true)
    }

    fun placeObservable(): MutableLiveData<PlaceWrapper> {
        return place
    }

    fun sheetVisibleObservable(): MutableLiveData<Boolean> {
        return sheetVisible
    }
}