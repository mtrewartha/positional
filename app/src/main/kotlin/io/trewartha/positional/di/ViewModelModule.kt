package io.trewartha.positional.di

import android.content.ClipboardManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.trewartha.positional.ui.utils.format.AndroidDateTimeFormatter
import io.trewartha.positional.ui.utils.format.AndroidLocationFormatter
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import io.trewartha.positional.ui.utils.format.LocationFormatter

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {

    @Provides
    fun clipboardManager(@ApplicationContext context: Context): ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    @Provides
    fun dateTimeFormatter(androidDateTimeFormatter: AndroidDateTimeFormatter): DateTimeFormatter =
        androidDateTimeFormatter

    @Provides
    fun locationFormatter(androidLocationFormatter: AndroidLocationFormatter): LocationFormatter =
        androidLocationFormatter
}