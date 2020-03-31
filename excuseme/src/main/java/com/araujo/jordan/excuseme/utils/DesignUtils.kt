package com.araujo.jordan.excuseme.utils


import android.content.Context
import android.graphics.Color.parseColor
import androidx.appcompat.content.res.AppCompatResources


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