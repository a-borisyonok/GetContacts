package by.seka.clevertec.hometask3.di

import android.content.Context
import androidx.room.Room
import by.seka.clevertec.hometask3.data.local.ContactsDB
import by.seka.clevertec.hometask3.data.local.ContactsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): ContactsDB {

        return Room.databaseBuilder(
            appContext,
            ContactsDB::class.java,
            "contacts.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideLocationsDAO(database: ContactsDB): ContactsDao =
        database.contactsDao()

}