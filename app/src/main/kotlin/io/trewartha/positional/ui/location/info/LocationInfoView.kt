package io.trewartha.positional.ui.location.info

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.trewartha.positional.R
import io.trewartha.positional.ui.Markdown
import io.trewartha.positional.ui.NavDestination.LocationInfo
import io.trewartha.positional.ui.PositionalTheme

fun NavController.navigateToLocationInfo() {
    navigate(LocationInfo.route)
}

fun NavGraphBuilder.locationInfoView(
    onNavigateUp: () -> Unit
) {
    composable(LocationInfo.route) {
        LocationInfoView(onNavigateUp = onNavigateUp)
    }
}

@Composable
private fun LocationInfoView(
    onNavigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Rounded.Close, stringResource(R.string.common_close))
                    }
                }
            )
        }
    ) { contentPadding ->
        val infoMarkdownContent = LocalContext.current.resources
            .openRawResource(R.raw.location_info).reader().readText()
        Markdown(
            content = infoMarkdownContent,
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    PositionalTheme {
        LocationInfoView(onNavigateUp = {})
    }
}
