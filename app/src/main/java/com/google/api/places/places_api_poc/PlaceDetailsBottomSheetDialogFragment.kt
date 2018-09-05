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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class PlaceDetailsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    lateinit var textBody: TextView
    lateinit var textHeader: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment.
        val layout = inflater.inflate(R.layout.fragment_place_details,
                                      container,
                                      false)

        textBody = layout.findViewById(R.id.text_place_details_body)
        textHeader = layout.findViewById(R.id.text_place_details_header)

        return layout
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Get argument that is passed to fragment.
        arguments?.apply {
            val map = getSerializable("place") as HashMap<String, Any?>
            textHeader.text = map["name"] as String
            textBody.text = StringBuffer().apply {
                for (entry in map) {
                    append("👉${entry.key}\n")
                    append("${entry.value}\n")
                }
            }.toString()
        }

    }


}