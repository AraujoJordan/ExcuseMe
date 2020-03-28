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
import com.araujo.jordan.excuseme.model.PermissionStatus
import com.araujo.jordan.excuseme.view.GentlyDialog
import com.araujo.jordan.excuseme.view.InvisibleActivity
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
    private var permissionStatus =
        PermissionStatus()
    private var channel: Channel<Boolean>? = null
    private var gentlyDialog: GentlyDialog? = null

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

        /**
         * This method shouldn't be used outside the ExcuseMe implementation
         */
        fun getGentlyDialog() = this.instance.gentlyDialog
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
     * Add a dialog before ask the permission to explain the reason of asking this permission.
     * This will help to reduce the users permissions denied that could decrease your Google
     * Store Vitals score.
     * Source: https://developer.android.com/topic/performance/vitals/permissions
     *
     * This dialog will create a text along with your explanation to clarify the usage of the
     * permission. For example, if you want permission to use the camera for scan user documents,
     * you can simply add "scan documents for verification". So the ExcuseMe will generate a text like:
     * "To scan documents for verification, allow YourAppName to access the permission for Camera."
     *
     * In this way, if the user deny the permission from this dialog, it won't propagate to your app
     * real permission, and you Play Store Vitals score.
     *
     * @param explanation an array of simple explanations related to your permissions request
     */
    fun gently(vararg explanation: String): ExcuseMe {
        weakContext?.let {
            gentlyDialog = GentlyDialog(*explanation)
        }
        return HOLDER.INSTANCE
    }

    /**
     * Ask permission for one or multiple permissions and start the permission dialog
     * This method use async return from Kotlin Coroutines and can be used without callbacks
     *
     * @param permission one or multiple permissions from android.Manifest.permission.* strings
     * @return PermissionStatus object that holds the result with the granted/refused permissions
     */
    suspend fun permissionFor(vararg permission: String) = runPermissionRequest(*permission)

    /**
     * Calls the InvisibleActivity that makes the Permission request. The channel will
     * listen for the completePermission()
     */
    private suspend fun runPermissionRequest(vararg permissions: String): PermissionStatus {
        weakContext?.get()?.let { context ->

            val deniedPerm = permissions.filter { !doWeHavePermissionFor(context, it) }

            if (deniedPerm.isEmpty()) {
                permissionStatus = PermissionStatus(granted = permissions.toMutableList())
                return permissionStatus
            } else {
                context.startActivity(Intent(context, InvisibleActivity::class.java).apply {
                    putExtra("permissions", deniedPerm.toTypedArray())
                })
                if (channel == null) channel = Channel()
                channel?.receive()
                channel = null
            }
        }

        return HOLDER.INSTANCE.permissionStatus
    }
}