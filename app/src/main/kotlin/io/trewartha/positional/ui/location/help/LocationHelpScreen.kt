package io.trewartha.positional.ui.location.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.insets.statusBarsPadding
import io.trewartha.positional.R
import io.trewartha.positional.ui.PositionalTheme

@Composable
fun LocationHelpScreen(
    navController: NavController,
    viewModel: LocationHelpViewModel = hiltViewModel()
) {
    LocationHelpContent(
        content = viewModel.helpContent,
        navigateBack = { navController.popBackStack() }
    )
}

@Composable
private fun LocationHelpContent(
    content: String,
    navigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(Icons.TwoTone.Close, stringResource(R.string.common_close))
                    }
                },
                title = {
                    Text(
                        stringResource(R.string.location_help_toolbar_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                modifier = Modifier.statusBarsPadding() // TODO: Handle window insets in the new way (see Accompanist page for why this isn't the correct way anymore)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(
                    horizontal = dimensionResource(R.dimen.activity_horizontal_margin),
                    vertical = paddingValues.calculateTopPadding() +
                            dimensionResource(R.dimen.activity_vertical_margin)
                )
        ) {
            Text(content)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationHelpContentPreview() {
    PositionalTheme {
        LocationHelpContent(content = "Help Content", navigateBack = {})
    }
}