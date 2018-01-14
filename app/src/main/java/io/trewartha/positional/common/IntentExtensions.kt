package io.trewartha.positional.common

import android.content.Intent
import java.util.*

fun Intent.getUUIDExtra(name: String): UUID? {
    val stringExtra = getStringExtra(name)
    return if (stringExtra == null) null else UUID.fromString(stringExtra)
}