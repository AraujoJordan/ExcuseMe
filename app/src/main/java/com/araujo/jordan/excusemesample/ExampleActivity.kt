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
import android.R.color.holo_green_light
import android.R.color.holo_red_light
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.araujo.jordan.excuseme.ExcuseMe
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.view.dialog.DialogType
import com.araujo.jordan.excusemesample.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

/**
 * Sample for the ExcuseMe library
 */
internal class ExampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    val green by lazy { AppCompatResources.getColorStateList(this, holo_green_light) }
    val red by lazy { AppCompatResources.getColorStateList(this, holo_red_light) }

    val granted = "Granted"
    val denied = "Denied"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        updateTextsWithPermissions()

//        ExcuseMe.couldYouHandlePermissionsForMe(this) { accept -> if (accept) updateTextsWithPermissions() }

        //Example of permission using the suspend function
        //and using the permission answer to update screen.
        binding.calendarPermissionButton.setOnClickListener {
            lifecycleScope.launch {
                val isGranted: Boolean = ExcuseMe.couldYouGive(this@ExampleActivity)
                    .permissionFor(permission.WRITE_CALENDAR)

                updateTextsWithPermissions()
            }
        }

        //Example of a dialog BEFORE ask the permissions. This is good for your Play Store Vitals
        //Source: https://developer.android.com/topic/performance/vitals/permissions
        binding.contactsPermissionButton.setOnClickListener {
            lifecycleScope.launch {
                val isGranted = ExcuseMe.couldYouGive(this@ExampleActivity)
                    .permissionFor(permission.READ_CONTACTS)

                if (isGranted) {
                    val phones: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )
                    println(phones?.count)
                    phones?.close()
                }

                updateTextsWithPermissions()
            }
        }

        //Example of a CUSTOM dialog BEFORE ask the permissions. This is good for your Play Store Vitals
        //Source: https://developer.android.com/topic/performance/vitals/permissions
        binding.cameraPermissionButton.setOnClickListener {
            lifecycleScope.launch {
                ExcuseMe.couldYouGive(this@ExampleActivity)
                    .gently { result ->
                        AlertDialog.Builder(this@ExampleActivity).apply {
                            setTitle("Permission to Camera Example")
                            setMessage("This is a custom dialog asking camera permission")
                            setPositiveButton(R.string.excuseme_continue_button) { _, _ ->
                                result(
                                    true
                                )
                            }
                            setNegativeButton(R.string.excuseme_not_now_button) { _, _ ->
                                result(
                                    false
                                )
                            }
                            setOnCancelListener { result(false) }
                        }.create().show()
                    }
                    .permissionFor(permission.CAMERA) {
                        updateTextsWithPermissions()
                    }
            }
        }

        //Example of a simple permissions request with callback
        binding.audioPermissionButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(permission.RECORD_AUDIO) {
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
                updateTextsWithPermissions()
            }.permissionFor(permission.SEND_SMS) {
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
