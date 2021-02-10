package com.kme.kaltura.kmeapplication.util.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import com.kme.kaltura.kmeapplication.R

inline fun Context.alert(
    title: CharSequence? = null,
    message: CharSequence? = null,
    withPassInput: Boolean = false,
    withTextInput: Boolean = false,
    func: AlertDialogHelper.() -> Unit
): AlertDialog {
    return AlertDialogHelper(this, title, message, withPassInput, withTextInput, null).apply {
        func()
    }.create()
}

inline fun Context.alert(
    titleResource: Int = 0,
    messageResource: Int = 0,
    withPassInput: Boolean = false,
    withTextInput: Boolean = false,
    func: AlertDialogHelper.() -> Unit
): AlertDialog {
    val title = if (titleResource == 0) null else getString(titleResource)
    val message = if (messageResource == 0) null else getString(messageResource)
    return AlertDialogHelper(this, title, message, withPassInput, withTextInput, null).apply {
        func()
    }.create()
}

inline fun Context.radioDialog(
    titleResource: Int = 0,
    messageResource: Int = 0,
    variants: List<String>,
    func: AlertDialogHelper.() -> Unit
): AlertDialog {
    val title = if (titleResource == 0) null else getString(titleResource)
    val message = if (messageResource == 0) null else getString(messageResource)
    return AlertDialogHelper(
        context = this,
        title,
        message,
        withPassInput = false,
        withTextInput = false,
        variants
    ).apply {
        func()
    }.create()
}

@SuppressLint("InflateParams")
class AlertDialogHelper(
    context: Context,
    title: CharSequence?,
    message: CharSequence?,
    private val withPassInput: Boolean,
    private val withTextInput: Boolean,
    private val variants: List<String>?
) {

    private val dialogView: View by lazyFast {
        LayoutInflater.from(context).inflate(R.layout.dialog_default_info, null)
    }

    private val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        .setView(dialogView)

    private val title: TextView by lazyFast {
        dialogView.findViewById(R.id.dialogInfoTitleTextView)
    }

    val inputPassword: AppCompatEditText by lazyFast {
        dialogView.findViewById(R.id.dialogPassInputEditText)
    }

    val inputText: AppCompatEditText by lazyFast {
        dialogView.findViewById(R.id.dialogTextInputEditText)
    }

    val radioGroup: RadioGroup by lazyFast {
        dialogView.findViewById(R.id.radioGroup)
    }

    private val message: TextView by lazyFast {
        dialogView.findViewById(R.id.dialogInfoMessageTextView)
    }

    private val positiveButton: Button by lazyFast {
        dialogView.findViewById(R.id.dialogInfoPositiveButton)
    }

    private val negativeButton: Button by lazyFast {
        dialogView.findViewById(R.id.dialogInfoNegativeButton)
    }

    private val neutralButton: Button by lazyFast {
        dialogView.findViewById(R.id.dialogInfoNeutralButton)
    }

    private var dialog: AlertDialog? = null

    var cancelable: Boolean = true

    init {
        this.title.text = title
        this.message.text = message

        variants?.size?.let {
            for (i in 0 until it) {
                with(RadioButton(context)) {
                    id = i
                    text = variants[i]
                    radioGroup.addView(this)
                }
            }
            radioGroup.check(0)
        }
    }

    fun positiveButton(@StringRes textResource: Int, func: (() -> Unit)? = null) {
        with(positiveButton) {
            text = builder.context.getString(textResource)
            setClickListenerToDialogButton(func)
        }
    }

    fun positiveButton(text: CharSequence, func: (() -> Unit)? = null) {
        with(positiveButton) {
            this.text = text
            setClickListenerToDialogButton(func)
        }
    }

    fun negativeButton(@StringRes textResource: Int, func: (() -> Unit)? = null) {
        with(negativeButton) {
            text = builder.context.getString(textResource)
            setClickListenerToDialogButton(func)
        }
    }

    fun negativeButton(text: CharSequence, func: (() -> Unit)? = null) {
        with(negativeButton) {
            this.text = text
            setClickListenerToDialogButton(func)
        }
    }

    fun neutralButton(@StringRes textResource: Int, func: (() -> Unit)? = null) {
        with(neutralButton) {
            text = builder.context.getString(textResource)
            setClickListenerToDialogButton(func)
        }
    }

    fun neutralButton(text: CharSequence, func: (() -> Unit)? = null) {
        with(neutralButton) {
            this.text = text
            setClickListenerToDialogButton(func)
        }
    }

    fun inputText(text: String) {
        with(inputText) {
            this.setText(text)
        }
    }

    fun onCancel(func: () -> Unit) {
        builder.setOnCancelListener { func() }
    }

    fun create(): AlertDialog {
        title.goneIfTextEmpty()
        message.goneIfTextEmpty()
        positiveButton.goneIfTextEmpty()
        negativeButton.goneIfTextEmpty()
        neutralButton.goneIfTextEmpty()

        if (withPassInput) {
            inputPassword.visible()
        } else {
            inputPassword.gone()
        }

        if (withTextInput) {
            inputText.visible()
        } else {
            inputText.gone()
        }

        radioGroup.goneIfEmpty()

        dialog = builder
            .setCancelable(cancelable)
            .create()
        return dialog as AlertDialog
    }

    private fun Button.setClickListenerToDialogButton(func: (() -> Unit)?) {
        setOnClickListener {
            func?.invoke()
            dialog?.dismiss()
        }
    }
}

fun AlertDialog?.hideIfExist() {
    if (this != null && this.isShowing) {
        this.cancel()
    }
}

/**
 * Implementation of lazy that is not thread safe. Useful when you know what thread you will be
 * executing on and are not worried about synchronization.
 */
private fun <T> lazyFast(operation: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE) {
    operation()
}