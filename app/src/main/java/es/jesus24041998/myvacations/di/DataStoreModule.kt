package es.jesus24041998.myvacations.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.MultiProcessDataStoreFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jesus24041998.myvacations.ui.datastore.SettingsApp
import es.jesus24041998.myvacations.ui.datastore.SettingsSerializer
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<SettingsApp> =
        MultiProcessDataStoreFactory.create(
            serializer = SettingsSerializer(),
            produceFile = {
                File("${context.filesDir.path}/myvacations.preferences_pb")
            }
        )
}