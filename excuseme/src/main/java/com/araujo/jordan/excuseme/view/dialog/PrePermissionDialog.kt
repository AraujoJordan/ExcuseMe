/**
 *
 * Copyright © 2025 Jordan Lira de Araujo Junior
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

import android.annotation.SuppressLint
import androidx.appcompat.app.AlertDialog
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.databinding.DialogGentlyAskBinding
import com.araujo.jordan.excuseme.utils.DesignUtils
import com.araujo.jordan.excuseme.view.InvisibleActivity

/**
 * Implementation Dialog to explain the reason for the permission should be granted.
 * This dialog will be showed before the permission request shows and will prevent if the user denied this one
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
class PrePermissionDialog : ExcuseMeDialog {

    private lateinit var binding: DialogGentlyAskBinding

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

    /**
     * Show PreDialog Permission (genlty method)
     * @param act Activity that will launch the dialog
     */
    @SuppressLint("InflateParams")
    override suspend fun showDialogForPermission(act: InvisibleActivity): Boolean {
        if (customRequest != null) {
            customRequest?.invoke { channelAns(it) }
        } else {
            val dialog = AlertDialog.Builder(act)
            val v = act.layoutInflater.inflate(R.layout.dialog_gently_ask, null)
            binding = DialogGentlyAskBinding.inflate(act.layoutInflater)
            binding.excuseMeGentlyTitle?.text = title
            binding.excuseMeGentlyDescriptionText?.text = reason
            binding.excuseMeGentlyYesBtn?.setOnClickListener { channelAns(true) }
            binding.excuseMeGentlyYesBtn?.setTextColor(DesignUtils.resolveColor(act, "colorPrimaryDark"))
            binding.excuseMeGentlyNoBtn?.setTextColor(DesignUtils.resolveColor(act, "colorPrimaryDark"))
            binding.excuseMeGentlyTitle?.setBackgroundColor(DesignUtils.resolveColor(act, "colorPrimary"))
            binding.excuseMeGentlyDescriptionText?.setTextColor(DesignUtils.resolveColor(act, "#0c0c0c"))
            binding.excuseMeGentlyNoBtn?.setOnClickListener { channelAns(false) }
            dialog.setOnCancelListener { channelAns(false) }
            dialog.setView(v)
            dialog.setCancelable(false)
            alertDialog = dialog.show()
        }

        return super.showDialogForPermission(act)
    }
}