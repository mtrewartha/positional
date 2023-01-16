package io.trewartha.positional.di.viewmodel

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.trewartha.positional.ui.utils.format.AndroidLocationFormatter
import io.trewartha.positional.ui.utils.format.LocationFormatter

@Module
@InstallIn(ViewModelComponent::class)
class FormatModule {

    @Provides
    fun locationFormatter(
        androidLocationFormatter: AndroidLocationFormatter
    ): LocationFormatter = androidLocationFormatter
}
