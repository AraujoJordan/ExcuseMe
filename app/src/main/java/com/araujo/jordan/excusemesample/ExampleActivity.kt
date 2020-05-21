package com.araujo.jordan.excusemesample

import android.Manifest.permission
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.araujo.jordan.excuseme.ExcuseMe
import com.araujo.jordan.excuseme.view.dialog.DialogType
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExampleActivity : AppCompatActivity() {

    val green by lazy { AppCompatResources.getColorStateList(this, R.color.colorGreen) }
    val red by lazy { AppCompatResources.getColorStateList(this, R.color.colorRed) }

    val granted = "Granted"
    val denied = "Denied"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        updateTextsWithPermissions()

        //Example of a simple permissions request with callback
        audioPermissionButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(permission.RECORD_AUDIO) {
                updateTextsWithPermissions()
            }
        }

        //Example of multiple permissions request.
        multiplePermissionsButton.setOnClickListener {
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
        calendarPermissionButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main.immediate).launch {
                val res =
                    ExcuseMe.couldYouGive(this@ExampleActivity)
                        .permissionFor(permission.WRITE_CALENDAR)

                calendarPermissionsFeedback?.apply {
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

        //Example of a dialog BEFORE ask the permissions. This is good for your Play Store Vitals
        //Source: https://developer.android.com/topic/performance/vitals/permissions
        contactsPermissionButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main.immediate).launch {
                ExcuseMe.couldYouGive(this@ExampleActivity)
                    .gently(
                        "Permission Request",
                        "To easily connect with family and friends, allow the app access to your contacts"
                    )
                    .permissionFor(permission.READ_CONTACTS)
                updateTextsWithPermissions()
            }
        }

        //Example of a CUSTOM dialog BEFORE ask the permissions. This is good for your Play Store Vitals
        //Source: https://developer.android.com/topic/performance/vitals/permissions
        cameraPermissionButton.setOnClickListener {
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
        smsPermissionButton.setOnClickListener {
//            ExcuseMe.couldYouGive(this).please(
//                explainAgainTitle = "Permission is necessary",
//                explainAgainExplanation = "The app need this permission to send the automatic SMS",
//                showSettingsTitle = "Set permission in Settings",
//                showSettingsExplanation = "The app will open the settings to change the permission from there"
//            ).permissionFor(permission.SEND_SMS) {
//                updateTextsWithPermissions()
//            }
            ExcuseMe.couldYouGive(this).please { type, result ->
                when (type) {
                    DialogType.EXPLAIN_AGAIN -> {
                        /** do you things**/
                    }
                    DialogType.SHOW_SETTINGS -> {
                        /** do you things**/
                    }
                }
                result.invoke(true) //continue
                // or
                result.invoke(false) //continue

            }.permissionFor(permission.SEND_SMS) {
                updateTextsWithPermissions()
            }
        }
    }

    private fun updateTextsWithPermissions() {
        changeTextViewWithPermission(calendarPermissionsFeedback, permission.WRITE_CALENDAR)
        changeTextViewWithPermission(cameraPermissionFeedback, permission.CAMERA)
        changeTextViewWithPermission(audioPermissionFeedback, permission.RECORD_AUDIO)
        changeTextViewWithPermission(contactPermissionsFeedback, permission.READ_CONTACTS)
        changeTextViewWithPermission(smsPermissionFeedback, permission.SEND_SMS)
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
