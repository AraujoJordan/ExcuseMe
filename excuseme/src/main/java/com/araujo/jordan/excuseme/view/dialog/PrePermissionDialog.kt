package com.araujo.jordan.excuseme.view.dialog

import androidx.appcompat.app.AlertDialog
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.utils.DesignUtils
import com.araujo.jordan.excuseme.view.InvisibleActivity
import kotlinx.android.synthetic.main.dialog_gently_ask.view.*

class PrePermissionDialog : ExcuseMeDialog {

    private var customRequest: (((Boolean) -> Unit) -> Unit)? = null

    constructor() : super(false)
    constructor(title: String, reason: String) : super(title, reason)

    /**
     * Constructor to let the user implement his own dialog/code to show before the
     * system permission dialog.
     *
     * Attention: This one can lead to errors if the user forget to implement the boolean callback inside.
     *
     * @param customDialogRequest run the code that user want. It must return the boolean callback inside to continue.
     */
    constructor(customDialogRequest: (((Boolean) -> Unit) -> Unit)) : super(true) {
        this.customRequest = customDialogRequest
    }

    override suspend fun showDialogForPermission(act: InvisibleActivity): Boolean {
        if (customRequest != null) {
            customRequest?.invoke { channelAns(it) }
        } else {
            val dialog = AlertDialog.Builder(act)
            val v = act.layoutInflater.inflate(R.layout.dialog_gently_ask, null)

            v.excuseMeGentlyTitle?.text = title
            v.excuseMeGentlyDescriptionText?.text = reason
            v.excuseMeGentlyYesBtn?.setOnClickListener { channelAns(true) }
            v.excuseMeGentlyYesBtn?.setTextColor(DesignUtils.resolveColor(act, "colorPrimaryDark"))
            v.excuseMeGentlyNoBtn?.setTextColor(DesignUtils.resolveColor(act, "colorPrimaryDark"))
            v.excuseMeGentlyTitle?.setBackgroundColor(DesignUtils.resolveColor(act, "colorPrimary"))
            v.excuseMeGentlyDescriptionText?.setTextColor(DesignUtils.resolveColor(act, "#0c0c0c"))
            v.excuseMeGentlyNoBtn?.setOnClickListener { channelAns(false) }
            dialog.setOnCancelListener { channelAns(false) }
            dialog.setView(v)
            dialog.setCancelable(false)
            alertDialog = dialog.show()
        }

        return super.showDialogForPermission(act)
    }
}