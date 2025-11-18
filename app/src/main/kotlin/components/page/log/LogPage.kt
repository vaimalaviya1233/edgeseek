package net.lsafer.edgeseek.app.components.page.log

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
context(local: Local)
fun LogPage(modifier: Modifier = Modifier) {
    val logs = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val p = Runtime.getRuntime().exec("logcat")

            try {
                p.inputStream
                    .bufferedReader()
                    .lineSequence()
                    .forEach {
                        logs += it
                        yield()
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    p.destroyForcibly()
                } else {
                    p.destroy()
                }
            }
        }
    }

    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .then(modifier),
        snackbarHost = {
            SnackbarHost(local.snackbar)
        },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.page_log_heading)) }
            )
        }
    ) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState(), reverseScrolling = true)
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            for ((i, log) in logs.withIndex()) {
                Text(
                    text = log.trim(),
                    fontSize = 12.sp,
                    modifier = Modifier.background(
                        color = if (i % 2 == 0) Color.Gray else Color.DarkGray,
                    ),
                    fontFamily = FontFamily.Monospace,
                )
            }
        }
    }
}
