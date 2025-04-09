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
import androidx.core.app.ActivityCompat
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.databinding.DialogGentlyAskBinding
import com.araujo.jordan.excuseme.utils.DesignUtils
import com.araujo.jordan.excuseme.view.InvisibleActivity

/**
 * Implementation Dialog to explain the reason for the permission should be granted.
 * This dialog will only be showed if the user denied a permission
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
class PosPermissionDialog : ExcuseMeDialog {

    private lateinit var binding: DialogGentlyAskBinding

    private var customRequest: ((type: DialogType, ((Boolean) -> Unit)) -> Unit)? = null

    constructor() : super(false)
    constructor(
        titleAskAgain: String,
        reasonAskAgain: String,
        titleShowSettings: String,
        reasonShowSettings: String
    ) : super(titleAskAgain, reasonAskAgain) {
        this.titleShowSettings = titleShowSettings
        this.reasonShowSettings = reasonShowSettings
    }

    constructor(customDialogRequest: ((type: DialogType, ((Boolean) -> Unit)) -> Unit)) : super(true) {
        this.customRequest = customDialogRequest
    }

    private var titleShowSettings: String? = null
    private var reasonShowSettings: String? = null
    private var deniedPermissions = listOf<String>()
    var dialogType: DialogType = DialogType.EXPLAIN_AGAIN

    /**
     * Show PosDialog Permission (please method)
     * @param act Activity that will launch the dialog
     */
    @SuppressLint("InflateParams")
    override suspend fun showDialogForPermission(act: InvisibleActivity): Boolean {

        dialogType = if (deniedPermissions.firstOrNull {
                ActivityCompat.shouldShowRequestPermissionRationale(act, it)
            } is String)
            DialogType.EXPLAIN_AGAIN
        else
            DialogType.SHOW_SETTINGS

        if (customRequest != null) {
            customRequest?.invoke(dialogType) { channelAns(it) }
        } else {
            val dialog = AlertDialog.Builder(act)
            val v = act.layoutInflater.inflate(R.layout.dialog_gently_ask, null)
            binding = DialogGentlyAskBinding.inflate(act.layoutInflater)
            binding.excuseMeGentlyTitle?.text =
                when (dialogType) {
                    DialogType.EXPLAIN_AGAIN -> title
                    DialogType.SHOW_SETTINGS -> titleShowSettings
                }
            binding.excuseMeGentlyDescriptionText?.text =
                when (dialogType) {
                    DialogType.EXPLAIN_AGAIN -> reason
                    DialogType.SHOW_SETTINGS -> reasonShowSettings
                }
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

    /**
     * Set the permissions that were denied
     * @param denied list of permissions denied
     */
    fun setDeniedPermissions(denied: List<String>) {
        this.deniedPermissions = denied
    }

}

/**
 * Types of Dialog
 */
enum class DialogType {
    EXPLAIN_AGAIN,
    SHOW_SETTINGS
}