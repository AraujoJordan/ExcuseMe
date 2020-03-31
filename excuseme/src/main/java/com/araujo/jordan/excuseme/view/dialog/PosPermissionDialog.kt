package com.araujo.jordan.excuseme.view.dialog

import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.utils.DesignUtils
import com.araujo.jordan.excuseme.view.InvisibleActivity
import kotlinx.android.synthetic.main.dialog_gently_ask.view.*

class PosPermissionDialog : ExcuseMeDialog {

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

            v.excuseMeGentlyTitle?.text =
                when (dialogType) {
                    DialogType.EXPLAIN_AGAIN -> title
                    DialogType.SHOW_SETTINGS -> titleShowSettings
                }
            v.excuseMeGentlyDescriptionText?.text =
                when (dialogType) {
                    DialogType.EXPLAIN_AGAIN -> reason
                    DialogType.SHOW_SETTINGS -> reasonShowSettings
                }
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

    fun setPermissions(denied: List<String>) {
        this.deniedPermissions = denied
    }

}

enum class DialogType {
    EXPLAIN_AGAIN,
    SHOW_SETTINGS
}