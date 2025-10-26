package net.lsafer.edgeseek.app.support

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.intl.Locale
import net.lsafer.edgeseek.app.l10n.strings.Strings_en

class InfoStore {
    var locale by mutableStateOf(Locale.current)
    var strings by mutableStateOf(Strings_en)
}
