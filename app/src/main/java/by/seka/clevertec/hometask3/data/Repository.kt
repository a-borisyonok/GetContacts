package by.seka.clevertec.hometask3.data

import by.seka.clevertec.hometask3.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Repository @Inject constructor (private val dao: ContactsDao){

   fun getAll(): Flow<List<Contact>> {

        return dao.getAll()
    }
}