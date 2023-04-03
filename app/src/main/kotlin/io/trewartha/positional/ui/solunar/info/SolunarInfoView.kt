package io.trewartha.positional.ui.solunar.info

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.composable
import io.trewartha.positional.R
import io.trewartha.positional.ui.Markdown
import io.trewartha.positional.ui.NavDestination.SolunarInfo
import io.trewartha.positional.ui.PositionalTheme

fun NavController.navigateToSolunarInfo() {
    navigate(SolunarInfo.route)
}

fun NavGraphBuilder.solunarInfoView(
    onNavigateUp: () -> Unit
) {
    composable(SolunarInfo.route) {
        SolunarInfoView(onNavigateUp = onNavigateUp)
    }
}

@Composable
private fun SolunarInfoView(
    onNavigateUp: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Rounded.Close, stringResource(R.string.common_close))
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { contentPadding ->
        val infoMarkdownContent = LocalContext.current.resources
            .openRawResource(R.raw.solunar_info).reader().readText()
        Markdown(
            content = infoMarkdownContent,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.standard_padding))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    PositionalTheme {
        SolunarInfoView(onNavigateUp = {})
    }
}
