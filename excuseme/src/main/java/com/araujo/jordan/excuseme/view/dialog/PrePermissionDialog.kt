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
import com.araujo.jordan.excuseme.ExcuseMe.renameGenericPermission
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.view.InvisibleExcuseMeActivity

/**
 * Implementation Dialog to explain the reason for the permission should be granted.
 * This dialog will be showed before the permission request shows and will prevent if the user denied this one
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
class PrePermissionDialog {

    var title: String? = null
    var reason: String? = null

    private var customRequest: (((Boolean) -> Unit) -> Unit)? = null

    constructor()
    constructor(title: String, reason: String) {
        this.title = title
        this.reason = reason
    }

    /**
     * Constructor to let the user implement his own dialog/code to show before the
     * system permission dialog.
     *
     * Attention: This one can lead to errors if the user forget to implement the boolean callback inside.
     *
     * @param customDialogRequest run the code that user want. It must return the boolean callback inside to continue.
     */
    constructor(customDialogRequest: (((Boolean) -> Unit) -> Unit)) {
        this.customRequest = customDialogRequest
    }

    /**
     * Show PreDialog Permission (genlty method)
     * @param act Activity that will launch the dialog
     */
    fun showDialogForPermission(
        act: InvisibleExcuseMeActivity,
        permissions: Array<out String?>,
        result: (Boolean) -> Unit
    ) {
        customRequest?.let { customRequest ->
            customRequest(result)
        } ?: act.setContent {
            AlertDialog(
                title = {
                    Text(
                        text = title
                            ?: stringResource(R.string.excuseme_generic_persmission_title)
                    )
                },
                text = {
                    Text(
                        text = reason ?: stringResource(
                            R.string.excuseme_generic_persmission_description,
                            permissions.joinToString { it?.renameGenericPermission().orEmpty() }
                        )
                    )
                },
                onDismissRequest = { result(false) },
                confirmButton = {
                    TextButton(onClick = { result(true) }) {
                        Text(stringResource(R.string.excuseme_continue_button))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { result(false) }) {
                        Text(stringResource(R.string.excuseme_not_now_button))
                    }
                }
            )
        }
    }
}