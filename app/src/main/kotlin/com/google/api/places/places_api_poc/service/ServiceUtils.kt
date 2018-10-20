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

package com.google.api.places.places_api_poc.service

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import java.util.concurrent.ExecutorService

sealed class ServiceResponse<T> {
    data class Success<T>(val value: T) : ServiceResponse<T>()
    data class Error<T>(val exception: Exception) : ServiceResponse<T>()
}

fun <T> Task<T>.handleResponse(executorService: ExecutorService,
                               functor: (ServiceResponse<T>) -> Unit) {
    addOnCompleteListener(
            executorService,
            OnCompleteListener<T> {
                // This runs in a background thread (provided by the executor).
                if (isSuccessful && result != null) {
                    functor(ServiceResponse.Success(result!!))
                } else {
                    functor(ServiceResponse.Error(exception!!))
                }
            }
    )
}
