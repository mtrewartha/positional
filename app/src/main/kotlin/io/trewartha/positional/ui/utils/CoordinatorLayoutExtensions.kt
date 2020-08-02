package io.trewartha.positional.ui.utils

import android.view.View
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

fun CoordinatorLayout.showSnackbar(
        @StringRes resId: Int,
        duration: Int = Snackbar.LENGTH_LONG,
        @StringRes actionResId: Int? = null,
        listener: ((View) -> Unit)? = null
) {
    var builder = Snackbar.make(this, resId, duration)
    if (actionResId != null) builder = builder.setAction(actionResId, listener)
    builder.show()
}

fun CoordinatorLayout.showSnackbar(
        text: String,
        duration: Int = Snackbar.LENGTH_LONG,
        actionText: String?,
        listener: ((View) -> Unit)? = null
) {
    var builder = Snackbar.make(this, text, duration)
    if (actionText != null) builder = builder.setAction(actionText, listener)
    builder.show()
}

fun CoordinatorLayout.showSnackbar(
        text: String,
        duration: Int = Snackbar.LENGTH_LONG,
        @StringRes actionResId: Int? = null,
        listener: ((View) -> Unit)? = null
) {
    var builder = Snackbar.make(this, text, duration)
    if (actionResId != null) builder = builder.setAction(actionResId, listener)
    builder.show()
}

fun CoordinatorLayout.showSnackbar(
        @StringRes resId: Int,
        duration: Int = Snackbar.LENGTH_LONG,
        actionText: String?,
        listener: ((View) -> Unit)? = null
) {
    var builder = Snackbar.make(this, resId, duration)
    if (actionText != null) builder = builder.setAction(actionText, listener)
    builder.show()
}
