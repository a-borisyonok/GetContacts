package by.seka.clevertec.hometask3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import by.seka.clevertec.hometask3.domain.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Query("SELECT * FROM contacts")
    fun getAll(): Flow<List<Contact>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(contact: Contact)

    @Query("SELECT number FROM contacts")
    suspend fun getNumbers(): List<String>

    @Query("SELECT * FROM contacts WHERE number=:number")
    suspend fun getContact(number: String): Contact
}