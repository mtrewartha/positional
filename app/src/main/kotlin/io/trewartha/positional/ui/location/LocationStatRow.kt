package io.trewartha.positional.ui.location

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Adjust
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


data class LocationStatRowState(
    val icon: ImageVector,
    val name: String,
    val value: String?,
    val accuracy: String?,
    val accuracyVisible: Boolean
)

@Composable
fun LocationStatRow(
    locationStatRowState: LocationStatRowState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = locationStatRowState.icon,
            contentDescription = null,
            modifier = Modifier
                .padding(end = HORIZONTAL_PADDING)
                .size(ICON_SIZE)
        )
        Text(
            text = locationStatRowState.name,
            fontSize = 18.sp,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = HORIZONTAL_PADDING)
                .weight(1f, true),
        )
        Text(
            text = locationStatRowState.value ?: "",
            fontSize = 18.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(end = HORIZONTAL_PADDING),
        )
        if (locationStatRowState.accuracyVisible) {
            Text(
                text = locationStatRowState.accuracy ?: "",
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LocationStatRowPreview() {
    LocationStatRow(
        locationStatRowState = LocationStatRowState(
            Icons.TwoTone.Adjust,
            "Accuracy",
            "100.0",
            "± 10.0",
            true
        )
    )
}

private val HORIZONTAL_PADDING = 16.dp
private val ICON_SIZE = 24.dp