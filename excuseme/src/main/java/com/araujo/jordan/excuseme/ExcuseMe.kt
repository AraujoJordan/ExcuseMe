/**
 *
 * Copyright © 2020 Jordan Lira de Araujo Junior
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.araujo.jordan.excuseme

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


/**
 * This class is a singleton and it holds the control of the Android Runtime permissions using
 * an InvisibleActivity for make the request and Coroutines to handle async the result
 */
class ExcuseMe private constructor() {

    //Holds ExcuseMe Singleton for return the permissions
    private object HOLDER {
        val INSTANCE = ExcuseMe()
    }

    private var weakContext: WeakReference<Context>? = null
    private var permissionStatus: PermissionStatus? = null
    private var channel: Channel<Boolean>? = null

    companion object {

        private val instance: ExcuseMe by lazy { HOLDER.INSTANCE }

        /**
         * Set the activity that will be used to call the invisible activity
         */
        fun couldYouGive(activity: Activity): ExcuseMe {
            HOLDER.INSTANCE.weakContext = WeakReference(activity)
            return instance
        }

        /**
         * Set the fragment that will be used to call the invisible activity
         */
        fun couldYouGive(fragment: Fragment): ExcuseMe {
            HOLDER.INSTANCE.weakContext = WeakReference(fragment.requireActivity())
            return instance
        }

        /**
         * Set the context that will be used to call the invisible activity
         */
        fun couldYouGive(context: Context): ExcuseMe {
            HOLDER.INSTANCE.weakContext = WeakReference(context)
            return instance
        }

        /**
         * Callback to continue with the result from the permissions requests
         * @param permissionResult the permissions result that come from the InvisibleActivity
         */
        fun onPermissionResult(permissionResult: PermissionStatus) {
            HOLDER.INSTANCE.permissionStatus = permissionResult
            CoroutineScope(Dispatchers.Main.immediate).launch {
                HOLDER.INSTANCE.channel?.send(true)
                HOLDER.INSTANCE.weakContext?.clear()
                HOLDER.INSTANCE.weakContext = null
            }
        }

        fun doWeHavePermissionFor(context: Context, vararg permissions: String): Boolean {
            permissions.forEach {
                if (ContextCompat.checkSelfPermission(context, it) !=
                    PackageManager.PERMISSION_GRANTED
                ) return false
            }
            return true
        }
    }

    /**
     * Ask permission for one or multiple permissions and start the permission dialog
     * This method use callback and can be used with Kotlin callback syntax
     *
     * @param permission one or multiple permissions from android.Manifest.permission.* strings
     * @param completion callback with PermissionStatus object that holds the result
     * @return Return nothing, but the completion callback have the PermissionStatus object that
     * holds the result
     */
    fun permissionFor(
        vararg permission: String,
        completion: (permissionStatus: PermissionStatus) -> Unit
    ) = CoroutineScope(Dispatchers.Main.immediate).launch {
        val req = runPermissionRequest(*permission)
        completion(req)
    }

    /**
     * Ask permission for one or multiple permissions and start the permission dialog
     * This method use async return from Kotlin Coroutines and can be used without callbacks
     *
     * @param permission one or multiple permissions from android.Manifest.permission.* strings
     * @return PermissionStatus object that holds the result with the granted/refused permissions
     */
    suspend fun permissionFor(vararg permission: String): PermissionStatus {
        return runPermissionRequest(*permission)
    }

    /**
     * Calls the InvisibleActivity that makes the Permission request. The channel will
     * listen for the completePermission()
     */
    private suspend fun runPermissionRequest(vararg permissions: String): PermissionStatus {
        val permissionStatus = PermissionStatus()
        weakContext?.get()?.let { context ->
            context.startActivity(Intent(context, InvisibleActivity::class.java).apply {
                putExtra("permissions", permissions)
            })
        }
        if (channel == null) channel = Channel()
        channel?.receive()
        channel = null
        return permissionStatus
    }
}