package net.lsafer.edgeseek.app.components.lib

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.R

@Composable
context(local: Local)
fun WizardScaffold(
    onCancel: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .then(modifier),
        snackbarHost = {
            SnackbarHost(local.snackbar)
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                content()
            }

            Surface(Modifier.fillMaxWidth()) {
                Row(Modifier.padding(vertical = 5.dp, horizontal = 75.dp)) {
                    TextButton(onCancel) {
                        Text(stringResource(R.string.back))
                    }
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    TextButton(onComplete) {
                        Text(stringResource(R.string.next), color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}
