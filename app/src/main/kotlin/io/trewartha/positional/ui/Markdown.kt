package io.trewartha.positional.ui

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon

private const val BASE_TEXT_SIZE_SP = 16f

@Composable
fun Markdown(
    content: String,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier
            .verticalScroll(state = rememberScrollState())
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
    ) {
        val textColor = LocalContentColor.current.toArgb()
        AndroidView(
            factory = {
                TextView(it).apply {
                    tag = Markwon.create(it)
                    textSize = BASE_TEXT_SIZE_SP
                    setTextColor(ColorStateList.valueOf(textColor))
                }
            },
            update = { (it.tag as Markwon).setMarkdown(it, content) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@ThemePreviews
@Composable
private fun Preview() {
    PositionalTheme {
        Markdown(content = "**Title** Body")
    }
}
