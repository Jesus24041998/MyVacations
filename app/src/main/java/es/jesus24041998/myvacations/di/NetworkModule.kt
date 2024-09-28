package es.jesus24041998.myvacations.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.jesus24041998.myvacations.utils.NetworkUtilities
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideNetworkConnection(@ApplicationContext context: Context): Boolean {
        return NetworkUtilities.isInternetAvailable(context)
    }
}