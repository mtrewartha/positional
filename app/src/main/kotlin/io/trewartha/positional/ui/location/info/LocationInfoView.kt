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
import io.trewartha.positional.R
import io.trewartha.positional.ui.Markdown
import io.trewartha.positional.ui.PositionalTheme

@Composable
fun LocationInfoView(navController: NavController) {
    LocationInfoView(navigateBack = { navController.popBackStack() })
}

@Composable
private fun LocationInfoView(
    navigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
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
        LocationInfoView(navigateBack = {})
    }
}
