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
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.places.GeoDataClient
import com.google.android.gms.location.places.PlaceDetectionClient
import com.google.api.places.places_api_poc.misc.ExecutorWrapper
import com.google.api.places.places_api_poc.model.AutocompletePredictionData
import com.google.api.places.places_api_poc.model.PlaceWrapper
import com.google.api.places.places_api_poc.model.PlacesAPI
import com.google.api.places.places_api_poc.service.*
import com.google.api.places.places_api_poc.ui.*
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Scope

@Scope
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(ExecutorModule::class,
                                LiveDataModule::class,
                                ServicesModule::class))
interface ActivityComponent {
    fun inject(api: PlacesAPI)
    fun inject(fragment: Tab1Fragment)
    fun inject(fragment: Tab3Fragment)
    fun inject(fragment: Tab2Fragment)
    fun inject(bottomSheetDialogFragment: PlaceDetailsSheetFragment)
    fun inject(activity: DriverActivity)
}

typealias PlacesLiveData = MutableLiveData<List<PlaceWrapper>>
typealias AutocompletePredictionsLiveData = MutableLiveData<List<AutocompletePredictionData>>
typealias LocationLiveData = MutableLiveData<Location>

@Module
class LiveDataModule {
    @Provides
    @ActivityScope
    fun providesGetCurrentPlaceLiveData() = PlacesLiveData()

    @Provides
    @ActivityScope
    fun providesModalPlaceDetailsSheetLiveData() = PlaceDetailsSheetLiveData()

    @Provides
    @ActivityScope
    fun providesAutocompletePredictionsLiveData() = AutocompletePredictionsLiveData()

    @Provides
    @ActivityScope
    fun providesGetLastLocationLiveData() = LocationLiveData()
}

@Module
class ExecutorModule {
    @Provides
    @ActivityScope
    fun provideExecutor(): ExecutorWrapper {
        return ExecutorWrapper()
    }
}

@Module
class ServicesModule {

    @Provides
    @ActivityScope
    fun provideGetPlaceByIDService(wrapper: ExecutorWrapper,
                                   client: GeoDataClient,
                                   data: PlaceDetailsSheetLiveData) =
            GetPlaceByIDService(wrapper, client, data)

    @Provides
    @ActivityScope
    fun provideGetAutocompletePredictionsService(wrapper: ExecutorWrapper,
                                                 client: GeoDataClient,
                                                 data: AutocompletePredictionsLiveData) =
            GetAutocompletePredictionsService(wrapper, client, data)

    @Provides
    @ActivityScope
    fun provideGetLastLocationService(wrapper: ExecutorWrapper,
                                      client: FusedLocationProviderClient,
                                      context: Context,
                                      data: LocationLiveData) =
            GetLastLocationService(wrapper, client, context, data)

    @Provides
    @ActivityScope
    fun provideGetCurrentPlaceService(wrapper: ExecutorWrapper,
                                      context: Context,
                                      client: PlaceDetectionClient,
                                      data: PlacesLiveData) =
            GetCurrentPlaceService(wrapper, context, client, data)

    @Provides
    @ActivityScope
    fun provideGetPhotoService(wrapper: ExecutorWrapper,
                               client: GeoDataClient,
                               data: PlaceDetailsSheetLiveData) =
            GetPhotoService(wrapper, client, data)

    @Provides
    @ActivityScope
    fun provideGetPlacePhotosService(wrapper: ExecutorWrapper,
                                     client: GeoDataClient,
                                     getPhotoService: GetPhotoService) =
            GetPlacePhotosService(wrapper, client, getPhotoService)

}