package io.trewartha.positional.sun

import android.content.Context
import androidx.lifecycle.MediatorLiveData
import ca.rmen.sunrisesunset.SunriseSunset.*
import io.trewartha.positional.location.LocationLiveData
import org.threeten.bp.Instant
import java.util.*

class SunLiveData(context: Context) : MediatorLiveData<SunViewData>() {

    private val locationLiveData = LocationLiveData(context)

    override fun onActive() {
        super.onActive()
        addSource(locationLiveData) {
            val calendar = Calendar.getInstance()
            val latitude = it.latitude
            val longitude = it.longitude
            val sunriseSunset = getSunriseSunset(calendar, latitude, longitude)
            val civilTwilights = getCivilTwilight(calendar, latitude, longitude)
            val nauticalTwilights = getNauticalTwilight(calendar, latitude, longitude)
            val astronomicalTwilights = getAstronomicalTwilight(calendar, latitude, longitude)

            value = SunViewData(
                    it,
                    instantFrom(astronomicalTwilights[0]),
                    instantFrom(nauticalTwilights[0]),
                    instantFrom(civilTwilights[0]),
                    instantFrom(sunriseSunset[0]),
                    instantFrom(sunriseSunset[1]),
                    instantFrom(civilTwilights[1]),
                    instantFrom(nauticalTwilights[1]),
                    instantFrom(astronomicalTwilights[1])
            )
        }
    }

    override fun onInactive() {
        super.onInactive()
        removeSource(locationLiveData)
    }

    private fun instantFrom(calendar: Calendar?): Instant? = calendar
            ?.let { Instant.ofEpochMilli(it.timeInMillis) }
}