package inc.pneuma.xule.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import inc.pneuma.xule.data.appPreferenceDatastore


@Module
@InstallIn(SingletonComponent::class)
object DatastoreModule {

    @Provides
    @Singleton
    fun provideSettingsPreference(
        @ApplicationContext context: Context
    ): DataStore<Preferences> = context.appPreferenceDatastore

}