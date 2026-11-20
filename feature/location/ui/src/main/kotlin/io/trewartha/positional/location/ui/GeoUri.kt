package io.trewartha.positional.location.ui

import io.trewartha.positional.core.measurement.Angle
import io.trewartha.positional.core.measurement.GeodeticCoordinates
import java.net.URLEncoder
import java.util.Locale

/**
 * Builds a `geo:` URI that drops a pin at [coordinates] labeled with [label].
 *
 * Coordinates are plain decimal degrees with a period as the decimal separator and no grouping or
 * symbols, regardless of the default locale. The label is percent-encoded.
 */
internal fun geoUri(coordinates: GeodeticCoordinates, label: String): String {
    val point = "${coordinates.latitude.toPlainDegrees()},${coordinates.longitude.toPlainDegrees()}"
    return "geo:$point?q=$point(${label.percentEncoded()})"
}

private fun Angle.toPlainDegrees(): String {
    val formatted = String.format(Locale.ROOT, "%.6f", inDegrees().magnitude)
    return if (formatted == "-0.000000") "0.000000" else formatted
}

private fun String.percentEncoded(): String =
    URLEncoder.encode(this, "UTF-8").replace("+", "%20")
