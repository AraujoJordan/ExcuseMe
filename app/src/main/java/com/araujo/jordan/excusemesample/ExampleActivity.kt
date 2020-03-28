package com.araujo.jordan.excusemesample

import android.Manifest.permission
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.araujo.jordan.excuseme.ExcuseMe
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

        calendarPermissionButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main.immediate).launch {
                val res = ExcuseMe.couldYouGive(this@ExampleActivity)
                    .gently("save an event in calendar").permissionFor(permission.WRITE_CALENDAR)

                calendarPermissionsFeedback?.apply {
                    if (res.granted.contains(permission.WRITE_CALENDAR)) {
                        text = granted
                        setTextColor(green.defaultColor)
                    } else {
                        text = denied
                        setTextColor(red.defaultColor)
                    }
                }
            }
        }
        allPermissionsButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(
                permission.READ_CONTACTS, permission.CAMERA, permission.RECORD_AUDIO
            ) {
                updateTextsWithPermissions()
            }
        }
        contactsPermissionButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main.immediate).launch {
                ExcuseMe.couldYouGive(this@ExampleActivity).permissionFor(permission.READ_CONTACTS)
                updateTextsWithPermissions()
            }
        }
        audioPermissionButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(permission.RECORD_AUDIO) {
                updateTextsWithPermissions()
            }
        }
        cameraPermissionButton.setOnClickListener {
            ExcuseMe.couldYouGive(this).permissionFor(permission.CAMERA) {
                updateTextsWithPermissions()
            }
        }
    }

    private fun updateTextsWithPermissions() {
        Log.d("MainActivity", "updateTextsWithPermissions()")




        calendarPermissionsFeedback?.apply {
            if (ExcuseMe.doWeHavePermissionFor(this.context, permission.WRITE_CALENDAR)) {
                text = granted
                setTextColor(green.defaultColor)
            } else {
                text = denied
                setTextColor(red.defaultColor)
            }
        }

        cameraPermissionFeedback?.apply {
            if (ExcuseMe.doWeHavePermissionFor(this.context, permission.CAMERA)) {
                text = granted
                setTextColor(green.defaultColor)
            } else {
                text = denied
                setTextColor(red.defaultColor)
            }
        }

        audioPermissionFeedback?.apply {
            if (ExcuseMe.doWeHavePermissionFor(this.context, permission.RECORD_AUDIO)) {
                text = granted
                setTextColor(green.defaultColor)
            } else {
                text = denied
                setTextColor(red.defaultColor)
            }
        }

        contactPermissionsFeedback?.apply {
            if (ExcuseMe.doWeHavePermissionFor(this.context, permission.READ_CONTACTS)) {
                text = granted
                setTextColor(green.defaultColor)
            } else {
                text = denied
                setTextColor(red.defaultColor)
            }
        }
    }
}
