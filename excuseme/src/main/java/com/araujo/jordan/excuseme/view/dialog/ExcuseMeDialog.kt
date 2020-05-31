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

package com.araujo.jordan.excuseme.view.dialog

import androidx.appcompat.app.AlertDialog
import com.araujo.jordan.excuseme.view.InvisibleActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

/**
 * Abstract dialog to explain the user permissions after or before the permission request itself
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
open class ExcuseMeDialog(val showDialog: Boolean) {

    /**
     * Constructor for default dialog, easy to implement
     * @param title title of dialog
     * @param reason description of the permissions that will be asked
     */
    constructor(title: String, reason: String) : this(true) {
        this.title = title
        this.reason = reason
    }

    protected var title: String? = null
    protected var reason: String? = null

    private var channel: Channel<Boolean>? = null
    protected var alertDialog: AlertDialog? = null

    /**
     *  Start the Courotine Channel and wait for the result
     *  @param act InvisibleActivity from ExcuseMe to ask the permissions
     *  @return the result of the dialog
     */
    open suspend fun showDialogForPermission(act: InvisibleActivity): Boolean {
        if (channel == null) channel = Channel()
        val status = channel?.receive() ?: false
        channel = null
        return status
    }

    /**
     * Callback method that releases the channel wit the result
     * @param chanelAns Result of the dialog
     */
    protected fun channelAns(chanelAns: Boolean) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            channel?.send(chanelAns)
            alertDialog?.dismiss()
            alertDialog = null
        }
    }


}