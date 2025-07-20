package io.trewartha.positional.core.ui.components

import android.content.res.ColorStateList
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.trewartha.positional.core.ui.PositionalTheme

private const val BASE_TEXT_SIZE_SP = 16f

@Composable
fun Markdown(
    content: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
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

@PreviewLightDark
@Composable
private fun Preview() {
    PositionalTheme {
        Markdown(content = "**Title** Body")
    }
}
