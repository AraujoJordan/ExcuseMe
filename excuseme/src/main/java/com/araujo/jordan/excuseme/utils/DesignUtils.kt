package com.araujo.jordan.excuseme.utils


import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color.parseColor
import androidx.appcompat.content.res.AppCompatResources
import com.araujo.jordan.excuseme.R
import java.util.*

class DesignUtils {
    companion object {

        /**
         * Separated text using commas and "and'.
         */
        fun separatedText(context: Context, text: List<String>): String {
            var textFormatted = ""

            when (text.size) {
                1 -> textFormatted += text.first()
                else -> {
                    text.forEachIndexed { index, s ->
                        textFormatted += when (index) {
                            text.size - 2 -> s + " ${context.getString(R.string.trailingSeparator)} "
                            text.size - 1 -> s
                            else -> s + "${context.getString(R.string.separator)} "
                        }
                    }
                }
            }

            return textFormatted
        }

        /**
         * This function create a formatted version of permissions, letting easier to read and
         * put a sense on the strange android permissions syntax.
         * todo: This function only work for english, I have to make it universal
         */
        fun formattedPermissions(context: Context, permissions: List<String>): String {
            val permissionsFormatted = mutableListOf<String>()
            permissions.forEach {
                permissionsFormatted.add(
                    it.toLowerCase(Locale.getDefault()).removePrefix("android.permission.")
                        .replace("write", context.getString(R.string.write))
                        .replace("_", " ")
                )
            }
            return separatedText(context, permissionsFormatted)
        }

        /**
         * Get app name from library
         */
        fun getAppName(ctx: Context) =
            try {
                val applicationInfo =
                    ctx.packageManager.getApplicationInfo(ctx.applicationInfo.packageName, 0)
                ctx.packageManager.getApplicationLabel(applicationInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                "App"
            }


        fun resolveColor(context: Context, color: String): Int {
            return try {
                compatColor(
                    context, context.resources.getIdentifier(
                        color,
                        "color", context.applicationContext.packageName
                    )
                ).defaultColor
            } catch (err: Exception) {
                parseColor(color)
            }
        }

        fun compatColor(context: Context, imgRes: Int): ColorStateList =
            AppCompatResources.getColorStateList(context, imgRes)

        fun getPermissionIcon(context: Context, permission: String) =
            context.packageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA).icon
    }


}