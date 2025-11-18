package net.lsafer.edgeseek.app.components.wrapper

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import net.lsafer.edgeseek.app.*

@Composable
context(local: Local, app: ComponentActivity, navCtrl: AppNavController)
fun AppWindowCompat(content: @Composable () -> Unit) {
    val ctx = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val isSystemDarkTheme = isSystemInDarkTheme()
    val uiColors = local.repo.uiColors

    fun onLeaveRequest() = coroutineScope.launch {
        val result = local.snackbar.showSnackbar(
            message = ctx.getString(R.string.exit_application_qm),
            actionLabel = ctx.getString(R.string.yes),
            withDismissAction = true,
            duration = SnackbarDuration.Short,
        )

        if (result == SnackbarResult.ActionPerformed)
            app.finish()
    }

    BackHandler {
        if (!navCtrl.back())
            onLeaveRequest()
    }

    LaunchedEffect(uiColors, isSystemDarkTheme) {
        val isDark = when (uiColors) {
            UI_COLORS_BLACK, UI_COLORS_DARK -> true
            UI_COLORS_LIGHT, UI_COLORS_WHITE -> false
            else -> isSystemDarkTheme
        }

        app.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ) { isDark },
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ) { isDark },
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTouch()
    ) {
        content()
    }
}

private fun Modifier.clearFocusOnTouch(): Modifier {
    return composed {
        val focusManager = LocalFocusManager.current
        pointerInput(Unit) {
            detectTapGestures {
                focusManager.clearFocus()
            }
        }
    }
}
