package io.trewartha.positional.ui.twilight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import io.trewartha.positional.R

@Composable
fun SunriseSunsetLabelsRow(
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        SunriseSunsetLabel(
            text = stringResource(R.string.twilight_label_sunrise),
            modifier = Modifier.weight(1f, fill = true)
        )
        Spacer(modifier = Modifier.weight(1f, fill = true))
        SunriseSunsetLabel(
            text = stringResource(R.string.twilight_label_sunset),
            modifier = Modifier.weight(1f, fill = true),
        )
    }
}

@Composable
private fun SunriseSunsetLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center
    )
}
