package io.trewartha.positional.ui.location

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme
import io.trewartha.positional.ui.utils.activity

@Composable
fun LocationPermissionRequiredContent(
    locationPermissionsState: MultiplePermissionsState,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 384.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(96.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(36.dp))
            Text(
                text = stringResource(R.string.location_permission_required_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.location_permission_required_body),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(12.dp))
            val activity = LocalContext.current.activity
            TextButton(onClick = { activity?.showPrivacyPolicy() }) {
                Text(
                    text = stringResource(R.string.location_permission_required_button_privacy_policy),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    if (locationPermissionsState.shouldShowRationale) {
                        onSettingsClick()
                    } else {
                        locationPermissionsState.launchMultiplePermissionRequest()
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.location_permission_required_button_ok))
            }
        }
    }
}

private fun Activity.showPrivacyPolicy() {
    startActivity(
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://github.com/mtrewartha/positional/blob/master/PRIVACY.md"),
        )
    )
}

@PreviewLightDark
@Composable
private fun Preview() {
    PositionalTheme {
        Surface {
            LocationPermissionRequiredContent(
                locationPermissionsState = object : MultiplePermissionsState {
                    override val allPermissionsGranted: Boolean = false
                    override val permissions: List<PermissionState> = emptyList()
                    override val revokedPermissions: List<PermissionState> = emptyList()
                    override val shouldShowRationale: Boolean = false

                    override fun launchMultiplePermissionRequest() {}
                },
                onSettingsClick = {},
            )
        }
    }
}
