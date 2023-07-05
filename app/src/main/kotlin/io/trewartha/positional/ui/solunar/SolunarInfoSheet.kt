package io.trewartha.positional.ui.solunar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import io.trewartha.positional.R
import io.trewartha.positional.ui.Markdown

@Composable
fun SolunarInfoSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier.windowInsetsPadding(WindowInsets.statusBars),
        sheetState = sheetState,
        windowInsets = windowInsets
    ) {
        val infoMarkdownContent = LocalContext.current.resources
            .openRawResource(R.raw.solunar_info).reader().readText()
        Markdown(
            content = infoMarkdownContent,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.standard_padding))
        )
    }
}
