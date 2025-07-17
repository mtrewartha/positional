package io.trewartha.positional.core.ui

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.format.SystemDateTimeFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
interface UiCoreModule {

    @Binds
    fun dateTimeFormatter(systemDateTimeFormatter: SystemDateTimeFormatter): DateTimeFormatter
}
