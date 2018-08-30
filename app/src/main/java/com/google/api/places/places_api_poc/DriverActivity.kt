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
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_driver.*
import org.jetbrains.anko.AnkoLogger

class DriverActivity : AppCompatActivity(), AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        // Enable bottom bar navigation to respond to user input
        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Pre-select the first fragment
        switchFragment(R.id.navigation_tab1)
    }

    // Handle user input on bottom bar navigation
    private val onNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                val id = item.itemId
                if (id in fragmentMap.keys) {
                    switchFragment(id)
                    return@OnNavigationItemSelectedListener true
                } else return@OnNavigationItemSelectedListener false
            }

    // Manage creating and switching Fragments
    private val fragmentMap = mutableMapOf<Int, Fragment>().apply {
        put(R.id.navigation_tab1, Tab1Fragment())
        put(R.id.navigation_tab2, Tab2Fragment())
        put(R.id.navigation_tab3, Tab3Fragment())
    }

    private fun switchFragment(id: Int) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_fragmentholder,
                         fragmentMap[id])
                .addToBackStack(null)
                .commit()
    }

}
