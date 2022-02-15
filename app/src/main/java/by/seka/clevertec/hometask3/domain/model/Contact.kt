package by.seka.clevertec.hometask3.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import by.seka.clevertec.hometask3.util.EMPTY_STRING

@Entity(tableName = "contacts")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val number: String,
    val firstName: String = EMPTY_STRING,
    val lastName: String = EMPTY_STRING,
    val email: String = EMPTY_STRING
)
