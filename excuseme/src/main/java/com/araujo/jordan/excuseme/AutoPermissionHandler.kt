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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * This class will listen for the thread exceptions, if it founds a SecurityException with the
 * Permission Denial message in it, it will read the message looking for the permission and ask for
 * it automatically.
 *
 * @param activity the activity that will listen for the permissions
 * @param lifecycle the lifecycle that observe the activity/fragment life and clean the memory
 * @param afterPermissionRequest will be called when the user granted/denied permission(s)
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
class AutoPermissionHandler(
    private var activity: Activity? = null,
    lifecycle: Lifecycle?,
    private var afterPermissionRequest: ((Boolean) -> Unit)?
) :
    Thread.UncaughtExceptionHandler,
    LifecycleObserver {

    init {
        lifecycle?.addObserver(this)
    }

    /**
     * Implementation of uncaughtException. It will only listen for Permission Exceptions
     */
    override fun uncaughtException(thread: Thread, trowable: Throwable) {
        if (trowable is SecurityException &&
            trowable.message?.contains("Permission Denial", true) == true
        ) {
            handlePermission(trowable)
        } else {
            println(trowable.message)
            activity?.finish()
        }
    }

    /**
     * Destroy the activity reference and remove the uncaughtExceptionHandler listener.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        activity = null
        Thread.currentThread().uncaughtExceptionHandler = null
    }

    private fun handlePermission(trowable: Throwable) =
        CoroutineScope(Dispatchers.Main.immediate).launch {
            val permissions = (trowable.message
                ?.split(" ")
                ?.filter { it.startsWith("android.permission.") }
                ?: listOf()).toTypedArray()
            if (permissions.isEmpty()) return@launch
            activity?.let {
                val granted = ExcuseMe.couldYouGive(it)
                    .permissionFor(*permissions).granted.size == permissions.size
                afterPermissionRequest?.invoke(granted) ?: activity?.recreate()
            }
        }
}