package net.lsafer.edgeseek.app.components.wrapper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.util.collectAndroidSystemLocale
import net.lsafer.edgeseek.app.util.langIsRTL

@Composable
context(local: Local)
fun AndroidUniLocaleProvider(content: @Composable () -> Unit) {
    val system by collectAndroidSystemLocale()
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
