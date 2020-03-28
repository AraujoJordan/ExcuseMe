package com.araujo.jordan.excuseme.view

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.araujo.jordan.excuseme.R
import com.araujo.jordan.excuseme.utils.DesignUtils
import com.araujojordan.ktlist.KtList
import com.araujojordan.ktlist.recycleviewLayoutManagers.SupportGridLayoutManager
import kotlinx.android.synthetic.main.dialog_gently_ask.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

class GentlyDialog(
    vararg reasons: String
) {

    private val reasons: List<String> = reasons.asList()
    private var channel: Channel<Boolean>? = null
    private var alertDialog: AlertDialog? = null

    suspend fun showDialogForPermission(
        act: AppCompatActivity,
        vararg permission: String
    ): Boolean {

        val dialog = AlertDialog.Builder(act)
        val v = act.layoutInflater.inflate(R.layout.dialog_gently_ask, null)

        val permissionsIcons = mutableListOf<Int>()
        permission.forEachIndexed { index, s ->
            permissionsIcons.add(DesignUtils.getPermissionIcon(act, s))
//            if (index < permission.size - 1)
            permissionsIcons.add(R.drawable.ic_add)
        }
        v.excuseMeGentlyIconsGrid.adapter = KtList(
            permissionsIcons,
            R.layout.excuseme_permission_icon,
            SupportGridLayoutManager(act, 5)
        ) { id, itemView ->
            (itemView as ImageView).setImageResource(id)
        }


        v.excuseMeGentlyDescriptionText.text = generateText(act, *permission)
        v.excuseMeGentlyYesBtn.setOnClickListener { channelAns(true) }
        v.excuseMeGentlyYesBtn.setTextColor(DesignUtils.resolveColor(act, "colorPrimaryDark"))
        v.excuseMeGentlyNoBtn.setTextColor(DesignUtils.resolveColor(act, "colorPrimaryDark"))
        v.excuseMeGentlyIconsGrid.setBackgroundColor(DesignUtils.resolveColor(act, "colorPrimary"))
        v.excuseMeGentlyDescriptionText.setTextColor(DesignUtils.resolveColor(act, "#0c0c0c"))
        v.excuseMeGentlyNoBtn.setOnClickListener { channelAns(false) }
        dialog.setOnCancelListener { channelAns(false) }
        dialog.setView(v)
        dialog.setCancelable(false)
        alertDialog = dialog.show()

        if (channel == null) channel = Channel()
        val status = channel?.receive() ?: false
        channel = null

        return status
    }

    private fun generateText(context: Context, vararg permission: String) = String.format(
        context.resources.getString(R.string.excuseme_gently_description),
        DesignUtils.separatedText(context, reasons),
        DesignUtils.getAppName(context),
        DesignUtils.formattedPermissions(context, permission.asList())
    )

    private fun channelAns(chanelAns: Boolean) {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            channel?.send(chanelAns)
            alertDialog?.dismiss()
            alertDialog = null
        }
    }


}