package by.seka.clevertec.hometask3.di

import android.content.Context
import by.seka.clevertec.hometask3.data.preferences.ContactPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

class SharedPreferencesModule {
    @Provides
    @Singleton
    fun provideContactPreferences(
        @ApplicationContext appContext: Context
    ): ContactPreferences = ContactPreferences(appContext)
}