// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/components/QuickActionGrid.kt
package com.chris.mobilewallet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class QuickAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String
)

@Composable
fun QuickActionGrid(
    onActionClick: (QuickAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = listOf(
        QuickAction("send", "Send Money", "M-Pesa / Phone", "💸"),
        QuickAction("paybill", "Lipa na M-Pesa", "Paybill & Till", "🏷️"),
        QuickAction("airtime", "Buy Airtime", "Safaricom / Airtel", "📱"),
        QuickAction("bank", "Bank Transfer", "KCB / Equity / NCBA", "🏦")
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "QUICK SERVICES",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.take(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    onClick = { onActionClick(action) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.drop(2).forEach { action ->
                QuickActionCard(
                    action = action,
                    onClick = { onActionClick(action) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    action: QuickAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = action.iconEmoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = action.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
