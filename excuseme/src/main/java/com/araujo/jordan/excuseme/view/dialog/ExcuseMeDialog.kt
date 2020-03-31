package com.araujo.jordan.excuseme.view.dialog

import androidx.appcompat.app.AlertDialog
import com.araujo.jordan.excuseme.view.InvisibleActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

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

    open suspend fun showDialogForPermission(act: InvisibleActivity): Boolean {
        if (channel == null) channel = Channel()
        val status = channel?.receive() ?: false
        channel = null

        return status
    }

    protected fun channelAns(chanelAns: Boolean) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            channel?.send(chanelAns)
            alertDialog?.dismiss()
            alertDialog = null
        }
    }


}