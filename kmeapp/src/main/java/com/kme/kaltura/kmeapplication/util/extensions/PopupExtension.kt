package com.kme.kaltura.kmeapplication.util.extensions

import android.content.Context
import android.view.Gravity
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import com.kme.kaltura.kmeapplication.R
import java.lang.reflect.Field
import java.lang.reflect.Method

fun View.popup(
    menu: Int,
    withIcons: Boolean = false,
    gravity: Int = Gravity.END,
    style: Int = R.style.AppTheme_PopupStyle
): PopupMenu {
    val wrapper: Context = ContextThemeWrapper(context, style)
    val popup = PopupMenu(wrapper, this, gravity)

    if (withIcons) {
        try {
            val fields: Array<Field> = popup.javaClass.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper: Any? = field.get(popup)
                    if (menuPopupHelper != null) {
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    popup.menuInflater.inflate(menu, popup.menu)
    return popup
}
