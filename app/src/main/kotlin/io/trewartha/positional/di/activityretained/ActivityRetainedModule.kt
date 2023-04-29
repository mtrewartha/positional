package io.trewartha.positional.di.activityretained

import android.content.Context
import android.view.WindowManager
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.trewartha.positional.data.settings.SettingsProto
import io.trewartha.positional.settings.SettingsSerializer
import io.trewartha.positional.ui.utils.format.DateTimeFormatter
import io.trewartha.positional.ui.utils.format.SystemDateTimeFormatter
import java.util.Locale

@Module
@InstallIn(ActivityRetainedComponent::class)
class ActivityRetainedModule {

    @Provides
    fun dateTimeFormatter(
        systemDateTimeFormatter: SystemDateTimeFormatter
    ): DateTimeFormatter = systemDateTimeFormatter

    @Provides
    fun locale(): Locale = checkNotNull(LocaleListCompat.getDefault()[0])

    @Provides
    @ActivityRetainedScoped
    fun settingsDataStore(
        @ApplicationContext context: Context
    ): DataStore<SettingsProto.Settings> = context.settingsDataStore

    @Provides
    fun windowManager(
        @ApplicationContext context: Context
    ): WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val Context.settingsDataStore: DataStore<SettingsProto.Settings> by dataStore(
        fileName = "settings.proto",
        serializer = SettingsSerializer
    )
}
