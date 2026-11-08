// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/components/OfflineBanner.kt
package com.chris.mobilewallet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chris.mobilewallet.ui.theme.WarningAmber

@Composable
fun OfflineBanner(isOffline: Boolean, modifier: Modifier = Modifier) {
    if (isOffline) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(WarningAmber)
                .padding(vertical = 8.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚡ Airplane Mode / Offline: Transfers will queue in outbox",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
