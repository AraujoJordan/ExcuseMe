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
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.araujo.jordan.excuseme.ExcuseMe
import com.araujo.jordan.excuseme.model.PermissionStatus
import com.araujo.jordan.excuseme.view.dialog.DialogType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Invisible screen that will handle the dialog permissions and it's results.
 * It won't block the user and it will finish itself after the result
 */
class InvisibleActivity : AppCompatActivity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private val PERMISSIONS_REQUEST_ID = 4002
    private val SETTINGS_REQUEST_ID = 4009

    /**
     * Create the permission request flow
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        CoroutineScope(Dispatchers.Main.immediate).launch {
            prePermission()
        }
    }

    /**
     * Check the permissions result from the settings page and send the callback
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (SETTINGS_REQUEST_ID == requestCode) CoroutineScope(Dispatchers.Main.immediate).launch {
            posPermission()
        }
    }

    /**
     * Check the permissions result and send the callback
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            posPermission()
        }
    }

    private suspend fun prePermission() {
        Log.d("InvisibleActivity", "prePermission()")

        intent.getStringArrayExtra("permissions")?.let { permissions ->

            val showPermission =
                if (ExcuseMe.getPreDialog().showDialog) {
                    ExcuseMe.getPreDialog().showDialogForPermission(this@InvisibleActivity)
                } else {
                    true
                }

            ExcuseMe.clearPreDialog()

            if (showPermission)
                ActivityCompat.requestPermissions(
                    this@InvisibleActivity,
                    permissions,
                    PERMISSIONS_REQUEST_ID
                )
            else
                finish()
        }
    }

    private suspend fun posPermission() {
        val permissionStatus = PermissionStatus()

        intent.getStringArrayExtra("permissions")?.forEach {
            if (ExcuseMe.doWeHavePermissionFor(this, it))
                permissionStatus.granted.add(it)
            else
                permissionStatus.denied.add(it)
        }


        //This will make the ExcuseMe insist for the user give the asked permission
        if (ExcuseMe.getPosDialog().showDialog && permissionStatus.denied.isNotEmpty()) {
            ExcuseMe.getPosDialog().setDeniedPermissions(permissionStatus.denied)
            val ans = ExcuseMe.getPosDialog()
                .showDialogForPermission(this@InvisibleActivity)
            if (ans) {
                when (ExcuseMe.getPosDialog().dialogType) {
                    DialogType.EXPLAIN_AGAIN -> prePermission()
                    DialogType.SHOW_SETTINGS -> {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package", packageName, null)
                        startActivityForResult(intent, SETTINGS_REQUEST_ID)
                    }
                }
                return
            } else {
                ExcuseMe.clearPosDialog()
            }
        }

        ExcuseMe.clearPosDialog()
        ExcuseMe.onPermissionResult(permissionStatus)
        finish()
    }

}