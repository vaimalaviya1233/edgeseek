package net.lsafer.edgeseek.app.common

import android.content.Context
import android.provider.Settings
import kotlin.enums.enumEntries

/**
 * Returns the next enum value in declaration order, wrapping to the first entry.
 */
inline fun <reified T : Enum<T>> T.next(): T {
    val entries = enumEntries<T>()
    return entries[(ordinal + 1) % entries.size]
}

/**
 * Checks whether the app has all required system permissions.
 */
fun Context.isMandatoryPermissionsMet(): Boolean {
    return Settings.canDrawOverlays(this) &&
            Settings.System.canWrite(this)
}
