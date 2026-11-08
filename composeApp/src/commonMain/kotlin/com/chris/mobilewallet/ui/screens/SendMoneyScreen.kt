// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/screens/SendMoneyScreen.kt
package com.chris.mobilewallet.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chris.mobilewallet.ui.components.PhoneInput

@Composable
fun SendMoneyScreen(
    errorMessage: String? = null,
    onSendSubmitted: (recipient: String, amountMajor: Long) -> Unit,
    onBack: () -> Unit
) {
    var recipient by remember { mutableStateOf("0712345678") }
    var amountText by remember { mutableStateOf("500") }
    var localError by remember { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Send Money via M-Pesa", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))

            PhoneInput(
                value = recipient,
                onValueChange = { recipient = it },
                error = localError ?: errorMessage
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { char -> char.isDigit() } },
                label = { Text("Amount (KES)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            val displayErr = localError ?: errorMessage
            if (displayErr != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = displayErr,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val amountMajor = amountText.toLongOrNull() ?: 0L
                    if (amountMajor <= 0) {
                        localError = "Enter a valid amount"
                        return@Button
                    }
                    localError = null
                    onSendSubmitted(recipient, amountMajor)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirm Transfer")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Home")
            }
        }
    }
}
