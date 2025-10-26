package net.lsafer.edgeseek.app.components.page.permissions

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.lsafer.edgeseek.app.Local
import net.lsafer.edgeseek.app.R
import net.lsafer.edgeseek.app.components.lib.ListDivider
import net.lsafer.edgeseek.app.components.lib.ListHeader
import net.lsafer.edgeseek.app.components.lib.ListSectionTitle

@Composable
context(local: Local)
fun PermissionsPage(modifier: Modifier = Modifier) {
    Scaffold(
        Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .then(modifier),
        snackbarHost = {
            SnackbarHost(local.snackbar)
        },
    ) { innerPadding ->
        PermissionsPageContent(Modifier.padding(innerPadding))
    }
}

@Composable
context(_: Local)
fun PermissionsPageContent(modifier: Modifier = Modifier) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .then(modifier)
    ) {
        ListHeader(title = stringResource(R.string.page_permissions_heading))
        ListSectionTitle(title = stringResource(R.string.mandatory))

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            PermissionsPage_ListItem_allow_restricted_permissions()
        }

        PermissionsPage_ListItem_display_over_other_apps()
        PermissionsPage_ListItem_write_system_settings()

        ListDivider()
        ListSectionTitle(title = stringResource(R.string.additional))
        PermissionsPage_ListItem_ignore_battery_optimizations()
        PermissionsPage_ListItem_accessibility_service()

        Spacer(Modifier.height(50.dp))
    }
}
