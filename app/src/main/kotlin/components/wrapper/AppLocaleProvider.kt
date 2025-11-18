package net.lsafer.edgeseek.app.components.wrapper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import net.lsafer.edgeseek.app.Local

@Composable
context(local: Local)
fun AppLocaleProvider(content: @Composable () -> Unit) {
    val system by collectSystemLocale()
    val storage = local.repo.uiLang
    val locale = storage?.let { Locale(it) } ?: system
    val isRtl = langIsRTL(locale.language)

    LaunchedEffect(locale) {
        java.util.Locale.setDefault(locale.platformLocale)
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) Rtl else Ltr,
    ) {
        content()
    }
}

@Composable
private fun collectSystemLocale(): State<Locale> {
    val ctx = LocalContext.current
    return produceState(Locale.current) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
                    val locale = context?.resources?.configuration?.locales?.get(0)
                    if (locale != null) value = Locale(locale.toLanguageTag())
                }
            }
        }

        ctx.registerReceiver(receiver, IntentFilter(Intent.ACTION_LOCALE_CHANGED))
        awaitDispose { ctx.unregisterReceiver(receiver) }
    }
}

private fun langIsRTL(languageTag: String): Boolean {
    val language = languageTag
        .splitToSequence('_', '-')
        .firstOrNull()

    return when (language) {
        "ae", /* Avestan */
        "ar", /* 'العربية', Arabic */
        "arc",  /* Aramaic */
        "bcc",  /* 'بلوچی مکرانی', Southern Balochi */
        "bqi",  /* 'بختياري', Bakthiari */
        "ckb",  /* 'Soranî / کوردی', Sorani */
        "dv",   /* Dhivehi */
        "fa",   /* 'فارسی', Persian */
        "glk",  /* 'گیلکی', Gilaki */
        "ku",   /* 'Kurdî / كوردی', Kurdish */
        "mzn",  /* 'مازِرونی', Mazanderani */
        "nqo",  /* N'Ko */
        "pnb",  /* 'پنجابی', Western Punjabi */
        "prs",  /* 'دری', Darī */
        "ps",   /* 'پښتو', Pashto, */
        "sd",   /* 'سنڌي', Sindhi */
        "ug",   /* 'Uyghurche / ئۇيغۇرچە', Uyghur */
        "ur",    /* 'اردو', Urdu */
        -> true

        else -> false
    }
}
