package by.seka.clevertec.hometask3.data

import androidx.room.Database
import androidx.room.RoomDatabase
import by.seka.clevertec.hometask3.domain.model.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)

abstract class ContactsDB : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao
}