package io.trewartha.positional.ui.compass

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import io.trewartha.positional.R
import io.trewartha.positional.ui.Markdown

@Composable
fun CompassInfoSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.statusBarsPadding(),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val infoMarkdownContent = LocalContext.current.resources
            .openRawResource(R.raw.compass_info).reader().readText()
        Markdown(
            content = infoMarkdownContent,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.standard_padding))
                .navigationBarsPadding()
                .padding(bottom = 96.dp)
        )
    }
}
