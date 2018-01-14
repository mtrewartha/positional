package io.trewartha.positional.storage

import android.arch.persistence.room.TypeConverter
import android.net.Uri
import org.threeten.bp.Instant
import java.util.*


class Converters {

    @TypeConverter
    fun toInstant(value: String?): Instant? {
        return if (value == null) null else Instant.parse(value)
    }

    @TypeConverter
    fun fromInstant(instant: Instant?): String? {
        return instant?.toString()
    }

    @TypeConverter
    fun toUUID(value: String?): UUID? {
        return if (value == null) null else UUID.fromString(value)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUri(value: String?): Uri? {
        return if (value == null) null else Uri.parse(value)
    }

    @TypeConverter
    fun fromUri(uri: Uri?): String? {
        return uri.toString()
    }
}