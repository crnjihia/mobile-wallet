// path: composeApp/src/commonMain/kotlin/com/chris/mobilewallet/ui/components/PhoneInput.kt
package com.chris.mobilewallet.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PhoneInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Recipient Phone Number (+254...)",
    error: String? = null,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text("e.g. 0712345678 or +254712345678") },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
