package net.lsafer.edgeseek.app.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale

@Composable
fun collectAndroidSystemLocale(): State<Locale> {
    val context = LocalContext.current
    return produceState(Locale.current) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
                    val locale = context?.resources?.configuration?.locales?.get(0)
                    if (locale != null) value = Locale(locale.toLanguageTag())
                }
            }
        }

        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_LOCALE_CHANGED))
        awaitDispose { context.unregisterReceiver(receiver) }
    }
}
