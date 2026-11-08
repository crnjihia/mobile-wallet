// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/screens/HomeScreen.kt
package com.chris.mobilewallet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chris.mobilewallet.core.Money
import com.chris.mobilewallet.domain.model.Transfer
import com.chris.mobilewallet.ui.components.BalanceCard
import com.chris.mobilewallet.ui.components.OfflineBanner
import com.chris.mobilewallet.ui.components.QuickActionGrid
import com.chris.mobilewallet.ui.components.RecentTransactionsList

@Composable
fun HomeScreen(
    isOffline: Boolean,
    balance: Money,
    transfers: List<Transfer>,
    onNavigateSend: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateSecurity: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            OfflineBanner(isOffline = isOffline)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Jambo, Chris! 👋",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Nairobi, Kenya • +254712345678",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            .clickable { onNavigateSecurity() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⚙️", fontSize = 22.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Balance Card
                BalanceCard(
                    balance = balance,
                    accountName = "Chris Njihia",
                    phoneNumber = "+254712345678",
                    onDepositClick = onNavigateSend
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Quick Action Services Grid
                QuickActionGrid(
                    onActionClick = { action ->
                        if (action.id == "send") {
                            onNavigateSend()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Recent Transactions List
                RecentTransactionsList(
                    transfers = transfers,
                    onSeeAllClick = onNavigateHistory
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
