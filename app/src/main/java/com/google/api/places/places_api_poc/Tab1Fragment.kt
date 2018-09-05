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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Tab1Fragment : BaseTabFragment() {

    private lateinit var fab: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var fragmentContainer: CoordinatorLayout

    // Inflate the layout.
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment.
        val layout = inflater.inflate(R.layout.fragment_tab1, container, false)

        fab = layout.findViewById(R.id.fab_current_place)
        recyclerView = layout.findViewById(R.id.current_place_recyclerview)
        fragmentContainer = layout.findViewById(R.id.fragment_container_tab1)

        return layout
    }

    override fun attachToUI() {
        // Setup RecyclerView.
        setupRecyclerView()

        // Attach a behavior to the FAB.
        fab.setOnClickListener { viewClicked ->
            getParentActivity().executeTaskOnPermissionGranted(
                object : PermissionDependentTask {
                    override fun getRequiredPermission() =
                            android.Manifest.permission.ACCESS_FINE_LOCATION

                    override fun onPermissionGranted() {
                        placesAPIViewModel.getCurrentPlace()
                        "❤️ This app will function well with this permission".snack(
                            fragmentContainer)
                    }

                    override fun onPermissionRevoked() {
                        "🛑 This app will not function without this permission".snack(
                            fragmentContainer)
                    }
                })
        }

    }

    private fun setupRecyclerView() {
        // Create the RecyclerView Adapter.
        val dataAdapter = DataAdapter(getParentActivity())

        // Attach LiveData observers for current place data (from Places API).
        placesAPIViewModel.currentPlaceData.observe(this@Tab1Fragment, Observer { data ->
            "🎉 observable reacting -> #places=${data.size}".log()
            dataAdapter.loadData(data)
        })

        with(recyclerView) {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            layoutManager = LinearLayoutManager(context)
            adapter = dataAdapter
        }

    }

}

//
// List Adapter.
//
class DataAdapter(val activity: DriverActivity) : RecyclerView.Adapter<RowViewHolder>() {
    // Underlying data storage.
    val underlyingData: MutableList<PlaceWrapper> = mutableListOf()

    // Load underlying data and update RecyclerView.
    fun loadData(data: List<PlaceWrapper>) {
        underlyingData.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return underlyingData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        with(activity.layoutInflater.inflate(R.layout.item_row_place,
                                             parent,
                                             false)) {
            return RowViewHolder(activity, this)
        }
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        holder.bindToDataItem(underlyingData[position])
    }

}

//
// Row renderer (ViewHolder).
//
class RowViewHolder(val activity: DriverActivity, itemView: View) :
        RecyclerView.ViewHolder(itemView) {

    // Get the row renderer from the itemView that's passed (which loads R.layout.item_row_place)
    private val rowView: TextView = itemView.findViewById(R.id.text_row_place)

    fun bindToDataItem(place: PlaceWrapper) {
        rowView.text = place.id
        rowView.setOnClickListener {
            activity.snack(R.id.fragment_container_tab1, "👍 ${place.name}")
            PlaceDetailsBottomSheetDialogFragment().apply {
                arguments = place.getBundle("place")
            }.show(activity.supportFragmentManager, PlaceDetailsBottomSheetDialogFragment::javaClass.name)
        }
    }

}