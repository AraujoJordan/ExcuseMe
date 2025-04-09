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

package com.araujo.jordan.excuseme

import android.Manifest
import android.os.Build
import androidx.test.rule.GrantPermissionRule
import com.araujo.jordan.excuseme.view.InvisibleActivity
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

/**
 * Test permissions checks
 *
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@LooperMode(LooperMode.Mode.PAUSED)
class CheckPermissionsTest {

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(Manifest.permission.READ_SMS)

    /**
     * Test for doWeHavePermissionFor() method return denied
     */
    @Test
    fun test01CheckNotGrantedPermission() {
        val activity = Robolectric.buildActivity(InvisibleActivity::class.java).get()
        assertFalse(ExcuseMe.doWeHavePermissionFor(activity, Manifest.permission.CAMERA))
    }

    /**
     * Test for doWeHavePermissionFor() method return granted
     */
    @Test
    fun test02CheckGrantedPermission() {
        val activity = Robolectric.buildActivity(InvisibleActivity::class.java).get()
        assert(ExcuseMe.doWeHavePermissionFor(activity, Manifest.permission.READ_SMS))
    }
}
