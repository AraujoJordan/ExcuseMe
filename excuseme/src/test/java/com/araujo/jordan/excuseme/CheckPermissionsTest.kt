package com.araujo.jordan.excuseme

import android.Manifest
import android.os.Build
import androidx.test.rule.GrantPermissionRule
import com.araujo.jordan.excuseme.view.InvisibleActivity
import junit.framework.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class CheckPermissionsTest {

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_SMS)

    @Test
    fun test01CheckNotGrantedPermission() {
        val activity = Robolectric.buildActivity(InvisibleActivity::class.java).get()
        assertFalse(ExcuseMe.doWeHavePermissionFor(activity, Manifest.permission.CAMERA))
    }

    @Test
    fun test02CheckGrantedPermission() {
        val activity = Robolectric.buildActivity(InvisibleActivity::class.java).get()
        assertTrue(ExcuseMe.doWeHavePermissionFor(activity, Manifest.permission.READ_SMS))
    }
}
