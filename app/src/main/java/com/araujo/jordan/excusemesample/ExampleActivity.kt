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
package com.araujo.jordan.excusemesample

import android.Manifest.permission
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.araujo.jordan.excuseme.ExcuseMe
import com.araujo.jordan.excuseme.view.dialog.DialogType
import com.araujo.jordan.excusemesample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * Sample for the ExcuseMe library
 */
class ExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val green by lazy { AppCompatResources.getColorStateList(this, R.color.colorGreen) }
    val red by lazy { AppCompatResources.getColorStateList(this, R.color.colorRed) }

    val granted = "Granted"
    val denied = "Denied"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        updateTextsWithPermissions()

        ExcuseMe.couldYouHandlePermissionsForMe(this) { accept -> if (accept) updateTextsWithPermissions() }

        //Example of a simple permissions request with callback
        binding.audioPermissionButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(permission.RECORD_AUDIO) {
                updateTextsWithPermissions()
            }
        }

        //Example of multiple permissions request.
        binding.multiplePermissionsButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(
                permission.READ_CONTACTS,
                permission.CAMERA,
                permission.RECORD_AUDIO,
                permission.WRITE_CALENDAR
            ) {
                updateTextsWithPermissions()
            }
        }

        //Example of permission using the suspend function
        //and using the permission answer to update screen.
        binding.calendarPermissionButton.setOnClickListener {
            lifecycleScope.launch {
                val res =
                    ExcuseMe.couldYouGive(this@ExampleActivity)
                        .permissionFor(permission.WRITE_CALENDAR)

                binding.calendarPermissionsFeedback?.apply {
                    if (res) {
                        text = granted
                        setTextColor(green.defaultColor)
                    } else {
                        text = denied
                        setTextColor(red.defaultColor)
                    }
                }
            }
        }

//        //Example of a dialog BEFORE ask the permissions. This is good for your Play Store Vitals
//        //Source: https://developer.android.com/topic/performance/vitals/permissions
//        contactsPermissionButton.setOnClickListener {
//            lifecycleScope.launch {
//                ExcuseMe.couldYouGive(this@ExampleActivity)
//                    .gently(
//                        "Permission Request",
//                        "To easily connect with family and friends, allow the app access to your contacts"
//                    )
//                    .permissionFor(permission.READ_CONTACTS)
//                updateTextsWithPermissions()
//            }
//        }

        //Example of a dialog BEFORE ask the permissions. This is good for your Play Store Vitals
        //Source: https://developer.android.com/topic/performance/vitals/permissions
        binding.contactsPermissionButton.setOnClickListener {
            lifecycleScope.launch {
                val phones: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
                println(phones?.count)
                phones?.close()
                updateTextsWithPermissions()
            }
        }

        //Example of a CUSTOM dialog BEFORE ask the permissions. This is good for your Play Store Vitals
        //Source: https://developer.android.com/topic/performance/vitals/permissions
        binding.cameraPermissionButton.setOnClickListener {
            ExcuseMe.couldYouGive(this)
                .gently { result ->
                    val dialog = AlertDialog.Builder(this@ExampleActivity)
                    dialog.setTitle("Ask Permissions")
                    dialog.setMessage("The app will need permission to take a picture for scan a document")
                    dialog.setNegativeButton("Not now") { _, _ -> result(false) }
                    dialog.setPositiveButton("Continue") { _, _ -> result(true) }
                    dialog.setOnCancelListener { result(false) } //important
                    dialog.show()

                }
                .permissionFor(permission.CAMERA) {
                    updateTextsWithPermissions()
                }
        }

        //Example of a simple permissions request with callback
        binding.smsPermissionButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).please { type, result ->
                when (type) {
                    DialogType.EXPLAIN_AGAIN -> {
                        /** do you things**/
                    }
                    DialogType.SHOW_SETTINGS -> {
                        /** do you things**/
                    }
                }
            }.permissionFor(permission.SEND_SMS) {
                updateTextsWithPermissions()
            }
        }
    }

    private fun updateTextsWithPermissions() {
        changeTextViewWithPermission(binding.calendarPermissionsFeedback, permission.WRITE_CALENDAR)
        changeTextViewWithPermission(binding.cameraPermissionFeedback, permission.CAMERA)
        changeTextViewWithPermission(binding.audioPermissionFeedback, permission.RECORD_AUDIO)
        changeTextViewWithPermission(binding.contactPermissionsFeedback, permission.READ_CONTACTS)
        changeTextViewWithPermission(binding.smsPermissionFeedback, permission.SEND_SMS)
    }

    private fun changeTextViewWithPermission(textView: TextView?, permission: String) {
        textView?.apply {
            if (ExcuseMe.doWeHavePermissionFor(textView.context, permission)) {
                text = granted
                setTextColor(green.defaultColor)
            } else {
                text = denied
                setTextColor(red.defaultColor)
            }
        }
    }
}
