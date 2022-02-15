package by.seka.clevertec.hometask3.data

import androidx.room.Dao
import androidx.room.Query
import by.seka.clevertec.hometask3.domain.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Query("SELECT * FROM contacts")
    fun getAll(): Flow<List<Contact>>
}