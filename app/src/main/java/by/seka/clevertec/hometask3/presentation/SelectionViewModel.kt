package by.seka.clevertec.hometask3.presentation

import androidx.lifecycle.ViewModel
import by.seka.clevertec.hometask3.data.Repository
import by.seka.clevertec.hometask3.domain.model.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectionViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    suspend fun addContact(contact: Contact) {
        repository.add(contact)
    }

    suspend fun getNumbersList(): List<String> {
        return repository.getNumbersList()
    }

    suspend fun getContact(number: String): Contact {
        return repository.getContact(number)
    }
}
