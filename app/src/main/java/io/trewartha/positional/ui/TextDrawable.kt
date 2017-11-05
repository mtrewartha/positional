package io.trewartha.positional.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.TypedValue

class TextDrawable(private val context: Context, val text: String, private val textColor: Int, private val backgroundColor: Int) : Drawable() {

    override fun draw(canvas: Canvas) {
        // Draw the background
        val backgroundPaint = Paint()
        backgroundPaint.color = backgroundColor
        canvas.drawPaint(backgroundPaint)

        // Draw the text
        val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = textColor
        val canvasWidth = canvas.width
        textPaint.textSize = convertDipToPixels(TEXT_SIZE_DP)
        val loadingStringWidth = textPaint.measureText(text)
        val xPos = (canvasWidth - loadingStringWidth) / 2.0f
        val yPos = canvas.height / 2 - (textPaint.descent() + textPaint.ascent()) / 2
        canvas.drawText(text, xPos, yPos, textPaint)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun setColorFilter(cf: ColorFilter?) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.OPAQUE
    }

    private fun convertDipToPixels(dip: Float): Float {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, metrics)
    }

    companion object {
        private val TEXT_SIZE_DP = 24f
    }
}
