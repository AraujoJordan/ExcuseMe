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

import androidx.activity.compose.setContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import com.araujo.jordan.excuseme.ExcuseMe.renameGenericPermission
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.view.InvisibleExcuseMeActivity

/**
 * Implementation Dialog to explain the reason for the permission should be granted.
 * This dialog will only be showed if the user denied a permission
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
class PosPermissionDialog {

    private var customRequest: ((type: DialogType, ((Boolean) -> Unit)) -> Unit)? = null

    constructor()
    constructor(
        titleAskAgain: String,
        reasonAskAgain: String,
        titleShowSettings: String,
        reasonShowSettings: String
    ) {
        this.titleShowSettings = titleShowSettings
        this.reasonShowSettings = reasonShowSettings
        this.titleAskAgain = titleAskAgain
        this.reasonAskAgain = reasonAskAgain
    }

    constructor(customDialogRequest: ((type: DialogType, ((Boolean) -> Unit)) -> Unit)) {
        this.customRequest = customDialogRequest
    }

    private var titleShowSettings: String? = null
    private var reasonShowSettings: String? = null
    private var titleAskAgain: String? = null
    private var reasonAskAgain: String? = null
    private var deniedPermissions = setOf<String>()
    var dialogType: DialogType = DialogType.EXPLAIN_AGAIN

    /**
     * Show PosDialog Permission (please method)
     * @param act Activity that will launch the dialog
     */
    fun showDialogForPermission(
        act: InvisibleExcuseMeActivity,
        permissions: Array<out String?>,
        callback: (Boolean) -> Unit
    ) {
        dialogType = if (deniedPermissions.firstOrNull {
                ActivityCompat.shouldShowRequestPermissionRationale(act, it)
            } is String)
            DialogType.EXPLAIN_AGAIN
        else
            DialogType.SHOW_SETTINGS

        if (customRequest != null) {
            customRequest?.invoke(dialogType) { callback(it) }
        } else {
            act.setContent {
                AlertDialog(
                    title = {
                        Text(
                            text = when (dialogType) {
                                DialogType.EXPLAIN_AGAIN -> titleAskAgain
                                DialogType.SHOW_SETTINGS -> titleShowSettings
                            } ?: stringResource(R.string.excuseme_generic_persmission_title)
                        )
                    },
                    text = {
                        Text(
                            text = when (dialogType) {
                                DialogType.EXPLAIN_AGAIN -> reasonAskAgain
                                DialogType.SHOW_SETTINGS -> reasonShowSettings
                            } ?: stringResource(
                                R.string.excuseme_generic_persmission_description,
                                permissions.joinToString { it?.renameGenericPermission().orEmpty() }
                            )
                        )
                    },
                    onDismissRequest = { callback(false) },
                    confirmButton = {
                        TextButton(
                            onClick = { callback(true) },
                        ) {
                            Text(stringResource(R.string.excuseme_continue_button))
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { callback(false) },
                        ) {
                            Text(stringResource(R.string.excuseme_not_now_button))
                        }
                    }
                )
            }
        }
    }

    /**
     * Set the permissions that were denied
     * @param denied list of permissions denied
     */
    fun setDeniedPermissions(denied: Set<String>) {
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