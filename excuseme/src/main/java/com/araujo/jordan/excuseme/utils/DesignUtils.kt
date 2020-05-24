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


package com.araujo.jordan.excuseme.utils


import android.content.Context
import android.graphics.Color.parseColor
import androidx.appcompat.content.res.AppCompatResources


/**
 * Design method that can be reused in differents parts of the project
 * @author Jordan L. Araujo Jr. (araujojordan)
 */
class DesignUtils {
    companion object {

        /**
         * Resolve a color from default app colors OR from Hexadeximal
         * @param context The context from the app
         * @param color could be a default color (like colorPrimary) or an hexdecimal color (like #FF0000)
         * @return the int representation of this color
         */
        fun resolveColor(context: Context, color: String): Int {
            return try {
                AppCompatResources.getColorStateList(
                    context,
                    context.resources.getIdentifier(
                        color,
                        "color", context.applicationContext.packageName
                    )
                ).defaultColor
            } catch (err: Exception) {
                parseColor(color)
            }
        }
    }

}