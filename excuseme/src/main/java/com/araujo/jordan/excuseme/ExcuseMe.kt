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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.araujo.jordan.excuseme.model.PermissionStatus
import com.araujo.jordan.excuseme.view.InvisibleActivity
import com.araujo.jordan.excuseme.view.dialog.DialogType
import com.araujo.jordan.excuseme.view.dialog.PosPermissionDialog
import com.araujo.jordan.excuseme.view.dialog.PrePermissionDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


/**
 * This class is a singleton and it holds the control of the Android Runtime permissions using
 * an InvisibleActivity for make the request and Coroutines to handle async the result
 *
 * @author Jordan L. Araujo Jr. (araujojordan)
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
    private var preDialog = PrePermissionDialog()
    private var posDialog = PosPermissionDialog()

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
         * Handle permissions automagically using the Thread.UncaughtExceptionHandler
         * @param activity the activity that the ExcuseMe will listen for permissions
         * @param afterPermissionsRequest the callback that will run after the permission result
         * @return the callback will return true if the user granted all the permissions
         */
        fun couldYouHandlePermissionsForMe(
            activity: Activity,
            afterPermissionRequest: (Boolean) -> Unit
        ) {
            try {
                Thread.currentThread().uncaughtExceptionHandler = AutoPermissionHandler(
                    activity,
                    (activity as? AppCompatActivity)?.lifecycle,
                    afterPermissionRequest
                )
            } catch (err: Exception) {
                println("Can't do it automatically: ${err.message}")
            }
        }

        /**
         * Handle permissions automagically using the Thread.UncaughtExceptionHandler
         * @param fragment the fragment that the ExcuseMe will listen for permissions
         * @param afterPermissionsRequest the callback that will run after the permission result
         * @return the callback will return true if the user granted all the permissions
         */
        fun couldYouHandlePermissionsForMe(
            fragment: Fragment,
            afterPermissionsRequest: (Boolean) -> Unit
        ) {
            try {
                Thread.currentThread().uncaughtExceptionHandler = AutoPermissionHandler(
                    fragment.requireActivity(),
                    fragment.lifecycle,
                    afterPermissionsRequest
                )
            } catch (err: Exception) {
                println("Can't do it automatically: ${err.message}")
            }
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

        /**
         * Check if the given context have granted permissions for all the strings given
         * @param context The context. Prefer use UI context like activity, fragment, view...
         * @param permissions One or more permissions that you want to check if have permissions
         * @return true if user had granted permissions to all of the strings given
         */
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
        fun getPreDialog() = this.instance.preDialog

        /**
         * This method shouldn't be used outside the ExcuseMe implementation
         */
        fun clearPreDialog() {
            this.instance.preDialog = PrePermissionDialog()
        }

        /**
         * This method shouldn't be used outside the ExcuseMe implementation
         */
        fun getPosDialog() = this.instance.posDialog

        /**
         * This method shouldn't be used outside the ExcuseMe implementation
         */
        fun clearPosDialog() {
            this.instance.posDialog = PosPermissionDialog()
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
     * This method will add a generic dialog before ask the permission for explain the reason
     * of asking this permission. This will help to reduce the users permissions denied that
     * could decrease your Google Store Vitals score.
     * Source: https://developer.android.com/topic/performance/vitals/permissions
     *
     * This dialog will create a text along with your explanation to clarify the usage of the
     * permission. For example, if you want permission to use the camera for scan user documents,
     * you can simply add a text like:
     * "To scan documents for verification, allow YourAppName to access the permission for Camera."
     *
     * In this way, if the user deny the permission from this dialog, it won't propagate to your app
     * real permission, and you Play Store Vitals score.
     *
     * @param explanation a simple explanation related to your permissions request
     */
    fun gently(title: String, explanation: String): ExcuseMe {
        preDialog =
            PrePermissionDialog(
                title,
                explanation
            )
        return HOLDER.INSTANCE
    }

    /**
     * This method will let the user to implement his custom dialog/something before ask the
     * permission for explain the reason of asking this permission. This will help to reduce the
     * users permissions denied that could decrease your Google Store Vitals score.
     * Source: https://developer.android.com/topic/performance/vitals/permissions
     *
     * This dialog will create a text along with your explanation to clarify the usage of the
     * permission. For example, if you want permission to use the camera for scan user documents,
     * you can simply add a text like:
     * "To scan documents for verification, allow YourAppName to access the permission for Camera."
     *
     * In this way, if the user deny the permission from this dialog, it won't propagate to your app
     * real permission, and you Play Store Vitals score.
     *
     * @param customGentlyRequest is a callback that will run a custom user code snippet, this code
     * snippet have to call other callback that will send a boolean to notify the ExcuseMe to continue
     * or cancel the permission request
     */
    fun gently(customGentlyRequest: ((Boolean) -> Unit) -> Unit): ExcuseMe {
        preDialog = PrePermissionDialog(
            customGentlyRequest
        )
        return HOLDER.INSTANCE
    }

    /**
     * This method will add a generic dialog after the permission request if the permission
     * is denied. This is a fallback to insist to ask again the permission, explaining why is
     * necessary to continue
     *
     * You need to add params the title and explanations for those two situations:
     *  1. Show permission dialog again
     *  2. Show the settings page
     *
     * @param explainAgainTitle a simple title related to your permissions request and why it's necessary
     * @param explainAgainExplanation a simple explanation related to your permissions request and why it's necessary
     * @param showSettingsTitle a simple explanation related to your permissions request and why it's need to show the settings page
     * @param showSettingsExplanation a simple explanation related to your permissions request and why it's need to show the settings page
     */
    fun please(
        explainAgainTitle: String, explainAgainExplanation: String,
        showSettingsTitle: String, showSettingsExplanation: String
    ): ExcuseMe {
        posDialog = PosPermissionDialog(
            explainAgainTitle,
            explainAgainExplanation,
            showSettingsTitle,
            showSettingsExplanation
        )
        return HOLDER.INSTANCE
    }

    /**
     * This method will add a generic dialog after the permission request if the permission
     * is denied. This is a fallback to insist to ask again the permission, explaining why is
     * necessary to continue.
     * You need to implement the callback for those two situations:
     *  1. Show permission dialog again
     *  2. Show the settings page
     *
     * @param customDialogRequest is a lambda callback that will run a custom user code snippet, this code
     * snippet will tell wich situation is and will wait the boolean to notify the ExcuseMe to continue
     * or cancel
     */
    fun please(customDialogRequest: ((type: DialogType, ((Boolean) -> Unit)) -> Unit)): ExcuseMe {
        posDialog = PosPermissionDialog(customDialogRequest)
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
     * Ask permission for one or multiple permissions and start the permission dialog
     * This method use async return from Kotlin Coroutines and can be used without callbacks
     *
     * @param permission one or multiple permissions from android.Manifest.permission.* strings
     * @return boolean that holds the result with the granted/refused permission
     */
    suspend fun permissionFor(permission: String) =
        runPermissionRequest(permission).granted.contains(permission)

    /**
     * Calls the InvisibleActivity that makes the Permission request. The channel will
     * listen for the completePermission()
     */
    private suspend fun runPermissionRequest(vararg permissions: String): PermissionStatus {
        weakContext?.get()?.let { context ->

            val deniedPerm = permissions.filter { !doWeHavePermissionFor(context, it) }

            if (deniedPerm.isEmpty()) {
                permissionStatus = PermissionStatus(granted = permissions.toMutableList())
                weakContext?.clear()
                weakContext = null
                preDialog = PrePermissionDialog()
                posDialog = PosPermissionDialog()
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