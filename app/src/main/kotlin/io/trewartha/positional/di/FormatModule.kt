package io.trewartha.positional.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import io.trewartha.positional.ui.utils.format.SystemDateTimeFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
interface FormatModule {

    @Binds
    fun dateTimeFormatter(systemDateTimeFormatter: SystemDateTimeFormatter): DateTimeFormatter
}
