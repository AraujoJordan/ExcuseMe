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

package com.araujo.jordan.excuseme

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker

/**
 * Invisible screen that will handle the dialog permissions and it's results.
 * It won't block the user and it will finish itself after the result
 */
class InvisibleActivity : Activity(), ActivityCompat.OnRequestPermissionsResultCallback {

    private val PERMISSIONS_REQUEST_ID = 4002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        intent.getStringArrayExtra("permissions")?.let {
            ActivityCompat.requestPermissions(this, it, PERMISSIONS_REQUEST_ID)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionStatus = PermissionStatus()
        intent.getStringArrayExtra("permissions")?.forEach {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, it))
                permissionStatus.askGently.add(it)
            when (PermissionChecker.checkSelfPermission(this, it)) {
                PermissionChecker.PERMISSION_GRANTED -> permissionStatus.granted.add(it)
                else -> permissionStatus.denied.add(it)
            }
        }
        ExcuseMe.onPermissionResult(permissionStatus)
        finish()
    }

}