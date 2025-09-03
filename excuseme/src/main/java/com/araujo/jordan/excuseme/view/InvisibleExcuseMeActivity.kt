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

package com.araujo.jordan.excuseme.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import com.araujo.jordan.excuseme.ExcuseMe
import com.araujo.jordan.excuseme.model.PermissionsStatus
import com.araujo.jordan.excuseme.view.dialog.DialogType
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Invisible screen that will handle the dialog permissions and it's results.
 * It won't block the user and it will finish itself after the result
 */
class InvisibleExcuseMeActivity : ComponentActivity() {

    private val PERMISSIONS_REQ = 4009
    val permissionsStatus = PermissionsStatus()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExcuseMe.getPreDialog().showDialogForPermission(
            this@InvisibleExcuseMeActivity,
            intent.getStringArrayExtra("permissions").orEmpty()

        ) { showPermission ->
            val permissions = intent.getStringArrayExtra("permissions")?.map { it }.orEmpty().toTypedArray()
            if (showPermission) {
                requestPermissions(permissions, PERMISSIONS_REQ)
            } else {
                permissionsStatus.denied.addAll(permissions)
                ExcuseMe.onPermissionResult(permissionsStatus)
                finish()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        intent.getStringArrayExtra("permissions")?.forEach {
            if (ExcuseMe.doWeHavePermissionFor(this, it))
                permissionsStatus.granted.add(it)
            else
                permissionsStatus.denied.add(it)
        }

        //This will make the ExcuseMe insist for the user give the asked permission
        if (permissionsStatus.denied.isNotEmpty()) {
            ExcuseMe.getPosDialog().setDeniedPermissions(permissionsStatus.denied)
            ExcuseMe.getPosDialog().showDialogForPermission(
                this@InvisibleExcuseMeActivity,
                permissions,
            ) { ans ->
                if (ans) {
                    when (ExcuseMe.getPosDialog().dialogType) {
                        DialogType.EXPLAIN_AGAIN -> onResume()
                        DialogType.SHOW_SETTINGS -> {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.data = Uri.fromParts("package", packageName, null)
                            startActivityForResult(intent, PERMISSIONS_REQ)
                        }
                    }
                } else {
                    ExcuseMe.clearPosDialog()
                }
            }
        }

        ExcuseMe.clearPosDialog()
        ExcuseMe.onPermissionResult(permissionsStatus)
        finish()
    }
}