// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/screens/HistoryScreen.kt
package com.chris.mobilewallet.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chris.mobilewallet.domain.model.Transfer

@Composable
fun HistoryScreen(
    transfers: List<Transfer>,
    onBack: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Transaction History", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }

            if (transfers.isEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "No transaction history recorded yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                    items(transfers) { tx ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = "To: ${tx.recipientPhone.formattedE164}", style = MaterialTheme.typography.bodyLarge)
                                Text(text = "Amount: ${tx.amount.formatFormattedString()}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Status: ${tx.state.name}", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
