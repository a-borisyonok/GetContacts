package by.seka.clevertec.hometask3.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import by.seka.clevertec.hometask3.data.local.ContactsDao
import by.seka.clevertec.hometask3.domain.model.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)

abstract class ContactsDB : RoomDatabase() {

    abstract fun contactsDao(): ContactsDao
}