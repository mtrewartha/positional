package io.trewartha.positional.ui.core

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import io.trewartha.positional.ui.core.format.DateTimeFormatter
import io.trewartha.positional.ui.core.format.SystemDateTimeFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
interface UiCoreModule {

    @Binds
    fun dateTimeFormatter(systemDateTimeFormatter: SystemDateTimeFormatter): DateTimeFormatter
}
