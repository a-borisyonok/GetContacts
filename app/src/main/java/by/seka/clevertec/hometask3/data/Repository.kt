package by.seka.clevertec.hometask3.data

import by.seka.clevertec.hometask3.data.local.ContactsDao
import by.seka.clevertec.hometask3.domain.model.Contact
import javax.inject.Inject

class Repository @Inject constructor(private val dao: ContactsDao) {

    suspend fun add(contact: Contact) {
        dao.add(contact)
    }

    suspend fun getNumbersList(): List<String> {
        return dao.getNumbers()
    }

    suspend fun getContact(number: String): Contact {
        return dao.getContact(number)
    }
}