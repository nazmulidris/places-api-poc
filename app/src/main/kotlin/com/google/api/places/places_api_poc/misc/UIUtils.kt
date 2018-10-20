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

package com.google.api.places.places_api_poc.misc

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.api.places.places_api_poc.daggger.MyApplication
import com.google.api.places.places_api_poc.ui.BaseTabFragment
import com.google.api.places.places_api_poc.ui.DriverActivity

//
// Extension Functions
//

inline fun snack(view: View,
                 text: String = "",
                 duration: Int = Snackbar.LENGTH_SHORT,
                 functor: Snackbar.() -> Unit) {
    with(Snackbar.make(view, text, duration)) {
        functor()
        show()
    }
}

inline fun toast(context: Context,
                 text: String = "",
                 duration: Int = Toast.LENGTH_SHORT,
                 functor: Toast.() -> Unit) {
    with(Toast.makeText(context, text, duration)) {
        functor()
        show()
    }
}

fun BottomSheetDialogFragment.getMyApplication(): MyApplication =
        this.requireActivity().application as MyApplication

fun DriverActivity.getMyApplication(): MyApplication = this.application as MyApplication

fun Application.getMyApplication(): MyApplication = this as MyApplication

fun BaseTabFragment.getMyApplication(): MyApplication =
        this.requireActivity().application as MyApplication

