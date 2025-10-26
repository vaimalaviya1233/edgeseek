package net.lsafer.edgeseek.app.components.wrapper

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.LayoutDirection.Ltr
import androidx.compose.ui.unit.LayoutDirection.Rtl
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.l10n.LocalStrings
import net.lsafer.edgeseek.app.l10n.Strings_all
import net.lsafer.edgeseek.app.l10n.strings.Strings_en
import net.lsafer.edgeseek.app.util.collectAndroidSystemLocale
import net.lsafer.edgeseek.app.util.langIsRTL

@Composable
context(local: Local)
fun AndroidUniLocaleProvider(content: @Composable () -> Unit) {
    val system by collectAndroidSystemLocale()
    val storage = local.repo.uiLang
    val locale = storage?.let { Locale(it) } ?: system
    val langTag = locale.toLanguageTag()
    val isRtl = langIsRTL(locale.language)

    // TODO use lyricist
    val strings = Strings_all[locale.language] ?: Strings_en

    local.infoStore.locale = locale
    local.infoStore.strings = strings

    LaunchedEffect(locale) {
        java.util.Locale.setDefault(locale.platformLocale)
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides if (isRtl) Rtl else Ltr,
        LocalStrings provides strings,
    ) {
        content()
    }
}
