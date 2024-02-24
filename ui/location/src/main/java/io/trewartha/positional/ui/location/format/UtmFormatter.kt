package io.trewartha.positional.ui.location.format

import android.content.Context
import earth.worldwind.geom.Angle.Companion.degrees
import earth.worldwind.geom.coords.UTMCoord
import io.trewartha.positional.model.core.measurement.Coordinates
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.ui.location.R
import java.util.Locale

class UtmFormatter(
    private val context: Context,
    private val locale: Locale
) : CoordinatesFormatter {

    override val format = CoordinatesFormat.UTM

    override fun formatForDisplay(coordinates: Coordinates?): List<String?> =
        coordinates?.let {
            coordinates.toUtmCoordinates().let { utmCoordinates ->
                listOf(
                    formatZone(utmCoordinates.zone, it.latitude),
                    formatEasting(utmCoordinates.easting, locale),
                    formatNorthing(utmCoordinates.northing, locale)
                )
            }
        } ?: List(UTM_COORDINATES_SIZE) { null }

    override fun formatForCopy(coordinates: Coordinates): String {
        val formattedLines = formatForDisplay(coordinates).map { it.orEmpty() }
        return context.getString(
            R.string.ui_location_coordinates_copy_format_mgrs,
            formattedLines[0],
            formattedLines[1],
            formattedLines[2],
        )
    }

    private fun formatEasting(easting: Double, locale: Locale): String =
        EASTING_FORMAT.format(locale, easting)

    @Suppress("CyclomaticComplexMethod", "MagicNumber")
    private fun getUtmLatBand(latitudeDegrees: Double): String =
        when {
            -80.0 <= latitudeDegrees && latitudeDegrees < -72.0 -> "C"
            -72.0 <= latitudeDegrees && latitudeDegrees < -64.0 -> "D"
            -72.0 <= latitudeDegrees && latitudeDegrees < -56.0 -> "E"
            -72.0 <= latitudeDegrees && latitudeDegrees < -48.0 -> "F"
            -72.0 <= latitudeDegrees && latitudeDegrees < -40.0 -> "G"
            -72.0 <= latitudeDegrees && latitudeDegrees < -32.0 -> "H"
            -72.0 <= latitudeDegrees && latitudeDegrees < -24.0 -> "J"
            -72.0 <= latitudeDegrees && latitudeDegrees < -16.0 -> "K"
            -72.0 <= latitudeDegrees && latitudeDegrees < -8.0 -> "L"
            -72.0 <= latitudeDegrees && latitudeDegrees < 0.0 -> "M"
            00.0 <= latitudeDegrees && latitudeDegrees < 08.0 -> "N"
            08.0 <= latitudeDegrees && latitudeDegrees < 16.0 -> "P"
            16.0 <= latitudeDegrees && latitudeDegrees < 24.0 -> "Q"
            24.0 <= latitudeDegrees && latitudeDegrees < 32.0 -> "R"
            32.0 <= latitudeDegrees && latitudeDegrees < 40.0 -> "S"
            40.0 <= latitudeDegrees && latitudeDegrees < 48.0 -> "T"
            48.0 <= latitudeDegrees && latitudeDegrees < 56.0 -> "U"
            56.0 <= latitudeDegrees && latitudeDegrees < 64.0 -> "V"
            64.0 <= latitudeDegrees && latitudeDegrees < 72.0 -> "W"
            72.0 <= latitudeDegrees && latitudeDegrees < 84.0 -> "X"
            else -> ""
        }

    private fun formatNorthing(northing: Double, locale: Locale): String =
        NORTHING_FORMAT.format(locale, northing)

    private fun formatZone(zone: Int, latitudeDegrees: Double): String =
        "$zone${getUtmLatBand(latitudeDegrees)}"

    private fun Coordinates.toUtmCoordinates(): UTMCoord =
        UTMCoord.fromLatLon(latitude.degrees, longitude.degrees)
}

private const val NUMBER_FORMAT = "%7.0f"
private const val EASTING_FORMAT = NUMBER_FORMAT + "m E"
private const val NORTHING_FORMAT = NUMBER_FORMAT + "m N"
private const val UTM_COORDINATES_SIZE = 3
