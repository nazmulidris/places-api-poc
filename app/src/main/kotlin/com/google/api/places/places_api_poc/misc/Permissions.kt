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

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun isPermissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED

fun isPermissionDenied(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PERMISSION_DENIED

fun requestPermission(activity: AppCompatActivity, permission: String, responseId: Int) {
    ActivityCompat.requestPermissions(activity, arrayOf(permission), responseId)
}

// Simple interface to perform a task that requires a permission.
interface PermissionDependentTask {
    fun getRequiredPermission(): String
    fun onPermissionGranted()
    fun onPermissionRevoked()
}

object PermissionsHandler {

    // Manage runtime permissions for ACCESS_FINE_LOCATION.
    // Constant required when dealing with asking user for permission grant.
    private const val PERMISSION_ID = 1234

    // Holds one pending task that will be run if permission is granted.
    private var pendingTask: PermissionDependentTask? = null

    fun executeTaskOnPermissionGranted(context: AppCompatActivity, task: PermissionDependentTask) {
        if (isPermissionDenied(context,
                               task.getRequiredPermission())) {
            // Permission is not granted ☹. Ask the user for the run time permission 🙏.
            "🔒 ${task.getRequiredPermission()} not granted 🛑, request it 🙏️".log()
            requestPermission(context,
                              task.getRequiredPermission(),
                              PERMISSION_ID)
            if (pendingTask == null) pendingTask = task
        } else {
            // Permission is granted 🙌. Run the task function.
            "🔒 ${task.getRequiredPermission()} permission granted 🙌, Execute pendingTask ".log()
            task.onPermissionGranted()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<String>,
                                   grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_ID -> {
                when (PermissionResult.convert(requestCode, permissions, grantResults)) {
                    is PermissionsHandler.PermissionResult.Granted -> {
                        if (pendingTask != null) {
                            "🔒 Permission is granted 🙌, Execute pendingTask".log()
                            pendingTask?.onPermissionGranted()
                            pendingTask = null
                        }
                    }
                    is PermissionsHandler.PermissionResult.Revoked -> {
                        pendingTask?.onPermissionRevoked()
                    }
                    is PermissionsHandler.PermissionResult.Cancelled -> {

                    }
                }
            }
            // Add other 'when' lines to check for other permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    sealed class PermissionResult {
        class Granted(id: Int) : PermissionResult()
        class Revoked(id: Int) : PermissionResult()
        class Cancelled(id: Int) : PermissionResult()

        companion object {
            fun convert(requestCode: Int,
                        permissions: Array<String>,
                        grantResults: IntArray): PermissionResult {
                return when {
                    // If request is cancelled, the result arrays are empty.
                    grantResults.isEmpty() -> Cancelled(requestCode)
                    // Permission was granted, 🎉. Run the pending task function.
                    grantResults.first() == PERMISSION_GRANTED -> Granted(requestCode)
                    // Permission denied, ☹.
                    else -> Revoked(requestCode)
                }
            }
        }
    }


}