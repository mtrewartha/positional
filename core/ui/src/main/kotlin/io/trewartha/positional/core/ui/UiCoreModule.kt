package io.trewartha.positional.core.ui

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import io.trewartha.positional.core.ui.format.DateTimeFormatter
import io.trewartha.positional.core.ui.format.SystemDateTimeFormatter

@Module
@InstallIn(ActivityRetainedComponent::class)
public interface UiCoreModule {

    @Binds
    public fun dateTimeFormatter(implementation: SystemDateTimeFormatter): DateTimeFormatter
}
