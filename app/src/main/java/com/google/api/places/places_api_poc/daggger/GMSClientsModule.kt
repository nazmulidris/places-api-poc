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

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.android.gms.location.places.Places
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GMSClientsModule {
    @Provides
    @Singleton
    fun providesPlaceDetectionClient(context: Context): PlaceDetectionClient {
        return Places.getPlaceDetectionClient(context)
    }

    @Provides
    @Singleton
    fun providesGeoDataClient(context: Context): GeoDataClient {
        return Places.getGeoDataClient(context)
    }

    @Provides
    @Singleton
    fun providesLocation(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}